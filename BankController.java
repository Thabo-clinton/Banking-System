import java.util.List;

public class BankController {
    private Bank bank;

    public BankController(String bankName) {
        this.bank = new Bank(bankName);
        loadData();
    }

    public void loadData() {
        TextFileRepository.load(bank);
    }

    public void saveData() {
        TextFileRepository.save(bank);
    }

    public Customer addIndividualCustomer(String firstName, String surname, String address) {
        Customer customer = bank.addIndividualCustomer(firstName, surname, address);
        saveData();
        return customer;
    }

    public Customer addCompanyCustomer(String companyName, String address, String cellNumber) {
        Customer customer = bank.addCompanyCustomer(companyName, address, cellNumber);
        saveData();
        return customer;
    }

    public void createAccount(String customerId, String accountType, double initialDeposit,
                              String branch, String... extra) {
        Customer customer = bank.findCustomerById(customerId);
        if (customer != null) {
            customer.openAccount(accountType, initialDeposit, branch, extra);
            saveData();
        } else {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
    }

    public void applyMonthlyInterest() {
        bank.applyInterestToAllCustomers();
        saveData();
    }

    public List<Customer> getAllCustomers() {
        return bank.getCustomers();
    }

    public Customer findCustomerById(String id) {
        return bank.findCustomerById(id);
    }

    public Bank getBank() {
        return bank;
    }
}