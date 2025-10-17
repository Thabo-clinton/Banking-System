import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private LoginController loginController;
    private Stage primaryStage;
    private BorderPane view;

    public LoginView(LoginController loginController, Stage primaryStage) {
        this.loginController = loginController;
        this.primaryStage = primaryStage;
        createView();
    }

    private void createView() {
        view = new BorderPane();
        view.getStyleClass().add("login-container");

        // Modern gradient background
        Rectangle background = new Rectangle();
        background.widthProperty().bind(view.widthProperty());
        background.heightProperty().bind(view.heightProperty());
        background.setFill(createModernGradient());

        StackPane backgroundPane = new StackPane(background);
        view.setCenter(backgroundPane);

        // Glass morphism card
        VBox loginCard = createLoginCard();
        backgroundPane.getChildren().add(loginCard);
    }

    private LinearGradient createModernGradient() {
        return new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(1, Color.web("#764ba2"))
        );
    }

    private VBox createLoginCard() {
        VBox card = new VBox(30);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50, 40, 50, 40));
        card.setMaxWidth(450);
        card.setMaxHeight(600);

        // App logo/header
        VBox headerSection = createHeaderSection();

        // Login options
        VBox loginSection = createLoginSection();

        card.getChildren().addAll(headerSection, loginSection);
        return card;
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        // App icon (using a styled label as placeholder)
        Label appIcon = new Label("🏦");
        appIcon.setStyle("-fx-font-size: 48px; -fx-padding: 20px;");

        Label appName = new Label("ACME BANK");
        appName.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 28));
        appName.setStyle("-fx-text-fill: white;");

        Label appTagline = new Label("Secure Banking Portal");
        appTagline.setFont(Font.font("System", FontWeight.NORMAL, 14));
        appTagline.setStyle("-fx-text-fill: rgba(255,255,255,0.8);");

        headerSection.getChildren().addAll(appIcon, appName, appTagline);
        return headerSection;
    }

    private VBox createLoginSection() {
        VBox loginSection = new VBox(25);
        loginSection.setAlignment(Pos.CENTER);
        loginSection.setPadding(new Insets(30, 20, 20, 20));
        loginSection.getStyleClass().add("login-section");

        // Clerk Login Card
        VBox clerkCard = createLoginOptionCard(
                "👨‍💼 Clerk Login",
                "Bank staff access",
                this::createClerkLoginForm
        );

        // Customer Login Card
        VBox customerCard = createLoginOptionCard(
                "👤 Customer Login",
                "Account holder access",
                this::createCustomerLoginForm
        );

        // Horizontal layout for login options
        HBox loginOptions = new HBox(20);
        loginOptions.setAlignment(Pos.CENTER);
        loginOptions.getChildren().addAll(clerkCard, customerCard);

        loginSection.getChildren().add(loginOptions);
        return loginSection;
    }

    private VBox createLoginOptionCard(String title, String subtitle,
                                       java.util.function.Supplier<VBox> formCreator) {
        VBox card = new VBox(15);
        card.getStyleClass().add("login-option-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 20, 25, 20));
        card.setPrefWidth(180);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Subtitle
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setAlignment(Pos.CENTER);

        // Login button
        Button loginBtn = new Button("Access →");
        loginBtn.getStyleClass().add("modern-login-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Form container (initially hidden)
        VBox formContainer = new VBox();
        formContainer.setVisible(false);
        formContainer.setManaged(false);

        loginBtn.setOnAction(e -> {
            if (!formContainer.isVisible()) {
                formContainer.getChildren().setAll(formCreator.get());
                formContainer.setVisible(true);
                formContainer.setManaged(true);
                loginBtn.setText("← Back");
            } else {
                formContainer.setVisible(false);
                formContainer.setManaged(false);
                loginBtn.setText("Access →");
            }
        });

        card.getChildren().addAll(titleLabel, subtitleLabel, loginBtn, formContainer);
        return card;
    }

    private VBox createClerkLoginForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = createModernTextField("Username");
        PasswordField passwordField = createModernPasswordField("Password");

        Button submitBtn = createModernButton("Sign In as Clerk", "primary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message");
        errorLabel.setWrapText(true);

        submitBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText();

            if (loginController.validateClerk(name, password)) {
                ClerkView clerkView = new ClerkView(
                        new ClerkController(loginController.getBankController()),
                        name,
                        primaryStage
                );
                primaryStage.getScene().setRoot(clerkView.getView());
            } else {
                errorLabel.setText("Invalid credentials. Please try again.");
                shakeAnimation(nameField);
                shakeAnimation(passwordField);
            }
        });

        form.getChildren().addAll(
                new Label("Clerk Access"),
                nameField,
                passwordField,
                submitBtn,
                errorLabel
        );
        return form;
    }

    private VBox createCustomerLoginForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField customerIdField = createModernTextField("Customer ID");

        Button submitBtn = createModernButton("Sign In as Customer", "secondary");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message");
        errorLabel.setWrapText(true);

        submitBtn.setOnAction(e -> {
            String customerId = customerIdField.getText().trim();
            Customer customer = loginController.validateCustomer(customerId);

            if (customer != null) {
                CustomerView customerView = new CustomerView(
                        new CustomerController(customer, loginController.getBankController()),
                        primaryStage
                );
                primaryStage.getScene().setRoot(customerView.getView());
            } else {
                errorLabel.setText("Customer ID not found. Please check and try again.");
                shakeAnimation(customerIdField);
            }
        });

        form.getChildren().addAll(
                new Label("Customer Access"),
                customerIdField,
                submitBtn,
                errorLabel
        );
        return form;
    }

    private TextField createModernTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("modern-text-field");
        field.setPrefHeight(45);
        return field;
    }

    private PasswordField createModernPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("modern-text-field");
        field.setPrefHeight(45);
        return field;
    }

    private Button createModernButton(String text, String type) {
        Button button = new Button(text);
        button.getStyleClass().add("modern-button-" + type);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        return button;
    }

    private void shakeAnimation(Control control) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(0),
                        new javafx.animation.KeyValue(control.translateXProperty(), 0)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                        new javafx.animation.KeyValue(control.translateXProperty(), 10)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(200),
                        new javafx.animation.KeyValue(control.translateXProperty(), -10)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                        new javafx.animation.KeyValue(control.translateXProperty(), 10)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(400),
                        new javafx.animation.KeyValue(control.translateXProperty(), 0))
        );
        timeline.play();
    }

    public BorderPane getView() {
        return view;
    }
}