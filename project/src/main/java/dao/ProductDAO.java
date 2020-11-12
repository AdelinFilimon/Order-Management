package dao;
import model.Client;
import model.Product;

/**
 * The class represents an implementation of AbstractDAO class. It provides access to products table from the database.
 */
public class ProductDAO extends AbstractDAO<Product> {

    /**
     * Method used for finding a product with the specified product name
     * @param name The of the product to be searched
     * @return Returns the product with the specified name
     */
    public Product findByProductName(String name) {
        if(findByField("productName", name).size() == 0) return null;
        return findByField("productName", name).get(0);
    }

    /**
     * Method used for deleting a product with the specified product name
     * @param name The name of the product to be deleted
     */
    public void deleteByProductName(String name) {
        deleteByField("productName", name);
    }

    /**
     * Method used for creating and inserting a product to the database
     * @param primaryKey The primary key used for creating the product
     * @param productName The name of the product used for creating the product
     * @param quantity The quantity of the product used for creating the product
     * @param price The price of the product used for creating the product
     * @return Returns the created product
     */
    public Product insertAndGet(Integer primaryKey, String productName, Integer quantity, Double price) {
        Product product = new Product();
        product.setPrimaryKey(primaryKey);
        product.setProductName(productName);
        product.setQuantity(quantity);
        product.setPrice(price);
        insert(product);
        return product;
    }

}
