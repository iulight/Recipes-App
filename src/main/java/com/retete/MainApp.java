package com.retete;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/retete/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 720);
        scene.getStylesheets().add(getClass().getResource("/com/retete/style.css").toExternalForm());
        primaryStage.setTitle("🍽️ Rețete Culinare & Listă Cumpărături");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
