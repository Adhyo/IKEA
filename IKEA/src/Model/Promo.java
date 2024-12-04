package Model;

import java.util.Date;

public class Promo {
    private String promoID;
    private String promoName;
    private String description;
    private double discount;
    private Date expirationDate;
    
    public Promo(String promoID, String promoName, String description, double discount, Date expirationDate) {
        this.promoID = promoID;
        this.promoName = promoName;
        this.description = description;
        this.discount = discount;
        this.expirationDate = expirationDate;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}