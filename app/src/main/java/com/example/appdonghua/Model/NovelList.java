package com.example.appdonghua.Model;

public class NovelList {
     private int image;
     private String title;
     private String views;
     private String category;
     private String chapter;
     private String author;

     public NovelList(int image, String title, String views, String category, String chapter, String author){
          this.image = image;
          this.title = title;
          this.views = views;
          this.category = category;
          this.chapter = chapter;
          this.author = author;
     }
     public int getImage() {return image;}
     public String getTitle() {return title;}
     public String getViews() {return views;}
     public String getCategory() {return category;}
     public String getChapter() {return chapter;}
     public String getAuthor() {return author;}
     public void setImage(int image) {this.image = image;}
     public void setTitle(String title) {this.title = title;}
     public void setViews(String views) {this.views = views;}
     public void setCategory(String category) {this.category = category;}
     public void setChapter(String chapter) {this.chapter = chapter;}
     public void setAuthor(String author) {this.author = author;}

}
