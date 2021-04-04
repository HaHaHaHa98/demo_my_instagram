package com.example.my_instagram.Model;

public class User {

    private String id;
    private String username;
    private String fullName;
    private String imageURL;
    private String bio;

    public User() {
    }

    public User(String id, String username, String fullName, String imageURL, String bio) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.imageURL = imageURL;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
