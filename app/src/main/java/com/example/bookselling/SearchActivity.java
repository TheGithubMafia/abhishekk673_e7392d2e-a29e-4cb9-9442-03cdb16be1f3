package com.example.bookselling;

import static com.example.bookselling.ExploreFragment.bookDataModelList;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemListener {

    private ArrayList<BookDataModel> bookDataModelArrayList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        bookDataModelArrayList = new ArrayList<>();
        bookDataModelArrayList.clear();
        recyclerView = findViewById(R.id.searchRecyclerView);

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


            Client client = new Client("IZDGA4GCBK", "0d030bc6e78332bbbc83f85017c87eec");
            Index index = client.getIndex("Books");

            CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    Log.e("op", content.toString());

                    sendResult(content);

                }
            };

            index.searchAsync(new Query(query), completionHandler);

        }
    }

    private void sendResult(JSONObject content) {
        JSONArray jsonArray;
        String title;
        String author;

        String description;
        int price;
        String downloadUri;
        String userId;
        try {
            jsonArray = content.getJSONArray("hits");
            Log.e("sdaaaaa", jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Log.e("jsonobject", jsonObject.toString());

                title = jsonObject.getString("title");
                author = jsonObject.getString("author");
                description = jsonObject.getString("description");
                price = jsonObject.getInt("price");
                downloadUri = jsonObject.getString("downloadUri");
                userId = jsonObject.getString("userId");

                BookDataModel bookDataModel = new BookDataModel(title, author, description, price,
                        downloadUri, userId);
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


        SearchAdapter adapter = new SearchAdapter(bookDataModelArrayList, SearchActivity.this,
                SearchActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void OnBookLongClick(int position, View view) {

    }


    @Override
    public void OnButton2Click(int position, View view) {

        String pushId = bookDataModelList.get(position).getRefKey();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        generateDynamicLink(generateDeepLinkUrl(pushId));
    }


    @Override
    public Context getCon() {
        return this;
    }

    @Override
    public List<BookDataModel> getBookDataModelList() {
        return bookDataModelArrayList;
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
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
//
        //   setSearchTextColour(searchView);
//        setSearchIcons(searchView);
//


        int searchEditTextId = R.id.search_src_text;
        final AutoCompleteTextView searchEditText = (AutoCompleteTextView) searchView.findViewById(
                searchEditTextId);
        final View dropDownAnchor = searchView.findViewById(searchEditText.getDropDownAnchor());

        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                        int oldLeft, int oldTop, int oldRight, int oldBottom) {

                    // screen width
                    int screenWidthPixel =
                            SearchActivity.this.getResources().getDisplayMetrics().widthPixels;
                    searchEditText.setDropDownWidth(screenWidthPixel);
                    searchEditText.setDropDownBackgroundDrawable(
                            getDrawable(R.drawable.suggestions_background));

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
                Intent intent = new Intent(this, FavouritesActivity.class);
                startActivity(intent);
                Log.i("asdad", "sad");
                return true;

//            case R.id.search:
//                onSearchRequested();
//                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String generateDeepLinkUrl(String pushID) {


        String url = "https://bookselling.com/shared_content=" + pushID;

        return url;

    }

    /**
     * This will return a shrinked link using Firebase Dynamic Links , this method will shrink this
     * lik myawesomeapp.com/shared_content=pushID
     *
     * @param url of the custom page we created above with the custom data of the user
     */
    private void generateDynamicLink(final String url) {

//Since this will take a little bit to generate I just make a simple dialog that is the same as a
// ProgressDialog displaying to the user a message that says that the link to share is beign
// generated

//        final Dialog dialog = new Dialog(getContext());
//        String generandoRecorrido = getString(R.string.generando_recorrido);
//        DialogsUtils.iniSaveDialog(dialog, generandoRecorrido);

//setDomainUriPrefix should host a link like this https://myawesomeapp.page.link , remember to
// use .page.link !!

//The androidParameters is just the package name of the app , this is because if the app is not
// installed it will prompt the user to the playstore to download it, package example com.gaston
// .myapp

//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse(url))
//                .setDomainUriPrefix(getString(R.string.page_link))
//                .setAndroidParameters(
//                        new DynamicLink.AndroidParameters.Builder("com.example.bookselling")
//                                .setMinimumVersion(102)
//                                .build())
//                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT).addOnCompleteListener(
//                new OnCompleteListener<ShortDynamicLink>() {
//            @Override
//            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
//                if (task.isSuccessful()) {
//// we get the dynamic link generated and pass it to the shareDeepLink method
//                    Uri shortURL = task.getResult().getShortLink();
//                    Log.d("short link", "ShortLink:" + shortURL);
//
//                   // dialog.dismiss();
//                    shareDeepLink(url);
//                } else {
//                   // dialog.dismiss();
//                    Log.e("err",task.getException().toString());
//                    Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT)
//                    .show();
//                }
//            }
//        });


        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                .setDomainUriPrefix("https://bookselling.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        // shareDeepLink(dynamicLinkUri.toString());


        Task<ShortDynamicLink> shortLinkTask =
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(url))
                        .setDomainUriPrefix("https://bookselling.page.link")
                        // Set parameters
                        // ...
                        .buildShortDynamicLink()
                        .addOnCompleteListener(SearchActivity.this,
                                new OnCompleteListener<ShortDynamicLink>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                        if (task.isSuccessful()) {
                                            // Short link created
                                            Uri shortLink = task.getResult().getShortLink();
                                            Uri flowchartLink = task.getResult().getPreviewLink();

                                            shareDeepLink(shortLink.toString());
                                        } else {
                                            // Error
                                            // ...
                                        }
                                    }
                                });
    }

    /**
     * We just share this link with any provider that the user may want
     *
     * @param url generated by the method above
     */
    private void shareDeepLink(String url) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! check this content out  " + url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this out !");
        progressDialog.dismiss();
        startActivity(Intent.createChooser(shareIntent, "Share this cool content"));

    }
}
