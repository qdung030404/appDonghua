package com.example.appdonghua.Model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Story {
    private String title;
    private String description;
    private String coverImageUrl;
    private List<String> genres; // Mảng thể loại
    private String status;
    private long viewCount;
    private double ratingAvg;
    private Date lastUpdated;
    public Story() {}

    public Story(String title, String description, String coverImageUrl, List<String> genres, String status, long viewCount, double ratingAvg) {
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.genres = genres;
        this.status = status;
        this.viewCount = viewCount;
        this.ratingAvg = ratingAvg;
        // lastUpdated sẽ được gán tự động
    }

    // --- Bắt đầu Getters và Setters ---
    // (Bấm Alt + Insert để tự động tạo)

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }

    @ServerTimestamp // Tự động gán thời gian của server
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}