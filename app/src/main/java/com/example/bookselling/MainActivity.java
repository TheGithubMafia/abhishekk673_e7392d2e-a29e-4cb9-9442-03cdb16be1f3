package com.example.bookselling;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ActionBar toolbar;
    final Fragment fragment1 = new ExploreFragment();
    final Fragment fragment2 = new SellFragment();
    final Fragment fragment3 = new AccountFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_explore:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    toolbar.setTitle("Explore");
                    return true;

                case R.id.navigation_sell:
                    Toast.makeText(getApplicationContext(),active.toString()+"......"+fragment2,Toast.LENGTH_SHORT).show();
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    toolbar.setTitle("Sell");
                    return true;

                case R.id.navigation_account:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    toolbar.setTitle("Account");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

            ExploreFragment.dataModelList = new ArrayList<>();
            fm.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
            fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.fragment_container, fragment1, "1").commit();
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            toolbar = getSupportActionBar();
            toolbar.setTitle("Explore");
            Log.i("abc", "onCreate");


            mTextMessage = (TextView) findViewById(R.id.message);

//
//            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
//            layoutParams.setBehavior(new BottomNavigationBehavior1());


//        loadFragment(new ExploreFragment());

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("activr","a");
        super.onSaveInstanceState(outState);
    }

    //    private void loadFragment(Fragment fragment) {
//        // load fragment
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, fragment);
//      //  transaction.addToBackStack(null);
//        transaction.commit();
//    }




}
