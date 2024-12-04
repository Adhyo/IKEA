package Model;

import java.util.Date;

public class Notification {
    private String notificationID;
    private String message;
    private Date notifDate;
    
    public Notification(String notificationID, String message, Date notifDate) {
        this.notificationID = notificationID;
        this.message = message;
        this.notifDate = notifDate;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getNotifDate() {
        return notifDate;
    }

    public void setNotifDate(Date notifDate) {
        this.notifDate = notifDate;
    }
}