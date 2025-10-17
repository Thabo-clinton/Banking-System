import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class ClerkView {
    private ClerkController controller;
    private String clerkName;
    private Stage primaryStage;
    private BorderPane view;
    private TableView<Customer> customersTable;
    private ObservableList<Customer> customerData;

    public ClerkView(ClerkController controller, String clerkName, Stage primaryStage) {
        this.controller = controller;
        this.clerkName = clerkName;
        this.primaryStage = primaryStage;
        createView();
        refreshCustomersTable();
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

        // Customer Management Tab
        Tab customersTab = new Tab("Customer Management");
        customersTab.setContent(createCustomersTab());
        customersTab.setClosable(false);

        // Account Management Tab
        Tab accountsTab = new Tab("Account Management");
        accountsTab.setContent(createAccountsTab());
        accountsTab.setClosable(false);

        // System Operations Tab
        Tab systemTab = new Tab("System Operations");
        systemTab.setContent(createSystemTab());
        systemTab.setClosable(false);

        tabPane.getTabs().addAll(customersTab, accountsTab, systemTab);
        view.setCenter(tabPane);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Welcome, " + clerkName);
        welcomeLabel.getStyleClass().add("header-title");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, logoutButton);

        return header;
    }

    private VBox createCustomersTab() {
        VBox tabContent = new VBox(10);
        tabContent.setPadding(new Insets(15));

        // Buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        Button addIndividualBtn = new Button("Add Individual Customer");
        addIndividualBtn.getStyleClass().add("success-button");
        addIndividualBtn.setOnAction(e -> showAddIndividualDialog());

        Button addCompanyBtn = new Button("Add Company Customer");
        addCompanyBtn.getStyleClass().add("info-button");
        addCompanyBtn.setOnAction(e -> showAddCompanyDialog());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("secondary-button");
        refreshBtn.setOnAction(e -> refreshCustomersTable());

        buttonsBox.getChildren().addAll(addIndividualBtn, addCompanyBtn, refreshBtn);

        // Customers Table
        customersTable = new TableView<>();
        setupCustomersTable();

        VBox.setVgrow(customersTable, Priority.ALWAYS);
        tabContent.getChildren().addAll(buttonsBox, customersTable);

        return tabContent;
    }

    private void setupCustomersTable() {
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Customer, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getClass().getSimpleName().replace("Customer", "")
                ));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().firstName != null ?
                                cellData.getValue().firstName + " " + cellData.getValue().surname :
                                cellData.getValue().surname
                ));

        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, Integer> accountsCol = new TableColumn<>("Accounts");
        accountsCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getAccounts().size()
                ).asObject());

        customersTable.getColumns().addAll(idCol, typeCol, nameCol, addressCol, accountsCol);
        customerData = FXCollections.observableArrayList();
        customersTable.setItems(customerData);
    }

    private VBox createAccountsTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(15));
        tabContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Create New Account");
        titleLabel.getStyleClass().add("section-title");

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("form-pane");
        form.setMaxWidth(500);

        // Customer ID
        Label customerIdLabel = new Label("Customer ID:");
        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Enter customer ID");
        form.add(customerIdLabel, 0, 0);
        form.add(customerIdField, 1, 0);

        // Account Type
        Label typeLabel = new Label("Account Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("savings", "investment", "cheque");
        typeCombo.setValue("savings");
        form.add(typeLabel, 0, 1);
        form.add(typeCombo, 1, 1);

        // Initial Deposit
        Label depositLabel = new Label("Initial Deposit:");
        TextField depositField = new TextField();
        depositField.setPromptText("0.00");
        form.add(depositLabel, 0, 2);
        form.add(depositField, 1, 2);

        // Branch
        Label branchLabel = new Label("Branch:");
        TextField branchField = new TextField();
        branchField.setPromptText("Enter branch name");
        form.add(branchLabel, 0, 3);
        form.add(branchField, 1, 3);

        // Cheque Account Fields (initially hidden)
        VBox chequeFields = new VBox(10);
        chequeFields.setVisible(false);

        TextField employerField = new TextField();
        employerField.setPromptText("Employer name");

        TextField companyAddressField = new TextField();
        companyAddressField.setPromptText("Company address");

        chequeFields.getChildren().addAll(
                new Label("Employer:"), employerField,
                new Label("Company Address:"), companyAddressField
        );

        form.add(chequeFields, 0, 4, 2, 1);

        // Show/hide cheque fields based on account type
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCheque = "cheque".equals(newVal);
            chequeFields.setVisible(isCheque);
        });

        // Create Account Button
        Button createAccountBtn = new Button("Create Account");
        createAccountBtn.getStyleClass().add("primary-button");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);

        createAccountBtn.setOnAction(e -> {
            String customerId = customerIdField.getText().trim();
            String accountType = typeCombo.getValue();
            String branch = branchField.getText().trim();

            try {
                double initialDeposit = Double.parseDouble(depositField.getText());
                String employer = employerField.getText().trim();
                String companyAddress = companyAddressField.getText().trim();

                String result = controller.createAccount(
                        customerId, accountType, initialDeposit, branch, employer, companyAddress
                );

                showAlert("Create Account", result);

                if (result.contains("successfully")) {
                    // Clear form
                    customerIdField.clear();
                    depositField.clear();
                    branchField.clear();
                    employerField.clear();
                    companyAddressField.clear();
                    refreshCustomersTable();
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid deposit amount");
            }
        });

        form.add(createAccountBtn, 0, 5, 2, 1);

        tabContent.getChildren().addAll(titleLabel, form);
        return tabContent;
    }

    private VBox createSystemTab() {
        VBox tabContent = new VBox(20);
        tabContent.setPadding(new Insets(50));
        tabContent.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("System Operations");
        titleLabel.getStyleClass().add("section-title");

        Button applyInterestBtn = new Button("Apply Monthly Interest");
        applyInterestBtn.getStyleClass().add("warning-button");
        applyInterestBtn.setStyle("-fx-font-size: 16px; -fx-padding: 15 30;");

        applyInterestBtn.setOnAction(e -> {
            String result = controller.applyMonthlyInterest();
            showAlert("Monthly Interest", result);
        });

        tabContent.getChildren().addAll(titleLabel, applyInterestBtn);
        return tabContent;
    }

    private void showAddIndividualDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Individual Customer");
        dialog.setHeaderText("Enter individual customer details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return controller.addIndividualCustomer(
                        firstNameField.getText().trim(),
                        surnameField.getText().trim(),
                        addressField.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            showAlert("Add Customer", result);
            if (result.contains("successfully")) {
                refreshCustomersTable();
            }
        });
    }

    private void showAddCompanyDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Company Customer");
        dialog.setHeaderText("Enter company customer details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField cellNumberField = new TextField();
        cellNumberField.setPromptText("Cell Number");

        grid.add(new Label("Company Name:"), 0, 0);
        grid.add(companyNameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Cell Number:"), 0, 2);
        grid.add(cellNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return controller.addCompanyCustomer(
                        companyNameField.getText().trim(),
                        addressField.getText().trim(),
                        cellNumberField.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            showAlert("Add Customer", result);
            if (result.contains("successfully")) {
                refreshCustomersTable();
            }
        });
    }

    private void refreshCustomersTable() {
        List<Customer> customers = controller.getAllCustomers();
        customerData.setAll(customers);
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