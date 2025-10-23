import java.util.*;

public class Bank {
    private final String name;
    private final List<Customer> customers = new ArrayList<>();

    public Bank(String name) { this.name = name; }

    /* ----- public clerk API ----- */
    public Customer addIndividualCustomer(String firstName, String surname, String address, String branch) {
        IndividualCustomer c = new IndividualCustomer(firstName, surname, address, branch);
        customers.add(c);
        return c;
    }

    public Customer addCompanyCustomer(String companyName, String address, String cellNumber, String branch) {
        CompanyCustomer c = new CompanyCustomer(companyName, address, cellNumber, branch);
        customers.add(c);
        return c;
    }

    public void applyInterestToAllCustomers() {
        customers.forEach(Customer::applyInterestToAllAccounts);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public Customer findCustomerById(String id) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /* ----- persistence helpers ----- */
    void loadData() { TextFileRepository.load(this); }
    void saveData() { TextFileRepository.save(this); }

    /* ======  MISSING METHOD NOW ADDED  ====== */
    Account findAccountByNumber(String accountNumber) {
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber().equals(accountNumber))
                    return a;
            }
        }
        return null;
    }
}