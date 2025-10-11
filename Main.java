import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Bank bank = new Bank("Acme Bank");
        bank.loadData();                       // load previous session
        LoginManager login = new LoginManager(bank, sc);
        login.showLogin();
        bank.saveData();                       // save before exit
        sc.close();
        System.out.println("Good-bye!");
    }
}