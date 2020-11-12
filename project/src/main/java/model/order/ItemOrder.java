package model.order;

import model.Table;

@Table(tableName = "itemOrders", pkField = "id", autoIncrement = true)
/**
 * This class represents the ItemOrder model and is equivalent with a row from the itemOrders table
 */
public class ItemOrder {

    /**
     * The primary key of the item order
     */
    private Integer primaryKey;
    /**
     * The id of the Order object
     */
    private Integer orderId;
    /**
     * The id of the Product object
     */
    private Integer productId;
    /**
     * The quantity ordered
     */
    private Integer quantity;

    public void setPrimaryKey(Integer primaryKey) {
        if(!getClass().getAnnotation(Table.class).autoIncrement()) this.primaryKey = primaryKey;
    }

    private void setPK(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getPrimaryKey() { return primaryKey; }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
