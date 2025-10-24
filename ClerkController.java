import java.util.List;

public class ClerkController {
    private BankController bankController;

    public ClerkController(BankController bankController) {
        this.bankController = bankController;
    }

    public String addIndividualCustomer(String firstName, String surname, String address, String branch) {
        try {
            if (firstName.isEmpty() || surname.isEmpty() || address.isEmpty() || branch.isEmpty()) {
                return "All fields are required";
            }
            Customer customer = bankController.addIndividualCustomer(firstName, surname, address, branch);
            return "Individual customer created successfully! ID: " + customer.getCustomerId();
        } catch (Exception e) {
            return "Error creating customer: " + e.getMessage();
        }
    }

    public String addCompanyCustomer(String companyName, String address, String cellNumber, String branch) {
        try {
            if (companyName.isEmpty() || address.isEmpty() || cellNumber.isEmpty() || branch.isEmpty()) {
                return "All fields are required";
            }
            Customer customer = bankController.addCompanyCustomer(companyName, address, cellNumber, branch);
            return "Company customer created successfully! ID: " + customer.getCustomerId();
        } catch (Exception e) {
            return "Error creating customer: " + e.getMessage();
        }
    }

    public String createAccount(String customerId, String accountType, double initialDeposit,
                                String branch, String employer, String companyAddress) {
        try {
            if (customerId.isEmpty() || accountType.isEmpty() || branch.isEmpty()) {
                return "Customer ID, account type, and branch are required";
            }

            if (initialDeposit < 0) {
                return "Initial deposit cannot be negative";
            }

            // Validate savings account minimum deposit
            if (accountType.equals("savings") && initialDeposit < 1000) {
                return "Savings account requires minimum deposit of $1000";
            }

            String[] extra = accountType.equals("cheque") ?
                    new String[]{employer, companyAddress} : new String[0];

            bankController.createAccount(customerId, accountType, initialDeposit, branch, extra);
            return "Account created successfully!";
        } catch (Exception e) {
            return "Error creating account: " + e.getMessage();
        }
    }

    public String applyMonthlyInterest() {
        try {
            bankController.applyMonthlyInterest();
            return "Monthly interest applied to all eligible accounts!";
        } catch (Exception e) {
            return "Error applying interest: " + e.getMessage();
        }
    }

    public List<Customer> getAllCustomers() {
        return bankController.getAllCustomers();
    }

    public Customer findCustomerById(String customerId) {
        return bankController.findCustomerById(customerId);
    }

    public BankController getBankController() {
        return bankController;
    }
}