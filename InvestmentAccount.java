public class InvestmentAccount extends Account implements InterestBearing {
    private static final double MONTHLY_RATE = 0.00075; // 0.075 %

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer owner) {
        super(accountNumber, balance, branch, owner);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    @Override
    public void applyMonthlyInterest() {
        double interest = balance * MONTHLY_RATE;
        deposit(interest);
        addTransaction(new Transaction("INTEREST", interest));
    }
}