package presentation;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Controller class provides methods for decoding the commands
 */
public class Controller {

    /**
     * Logger object used for logging possible errors
     */
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    /**
     * The type of the current command
     */
    private Command commandType;

    /**
     * The current command
     */
    private String command;

    /**
     * The scanner used for reading the input
     */
    private Scanner scanner;

    /**
     * The name of the client obtained from the command
     */
    private String clientName;
    /**
     * The address of the client obtained from the command
     */
    private String clientAddress;
    /**
     * The name of the product obtained from the command
     */
    private String productName;
    /**
     * The name of the table to be reported obtained from the command
     */
    private String reportTableName;
    /**
     * The ordered quantity / product quantity obtained from the command
     */
    private int quantity;
    /**
     * The price of the product obtained from the command
     */
    private double productPrice;

    public String getClientName() {
        return clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getProductName() {
        return productName;
    }

    public String getReportTableName() {
        return reportTableName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    /**
     * The constructor initializes the scanner based on the file provided
     * @param input The input file with commands
     */
    public Controller(File input) {
        LOGGER.setLevel(Level.WARNING);
        try {
            scanner = new Scanner(input);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "The file could not be found", e);
        }
    }

    public Command getCommandType() {
        return commandType;
    }

    /**
     * Method used for getting the next command
     */
    public void readCommand() {
        if(scanner.hasNext()) {
            command = scanner.nextLine();
        } else return;
        if(command.toUpperCase().startsWith("INSERT CLIENT: ")) {
            commandType = Command.ADD_CLIENT;
            getClientInfo();
        }
        else if(command.toUpperCase().startsWith("DELETE CLIENT: ")){
            commandType = Command.DELETE_CLIENT;
            getClientInfo();
        }
        else if(command.toUpperCase().startsWith("INSERT PRODUCT: ")){
            commandType = Command.ADD_PRODUCT;
            getProductInfo();
        }
        else if(command.toUpperCase().startsWith("DELETE PRODUCT: ")){
            commandType = Command.DELETE_PRODUCT;
            getProductInfo();
        }
        else if(command.toUpperCase().startsWith("ORDER: ")){
            commandType = Command.CREATE_ORDER;
            getOrderInfo();
        }
        else if(command.toUpperCase().startsWith("REPORT ")){
            commandType = Command.GENERATE_REPORT;
            getReportInfo();
        }
    }

    private void getClientInfo() {
        String sub = command.substring(15);
        String[] info = sub.split(", ");
        if (info.length >= 1) clientName = info[0];
        if(info.length >= 2) clientAddress = info[1];
    }

    private void getProductInfo() {
        String sub = command.substring(16);
        String[] info = sub.split(", ");
        if(info.length >= 1) productName = info[0];
        if(info.length >= 2) quantity = Integer.parseInt(info[1]);
        if(info.length >= 3) productPrice = Double.parseDouble(info[2]);
    }

    private void getOrderInfo() {
        String sub = command.substring(7);
        String[] info = sub.split(", ");
        if(info.length >= 1) clientName = info[0];
        if(info.length >= 2) productName = info[1];
        if(info.length >= 3) quantity = Integer.parseInt(info[2]);
    }

    private void getReportInfo() {
        reportTableName = command.substring(7);
    }

    public boolean hasNext() {
        return scanner.hasNext();
    }

    public void closeScanner() {
        scanner.close();
    }

}
