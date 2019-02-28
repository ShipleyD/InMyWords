package com.example.inmywords.Model_Controller;

public class Word {
    private String Word;

    public Word(){
        //empty constructor needed
    }

    public Word(String word){

        this.Word = word;
    }

    public String getWord() {
        return Word;
    }
}
