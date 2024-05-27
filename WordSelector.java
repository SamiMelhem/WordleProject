package com.example.wordlegame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WordSelector {
    private List<String> validWords;
    private List<String> lines;

    public WordSelector() {
        try {
            lines = Files.readAllLines(Paths.get("C:\\Users\\samim\\IdeaProjects\\WordleGame\\src\\main\\java\\com\\example\\wordlegame\\WordList.txt")); // Adjust the path as necessary
            validWords = lines.stream().map(String::toUpperCase).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getValidWords() {
        return validWords;
    }
    public String getNewWord() {
        return lines.get(new Random().nextInt(lines.size())).toUpperCase();
    }
}
