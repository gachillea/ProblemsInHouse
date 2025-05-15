package com.example.problemsinhouse;

public class Post {
    private String username;
    private String title;
    private String content;
    private String imagePath;

    public Post(String username, String title, String content, String imagePath) {
        this.username = username;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

    public String getUsername() { return username; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }
}
