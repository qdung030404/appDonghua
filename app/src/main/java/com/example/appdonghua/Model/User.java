package com.example.appdonghua.Model; // (Tạo package Model nếu chưa có)

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class User {
    private String uid;
    private String username;
    private String email;
    private String avatarUrl;
    private Date createdAt;


    public User() {}


    public User(String uid, String username, String email, String avatarUrl) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;

    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }


    @ServerTimestamp // Tự động gán thời gian của server
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}