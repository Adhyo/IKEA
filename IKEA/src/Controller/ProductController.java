package Controller;

import Model.Product;
import Model.Category;

import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private List<Product> productList;

    public ProductController() {
        this.productList = new ArrayList<>();

        Category furniture = new Category("C01", "Furniture");
        productList.add(new Product(1, "Chair", "Comfortable wooden chair", 100.0, 50, furniture, 5.0, "Brown", "40x40x90", 90.0));
        productList.add(new Product(2, "Table", "Dining table", 300.0, 20, furniture, 20.0, "Black", "200x100x75", 270.0));
    }

    public List<Product> getAllProducts() {
        return productList;
    }

    public void addProduct(Product product) {
        productList.add(product);
    }
}
