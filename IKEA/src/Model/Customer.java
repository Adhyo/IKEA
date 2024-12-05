package Model;

public class Customer extends User {
    private String name;
    private String phone;
    private String address;

    public Customer(int userId, String username, String password, String email, String name, String phone) {
        super(userId, username, password, email, UserType.CUSTOMER);
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}