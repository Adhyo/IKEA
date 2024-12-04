package Model;

public class Order {
    private String orderID;
    private String address;
    private String cartID;
    private double price;
    private OrderStatus status;

    public Order(String orderID, String address, String cartID, double price, OrderStatus status) {
        this.orderID = orderID;
        this.address = address;
        this.cartID = cartID;
        this.price = price;
        this.status = status;
    }
    
    public String getOrderID() {
        return orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCartID() {
        return cartID;
    }
    public void setCartID(String cartID) {
        this.cartID = cartID;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
