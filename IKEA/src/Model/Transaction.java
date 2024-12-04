package Model;

import java.util.Date;

public class Transaction {
    private String transactionID;
    private String cartID;
    private String promoID;
    private double subTotal;
    private double finalAmount;
    private Date transactionDate;
    
    public Transaction(String transactionID, String cartID, String promoID, double subTotal, double finalAmount,
            Date transactionDate) {
        this.transactionID = transactionID;
        this.cartID = cartID;
        this.promoID = promoID;
        this.subTotal = subTotal;
        this.finalAmount = finalAmount;
        this.transactionDate = transactionDate;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

}

