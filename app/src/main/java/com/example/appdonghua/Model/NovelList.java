package com.example.appdonghua.Model;

public class NovelList {
     private int image;
     private String title;
     private int views;
     private String category;
     private int chapter;
     private String author;

     public NovelList(int image, String title, int views, String category, int chapter, String author){
          this.image = image;
          this.title = title;
          this.views = views;
          this.category = category;
          this.chapter = chapter;
          this.author = author;
     }
     public int getImage() {return image;}
     public String getTitle() {return title;}
     public int getViews() {return views;}
     public String getCategory() {return category;}
     public int getChapter() {return chapter;}
     public String getAuthor() {return author;}
     public void setImage(int image) {this.image = image;}
     public void setTitle(String title) {this.title = title;}
     public void setViews(int views) {this.views = views;}
     public void setCategory(String category) {this.category = category;}
     public void setChapter(int chapter) {this.chapter = chapter;}
     public void setAuthor(String author) {this.author = author;}

}
