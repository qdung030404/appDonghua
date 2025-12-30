package com.example.appdonghua.Model;

public class Chapter {
    private String Chapter;
    private int views;
    private String content; // Thêm content
    private int order; // Thêm thứ tự

    public Chapter(String chapter, int views) {
        this.Chapter = chapter;
        this.views = views;
    }
    public Chapter(String chapter, int views, String content, int order) {
        this.Chapter = chapter;
        this.views = views;
        this.content = content;
        this.order = order;
    }
    public String getChapter() {
        return Chapter;
    }

    public void setChapter(String chapter) {
        Chapter = chapter;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
