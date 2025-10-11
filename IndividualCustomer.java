public class IndividualCustomer extends Customer {
    private static int idCounter = 1000;
    final int customerId;

    public IndividualCustomer(String firstName, String lastName, String address) {
        super(firstName, lastName, address);
        this.customerId = idCounter++;
    }

    @Override
    public String getCustomerId() {
        return "IND-" + customerId;
    }

    @Override
    public void openAccount(String type, double initialDeposit, String branch, String... extra) {
        String accNo = getCustomerId() + "-A" + (accounts.size() + 1);
        switch (type.toLowerCase()) {
            case "savings":
                accounts.add(new SavingsAccount(accNo, initialDeposit, branch, this));
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
}