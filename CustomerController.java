import java.util.List;

public class CustomerController {
    private Customer customer;
    private BankController bankController;

    public CustomerController(Customer customer, BankController bankController) {
        this.customer = customer;
        this.bankController = bankController;
    }

    public String deposit(String accountNumber, double amount) {
        try {
            if (amount <= 0) {
                return "Deposit amount must be greater than 0";
            }
            customer.deposit(accountNumber, amount);
            bankController.saveData();
            return String.format("Successfully deposited $%.2f to account %s", amount, accountNumber);
        } catch (Exception e) {
            return "Error during deposit: " + e.getMessage();
        }
    }

    public String withdraw(String accountNumber, double amount) {
        try {
            if (amount <= 0) {
                return "Withdrawal amount must be greater than 0";
            }
            boolean success = customer.withdraw(accountNumber, amount);
            bankController.saveData();
            return success ?
                    String.format("Successfully withdrew $%.2f from account %s", amount, accountNumber) :
                    "Withdrawal failed: Insufficient funds or invalid amount";
        } catch (Exception e) {
            return "Error during withdrawal: " + e.getMessage();
        }
    }

    public double getBalance(String accountNumber) {
        try {
            Account account = customer.findAccount(accountNumber);
            return account.getBalance();
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        try {
            Account account = customer.findAccount(accountNumber);
            return account.getTransactions();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Account> getAccounts() {
        return customer.getAccounts();
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCustomerName() {
        return customer.firstName + " " + customer.surname;
    }

    public BankController getBankController() {
        return bankController;
    }
}