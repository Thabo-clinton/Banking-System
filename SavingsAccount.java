public class SavingsAccount extends Account {
    private static final double MIN_INITIAL_DEPOSIT = 1000.0;

    public SavingsAccount(String accountNumber, double balance, String branch, Customer owner) {
        super(accountNumber, balance, branch, owner);
    }

    @Override
    public boolean withdraw(double amount) {
        // Completely prevent withdrawals from savings accounts
        return false;
    }

    @Override
    public void deposit(double amount) {
        // Backup validation
        if (balance == 0 && amount < MIN_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Minimum initial deposit for Savings Account is $1000");
        }
        balance += amount;
    }
}