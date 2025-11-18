package com.example.appdonghua.Model;

public class NovelList {
     private String imageUrl; // THAY ĐỔI: từ int thành String
     private String title;
     private long viewCount; // THAY ĐỔI: từ int thành long
     private String genre;
     private String chapterCount;
     private String author;
     private String description;


     public NovelList(String imageUrl, String title, long viewCount, String genre, String chapterCount, String author, String description) {
          this.imageUrl = imageUrl;
          this.title = title;
          this.viewCount = viewCount;
          this.genre = genre;
          this.chapterCount = chapterCount;
          this.author = author;
          this.description = description;
     }
     public String getImageUrl() { return imageUrl; }
     public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

     public void setTitle(String title) { this.title = title; }
     public String getTitle() { return title; }
     public void setViewCount(long viewCount) { this.viewCount = viewCount; }
     public long getViewCount() { return viewCount; }
     public void setGenre(String genre) { this.genre = genre; }
     public String getGenre() { return genre; }
     public void setChapterCount(String chapterCount) { this.chapterCount = chapterCount; }
     public String getChapterCount() { return chapterCount; }
     public void setAuthor(String author) { this.author = author; }
     public String getAuthor() { return author; }
     public void setDescription(String description) { this.description = description; }
     public String getDescription() { return description; }

}