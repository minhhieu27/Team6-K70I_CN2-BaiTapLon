package com.app;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;

public class AuctionLogic {
    private final Stage primaryStage;
    private final AuthService authService = new AuthService();
    private final WalletService walletService = new WalletService();
    private SocketClient socketClient;
    private final Gson gson = new Gson();
    private String userToken = "";

    public AuctionLogic(Stage stage) { this.primaryStage = stage; }

    public void startSystem() { showLogin(); primaryStage.show(); }

    private String glassStyle = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 20; -fx-padding: 30; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 20;";

    public void showLogin() {
        VBox box = new VBox(15); box.setAlignment(Pos.CENTER); box.setStyle(glassStyle);
        TextField user = new TextField(); user.setPromptText("Username");
        PasswordField pass = new PasswordField(); pass.setPromptText("Password");
        Button btn = new Button("ĐĂNG NHẬP");
        
        btn.setOnAction(e -> {
            authService.login(user.getText(), pass.getText()).thenAccept(res -> Platform.runLater(() -> {
                if (res.statusCode() == 200) {
                    this.userToken = gson.fromJson(res.body(), JsonObject.class).get("token").getAsString();
                    showDashboard();
                } else {
                    System.out.println("Lỗi đăng nhập: " + res.body());
                }
            })).exceptionally(ex -> { System.out.println("Server ngỏm!"); return null; });
        });
        
        box.getChildren().addAll(new Label("LOGIN") {{ setTextFill(Color.WHITE); setFont(Font.font("Arial", FontWeight.BOLD, 20)); }}, user, pass, btn);
        StackPane root = new StackPane(box); root.setStyle("-fx-background-color: #1a1a2e;");
        primaryStage.setScene(new Scene(root, 400, 400));
    }

    public void showDashboard() {
        VBox root = new VBox(20); root.setAlignment(Pos.CENTER); root.setStyle(glassStyle);
        Button btnCatalog = new Button("SÀN ĐẤU GIÁ"); btnCatalog.setOnAction(e -> showCatalog());
        Button btnWallet = new Button("VÍ TIỀN"); btnWallet.setOnAction(e -> showWallet());
        
        root.getChildren().addAll(new Label("MENU CHÍNH") {{ setTextFill(Color.WHITE); }}, btnCatalog, btnWallet);
        StackPane base = new StackPane(root); base.setStyle("-fx-background-color: #1a1a2e;");
        primaryStage.setScene(new Scene(base, 600, 400));
    }

    // --- PHASE 3: HIỂN THỊ CHỢ ĐẤU GIÁ ---
    public void showCatalog() {
        TableView<JsonObject> table = new TableView<>();
        TableColumn<JsonObject, String> colId = new TableColumn<>("Mã SP");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("auctionId").getAsString()));
        TableColumn<JsonObject, String> colName = new TableColumn<>("Tên sản phẩm");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("title").getAsString()));
        table.getColumns().addAll(colId, colName);

        // Nút vào Live Room
        Button btnEnter = new Button("VÀO PHÒNG ĐẤU GIÁ");
        btnEnter.setOnAction(e -> {
            JsonObject selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showLiveRoom(selected.get("auctionId").getAsString());
        });

        Button back = new Button("Trở về"); back.setOnAction(e -> showDashboard());

        // Lấy dữ liệu API
        HttpClient.newHttpClient().sendAsync(
            HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/auctions")).header("Authorization", "Bearer " + userToken).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenAccept(res -> Platform.runLater(() -> {
            if(res.statusCode() == 200) {
                JsonObject json = gson.fromJson(res.body(), JsonObject.class);
                if(json.has("content")) { // Phân trang của Spring Boot
                    for(JsonElement el : json.getAsJsonArray("content")) table.getItems().add(el.getAsJsonObject());
                } else if(json.isJsonArray()) {
                    for(JsonElement el : json.getAsJsonArray()) table.getItems().add(el.getAsJsonObject());
                }
            }
        }));

        VBox root = new VBox(10, new Label("SÀN ĐẤU GIÁ") {{ setTextFill(Color.WHITE); }}, table, new HBox(10, btnEnter, back));
        root.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 20;");
        primaryStage.setScene(new Scene(root, 600, 500));
    }

    // --- PHASE 4: PHÒNG ĐẤU GIÁ LIVE (SOCKET) ---
    public void showLiveRoom(String auctionId) {
        TextArea logArea = new TextArea(); logArea.setEditable(false);
        TextField txtPrice = new TextField(); txtPrice.setPromptText("Nhập giá tiền...");
        Button btnBid = new Button("BID");

        try {
            socketClient = new SocketClient();
            socketClient.connect("localhost", 9999);
            socketClient.listen(msg -> Platform.runLater(() -> logArea.appendText("> " + msg + "\n")));
            logArea.appendText("Đã kết nối vào phòng: " + auctionId + "\n");
        } catch (Exception ex) {
            logArea.appendText("Lỗi kết nối Socket: Không tìm thấy Server ở cổng 9999\n");
        }

        btnBid.setOnAction(e -> {
            try {
                double price = Double.parseDouble(txtPrice.getText());
                socketClient.placeBid(auctionId, price);
                txtPrice.clear();
            } catch(Exception ex) { logArea.appendText("Giá không hợp lệ!\n"); }
        });

        Button back = new Button("Thoát");
        back.setOnAction(e -> {
            try { if(socketClient != null) socketClient.close(); } catch(Exception ignored){}
            showCatalog();
        });

        VBox root = new VBox(10, new Label("PHÒNG LIVE - MÃ: " + auctionId) {{ setTextFill(Color.WHITE); }}, logArea, new HBox(10, txtPrice, btnBid), back);
        root.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 20;");
        primaryStage.setScene(new Scene(root, 600, 400));
    }

    public void showWallet() {
        Label bal = new Label("Loading..."); bal.setTextFill(Color.WHITE);
        walletService.getWallet(userToken).thenAccept(res -> Platform.runLater(() -> {
            if(res.statusCode() == 200) bal.setText("Số dư: " + gson.fromJson(res.body(), JsonObject.class).get("balance").getAsString());
        }));
        Button back = new Button("Back"); back.setOnAction(e -> showDashboard());
        VBox root = new VBox(10, new Label("VÍ TIỀN") {{ setTextFill(Color.WHITE); }}, bal, back);
        root.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 20;");
        primaryStage.setScene(new Scene(root, 400, 300));
    }
}