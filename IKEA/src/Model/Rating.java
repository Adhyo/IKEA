package Model;

import java.util.Date;

public class Rating {
    private String ratingID;
    private String customerID;
    private int rating;
    private String feedbackText;
    private Date reviewDate;
    
    public Rating(String ratingID, String customerID, int rating, String feedbackText, Date reviewDate) {
        this.ratingID = ratingID;
        this.customerID = customerID;
        this.rating = rating;
        this.feedbackText = feedbackText;
        this.reviewDate = reviewDate;
    }

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
}
