package com.example.lenovo.getit;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable, Comparable<Post> {
    String postId;
    String userId;
    String profilePic;              // the users profile pic who posted the product
    String postedWho;               // the username who posted the product
    String productCategory;         // the product category
    String productLocation;
    String productPic;              // the main display pic of the product
    ArrayList<String> productImages;
    String productTitle;            // the title of the product
    String productType;             // new or used product
    int productPrice;               // the price of the product
    String productDescription;      // the description of the product
    int quantity;
    ArrayList<Like> likedByIds;
    ArrayList<Rate> ratings;
    ArrayList<Comment> comments;
    int priority;

    public Post(){}

    public Post(String postId, String userId, String profilePic, String postedWho, String productCategory, String productLocation, String productPic, ArrayList<String> productImages, String productTitle, String productType, int productPrice, String productDescription, int quantity, ArrayList<Like> likedByIds, ArrayList<Rate> ratings, ArrayList<Comment> comments, int priority) {
        this.postId = postId;
        this.userId = userId;
        this.profilePic = profilePic;
        this.postedWho = postedWho;
        this.productCategory = productCategory;
        this.productLocation = productLocation;
        this.productPic = productPic;
        if(productImages != null)
            this.productImages = productImages;
        else
            this.productImages = new ArrayList<>();
        this.productTitle = productTitle;
        this.productType = productType;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.quantity = quantity;
        if(likedByIds != null)
            this.likedByIds = likedByIds;
        else
            this.likedByIds = new ArrayList<>();
        if(ratings != null)
            this.ratings = ratings;
        else
            this.ratings = new ArrayList<>();
        if(comments != null)
            this.comments = comments;
        else
            this.comments = new ArrayList<>();
        this.priority = priority;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getPostedWho() {
        return postedWho;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductPic() {
        return productPic;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductType() {
        return productType;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public int getPriority() {
        return priority;
    }

    public ArrayList<Like> getLikedByIds() {
        return likedByIds;
    }

    public ArrayList<Rate> getRates() {
        return ratings;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<Rate> getRatings() {
        return ratings;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setRatings(ArrayList<Rate> ratings) {
        this.ratings = ratings;
    }

    public void setLikedByIds(ArrayList<Like> likedByIds) {
        this.likedByIds = likedByIds;
    }

    public void setRates(ArrayList<Rate> ratings) {
        this.ratings = ratings;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setPostedWho(String postedWho) {
        this.postedWho = postedWho;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductLocation(String productLocation) {
        this.productLocation = productLocation;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public boolean searchLiked(String id){
        if(likedByIds != null && likedByIds.size() > 0)
            for(Like iterator:likedByIds){
                if(iterator.getId().equals(id)){
                    return true;
                }
            }
        return false;
    }

    public int searchRated(String id){
        if(ratings != null && ratings.size() > 0)
            for(Rate iterator:ratings){
                if(iterator.getId().equals(id)){
                    return iterator.getRated();
                }
            }
        return 0;
    }

    public void removeLiked(String id){
        if(likedByIds != null && likedByIds.size() > 0)
            for(Like iterator:likedByIds)
                if(iterator.getId().equals(id))
                    likedByIds.remove(iterator);
    }

    public void removedRated(String id){
        ratings.remove(id);
    }

    public void addLiked(String id, String name){
        likedByIds.add(new Like(id, name));
    }

    public void addRated(Rate obj){
        ratings.add(obj);
    }

    @Override
    public int compareTo(@NonNull Post p) {
        return p.getPriority() - this.getPriority();
    }
}
