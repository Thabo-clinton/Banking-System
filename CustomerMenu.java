import java.util.Scanner;

public class CustomerMenu {
    private final Customer customer;
    private final Scanner sc;
    private final String customerName;

    public CustomerMenu(Customer customer, Scanner sc, String customerName) {
        this.customer = customer;
        this.sc = sc;
        this.customerName = customerName;
    }

    public void start() {
        while (true) {
            System.out.println("\n========== " + customerName.toUpperCase() + " ==========");
        
            System.out.println("1. View my accounts");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check balance");
            System.out.println("5. Transaction history");
            System.out.println("0. Log out");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> viewAccounts();
                case "2" -> deposit();
                case "3" -> withdraw();
                case "4" -> checkBalance();
                case "5" -> transactionHistory();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAccounts() {
        customer.getAccounts().forEach(System.out::println);
    }

    private void deposit() {
        String acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to deposit: ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        customer.deposit(acc, amt);
        System.out.println("Deposited.");
    }

    private void withdraw() {
        String acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to withdraw: ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        boolean ok = customer.withdraw(acc, amt);
        System.out.println(ok ? "Withdrawn." : "Withdrawal failed.");
    }

    private void checkBalance() {
        String acc = selectAccount();
        if (acc == null) return;
        System.out.printf("Balance: %.2f%n", customer.findAccount(acc).getBalance());
    }

    private void transactionHistory() {
        String acc = selectAccount();
        if (acc == null) return;
        customer.findAccount(acc).getTransactions().forEach(System.out::println);
    }

    private String selectAccount() {
        var list = customer.getAccounts();
        if (list.isEmpty()) {
            System.out.println("No accounts.");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).getAccountNumber());
        }
        System.out.print("Select account (number): ");
        int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid.");
            return null;
        }
        return list.get(idx).getAccountNumber();
    }
}