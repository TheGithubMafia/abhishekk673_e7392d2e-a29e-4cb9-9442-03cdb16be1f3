package com.example.bookselling;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.Locale;

public class DataModel {
    private String title;
    private String author;

    private String description;
    private int price;
    private String downloadUri;

   // private Bitmap image;

    public DataModel(){}

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

   // public Bitmap getImage() {
       // return image;
    //}


    public DataModel(String title, String author, String description, int price,String downloadUri) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.downloadUri=downloadUri;
        Log.i("dl",downloadUri);
    }




   /* public DataModel(int id) {
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
