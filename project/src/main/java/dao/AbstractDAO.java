package dao;

import dbaccess.DBConnection;
import model.Table;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for querying and creating
 *  * objects based on table rows
 * @param <T> The model of generated objects
 */
public class AbstractDAO<T> {
    /**
     * Logger object used for logging possible errors
     */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    /**
     * The class object of the used model
     */
    private final Class<T> type;

    /**
     * The constructor initialize the {@link #type type} with the class object of the used model. That constructor is usable
     * only if this class is inherited by another class
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        LOGGER.setLevel(Level.WARNING);
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Method used for creating a select query
     * @param field The field after which the query will select
     * @return Returns the query as a String and needs to be processed by a PreparedStatement
     */
    protected String createSelectQuery(String field) {
        return "SELECT " +
                " * " +
                " FROM " +
                type.getAnnotation(Table.class).tableName() +
                " WHERE " + field + " =?";
    }

    /**
     * Method used for creating an insert query
     * @return Returns the query as a String and needs to be processed by a PreparedStatement
     */
    protected String createInsertQuery() {
        StringBuilder query = new StringBuilder();
        int nrOfFields = type.getDeclaredFields().length;

        query.append("INSERT INTO ").append(type.getAnnotation(Table.class).tableName());
        query.append("(").append(type.getAnnotation(Table.class).pkField()).append(",");

        for(Field field : type.getDeclaredFields()) {
            if(field.getName().equals("primaryKey")) continue;
            query.append(field.getName()).append(",");
        }
        query.setCharAt(query.length()-1, ')');
        query.append(" values (");

        for(int i = 0; i < nrOfFields; i++) {
            query.append("?,");
        }
        query.setCharAt(query.length() - 1, ')');

        return query.toString();
    }

    /**
     * Method used for creating an update query
     * @param selectField The field used for selecting the data which will be updated
     * @return Returns the query as a String. Needs to be processed by a PreparedStatement
     */
    protected String createUpdateQuery(String selectField) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ").append(type.getAnnotation(Table.class).tableName()).append(" SET ");
        for(Field field : type.getDeclaredFields()) {
            if(field.getName().equals("primaryKey")) continue;
            query.append(field.getName()).append("= ?,");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(selectField).append("= ?");
        return query.toString();
    }

    /**
     * Method used for creating a delete query
     * @param field The used for selecting which rows to be deleted
     * @return Returns the query as a String. Needs to be processed by a PreparedStatement
     */
    protected String createDeleteQuery(String field) {
        return "DELETE FROM " + type.getAnnotation(Table.class).tableName() + " " + "WHERE " + field + " = ?";
    }


    public ArrayList<T> findAll() {
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM " + type.getAnnotation(Table.class).tableName();

        connection = DBConnection.getConnection();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database or create statement / execute query", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
            DBConnection.close(resultSet);
        }
        return null;
    }

    /**
     * Method used for creating objects of type {@link #type}
     * @param resultSet from which the objects will be created
     * @return Returns an ArrayList of objects of type {@link #type}
     * @throws SQLException If there was a problem in database connection
     */
    protected ArrayList<T> createObjects(ResultSet resultSet) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
            while(resultSet.next()) {
                try {
                    T instance = type.newInstance();
                    PropertyDescriptor propertyDescriptor;
                    Method method;
                    Object primaryKey = resultSet.getObject(type.getAnnotation(Table.class).pkField());
                    method = type.getDeclaredMethod("setPK", primaryKey.getClass());
                    method.setAccessible(true);
                    method.invoke(instance, primaryKey);
                    for (Field field : type.getDeclaredFields()) {
                        if (field.getName().equals("primaryKey")) continue;
                        Object value = resultSet.getObject(field.getName());
                        propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                        method = propertyDescriptor.getWriteMethod();
                        method.invoke(instance, value);
                    }
                    result.add(instance);
                } catch (InstantiationException e) { LOGGER.log(Level.WARNING, "Unable to instantiate a new object", e);
                } catch (IllegalAccessException e) { LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
                } catch (NoSuchMethodException e) { LOGGER.log(Level.WARNING, "The method is not found", e);
                } catch (InvocationTargetException e) { LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
                } catch (IntrospectionException e) { LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
                }
            }
        return result;
    }

    /**
     * Method used for searching data from table
     * @param field The field used for selecting
     * @param value The value used for selecting
     * @return Returns an ArrayList of found rows
     */
    public ArrayList<T> findByField(String field, Object value) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery(field);
        connection = DBConnection.getConnection();
        try {
            statement = connection.prepareStatement(query);
            statement.setObject(1, value);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
            DBConnection.close(resultSet);
        }
        return null;
    }

    /**
     * Method used for searching after a row with specified primary key
     * @param primaryKey The primary key to be used for searching
     * @return Returns the object with specified primary key
     */
    public T findByPK(Object primaryKey) {
        return findByField(type.getAnnotation(Table.class).pkField(), primaryKey).get(0);
    }

    /**
     * Method used for deleting rows with specified field
     * @param field The field used for deletion
     * @param value The value of the field
     */
    public void deleteByField(String field, Object value) {
        String query = createDeleteQuery(field);
        Connection connection;
        PreparedStatement statement = null;
        connection = DBConnection.getConnection();
        try {
            statement = connection.prepareStatement(query);
            statement.setObject(1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
        }
    }

    /**
     * Method used for inserting an object to the database
     * @param t The object to be inserted
     */
    public void insert(T t) {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = null;
        String query = createInsertQuery();
        try {
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor("primaryKey", type);
            Method method = propertyDescriptor.getReadMethod();
            Object pk = method.invoke(t);
            statement.setObject(1, pk);
            setQuery(t, statement, 2);
            statement.executeUpdate();
            if (type.getAnnotation(Table.class).autoIncrement()) {
                ResultSet resultSet = statement.getGeneratedKeys();
                method = type.getDeclaredMethod("setPK", Integer.class);
                method.setAccessible(true);
                if(resultSet.next()) method.invoke(t, resultSet.getInt(1));
                DBConnection.close(resultSet);
            }
        } catch (IntrospectionException e) { LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
        } catch (InvocationTargetException e) { LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
        } catch (IllegalAccessException e) { LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
        } catch (NoSuchMethodException e) { LOGGER.log(Level.WARNING, "The method is not found", e);
        } catch (SQLException e) { LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
        }
    }

    /**
     * Method used for updating the object specified
     * @param t The updated object
     */
    public void update(T t) {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = null;
        Method method;
        int count = 1;
        PropertyDescriptor propertyDescriptor;
        String query = createUpdateQuery(type.getAnnotation(Table.class).pkField());
        try {
            statement = connection.prepareStatement(query);
            count = setQuery(t, statement, count);
            propertyDescriptor = new PropertyDescriptor("primaryKey", type);
            method = propertyDescriptor.getReadMethod();
            Object pk = method.invoke(t);
            statement.setObject(count, pk);
            statement.executeUpdate();
        } catch (SQLException e) { LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } catch (IntrospectionException e) { LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
        } catch (IllegalAccessException e) { LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
        } catch (InvocationTargetException e) { LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
        }
    }

    /**
     * Method used for deleting an object
     * @param t The object to be deleted
     */
    public void delete(T t) {
        Connection connection = DBConnection.getConnection();
        PreparedStatement statement = null;
        PropertyDescriptor propertyDescriptor;
        Method method;
        String query = createDeleteQuery(type.getAnnotation(Table.class).pkField());
        try {
            statement = connection.prepareStatement(query);
            propertyDescriptor = new PropertyDescriptor("primaryKey", type);
            method = propertyDescriptor.getReadMethod();
            Object pk = method.invoke(t);
            statement.setObject(1, pk);
            statement.executeUpdate();
        } catch (SQLException e) { LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } catch (IntrospectionException e) { LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
        } catch (IllegalAccessException e) { LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
        } catch (InvocationTargetException e) { LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
        }
    }

    /**
     * Method used for filling the unprocessed queries with corresponding fields
     * @param t The object used for obtaining the value of the needed fields
     * @param statement The statement used for setting the obtained value
     * @param count The count used for counting the processed fields
     * @return Returns the count
     */
    protected int setQuery(T t, PreparedStatement statement, int count) {
        PropertyDescriptor propertyDescriptor;
        Method method;
        for(Field field : type.getDeclaredFields()) {
            if(field.getName().equals("primaryKey")) continue;
            try {
                propertyDescriptor = new PropertyDescriptor(field.getName(), t.getClass());
                method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(t);
                statement.setObject(count, value);
            } catch (SQLException e) { LOGGER.log(Level.WARNING, "Unable to access the database", e);
            } catch (IntrospectionException e) { LOGGER.log(Level.WARNING, "An exception occurs during introspection", e);
            } catch (IllegalAccessException e) { LOGGER.log(Level.WARNING, "The provided class is not accessible", e);
            } catch (InvocationTargetException e) { LOGGER.log(Level.WARNING, "The invoked method throws exceptions", e);
            }
            count++;
        }
        return count;
    }

    /**
     * Method used for obtaining the name of the table columns
     * @return Returns an ArrayList of Strings representing the name of the columns
     */
    public ArrayList<String> getColumns() {
        Connection connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM " + type.getAnnotation(Table.class).tableName();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            return getColumns(resultSet);
        } catch (SQLException e) { LOGGER.log(Level.WARNING, "Unable to access the database", e);
        } finally {
            DBConnection.close(connection);
            DBConnection.close(statement);
            DBConnection.close(resultSet);
        }
        return null;
    }

    /**
     * Method used for obtaining the column names of a table
     * @param resultSet The result set of the table
     * @return Returns an ArrayList of Strings representing the name of the columns
     */
    public ArrayList<String> getColumns(ResultSet resultSet) {
        ArrayList<String> res = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; i++) res.add(metaData.getColumnLabel(i));
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to access the database", e);
        }
        return res;
    }

}
