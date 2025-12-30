package com.example.appdonghua.Model;

import java.util.ArrayList;

public class Story {
    private String title;
    private String description;
    private long chapter;
    private String coverImageUrl;
    private ArrayList<String> genres; // Mảng thể loại
    private String status;
    private long viewCount;
    private String author;
    private long search; // Số lần tìm kiếm

    public Story() {}

    public Story(String coverImageUrl,String title, long viewCount, ArrayList<String> genres, long chapter, String author,
                 String description) {
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.chapter = chapter;
        this.genres = genres;
        this.viewCount = viewCount;
        this.author = author;
        this.search = 0;
    }

    // --- Getters và Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public ArrayList<String> getGenres() { return genres; }
    public void setGenres(ArrayList<String> genres) { this.genres = genres; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getChapter() { return chapter; }
    public void setChapter(long chapter) { this.chapter = chapter; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public long getSearch() { return search; }
    public void setSearch(long search) { this.search = search; }

}