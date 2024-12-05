package View;

import Model.Product;

import java.util.List;

public class ProductView {
    public void listAllProducts(List<Product> products) {
        System.out.println("Available Products:");
        for (Product product : products) {
            System.out.println("ID: " + product.getProductId() + " | Name: " + product.getName() + " | Price: $" + product.getPrice());
        }
    }
}
