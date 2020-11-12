package model;

@Table(tableName = "clients", pkField = "id", autoIncrement = true)
/**
 * This class represents the Client model and is equivalent with a row from the clients table
 */
public class Client {

    /**
     * The name of the client
     */
    private String name;
    /**
     * The address of the client
     */
    private String address;
    /**
     * The primary key of the client
     */
    private Integer primaryKey;

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Integer primaryKey) {
        if(!getClass().getAnnotation(Table.class).autoIncrement()) this.primaryKey = primaryKey;
    }

    private void setPK(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) { this.name = name;}

    public void setAddress(String address) { this.address = address;}

}
