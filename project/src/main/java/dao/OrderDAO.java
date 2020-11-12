package dao;

import dbaccess.DBConnection;
import model.order.Order;
import model.order.OrderHelper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * The class represents an implementation of AbstractDAO class. It provides access to orders table from the database and
 * helper methods used for getting rows / columns of type OrderHelper, for displaying purposes.
 */
public class OrderDAO extends AbstractDAO<Order> {

    /**
     * Method used for getting rows of type OrderHelper
     * @return Returns an ArrayList of OrderHelper objects
     */
    public ArrayList<OrderHelper> getOrderHelperRows() {
        ArrayList<OrderHelper> orders = new ArrayList<>();
        String query = "SELECT itemorders.id as id, `name`, productName, itemorders.quantity FROM clients JOIN orders ON clients.id = orders.clientId" +
                " JOIN itemorders ON itemorders.orderId = orders.id JOIN products ON itemorders.productId = products.id";
        Connection connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                OrderHelper orderHelper = new OrderHelper();
                orderHelper.setPrimaryKey(resultSet.getInt("id"));
                orderHelper.setClientName(resultSet.getString("name"));
                orderHelper.setProductName(resultSet.getString("productName"));
                orderHelper.setQuantity(resultSet.getInt("quantity"));
                orders.add(orderHelper);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        }
        DBConnection.close(connection);
        DBConnection.close(statement);
        DBConnection.close(resultSet);
        return orders;
    }

    /**
     * Method used for getting the column names used for order table
     * @return Returns an ArrayList of Strings
     */
    public ArrayList<String> getOrderHelperColumns() {
        String query = "SELECT itemorders.id as id, `name`, productName, itemorders.quantity FROM clients JOIN orders ON clients.id = orders.clientId" +
                " JOIN itemorders ON itemorders.orderId = orders.id JOIN products ON itemorders.productId = products.id";
        Connection connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            return getColumns(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
            DBConnection.close(resultSet);
        }
        return null;
    }
}
