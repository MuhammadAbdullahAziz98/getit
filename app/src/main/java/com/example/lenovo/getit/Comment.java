package com.example.lenovo.getit;

import java.io.Serializable;

public class Comment implements Serializable {
    String key;
    String userId;
    String name;
    String commented;

    public Comment(){}

    public Comment(String key, String userId, String name, String commented) {
        this.key = key;
        this.userId = userId;
        this.name = name;
        this.commented = commented;
    }

    public String getUserId() {
        return userId;
    }

    public String getCommented() {
        return commented;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public String toString(){
        return name + ">> " + commented;
    }
}
