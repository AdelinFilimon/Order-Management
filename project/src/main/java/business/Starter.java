package business;

import business.validator.ClientValidator;
import business.validator.ProductValidator;
import dao.ClientDAO;
import dao.ItemOrderDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import model.Client;
import model.Product;
import model.order.ItemOrder;
import model.order.Order;
import presentation.Controller;
import presentation.ReportGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The executor of the application. This class provides methods for executing different operations.
 */
public class Starter {

    /**
     * The logger used for generating messages when an exception occurs
     */
    private static final Logger LOGGER = Logger.getLogger(Starter.class.getName());


    /**
     * Controller used for input operations
     */
    private Controller controller;

    /**
     * The data access object for Client
     */
    private ClientDAO clientDAO;

    /**
     * The data access object for Product
     */
    private ProductDAO productDAO;

    /**
     * The data access object for Order
     */
    private OrderDAO orderDAO;

    /**
     * The data access object for ItemOrder
     */
    private ItemOrderDAO itemOrderDAO;

    /**
     * The client validator used
     */
    private ClientValidator clientValidator;

    /**
     * The product validator used
     */
    private ProductValidator productValidator;

    /**
     * The report generator used for output operations
     */
    private ReportGenerator reportGenerator;

    /**
     * Counter used for generating the name of the pdf file of clients table
     */
    private static int clientReportCount;

    /**
     * Counter used for generating the name of the pdf file of products table
     */
    private static int productReportCount;

    /**
     * Counter used for generating the name of the pdf file of orders table
     */
    private static int orderReportCount;

    /**
     * Counter used for generating the name of  the pdf file of bill
     */
    private static int billCount;

    /**
     * Counter used for generating the name of the pdf file of under-stock message
     */
    private static int underStockCount;

    /**
     * The constructor initialize the fields
     * @param input is the input file with commands
     */
    public Starter(File input) {
        controller = new Controller(input);
        clientDAO = new ClientDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        itemOrderDAO = new ItemOrderDAO();
        clientValidator = new ClientValidator();
        productValidator = new ProductValidator();
        reportGenerator = new ReportGenerator();

        clientReportCount = -1;
        productReportCount = -1;
        orderReportCount = -1;
        billCount = -1;
        underStockCount = -1;
    }

    /**
     * Method used for executing the add client operation. If the validator throws an exception that means that the client
     * with specified name already exists in the table and the method will exit without inserting the client.
     */
    private void executeAddClient() {
        Client client = new Client();
        client.setName(controller.getClientName());
        client.setAddress(controller.getClientAddress());
        try {
            clientValidator.validate(client);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Client already exists");
            return;
        }
        clientDAO.insert(client);
    }

    /**
     * Method used for deleting a client from the clients table
     */
    private void executeDeleteClient() {
        clientDAO.deleteByName(controller.getClientName());
    }

    /**
     * Method used for executing the add product operation. See  {@link #executeAddClient()}
     */
    private void executeAddProduct() {
        Product product = new Product();
        product.setProductName(controller.getProductName());
        product.setPrice(controller.getProductPrice());
        product.setQuantity(controller.getQuantity());
        try {
            productValidator.validate(product);
        } catch (Exception e) {
            int quantity = product.getQuantity();
            product = productDAO.findByProductName(product.getProductName());
            product.setQuantity(product.getQuantity() + quantity);
            productDAO.update(product);
            return;
        }
        productDAO.insert(product);
    }

    /**
     * Method used for deleting a product from the products table
     */
    private void executeDeleteProduct() {
        productDAO.deleteByProductName(controller.getProductName());
    }

    /**
     * Method used for executing the generate report command
     */
    private void executeGenerateReport() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<?> rows = new ArrayList<>();
        String reportName = "";
        String table = controller.getReportTableName();
        switch (table) {
            case "client" :
                columns = clientDAO.getColumns();
                rows = clientDAO.findAll();
                clientReportCount++;
                reportName = table + clientReportCount;
                break;
            case "product" :
                columns = productDAO.getColumns();
                rows = productDAO.findAll();
                productReportCount++;
                reportName = table + productReportCount;
                break;
            case "order" :
                columns = orderDAO.getOrderHelperColumns();
                rows = orderDAO.getOrderHelperRows();
                orderReportCount++;
                reportName = table + orderReportCount;
        }
        reportName += ".pdf";
        reportGenerator.generateReport(reportName, rows, columns);
    }

    /**
     * Method used for getting the order a specified client, if it doesnt exist create a new one
     * @return Returns the order
     */
    private Order getOrder() {
        if(orderDAO.findByField("clientId", clientDAO.findByName(controller.getClientName()).getPrimaryKey()) == null ||
                orderDAO.findByField("clientId", clientDAO.findByName(controller.getClientName()).getPrimaryKey()).size() == 0) {
            Order order = new Order();
            Client client = clientDAO.findByName(controller.getClientName());
            order.setClientId(client.getPrimaryKey());
            orderDAO.insert(order);
            return order;
        }
        else {
            return orderDAO.findByField("clientId", clientDAO.findByName(controller.getClientName()).getPrimaryKey()).get(0);
        }
    }


    /**
     * Method used for creating a order and inserting that order in the database. This method also generates a bill
     * if successful otherwise it will generate an under-stock message.
     */
    private void executeCreateOrder() {
        if(clientDAO.findByName(controller.getClientName()) == null) return;
        billCount++;
        Order order;
        order = getOrder();
        ItemOrder itemOrder = new ItemOrder();
        Product product = productDAO.findByProductName(controller.getProductName());
        int quantity = controller.getQuantity();
        if(quantity > product.getQuantity()) {
            underStockCount++;
            reportGenerator.generateUnderStock("understock" + underStockCount + ".pdf", product, quantity);
            orderDAO.delete(order);
            return;
        }
        order.setTotal(order.getTotal() + controller.getQuantity() * product.getPrice());
        orderDAO.update(order);
        itemOrder.setOrderId(order.getPrimaryKey());
        itemOrder.setProductId(product.getPrimaryKey());
        itemOrder.setQuantity(quantity);
        itemOrderDAO.insert(itemOrder);
        reportGenerator.generateBill("bill" + billCount + ".pdf", order, itemOrder);
        product.setQuantity(product.getQuantity() - quantity);
        productDAO.update(product);
    }

    /**
     * This method will take all commands from input file using the controller and execute all commands
     */
    public void executeAll() {
        while(controller.hasNext()) {
            controller.readCommand();
            switch (controller.getCommandType()) {
                case ADD_CLIENT:
                    executeAddClient();
                    break;
                case DELETE_CLIENT:
                    executeDeleteClient();
                    break;
                case ADD_PRODUCT:
                    executeAddProduct();
                    break;
                case DELETE_PRODUCT:
                    executeDeleteProduct();
                    break;
                case GENERATE_REPORT:
                    executeGenerateReport();
                    break;
                case CREATE_ORDER:
                    executeCreateOrder();
                default:break;
            }
        }
        controller.closeScanner();
    }

    public static void main(String[] args) {
        if(args.length == 0) return;
        Starter starter = new Starter(new File(args[0]));
        starter.executeAll();
    }
}
