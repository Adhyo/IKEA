package Controller;

import Model.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    
    private Admin admin;
    private List<Product> products;
    private List<Cart> carts;
    private List<Order> orders;
    private List<Transaction> transactions;

    // Constructor
    public AdminController(Admin admin) {
        this.admin = admin;
        this.products = new ArrayList<>();
        this.carts = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // Get Admin Info
    public Admin getAdminInfo() {
        return admin;
    }

    // Manage Products
    public void addProduct(Product product) {
        products.add(product);
        System.out.println("Product added: " + product.getName());
    }

    public void removeProduct(int productId) {
        products.removeIf(product -> product.getProductId() == productId);
        System.out.println("Product removed with ID: " + productId);
    }

    public List<Product> getAllProducts() {
        return products;
    }

    // Manage Orders
    public void processOrder(Order order) {
        order.setStatus(OrderStatus.DELIVER);
        orders.add(order);
        System.out.println("Order processed with ID: " + order.getOrderID());
    }

    public void completeOrder(Order order) {
        order.setStatus(OrderStatus.DONE);
        System.out.println("Order completed with ID: " + order.getOrderID());
    }

    // Generate Transaction
    public void createTransaction(Cart cart, Promo promo) {
        double subTotal = calculateSubtotal(cart);
        double discount = subTotal * (promo.getDiscount() / 100);
        double finalAmount = subTotal - discount;

        Transaction transaction = new Transaction(
                "T" + System.currentTimeMillis(),
                cart.getCartID(),
                promo.getPromoID(),
                subTotal,
                finalAmount,
                new java.util.Date()
        );

        transactions.add(transaction);
        System.out.println("Transaction created with ID: " + transaction.getTransactionID());
    }

    // Helper Methods
    private double calculateSubtotal(Cart cart) {
        double subtotal = 0;
        for (Product product : cart.getProducts().keySet()) {
            subtotal += product.getPrice() * cart.getProducts().get(product);
        }
        return subtotal;
    }

    // Admin Income Report
    public double getAdminIncome() {
        double totalIncome = 0;
        for (Transaction transaction : transactions) {
            totalIncome += transaction.getFinalAmount();
        }
        return totalIncome;
    }

    // Manage User
    public void activateUser(User user) {
        user.setActive(true);
        System.out.println("User " + user.getUsername() + " activated.");
    }

    public void deactivateUser(User user) {
        user.setActive(false);
        System.out.println("User " + user.getUsername() + " deactivated.");
    }

    

}


