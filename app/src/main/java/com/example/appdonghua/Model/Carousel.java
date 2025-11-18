package com.example.appdonghua.Model;

public class Carousel {
    private String imageUrl; // THAY ĐỔI: từ int thành String

    public Carousel(String imageUrl) { // THAY ĐỔI: String imageUrl
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() { return imageUrl; }
}