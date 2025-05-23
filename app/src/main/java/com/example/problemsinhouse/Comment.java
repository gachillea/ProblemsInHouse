package com.example.problemsinhouse;

public class Comment {
    private String id;
    private String username;
    private String text;

    public Comment() {}

    public Comment(String id, String username, String text) {
        this.id = id;
        this.username = username;
        this.text = text;
    }

    public Comment(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
