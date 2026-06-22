package com.example.appdoctruyen.models;

public class Genre {

    private String name;
    private boolean selected;

    public Genre(String name) {
        this.name = name;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}