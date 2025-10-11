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
                if (p[0].startsWith("IND"))
                    bank.addIndividualCustomer(p[1], p[2], p[3]);
                else
                    bank.addCompanyCustomer(p[1], p[2], p[3]);
            }

            /* 2. accounts */
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
                        owner.openAccount("savings", bal, branch);
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

            /* 3. transactions */
            lines = Files.readAllLines(Paths.get(TXN_FILE));
            for (String line : lines) {
                String[] p = line.split("\\|", -1);
                Account acc = bank.findAccountByNumber(p[0]);
                if (acc == null) continue;
                acc.addTransaction(new Transaction(p[1], Double.parseDouble(p[2])));
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
                if (c instanceof IndividualCustomer ic)
                    cw.printf("IND-%d|%s|%s|%s%n", ic.customerId, ic.firstName, ic.surname, ic.address);
                else if (c instanceof CompanyCustomer cc)
                    cw.printf("CMP-%d|%s|%s|%s%n", cc.customerId, cc.companyName, cc.address, cc.cellNumber);
            }

            /* accounts + transactions */
            for (Customer c : bank.getCustomers()) {
                for (Account a : c.getAccounts()) {
                    aw.printf("%s|%s|%s|%.2f|%s", a.getAccountNumber(), c.getCustomerId(),
                            a.getClass().getSimpleName(), a.getBalance(), a.branch);
                    if (a instanceof ChequeAccount ch)
                        aw.printf("|%s|%s", ch.getEmployer(), ch.getCompanyAddress());
                    aw.println();

                    for (Transaction t : a.getTransactions())
                        tw.printf("%s|%s|%.2f%n", a.getAccountNumber(), t.type, t.amount);
                }
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }
}