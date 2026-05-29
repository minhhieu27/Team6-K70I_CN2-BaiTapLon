import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import client.ui.SocketUiBridge;

public class MainApp extends Application {

    private Stage primaryStage;

    // --- DATABASE ---
    private Map<String, User> userDatabase = new HashMap<>();
    private final String DB_FILE = "users.dat";
    private final String REMEMBER_FILE = "remember_me.txt";

    private final String BACKGROUND_IMAGE_URL = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1920&auto=format&fit=crop";

    // --- MODEL: USER ---
    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        private String username, email, phone, passwordHash, role;
        private int failedLoginAttempts = 0;
        private boolean isLocked = false;

        public User(String username, String email, String phone, String passwordHash, String role) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.passwordHash = passwordHash;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return isLocked ? "Bị Khóa" : "Hoạt động";
        }

        public boolean checkPassword(String inputHash) {
            return this.passwordHash.equals(inputHash);
        }

        public void incrementFailedAttempts() {
            this.failedLoginAttempts++;
            if (this.failedLoginAttempts >= 3) this.isLocked = true;
        }

        public void resetFailedAttempts() {
            this.failedLoginAttempts = 0;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public void setLocked(boolean locked) {
            this.isLocked = locked;
            if (!locked) resetFailedAttempts();
        }
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        SocketUiBridge.connect();

        loadDatabase();
        showLoginScene();
        stage.setTitle("Auction Pro - Đỉnh cao Đấu giá");
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private void loadDatabase() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB_FILE))) {
            userDatabase = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            userDatabase = new HashMap<>();
            userDatabase.put("admin", new User("admin", "admin@auction.com", "0000000000", hashPassword("Admin@123"), "ADMIN"));
            saveDatabase();
        }
    }

    private void saveDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(userDatabase);
        } catch (Exception ignored) {
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setMacBookBackground(Pane root) {
        root.setStyle("-fx-background-image: url('" + BACKGROUND_IMAGE_URL + "'); -fx-background-size: cover; -fx-background-position: center center;");
    }

    // ==============================================================================
    // 1 & 2. ĐĂNG KÝ & ĐĂNG NHẬP
    // ==============================================================================
    private void showRegisterScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30, 40, 30, 40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(420);
        formBox.setMaxHeight(Region.USE_PREF_SIZE);
        formBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        Label title = new Label("ĐĂNG KÝ TÀI KHOẢN");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #cba6f7; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        TextField txtUser = createField("Tên đăng nhập (viết liền, không dấu)");
        Label errUser = createErrorLabel();
        TextField txtEmail = createField("Email liên hệ");
        Label errEmail = createErrorLabel();
        TextField txtPhone = createField("Số điện thoại");
        Label errPhone = createErrorLabel();
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu");
        styleInputField(txtPass);
        Label errPass = createErrorLabel();
        PasswordField txtRePass = new PasswordField();
        txtRePass.setPromptText("Xác nhận lại mật khẩu");
        styleInputField(txtRePass);
        Label errRePass = createErrorLabel();

        Button btnReg = new Button("ĐĂNG KÝ");
        btnReg.setMaxWidth(Double.MAX_VALUE);
        btnReg.setStyle("-fx-background-color: linear-gradient(to right, #f38ba8, #cba6f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");

        btnReg.setOnAction(e -> {
            boolean isValid = true;
            errUser.setText("");
            errEmail.setText("");
            errPhone.setText("");
            errPass.setText("");
            errRePass.setText("");

            String username = txtUser.getText().trim();
            if (username.isEmpty() || username.contains(" ")) {
                errUser.setText("* Tên đăng nhập không hợp lệ");
                isValid = false;
            } else if (userDatabase.containsKey(username)) {
                errUser.setText("* Tên đăng nhập đã tồn tại");
                isValid = false;
            }

            if (!txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                errEmail.setText("* Email sai định dạng");
                isValid = false;
            }
            if (!txtPhone.getText().matches("^\\d{10}$")) {
                errPhone.setText("* Yêu cầu đúng 10 chữ số");
                isValid = false;
            }
            if (txtPass.getText().isEmpty()) {
                errPass.setText("* Mật khẩu trống");
                isValid = false;
            }
            if (!txtRePass.getText().equals(txtPass.getText()) || txtRePass.getText().isEmpty()) {
                errRePass.setText("* Không khớp");
                isValid = false;
            }

            if (isValid) {
                userDatabase.put(username, new User(username, txtEmail.getText(), txtPhone.getText(), hashPassword(txtPass.getText()), "USER"));
                saveDatabase();
                showAlert(Alert.AlertType.INFORMATION, "Hoàn tất", "Tài khoản của bạn đã sẵn sàng!");
                showLoginScene();
            }
        });

        Hyperlink linkLogin = new Hyperlink("Đã có tài khoản? Đăng nhập tại đây");
        linkLogin.setStyle("-fx-text-fill: #89b4fa; -fx-underline: false;");
        linkLogin.setOnAction(e -> showLoginScene());

        formBox.getChildren().addAll(title, new VBox(2, txtUser, errUser), new VBox(2, txtEmail, errEmail), new VBox(2, txtPhone, errPhone), new VBox(2, txtPass, errPass), new VBox(2, txtRePass, errRePass), btnReg, linkLogin);
        root.getChildren().add(formBox);
        primaryStage.setScene(new Scene(root, 1000, 700));
    }

    private void showLoginScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(50, 40, 50, 40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setMaxHeight(Region.USE_PREF_SIZE);
        formBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        Label title = new Label("AUCTION PRO");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #f38ba8; -fx-font-weight: bold;");

        TextField txtUser = createField("Tên đăng nhập");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu");
        styleInputField(txtPass);

        CheckBox chkRemember = new CheckBox("Nhớ mật khẩu");
        chkRemember.setStyle("-fx-text-fill: #bac2de;");
        loadRememberedUser(txtUser, txtPass, chkRemember);

        Button btnLogin = new Button("ĐĂNG NHẬP");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #a6e3a1, #89b4fa); -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");

        btnLogin.setOnAction(e -> {
            String username = txtUser.getText().trim();
            String passInput = txtPass.getText();

            if (!userDatabase.containsKey(username)) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tài khoản không tồn tại!");
                return;
            }
            User user = userDatabase.get(username);
            if (user.isLocked()) {
                showAlert(Alert.AlertType.ERROR, "Khóa", "Tài khoản bị khóa. Liên hệ Admin!");
                return;
            }

            if (user.checkPassword(hashPassword(passInput))) {
                user.resetFailedAttempts();
                saveDatabase();
                handleRememberMe(username, passInput, chkRemember.isSelected());
                if (user.getRole().equals("ADMIN")) showAdminDashboard();
                else showUserDashboard(user);
            } else {
                user.incrementFailedAttempts();
                saveDatabase();
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Sai mật khẩu! Sai 3 lần sẽ khóa tài khoản.");
            }
        });

        Hyperlink linkReg = new Hyperlink("Tạo tài khoản mới");
        linkReg.setStyle("-fx-text-fill: #89b4fa;");
        linkReg.setOnAction(e -> showRegisterScene());

        formBox.getChildren().addAll(title, txtUser, txtPass, chkRemember, btnLogin, linkReg);
        root.getChildren().add(formBox);
        primaryStage.setScene(new Scene(root, 1000, 700));
    }

    private void handleRememberMe(String user, String pass, boolean isRemembered) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REMEMBER_FILE))) {
            if (isRemembered) {
                writer.println(user);
                writer.println(pass);
            } else {
                writer.print("");
            }
        } catch (Exception ignored) {
        }
    }

    private void loadRememberedUser(TextField txtUser, PasswordField txtPass, CheckBox chk) {
        try (BufferedReader reader = new BufferedReader(new FileReader(REMEMBER_FILE))) {
            String u = reader.readLine();
            String p = reader.readLine();
            if (u != null && p != null && !u.isEmpty()) {
                txtUser.setText(u);
                txtPass.setText(p);
                chk.setSelected(true);
            }
        } catch (Exception ignored) {
        }
    }

    // ==============================================================================
    // 3A. ADMIN DASHBOARD
    // ==============================================================================
    private void showAdminDashboard() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1e1e2e;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("⚙️ BẢNG ĐIỀU KHIỂN ADMIN");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #f38ba8; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("Đăng xuất");
        btnLogout.setOnAction(e -> showLoginScene());
        header.getChildren().addAll(title, spacer, btnLogout);

        TableView<User> table = new TableView<>();
        ObservableList<User> userList = FXCollections.observableArrayList(userDatabase.values());

        TableColumn<User, String> userCol = new TableColumn<>("Tài khoản");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        TableColumn<User, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        table.getColumns().addAll(userCol, emailCol, statusCol);
        table.setItems(userList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: #181825; -fx-text-background-color: #cdd6f4;");

        HBox actionBox = new HBox(15);
        Button btnLock = new Button("Khóa tài khoản");
        btnLock.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnUnlock = new Button("Mở khóa tài khoản");
        btnUnlock.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: black; -fx-font-weight: bold;");

        btnLock.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.getRole().equals("ADMIN")) {
                selected.setLocked(true);
                saveDatabase();
                table.refresh();
            }
        });
        btnUnlock.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setLocked(false);
                saveDatabase();
                table.refresh();
            }
        });

        actionBox.getChildren().addAll(btnLock, btnUnlock);
        layout.getChildren().addAll(header, table, actionBox);
        primaryStage.setScene(new Scene(layout, 1000, 700));
    }

    private void showAuctionRoomScene(String auctionId, String userId) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #11111b;");

        Label title = new Label("⚡ Phòng Đấu Giá Live");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblAuctionId = new Label("Mã phiên đấu giá: " + auctionId);
        lblAuctionId.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Nhập giá muốn đặt");

        Button btnJoin = new Button("Tham gia phòng");
        btnJoin.setOnAction(e -> {
            SocketUiBridge.joinAuction(auctionId);
            showAlert(Alert.AlertType.INFORMATION, "Socket", "Đã tham gia phòng đấu giá " + auctionId);
        });

        Button btnBid = new Button("Đặt giá");
        btnBid.setOnAction(e -> {
            try {
                double price = Double.parseDouble(txtPrice.getText());

                SocketUiBridge.placeBid(auctionId, userId, price);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Socket",
                        "Đã gửi yêu cầu đặt giá: " + price
                );
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá phải là số!");
            }
        });

        root.getChildren().addAll(
                title,
                lblAuctionId,
                txtPrice,
                btnJoin,
                btnBid
        );

        primaryStage.setScene(new Scene(root, 1000, 700));
    }

    // ==============================================================================
    // 3B. USER DASHBOARD (SẠCH SẼ, CHUYÊN NGHIỆP)
    // ==============================================================================
    private void showUserDashboard(User user) {
        // --- A. SIDEBAR ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: rgba(17, 17, 27, 0.9); -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        Label logo = new Label("🔥 AUCTION PRO");
        logo.setStyle("-fx-font-size: 24px; -fx-text-fill: #f38ba8; -fx-font-weight: bold;");
        VBox.setMargin(logo, new Insets(0, 0, 20, 0));

        // Nút điều hướng chuẩn chỉ, không còn chữ "Người"
        Button btnProfile = createNavButton("👤 Hồ sơ cá nhân", true);
        Button btnCatalog = createNavButton("📦 Chợ Đấu Giá", false);
        Button btnLiveRoom = createNavButton("⚡ Phòng Đấu Giá Live", false);
        Button btnWallet = createNavButton("💳 Ví & Thanh toán", false);


        btnCatalog.setOnAction(e -> {
            SocketUiBridge.loadAuctions();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Socket",
                    "Đã gửi yêu cầu lấy danh sách đấu giá lên server."
            );
        });

        btnLiveRoom.setOnAction(e -> {
            showAuctionRoomScene("A001", "U002");
        });
        btnWallet.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Sắp ra mắt", "Hệ thống Thanh toán & Ví điện tử đang được bảo trì. Vui lòng quay lại sau!"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("🚪 Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #f38ba8; -fx-border-color: #f38ba8; -fx-border-radius: 8; -fx-cursor: hand; -fx-padding: 10;");
        btnLogout.setOnAction(e -> showLoginScene());

        sidebar.getChildren().addAll(logo, btnProfile, btnCatalog, btnLiveRoom, btnWallet, spacer, btnLogout);

        // --- B. MAIN CONTENT ---
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(40));
        mainContent.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        Label welcome = new Label("Xin chào, " + user.getUsername() + " 👋");
        welcome.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");

        GridPane infoCard = new GridPane();
        infoCard.setHgap(20);
        infoCard.setVgap(15);
        infoCard.setPadding(new Insets(30));
        infoCard.setStyle("-fx-background-color: rgba(49, 50, 68, 0.85); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);");

        infoCard.add(createDetailLabel("Tên đăng nhập:"), 0, 0);
        infoCard.add(createValueLabel(user.getUsername()), 1, 0);
        infoCard.add(createDetailLabel("Email liên hệ:"), 0, 1);
        infoCard.add(createValueLabel(user.getEmail()), 1, 1);
        infoCard.add(createDetailLabel("Số điện thoại:"), 0, 2);
        infoCard.add(createValueLabel(user.phone), 1, 2);

        infoCard.add(createDetailLabel("Trạng thái:"), 0, 3);
        Label lblStatus = new Label("ĐANG HOẠT ĐỘNG");
        lblStatus.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold; -fx-background-color: rgba(166, 227, 161, 0.2); -fx-padding: 5 10; -fx-background-radius: 5;");
        infoCard.add(lblStatus, 1, 3);

        // Widget đổi tên cực mượt
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createMiniStatCard("Số dư ví", "0 VNĐ", "#f9e2af"),
                createMiniStatCard("Đang tham gia", "0 phiên", "#89b4fa"),
                createMiniStatCard("Vật phẩm đã thắng", "0", "#a6e3a1")
        );

        mainContent.getChildren().addAll(welcome, infoCard, new Label(""), statsRow);

        // --- C. RÁP LẠI VỚI NHAU ---
        HBox hBoxRoot = new HBox(sidebar, mainContent);

        StackPane rootPane = new StackPane();
        setMacBookBackground(rootPane);
        rootPane.getChildren().add(hBoxRoot);

        primaryStage.setScene(new Scene(rootPane, 1000, 700));
    }

    // --- HELPER METHODS UI ---
    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        if (isActive) {
            btn.setStyle("-fx-background-color: rgba(137, 180, 250, 0.2); -fx-text-fill: #89b4fa; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a6adc8; -fx-padding: 12; -fx-cursor: hand;");
        }
        return btn;
    }

    private Label createDetailLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #bac2de; -fx-font-size: 14px;");
        return l;
    }

    private Label createValueLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        return l;
    }

    private VBox createMiniStatCard(String title, String value, String colorHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 13px;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 22px; -fx-font-weight: bold;");

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private TextField createField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        styleInputField(f);
        return f;
    }

    private void styleInputField(TextField field) {
        field.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: transparent;");
    }

    private Label createErrorLabel() {
        Label l = new Label();
        l.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 11px; -fx-font-style: italic;");
        return l;
    }

    public static void main(String[] args) {
        launch(args);
    }

}