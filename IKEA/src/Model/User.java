package Model;

public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private UserType userType;
    private boolean isActive;

    public User(int userId, String username, String password, String email, UserType userType) {
        this.userId = userId;
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
