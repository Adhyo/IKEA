package Model;

public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String userType;
    private boolean isActive;

    public User(int userId, String username, String email, String userType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userType = userType;
        this.isActive = true;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
