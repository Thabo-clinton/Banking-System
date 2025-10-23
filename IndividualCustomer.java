public class IndividualCustomer extends Customer {
    private static int idCounter = 1000;
    final int customerId;
    private final String branch;

    public IndividualCustomer(String firstName, String lastName, String address, String branch) {
        super(firstName, lastName, address);
        this.customerId = idCounter++;
        this.branch = branch;
    }

    @Override
    public String getCustomerId() {
        return "IND-" + customerId;
    }

    @Override
    public void openAccount(String type, double initialDeposit, String branch, String... extra) {
        String accNo = getCustomerId() + "-A" + (accounts.size() + 1);
        System.out.println("DEBUG: Opening account - Type: " + type + ", Initial Deposit: " + initialDeposit);

        switch (type.toLowerCase()) {
            case "savings":
                // Enforce minimum deposit when creating savings account
                if (initialDeposit < 1000) {
                    throw new IllegalArgumentException("Minimum initial deposit for Savings Account is $1000");
                }
                accounts.add(new SavingsAccount(accNo, initialDeposit, branch, this));
                System.out.println("DEBUG: Savings account created with balance: " + initialDeposit);
                break;
            case "investment":
                accounts.add(new InvestmentAccount(accNo, initialDeposit, branch, this));
                break;
            case "cheque":
                if (extra.length < 2) throw new IllegalArgumentException("Employer & address required");
                accounts.add(new ChequeAccount(accNo, initialDeposit, branch, this, extra[0], extra[1]));
                break;
            default:
                throw new IllegalArgumentException("Unknown account type");
        }
    }

    public String getBranch() {
        return branch;
    }
}