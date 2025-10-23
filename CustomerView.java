import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private ComboBox<String> bankingAccountCombo;

    public CustomerView(CustomerController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        createView();
        refreshData();
    }

    private void createView() {
        view = new BorderPane();
        view.getStyleClass().add("dashboard-container");

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
        HBox header = new HBox(20);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);

        // Customer info section
        VBox customerInfo = new VBox(5);
        customerInfo.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Welcome back,");
        welcomeLabel.getStyleClass().add("user-welcome");

        Label customerNameLabel = new Label(controller.getCustomerName());
        customerNameLabel.getStyleClass().add("header-title");

        customerInfo.getChildren().addAll(welcomeLabel, customerNameLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        HBox.setHgrow(customerInfo, Priority.ALWAYS);
        header.getChildren().addAll(customerInfo, logoutButton);

        return header;
    }

    private VBox createAccountsTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #f8fafc;");

        Label titleLabel = new Label("My Accounts Overview");
        titleLabel.getStyleClass().add("section-title");

        // Accounts Table
        accountsTable = new TableView<>();
        setupAccountsTable();

        VBox.setVgrow(accountsTable, Priority.ALWAYS);
        tabContent.getChildren().addAll(titleLabel, accountsTable);

        return tabContent;
    }

    @SuppressWarnings("unchecked")
    private void setupAccountsTable() {
        TableColumn<Account, String> accNumberCol = new TableColumn<>("Account Number");
        accNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accNumberCol.setPrefWidth(200);

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        getAccountTypeDisplayName(cellData.getValue())
                ));
        typeCol.setPrefWidth(120);

        TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setCellFactory(col -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%.2f", item));
                    if (item >= 0) {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    }
                }
            }
        });
        balanceCol.setPrefWidth(120);

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        branchCol.setPrefWidth(150);

        accountsTable.getColumns().addAll(accNumberCol, typeCol, balanceCol, branchCol);
        accountData = FXCollections.observableArrayList();
        accountsTable.setItems(accountData);
    }

    private String getAccountTypeDisplayName(Account account) {
        if (account instanceof SavingsAccount) return "💳 Savings";
        if (account instanceof InvestmentAccount) return "📈 Investment";
        if (account instanceof ChequeAccount) return "🏦 Cheque";
        return account.getClass().getSimpleName();
    }

    private VBox createTransactionsTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(25));
        tabContent.setStyle("-fx-background-color: #f8fafc;");

        Label titleLabel = new Label("Transaction History");
        titleLabel.getStyleClass().add("section-title");

        // Account Selection
        HBox selectionBox = new HBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        selectionBox.setPadding(new Insets(15));
        selectionBox.getStyleClass().add("form-pane");

        Label accountLabel = new Label("Select Account:");
        accountLabel.setStyle("-fx-font-weight: 600;");

        accountComboBox = new ComboBox<>();
        accountComboBox.getStyleClass().add("modern-text-field");
        accountComboBox.setPrefWidth(250);

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

    @SuppressWarnings("unchecked")
    private void setupTransactionsTable() {
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            String formatted = transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        dateCol.setPrefWidth(200);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Transaction Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Show proper transaction type names
                    String displayText;
                    switch (item.toUpperCase()) {
                        case "DEPOSIT":
                            displayText = "💰 Deposit";
                            setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                            break;
                        case "WITHDRAW":
                            displayText = "💸 Withdrawal";
                            setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                            break;
                        case "INTEREST":
                            displayText = "📈 Interest";
                            setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                            break;
                        case "BALANCE_CHECK":
                            displayText = "👁️ Balance Check";
                            setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                            break;
                        default:
                            displayText = item;
                            setStyle("-fx-text-fill: #4b5563;");
                    }
                    setText(displayText);
                }
            }
        });
        typeCol.setPrefWidth(150);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if (transaction.getType().equalsIgnoreCase("DEPOSIT") || transaction.getType().equalsIgnoreCase("INTEREST")) {
                        setText(String.format("+$%.2f", item));
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else if (transaction.getType().equalsIgnoreCase("WITHDRAW")) {
                        setText(String.format("-$%.2f", item));
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    } else {
                        setText(String.format("$%.2f", item));
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    }
                }
            }
        });
        amountCol.setPrefWidth(120);

        transactionsTable.getColumns().addAll(dateCol, typeCol, amountCol);
        transactionData = FXCollections.observableArrayList();
        transactionsTable.setItems(transactionData);
    }

    private VBox createBankingTab() {
        VBox tabContent = new VBox(25);
        tabContent.setPadding(new Insets(30));
        tabContent.setStyle("-fx-background-color: #f8fafc;");
        tabContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Banking Operations");
        titleLabel.getStyleClass().add("section-title");

        // Account Selection
        HBox accountSelectionBox = new HBox(15);
        accountSelectionBox.setAlignment(Pos.CENTER);
        accountSelectionBox.setPadding(new Insets(20));
        accountSelectionBox.getStyleClass().add("form-pane");

        Label accountLabel = new Label("Select Account:");
        accountLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");

        bankingAccountCombo = new ComboBox<>();
        bankingAccountCombo.getStyleClass().add("modern-text-field");
        bankingAccountCombo.setPrefWidth(300);

        // Savings account info label
        Label savingsInfoLabel = new Label();
        savingsInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #d97706; -fx-font-weight: bold;");
        savingsInfoLabel.setWrapText(true);
        savingsInfoLabel.setMaxWidth(300);
        savingsInfoLabel.setVisible(false);

        accountSelectionBox.getChildren().addAll(accountLabel, bankingAccountCombo);

        // In createBankingTab method, update the savings info:
        bankingAccountCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    Account account = controller.getCustomer().findAccount(newVal);
                    if (account instanceof SavingsAccount) {
                        if (account.getBalance() == 0) {
                            savingsInfoLabel.setText("💡 Savings Account - Minimum initial deposit: $1000 | No withdrawals allowed");
                        } else {
                            savingsInfoLabel.setText("💡 Savings Account - Minimum Deposit: $1000");
                        }
                        savingsInfoLabel.setVisible(true);
                        System.out.println("DEBUG: Savings account selected - Balance: " + account.getBalance());
                    } else {
                        savingsInfoLabel.setVisible(false);
                        System.out.println("DEBUG: Non-savings account selected");
                    }
                } catch (Exception e) {
                    savingsInfoLabel.setVisible(false);
                }
            } else {
                savingsInfoLabel.setVisible(false);
            }
        });
        // Operations Container
        HBox operationsContainer = new HBox(30);
        operationsContainer.setAlignment(Pos.CENTER);
        operationsContainer.setPadding(new Insets(20));

        // Deposit Box
        VBox depositBox = createDepositOperationBox();

        // Withdrawal Box
        VBox withdrawBox = createWithdrawOperationBox();

        operationsContainer.getChildren().addAll(depositBox, withdrawBox);

        // Balance Check
        VBox balanceBox = createBalanceBox();

        tabContent.getChildren().addAll(titleLabel, accountSelectionBox, savingsInfoLabel, operationsContainer, balanceBox);

        return tabContent;
    }

    private VBox createDepositOperationBox() {
        VBox operationBox = new VBox(15);
        operationBox.getStyleClass().add("operation-box");
        operationBox.setPadding(new Insets(25));
        operationBox.setAlignment(Pos.CENTER);
        operationBox.setMinWidth(280);
        operationBox.setMinHeight(200);

        Label titleLabel = new Label("💰 Deposit");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #10b981;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount...");
        amountField.getStyleClass().add("modern-text-field");
        amountField.setPrefWidth(200);

        // Savings minimum deposit message
        Label savingsDepositMessage = new Label();
        savingsDepositMessage.setStyle("-fx-font-size: 11px; -fx-text-fill: #d97706; -fx-font-weight: bold;");
        savingsDepositMessage.setWrapText(true);
        savingsDepositMessage.setMaxWidth(200);
        savingsDepositMessage.setVisible(false);

        Button operationButton = new Button("Deposit");
        operationButton.getStyleClass().add("success-button");
        operationButton.setPrefWidth(200);

        // In the createDepositOperationBox method, replace the operationButton action:
        operationButton.setOnAction(e -> {
            String accountNum = bankingAccountCombo.getValue();
            if (accountNum == null || accountNum.isEmpty()) {
                showAlert("Error", "Please select an account first!");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                System.out.println("DEBUG: UI - Deposit button clicked. Account: " + accountNum + ", Amount: " + amount);

                if (amount <= 0) {
                    showAlert("Error", "Amount must be greater than 0!");
                    return;
                }

                // Call the controller and get the result
                String result = controller.deposit(accountNum, amount);
                System.out.println("DEBUG: UI - Deposit result: " + result);

                // Check the result and show appropriate alert
                if (result.contains("Successfully")) {
                    showAlert("Deposit Success", result);
                    refreshData();
                    amountField.clear();
                } else {
                    showAlert("Deposit Failed", result);
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid amount!");
            }
        });
        // Update savings message when account changes
        bankingAccountCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    Account account = controller.getCustomer().findAccount(newVal);
                    if (account instanceof SavingsAccount && account.getBalance() == 0) {
                        savingsDepositMessage.setText("Minimum initial deposit: $1000");
                        savingsDepositMessage.setVisible(true);
                    } else {
                        savingsDepositMessage.setVisible(false);
                    }
                } catch (Exception e) {
                    savingsDepositMessage.setVisible(false);
                }
            } else {
                savingsDepositMessage.setVisible(false);
            }
        });

        operationBox.getChildren().addAll(titleLabel, amountField, savingsDepositMessage, operationButton);
        return operationBox;
    }

    private VBox createWithdrawOperationBox() {
        VBox operationBox = new VBox(15);
        operationBox.getStyleClass().add("operation-box");
        operationBox.setPadding(new Insets(25));
        operationBox.setAlignment(Pos.CENTER);
        operationBox.setMinWidth(280);
        operationBox.setMinHeight(200);

        Label titleLabel = new Label("💸 Withdraw");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount...");
        amountField.getStyleClass().add("modern-text-field");
        amountField.setPrefWidth(200);

        // Savings minimum balance message
        Label savingsWithdrawMessage = new Label();
        savingsWithdrawMessage.setStyle("-fx-font-size: 11px; -fx-text-fill: #d97706; -fx-font-weight: bold;");
        savingsWithdrawMessage.setWrapText(true);
        savingsWithdrawMessage.setMaxWidth(200);
        savingsWithdrawMessage.setVisible(false);

        Button operationButton = new Button("Withdraw");
        operationButton.getStyleClass().add("warning-button");
        operationButton.setPrefWidth(200);

        // In the createWithdrawOperationBox method, replace the operationButton action:
        operationButton.setOnAction(e -> {
            String accountNum = bankingAccountCombo.getValue();
            if (accountNum == null || accountNum.isEmpty()) {
                showAlert("Error", "Please select an account first!");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                System.out.println("DEBUG: UI - Withdraw button clicked. Account: " + accountNum + ", Amount: " + amount);

                if (amount <= 0) {
                    showAlert("Error", "Amount must be greater than 0!");
                    return;
                }

                // Call the controller and get the result
                String result = controller.withdraw(accountNum, amount);
                System.out.println("DEBUG: UI - Withdraw result: " + result);

                // Check the result and show appropriate alert
                if (result.contains("Successfully")) {
                    showAlert("Withdrawal Success", result);
                    refreshData();
                    amountField.clear();
                } else {
                    showAlert("Withdrawal Failed", result);
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid amount!");
            }
        });

        operationBox.getChildren().addAll(titleLabel, amountField, savingsWithdrawMessage, operationButton);
        return operationBox;
    }

    private VBox createBalanceBox() {
        VBox balanceBox = new VBox(15);
        balanceBox.getStyleClass().add("operation-box");
        balanceBox.setPadding(new Insets(25));
        balanceBox.setAlignment(Pos.CENTER);
        balanceBox.setMinWidth(300);

        Label titleLabel = new Label("💳 Account Balance");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");

        Label balanceLabel = new Label("Select an account to view balance");
        balanceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        balanceLabel.setWrapText(true);

        Button checkBalanceButton = new Button("Check Balance");
        checkBalanceButton.getStyleClass().add("info-button");
        checkBalanceButton.setPrefWidth(200);

        checkBalanceButton.setOnAction(e -> {
            String accountNum = bankingAccountCombo.getValue();
            if (accountNum != null && !accountNum.isEmpty()) {
                double balance = controller.getBalance(accountNum);
                if (balance >= 0) {
                    balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
                    balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #059669;");

                    // Record balance check transaction
                    recordBalanceCheckTransaction(accountNum);
                } else {
                    balanceLabel.setText("Error retrieving balance");
                    balanceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626;");
                }
            } else {
                showAlert("Error", "Please select an account first!");
            }
        });

        balanceBox.getChildren().addAll(titleLabel, balanceLabel, checkBalanceButton);
        return balanceBox;
    }

    private void recordBalanceCheckTransaction(String accountNumber) {
        // Create a balance check transaction
        Transaction balanceCheck = new Transaction("BALANCE_CHECK", 0.0);

        // Add to account's transaction history
        try {
            Account account = controller.getCustomer().findAccount(accountNumber);
            account.addTransaction(balanceCheck);
            controller.getBankController().saveData();
        } catch (Exception e) {
            // Silent fail - balance check transaction is optional
        }
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
        bankingAccountCombo.setItems(accountNumbers);

        if (!accountNumbers.isEmpty()) {
            if (accountComboBox.getValue() == null) {
                accountComboBox.setValue(accountNumbers.get(0));
            }
            if (bankingAccountCombo.getValue() == null) {
                bankingAccountCombo.setValue(accountNumbers.get(0));
            }
        }
    }

    private void viewTransactions() {
        String accountNum = accountComboBox.getValue();
        if (accountNum != null && !accountNum.isEmpty()) {
            List<Transaction> transactions = controller.getTransactionHistory(accountNum);
            transactionData.setAll(transactions);

            if (transactions.isEmpty()) {
                showAlert("Transactions", "No transactions found for this account.");
            } else {
                showAlert("Transactions", "Found " + transactions.size() + " transactions.");
            }
        } else {
            showAlert("Error", "Please select an account first!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

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