package com.example.bookselling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class BookDetailsActivity extends AppCompatActivity {
private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        fragmentManager=getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.detailsBookContainer,new BookDetailsFragment()).commit();
    }
}
