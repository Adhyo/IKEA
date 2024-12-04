package Model;

public class Admin extends User{
    private double income;

    public Admin(int userId, String username, String email, String userType, double income) {
        super(userId, username, email, userType);
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
