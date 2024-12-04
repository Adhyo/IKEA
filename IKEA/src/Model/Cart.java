package Model;

import java.util.HashMap;


public class Cart {
    private String cartID;
    private String userID;
    private HashMap<Product, Integer> products;
    

    public Cart(String cartID, HashMap<Product, Integer> products, String userID) {
        this.cartID = cartID;
        this.products = products;
        this.userID = userID;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<Product, Integer> products) {
        this.products = products;
    }
    
}
