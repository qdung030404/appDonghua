package com.example.appdonghua.Model;

public class Chapter {
    private String Chapter;
    private int views;

    public Chapter(String chapter, int views) {
        this.Chapter = chapter;
        this.views = views;
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
}
