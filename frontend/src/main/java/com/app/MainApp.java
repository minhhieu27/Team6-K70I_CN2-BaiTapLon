package com.app; // Dòng này là bắt buộc
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        AuctionLogic logic = new AuctionLogic(primaryStage);
        logic.startSystem();
    }
    public static void main(String[] args) { launch(args); }
}