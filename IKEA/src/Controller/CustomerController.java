package Controller;

import Model.Customer;
import Model.UserType;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private List<Customer> customerList;

    public CustomerController() {
        this.customerList = new ArrayList<>();

        // Example data
        customerList.add(new Customer(1, "john_doe", "kocak123", "john@gmail.com", UserType.CUSTOMER, "John Doe", "123456789"));
        customerList.add(new Customer(2, "jane_doe", "awikwok123", "jane@gmail.com", UserType.CUSTOMER, "Jane Doe", "987654321"));
    }

    public List<Customer> getAllCustomers() {
        return customerList;
    }

    public void addCustomer(Customer customer) {
        customerList.add(customer);
    }
}