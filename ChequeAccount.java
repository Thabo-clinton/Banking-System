public class ChequeAccount extends Account {
    private String employer;
    private String companyAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer owner, String employer, String companyAddress) {
        super(accountNumber, balance, branch, owner);
        this.employer = employer;
        this.companyAddress = companyAddress;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        balance -= amount;
        return true;
    }

    public String getEmployer() {
        return employer;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }
}