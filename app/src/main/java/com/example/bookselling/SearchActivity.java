package com.example.bookselling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemListener {

    private ArrayList<BookDataModel> bookDataModelArrayList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        bookDataModelArrayList=new ArrayList<>();
        bookDataModelArrayList.clear();
        recyclerView=findViewById(R.id.searchRecyclerView);

       // Toast.makeText(this,"Gdfgd",Toast.LENGTH_SHORT).show();

        handleIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        bookDataModelArrayList.clear();
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);


            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();


            Client client = new Client("IZDGA4GCBK", "0d030bc6e78332bbbc83f85017c87eec");
            Index index = client.getIndex("Books");

            CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    Log.e("op",content.toString());

                    sendResult(content);

                }
            };

            index.searchAsync(new Query(query), completionHandler);

        }
    }

    private void sendResult(JSONObject content){
        JSONArray jsonArray;
         String title;
         String author;

        String description;
        int price;
        String downloadUri;
        String userId;
        try {
           jsonArray =content.getJSONArray("hits");
            Log.e("sdaaaaa",jsonArray.toString());

            for (int i=0;i<jsonArray.length();++i){
                JSONObject jsonObject=jsonArray.getJSONObject(i);

                Log.e("jsonobject",jsonObject.toString());

                title=jsonObject.getString("title");
                author=jsonObject.getString("author");
                description=jsonObject.getString("description");
                price=jsonObject.getInt("price");
                downloadUri=jsonObject.getString("downloadUri");
                userId=jsonObject.getString("userId");

                BookDataModel bookDataModel=new BookDataModel(title,author,description,price,downloadUri,userId);
                bookDataModel.setRefKey(jsonObject.getString("objectID"));

                bookDataModelArrayList.add(bookDataModel);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);




        SearchAdapter adapter=new SearchAdapter(bookDataModelArrayList,SearchActivity.this,SearchActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void OnBookClick(int position) {

    }

    @Override
    public void OnBookLongClick(int position, View view) {

    }

    @Override
    public void OnButton1Click(int position, View view) {

    }

    @Override
    public void OnButton2Click(int position, View view) {

    }

    @Override
    public void OnFavButtonClick(int position, View view) {

    }

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
                    int screenWidthPixel = SearchActivity.this.getResources().getDisplayMetrics().widthPixels;
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
}
