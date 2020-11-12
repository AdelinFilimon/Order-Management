package model.order;

import model.Table;

@Table(tableName = "orders", pkField = "id", autoIncrement = true)
/**
 * This class represents the Order model and is equivalent with a row from the orders table
 */
public class Order {

    /**
     * The primary key of the order
     */
    private Integer primaryKey;
    /**
     * The id of the Client object
     */
    private Integer clientId;
    /**
     * The total amount of money
     */
    private Double total;

    public Order() {
        total = 0d;
    }

    public void setPrimaryKey(Integer primaryKey) {
        if(!getClass().getAnnotation(Table.class).autoIncrement())this.primaryKey = primaryKey;
    }

    private void setPK(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getPrimaryKey() {
        return primaryKey;
    }

}
