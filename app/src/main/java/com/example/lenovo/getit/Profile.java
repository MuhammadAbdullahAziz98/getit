package com.example.lenovo.getit;

public class Profile {
    private String name;
    private String photoUrl;
    int isPremium;
    Profile(){}
    Profile(String name,String photoUrl){
        this.name = name;
        this.photoUrl = photoUrl;
        this.isPremium =3;
    }
    public int getIsPremium(){return isPremium;}
    public String getPhotoUrl(){
        return photoUrl;
    }
    public String getName(){
        return name;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPremium(int isPremium){
        this.isPremium = isPremium;
    }
}
