import java.io.Serializable;
import java.util.*;

public abstract class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final String firstName;
    protected final String surname;
    protected final String address;
    protected final List<Account> accounts = new ArrayList<>();

    public Customer(String firstName, String surname, String address) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
    }

    public abstract String getCustomerId();

    public abstract void openAccount(String type, double initialDeposit, String branch, String... extra);

    public void deposit(String accountNumber, double amount) {
        Account a = findAccount(accountNumber);
        a.deposit(amount);
        a.addTransaction(new Transaction("DEPOSIT", amount));
    }

    public boolean withdraw(String accountNumber, double amount) {
        Account a = findAccount(accountNumber);
        boolean ok = a.withdraw(amount);
        if (ok) a.addTransaction(new Transaction("WITHDRAW", amount));
        return ok;
    }

    public void applyInterestToAllAccounts() {
        accounts.stream()
                .filter(a -> a instanceof InterestBearing)
                .forEach(a -> ((InterestBearing) a).applyMonthlyInterest());
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    Account findAccount(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Override
    public String toString() {
        return getCustomerId() + " - " + firstName + " " + surname + " (" + address + ")";
    }
}