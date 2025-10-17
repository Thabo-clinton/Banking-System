import java.util.*;

public class LoginController {
    private BankController bankController;

    public LoginController(BankController bankController) {
        this.bankController = bankController;
    }

    public boolean validateClerk(String name, String password) {
        return name.equalsIgnoreCase("Thabo Ngwako") && password.equals("Thabo123");
    }

    public Customer validateCustomer(String customerId) {
        return bankController.findCustomerById(customerId);
    }

    public BankController getBankController() {
        return bankController;
    }
}