package com.app;

import com.app.service.AuctionService;
import com.app.service.AuthService;
import com.app.service.ImageService;
import com.app.service.NotificationService;
import com.app.service.UserService;
import com.app.service.WalletService;
import com.app.socket.AuctionSocketClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MainApp extends Application {

    private AuctionSocketClient socketClient = new AuctionSocketClient();

    private AuctionService auctionService = new AuctionService();

    private UserService userService = new UserService();

    private WalletService walletService = new WalletService();

    private NotificationService notificationService = new NotificationService();

    private ImageService imageService = new ImageService();

    private AuthService authService = new AuthService();

    private WebSocketClient liveWsClient;

    private Label lbBalance;

    private TextArea txTransactions;

    private int currentPage = 0;

    private final int PAGE_SIZE = 10;

    private Stage primaryStage;
    private StackPane contentArea;
    private String REMEMBER_FILE = "remember_me.txt";

    private String BG_DASHBOARD = "https://res.cloudinary.com/dooo1pcd6/image/upload/v1780434484/ta%CC%89i_xu%C3%B4%CC%81ng_gyefbc.jpg";

    private String currentUsername  = "";
    private String currentUserId = "";
    private String userToken = "";
    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    private List<Button> navButtons = new ArrayList<>();

    private final VBox dynamicFieldsBox = new VBox(20);

    private final Map<String, Control> dynamicInputs = new HashMap<>();

    @Override
    public void start(Stage stage) {

        System.out.println("START");

        this.primaryStage = stage;

        System.out.println("BEFORE LOGIN");

        showLoginScene();

        System.out.println("AFTER LOGIN");

        stage.setTitle("Auction Pro");

        stage.show();

        System.out.println("SHOW");
    }  

    private void setMacBookBackground(Pane root) {
        root.setStyle("-fx-background-image: url('" + BG_DASHBOARD + "'); -fx-background-size: cover; -fx-background-position: center center;");
    }

    private void closeLiveSocket() {
        try {
            if (liveWsClient != null) {
                liveWsClient.close();
                liveWsClient = null;
            }
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

                    String token = json.get("token").getAsString();

                    userToken = token;
                    
                    currentUsername = json.get("username").getAsString(); 
                    
                    currentUserId = json.get("userId").getAsString();

                    socketClient = new AuctionSocketClient();

                        try {

                                socketClient = new AuctionSocketClient();

                                socketClient.connect(token, message -> {
                                        System.out.println("[WS] " + message);
                                });

                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }

                    handleRememberMe(txtUser.getText().trim(), txtPass.getText(), chkRemember.isSelected());

                    showUserDashboard();
                } else showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tài khoản hoặc mật khẩu!");
            })).exceptionally(ex -> { Platform.runLater(() -> { btnLogin.setText("ĐĂNG NHẬP"); btnLogin.setDisable(false); showAlert(Alert.AlertType.ERROR, "Lỗi Server", "Mất kết nối!"); }); return null; });
        });

        Hyperlink forgotPassword = new Hyperlink( "Forgot password?" ); 

        forgotPassword.setStyle(""" 
                                -fx-text-fill: #ff9f43; 
                                -fx-font-size: 13px; 
                                -fx-border-color: transparent; 
                                -fx-cursor: hand; 
                        """);

        forgotPassword.setOnAction(e -> { 
                TextInputDialog dialog = new TextInputDialog(); 
                
                dialog.setTitle( "Reset Password" ); 
                
                dialog.setHeaderText( "Enter your email" ); 
                
                dialog.setContentText( "Email:" ); 
                
                dialog.showAndWait() .ifPresent(email -> { 
                        
                        authService .forgotPassword(email) 
                                        .thenAccept(response -> { 
                                                Platform.runLater(() -> { 
                                                        showAlert( Alert.AlertType.INFORMATION, "Reset Password", "Please check your email" ); 
                                                }); 
                                        }) .exceptionally(ex -> { 
                                                Platform.runLater(() -> { 
                                                        showAlert( Alert.AlertType.ERROR, "Reset Password", ex.getMessage() ); 
                                                }); 
                                                return null; 
                                        }); 
                                }); 
                        });

        HBox linkBox = new HBox(5); linkBox.setAlignment(Pos.CENTER);

        Label lblNoAcc = new Label("Chưa có tài khoản?");
        
        lblNoAcc.setStyle("-fx-text-fill: #bac2de;");

        Hyperlink linkReg = new Hyperlink("Tạo ngay"); 
        
        linkReg.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        linkReg.setOnAction(e -> showRegisterScene());

        linkBox.getChildren().addAll(lblNoAcc, linkReg);

        leftSide.getChildren().addAll(title, txtUser, txtPass, chkRemember, forgotPassword, btnLogin, linkBox);

        // 
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

        Label lbHello = new Label("👋 Xin chào, " + currentUsername);

        // Tạm thời placeholder
        VBox[] vipCardHolder = {createVipCard("NORMAL", BigDecimal.ZERO)};
        VBox.setMargin(vipCardHolder[0], new Insets(0, 10, 10, 10));

        // Gọi API lấy vipLevel và totalSpent
        userService.getProfile(userToken)
                        .thenAccept(response -> {
                                Platform.runLater(() -> {
                                        try {
                                                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                                                String level = json.has("vipLevel") && !json.get("vipLevel").isJsonNull() ? json.get("vipLevel").getAsString() : "NORMAL";

                                                BigDecimal spent = json.has("totalSpent") && !json.get("totalSpent").isJsonNull() ? json.get("totalSpent").getAsBigDecimal() : BigDecimal.ZERO;

                                                VBox newCard = createVipCard(level, spent);

                                                VBox.setMargin(newCard, new Insets(0, 10, 10, 10));

                                                int idx = sidebar.getChildren().indexOf(vipCardHolder[0]);

                                                if (idx >= 0) sidebar.getChildren().set(idx, newCard);

                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                });
                        }); 

        lbHello.setStyle("""
                            -fx-text-fill: white;
                            -fx-font-size: 18px;
                            -fx-font-weight: bold;
                        """);   

        Button btnCatalog = createNavButton("🏠 Sàn Đấu Giá");
        Button btnProfile = createNavButton("👤 Hồ sơ bảo mật");
        Button btnWallet = createNavButton("💳 Ví & Thanh toán");
        Button btnPostItem = createNavButton("📤 Đăng bán sản phẩm");
        Button btnSearch = createNavButton("🔍 Tìm kiếm");
        Button btnNotify = createNavButton("🔔 Thông báo");
        Button btnSeller = createNavButton("📊 Seller Dashboard");
        
        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout = createNavButton("🚪 Đăng xuất");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #f38ba8; -fx-font-size: 16px; -fx-padding: 15; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> { closeLiveSocket(); userToken = ""; showLoginScene(); });

        sidebar.getChildren().addAll(logo, btnCatalog, btnSearch, btnProfile, btnWallet, btnPostItem, spacer, btnLogout, btnNotify, btnSeller, vipCardHolder[0]);

        contentArea = new StackPane(); contentArea.setPadding(new Insets(30)); HBox.setHgrow(contentArea, Priority.ALWAYS);
        
        // Mặc định tab 1
        setActiveButton(btnCatalog); contentArea.getChildren().setAll(getCatalogView());

        btnCatalog.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnCatalog); contentArea.getChildren().setAll(getCatalogView()); });
        btnProfile.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnProfile); contentArea.getChildren().setAll(getProfileView()); });
        btnWallet.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnWallet); contentArea.getChildren().setAll(getWalletView()); });
        btnPostItem.setOnAction(e -> { closeLiveSocket(); setActiveButton(btnPostItem); contentArea.getChildren().setAll(getCreateAuctionView()); });
        btnSearch.setOnAction(e -> {closeLiveSocket(); setActiveButton(btnSearch); contentArea.getChildren().setAll(getSearchView()); });
        btnNotify.setOnAction(e -> {closeLiveSocket(); setActiveButton(btnNotify); contentArea.getChildren().setAll(getNotificationView());});
        btnSeller.setOnAction(e -> {closeLiveSocket(); setActiveButton(btnSeller); contentArea.getChildren().setAll(getSellerDashboardView());});

        ScrollPane sidebarScroll = new ScrollPane(sidebar);

        sidebarScroll.setFitToWidth(true);

        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        sidebarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        sidebarScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        sidebarScroll.prefHeightProperty().bind(primaryStage.heightProperty());

        sidebar.setMinHeight(Region.USE_PREF_SIZE);

        HBox hBoxRoot = new HBox(sidebarScroll, contentArea); 

        StackPane rootPane = new StackPane(); rootPane.setStyle("-fx-background-image: url('" + BG_DASHBOARD + "'); -fx-background-size: cover;");

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

    private VBox getSellerDashboardView() {

        VBox layout = new VBox(20);

        layout.setPadding(new Insets(20));

        Label title = new Label("📊 SELLER DASHBOARD");

        title.setStyle("""
                -fx-font-size: 28px;
                -fx-text-fill: white;
                -fx-font-weight: bold;
        """);

        VBox auctionList = new VBox(15);

        ScrollPane scroll = new ScrollPane( auctionList);

        scroll.setFitToWidth(true);

        auctionService
                .getAuctionBySeller(currentUserId)
                .thenAccept(response -> {
                        Platform.runLater(() -> {

                                try {

                                        System.out.println(response.body());

                                        JsonObject json =
                                                gson.fromJson(
                                                        response.body(),
                                                        JsonObject.class
                                                );

                                        JsonArray arr =
                                                json.getAsJsonArray("content");

                                        if (
                                                arr == null
                                                || arr.size() == 0
                                        ) {

                                                Label empty =
                                                        new Label(
                                                                "Chưa có sản phẩm nào"
                                                        );

                                                empty.setStyle("""
                                                -fx-text-fill: white;
                                                -fx-font-size: 18px;
                                                -fx-font-weight: bold;
                                                """);

                                                auctionList.getChildren().add(empty);

                                                return;
                                        }

                                        for (JsonElement el : arr) {

                                                JsonObject auction =
                                                        el.getAsJsonObject();

                                                VBox card =
                                                        createAuctionCard(auction);

                                                Button btnDelete =
                                                        new Button("🗑 Delete");

                                                btnDelete.setOnAction(e -> {

                                                // delete logic
                                                });

                                                card.getChildren().add(btnDelete);

                                                auctionList
                                                        .getChildren()
                                                        .add(card);
                                        }

                                        } catch (Exception ex) {

                                        ex.printStackTrace();
                                }
                        });
                });

        layout.getChildren().addAll(title, scroll);

        return layout;
    }

    private VBox createVipCard(String vipLevel, BigDecimal totalSpent) {

    VBox card = new VBox(10);

    card.setPadding(new Insets(16));

    card.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 12;");

    // Badge màu theo level
    String badgeColor = switch (vipLevel) {
        case "BRONZE"  -> "#CD7F32";
        case "SILVER"  -> "#A8A9AD";
        case "GOLD"    -> "#FFD700";
        case "DIAMOND" -> "#378ADD";
        default        -> "#888780";
    };
    String icon = switch (vipLevel) {
        case "BRONZE"  -> "🥉";
        case "SILVER"  -> "🥈";
        case "GOLD"    -> "🥇";
        case "DIAMOND" -> "💎";
        default        -> "👤";
    };
    int discount = switch (vipLevel) {
        case "BRONZE"  -> 3;
        case "SILVER"  -> 7;
        case "GOLD"    -> 12;
        case "DIAMOND" -> 18;
        default        -> 0;
    };
    BigDecimal nextThreshold = switch (vipLevel) {
        case "NORMAL"  -> new BigDecimal("5000000");
        case "BRONZE"  -> new BigDecimal("50000000");
        case "SILVER"  -> new BigDecimal("500000000");
        case "GOLD"    -> new BigDecimal("1000000000");
        default        -> null;
    };

    // Title row
    Label lblIcon = new Label(icon + " VIP Status");

    lblIcon.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    // Badge level
    Label lblLevel = new Label(vipLevel);

    lblLevel.setStyle(String.format(
        "-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 6; -fx-font-size: 12px; -fx-font-weight: bold;",
        badgeColor
    ));

    HBox titleRow = new HBox(8, lblIcon, lblLevel);

    titleRow.setAlignment(Pos.CENTER_LEFT);

    // Separator
    Separator sep = new Separator();

    sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

    // Stats row
    DecimalFormat df = new DecimalFormat("#,###");

    VBox statSpent = createStatBox("Tổng đã tiêu", df.format(totalSpent) + " VNĐ");

    VBox statDiscount = createStatBox("Chiết khấu", discount + "%");

    HBox statsRow = new HBox(12, statSpent, statDiscount);

    HBox.setHgrow(statSpent, Priority.ALWAYS);

    HBox.setHgrow(statDiscount, Priority.ALWAYS);

    statSpent.setMaxWidth(Double.MAX_VALUE);

    statDiscount.setMaxWidth(Double.MAX_VALUE);

    card.getChildren().addAll(titleRow, sep, statsRow);

    // Progress bar nếu chưa phải Diamond
    if (nextThreshold != null) {

        double pct = totalSpent.doubleValue() / nextThreshold.doubleValue();

        pct = Math.min(pct, 1.0);

        String nextLevel = switch (vipLevel) {
            case "NORMAL" -> "BRONZE";
            case "BRONZE" -> "SILVER";
            case "SILVER" -> "GOLD";
            default       -> "DIAMOND";
        };

        Label lblProgress = new Label("Tiến độ → " + nextLevel);

        lblProgress.setStyle("-fx-text-fill: #8a9bb5; -fx-font-size: 12px;");

        ProgressBar progressBar = new ProgressBar(pct);

        progressBar.setMaxWidth(Double.MAX_VALUE);

        progressBar.setStyle(String.format("-fx-accent: %s;", badgeColor));

        BigDecimal remaining = nextThreshold.subtract(totalSpent).max(BigDecimal.ZERO);

        Label lblRemaining = new Label("Cần thêm " + df.format(remaining) + " ₫ để lên " + nextLevel);

        lblRemaining.setStyle("-fx-text-fill: #8a9bb5; -fx-font-size: 11px;");

        card.getChildren().addAll(lblProgress, progressBar, lblRemaining);
    } else {
        Label lblMax = new Label("🏆 Bạn đã đạt cấp cao nhất!");

        lblMax.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");

        card.getChildren().add(lblMax);
    }

    return card;
}

private VBox createStatBox(String label, String value) {

    Label lbl = new Label(label);

    lbl.setStyle("-fx-text-fill: #8a9bb5; -fx-font-size: 11px;");

    Label val = new Label(value);

    val.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

    VBox box = new VBox(3, lbl, val);

    box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8; -fx-padding: 10;");

    return box;
}

    // ==============================================================================
    // 3. HỒ SƠ 
    // ==============================================================================
    private ScrollPane getProfileView() {

        VBox layout = new VBox(25);

        layout.setPadding(new Insets(20));

        layout.setStyle("""
                        -fx-background-color: transparent;
                        """);

        // =====================================================
        // TITLE
        // =====================================================

        Label title = new Label("👤 USER PROFILE");

        title.setStyle("""
                            -fx-font-size: 30px;
                            -fx-font-weight: bold;
                            -fx-text-fill: white;
                        """);

        // =====================================================
        // PROFILE PANEL
        // =====================================================

        VBox profilePanel = new VBox(20);

        profilePanel.setPadding(new Insets(25));

        profilePanel.setStyle("""
                                -fx-background-color: rgba(255,255,255,0.08);
                                -fx-background-radius: 20;
                            """);

        Label profileTitle = new Label("📋 Personal Information");

        profileTitle.setStyle("""
                                -fx-text-fill: #a6e3a1;
                                -fx-font-size: 22px;
                                -fx-font-weight: bold;
                            """);

        GridPane grid = new GridPane();

        grid.setHgap(20);

        grid.setVgap(18);

        // =====================================================
        // FIELDS
        // =====================================================

        TextField txtUsername = createField("Username");

        txtUsername.setEditable(false);

        Label lblUserId = new Label("User ID");

        TextField txtUserId = new TextField(currentUserId);

        txtUserId.setEditable(false);

        TextField txtFullName = createField("Full Name");

        TextField txtPhone = createField("Phone");

        TextField txtAddress = createField("Address");

        TextField txtEmail = createField("Email");

        // =====================================================
        // LABELS
        // =====================================================

        Label lbUsername = createProfileLabel("Username");

        Label lbFullName = createProfileLabel("Full Name");

        Label lbPhone = createProfileLabel("Phone");

        Label lbAddress = createProfileLabel("Address");

        Label lbEmail = createProfileLabel("Email");

        // =====================================================
        // GRID
        // =====================================================

        grid.add(lbUsername, 0, 0);
        grid.add(txtUsername, 1, 0);

        grid.add(lblUserId, 0, 1);
        grid.add(txtUserId, 1, 1);

        grid.add(lbFullName, 0, 2);
        grid.add(txtFullName, 1, 2);

        grid.add(lbPhone, 0, 3);
        grid.add(txtPhone, 1, 3);

        grid.add(lbAddress, 0, 4);
        grid.add(txtAddress, 1, 4);

        grid.add(lbEmail, 0, 5);
        grid.add(txtEmail, 1, 5);

        profilePanel.getChildren().addAll(profileTitle, grid);

        // =====================================================
        // UPDATE PROFILE BUTTON
        // =====================================================

        Button btnSave = new Button("💾 UPDATE PROFILE");

        btnSave.setStyle("""
                            -fx-background-color: linear-gradient(to right, #89b4fa, #74c7ec);
                            -fx-text-fill: white;
                            -fx-font-size: 15px;
                            -fx-font-weight: bold;
                            -fx-background-radius: 12;
                            -fx-padding: 12 24 12 24;
                        """);

        // =====================================================
        // LOAD PROFILE
        // =====================================================

        userService
                .getProfile(userToken)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        try {
                            JsonObject obj =
                                    gson.fromJson(
                                            response.body(),
                                            JsonObject.class
                                    );
                            txtUsername.setText(
                                    obj.has("username")
                                            ? obj.get("username").getAsString()
                                            : ""
                            );
                            txtFullName.setText(
                                    obj.has("fullName")
                                            ? obj.get("fullName").getAsString()
                                            : ""
                            );
                            txtPhone.setText(
                                    obj.has("phone")
                                            ? obj.get("phone").getAsString()
                                            : ""
                            );
                            txtAddress.setText(
                                    obj.has("address")
                                            ? obj.get("address").getAsString()
                                            : ""
                            );
                            txtEmail.setText(
                                    obj.has("email")
                                            ?obj.get("email").getAsString()
                                            :""
                            );
                        } catch (Exception ex) {
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Profile Error",
                                    "Cannot parse profile data"
                            );
                        }
                    });

                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showAlert(
                                Alert.AlertType.ERROR,
                                "API Error",
                                ex.getMessage()
                        );
                    });
                    return null;
                });

        // =====================================================
        // UPDATE PROFILE EVENT
        // =====================================================

        btnSave.setOnAction(e -> {

            userService.updateProfile(
                            userToken,
                            txtFullName.getText(),
                            txtPhone.getText(),
                            txtAddress.getText(),
                            txtEmail.getText()
                    )
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Update Profile",
                                    "Profile updated successfully"
                            );
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "API Error",
                                    ex.getMessage()
                            );
                        });
                        return null;
                    });
        });

        // =====================================================
        // CHANGE PASSWORD PANEL
        // =====================================================

        VBox passwordPanel = new VBox(15);

        passwordPanel.setPadding(new Insets(25));

        passwordPanel.setStyle("""
                                -fx-background-color: rgba(255,255,255,0.08);
                                -fx-background-radius: 20;
                            """);

        Label passwordTitle = new Label("🔐 Change Password");

        passwordTitle.setStyle("""
                                -fx-text-fill: #f9e2af;
                                -fx-font-size: 22px;
                                -fx-font-weight: bold;
                            """);

        PasswordField txtOldPassword = new PasswordField();

        txtOldPassword.setPromptText("Old Password");

        PasswordField txtNewPassword = new PasswordField();

        txtNewPassword.setPromptText("New Password");

        Button btnChangePassword = new Button("🔑 CHANGE PASSWORD");

        btnChangePassword.setStyle("""
                                    -fx-background-color: linear-gradient(to right, #f38ba8, #fab387);
                                    -fx-text-fill: white;
                                    -fx-font-size: 15px;
                                    -fx-font-weight: bold;
                                    -fx-background-radius: 12;
                                    -fx-padding: 10 20 10 20;
                                """);

        btnChangePassword.setOnAction(e -> {

            userService.changePassword(
                            userToken,
                            txtOldPassword.getText(),
                            txtNewPassword.getText()
                    )
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Change Password",
                                    "Password changed successfully"
                            );
                            txtOldPassword.clear();
                            txtNewPassword.clear();
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "API Error",
                                    ex.getMessage()
                            );
                        }); 
                        return null;
                    });
        });

        passwordPanel.getChildren().addAll(
                passwordTitle,
                txtOldPassword,
                txtNewPassword,
                btnChangePassword
        );

        // =====================================================
        // ROOT
        // =====================================================

        layout.getChildren().addAll(
                title,
                profilePanel,
                btnSave,
                passwordPanel
        );

        ScrollPane scroll =
                new ScrollPane(layout);

        scroll.setFitToWidth(true);

        scroll.setStyle("""
                            -fx-background: transparent;
                            -fx-background-color: transparent;
                    """);

        return scroll;
    }

    // ==============================================================================
    // 4. VÍ TIỀN VÀ LỊCH SỬ GIAO DỊCH
    // ==============================================================================
    private ScrollPane getWalletView() {

    VBox layout = new VBox(25);

        layout.setPadding(new Insets(20));

        // =========================
        // TITLE
        // =========================

        Label title =
                new Label("💰 MY WALLET");

        title.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        // =========================
        // BALANCE CARD
        // =========================

        VBox balanceCard =
                new VBox(15);

        balanceCard.setPadding(
                new Insets(25)
        );

        balanceCard.setStyle("""
            -fx-background-color:
            rgba(255,255,255,0.08);

            -fx-background-radius: 20;
        """);

        Label lbBalanceTitle =
                new Label("Current Balance");

        lbBalanceTitle.setStyle("""
            -fx-text-fill: #bac2de;
            -fx-font-size: 18px;
        """);

        lbBalance =
                new Label("VNĐ 0.00");

        fetchWalletBalance(lbBalance);

        balanceCard.getChildren().addAll(
                lbBalanceTitle,
                lbBalance
        );

        // =========================
        // ACTION PANEL
        // =========================

        VBox actionPanel =
                new VBox(20);

        actionPanel.setPadding(
                new Insets(25)
        );

        actionPanel.setStyle("""
            -fx-background-color:
            rgba(255,255,255,0.08);

            -fx-background-radius: 20;
        """);

        Label actionTitle =
                new Label("Wallet Actions");

        actionTitle.setStyle("""
            -fx-text-fill: #89b4fa;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
        """);

        TextField txtAmount =
                createField("Enter Amount");

        HBox btnBox =
                new HBox(15);

        Button btnDeposit =
                new Button("💵 Deposit");

        Button btnWithdraw =
                new Button("💸 Withdraw");

        btnDeposit.setStyle("""
            -fx-background-color: #a6e3a1;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
        """);

        btnWithdraw.setStyle("""
            -fx-background-color: #f38ba8;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
        """);

        btnBox.getChildren().addAll(
                btnDeposit,
                btnWithdraw
        );

        actionPanel.getChildren().addAll(
                actionTitle,
                txtAmount,
                btnBox
        );

        // =========================
        // TRANSACTION PANEL
        // =========================

        VBox transactionPanel =
                new VBox(15);

        transactionPanel.setPadding(
                new Insets(25)
        );

        transactionPanel.setStyle("""
            -fx-background-color:
            rgba(255,255,255,0.08);

            -fx-background-radius: 20;
        """);

        Label txTitle =
                new Label("📜 Transactions");

        txTitle.setStyle("""
            -fx-text-fill: #f9e2af;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
        """);

        txTransactions =
                new TextArea();

        txTransactions.setEditable(false);

        txTransactions.setPrefHeight(250);

        txTransactions.setStyle("""
            -fx-control-inner-background:
            rgba(20,20,30,0.95);

            -fx-text-fill: white;
        """);

        transactionPanel.getChildren().addAll(
                txTitle,
                txTransactions
        );

        // =========================
        // LOAD WALLET
        // =========================
        loadWallet();

        // =========================
        // LOAD TRANSACTIONS
        // =========================

        loadTransactions();

        // =========================
        // DEPOSIT EVENT
        // =========================

        btnDeposit.setOnAction(e -> {

            BigDecimal amount =
                    BigDecimal.valueOf(Double.parseDouble(
                            txtAmount.getText())
                    );

            walletService
                    .deposit(
                            userToken,
                            amount
                    )

                    .thenAccept(response -> {

                        Platform.runLater(() -> {

                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Deposit",
                                    "Deposit successful"
                            );

                            loadWallet();

                            loadTransactions();

                        });

                    });
        });

        // =========================
        // WITHDRAW EVENT
        // =========================

        btnWithdraw.setOnAction(e -> {

            BigDecimal amount =
                    BigDecimal.valueOf(Double.parseDouble(
                            txtAmount.getText())
                    );

            walletService
                    .withdraw(
                            userToken,
                            amount
                    )

                    .thenAccept(response -> {

                        Platform.runLater(() -> {

                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Withdraw",
                                    "Withdraw successful"
                            );

                            loadWallet();

                            loadTransactions();

                        });

                    });
        });

        // =========================
        // ROOT
        // =========================

        layout.getChildren().addAll(
                title,
                balanceCard,
                actionPanel,
                transactionPanel
        );

        ScrollPane scroll =
                new ScrollPane(layout);

        scroll.setFitToWidth(true);

        scroll.setStyle("""
            -fx-background:
            transparent;

            -fx-background-color:
            transparent;
        """);

        return scroll;
    }

    // ==============================================================================
    // 5. CHỢ ĐẤU GIÁ (REAL API)
    // ==============================================================================
    private VBox getCatalogView() {

        VBox layout = new VBox(20);

        layout.setPadding(new Insets(20));

        Label title = new Label("🔥 SÀN ĐẤU GIÁ NỔI BẬT");

        title.setStyle("""
                -fx-font-size: 28px;
                -fx-text-fill: white;
                -fx-font-weight: bold;
        """);

        FlowPane grid = new FlowPane();

        grid.setHgap(20);
        grid.setVgap(20);

        ScrollPane scroll = new ScrollPane(grid);

        scroll.setFitToWidth(true);

        scroll.setStyle("""
                -fx-background: transparent;
                -fx-background-color: transparent;
        """);

        Button btnPrev = new Button("⬅ Prev");
        Button btnNext = new Button("Next ➡");

        Label lblPage = new Label();

        lblPage.setStyle("""
                                -fx-text-fill: white;
                                -fx-font-size: 16px;   
                        """);

        HBox paging = new HBox(15, btnPrev, lblPage, btnNext);

        paging.setAlignment(Pos.CENTER);

        Runnable loadPage = () -> {
                grid.getChildren().clear();

                lblPage.setText("Page " + (currentPage + 1));

                auctionService.getAllAuctions(currentPage, PAGE_SIZE)
                                .thenAccept(response -> {
                                        Platform.runLater(() -> {

                                                try {

                                                        System.out.println(response.body());

                                                        JsonObject json =
                                                                gson.fromJson(
                                                                        response.body(),
                                                                        JsonObject.class
                                                                );

                                                        JsonArray content =
                                                                json.getAsJsonArray("content");

                                                        if (
                                                                content == null
                                                                || content.size() == 0
                                                        ) {

                                                                Label emptyLabel =
                                                                        new Label(
                                                                                "Chưa có phiên đấu giá nào"
                                                                        );

                                                                emptyLabel.setStyle("""
                                                                -fx-text-fill: white;
                                                                -fx-font-size: 20px;
                                                                -fx-font-weight: bold;
                                                                """);

                                                                grid.getChildren().add(emptyLabel);

                                                                return;
                                                        }

                                                        for (JsonElement el : content) {

                                                                grid.getChildren().add(
                                                                        createAuctionCard(
                                                                                el.getAsJsonObject()
                                                                        )
                                                                );
                                                        }

                                                        } catch (Exception ex) {

                                                        ex.printStackTrace();

                                                        showAlert(
                                                                Alert.AlertType.ERROR,
                                                                "Catalog",
                                                                "Không load được dữ liệu"
                                                        );
                                                }
                                        });
                                });
        };

        btnPrev.setOnAction(e -> {if (currentPage > 0){
                                        currentPage --;
                                        loadPage.run();
                                }
                        });  
                        
        btnNext.setOnAction(e -> {currentPage++; loadPage.run();

                        });     

        layout.getChildren().addAll(title, scroll, paging);

        loadPage.run();

        return layout;
    }

    // ==============================================================================
    // 6. ĐĂNG BÁN SẢN PHẨM (MỌI USER ĐỀU ĐƯỢC ĐĂNG - ĐÃ BỎ CHẶN ROLE)
    // ==============================================================================
    private VBox getCreateAuctionView() {

        VBox layout = new VBox(20);

        layout.setPadding(new Insets(20));

        // =================================================
        // TITLE
        // =================================================

        Label title =
                new Label("📤 ĐĂNG SẢN PHẨM");

        title.setStyle("""
            -fx-font-size: 28px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        // =================================================
        // FORM BOX
        // =================================================

        VBox formBox = new VBox(15);

        formBox.setPadding(new Insets(30));

        formBox.setMaxWidth(650);

        formBox.setStyle("""
            -fx-background-color:
            rgba(30,30,46,0.85);

            -fx-background-radius: 20;
        """);

        // =================================================
        // INPUTS
        // =================================================

        TextField txtTitle =
                createField("Tiêu đề phiên đấu giá");

        TextField txtItemName =
                createField("Tên sản phẩm");

        // =================================================
        // ITEM TYPE
        // =================================================

        Label lblType =
                new Label("Loại sản phẩm");

        lblType.setStyle("""
            -fx-text-fill: #bac2de;
            -fx-font-size: 14px;
        """);

        ComboBox<String> cbType =
                new ComboBox<>();

        cbType.getItems().addAll(
                "ELECTRONICS",
                "FASHION",
                "VEHICLE",
                "BOOK",
                "JEWELRY",
                "ART",
                "COLLECTIBLE"
        );

        cbType.setValue("ELECTRONICS");

        cbType.setPrefHeight(40);

        cbType.setMaxWidth(Double.MAX_VALUE);

        cbType.setStyle("""
            -fx-background-color:
            rgba(255,255,255,0.08);

            -fx-text-fill: white;

            -fx-background-radius: 10;
        """);

        cbType.setOnAction(e -> {
                String type = cbType.getValue();

                renderDynamicFields(type);
        });

        // =================================================
        // PRICE
        // =================================================

        TextField txtPrice =
                createField("Giá khởi điểm");

        // =================================================
        // IMAGE
        // =================================================

        Label lbImage =
                new Label("Chưa chọn ảnh");

        lbImage.setStyle("""
            -fx-text-fill: #bac2de;
        """);

        Button btnChooseImage =
                new Button("🖼 Upload Image");

        btnChooseImage.setStyle("""
            -fx-background-color: #89b4fa;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
        """);

        final String[] uploadedImage = {""};

        btnChooseImage.setOnAction(e -> {

            FileChooser chooser =
                    new FileChooser();

            chooser.setTitle("Choose Image");

            File file =
                    chooser.showOpenDialog(primaryStage);

            if (file != null) {

                lbImage.setText(file.getName());

                try {

                    try {

                                uploadedImage[0] = imageService.uploadImage(file.toPath());

                                System.out.println(uploadedImage[0]);

                                showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Upload",
                                        "Image uploaded successfully"
                                );

                        } catch (Exception ex) {

                                ex.printStackTrace();

                                showAlert(
                                        Alert.AlertType.ERROR,
                                        "Upload Error",
                                        ex.getMessage()
                                );
                        }

                } catch (Exception ex) {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Upload Error",
                            ex.getMessage()
                    );
                }
            }
        });

        // =================================================
        // DESCRIPTION
        // =================================================

        TextArea txtDesc =
                new TextArea();

        txtDesc.setPromptText(
                "Mô tả chi tiết sản phẩm..."
        );

        txtDesc.setPrefRowCount(5);

        txtDesc.setStyle("""
            -fx-control-inner-background:
            rgba(241, 237, 237, 0.96);

            -fx-text-fill: black;

            -fx-background-radius: 10;
        """);

        // =================================================
        // SUBMIT BUTTON
        // =================================================

        Button btnSubmit =
                new Button("🚀 TẠO PHIÊN ĐẤU GIÁ");

        btnSubmit.setMaxWidth(Double.MAX_VALUE);

        btnSubmit.setStyle("""
            -fx-background-color:
            linear-gradient(
                to right,
                #89b4fa,
                #cba6f7
            );

            -fx-text-fill: white;

            -fx-font-size: 16px;

            -fx-font-weight: bold;

            -fx-background-radius: 12;

            -fx-padding: 14;
        """);

        // =================================================
        // SUBMIT EVENT
        // =================================================

        btnSubmit.setOnAction(e -> {

            if (
                    txtTitle.getText().isBlank() ||
                    txtItemName.getText().isBlank() ||
                    txtPrice.getText().isBlank()
            ) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Validation",
                        "Nhập đầy đủ thông tin"
                );

                return;
            }

            try {

                BigDecimal startPrice =
                        new BigDecimal(
                                txtPrice.getText().trim()
                        );

                String imgUrl =
                        uploadedImage[0];

                // fallback image
                if (imgUrl.isBlank()) {

                    imgUrl =
                            "https://images.unsplash.com/photo-1542291026-7eec264c27ff";
                }

                // =================================================
                // JSON PAYLOAD
                // =================================================

                ObjectMapper mapper = new ObjectMapper();

                ObjectNode root = mapper.createObjectNode();

                root.put("title", txtTitle.getText());

                ObjectNode item = root.putObject("item");

                item.put(
                        "itemType",
                        cbType.getValue().toString()
                );

                item.put(
                        "itemName",
                        txtItemName.getText()
                );

                item.put(
                        "description",
                        txtDesc.getText()
                );

                item.put("startPrice", startPrice);

                ArrayNode imageUrls = root.putArray("imageUrls");

                imageUrls.add(imgUrl);

                for (String key : dynamicInputs.keySet()) {

                        Control control = dynamicInputs.get(key);

                        if (control instanceof TextField tf) {

                                item.put(key, tf.getText());
                        }
                }

                String jsonPayload =
                        mapper.writeValueAsString(root);

                System.out.println(jsonPayload);
                // =================================================
                // CREATE AUCTION API
                // =================================================

                auctionService
                        .createAuction(
                                userToken,
                                jsonPayload
                        )

                        .thenAccept(response -> {

                            Platform.runLater(() -> {

                                if (
                                        response.statusCode() == 200 ||
                                        response.statusCode() == 201
                                ) {

                                    showAlert(
                                            Alert.AlertType.INFORMATION,
                                            "Success",
                                            "Đăng sản phẩm thành công"
                                    );

                                    contentArea
                                            .getChildren()
                                            .setAll(
                                                    getCatalogView()
                                            );

                                } else {

                                    showAlert(
                                            Alert.AlertType.ERROR,
                                            "API Error",
                                            response.body()
                                    );
                                }

                            });

                        })

                        .exceptionally(ex -> {

                            Platform.runLater(() -> {

                                showAlert(
                                        Alert.AlertType.ERROR,
                                        "Create Error",
                                        ex.getMessage()
                                );

                            });

                            return null;
                        });

            }catch (NumberFormatException exc){

                showAlert(
                        Alert.AlertType.ERROR,
                        "Invalid Price",
                        "Giá tiền không hợp lệ"
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                showAlert(Alert.AlertType.ERROR, "Error", "Error");
            }
        });

        // =================================================
        // ADD COMPONENTS
        // =================================================

        formBox.getChildren().addAll(
                txtTitle,
                txtItemName,
                lblType,
                cbType,
                txtPrice,
                dynamicFieldsBox,
                btnChooseImage,
                lbImage,
                txtDesc,
                btnSubmit
        );

        // =================================================
        // ROOT
        // =================================================

        ScrollPane scrollPane = new ScrollPane(formBox);

        scrollPane.setFitToWidth(true);

        scrollPane.setPannable(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle("""
                                -fx-background: transparent;
                                -fx-background-color: transparent;
                        """);

        layout.getChildren().addAll(
                title,
                scrollPane
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        return layout;
    }

    private VBox createAuctionCard(JsonObject auctionData) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefWidth(320);

        card.setStyle("""
            -fx-background-color:
            rgba(255,255,255,0.08);

            -fx-background-radius: 18;
        """);

        String auctionId =
                auctionData
                        .get("auctionId")
                        .getAsString();

        String title =
                auctionData
                        .get("title")
                        .getAsString();

        String price =
                auctionData
                        .get("currentPrice")
                        .getAsString();

        String status =
                auctionData
                        .get("status")
                        .getAsString();

        String startTime = auctionData.get("startTime").getAsString();

        String endTime = auctionData.get("endTime").getAsString();

        long remainingSeconds = auctionData.get("remainingSeconds").getAsLong();

        Label lblStatus = new Label(status);

        lblStatus.setStyle("""
            -fx-background-color:
            #a6e3a1;

            -fx-text-fill: black;

            -fx-padding: 5 10 5 10;

            -fx-background-radius: 10;
        """);

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Label lblStartTime = new Label("Bắt đầu: " + start.format(fmt));
        Label lblEndTime = new Label("Kết thúc: " + end.format(fmt));

        lblStartTime.setStyle("""
                                -fx-text-fill: #bac2de;
                                -fx-font-size: 13px;
                        """);

        lblEndTime.setStyle("""
                                -fx-text-fill: #bac2de;
                                -fx-font-size: 13px;
                        """);

        Label lblCountdown = new Label();

        lblCountdown.setStyle("""
                                -fx-text-fill: #ff7675;
                                -fx-font-size: 15px;
                                -fx-font-weight: bold;
                        """);

        final long[] sec = {remainingSeconds};

        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
                                                                if (sec[0] <= 0){
                                                                        lblCountdown.setText("Auction Ended");

                                                                        return;
                                                                }

                                                                long h = sec[0] / 3600;

                                                                long m = (sec[0] % 3600) / 60;

                                                                long s = (sec[0] % 60);

                                                                lblCountdown.setText(String.format("⏳ %02d:%02d:%02d", h, m, s));

                                                                sec[0]--;
                                                                }
                                                        )
                                        );

        if ("ACTIVE".equals(status)) {
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
        } else {
                lblCountdown.setText("Bắt đầu: " + start.format(fmt));
        }

        Label lblTitle =
                new Label(title);

        lblTitle.setWrapText(true);

        lblTitle.setStyle("""
            -fx-text-fill: white;

            -fx-font-size: 18px;

            -fx-font-weight: bold;
        """);

        BigDecimal currentPrice = new BigDecimal(auctionData.get("currentPrice").getAsString());

        DecimalFormat df = new DecimalFormat("#,###");
        Label lblPrice =
                new Label(
                        df.format(currentPrice) + "VND"
                );

        lblPrice.setStyle("""
            -fx-text-fill: #f9e2af;

            -fx-font-size: 20px;

            -fx-font-weight: bold;
        """);

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        // =========================
        // BUTTONS
        // =========================

        HBox actionBox =
                new HBox(10);

        Button btnJoin =
                new Button(
                        status.equals("OPEN")
                                ? "🔥 Live"
                                : "⛔ Closed"
                );

        Button btnFollow =  new Button("⭐ Follow");
        Button btnUnfollow = new Button("💔 Unfollow");

        btnJoin.setStyle("""
            -fx-background-color:
            #89b4fa;

            -fx-text-fill: white;

            -fx-font-weight: bold;
        """);

        btnFollow.setStyle("""
            -fx-background-color:
            #f9e2af;

            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
        """);

        btnUnfollow.setStyle("""
            -fx-background-color:
            #f9e2af;

            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
        """);

        btnJoin.setDisable(
                !status.equals("OPEN")
        );

        // =========================
        // JOIN LIVE
        // =========================

        btnJoin.setOnAction(e -> {
            contentArea
                    .getChildren()
                    .setAll(
                            getLiveRoomView(
                                    auctionId,
                                    title,
                                    price
                            )
                    );
        });

        // =========================
        // FOLLOW
        // =========================

        btnFollow.setOnAction(e -> {
            auctionService
                    .followAuction(
                            userToken,
                            auctionId
                    )
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Follow",
                                    "Auction followed"
                            );

                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Follow Error",
                                    ex.getMessage()
                            );
                        });

                        return null;
                    });
        });

        btnUnfollow.setOnAction(e -> {
                auctionService.unfollowAuction(userToken, auctionId)
                                .thenAccept(response -> {
                                        Platform.runLater(() -> {
                                                showAlert(Alert.AlertType.CONFIRMATION, "Unfollow", "Đã bỏ theo dõ phiên đấu giá");
                                        });
                                });
        });

        actionBox.getChildren().addAll(
                btnJoin,
                btnFollow,
                btnUnfollow
        );

        card.getChildren().addAll(
                lblStatus,
                lblTitle,
                lblEndTime,
                lblCountdown,
                new Label("Current Price"),
                lblPrice,
                spacer,
                actionBox
        );

        return card;    
}

    // ==============================================================================
    // 7. PHÒNG LIVE SOCKET
    // ==============================================================================
    private VBox getLiveRoomView(
        String auctionId,
        String title,
        String startPrice
    ) {

        VBox layout = new VBox(20);

        layout.setPadding(new Insets(20));

        // =================================================
        // HEADER
        // =================================================

        HBox header = new HBox(15);

        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle =
                new Label("🔥 LIVE: " + title);

        lblTitle.setStyle("""
            -fx-font-size: 24px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        Label lblId =
                new Label("ID: " + auctionId);

        lblId.setStyle("""
            -fx-background-color: #313244;
            -fx-text-fill: #bac2de;
            -fx-padding: 5 10 5 10;
            -fx-background-radius: 10;
        """);

        header.getChildren().addAll(
                lblTitle,
                lblId
        );

        // =================================================
        // MAIN BOX
        // =================================================

        HBox mainBox = new HBox(20);

        VBox.setVgrow(
                mainBox,
                Priority.ALWAYS
        );

        // =================================================
        // LEFT PANEL
        // =================================================

        VBox bidArea = new VBox(20);

        bidArea.setPadding(
                new Insets(30)
        );

        bidArea.setPrefWidth(420);

        bidArea.setStyle("""
            -fx-background-color:
            rgba(30,30,46,0.85);

            -fx-background-radius: 15;
        """);

        // =================================================
        // CURRENT PRICE
        // =================================================

        BigDecimal currentPrice = new BigDecimal(startPrice);

        DecimalFormat moneyFormat = new DecimalFormat("#,###");

        Label lblCurrentPrice = new Label(moneyFormat.format(currentPrice) + " VND");

        lblCurrentPrice.setStyle("""
            -fx-font-size: 34px;
            -fx-font-weight: bold;
            -fx-text-fill: #f9e2af;
        """);

        // =================================================
        // INPUT BID
        // =================================================

        TextField txtBidAmount =
                createField("Nhập giá đấu...");

        Button btnPlaceBid =
                new Button("ĐẶT GIÁ NGAY");

        btnPlaceBid.setMaxWidth(
                Double.MAX_VALUE
        );

        btnPlaceBid.setStyle("""
            -fx-background-color: #89b4fa;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 16px;
        """);

        // =================================================
        // AUTO BID
        // =================================================

        Button btnAutoBid =
                new Button("🤖 Cài Auto-Bid");

        btnAutoBid.setMaxWidth(
                Double.MAX_VALUE
        );

        btnAutoBid.setStyle("""
            -fx-background-color: #a6e3a1;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-font-size: 16px;
        """);

        // =================================================
        // BID HISTORY
        // =================================================

        Label historyTitle =
                new Label("📜 Bid History");

        historyTitle.setStyle("""
            -fx-text-fill: #f9e2af;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
        """);

        TextArea txtBidHistory =
                new TextArea();

        txtBidHistory.setEditable(false);

        txtBidHistory.setPrefHeight(200);

        txtBidHistory.setStyle("""
                                -fx-control-inner-background: #0b1020;
                                -fx-text-fill: white;
                                -fx-font-size: 14px;
                                """);

        bidArea.getChildren().addAll(

                new Label("GIÁ HIỆN TẠI") {{
                    setStyle("""
                        -fx-text-fill: #bac2de;
                        -fx-font-size: 15px;
                    """);
                }},

                lblCurrentPrice,

                new Region() {{
                    setMinHeight(15);
                }},

                txtBidAmount,

                btnPlaceBid,

                btnAutoBid,

                historyTitle,

                txtBidHistory
        );

        // =================================================
        // RIGHT PANEL
        // =================================================

        VBox logArea = new VBox(10);

        HBox.setHgrow(
                logArea,
                Priority.ALWAYS
        );

        Label lblLogTitle =
                new Label("📡 Live Activity");

        lblLogTitle.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
        """);

        TextArea txtLog =
                new TextArea(
                        ">>> Connecting live auction...\n"
                );

        txtLog.setEditable(false);

        txtLog.setStyle("""
            -fx-control-inner-background:
            #11111b;

            -fx-text-fill: white;
        """);

        VBox.setVgrow(
                txtLog,
                Priority.ALWAYS
        );

        logArea.getChildren().addAll(
                lblLogTitle,
                txtLog
        );

        // =================================================
        // ROOT
        // =================================================

        mainBox.getChildren().addAll(
                bidArea,
                logArea
        );

        layout.getChildren().addAll(
                header,
                mainBox
        );

        socketClient.joinRoom(auctionId);

        // =================================================
        // LOAD BID HISTORY
        // =================================================

        auctionService
                .getBidHistory(userToken, auctionId)
                .thenAccept(response -> {


                        try {
                                URI wsUri = new URI("ws://localhost:8080/auction?token=" + userToken);

                                liveWsClient = new WebSocketClient(wsUri) {

                                        @Override
                                        public void onOpen(ServerHandshake handshakedata) {
                                                Platform.runLater(() -> {txtLog.appendText(">>> Đã kết nối WebSocket thành công!\n");
                                                });

                                                String joinRequest = String.format("{\"type\":\"JOIN_AUCTION\",\"token\":\"%s\",\"data\":{\"auctionId\":\"%s\"}}", userToken, auctionId);

                                                send(joinRequest);
                                        }

                                        @Override
                                        public void onMessage(String message) {
                                                try {
                                                        JsonObject json = gson.fromJson(message, JsonObject.class);
                                                        String type = json.has("type") ? json.get("type").getAsString() : "";

                                                        Platform.runLater(() -> {
                                                                if (type.equals("BID_UPDATE")) {
                                                                        JsonObject data = json.getAsJsonObject("data");

                                                                        if (data != null && data.has("bid")) {
                                                                                JsonObject bidObj = data.getAsJsonObject("bid");

                                                                                if (bidObj != null && bidObj.has("amount")) {
                                                                                        BigDecimal amount = bidObj.get("amount").getAsBigDecimal();

                                                                                        lblCurrentPrice.setText(
                                                                                                String.format("%,,0f VNĐ", amount)
                                                                                        );
                                                                                        txtLog.appendText(">>> [HOT] Có người đặt giá: " + lblCurrentPrice.getText() + "!\n");
                                                                                }
                                                                        }
                                                                } else if (type.equals("VIEWER_UPDATE")) {
                                                                        JsonObject data = json.getAsJsonObject("data");
                                                                        if (data != null && data.has("viewerCount")) {
                                                                                txtLog.appendText(">>> Số người đang xem: " + data.get("viewerCount").getAsString() + "\n");
                                                                        }
                                                                } else if (type.equals("SUCCESS") || type.equals("ERROR")) {
                                                                        txtLog.appendText(">>> " + (json.has("message") ? json.get("message").getAsString() : "") + "\n");
                                                                }
                                                        });

                                                } catch (Exception ex) {
                                                        Platform.runLater(() -> txtLog.appendText(">>> Lỗi đọc dữ liệu WebSocket: " + ex.getMessage() + "\n"));
                                                }
                                        }

                                        @Override
                                        public void onClose(int code, String reason, boolean remote) {
                                                Platform.runLater(() -> txtLog.appendText(">>> WebSocket đã ngắt kết nối: " + reason + "\n"));
                                        }

                                        @Override
                                        public void onError(Exception ex) {
                                                Platform.runLater(() -> { txtLog.appendText(">>> Lỗi WebSocket: " + ex.getMessage() + "\n");
                                                });
                                        }
                                };

                        liveWsClient.connect();

                        } catch (Exception e) {
                        txtLog.appendText(">>> Không thể kết nối WebSocket: " + e.getMessage() + "\n");
                        }

        // =================================================
        // SOCKET CONNECT
        // =================================================

        socketClient.connect(

                userToken,
                message -> {
                    Platform.runLater(() -> {
                        txtLog.appendText(message + "\n");

                        try {
                                JsonObject json =
                                    gson.fromJson(message, JsonObject.class);

                                String type = json.get("type").getAsString();

                                if (type.equals("BID_UPDATE")) {

                                        JsonObject data = json.getAsJsonObject( "data");

                                        JsonObject bid = data.getAsJsonObject("bid");

                                        BigDecimal amount =
                                                bid.get("amount")
                                                .getAsBigDecimal();

                                        DecimalFormat moneydf = new DecimalFormat("#,###");

                                        lblCurrentPrice.setText(
                                                moneydf.format(amount) + " VND"
                                        );

                                        txtLog.appendText("🔥 Có giá mới: " + moneydf.format(amount) + " VND\n");

                                        String bidderName = "Unknown";

                                        if (bid.has("bidderUsername")) {

                                                bidderName =
                                                        bid.get("bidderUsername")
                                                        .getAsString();
                                        }

                                        txtBidHistory.appendText("👤 " + bidderName + " đặt " + moneydf.format(amount) + " VND\n");
                                }
                        } catch (Exception ignored) {
                        }

                    });
                }
        );

        // =================================================
        // PLACE BID
        // =================================================
        btnPlaceBid.setOnAction(e -> {

            try {

                BigDecimal bidAmount = new BigDecimal(txtBidAmount.getText().trim());

                if (liveWsClient != null && liveWsClient.isOpen()) {

                    String request = String.format("{\"type\":\"PLACE_BID\",\"token\":\"%s\",\"data\":{\"auctionId\":\"%s\",\"price\":%s}}", userToken, auctionId, bidAmount.toPlainString());

                    liveWsClient.send(request);

                    txtLog.appendText(">>> Bạn vừa đặt: " + String.format("%,.0f VNĐ", bidAmount.doubleValue()) + "\n");

                    txtBidAmount.clear();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa kết nối WebSocket!");
                }

            }catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá tiền sai định dạng!");

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Bid Error!");
            }

        });

        // =================================================
        // AUTO BID
        // =================================================

        btnAutoBid.setOnAction(e -> {

            TextInputDialog dialog =
                    new TextInputDialog();

            dialog.setTitle("Auto Bid");

            dialog.setHeaderText(
                    "Automatic bidding system"
            );

            dialog.setContentText(
                    "Maximum bid amount:"
            );

            dialog.showAndWait().ifPresent(max -> {

                try {

                        BigDecimal amount = new BigDecimal(max.trim());

                         auctionService
                                .createAutoBid(
                                    userToken,
                                    auctionId,
                                    amount)
                                .thenAccept(res -> {
                                        Platform.runLater(() -> {
                                                txtLog.appendText(
                                                "[AUTO BID] ON: "
                                                    + max
                                                    + " VND\n"
                                                );
                                        });
                                 })
                                .exceptionally(ex -> {
                                        Platform.runLater(() -> {
                                                showAlert(
                                                        Alert.AlertType.ERROR,
                                                        "AutoBid Error",
                                                        ex.getMessage()
                                                );
                                        });

                                        return null;
                                });

                } catch (Exception ex) {

                        showAlert(
                            Alert.AlertType.ERROR,
                            "AutoBid Error",
                            "Invalid amount"
                        );
                }
            });
        });
    });
        return layout;
    }

    //==============================================
    // 8. SEARCH
    //==============================================
    private VBox getSearchView() {
        VBox root = new VBox(20);

        root.setPadding(new Insets(25));

        Label title = new Label("🔍 Search Auctions");

        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #bac2de; -fx-font-weight: bold;");

        // ===== SEARCH BY AUCTION ID =====
        Label auctionLabel = new Label("Search By Auction ID");

        auctionLabel.setStyle("-fx-text-fill: #bac2de; -fx-font-size: 13px;");

        TextField auctionField = new TextField();
        
        auctionField.setPromptText("Enter Auction ID");

        auctionField.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-prompt-text-fill: #555; -fx-background-radius: 8; -fx-padding: 10;");

        Button auctionBtn = new Button("Search Auction");

        auctionBtn.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");

        VBox auctionResultBox = new VBox(10);

        VBox auctionBox = new VBox(10, auctionLabel, auctionField, auctionBtn, auctionResultBox);

        auctionBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 20; -fx-background-radius: 15;");

        // ===== SEARCH BY SELLER ID =====
        Label sellerLabel = new Label("Search By Seller ID");

        sellerLabel.setStyle("-fx-text-fill: #bac2de; -fx-font-size: 13px;");

        TextField sellerField = new TextField();

        sellerField.setPromptText("Enter Seller ID");

        sellerField.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-prompt-text-fill: #555; -fx-background-radius: 8; -fx-padding: 10;");

        Button sellerBtn = new Button("Search Seller");

        sellerBtn.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");

        VBox sellerResultBox = new VBox(10);

        VBox sellerBox = new VBox(10, sellerLabel, sellerField, sellerBtn, sellerResultBox);

        sellerBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 20; -fx-background-radius: 15;");

        // ===== ACTIONS =====
        auctionBtn.setOnAction(e -> {
                String auctionId = auctionField.getText().trim();

                if (auctionId.isBlank()) { showAlert(Alert.AlertType.ERROR, "Error", "Auction ID Required"); return; }

                auctionResultBox.getChildren().clear();

                Label loading = new Label("Searching...");

                loading.setStyle("-fx-text-fill: #8a9bb5;");

                auctionResultBox.getChildren().add(loading);

                auctionService.getAuctionById(auctionId)
                                .thenAccept(response -> Platform.runLater(() -> {
                                        auctionResultBox.getChildren().clear();
                                        try {
                                                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                                                auctionResultBox.getChildren().add(createAuctionCard(json));

                                        } catch (Exception ex) {
                                                Label err = new Label("Không tìm thấy auction.");

                                                err.setStyle("-fx-text-fill: #f38ba8;");

                                                auctionResultBox.getChildren().add(err);
                                        }
                                }))
                                .exceptionally(ex -> { Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage())); return null; });
                        });

        sellerBtn.setOnAction(e -> {
                String sellerId = sellerField.getText().trim();

                if (sellerId.isBlank()) { showAlert(Alert.AlertType.ERROR, "Error", "Seller ID required"); return; }

                sellerResultBox.getChildren().clear();

                Label loading = new Label("Searching...");

                loading.setStyle("-fx-text-fill: #8a9bb5;");

                sellerResultBox.getChildren().add(loading);

                auctionService.getAuctionBySeller(sellerId)
                                .thenAccept(response -> Platform.runLater(() -> {
                                        sellerResultBox.getChildren().clear();
                                        try {
                                                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                                                JsonArray arr = json.getAsJsonArray("content");

                                                if (arr == null || arr.size() == 0) {
                                                        Label empty = new Label("Không có kết quả.");

                                                        empty.setStyle("-fx-text-fill: #8a9bb5;");

                                                        sellerResultBox.getChildren().add(empty);

                                                        return;
                                                }
                                                for (JsonElement el : arr) {
                                                        sellerResultBox.getChildren().add(createAuctionCard(el.getAsJsonObject()));
                                                }

                                        } catch (Exception ex) {
                                                Label err = new Label("Lỗi khi tải dữ liệu.");

                                                err.setStyle("-fx-text-fill: #f38ba8;");

                                                sellerResultBox.getChildren().add(err);
                                        }
                                }))
                                .exceptionally(ex -> { Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage())); return null; });
                        });

        ScrollPane scroll = new ScrollPane(new VBox(20, auctionBox, sellerBox));

        scroll.setFitToWidth(true);

        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.getChildren().addAll(title, scroll);

        return root;
        }

    //===============================================================
    //9. NOTIFICATIONS
    //===============================================================
    private ScrollPane getNotificationView() {

        VBox layout = new VBox(20);

        layout.setPadding(new Insets(20));

        // =========================
        // TITLE
        // =========================

        Label title =
                new Label("🔔 Notifications");

        title.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        // =========================
        // BUTTONS
        // =========================

        HBox topBar =
                new HBox(15);

        Button btnRefresh =
                new Button("🔄 Refresh");

        Button btnReadAll =
                new Button("✅ Read All");

        btnRefresh.setStyle("""
            -fx-background-color: #89b4fa;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        btnReadAll.setStyle("""
            -fx-background-color: #a6e3a1;
            -fx-text-fill: black;
            -fx-font-weight: bold;
        """);

        topBar.getChildren().addAll(
                btnRefresh,
                btnReadAll
        );

        // =========================
        // TEXT AREA
        // =========================

        TextArea area =
                new TextArea();

        area.setEditable(false);

        area.setPrefHeight(500);

        area.setStyle("""
            -fx-control-inner-background:
            rgba(20,20,30,0.95);

            -fx-text-fill: white;
        """);

        // =========================
        // LOAD FUNCTION
        // =========================

        Runnable loadNotifications = () -> {

            notificationService
                    .getNotifications(userToken)

                    .thenAccept(response -> {

                        Platform.runLater(() -> {

                            area.setText(
                                    response.body()
                            );

                        });

                    })

                    .exceptionally(ex -> {

                        Platform.runLater(() -> {

                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Notification Error",
                                    ex.getMessage()
                            );

                        });

                        return null;
                    });
        };

        // =========================
        // INITIAL LOAD
        // =========================

        loadNotifications.run();

        // =========================
        // REFRESH
        // =========================

        btnRefresh.setOnAction(e -> {

            loadNotifications.run();

        });

        // =========================
        // READ ALL
        // =========================

        btnReadAll.setOnAction(e -> {

            notificationService
                    .readAll(userToken)

                    .thenAccept(response -> {

                        Platform.runLater(() -> {

                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Notifications",
                                    "All notifications marked as read"
                            );

                            loadNotifications.run();

                        });

                    })

                    .exceptionally(ex -> {

                        Platform.runLater(() -> {

                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "API Error",
                                    ex.getMessage()
                            );

                        });

                        return null;
                    });
        });

        // =========================
        // ROOT
        // =========================

        layout.getChildren().addAll(
                title,
                topBar,
                area
        );

        ScrollPane scroll =
                new ScrollPane(layout);

        scroll.setFitToWidth(true);

        scroll.setStyle("""
            -fx-background:
            transparent;

            -fx-background-color:
            transparent;
        """);

        return scroll;
    }

    private void renderDynamicFields(String type) {

        dynamicFieldsBox.getChildren().clear();

        dynamicInputs.clear();

        switch (type) {

                case "ELECTRONICS" ->
                        renderElectronicsFields();

                case "FASHION" ->
                        renderFashionFields();

                case "JEWELRY" ->
                        renderJewelryFields();

                case "VEHICLE" ->
                        renderVehicleFields();

                case "BOOK" ->
                        renderBookFields();

                case "ART" ->
                        renderArtFields();

                case "COLLECTIBLE" ->
                        renderCollectibleFields();
        }
     }

        private void addTextField(String labelText, String key) {

                Label label = new Label(labelText);

                label.setStyle("""
                        -fx-text-fill: white;
                        -fx-font-size: 13px;
                        """);

                TextField field = createField(labelText);

                dynamicInputs.put(key, field);

                dynamicFieldsBox.getChildren().addAll(label, field);
        }

        private void renderElectronicsFields() {

                addTextField("Brand", "brand");

                addTextField("Model", "model");

                addTextField("Color", "color");

                addTextField("Storage", "storage");

                addTextField(
                        "Condition Status",
                        "conditionStatus"
                );

                addTextField(
                        "Warranty Months",
                        "warrantyMonths"
                );
        }

        private void renderFashionFields() {

                addTextField("Brand", "brand");

                addTextField("Model", "model");

                addTextField("Size", "size");

                addTextField("Color", "color");

                addTextField("Material", "material");
        }

        private void renderJewelryFields() {

                addTextField("Brand", "brand");

                addTextField("Model", "model");

                addTextField("Material", "material");

                addTextField("Weight", "weight");
        }

        private void renderVehicleFields() {

                addTextField("Brand", "brand");

                addTextField("Model", "model");

                addTextField("Fuel Type", "fuelType");

                addTextField("Color", "color");

                addTextField("Mileage", "mileage");

                addTextField("Year", "year");
        }

        private void renderBookFields() {

                addTextField("Author", "author");

                addTextField("Publisher", "publisher");

                addTextField("Publish Year", "publishYear");
        }

        private void renderArtFields() {

                addTextField("Artist", "artist");

                addTextField("Style", "style");
        }

        private void renderCollectibleFields() {

                addTextField("Category", "category");

                addTextField("Rarity", "rarity");

                addTextField(
                        "Production Year",
                        "productionYear"
                );
        }

        private void loadWallet() {

                try {

                        walletService
                                .getWallet(userToken)
                                .thenAccept(response -> {

                                Platform.runLater(() -> {

                                        try {

                                        ObjectMapper mapper =
                                                new ObjectMapper();

                                        JsonNode root =
                                                mapper.readTree(
                                                        response.body()
                                                );

                                        double balance =
                                                root.get("balance")
                                                        .asDouble();

                                        lbBalance.setText(
                                                "VNĐ" + balance
                                        );

                                        } catch (Exception ex) {

                                        ex.printStackTrace();
                                        }
                                });
                                });

                } catch (Exception ex) {

                        ex.printStackTrace();
                }
        }

        private void loadTransactions() {

                try {

                        walletService
                                .getTransactions(userToken)
                                .thenAccept(response -> {

                                Platform.runLater(() -> {

                                        try {

                                        ObjectMapper mapper =
                                                new ObjectMapper();

                                        JsonNode root =
                                                mapper.readTree(
                                                        response.body()
                                                );

                                        JsonNode content =
                                                root.get("content");

                                        StringBuilder sb =
                                                new StringBuilder();

                                        for (JsonNode tx : content) {

                                                sb.append("Type: ")
                                                .append(
                                                tx.get("transactionType")
                                                        .asText()
                                                )
                                                .append("\n");

                                                sb.append("Amount: ")
                                                .append(
                                                tx.get("amount")
                                                        .asText()
                                                )
                                                .append("\n\n");
                                        }

                                        txTransactions.setText(
                                                sb.toString()
                                        );

                                        } catch (Exception ex) {

                                        ex.printStackTrace();
                                        }
                                });
                                });

                } catch (Exception ex) {

                        ex.printStackTrace();
                }
        }

        // --- CÁC HÀM TIỆN ÍCH UI ---
        private void fetchWalletBalance(Label l) { HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080")).header("Authorization", "Bearer " + userToken).GET().build(); httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> { if (res.statusCode() == 200) l.setText(String.format("%,.0f VNĐ", new BigDecimal((gson.fromJson(res.body(), JsonObject.class).get("balance").getAsString())))); })); }
        private void handleRememberMe(String u, String p, boolean isR) { try(PrintWriter w = new PrintWriter(new FileWriter(REMEMBER_FILE))) { if(isR){w.println(u);w.println(p);} else w.print(""); } catch(Exception ignored){} }
        private void loadRememberedUser(TextField u, PasswordField p, CheckBox c) { try(BufferedReader r = new BufferedReader(new FileReader(REMEMBER_FILE))) { String a=r.readLine(), b=r.readLine(); if(a!=null&&b!=null){u.setText(a);p.setText(b);c.setSelected(true);} } catch(Exception ignored){} }
        private TextField createField(String p) { TextField f = new TextField(); f.setPromptText(p); styleInputField(f); return f; }
        private void styleInputField(TextField f) { f.setStyle("-fx-background-color: #313244; -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8;"); }
        private void showAlert(Alert.AlertType t, String title, String c) { Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(c); a.showAndWait(); }

        private Label createProfileLabel(String text){

                Label label = new Label(text);

                label.setStyle("""
                                -fx-text-fill: #bac2de;
                                -fx-font-size: 16px;
                                -fx-font-weight: bold;
                        """);      
                return label; 
        }

        public static void main(String[] args) { launch(args);}
}
