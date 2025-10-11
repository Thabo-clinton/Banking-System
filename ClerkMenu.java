import java.util.Scanner;

public class ClerkMenu {
    private final Bank bank;
    private final Scanner sc;

    public ClerkMenu(Bank bank, Scanner sc) {
        this.bank = bank;
        this.sc = sc;
    }

    public void start() {
        while (true) {
System.out.println("\n========== THABO NGWAKO ==========");
            System.out.println("1. Add individual customer");
            System.out.println("2. Add company customer");
            System.out.println("3. Create account for customer");
            System.out.println("4. Apply monthly interest (all accounts)");
            System.out.println("5. View all customers");
            System.out.println("0. Log out");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> addIndividual();
                case "2" -> addCompany();
                case "3" -> createAccount();
                case "4" -> {
                    bank.applyInterestToAllCustomers();
                    System.out.println("Monthly interest applied.");
                }
                case "5" -> viewAllCustomers();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addIndividual() {
        System.out.print("First name: ");
        String fn = sc.nextLine().trim();
        System.out.print("Surname: ");
        String sn = sc.nextLine().trim();
        System.out.print("Address: ");
        String ad = sc.nextLine().trim();
        Customer c = bank.addIndividualCustomer(fn, sn, ad);
        System.out.println("Individual customer created. ID: " + c.getCustomerId());
          bank.saveData();  
    }

    private void addCompany() {
        System.out.print("Company name: ");
        String cn = sc.nextLine().trim();
        System.out.print("Address: ");
        String ad = sc.nextLine().trim();
        System.out.print("Cell number: ");
        String cell = sc.nextLine().trim();
        Customer c = bank.addCompanyCustomer(cn, ad, cell);
        System.out.println("Company customer created. ID: " + c.getCustomerId());
          bank.saveData();  
    }

    private void createAccount() {
        System.out.print("Customer ID: ");
        String id = sc.nextLine().trim();
        Customer c = bank.findCustomerById(id);
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.print("Account type (savings/investment/cheque): ");
        String type = sc.nextLine().trim().toLowerCase();
        System.out.print("Initial deposit: ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Branch: ");
        String br = sc.nextLine().trim();
        String[] extra = null;
        if (type.equals("cheque")) {
            System.out.print("Employer: ");
            String emp = sc.nextLine().trim();
            System.out.print("Company address: ");
            String addr = sc.nextLine().trim();
            extra = new String[]{emp, addr};
        }
        c.openAccount(type, amt, br, extra != null ? extra : new String[0]);
        System.out.println("Account created.");
          bank.saveData();  
    }

    private void viewAllCustomers() {
        bank.getCustomers().forEach(c -> {
            System.out.println(c);
            c.getAccounts().forEach(a -> System.out.println("  --> " + a));
        });
    }
}