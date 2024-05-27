package com.example.wordlegame;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoard {
    @FXML
    private GridPane wordleGrid;
    @FXML
    private VBox keyboard;
    private CurrentWord currentWord;
    private StatsManager statsManager;
    private int currentAttempt = 0;

    public GameBoard() {
        currentWord = new CurrentWord();
        statsManager = new StatsManager();
    }
    @FXML
    protected void handleResetGame(javafx.event.ActionEvent event){
        resetGame();
    }
    @FXML
    protected void handleLoadGame(javafx.event.ActionEvent event){
        try {
            showLoadGameAlert();
            loadGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoadGameAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Loading Game");
        alert.setHeaderText(null);
        alert.setContentText("Loading the most previous game!");
        alert.showAndWait();
    }

    public void loadGame() throws IOException{
        Path path = Paths.get("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\saved_games.txt");
        List<String> lines = Files.readAllLines(path);

        if (lines.isEmpty()) {
            fileEmptyAlert();
            return;
        }

        // Clear the current game state if necessary
        resetGame();

        // Set the secret word and currentAttempt
        currentWord.setSecretWord(lines.get(0));
        currentAttempt = Integer.parseInt(lines.get(4));

        // Arrays needed for building the game from saved_games.txt
        String[] lettersOfWords = lines.get(1).replace(" ", "").split("");
        String[] cellTypes = lines.get(2).split(" ");
        String[] keyTypes = lines.get(3).split(" ");

        // Set the current word of currentWord if necessary
        if (lettersOfWords.length % 5 != 0) {
            String wordsCombined = String.join("",lettersOfWords);
            currentWord.setCurrentWord(wordsCombined.substring(wordsCombined.length()-(lettersOfWords.length % 5), wordsCombined.length()));
        }

        for (int i = 0; i < wordleGrid.getChildren().size(); i++){
            Node node = wordleGrid.getChildren().get(i);
            if (node instanceof TextField) {
                TextField cell = (TextField) node;
                String styleClassName = getStyleClassFromCellColor(cellTypes[i]);
                cell.getStyleClass().removeAll("wordle-cell");
                cell.getStyleClass().add(styleClassName);
                if (!styleClassName.equals("wordle-cell"))
                    cell.setText(lettersOfWords[i]);
            }
        }

        Node[] rows = new Node[]{keyboard.getChildren().get(0), keyboard.getChildren().get(1), keyboard.getChildren().get(2)};
        ArrayList<Node> keys = new ArrayList<>();
        for (Node row : rows){
            keys.addAll(((HBox) row).getChildren());
        }
        for (int i = 0; i < keys.size(); i++){
            Node key = keys.get(i);
            String styleClassName = getStyleClassFromKeyColor(keyTypes[i].split("-")[1]);
            key.getStyleClass().removeAll("keyboard-button");
            key.getStyleClass().add(styleClassName);
        }

        // Delete the loaded in game from the file
        lines.subList(0, 5).clear();  // Remove the first five lines representing the loaded game
        Files.write(path, lines); // Write the updated list back to the file
    }

    private String getStyleClassFromCellColor(String color) {
        if (color.equals("green")) return "green-wordle-cell";
        if (color.equals("yellow")) return "yellow-wordle-cell";
        if (color.equals("grey")) return "grey-wordle-cell";
        if (color.equals("updated")) return "update-wordle-cell";
        return "wordle-cell"; // if the word is "default"
    }

    private String getStyleClassFromKeyColor(String color) {
        if (color.equals("green")) return "green-keyboard-button";
        if (color.equals("yellow")) return "yellow-keyboard-button";
        if (color.equals("grey")) return "grey-keyboard-button";
        return "keyboard-button"; // if the word is "default"
    }

    private void fileEmptyAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No saved games");
        alert.setHeaderText(null);
        alert.setContentText("You have no saved games!");
        alert.showAndWait();
    }

    @FXML
    protected void handleSaveGame(javafx.event.ActionEvent event){
        try {
            saveGame();
            showSaveGameAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() throws IOException {
        String gameData = getCurrentGameData();
        File saveFile = new File("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\saved_games.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile, true))) { // The 'true' here sets append mode
            writer.write(gameData);
        }
    }

    private String getCurrentGameData() {
        StringBuilder sb = new StringBuilder();

        // Save the secret word
        sb.append(currentWord.getSecretWord()).append("\n");

        // Save the words on each row
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                TextField cell = getCell(i, j);
                String text = cell.getText().isEmpty() ? " " : cell.getText(); // Use space for empty cells
                sb.append(text);
            }
            sb.append(i < 5 ? " " : "\n"); // Separate words with spaces, end line with newline
        }

        // Save the colors of each tile
        for (Node node : wordleGrid.getChildren()) {
            if (node instanceof TextField) {
                TextField cell = (TextField) node;
                String color = getCellColorFromStyleClass(cell.getStyleClass());
                sb.append(color).append(" ");
            }
        }
        sb.append("\n");

        // Save the colors of each key on the keyboard
        for (Node row : keyboard.getChildren()) {
            if (row instanceof HBox) {
                for (Node keyNode : ((HBox) row).getChildren()) {
                    if (keyNode instanceof Button) {
                        Button key = (Button) keyNode;
                        String color = getKeyColorFromStyleClass(key.getStyleClass());
                        if (key.getStyleClass().contains("enter-button")) {
                            sb.append("Enter").append("-").append(color).append(" ");
                        } else if (key.getStyleClass().contains("delete-button")) {
                            sb.append("Delete").append("-").append(color).append(" ");
                        } else {
                            sb.append(key.getText()).append("-").append(color).append(" ");
                        }
                    }
                }
            }
        }
        sb.append("\n").append(currentAttempt).append("\n");

        return sb.toString();
    }

    private String getCellColorFromStyleClass(ObservableList<String> styleClass) {
        if (styleClass.contains("green-wordle-cell")) return "green";
        if (styleClass.contains("yellow-wordle-cell")) return "yellow";
        if (styleClass.contains("grey-wordle-cell")) return "grey";
        if (styleClass.contains("update-wordle-cell")) return "updated";
        return "default";
    }
    private String getKeyColorFromStyleClass(ObservableList<String> styleClass) {
        if (styleClass.contains("green-keyboard-button")) return "green";
        if (styleClass.contains("yellow-keyboard-button")) return "yellow";
        if (styleClass.contains("grey-keyboard-button")) return "grey";
        return "default"; // if it's a keyboard-button
    }

    private void showSaveGameAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your game has been saved successfully!");
        alert.showAndWait();
    }

    @FXML
    protected void handleShowStats(javafx.event.ActionEvent event){
        statsManager.showStatsWindow();
    }

    @FXML
    protected void handleKey(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String key = btn.getText();
        if (currentWord.getCurrentWord().size() < 5 && currentAttempt < 6) {
            updateActiveCell(key);
        }
    }

    private void updateActiveCell(String key) {
        TextField cell = getCell(currentAttempt, currentWord.getCurrentWord().size());
        cell.setText(key);

        cell.getStyleClass().removeAll("wordle-cell", "update-wordle-cell");
        cell.getStyleClass().add("update-wordle-cell");

        currentWord.addChar(key.charAt(0));
    }

    private TextField getCell(int row, int column) {
        for (Node node : wordleGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && node instanceof TextField) {
                return (TextField) node;
            }
        }
        return null;  // Should never happen
    }

    @FXML
    private void handleEnter() throws IOException {
        if (currentWord.getCurrentWord().size() == 5) {
            String guessedWord = currentWord.getCurrentWord().stream().map(String::valueOf).collect(Collectors.joining()).toUpperCase();
            if (!currentWord.getValidWords().contains(guessedWord)){
                showInvalidWordAlert();
                return;
            }
            colorTiles();
            processGuess();
        }
        else {
            showNotEnoughLettersAlert();
        }
    }

    @FXML
    private void handleDelete() {
        if (!currentWord.getCurrentWord().isEmpty()) {
            currentWord.getCurrentWord().remove(currentWord.getCurrentWord().size()-1); // removes the last character
            TextField cell = getCell(currentAttempt, currentWord.getCurrentWord().size());
            cell.getStyleClass().remove("update-wordle-cell");
            cell.getStyleClass().add("wordle-cell");
            cell.setText("");
        }
    }

    private void processGuess() throws IOException {
        String guessedWord = currentWord.getCurrentWord().stream().collect(Collectors.collectingAndThen(Collectors.toList(),
                list -> list.stream().map(String::valueOf).collect(Collectors.joining())));
        if (guessedWord.equalsIgnoreCase(currentWord.getSecretWord())) {
            showEndGameScreen(true);
        } else if (currentAttempt == 5) {  // Last attempt is 0-indexed at 5
            showEndGameScreen(false);
        } else {
            currentAttempt++;
            currentWord.clear();  // Prepare for next guess
        }
    }

    private void colorTiles() {
        int rowStart = currentAttempt * 5;
        boolean[] secretWordUsed = new boolean[5]; // Tracks whether a letter in the secret word has been matched
        boolean[] guessedWordUsed = new boolean[5]; // Tracks whether a guessed letter should be marked yellow

        // First pass for green tiles
        for (int i = 0; i < 5; i++) {
            TextField cell = (TextField) wordleGrid.getChildren().get(rowStart + i);
            char guessedChar = currentWord.getCurrentWord().get(i);
            if (guessedChar == currentWord.getSecretWord().charAt(i)) {
                cell.getStyleClass().remove("update-wordle-cell");
                cell.getStyleClass().add("green-wordle-cell");
                // Updates the keyboard tile associated with the letter to green
                Button letter = findButtonByChar(guessedChar);
                letter.getStyleClass().removeAll("keyboard-button", "yellow-keyboard-button", "grey-keyboard-button");
                letter.getStyleClass().add("green-keyboard-button");
                secretWordUsed[i] = true;
                guessedWordUsed[i] = true;
            }
        }

        // Second pass to assign yellow or grey
        for (int i = 0; i < 5; i++){
            if (!guessedWordUsed[i]){ // If the letter is either yellow or grey
                TextField cell = (TextField) wordleGrid.getChildren().get(rowStart + i);
                char guessedChar = currentWord.getCurrentWord().get(i);
                boolean found = false;
                for (int j = 0; j < 5; j++) {
                    if (guessedChar == currentWord.getSecretWord().charAt(j) && !secretWordUsed[j]) {
                        cell.getStyleClass().remove("update-wordle-cell");
                        cell.getStyleClass().add("yellow-wordle-cell");
                        Button letter = findButtonByChar(guessedChar);
                        if (!letter.getStyleClass().get(1).contains("green")) {
                            letter.getStyleClass().removeAll("keyboard-button", "grey-keyboard-button");
                            letter.getStyleClass().add("yellow-keyboard-button");
                        }
                        secretWordUsed[j] = true;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cell.getStyleClass().remove("update-wordle-cell");
                    cell.getStyleClass().add("grey-wordle-cell");
                    // Update the keyboard tile associated with the letter to grey
                    Button letter = findButtonByChar(guessedChar);
                    if (!letter.getStyleClass().get(1).contains("green") && !letter.getStyleClass().get(1).contains("yellow")) {
                        letter.getStyleClass().remove("keyboard-button");
                        letter.setDisable(true); // Disabling the button
                        letter.getStyleClass().add("grey-keyboard-button");
                    }
                }
            }
        }
    }

    private Button findButtonByChar(char ch) {
        for (Node row : keyboard.getChildren()) {
            if (row instanceof HBox) {
                for (Node key : ((HBox) row).getChildren()) {
                    if (key instanceof Button) {
                        Button button = (Button) key;
                        if (button.getText().equalsIgnoreCase(String.valueOf(ch))) {
                            return button;
                        }
                    }
                }
            }
        }
        return null;  // Return null if no matching button is found
    }

    private void showEndGameScreen(boolean isWin) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);  // No header
        if (isWin) {
            alert.setContentText("Congratulations! You guessed the word!");
        } else {
            alert.setContentText("Game over! The correct word was: " + currentWord.getSecretWord());
        }
        alert.showAndWait();
        saveEndGame(isWin);
        resetGame();  // Reset the game or provide an option to restart
    }

    private void showNotEnoughLettersAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);  // No header
        alert.setContentText("Not enough letters");
        alert.showAndWait();
    }
    private void showInvalidWordAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Word");
        alert.setHeaderText(null);
        alert.setContentText("Word Not Found");
        alert.showAndWait();
    }

    private void saveEndGame(boolean isWin) throws IOException{
        String gameData = getEndingGameData(isWin);
        File saveFile = new File("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\finished_games.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile, true))) {
            writer.write(gameData);
        }
    }

    private String getEndingGameData(boolean isWin){
        // currentAttempt+1
        // win or lose (W or L)
        StringBuilder sb = new StringBuilder();

        sb.append(currentAttempt+1).append(" ");
        if (isWin) { sb.append("W"); }
        else { sb.append("L"); }
        sb.append("\n");

        return sb.toString();
    }

    private void resetGame() {
        for (int i = 0; i < 30; i++) {  // Clear all cells
            TextField cell = (TextField) wordleGrid.getChildren().get(i);
            cell.setText("");
            cell.getStyleClass().removeAll("update-wordle-cell", "grey-wordle-cell", "yellow-wordle-cell", "green-wordle-cell");
            cell.getStyleClass().add("wordle-cell");
        }
        currentWord.clear();
        currentAttempt = 0;
        currentWord.resetSecretWord();
        resetKeyboard();
    }

    private void resetKeyboard() {
        for (Node row : keyboard.getChildren()) {
            if (row instanceof HBox) {
                for (Node key : ((HBox) row).getChildren()) {
                    if (key instanceof Button) {
                        key.getStyleClass().removeAll("grey-keyboard-button", "green-keyboard-button", "yellow-keyboard-button", "keyboard-button");
                        key.getStyleClass().add("keyboard-button");
                        key.setDisable(false); // Re-enable the button for a new game
                    }
                }
            }
        }
    }
}
