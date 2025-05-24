package com.example.problemsinhouse;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Post implements Parcelable {
    private String id;
    private String username;
    private String title;
    private String content;
    private String imagePath;
    public Post(){}

    public Post(String id, String username, String title, String content, String imagePath) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

    public Post(String username, String title, String content, String imagePath) {
        this.username = username;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

    protected Post(Parcel in) {
        username = in.readString();
        title = in.readString();
        content = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(imagePath);
    }
}
