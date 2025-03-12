package com.example.dicerealmandroid.command;

public class Command {
    private final String type;

    public Command(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
