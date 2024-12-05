package Model;

public class Admin extends User{
    private double income;

    public Admin(int userId, String username, String password, String email, double income) {
        super(userId, username, password, email, UserType.ADMIN);
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
