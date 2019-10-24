package com.example.bookselling;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

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
        BottomNavigationView navigation = findViewById(R.id.navigation);

        Client client = new Client("IZDGA4GCBK", "0d030bc6e78332bbbc83f85017c87eec");
        Index index = client.getIndex("Books");

        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                // [...]
                Log.e("op",content.toString());
            }
        };
// Search for a first name
        index.searchAsync(new Query("mai hi hu"), completionHandler);
// Search for a first name with typo
        index.searchAsync(new Query("j"), completionHandler);


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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.explore_action_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();


//        searchView.setSearchableInfo( searchManager.getSearchableInfo(new
//                ComponentName(this,SearchActivity.class)));
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
//
   //   setSearchTextColour(searchView);
//        setSearchIcons(searchView);
//


        int searchEditTextId = R.id.search_src_text;
        final AutoCompleteTextView searchEditText = (AutoCompleteTextView) searchView.findViewById(searchEditTextId);
        final View dropDownAnchor = searchView.findViewById(searchEditText.getDropDownAnchor());

        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {

                    // screen width
                    int screenWidthPixel = MainActivity.this.getResources().getDisplayMetrics().widthPixels;
                    searchEditText.setDropDownWidth(screenWidthPixel);
                    searchEditText.setDropDownBackgroundDrawable(getDrawable(R.drawable.suggestions_background));

                }
            });
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent=new Intent(this,FavouritesActivity.class);
                startActivity(intent);
                Log.i("asdad","sad");
                return true;

//            case R.id.search:
//                onSearchRequested();
//                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onSearchRequested() {
//        return super.onSearchRequested();
//    }




//    private void setSearchTextColour(SearchView searchView) {
//        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        EditText searchPlate = (EditText) searchView.findViewById(searchPlateId);
//        searchPlate.setTextColor(Color.RED);
//       // searchPlate.setBackgroundResource(R.drawable.edit_text_holo_light);
//        searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//    }
//
//
//    private void setSearchIcons(SearchView searchView) {
//        try {
//            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
//            searchField.setAccessible(true);
//            ImageView closeBtn = (ImageView) searchField.get(searchView);
//            closeBtn.setImageResource(R.drawable.ic_menu_cancel);
//
//            searchField = SearchView.class.getDeclaredField("mVoiceButton");
//            searchField.setAccessible(true);
//            ImageView voiceBtn = (ImageView) searchField.get(searchView);
//            voiceBtn.setImageResource(R.drawable.ic_menu_voice_input);
//
//            ImageView searchButton = (ImageView) searchView.findViewById(R.id.abs__search_button);
//            searchButton.setImageResource(R.drawable.ic_menu_search);
//
//        } catch (NoSuchFieldException e) {
//            Log.e("SearchView", e.getMessage(), e);
//        } catch (IllegalAccessException e) {
//            Log.e("SearchView", e.getMessage(), e);
//        }
//    }

}

