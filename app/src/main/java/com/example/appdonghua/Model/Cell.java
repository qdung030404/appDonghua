package com.example.appdonghua.Model;
public class Cell {
    private String imageUrl; // THAY ĐỔI: từ int thành String
    private String title;

    public Cell(String imageUrl, String title) { // THAY ĐỔI: String imageUrl
        this.imageUrl = imageUrl;
        this.title = title;
    }

    // Thêm Getters
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
}