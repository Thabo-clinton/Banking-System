import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private BankController bankController;
    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) {
        bankController = new BankController("Acme Bank");
        loginController = new LoginController(bankController);

        LoginView loginView = new LoginView(loginController, primaryStage);
        Scene scene = new Scene(loginView.getView(), 800, 600);

        // Add CSS
        scene.getStylesheets().add("styles.css");

        primaryStage.setTitle("Acme Bank");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Save data when application closes
        if (bankController != null) {
            bankController.saveData();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}