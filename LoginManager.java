import java.util.Scanner;

public class LoginManager {
    private final Bank bank;
    private final Scanner sc;

    public LoginManager(Bank bank, Scanner sc) {
        this.bank = bank;
        this.sc = sc;
    }

    public void showLogin() {
        while (true) {
            System.out.println("\n========== LOGIN ==========");
            System.out.println("1. Clerk");
            System.out.println("2. Customer");
            System.out.println("0. Exit");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
               case "1":
    System.out.print("Clerk name: ");
    String name = sc.nextLine().trim();
    System.out.print("Password: ");
    String pwd = sc.nextLine().trim();
    if (!name.equalsIgnoreCase("Thabo Ngwako") || !pwd.equals("Thabo123")) {
        System.out.println("Invalid credentials.");
        break;
    }
    new ClerkMenu(bank, sc).start();   // pass name
    break;

case "2":
    System.out.print("Customer ID: ");
    String id = sc.nextLine().trim();
    Customer c = bank.findCustomerById(id);
    if (c == null) {
        System.out.println("Customer not found.");
        return;
    }
    new CustomerMenu(c, sc, c.firstName + " " + c.surname).start(); // pass name
    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void customerLogin() {
        System.out.print("Enter customer ID: ");
        String id = sc.nextLine().trim();
        Customer c = bank.findCustomerById(id);
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        new CustomerMenu(c, sc, id).start();
    }
}