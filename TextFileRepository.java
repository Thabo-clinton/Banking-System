import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextFileRepository {
    private static final String CUST_FILE = "customers.txt";
    private static final String ACC_FILE  = "accounts.txt";
    private static final String TXN_FILE  = "transactions.txt";

    /* ---------- LOAD ---------- */
    static void load(Bank bank) {
        if (!Files.exists(Paths.get(CUST_FILE))) return;

        try {
            /* 1. customers */
            List<String> lines = Files.readAllLines(Paths.get(CUST_FILE));
            for (String line : lines) {
                String[] p = line.split("\\|", -1);
                if (p[0].startsWith("IND")) {
                    // Individual customer: ID|firstName|surname|address|branch
                    if (p.length >= 5) {
                        bank.addIndividualCustomer(p[1], p[2], p[3], p[4]);
                    } else if (p.length >= 4) {
                        // For backward compatibility
                        bank.addIndividualCustomer(p[1], p[2], p[3], "Main Branch");
                    }
                } else if (p[0].startsWith("CMP")) {
                    // Company customer: ID|companyName|address|cellNumber|branch
                    if (p.length >= 5) {
                        bank.addCompanyCustomer(p[1], p[2], p[3], p[4]);
                    } else if (p.length >= 4) {
                        // For backward compatibility
                        bank.addCompanyCustomer(p[1], p[2], p[3], "Main Branch");
                    }
                }
            }

            /* 2. accounts */
            if (Files.exists(Paths.get(ACC_FILE))) {
                lines = Files.readAllLines(Paths.get(ACC_FILE));
                for (String line : lines) {
                    String[] p = line.split("\\|", -1);
                    Customer owner = bank.findCustomerById(p[1]);
                    if (owner == null) continue;

                    String type   = p[2];
                    String accNo  = p[0];
                    double bal    = Double.parseDouble(p[3]);
                    String branch = p[4];

                    switch (type) {
                        case "SavingsAccount":
                            // Ensure minimum deposit for savings accounts
                            double actualDeposit = Math.max(bal, 1000.0); // Enforce $1000 minimum
                            owner.openAccount("savings", actualDeposit, branch);
                            break;
                        case "InvestmentAccount":
                            owner.openAccount("investment", bal, branch);
                            break;
                        case "ChequeAccount":
                            if (p.length < 7) continue;
                            owner.openAccount("cheque", bal, branch, p[5], p[6]);
                            break;
                    }
                }
            }

            /* 3. transactions */
            if (Files.exists(Paths.get(TXN_FILE))) {
                lines = Files.readAllLines(Paths.get(TXN_FILE));
                for (String line : lines) {
                    String[] p = line.split("\\|", -1);
                    Account acc = bank.findAccountByNumber(p[0]);
                    if (acc == null) continue;
                    acc.addTransaction(new Transaction(p[1], Double.parseDouble(p[2])));
                }
            }

        } catch (IOException e) {
            System.out.println("Load warning: " + e.getMessage());
        }
    }

    /* ---------- SAVE ---------- */
    static void save(Bank bank) {
        try (PrintWriter cw = new PrintWriter(CUST_FILE);
             PrintWriter aw = new PrintWriter(ACC_FILE);
             PrintWriter tw = new PrintWriter(TXN_FILE)) {

            /* customers */
            for (Customer c : bank.getCustomers()) {
                if (c instanceof IndividualCustomer) {
                    IndividualCustomer ic = (IndividualCustomer) c;
                    // Save: IND-id|firstName|surname|address|branch
                    cw.printf("IND-%d|%s|%s|%s|%s%n",
                            ic.customerId, ic.firstName, ic.surname, ic.address, getCustomerBranch(ic));
                } else if (c instanceof CompanyCustomer) {
                    CompanyCustomer cc = (CompanyCustomer) c;
                    // Save: CMP-id|companyName|address|cellNumber|branch
                    cw.printf("CMP-%d|%s|%s|%s|%s%n",
                            cc.customerId, cc.companyName, cc.address, cc.cellNumber, getCustomerBranch(cc));
                }
            }

            /* accounts */
            for (Customer c : bank.getCustomers()) {
                for (Account a : c.getAccounts()) {
                    aw.printf("%s|%s|%s|%.2f|%s",
                            a.getAccountNumber(), c.getCustomerId(),
                            a.getClass().getSimpleName(), a.getBalance(), a.branch);
                    if (a instanceof ChequeAccount) {
                        ChequeAccount ch = (ChequeAccount) a;
                        aw.printf("|%s|%s", ch.getEmployer(), ch.getCompanyAddress());
                    }
                    aw.println();
                }
            }

            /* transactions */
            for (Customer c : bank.getCustomers()) {
                for (Account a : c.getAccounts()) {
                    for (Transaction t : a.getTransactions()) {
                        tw.printf("%s|%s|%.2f%n", a.getAccountNumber(), t.type, t.amount);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    /* ---------- HELPER METHOD TO GET CUSTOMER BRANCH ---------- */
    private static String getCustomerBranch(Customer customer) {
        // Use the getter methods we just added
        if (customer instanceof IndividualCustomer) {
            return ((IndividualCustomer) customer).getBranch();
        } else if (customer instanceof CompanyCustomer) {
            return ((CompanyCustomer) customer).getBranch();
        }

        // Fallback: get branch from first account or use default
        if (!customer.getAccounts().isEmpty()) {
            return customer.getAccounts().get(0).branch;
        }

        return "Main Branch"; // Default fallback
    }
}