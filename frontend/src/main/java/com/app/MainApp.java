package com.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
    private StackPane contentArea;
    private final String REMEMBER_FILE = "remember_me.txt";

    // Ảnh nền duy nhất cực xịn
    private final String BG_DASHBOARD = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1920&auto=format&fit=crop";

    private String userToken = "";
    private String currentUsername = "";
    private String currentUserId = "";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private Socket liveSocket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    private List<Button> navButtons = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showLoginScene();
        stage.setTitle("Auction Pro - Đỉnh cao Đấu giá");
        stage.show();
    }

    private void setMacBookBackground(Pane root) {
        root.setStyle("-fx-background-image: url('" + BG_DASHBOARD + "'); -fx-background-size: cover; -fx-background-position: center center;");
    }

    private void closeLiveSocket() {
        try {
            if (socketOut != null) socketOut.close();
            if (socketIn != null) socketIn.close();
            if (liveSocket != null && !liveSocket.isClosed()) liveSocket.close();
        } catch (Exception ignored) {}
    }

    // ==============================================================================
    // 1. FILMORA STYLE LOGIN / REGISTER SPLIT SCREEN (ĐỒNG BỘ NỀN)
    // ==============================================================================
    private void showLoginScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        // Khối Card lớn giữa màn hình
        HBox splitCard = new HBox();
        splitCard.setMaxSize(900, 550);
        splitCard.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        // Bên Trái: Form Đăng nhập
        VBox leftSide = new VBox(20);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setPadding(new Insets(50));
        leftSide.setPrefWidth(450);
        leftSide.setStyle("-fx-background-color: rgba(30, 30, 46, 0.95); -fx-background-radius: 20 0 0 20;");

        Label title = new Label("Đăng nhập");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField txtUser = createField("Tên đăng nhập / Email / SĐT");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Mật khẩu");
        styleInputField(txtPass);

        CheckBox chkRemember = new CheckBox("Nhớ mật khẩu");
        chkRemember.setStyle("-fx-text-fill: #bac2de;");
        loadRememberedUser(txtUser, txtPass, chkRemember);

        Button btnLogin = new Button("ĐĂNG NHẬP");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #89b4fa, #cba6f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand;");

        btnLogin.setOnAction(e -> {
            if (txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Nhập đủ thông tin!"); return; }
            AuthService authService = new AuthService(); btnLogin.setText("ĐANG XỬ LÝ..."); btnLogin.setDisable(true);
            authService.login(txtUser.getText().trim(), txtPass.getText()).thenAccept(res -> Platform.runLater(() -> {
                btnLogin.setText("ĐĂNG NHẬP"); btnLogin.setDisable(false);
                if (res.statusCode() == 200) {
                    JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                    userToken = json.get("token").getAsString(); currentUsername = json.get("username").getAsString(); currentUserId = json.get("userId").getAsString();
                    handleRememberMe(txtUser.getText().trim(), txtPass.getText(), chkRemember.isSelected());
                    showUserDashboard();
                } else showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tài khoản hoặc mật khẩu!");
            })).exceptionally(ex -> { Platform.runLater(() -> { btnLogin.setText("ĐĂNG NHẬP"); btnLogin.setDisable(false); showAlert(Alert.AlertType.ERROR, "Lỗi Server", "Mất kết nối!"); }); return null; });
        });

        HBox linkBox = new HBox(5); linkBox.setAlignment(Pos.CENTER);
        Label lblNoAcc = new Label("Chưa có tài khoản?"); lblNoAcc.setStyle("-fx-text-fill: #bac2de;");
        Hyperlink linkReg = new Hyperlink("Tạo ngay"); linkReg.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");
        linkReg.setOnAction(e -> showRegisterScene());
        linkBox.getChildren().addAll(lblNoAcc, linkReg);

        leftSide.getChildren().addAll(title, txtUser, txtPass, chkRemember, btnLogin, linkBox);

        // Bên Phải: Giới thiệu đẹp mắt (Không lo chết link ảnh)
        VBox rightSide = new VBox(20);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setPrefWidth(450);
        rightSide.setStyle("-fx-background-color: linear-gradient(to bottom right, #f38ba8, #cba6f7); -fx-background-radius: 0 20 20 0;");
        
        Label logo = new Label("🔥 AUCTION PRO");
        logo.setStyle("-fx-font-size: 38px; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        
        Label rightDesc = new Label("Nền tảng đấu giá trực tuyến\nBảo mật - Nhanh chóng - Đẳng cấp");
        rightDesc.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9); -fx-text-alignment: center;");
        
        rightSide.getChildren().addAll(logo, rightDesc);
        splitCard.getChildren().addAll(leftSide, rightSide);
        root.getChildren().add(splitCard);

        primaryStage.setScene(new Scene(root, 1200, 750));
    }

    private void showRegisterScene() {
        StackPane root = new StackPane();
        setMacBookBackground(root);

        HBox splitCard = new HBox();
        splitCard.setMaxSize(900, 550);
        splitCard.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);");

        VBox leftSide = new VBox(15);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setPadding(new Insets(40));
        leftSide.setPrefWidth(450);
        leftSide.setStyle("-fx-background-color: rgba(30, 30, 46, 0.95); -fx-background-radius: 20 0 0 20;");
        
        Label title = new Label("Tạo tài khoản mới");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField txtUser = createField("Tên đăng nhập (không dấu)"); TextField txtEmail = createField("Email liên hệ"); TextField txtPhone = createField("Số điện thoại");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Mật khẩu"); styleInputField(txtPass);
        PasswordField txtRePass = new PasswordField(); txtRePass.setPromptText("Xác nhận mật khẩu"); styleInputField(txtRePass);

        Button btnReg = new Button("ĐĂNG KÝ"); btnReg.setMaxWidth(Double.MAX_VALUE);
        btnReg.setStyle("-fx-background-color: linear-gradient(to right, #a6e3a1, #89b4fa); -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand;");

        btnReg.setOnAction(e -> {
            if(txtUser.getText().isEmpty() || !txtPass.getText().equals(txtRePass.getText())) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin không hợp lệ!"); return; }
            AuthService authService = new AuthService(); btnReg.setText("ĐANG XỬ LÝ..."); btnReg.setDisable(true);
            authService.register(txtUser.getText().trim(), txtEmail.getText(), txtPhone.getText(), txtPass.getText()).thenAccept(res -> Platform.runLater(() -> {
                if (res.statusCode() == 200) { showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công!"); showLoginScene(); } 
                else { btnReg.setText("ĐĂNG KÝ"); btnReg.setDisable(false); showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã tồn tại tài khoản!"); }
            }));
        });

        HBox linkBox = new HBox(5); linkBox.setAlignment(Pos.CENTER);
        Label lblHasAcc = new Label("Đã có tài khoản?"); lblHasAcc.setStyle("-fx-text-fill: #bac2de;");
        Hyperlink linkLog = new Hyperlink("Đăng nhập"); linkLog.setStyle("-fx-text-fill: #f38ba8; -fx-font-weight: bold;");
        linkLog.setOnAction(e -> showLoginScene()); linkBox.getChildren().addAll(lblHasAcc, linkLog);

        leftSide.getChildren().addAll(title, txtUser, txtEmail, txtPhone, txtPass, txtRePass, btnReg, linkBox);
        
        VBox rightSide = new VBox(20);
        rightSide.setAlignment(Pos.CENTER); rightSide.setPrefWidth(450);
        rightSide.setStyle("-fx-background-color: linear-gradient(to bottom right, #a6e3a1, #89b4fa); -fx-background-radius: 0 20 20 0;");
        Label logo = new Label("🔥 AUCTION PRO"); logo.setStyle("-fx-font-size: 38px; -fx-text-fill: #11111b; -fx-font-weight: bold;");
        Label rightDesc = new Label("Tham gia ngay hôm nay\nĐể sở hữu những món đồ Độc - Lạ");
        rightDesc.setStyle("-fx-font-size: 16px; -fx-text-fill: #313244; -fx-text-alignment: center; -fx-font-weight: bold;");
        rightSide.getChildren().addAll(logo, rightDesc);

        splitCard.getChildren().addAll(leftSide, rightSide);
        root.getChildren().add(splitCard);
        primaryStage.setScene(new Scene(root, 1200, 750));
    }

    // ==============================================================================
    // 2. DASHBOARD & SIDEBAR HOVER HIỆN ĐẠI
    // ==============================================================================
    private void showUserDashboard() {
        navButtons.clear();
        VBox sidebar = new VBox(10); sidebar.setPadding(new Insets(20, 10, 20, 10)); sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: rgba(17, 17, 27, 0.95); -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        Label logo = new Label("🔥 AUCTION PRO"); logo.setStyle("-fx-font-size: 26px; -fx-text-fill: #f38ba8; -fx-font-weight: bold; -fx-cursor: hand;");
        logo.setOnMouseClicked(e -> { closeLiveSocket(); setActiveButton(navButtons.get(0)); contentArea.getChildren().setAll(getCatalogView()); });
        VBox.setMargin(logo, new Insets(10, 0, 30, 15));

        Button btnCatalog = createNavButton("🏠 Sàn Đấu Giá");
        Button btnProfile = createNavButton("👤 Hồ sơ bảo mật");
        Button btnWallet = createNavButton("💳 Ví & Thanh toán");
        Button btnPostItem = createNavButton("📤 Đăng bán sản phẩm");
        
        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout = createNavButton("🚪 Đăng xuất");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #f38ba8; -fx-font-size: 16px; -fx-padding: 15; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> { closeLiveSocket(); userToken = ""; showLoginScene(); });

        sidebar.getChildren().addAll(logo, btnCatalog, btnProfile, btnWallet, btnPostItem, spacer, btnLogout);

        contentArea = new StackPane(); contentArea.setPadding(new Insets(30)); HBox.setHgrow(contentArea, Priority.ALWAYS);
        
        // Mặc định tab 1
        setActiveButton(btnCatalog); contentArea.getChildren().setAll(getCatalogView());

        btnCatalog.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnCatalog); contentArea.getChildren().setAll(getCatalogView()); });
        btnProfile.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnProfile); contentArea.getChildren().setAll(getProfileView()); });
        btnWallet.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnWallet); contentArea.getChildren().setAll(getWalletView()); });
        btnPostItem.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnPostItem); contentArea.getChildren().setAll(getCreateAuctionView()); });

        HBox hBoxRoot = new HBox(sidebar, contentArea); StackPane rootPane = new StackPane(); rootPane.setStyle("-fx-background-image: url('" + BG_DASHBOARD + "'); -fx-background-size: cover;");
        rootPane.getChildren().add(hBoxRoot); primaryStage.setScene(new Scene(rootPane, 1280, 800));
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text); btn.setMaxWidth(Double.MAX_VALUE); btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bac2de; -fx-font-size: 16px; -fx-padding: 15 20; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> { if (!btn.getStyle().contains("bold")) btn.setStyle("-fx-background-color: rgba(137, 180, 250, 0.1); -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 20; -fx-cursor: hand; -fx-background-radius: 8;"); });
        btn.setOnMouseExited(e -> { if (!btn.getStyle().contains("bold")) btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bac2de; -fx-font-size: 16px; -fx-padding: 15 20; -fx-cursor: hand;"); });
        navButtons.add(btn); return btn;
    }

    private void setActiveButton(Button activeBtn) {
        for (Button btn : navButtons) {
            if (btn == activeBtn) {
                btn.setStyle("-fx-background-color: rgba(137, 180, 250, 0.2); -fx-text-fill: #89b4fa; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15 20; -fx-border-color: transparent transparent transparent #89b4fa; -fx-border-width: 0 0 0 4; -fx-cursor: hand;");
            } else if (!btn.getText().contains("Đăng xuất")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bac2de; -fx-font-size: 16px; -fx-padding: 15 20; -fx-cursor: hand;");
            }
        }
    }

    // ==============================================================================
    // 3. HỒ SƠ BẢO MẬT KYC (CHUYÊN NGHIỆP Y HỆT ẢNH)
    // ==============================================================================
    private ScrollPane getProfileView() {
        VBox layout = new VBox(25); layout.setPadding(new Insets(20));
        Label title = new Label("HỒ SƠ BẢO MẬT (KYC)"); title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Panel Căn cước
        VBox cccdPanel = new VBox(15); cccdPanel.setPadding(new Insets(25)); cccdPanel.setStyle("-fx-background-color: rgba(30, 30, 46, 0.9); -fx-background-radius: 12; -fx-border-color: #89b4fa; -fx-border-width: 0 0 0 4;");
        Label l1 = new Label("🪪 Thông tin Căn cước công dân"); l1.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid1 = new GridPane(); grid1.setHgap(20); grid1.setVgap(15);
        grid1.add(new Label("Họ và tên"){{setStyle("-fx-text-fill: #bac2de;");}}, 0, 0); grid1.add(createField("VD: HOANG BAO LAM"), 0, 1);
        grid1.add(new Label("Số CCCD"){{setStyle("-fx-text-fill: #bac2de;");}}, 1, 0); grid1.add(createField("VD: 019207..."), 1, 1);
        grid1.add(new Label("Ngày sinh"){{setStyle("-fx-text-fill: #bac2de;");}}, 2, 0); grid1.add(createField("dd/MM/yyyy"), 2, 1);
        grid1.add(new Label("Quê quán"){{setStyle("-fx-text-fill: #bac2de;");}}, 0, 2); 
        TextField txtQue = createField("Xã, Huyện, Tỉnh..."); txtQue.setPrefWidth(300); GridPane.setColumnSpan(txtQue, 2); grid1.add(txtQue, 0, 3);
        cccdPanel.getChildren().addAll(l1, grid1);

        // Panel Ngân hàng
        VBox bankPanel = new VBox(15); bankPanel.setPadding(new Insets(25)); bankPanel.setStyle("-fx-background-color: rgba(30, 30, 46, 0.9); -fx-background-radius: 12; -fx-border-color: #a6e3a1; -fx-border-width: 0 0 0 4;");
        Label l2 = new Label("🏦 Liên kết Ngân Hàng"); l2.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid2 = new GridPane(); grid2.setHgap(20); grid2.setVgap(15);
        grid2.add(new Label("Chủ tài khoản (Phải trùng CCCD)"){{setStyle("-fx-text-fill: #bac2de;");}}, 0, 0); grid2.add(createField("HOANG BAO LAM"), 0, 1);
        grid2.add(new Label("Chọn Ngân Hàng"){{setStyle("-fx-text-fill: #bac2de;");}}, 1, 0); 
        ComboBox<String> cbBank = new ComboBox<>(); cbBank.getItems().addAll("MB Bank", "Vietcombank", "Techcombank", "TPBank"); cbBank.getSelectionModel().selectFirst(); cbBank.setStyle("-fx-background-color: #313244; -fx-text-fill: white;");
        grid2.add(cbBank, 1, 1);
        grid2.add(new Label("Số tài khoản"){{setStyle("-fx-text-fill: #bac2de;");}}, 0, 2); grid2.add(createField("Nhập STK..."), 0, 3);
        bankPanel.getChildren().addAll(l2, grid2);

        Button btnSave = new Button("💾 LƯU THÔNG TIN"); btnSave.setStyle("-fx-background-color: #cba6f7; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        btnSave.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Cập nhật KYC", "Hồ sơ của bạn đang chờ Admin duyệt!"));

        layout.getChildren().addAll(title, cccdPanel, bankPanel, btnSave);
        ScrollPane scroll = new ScrollPane(layout); scroll.setFitToWidth(true); scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    // ==============================================================================
    // 4. VÍ TIỀN VÀ LỊCH SỬ GIAO DỊCH
    // ==============================================================================
    private VBox getWalletView() {
        VBox layout = new VBox(20); Label title = new Label("QUẢN LÝ VÍ & THANH TOÁN"); title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox topBox = new HBox(20);
        Label lblBalance = new Label("Đang tải..."); lblBalance.setStyle("-fx-font-size: 36px; -fx-text-fill: #a6e3a1; -fx-font-weight: bold;");
        VBox balanceCard = new VBox(10, new Label("Số dư khả dụng:") {{ setStyle("-fx-text-fill: #bac2de; -fx-font-size: 16px;"); }}, lblBalance);
        balanceCard.setPadding(new Insets(30)); balanceCard.setPrefWidth(350); balanceCard.setStyle("-fx-background-color: rgba(49, 50, 68, 0.9); -fx-background-radius: 15;");

        VBox depositArea = new VBox(15); depositArea.setPadding(new Insets(30)); depositArea.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15;"); HBox.setHgrow(depositArea, Priority.ALWAYS);
        Label lblDepositTitle = new Label("Nạp tiền vào ví"); lblDepositTitle.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
        HBox methods = new HBox(15); ToggleButton btnBank = new ToggleButton("Bank Transfer"); ToggleButton btnVNPay = new ToggleButton("VNPay"); ToggleButton btnMomo = new ToggleButton("Momo"); ToggleGroup group = new ToggleGroup(); btnBank.setToggleGroup(group); btnVNPay.setToggleGroup(group); btnMomo.setToggleGroup(group); btnBank.setSelected(true); methods.getChildren().addAll(btnBank, btnVNPay, btnMomo); styleMethodBtn(btnBank); styleMethodBtn(btnVNPay); styleMethodBtn(btnMomo);
        HBox inputRow = new HBox(10); TextField txtAmount = createField("Nhập số tiền (VNĐ)..."); Button btnSubmit = new Button("Nạp Ngay"); btnSubmit.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;"); inputRow.getChildren().addAll(txtAmount, btnSubmit);
        depositArea.getChildren().addAll(lblDepositTitle, methods, inputRow);
        topBox.getChildren().addAll(balanceCard, depositArea);

        VBox historyBox = new VBox(10); historyBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15; -fx-padding: 20;");
        Label historyTitle = new Label("Lịch sử giao dịch gần đây"); historyTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        ListView<String> listView = new ListView<>(); listView.setStyle("-fx-control-inner-background: #181825; -fx-text-fill: white; -fx-font-size: 14px;"); listView.setPrefHeight(200);
        listView.getItems().addAll("🟢 +500,000 VNĐ (Khuyến mãi tân thủ) - Hôm nay", "🔴 -100,000 VNĐ (Đấu giá iPhone) - Hôm qua", "🟢 +100,000 VNĐ (Hoàn tiền) - Hôm qua");
        historyBox.getChildren().addAll(historyTitle, listView);

        fetchWalletBalance(lblBalance);
        btnSubmit.setOnAction(e -> {
            try {
                long amount = Long.parseLong(txtAmount.getText().trim());
                String json = String.format("{\"amount\": %s}", amount);
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet/deposit")).header("Authorization", "Bearer " + userToken).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
                httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) { showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã nạp thành công!"); txtAmount.clear(); fetchWalletBalance(lblBalance); listView.getItems().add(0, "🟢 +" + String.format("%,d", amount) + " VNĐ (Nạp tiền) - Vừa xong"); } 
                }));
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ!"); }
        });

        layout.getChildren().addAll(title, topBox, historyBox); return layout;
    }

    // ==============================================================================
    // 5. CHỢ ĐẤU GIÁ (REAL API)
    // ==============================================================================
    private VBox getCatalogView() {
        VBox layout = new VBox(20); Label title = new Label("🔥 SÀN ĐẤU GIÁ NỔI BẬT"); title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");
        FlowPane grid = new FlowPane(20, 20); ScrollPane scroll = new ScrollPane(grid); scroll.setFitToWidth(true); scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent;"); VBox.setVgrow(scroll, Priority.ALWAYS);
        layout.getChildren().addAll(title, scroll);

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/auctions?page=0&size=50")).header("Authorization", "Bearer " + userToken).GET().build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
            if (res.statusCode() == 200) {
                JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                if (json.has("content")) {
                    JsonArray content = json.getAsJsonArray("content");
                    if (content.size() == 0) grid.getChildren().add(new Label("Đang lấy sản phẩm từ kho...") {{ setStyle("-fx-text-fill: #bac2de; -fx-font-size: 16px;"); }});
                    for (JsonElement el : content) grid.getChildren().add(createAuctionCard(el.getAsJsonObject()));
                }
            }
        }));
        return layout;
    }

    // ==============================================================================
    // 6. ĐĂNG BÁN SẢN PHẨM (MỌI USER ĐỀU ĐƯỢC ĐĂNG - ĐÃ BỎ CHẶN ROLE)
    // ==============================================================================
    private VBox getCreateAuctionView() {
        VBox layout = new VBox(20); Label title = new Label("📤 ĐĂNG BÁN SẢN PHẨM LÊN SÀN"); title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");
        VBox formBox = new VBox(15); formBox.setPadding(new Insets(30)); formBox.setMaxWidth(600); formBox.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15;");
        TextField txtTitle = createField("Tiêu đề phiên đấu giá"); TextField txtItemName = createField("Tên sản phẩm"); TextField txtPrice = createField("Giá khởi điểm (VNĐ)"); TextField txtImageUrl = createField("Link ảnh sản phẩm (URL)");
        TextArea txtDesc = new TextArea(); txtDesc.setPromptText("Mô tả chi tiết sản phẩm..."); txtDesc.setPrefRowCount(4); txtDesc.setStyle("-fx-control-inner-background: #313244; -fx-text-fill: white;");
        Button btnSubmit = new Button("🚀 TẠO PHIÊN ĐẤU GIÁ NGAY"); btnSubmit.setMaxWidth(Double.MAX_VALUE); btnSubmit.setStyle("-fx-background-color: linear-gradient(to right, #a6e3a1, #89b4fa); -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            if (txtTitle.getText().isEmpty() || txtPrice.getText().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Lỗi", "Nhập đủ các trường!"); return; }
            try {
                long startPrice = Long.parseLong(txtPrice.getText().trim()); String imgUrl = txtImageUrl.getText().isEmpty() ? "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500" : txtImageUrl.getText();
                String jsonPayload = String.format("{\"title\":\"%s\",\"item\":{\"itemType\":\"ELECTRONICS\",\"itemName\":\"%s\",\"description\":\"%s\",\"startPrice\":%d,\"brand\":\"Hãng\",\"model\":\"Model\",\"conditionStatus\":\"Mới\",\"color\":\"Mặc định\",\"storage\":\"256GB\",\"warrantyMonths\":12},\"imageUrls\":[\"%s\"]}", txtTitle.getText().replace("\"","\\\""), txtItemName.getText().replace("\"","\\\""), txtDesc.getText().replace("\"","\\\"").replace("\n"," "), startPrice, imgUrl);
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/auctions")).header("Authorization", "Bearer " + userToken).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
                httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200 || res.statusCode() == 201) { showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lên Sàn!"); setActiveButton(navButtons.get(0)); contentArea.getChildren().setAll(getCatalogView()); } 
                    else showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi tạo phiên: " + res.body());
                }));
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá tiền sai định dạng!"); }
        });
        formBox.getChildren().addAll(txtTitle, txtItemName, txtPrice, txtImageUrl, txtDesc, btnSubmit); layout.getChildren().addAll(title, formBox); return layout;
    }

    private VBox createAuctionCard(JsonObject auctionData) {
        VBox card = new VBox(10); card.setPadding(new Insets(20)); card.setPrefSize(240, 280); card.setStyle("-fx-background-color: rgba(49, 50, 68, 0.9); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        String id = auctionData.get("auctionId").getAsString(); String title = auctionData.get("title").getAsString(); String price = auctionData.get("currentPrice").getAsString(); String status = auctionData.get("status").getAsString();
        Label lblTitle = new Label(title); lblTitle.setWrapText(true); lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblPrice = new Label(String.format("%,d VNĐ", Long.parseLong(price.split("\\.")[0]))); lblPrice.setStyle("-fx-text-fill: #f9e2af; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label lblStatus = new Label(status); String color = status.equals("OPEN") ? "#a6e3a1" : (status.equals("FINISHED") ? "#f38ba8" : "#bac2de"); lblStatus.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-border-color: " + color + "; -fx-border-radius: 4; -fx-padding: 3 8;");
        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnJoin = new Button(status.equals("OPEN") ? "Vào Phòng Live" : "Đã chốt sổ"); btnJoin.setMaxWidth(Double.MAX_VALUE); btnJoin.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10;");
        btnJoin.setOnAction(e -> { if (status.equals("OPEN")) contentArea.getChildren().setAll(getLiveRoomView(id, title, price)); else showAlert(Alert.AlertType.WARNING, "Đã đóng", "Phiên đấu giá đã kết thúc!"); });
        card.getChildren().addAll(lblStatus, lblTitle, new Label("Giá cao nhất:"){{setStyle("-fx-text-fill:#bac2de;");}}, lblPrice, spacer, btnJoin); return card;
    }

    // ==============================================================================
    // 7. PHÒNG LIVE SOCKET
    // ==============================================================================
    private VBox getLiveRoomView(String auctionId, String title, String startPrice) {
        VBox layout = new VBox(20); HBox header = new HBox(15); header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label("🔥 ĐANG LIVE: " + title); lblTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        Label lblId = new Label("Mã: " + auctionId); lblId.setStyle("-fx-background-color: #313244; -fx-text-fill: #bac2de; -fx-padding: 5 10; -fx-background-radius: 5;");
        header.getChildren().addAll(lblTitle, lblId);

        HBox mainBox = new HBox(20); VBox.setVgrow(mainBox, Priority.ALWAYS);
        VBox bidArea = new VBox(20); bidArea.setPadding(new Insets(30)); bidArea.setPrefWidth(400); bidArea.setStyle("-fx-background-color: rgba(30, 30, 46, 0.85); -fx-background-radius: 15;");
        Label lblCurrentPrice = new Label(String.format("%,d VNĐ", Long.parseLong(startPrice.split("\\.")[0]))); lblCurrentPrice.setStyle("-fx-font-size: 36px; -fx-text-fill: #a6e3a1; -fx-font-weight: bold;");
        TextField txtBidAmount = createField("Nhập giá đặt..."); Button btnPlaceBid = new Button("ĐẶT GIÁ NGAY"); btnPlaceBid.setMaxWidth(Double.MAX_VALUE); btnPlaceBid.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8;");
        Button btnAutoBid = new Button("🤖 Cài Auto-Bid"); btnAutoBid.setMaxWidth(Double.MAX_VALUE); btnAutoBid.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8;");
        bidArea.getChildren().addAll(new Label("GIÁ HIỆN TẠI"){{setStyle("-fx-text-fill: #bac2de;");}}, lblCurrentPrice, new Region() {{ setMinHeight(30); }}, txtBidAmount, btnPlaceBid, btnAutoBid);

        VBox logArea = new VBox(10); HBox.setHgrow(logArea, Priority.ALWAYS); Label lblLogTitle = new Label("Lịch sử diễn biến"); lblLogTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextArea txtLog = new TextArea(">>> Đang kết nối Socket...\n"); txtLog.setEditable(false); txtLog.setStyle("-fx-control-inner-background: #181825; -fx-text-fill: #a6e3a1; -fx-font-family: 'Consolas'; -fx-font-size: 14px;"); VBox.setVgrow(txtLog, Priority.ALWAYS);
        logArea.getChildren().addAll(lblLogTitle, txtLog); mainBox.getChildren().addAll(bidArea, logArea); layout.getChildren().addAll(header, mainBox);

        new Thread(() -> {
            try {
                liveSocket = new Socket("localhost", 9999); socketOut = new PrintWriter(liveSocket.getOutputStream(), true); socketIn = new BufferedReader(new InputStreamReader(liveSocket.getInputStream(), StandardCharsets.UTF_8));
                socketOut.println(String.format("{\"type\":\"JOIN_AUCTION\",\"data\":{\"auctionId\":\"%s\"}}", auctionId));
                Platform.runLater(() -> txtLog.appendText(">>> Đã vào phòng Live thành công!\n"));
                String line; while ((line = socketIn.readLine()) != null) {
                    JsonObject res = gson.fromJson(line, JsonObject.class); String type = res.has("type") ? res.get("type").getAsString() : "";
                    Platform.runLater(() -> {
                        if (type.equals("BID_UPDATE")) {
                            JsonObject bidObj = res.getAsJsonObject("data").getAsJsonObject("bid");
                            if (bidObj != null) { lblCurrentPrice.setText(String.format("%,d VNĐ", Long.parseLong(bidObj.get("amount").getAsString().split("\\.")[0]))); txtLog.appendText(">>> [HOT] Có người đặt giá: " + lblCurrentPrice.getText() + "!\n"); }
                        } else if (type.equals("SUCCESS") || type.equals("ERROR")) { txtLog.appendText(">>> " + (res.has("message") ? res.get("message").getAsString() : "") + "\n"); }
                    });
                }
            } catch (Exception e) { Platform.runLater(() -> txtLog.appendText(">>> Đã ngắt kết nối Live.\n")); }
        }).start();

        btnPlaceBid.setOnAction(e -> {
            try {
                long bidAmount = Long.parseLong(txtBidAmount.getText().trim());
                if (socketOut != null) socketOut.println(String.format("{\"type\":\"PLACE_BID\",\"data\":{\"auctionId\":\"%s\",\"userId\":\"%s\",\"price\":%d}}", auctionId, currentUserId, bidAmount));
                txtLog.appendText(">>> Bạn vừa đặt: " + String.format("%,d VNĐ", bidAmount) + "\n"); txtBidAmount.clear();
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá tiền sai định dạng!"); }
        });

        btnAutoBid.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(); d.setTitle("Auto-Bid"); d.setHeaderText("Hệ thống tự động đấu giá"); d.setContentText("Nhập giá tối đa (VNĐ):");
            d.showAndWait().ifPresent(max -> {
                try {
                    HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/autobids")).header("Authorization", "Bearer " + userToken).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(String.format("{\"auctionId\":\"%s\", \"maxAmount\":%d}", auctionId, Long.parseLong(max.trim())))).build();
                    httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
                        if (res.statusCode() == 200) { txtLog.appendText(">>> [HỆ THỐNG] Đã BẬT Auto-Bid tới: " + max + " VNĐ\n"); } else showAlert(Alert.AlertType.ERROR, "Lỗi", res.body());
                    }));
                } catch(Exception ex) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi nhập giá"); }
            });
        });

        return layout;
    }

    // --- CÁC HÀM TIỆN ÍCH UI ---
    private void fetchWalletBalance(Label l) { HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet")).header("Authorization", "Bearer " + userToken).GET().build(); httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> { if (res.statusCode() == 200) l.setText(String.format("%,d VNĐ", Long.parseLong(gson.fromJson(res.body(), JsonObject.class).get("balance").getAsString().split("\\.")[0]))); })); }
    private void styleMethodBtn(ToggleButton b) { b.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8;"); b.selectedProperty().addListener((o, w, is) -> b.setStyle(is ? "-fx-background-color: #f38ba8; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-font-weight: bold;" : "-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8;")); }
    private void handleRememberMe(String u, String p, boolean isR) { try(PrintWriter w = new PrintWriter(new FileWriter(REMEMBER_FILE))) { if(isR){w.println(u);w.println(p);} else w.print(""); } catch(Exception ignored){} }
    private void loadRememberedUser(TextField u, PasswordField p, CheckBox c) { try(BufferedReader r = new BufferedReader(new FileReader(REMEMBER_FILE))) { String a=r.readLine(), b=r.readLine(); if(a!=null&&b!=null){u.setText(a);p.setText(b);c.setSelected(true);} } catch(Exception ignored){} }
    private TextField createField(String p) { TextField f = new TextField(); f.setPromptText(p); styleInputField(f); return f; }
    private void styleInputField(TextField f) { f.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8;"); }
    private void showAlert(Alert.AlertType t, String title, String c) { Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(c); a.showAndWait(); }

    public static void main(String[] args) { launch(args); }
}