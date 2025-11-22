package com.example.appdonghua.Model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Story {
    private String title;
    private String description;
    private long chapter;
    private String coverImageUrl;
    private List<String> genres; // Mảng thể loại
    private String status;
    private long viewCount;
    private double ratingAvg;
    private String author;
    private Date lastUpdated;
    private long search; // Số lần tìm kiếm

    public Story() {}

    public Story(String title, long chapter, String author, String coverImageUrl, List<String> genres,
                 String status, long viewCount, double ratingAvg, String description) {
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.chapter = chapter;
        this.genres = genres;
        this.status = status;
        this.viewCount = viewCount;
        this.ratingAvg = ratingAvg;
        this.author = author;
        this.search = 0; // Mặc định = 0
    }

    // --- Getters và Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getChapter() { return chapter; }
    public void setChapter(long chapter) { this.chapter = chapter; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }

    public long getSearch() { return search; }
    public void setSearch(long search) { this.search = search; }

    @ServerTimestamp
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}