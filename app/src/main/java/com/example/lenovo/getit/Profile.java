package com.example.lenovo.getit;

public class Profile {
    private String name;
    private String email;
    private String photoUrl;
    Profile(){}
    Profile(String name,String email,String photoUrl){
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }
    public String getEmail(){
        return email;
    }
    public String getPhotoUrl(){
        return photoUrl;
    }
}
