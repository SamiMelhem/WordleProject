package com.example.wordlegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        File fxmlFile = new File("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\Wordle.fxml");
        URL fxmlUrl = fxmlFile.toURI().toURL();
        Parent root = FXMLLoader.load(fxmlUrl);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Wordle Game");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}