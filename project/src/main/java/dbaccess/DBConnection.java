package dbaccess;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Connection class provides access to a database. The class contains private fields, used for loading the
 * database, and methods for getting and closing database connections
 */
public class DBConnection {
    /**
     * Logger object used for logging possible errors
     */
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    /**
     * The driver provided by  the RDBMS used
     */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    /**
     * The url which points to a specific database
     */
    private static final String DB_URL = "jdbc:mysql://localhost/shopdb";
    /**
     * The username of the database
     */
    private static final String USER = "root";
    /**
     * The password of the database user
     */
    private static final String PASSWORD = "root";
    /**
     * The single object of this class
     */
    private static final DBConnection dbConnection = new DBConnection();

    /**
     * The constructor set the {@link #LOGGER logger} level to WARNING and tries to instantiate the driver
     */
    private DBConnection() {
        LOGGER.setLevel(Level.WARNING);
        try {
            Class.forName(DRIVER).newInstance();
        } catch (InstantiationException e) {
            LOGGER.log(Level.WARNING, "Unable to instantiate the driver class", e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "The driver class is not accessible", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Unable to load driver class", e);
        }
    }

    /**
     * Private method which tries to create and return a connection to the database
     * @return Return the created connection. If the database is not accessible returns null
     */
    private Connection createConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        }
        return null;
    }

    /**
     * Method used for getting a connection to the database
     * @return Return the created connection. If the database is not accessible returns null
     */
    public static Connection getConnection() {
        return dbConnection.createConnection();
    }

    /**
     * Method used for closing a connection
     * @param connection The connection to be closed
     */
    public static void close(Connection connection) {
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database and close the connection", e);
        }
    }

    /**
     * Method used for closing a statement
     * @param statement The statement to be closed
     */
    public static void close(Statement statement) {
        if(statement == null) return;
        try {
            statement.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database and close the statement", e);
        }
    }

    /**
     * Method used for closing a result set
     * @param resultSet The result set to be closed
     */
    public static void close(ResultSet resultSet) {
        if(resultSet == null) return;
        try {
            resultSet.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database and close the result set", e);
        }
    }

}
