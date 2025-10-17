import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class CustomerView {
    private CustomerController controller;
    private Stage primaryStage;
    private BorderPane view;
    private ComboBox<String> accountComboBox;
    private TableView<Account> accountsTable;
    private TableView<Transaction> transactionsTable;
    private ObservableList<Account> accountData;
    private ObservableList<Transaction> transactionData;

    public CustomerView(CustomerController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        createView();
        refreshData();
    }

    private void createView() {
        view = new BorderPane();
        view.getStyleClass().add("main-container");

        // Header
        HBox headerBox = createHeader();
        view.setTop(headerBox);

        // TabPane for different functionalities
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("main-tab-pane");

        // Accounts Tab
        Tab accountsTab = new Tab("My Accounts");
        accountsTab.setContent(createAccountsTab());
        accountsTab.setClosable(false);

        // Transactions Tab
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setContent(createTransactionsTab());
        transactionsTab.setClosable(false);

        // Banking Tab
        Tab bankingTab = new Tab("Banking Operations");
        bankingTab.setContent(createBankingTab());
        bankingTab.setClosable(false);

        tabPane.getTabs().addAll(accountsTab, transactionsTab, bankingTab);
        view.setCenter(tabPane);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Welcome, " + controller.getCustomerName());
        welcomeLabel.getStyleClass().add("header-title");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, logoutButton);

        return header;
    }

    private VBox createAccountsTab() {
        VBox tabContent = new VBox(10);
        tabContent.setPadding(new Insets(15));

        Label titleLabel = new Label("My Accounts");
        titleLabel.getStyleClass().add("section-title");

        // Accounts Table
        accountsTable = new TableView<>();
        setupAccountsTable();

        VBox.setVgrow(accountsTable, Priority.ALWAYS);
        tabContent.getChildren().addAll(titleLabel, accountsTable);

        return tabContent;
    }

    private void setupAccountsTable() {
        TableColumn<Account, String> accNumberCol = new TableColumn<>("Account Number");
        accNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getClass().getSimpleName()
                ));

        TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setCellFactory(col -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));

        accountsTable.getColumns().addAll(accNumberCol, typeCol, balanceCol, branchCol);
        accountData = FXCollections.observableArrayList();
        accountsTable.setItems(accountData);
    }

    private VBox createTransactionsTab() {
        VBox tabContent = new VBox(10);
        tabContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Transaction History");
        titleLabel.getStyleClass().add("section-title");

        // Account Selection
        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        Label accountLabel = new Label("Select Account:");
        accountComboBox = new ComboBox<>();
        accountComboBox.getStyleClass().add("form-field");

        Button viewTransactionsBtn = new Button("View Transactions");
        viewTransactionsBtn.getStyleClass().add("primary-button");
        viewTransactionsBtn.setOnAction(e -> viewTransactions());

        selectionBox.getChildren().addAll(accountLabel, accountComboBox, viewTransactionsBtn);

        // Transactions Table
        transactionsTable = new TableView<>();
        setupTransactionsTable();

        VBox.setVgrow(transactionsTable, Priority.ALWAYS);
        tabContent.getChildren().addAll(titleLabel, selectionBox, transactionsTable);

        return tabContent;
    }

    private void setupTransactionsTable() {
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().toString().split("\\|")[0].trim()
                ));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        transactionsTable.getColumns().addAll(dateCol, typeCol, amountCol);
        transactionData = FXCollections.observableArrayList();
        transactionsTable.setItems(transactionData);
    }

    private VBox createBankingTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(15));
        tabContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Banking Operations");
        titleLabel.getStyleClass().add("section-title");

        // Account Selection
        HBox accountSelectionBox = new HBox(10);
        accountSelectionBox.setAlignment(Pos.CENTER);

        Label accountLabel = new Label("Select Account:");
        ComboBox<String> bankingAccountCombo = new ComboBox<>();
        bankingAccountCombo.getStyleClass().add("form-field");

        accountSelectionBox.getChildren().addAll(accountLabel, bankingAccountCombo);

        // Deposit Section
        VBox depositBox = createOperationBox("Deposit", bankingAccountCombo, true);

        // Withdrawal Section
        VBox withdrawalBox = createOperationBox("Withdraw", bankingAccountCombo, false);

        // Balance Check
        Button balanceButton = new Button("Check Balance");
        balanceButton.getStyleClass().add("info-button");
        balanceButton.setOnAction(e -> {
            String accountNum = bankingAccountCombo.getValue();
            if (accountNum != null) {
                double balance = controller.getBalance(accountNum);
                if (balance >= 0) {
                    showAlert("Balance", String.format("Current Balance: $%.2f", balance));
                } else {
                    showAlert("Error", "Error retrieving balance");
                }
            }
        });

        // Update combo boxes when data refreshes
        refreshData();

        tabContent.getChildren().addAll(
                titleLabel, accountSelectionBox, depositBox,
                withdrawalBox, balanceButton
        );

        return tabContent;
    }

    private VBox createOperationBox(String operation, ComboBox<String> accountCombo, boolean isDeposit) {
        VBox operationBox = new VBox(10);
        operationBox.setPadding(new Insets(15));
        operationBox.getStyleClass().add("operation-box");
        operationBox.setAlignment(Pos.CENTER);

        Label opLabel = new Label(operation);
        opLabel.getStyleClass().add("operation-title");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.getStyleClass().add("form-field");

        Button opButton = new Button(operation);
        opButton.getStyleClass().add(isDeposit ? "success-button" : "warning-button");

        opButton.setOnAction(e -> {
            String accountNum = accountCombo.getValue();
            if (accountNum != null) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String result = isDeposit ?
                            controller.deposit(accountNum, amount) :
                            controller.withdraw(accountNum, amount);

                    showAlert(operation, result);
                    refreshData();
                    amountField.clear();
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Please enter a valid amount");
                }
            }
        });

        operationBox.getChildren().addAll(opLabel, amountField, opButton);
        return operationBox;
    }

    private void refreshData() {
        // Refresh accounts table
        List<Account> accounts = controller.getAccounts();
        accountData.setAll(accounts);

        // Refresh account comboboxes
        ObservableList<String> accountNumbers = FXCollections.observableArrayList();
        for (Account account : accounts) {
            accountNumbers.add(account.getAccountNumber());
        }

        accountComboBox.setItems(accountNumbers);
        if (!accountNumbers.isEmpty()) {
            accountComboBox.setValue(accountNumbers.get(0));
        }

        // Update all comboboxes in the view
        updateAllComboBoxes(view, accountNumbers);
    }

    private void updateAllComboBoxes(javafx.scene.Node node, ObservableList<String> accountNumbers) {
        if (node instanceof ComboBox) {
            @SuppressWarnings("unchecked")
            ComboBox<String> combo = (ComboBox<String>) node;
            if (combo != accountComboBox) { // Don't update the transactions combo
                combo.setItems(accountNumbers);
                if (!accountNumbers.isEmpty()) {
                    combo.setValue(accountNumbers.get(0));
                }
            }
        }

        if (node instanceof Pane) {
            for (javafx.scene.Node child : ((Pane) node).getChildren()) {
                updateAllComboBoxes(child, accountNumbers);
            }
        }
    }

    private void viewTransactions() {
        String accountNum = accountComboBox.getValue();
        if (accountNum != null) {
            List<Transaction> transactions = controller.getTransactionHistory(accountNum);
            transactionData.setAll(transactions);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void logout() {
        controller.getBankController().saveData();
        LoginView loginView = new LoginView(
                new LoginController(controller.getBankController()),
                primaryStage
        );
        primaryStage.getScene().setRoot(loginView.getView());
    }

    public BorderPane getView() {
        return view;
    }
}