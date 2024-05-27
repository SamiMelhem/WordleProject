package com.example.wordlegame;

import java.util.ArrayList;
import java.util.List;

public class CurrentWord {
    private ArrayList<Character> currentWord = new ArrayList<>();
    private WordSelector words;
    private String secretWord;

    public CurrentWord(){
        words = new WordSelector();
        secretWord = words.getNewWord();
    }
    public void setSecretWord(String secretWord){
        this.secretWord = secretWord;
    }
    public void setCurrentWord(String currentWord){
        for (int i = 0; i < currentWord.length(); i++)
            this.currentWord.add(currentWord.charAt(i));
    }
    public ArrayList<Character> getCurrentWord() {
        return currentWord;
    }
    public String getSecretWord(){
        return secretWord;
    }
    public List<String> getValidWords(){
        return words.getValidWords();
    }
    public void resetSecretWord(){
        secretWord = words.getNewWord();
    }
    public void clear() {
        currentWord.clear();
    }

    public void addChar(char ch) {
        if (currentWord.size() < 5) {
            currentWord.add(ch);
        }
    }
}
