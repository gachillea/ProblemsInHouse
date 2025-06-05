package com.example.problemsinhouse;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String username;
    private String password;
    private Long lives;

    public User(String usernme, String password, Long lives)
    {
        this.lives=lives;
        this.password=password;
        this.username=usernme;
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        lives = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Long getLives() {
        return lives;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeLong(lives);
    }

    public void setLives(long l) {
        this.lives = l;
    }
}
