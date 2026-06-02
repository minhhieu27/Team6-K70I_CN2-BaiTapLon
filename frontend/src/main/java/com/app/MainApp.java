package com.app;

import com.app.service.AuctionService;
import com.app.service.AuthService;
import com.app.service.ImageService;
import com.app.service.NotificationService;
import com.app.service.UserService;
import com.app.service.WalletService;
import com.app.socket.AuctionSocketClient;
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
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

@RequiredArgsConstructor
public class MainApp extends Application {

    private final AuctionSocketClient socketClient;

    private final AuctionService auctionService;

    private final UserService userService;

    private final WalletService walletService;

    private final NotificationService notificationService;

    private final ImageService imageService;

    private WebSocketClient liveWsClient;

    private int currentPage = 0;

    private final int PAGE_SIZE = 10;

    private Stage primaryStage;
    private StackPane contentArea;
    private final String REMEMBER_FILE = "remember_me.txt";

    // Ảnh nền duy nhất cực xịn
    private final String BG_DASHBOARD = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1920&auto=format&fit=crop";

    private String currentUsername  = "";
    private String currentUserId = "";
    private String userToken = "";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

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

                    socketClient.connect(token, message -> {System.out.println("[WS]: " + message);});

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

        sidebar.getChildren().addAll(logo, btnCatalog, btnSearch, btnProfile, btnWallet, btnPostItem, spacer, btnLogout, btnNotify, btnSeller);

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
                                        JsonArray arr = gson.fromJson(response.body(), JsonArray.class);

                                        for (JsonElement el : arr) {

                                        JsonObject auction = el.getAsJsonObject();

                                        VBox card = createAuctionCard(auction);

                                        Button btnDelete = new Button("🗑 Delete");

                                        btnDelete.setOnAction(e -> {

                                                auctionService
                                                        .deleteAuction(userToken, auction.get("auctionId").getAsString());
                                        });

                                        card.getChildren().add(btnDelete);

                                        auctionList.getChildren().add(card);
                                        }

                                } catch (Exception ex) {

                                        ex.printStackTrace();
                                }
                        });
                });

        layout.getChildren().addAll(title, scroll);

        return layout;
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

        grid.add(lbFullName, 0, 1);
        grid.add(txtFullName, 1, 1);

        grid.add(lbPhone, 0, 2);
        grid.add(txtPhone, 1, 2);

        grid.add(lbAddress, 0, 3);
        grid.add(txtAddress, 1, 3);

        grid.add(lbEmail, 0, 1);
        grid.add(txtEmail, 1, 4);

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
        // RESET PASSWORD PANEL
        // =====================================================

        VBox resetPanel = new VBox(15);

        resetPanel.setPadding(new Insets(25));

        resetPanel.setStyle("""
                            -fx-background-color: rgba(255,255,255,0.08);
                            -fx-background-radius: 20;
                        """);

        Label resetTitle = new Label("📧 Reset Password");

        resetTitle.setStyle("""
                            -fx-text-fill: #94e2d5;
                            -fx-font-size: 22px;
                            -fx-font-weight: bold;
                        """);

        TextField txtResetEmail = createField("Enter Email");

        Button btnResetPassword = new Button("📨 RESET PASSWORD");

        btnResetPassword.setStyle("""
                                    -fx-background-color: linear-gradient(to right, #94e2d5, #89dceb);
                                    -fx-text-fill: white;
                                    -fx-font-size: 15px;
                                    -fx-font-weight: bold;
                                    -fx-background-radius: 12;
                                    -fx-padding: 10 20 10 20;
                            """);

        btnResetPassword.setOnAction(e -> {

            userService
                    .resetPassword(
                            txtResetEmail.getText()
                    )
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Reset Password",
                                    "Reset request sent"
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

        resetPanel.getChildren().addAll(
                resetTitle,
                txtResetEmail,
                btnResetPassword
        );

        // =====================================================
        // ROOT
        // =====================================================

        layout.getChildren().addAll(
                title,
                profilePanel,
                btnSave,
                passwordPanel,
                resetPanel
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

        Label lbBalance =
                new Label("Loading...");

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

        TextArea txArea =
                new TextArea();

        txArea.setEditable(false);

        txArea.setPrefHeight(250);

        txArea.setStyle("""
            -fx-control-inner-background:
            rgba(20,20,30,0.95);

            -fx-text-fill: white;
        """);

        transactionPanel.getChildren().addAll(
                txTitle,
                txArea
        );

        // =========================
        // LOAD WALLET
        // =========================

        walletService
                .getWallet(userToken)

                .thenAccept(response -> {

                    Platform.runLater(() -> {

                        try {

                            JsonObject obj =
                                    gson.fromJson(
                                            response.body(),
                                            JsonObject.class
                                    );

                            if (obj.has("balance")) {

                                lbBalance.setText(
                                        "$" +
                                        obj.get("balance")
                                                .getAsString()
                                );
                            }

                        } catch (Exception ignored) {
                        }

                    });

                });

        // =========================
        // LOAD TRANSACTIONS
        // =========================

        walletService
                .getTransactions(userToken)

                .thenAccept(response -> {

                    Platform.runLater(() -> {

                        txArea.setText(
                                response.body()
                        );

                    });

                });

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
                                                        JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                                                        JsonArray content = json.getAsJsonArray("content");

                                                        for (JsonElement el : content){
                                                                grid.getChildren().add(createAuctionCard(el.getAsJsonObject()));
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

                    imageService
                            .uploadImage(file.toPath())
                            .thenAccept(response -> {
                                Platform.runLater(() -> {
                                    uploadedImage[0] =
                                            response.body();
                                    showAlert(
                                            Alert.AlertType.INFORMATION,
                                            "Upload",
                                            "Image uploaded successfully"
                                    );
                                });
                            })
                            .exceptionally(ex -> {
                                Platform.runLater(() -> {
                                    showAlert(
                                            Alert.AlertType.ERROR,
                                            "Upload Error",
                                            ex.getMessage()
                                    );
                                });

                                return null;
                            });

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
            rgba(255,255,255,0.08);

            -fx-text-fill: white;

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

                long startPrice =
                        Long.parseLong(
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

                String jsonPayload =
                        String.format(
                                """
                                {
                                    "title":"%s",
                                    "item":{
                                        "itemType":"%s",
                                        "itemName":"%s",
                                        "description":"%s",
                                        "imageUrl":"%s"
                                    },
                                    "startPrice":%d
                                }
                                """,

                                txtTitle.getText(),

                                cbType.getValue(),

                                txtItemName.getText(),

                                txtDesc.getText(),

                                imgUrl,

                                startPrice
                        );

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

            } catch (Exception ex) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Invalid Price",
                        "Giá tiền không hợp lệ"
                );
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
                btnChooseImage,
                lbImage,
                txtDesc,
                btnSubmit
        );

        // =================================================
        // ROOT
        // =================================================

        layout.getChildren().addAll(
                title,
                formBox
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

        LocalDateTime end = LocalDateTime.parse(endTime);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd//MM/yyyy HH:mm:ss");

        Label lblEndTime = new Label("Kết thúc: " + end.format(fmt));

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

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();

        Label lblTitle =
                new Label(title);

        lblTitle.setWrapText(true);

        lblTitle.setStyle("""
            -fx-text-fill: white;

            -fx-font-size: 18px;

            -fx-font-weight: bold;
        """);

        Label lblPrice =
                new Label(
                        String.format(
                                "%,d VND",
                                Long.parseLong(price)
                        )
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

        Label lblCurrentPrice =
                new Label(
                        String.format(
                                "%,d VND",
                                Long.parseLong(
                                        startPrice
                                                .split("\\.")[0]
                                )
                        )
                );

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

        TextArea txtHistory =
                new TextArea();

        txtHistory.setEditable(false);

        txtHistory.setPrefHeight(180);

        txtHistory.setStyle("""
            -fx-control-inner-background:
            rgba(20,20,30,0.95);

            -fx-text-fill: white;
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

                new Region() {{
                    setMinHeight(10);
                }},

                historyTitle,

                txtHistory
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
                                URI wsUri = new URI("wss://team6-k70i-cn2-baitaplon.onrender.com/ws/auction?token=" + userToken);

                                liveWsClient = new WebSocketClient(wsUri) {

                                        @Override
                                        public void onOpen(ServerHandshake handshakedata) {
                                                Platform.runLater(() -> {txtLog.appendText(">>> Đã kết nối WebSocket thành công!\n");
                                                });

                                                String joinRequest = String.format("""
                                                        {"type":"JOIN_AUCTION","data":{"auctionId":"%s"}}""",
                                                        auctionId
                                                );

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
                                                                                        String amount = bidObj.get("amount").getAsString();

                                                                                        lblCurrentPrice.setText(
                                                                                                String.format("%,d VNĐ", Long.parseLong(amount.split("\\.")[0]))
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

                                        String amount = bid.get("amount").getAsString();

                                        lblCurrentPrice.setText(String.format("%,d VND", BigDecimal.valueOf(Long.parseLong(amount))));
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

                BigDecimal bidAmount = BigDecimal.valueOf(Long.parseLong(txtBidAmount.getText().trim()));

                if (liveWsClient != null && liveWsClient.isOpen()) {

                    String request = String.format("""
                        {"type":"PLACE_BID","data":{\"auctionId":"%s","price":%s}
                        }""",
                            auctionId,
                            bidAmount
                    );

                    liveWsClient.send(request);

                    txtLog.appendText(">>> Bạn vừa đặt: " + String.format("%,d VNĐ", bidAmount.doubleValue()) + "\n");

                    txtBidAmount.clear();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa kết nối WebSocket!");
                }

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá tiền sai định dạng!");
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

                        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(max));

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
    private VBox getSearchView(){

        VBox root = new VBox(20);

        root.setPadding(new Insets(25));

        Label title = new Label("🔍 Search Auctions");

        title.setStyle("""
                        -fx-font-size: 28px;
                        -fx-text-fill: #bac2de;
                        -fx-font-weight: bold;
                    """);

        Label auctionLabel = new Label("Search By Auction ID");

        auctionLabel.setStyle("-fx-text-fill: #bac2de;");

        TextField auctionField = new TextField();

        auctionField.setPromptText("Enter Auction ID");

        Button auctionBtn = new Button("Search Auction");

        TextArea auctionResult = new TextArea();

        auctionResult.setPrefHeight(150);

        Label sellerLabel = new Label("Search By Seller ID");

        sellerLabel.setStyle("-fx-text-fill: #bac2de;");

        TextField sellerField = new TextField();

        sellerField.setPromptText("Enter Seller ID");

        Button sellerBtn = new Button("Search Seller");

        TextArea sellerResult = new TextArea();

        sellerResult.setPrefHeight(150);

        auctionBtn.setOnAction(e -> {

            String auctionId = auctionField.getText();

            if (auctionId.isBlank()){
                showAlert(Alert.AlertType.ERROR, "Error", "Auction ID Required");

                return;
            }

            auctionService
                    .getAuctionById(auctionId)
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            auctionResult.setText(
                                response.body());
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

        sellerBtn.setOnAction(e -> {
            String sellerId = sellerField.getText();

            if (sellerId.isBlank()){
                showAlert(Alert.AlertType.ERROR, "Error", "Seller ID required");

                return;
            }

            auctionService
                .getAuctionBySeller(sellerId)
                .thenAccept(response ->{
                    Platform.runLater(() ->{
                        sellerResult.setText(response.body());
                    });
                })
                .exceptionally(ex ->{
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "API Error", ex.getMessage());
                    });

                    return null;
                });
        });

        VBox auctionBox = new VBox(10, auctionLabel, auctionField, auctionBtn, auctionResult);

        auctionBox.setStyle("""
                            -fx-background-color: rgba(255, 255, 255, 0.08);
                            -fx-padding: 20;
                            -fx-background-radius: 15;
                            """);
        
        VBox sellerBox = new VBox(10, sellerLabel, sellerField, sellerBtn, sellerResult);

        sellerBox.setStyle("""
                            -fx-background-color: rgba(255, 255, 255, 0.08);
                            -fx-padding: 20;
                            -fx-background-radius: 15;
                            """);
                        
        root.getChildren().addAll(title, auctionBox, sellerBox);

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

    // --- CÁC HÀM TIỆN ÍCH UI ---
    private void fetchWalletBalance(Label l) { HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wallet")).header("Authorization", "Bearer " + userToken).GET().build(); httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> { if (res.statusCode() == 200) l.setText(String.format("%,d VNĐ", Long.parseLong(gson.fromJson(res.body(), JsonObject.class).get("balance").getAsString().split("\\.")[0]))); })); }
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
                        -fx-font-size: bold;
                    """);      
        return label; 
    }

    public static void main(String[] args) { launch(args);}
}
