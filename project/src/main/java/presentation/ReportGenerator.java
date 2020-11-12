package presentation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.ClientDAO;
import dao.ProductDAO;
import model.Client;
import model.order.ItemOrder;
import model.order.Order;
import model.Product;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for generating reports in PDF format
 */
public class ReportGenerator {

    /**
     * Logger object used for logging possible errors
     */
    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class.getName());
    /**
     * Color of the headers
     */
    private static final BaseColor HEADER_COLOR = new BaseColor(114,181,183);
    /**
     * Border color of the cells
     */
    private static final BaseColor BORDER_COLOR = new BaseColor(39,65,67);

    /**
     * Method used for adding headers to a table
     * @param table The table used
     * @param columns The columns representing the headers in ArrayList format
     */
    private void addTableHeader(PdfPTable table, ArrayList<String> columns) {
        for(String column : columns) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(HEADER_COLOR);
            header.setBorderColor(BORDER_COLOR);
            header.setBorderWidth(1.2f);
            header.setPhrase(new Phrase(column));
            header.getPhrase().getFont().setColor(BaseColor.WHITE);
            header.getPhrase().getFont().setSize(15);
            header.setPadding(10);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        }
    }

    /**
     * Method used for getting a designed cell
     * @param text The text of the cell
     * @return Returns the generated cell
     */
    private PdfPCell getCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(text));
        cell.getPhrase().getFont().setSize(15);
        return cell;
    }

    /**
     * Method used for generating bills
     * @param billName The name of the document to be generated
     * @param order The order used for generating the bill
     * @param itemOrder The item order used for generating the bill
     */
    public void generateBill(String billName, Order order, ItemOrder itemOrder) {
        Document document = openAndGetDocument(billName);
        int clientPK = order.getClientId();
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findByPK(clientPK);
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.findByPK(itemOrder.getProductId());
        try {
            document.add(new Paragraph("Client name: " + client.getName()));
            document.add(new Paragraph("Product: " + product.getProductName()));
            document.add(new Paragraph("Product price: " + product.getPrice()));
            document.add(new Paragraph("Quantity: " + itemOrder.getQuantity()));
            document.add(new Paragraph("Total: " + (itemOrder.getQuantity() * product.getPrice())));
        } catch (DocumentException e) {
            LOGGER.log(Level.WARNING, "An error has occurred in a document", e);
        }
        document.close();
    }

    /**
     * Method used for generating under stock messages
     * @param underStockName The name of the document to be generated
     * @param product The product used for generating the message
     * @param orderedQuantity The ordered quantity used for generating the message
     */
    public void generateUnderStock(String underStockName, Product product, Integer orderedQuantity) {
        Document document = openAndGetDocument(underStockName);
        try {
            Paragraph p = new Paragraph("The operation could not be performed (under-stock): ");
            p.getFont().setColor(BaseColor.RED);
            document.add(p);
            document.add(new Paragraph("Product: " + product.getProductName()));
            document.add(new Paragraph("Currently in stock: " + product.getQuantity()));
            document.add(new Paragraph("Ordered quantity: " + orderedQuantity));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception occur", e);
        }
        document.close();
    }

    /**
     * Method used for opening a new document
     * @param fileName The filename to be used
     * @return Returns the opened document
     */
    private Document openAndGetDocument(String fileName) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "The file could not be found", e);
        } catch (DocumentException e) {
            LOGGER.log(Level.WARNING, "An error has occurred in a document", e);
        }
        document.open();
        return document;
    }


    /**
     * Method used for inserting the rows in a table
     * @param table The table to be used for
     * @param rows The rows to be inserted, in ArrayList format
     */
    private void insertRows(PdfPTable table, ArrayList<?> rows) {
        for (Object row : rows) {
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor("primaryKey", row.getClass(), "getPrimaryKey", null);
                Method method = descriptor.getReadMethod();
                Object pk = method.invoke(row);
                table.addCell(getCell(pk.toString()));
                for (Field field : row.getClass().getDeclaredFields()) {
                    if (field.getName().equals("primaryKey")) continue;
                    descriptor = new PropertyDescriptor(field.getName(), row.getClass());
                    method = descriptor.getReadMethod();
                    Object object = method.invoke(row);
                    table.addCell(getCell(object.toString()));
                }
            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
            } catch (IntrospectionException e) {
                LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
            }
        }
    }

    /**
     * Method used for generating reports
     * @param reportName The report name to be generated
     * @param rows The rows of the generated document
     * @param columns The columns of the generated document
     */
    public void generateReport(String reportName, ArrayList<?> rows, ArrayList<String> columns) {
        Document document = openAndGetDocument(reportName);
        PdfPTable table = new PdfPTable(columns.size());
        addTableHeader(table, columns);
        insertRows(table, rows);
        try {
            document.add(table);
        } catch (DocumentException e) {
            LOGGER.log(Level.WARNING, "An error has occurred in a document", e);
        }
        document.close();
    }

}
