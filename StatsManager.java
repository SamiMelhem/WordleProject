package com.example.wordlegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StatsManager {
    @FXML
    private Label games_played;
    @FXML
    private Label win_percent;
    @FXML
    private Label num_current_streak;
    @FXML
    private Label num_max_streak;
    @FXML
    private ProgressBar one_wins_progress_bar;
    @FXML
    private ProgressBar two_wins_progress_bar;
    @FXML
    private ProgressBar three_wins_progress_bar;
    @FXML
    private ProgressBar four_wins_progress_bar;
    @FXML
    private ProgressBar five_wins_progress_bar;
    @FXML
    private ProgressBar six_wins_progress_bar;
    @FXML
    private Label one_wins;
    @FXML
    private Label two_wins;
    @FXML
    private Label three_wins;
    @FXML
    private Label four_wins;
    @FXML
    private Label five_wins;
    @FXML
    private Label six_wins;
    private int wins;
    private int totalGamesPlayed;
    private int currentStreak;
    private int maxStreak;
    private int maxGuesses;
    private int winRatio;
    private int[] guessDistribution;

    public StatsManager(){
        wins = 0;
        totalGamesPlayed = 0;
        currentStreak = 0;
        maxStreak = 0;
        maxGuesses = 0;
        winRatio = 0;
        guessDistribution = new int[]{0, 0, 0, 0, 0, 0};
    }
    public void generateStats() throws IOException {
        Path path = Paths.get("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\finished_games.txt");
        List<String> lines = Files.readAllLines(path);

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("W")){
                wins++;
                currentStreak++;
                if (currentStreak > maxStreak) { maxStreak = currentStreak; }
                int indexOfGuessDistribution = Integer.parseInt(String.valueOf(lines.get(i).charAt(0)));
                guessDistribution[indexOfGuessDistribution-1]++;
            }
            else {
                currentStreak = 0;
            }
            totalGamesPlayed++;
        }

        winRatio = (int) (100.0*((double) wins / (double) totalGamesPlayed));
        for (int amountOfGuesses : guessDistribution) {
            if (amountOfGuesses > maxGuesses){
                maxGuesses = amountOfGuesses;
            }
        }
    }

    public void updateStatsWindow(){
        // Update labels with the generated statistics
        games_played.setText(String.valueOf(totalGamesPlayed));
        win_percent.setText(String.valueOf(winRatio));
        num_current_streak.setText(String.valueOf(currentStreak));
        num_max_streak.setText(String.valueOf(maxStreak));

        // Update guess distribution labels and progress bars if needed
        Label[] guessLabels = new Label[]{one_wins, two_wins, three_wins, four_wins, five_wins, six_wins};

        ProgressBar[] progressBars = new ProgressBar[] {one_wins_progress_bar, two_wins_progress_bar,
            three_wins_progress_bar, four_wins_progress_bar, five_wins_progress_bar, six_wins_progress_bar};

        for (int i = 0; i < guessDistribution.length; i++) {
            guessLabels[i].setText(String.valueOf(guessDistribution[i]));
            progressBars[i].setProgress((double) guessDistribution[i] / maxGuesses);
        }
    }

    public void showStatsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            File fxmlFile = new File("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\StatsWindow.fxml");
            URL fxmlUrl = fxmlFile.toURI().toURL();
            loader.setLocation(fxmlUrl);

            Parent root = loader.load();

            // After loading, now retrieve the controller and generateStats & updateStatsWindow
            StatsManager controller = loader.getController();
            controller.generateStats();
            controller.updateStatsWindow();

            Stage statsStage = new Stage();
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.setTitle("Total Game Statistics");
            statsStage.setScene(new Scene(root));
            statsStage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
