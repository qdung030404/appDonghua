package com.example.appdonghua.Model;

public class TopSearch {
    private String ImageUrl;
    private String topSearchBookCover;
    public TopSearch(String imageUrl, String topSearchBookCover) {
        this.ImageUrl = imageUrl;
        this.topSearchBookCover = topSearchBookCover;
    }


    public String getImageUrl() {
        return ImageUrl;
    }
    public void setImage(String image) {
        ImageUrl = image;
    }
    public String gettopSearchBookCover() {
        return topSearchBookCover;
    }
    public void settopSearchBookCover(String topSearchBookCover) {
        this.topSearchBookCover = topSearchBookCover;
    }

}