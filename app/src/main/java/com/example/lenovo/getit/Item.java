package com.example.lenovo.getit;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Item  {
    private String userId;
    private String username;
    private String profilePic;
    private int isPremium;


    //user name
    //profile pic
    //is premium

    private String productTitle;
    private String productDescription;
    private String productCategory;
    private String productType;
    //private String Company;
    private int productPrice,quantity;
    private String productPic;

    private List<String> productImages;
    private Location location;

//    public Item(Parcel input) {
//        name = input.readString();
//        description = input.readString();
//        catagory = input.readString();
//        subCatagory = input.readString();
//        Condition = input.readString();
//        Company = input.readString();
//
//        images = (ArrayList<Image>) input.readSerializable();
//        location=Location.CREATOR.createFromParcel(input);
//    }

    public Item(String userId, String username, String profilePic, int isPremium, String productTitle, String productDescription, String productCategory, String productType, int productPrice, int quantity, String productPic, List<String> productImages) {
        this.userId = userId;
        this.username = username;
        this.profilePic = profilePic;
        this.isPremium = isPremium;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productType = productType;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.productPic = productPic;
        this.productImages = productImages;
    }

    public Item() {
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getIsPremium() {
        return isPremium;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductType() {
        return productType;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductPic() {
        return productPic;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public Location getLocation() {
        return location;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setIsPremium(int isPremium) {
        this.isPremium = isPremium;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //    @Override
//    public int describeContents() {
//        return 0;
//    }
//    /*
//    String name;
//    String description;
//    String catagory;
//    String subCatagory;
//    String Condition;
//    String Company;
//
//    ArrayList<Image> images;
//    Location location;*/
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(name);
//        dest.writeString(description);
//        dest.writeString(catagory);
//        dest.writeString(subCatagory);
//        dest.writeString(Condition);
//        dest.writeString(Company);
//
//        dest.writeList(images);
//        location.writeToParcel(dest,flags);
//    }
//
//    public static Parcelable.Creator<Item> CREATOR = new
//            Creator<Item>() {
//                @Override
//                public Item createFromParcel(Parcel source) {
//                    return  new Item(source);
//                }
//
//                @Override
//                public Item[] newArray(int size) {
//                    return new Item[0];
//                }
//            };
}
