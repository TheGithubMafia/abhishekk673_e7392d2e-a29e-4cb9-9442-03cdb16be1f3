package com.example.bookselling;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rbddevs.splashy.Splashy;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity implements
        Splashy.OnComplete {

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splas);
        setSplashy();
    }

    void setSplashy() {
        new Splashy(this)  // For JAVA : new Splashy(this)
                .setLogo(R.mipmap.ic_launcher)
                .setTitle("BOOKS TROVE")
                .setTitleColor(R.color.colorPrimary)
                .setSubTitle("More than a book store")
                .setProgressColor(R.color.colorPrimaryDark)
                .setSubTitleItalic(true)
                .setSubTitleColor(R.color.colorPrimaryLite)
                .showProgress(true)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_RIGHT, 800)
//                .setBackgroundResource("#000000")
                .setFullScreen(true)
                .setTime(1000)
//                .setInfiniteDuration(true)
                .show();
        checkLogin();
    }

    void checkLogin() {

        // Some mock example response operation
//        Response.onResponse(object  :Response.onResponse {
//            override fun onResponse(response) {
//                Splashy.Companion.hide();             // Hide after operation
//            }
//
//        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        Splashy.Companion.hide();


        // Listener for completion of splash screen
        Splashy.Companion.onComplete(this);
    }

    @Override
    public void onComplete() {

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }
}