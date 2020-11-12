package business.validator;

import dao.ProductDAO;
import model.Product;

/**
 * Validator used for checking if a product already exists in the database table
 */
public class ProductValidator implements Validator<Product> {
    /**
     * @param product The product to be checked
     * @throws IllegalArgumentException If the product already exists in the table
     */
    @Override
    /**
     * The method used for validation
     */
    public void validate(Product product) {
        ProductDAO productDAO = new ProductDAO();
        if(productDAO.findByProductName(product.getProductName()) != null &&
                productDAO.findByProductName(product.getProductName()).getPrice().equals(product.getPrice()))
            throw new IllegalArgumentException("The product with that name already exists in the database");
    }
}
