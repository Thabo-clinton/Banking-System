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
            System.out.println("=== DEPOSIT DEBUG START ===");
            System.out.println("DEBUG: Deposit called - Account: " + accountNumber + ", Amount: " + amount);

            if (amount <= 0) {
                System.out.println("DEBUG: Amount validation failed - amount <= 0");
                return "Deposit amount must be greater than 0";
            }

            // Get the account first to validate
            Account account = customer.findAccount(accountNumber);
            System.out.println("DEBUG: Account found - Type: " + account.getClass().getSimpleName());
            System.out.println("DEBUG: Current balance: " + account.getBalance());
            System.out.println("DEBUG: Is SavingsAccount? " + (account instanceof SavingsAccount));

            // Check for savings account minimum deposit - PREVENT ALL deposits under $1000
            if (account instanceof SavingsAccount) {
                System.out.println("DEBUG: This is a SavingsAccount - checking minimum deposit rules");
                System.out.println("DEBUG: Amount < 1000? " + (amount < 1000));

                if (amount < 1000) {
                    System.out.println("DEBUG: VALIDATION FAILED - any deposit to savings must be at least $1000");
                    return "Deposit failed: Minimum deposit for Savings Account is $1000";
                }
                System.out.println("DEBUG: Savings account validation PASSED");
            } else {
                System.out.println("DEBUG: Not a savings account - no minimum deposit validation");
            }

            // If validation passes, process the deposit
            System.out.println("DEBUG: Proceeding with actual deposit...");
            customer.deposit(accountNumber, amount);
            bankController.saveData();
            System.out.println("DEBUG: Deposit completed successfully");
            return String.format("Successfully deposited $%.2f to account %s", amount, accountNumber);

        } catch (IllegalArgumentException e) {
            System.out.println("DEBUG: Exception occurred: " + e.getMessage());
            return "Deposit failed: " + e.getMessage();
        } catch (Exception e) {
            System.out.println("DEBUG: General exception: " + e.getMessage());
            return "Error during deposit: " + e.getMessage();
        } finally {
            System.out.println("=== DEPOSIT DEBUG END ===");
        }
    }

    public String testSavingsValidation() {
        try {
            // Create a test savings account
            customer.openAccount("savings", 0, "Test Branch");
            Account testAccount = customer.getAccounts().get(customer.getAccounts().size() - 1);
            String accountNumber = testAccount.getAccountNumber();

            System.out.println("=== TEST VALIDATION ===");
            System.out.println("Test Account: " + accountNumber);
            System.out.println("Initial Balance: " + testAccount.getBalance());
            System.out.println("Is Savings: " + (testAccount instanceof SavingsAccount));

            // Test deposit of $500 (should fail)
            String result = deposit(accountNumber, 500);
            System.out.println("Test Result: " + result);

            return result;
        } catch (Exception e) {
            return "Test failed: " + e.getMessage();
        }
    }

    public String withdraw(String accountNumber, double amount) {
        try {
            System.out.println("DEBUG: Withdraw called - Account: " + accountNumber + ", Amount: " + amount);

            if (amount <= 0) {
                return "Withdrawal amount must be greater than 0";
            }

            // Get the account first to validate
            Account account = customer.findAccount(accountNumber);
            System.out.println("DEBUG: Account type: " + account.getClass().getSimpleName());

            // Check for savings account withdrawal restrictions
            if (account instanceof SavingsAccount) {
                System.out.println("DEBUG: Blocking withdrawal from SavingsAccount");
                return "Withdrawal failed: Cannot withdraw from Savings Account. Please use a different account type.";
            }

            boolean success = customer.withdraw(accountNumber, amount);
            bankController.saveData();
            return success ?
                    String.format("Successfully withdrew $%.2f from account %s", amount, accountNumber) :
                    "Withdrawal failed: Insufficient funds or invalid amount";
        } catch (IllegalArgumentException e) {
            return "Withdrawal failed: " + e.getMessage();
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
        if (customer instanceof IndividualCustomer) {
            return customer.firstName + " " + customer.surname;
        } else if (customer instanceof CompanyCustomer) {
            return ((CompanyCustomer) customer).companyName;
        }
        return "Customer";
    }

    public BankController getBankController() {
        return bankController;
    }
}