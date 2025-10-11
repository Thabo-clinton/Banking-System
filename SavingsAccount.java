public class SavingsAccount extends Account implements InterestBearing {
    private static final double MONTHLY_RATE = 0.00025; // 0.025 %

    public SavingsAccount(String accountNumber, double balance, String branch, Customer owner) {
        super(accountNumber, balance, branch, owner);
    }

    @Override
    public boolean withdraw(double amount) {
        throw new UnsupportedOperationException("Savings account does not allow withdrawals.");
    }

    @Override
    public void applyMonthlyInterest() {
        double interest = balance * MONTHLY_RATE;
        deposit(interest);
        addTransaction(new Transaction("INTEREST", interest));
    }
}