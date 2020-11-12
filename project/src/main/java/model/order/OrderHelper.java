package model.order;

/**
 * This class represents a printable version of the Order object. It is not connected to any table and provides the actual
 * data stored in orders and itemorders tables.
 */
public class OrderHelper {

    /**
     * The primary key of the item order
     */
    private Integer primaryKey;

    /**
     * The name of the client
     */
    private String clientName;
    /**
     * The name of the product
     */
    private String productName;
    /**
     * The ordered quantity
     */
    private Integer quantity;

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
