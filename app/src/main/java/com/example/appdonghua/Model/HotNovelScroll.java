package com.example.appdonghua.Model;

public class HotNovelScroll {
     private int image;
     private String title;
     private String views;
     private String category;
     public HotNovelScroll(int image, String title, String views, String category){
          this.image = image;
          this.title = title;
          this.views = views;
          this.category = category;
     }
     public int getImage() {return image;}
     public String getTitle() {return title;}
     public String getViews() {return views;}
     public String getCategory() {return category;}
     public void setImage(int image) {this.image = image;}
     public void setTitle(String title) {this.title = title;}
     public void setViews(String views) {this.views = views;}
     public void setCategory(String category) {this.category = category;}
}
