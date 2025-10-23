public class SavingsAccount extends Account {
    private static final double MIN_INITIAL_DEPOSIT = 1000.0;

    public SavingsAccount(String accountNumber, double balance, String branch, Customer owner) {
        super(accountNumber, balance, branch, owner);
        System.out.println("DEBUG: SavingsAccount created - Balance: " + balance);
    }

    @Override
    public boolean withdraw(double amount) {
        System.out.println("DEBUG: SavingsAccount withdraw attempted - Amount: " + amount);
        // Completely prevent withdrawals from savings accounts
        return false;
    }

    @Override
    public void deposit(double amount) {
        System.out.println("DEBUG: SavingsAccount.deposit called - Amount: " + amount + ", Current balance: " + balance);

        // Backup validation - should already be handled by controller, but just in case
        if (balance == 0 && amount < MIN_INITIAL_DEPOSIT) {
            System.out.println("DEBUG: SavingsAccount VALIDATION FAILED - initial deposit < 1000");
            throw new IllegalArgumentException("Minimum initial deposit for Savings Account is $1000");
        }

        System.out.println("DEBUG: SavingsAccount deposit validation PASSED");
        balance += amount;
    }
}