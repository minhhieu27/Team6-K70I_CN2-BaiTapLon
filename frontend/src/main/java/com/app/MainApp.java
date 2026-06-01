package com.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainApp extends Application {

    private Stage primaryStage;
    private StackPane contentArea;
    private final String REMEMBER_FILE = "remember_me.txt";
    private final String BACKGROUND_IMAGE_URL = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1920&auto=format&fit=crop";

    // Session Data
    private String userToken = "";
    private String currentUsername = "";
    private String currentRole = "";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showLoginScene();
        stage.setTitle("Auction Pro - Đỉnh cao Đấu giá");
        stage.show();
    }

    private void setMacBookBackground(Pane root) {
        root.setStyle("-fx-background-image: url('" + BACKGROUND_IMAGE_URL + "'); -fx-background-size: cover; -fx-background-position: center center;");
    }

    // ==============================================================================
    // 1. ĐĂNG KÝ
    // ==============================================================================
    private void showRegisterScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30, 40, 30, 40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(420);
        formBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        Label title = new Label("ĐĂNG KÝ TÀI KHOẢN");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #cba6f7; -fx-font-weight: bold;");

        TextField txtUser = createField("Tên đăng nhập (viết liền, không dấu)");
        TextField txtEmail = createField("Email liên hệ");
        TextField txtPhone = createField("Số điện thoại (Bắt đầu bằng số 0)");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu"); styleInputField(txtPass);
        PasswordField txtRePass = new PasswordField();
        txtRePass.setPromptText("Xác nhận mật khẩu"); styleInputField(txtRePass);

        Button btnReg = new Button("ĐĂNG KÝ");
        btnReg.setMaxWidth(Double.MAX_VALUE);
        btnReg.setStyle("-fx-background-color: linear-gradient(to right, #f38ba8, #cba6f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");

        btnReg.setOnAction(e -> {
            if(txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đủ thông tin!"); return;
            }
            if(!txtPass.getText().equals(txtRePass.getText())) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không khớp!"); return;
            }

            AuthService authService = new AuthService();
            btnReg.setText("ĐANG XỬ LÝ..."); btnReg.setDisable(true);
            authService.register(txtUser.getText().trim(), txtEmail.getText(), txtPhone.getText(), txtPass.getText())
                .thenAccept(res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công!");
                        showLoginScene();
                    } else {
                        btnReg.setText("ĐĂNG KÝ"); btnReg.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin đã tồn tại hoặc sai định dạng!");
                    }
                }));
        });

        Hyperlink linkLogin = new Hyperlink("Đã có tài khoản? Quay lại đăng nhập");
        linkLogin.setStyle("-fx-text-fill: #89b4fa;");
        linkLogin.setOnAction(e -> showLoginScene());

        formBox.getChildren().addAll(title, txtUser, txtEmail, txtPhone, txtPass, txtRePass, btnReg, linkLogin);
        root.getChildren().add(formBox);
        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    // ==============================================================================
    // 2. ĐĂNG NHẬP (ĐÃ FIX MẤT NÚT NHỚ MẬT KHẨU)
    // ==============================================================================
    private void showLoginScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(50, 40, 50, 40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        Label title = new Label("AUCTION PRO");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #f38ba8; -fx-font-weight: bold;");

        TextField txtUser = createField("Tên đăng nhập / Email / SĐT");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu");
        styleInputField(txtPass);

        // NÚT NHỚ MẬT KHẨU ĐÃ TRỞ LẠI
        CheckBox chkRemember = new CheckBox("Nhớ mật khẩu");
        chkRemember.setStyle("-fx-text-fill: #bac2de;");
        loadRememberedUser(txtUser, txtPass, chkRemember);

        Button btnLogin = new Button("ĐĂNG NHẬP");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #a6e3a1, #89b4fa); -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");

        btnLogin.setOnAction(e -> {
            String username = txtUser.getText().trim();
            String passInput = txtPass.getText();

            if (username.isEmpty() || passInput.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            AuthService authService = new AuthService();
            btnLogin.setText("ĐANG XỬ LÝ..."); btnLogin.setDisable(true);

            authService.login(username, passInput).thenAccept(res -> Platform.runLater(() -> {
                btnLogin.setText("ĐĂNG NHẬP"); btnLogin.setDisable(false);
                if (res.statusCode() == 200) {
                    JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                    userToken = json.get("token").getAsString();
                    currentUsername = json.get("username").getAsString();
                    
                    currentRole = "ROLE_USER";
                    if (json.has("roles") && !json.get("roles").isJsonNull() && json.getAsJsonArray("roles").size() > 0) {
                        currentRole = json.getAsJsonArray("roles").get(0).getAsString();
                    }

                    handleRememberMe(username, passInput, chkRemember.isSelected());

                    if (currentRole.equals("ROLE_ADMIN")) {
                        showAlert(Alert.AlertType.INFORMATION, "Admin", "Giao diện Admin đang được phát triển ở bước sau!");
                    } else {
                        showUserDashboard();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tài khoản hoặc mật khẩu!");
                }
            })).exceptionally(ex -> {
                Platform.runLater(() -> { btnLogin.setText("ĐĂNG NHẬP"); btnLogin.setDisable(false); showAlert(Alert.AlertType.ERROR, "Lỗi Server", "Mất kết nối máy chủ Spring Boot!"); });
                return null;
            });
        });

        // NÚT ĐĂNG KÝ ĐÃ TRỞ LẠI
        Hyperlink linkReg = new Hyperlink("Chưa có tài khoản? Tạo ngay");
        linkReg.setStyle("-fx-text-fill: #89b4fa;");
        linkReg.setOnAction(e -> showRegisterScene());

        formBox.getChildren().addAll(title, txtUser, txtPass, chkRemember, btnLogin, linkReg);
        root.getChildren().add(formBox);
        primaryStage.setScene(new Scene(root, 1100, 750));
    }

    // ==============================================================================
    // 3. DASHBOARD BỐ CỤC CHUYÊN NGHIỆP
    // ==============================================================================
    private void showUserDashboard() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: rgba(17, 17, 27, 0.9); -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        // Nhấn vào Logo để về Trang chủ (Sàn Đấu Giá)
        Label logo = new Label("🔥 AUCTION PRO");
        logo.setStyle("-fx-font-size: 24px; -fx-text-fill: #f38ba8; -fx-font-weight: bold; -fx-cursor: hand;");
        logo.setOnMouseClicked(e -> contentArea.getChildren().setAll(getCatalogView()));
        VBox.setMargin(logo, new Insets(0, 0, 20, 0));

        Button btnCatalog = createNavButton("🏠 Sàn Đấu Giá (Home)", true);
        Button btnProfile = createNavButton("👤 Hồ sơ cá nhân", false);
        Button btnWallet = createNavButton("💳 Ví & Thanh toán", false);
        Button btnPostItem = createNavButton("📤 Đăng bán sản phẩm", false);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("🚪 Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #f38ba8; -fx-border-color: #f38ba8; -fx-border-radius: 8; -fx-cursor: hand; -fx-padding: 10;");
        btnLogout.setOnAction(e -> { userToken = ""; showLoginScene(); });

        sidebar.getChildren().addAll(logo, btnCatalog, btnProfile, btnWallet, btnPostItem, spacer, btnLogout);

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        
        // Mặc định hiện Sàn Đấu Giá
        contentArea.getChildren().setAll(getCatalogView());

        btnCatalog.setOnAction(e -> contentArea.getChildren().setAll(getCatalogView()));
        btnProfile.setOnAction(e -> contentArea.getChildren().setAll(getProfileView()));
        btnWallet.setOnAction(e -> contentArea.getChildren().setAll(getWalletView()));
        btnPostItem.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Seller", "Tính năng Form đăng sản phẩm ở bước sau!"));

        HBox hBoxRoot = new HBox(sidebar, contentArea);
        StackPane rootPane = new StackPane();
        setMacBookBackground(rootPane);
        rootPane.getChildren().add(hBoxRoot);

        primaryStage.setScene(new Scene(rootPane, 1200, 800));
    }

    // ==============================================================================
    // 4. VÍ TIỀN SIÊU CHUYÊN NGHIỆP (GIẢI QUYẾT VẤN ĐỀ 4)
    // ==============================================================================
    private VBox getWalletView() {
        VBox layout = new VBox(20);
        Label title = new Label("QUẢN LÝ VÍ & THANH TOÁN");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblBalance = new Label("Đang tải...");
        lblBalance.setStyle("-fx-font-size: 40px; -fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        VBox balanceCard = new VBox(10, new Label("Số dư khả dụng:") {{ setStyle("-fx-text-fill: #bac2de; -fx-font-size: 16px;"); }}, lblBalance);
        balanceCard.setPadding(new Insets(30));
        balanceCard.setStyle("-fx-background-color: rgba(49, 50, 68, 0.9); -fx-background-radius: 15;");

        // KHU VỰC NẠP TIỀN CHUYÊN NGHIỆP
        Label lblDepositTitle = new Label("Nạp tiền vào ví");
        lblDepositTitle.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox methods = new HBox(15);
        ToggleButton btnMomo = new ToggleButton("Momo"); styleMethodBtn(btnMomo);
        ToggleButton btnVNPay = new ToggleButton("VNPay"); styleMethodBtn(btnVNPay);
        ToggleButton btnBank = new ToggleButton("Bank Transfer"); styleMethodBtn(btnBank);
        ToggleGroup group = new ToggleGroup();
        btnMomo.setToggleGroup(group); btnVNPay.setToggleGroup(group); btnBank.setToggleGroup(group); btnBank.setSelected(true);
        methods.getChildren().addAll(btnBank, btnVNPay, btnMomo);

        TextField txtAmount = createField("Nhập số tiền muốn nạp (Ví dụ: 500000)");
        txtAmount.setMaxWidth(300);

        Button btnSubmitDeposit = new Button("Xác nhận nạp tiền");
        btnSubmitDeposit.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        VBox depositArea = new VBox(15, lblDepositTitle, new Label("Chọn phương thức:"){{setStyle("-fx-text-fill:#bac2de;");}}, methods, txtAmount, btnSubmitDeposit);
        depositArea.setPadding(new Insets(30));
        depositArea.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15;");

        // Logic Gọi API lấy Ví
        fetchWalletBalance(lblBalance);

        // Logic Gửi Nạp Tiền
        btnSubmitDeposit.setOnAction(e -> {
            try {
                long amount = Long.parseLong(txtAmount.getText().trim());
                if(amount <= 0) throw new Exception();

                btnSubmitDeposit.setText("Đang xử lý..."); btnSubmitDeposit.setDisable(true);
                String json = String.format("{\"amount\": %s}", amount);
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet/deposit"))
                        .header("Authorization", "Bearer " + userToken).header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)).build();

                httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
                    btnSubmitDeposit.setText("Xác nhận nạp tiền"); btnSubmitDeposit.setDisable(false);
                    if (res.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Giao dịch qua " + ((ToggleButton)group.getSelectedToggle()).getText() + " thành công!");
                        txtAmount.clear();
                        fetchWalletBalance(lblBalance);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Giao dịch thất bại!");
                    }
                }));
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ!");
            }
        });

        layout.getChildren().addAll(title, balanceCard, depositArea);
        return layout;
    }

    private void styleMethodBtn(ToggleButton btn) {
        btn.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) btn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-font-weight: bold;");
            else btn.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8;");
        });
    }

    private void fetchWalletBalance(Label lblBalance) {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet"))
                .header("Authorization", "Bearer " + userToken).GET().build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
            if (res.statusCode() == 200) {
                JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                lblBalance.setText(String.format("%,d VNĐ", Long.parseLong(json.get("balance").getAsString().split("\\.")[0])));
            }
        }));
    }

    // ==============================================================================
    // 5. TRANG CHỦ / SÀN ĐẤU GIÁ (REAL API)
    // ==============================================================================
    private VBox getCatalogView() {
        VBox layout = new VBox(20);
        Label title = new Label("🔥 SÀN ĐẤU GIÁ NỔI BẬT");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        FlowPane grid = new FlowPane(20, 20); 
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        layout.getChildren().addAll(title, scroll);

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/auctions?page=0&size=50"))
                .header("Authorization", "Bearer " + userToken).GET().build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
            if (res.statusCode() == 200) {
                JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                if (json.has("content")) {
                    JsonArray content = json.getAsJsonArray("content");
                    if (content.size() == 0) {
                        grid.getChildren().add(new Label("Hiện tại chưa có sản phẩm nào trên sàn. Hãy là người đầu tiên đăng bán!") {{ setStyle("-fx-text-fill: #bac2de; -fx-font-size: 16px;"); }});
                    }
                    for (JsonElement el : content) {
                        grid.getChildren().add(createAuctionCard(el.getAsJsonObject()));
                    }
                }
            }
        }));

        return layout;
    }

    private VBox createAuctionCard(JsonObject auctionData) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(240, 280);
        card.setStyle("-fx-background-color: rgba(49, 50, 68, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        String id = auctionData.get("auctionId").getAsString();
        String title = auctionData.get("title").getAsString();
        String price = auctionData.get("currentPrice").getAsString();
        String status = auctionData.get("status").getAsString();

        Label lblTitle = new Label(title);
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblPrice = new Label(String.format("%,d VNĐ", Long.parseLong(price.split("\\.")[0])));
        lblPrice.setStyle("-fx-text-fill: #f9e2af; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblStatus = new Label(status);
        String color = status.equals("OPEN") ? "#a6e3a1" : (status.equals("FINISHED") ? "#f38ba8" : "#bac2de");
        lblStatus.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-border-color: " + color + "; -fx-border-radius: 4; -fx-padding: 3 8;");

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnJoin = new Button(status.equals("OPEN") ? "Vào Phòng Live" : "Xem chi tiết");
        btnJoin.setMaxWidth(Double.MAX_VALUE);
        btnJoin.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10;");
        
        btnJoin.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Phòng Live", "Màn hình Live Socket đang được tích hợp...");
        });

        card.getChildren().addAll(lblStatus, lblTitle, new Label("Giá cao nhất:"){{setStyle("-fx-text-fill:#bac2de;");}}, lblPrice, spacer, btnJoin);
        return card;
    }

    private VBox getProfileView() {
        VBox layout = new VBox(20);
        Label welcome = new Label("Hồ sơ cá nhân");
        welcome.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");
        layout.getChildren().add(welcome);
        return layout;
    }

    // --- CÁC HÀM TIỆN ÍCH UI ---
    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text); btn.setMaxWidth(Double.MAX_VALUE); btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a6adc8; -fx-padding: 15; -fx-font-size: 14px; -fx-cursor: hand;");
        return btn;
    }
    private void handleRememberMe(String user, String pass, boolean isRem) {
        try(PrintWriter w = new PrintWriter(new FileWriter(REMEMBER_FILE))) { if(isRem){w.println(user);w.println(pass);} else w.print(""); } catch(Exception ignored){}
    }
    private void loadRememberedUser(TextField u, PasswordField p, CheckBox c) {
        try(BufferedReader r = new BufferedReader(new FileReader(REMEMBER_FILE))) { String a=r.readLine(); String b=r.readLine(); if(a!=null&&b!=null){u.setText(a);p.setText(b);c.setSelected(true);} } catch(Exception ignored){}
    }
    private TextField createField(String p) { TextField f = new TextField(); f.setPromptText(p); styleInputField(f); return f; }
    private void styleInputField(TextField f) { f.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8;"); }
    private void showAlert(Alert.AlertType t, String title, String c) { Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(c); a.showAndWait(); }

    public static void main(String[] args) { launch(args); }
}