package model;


@Table(tableName = "products", pkField = "id", autoIncrement = true)
/**
 * This class represents the Product model and is equivalent with a row from the products table
 */
public class Product {

    /**
     * The primary key of the product
     */
    private Integer primaryKey;
    /**
     * The name of the product
     */
    private String productName;
    /**
     * The quantity of the product
     */
    private Integer quantity;
    /**
     * The price of the product
     */
    private Double price;

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    private void setPK(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrimaryKey(Integer primaryKey) {
        if(!getClass().getAnnotation(Table.class).autoIncrement())this.primaryKey = primaryKey;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
