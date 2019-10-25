package com.example.bookselling;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Ref;
import java.util.Locale;

public class BookDataModel {
    private String title;
    private String author;

    private String description;
    private int price;
    private String downloadUri;
    private String userId;

    private transient String RefKey;

   // private Bitmap image;

    public BookDataModel(){}


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getDownloadUri(){
        return downloadUri;
    }

    public String getUserId(){return userId;}

    public void setRefKey(String RefKey){
        this.RefKey=RefKey;
    }

    public String getRefKey(){
        return RefKey;
    }

   // public Bitmap getImage() {
       // return image;
    //}


    public BookDataModel(String title, String author, String description, int price, String downloadUri, String userId) {

        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.downloadUri=downloadUri;
        this.userId=userId;
        Log.i("dl",downloadUri);
    }



   /* public BookDataModel(int id) {
        imageDrawable =R.drawable.ic_dashboard_black_24dp;
        title = String.format(Locale.ENGLISH, "Title %d Goes Here", id);
        subTitle = String.format(Locale.ENGLISH, "Sub title %d goes here", id);
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }*/
}
