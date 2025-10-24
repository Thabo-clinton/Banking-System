import java.io.Serializable;
import java.util.*;

public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final String accountNumber;
    protected double balance;
    protected final String branch;
    protected final Customer owner;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, double balance, String branch, Customer owner) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.owner = owner;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        balance += amount;
    }

    public abstract boolean withdraw(double amount);

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    void addTransaction(Transaction t) {
        transactions.add(t);
    }

    @Override
    public String toString() {
        return accountNumber + " (" + getClass().getSimpleName() + ")  Balance: " + balance;
    }
}