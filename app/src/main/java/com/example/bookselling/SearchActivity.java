package com.example.bookselling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
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

        recyclerView=findViewById(R.id.searchRecyclerView);

       // Toast.makeText(this,"Gdfgd",Toast.LENGTH_SHORT).show();

        handleIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
}
