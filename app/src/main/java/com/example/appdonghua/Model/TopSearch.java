package com.example.appdonghua.Model;

public class TopSearch {
    private int Image;
    private String topSearchBookCover;
    public TopSearch(int Image, String topSearchBookCover) {
        this.Image = Image;
        this.topSearchBookCover = topSearchBookCover;
    }


    public int getImage() {
        return Image;
    }
    public void setImage(int image) {
        Image = image;
    }
    public String gettopSearchBookCover() {
        return topSearchBookCover;
    }
    public void settopSearchBookCover(String topSearchBookCover) {
        this.topSearchBookCover = topSearchBookCover;
    }

}