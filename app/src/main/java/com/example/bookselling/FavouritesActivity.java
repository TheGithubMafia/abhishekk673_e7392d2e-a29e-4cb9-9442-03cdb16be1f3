package com.example.bookselling;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesActivity extends AppCompatActivity implements
        RecyclerViewAdapter.OnItemListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mdatabase;
    private DatabaseReference muserReference;
    private DatabaseReference mBooksReference;
    private ChildEventListener mChildEventListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<BookDataModel> favouritesList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explore);

        favouritesList = new ArrayList<>();

        mdatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        muserReference = mdatabase.getReference().child("users").child(
                mAuth.getCurrentUser().getUid());
        mBooksReference = mdatabase.getReference().child("books");

        addItems();



        /*for (int i = 1; i <= 20; ++i) {
            bookDataModelList.add(new BookDataModel(i));
        }*/

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
//                DividerItemDecoration.VERTICAL));

        // specify an adapter and pass in our data model list

        mAdapter = new RecyclerViewAdapter(favouritesList, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    private void addItems() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("idk", dataSnapshot.getKey());
                mBooksReference.child(dataSnapshot.getKey()).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.i("gdgf", dataSnapshot.getValue().toString());
                                BookDataModel bookDataModel = dataSnapshot.getValue(
                                        BookDataModel.class);
                                bookDataModel.setRefKey(dataSnapshot.getKey());
                                Log.i("gdgf", bookDataModel.getAuthor());
                                favouritesList.add(bookDataModel);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        muserReference.child("Favourites").addChildEventListener(mChildEventListener);

    }

    @Override
    public void OnBookLongClick(int position, View view) {

    }

    @Override
    public void OnButton2Click(int position, View view) {


        String pushId = favouritesList.get(position).getRefKey();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        generateDynamicLink(generateDeepLinkUrl(pushId));
    }

    /**
     * This will generate my link with the pushKey of the data stored above
     *
     * @param pushID of the current set of data stored in Firebase Realtime Database
     * @return Returns a link that matches my AndroidManifest data block
     */
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
                        .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
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


    @Override
    public Context getCon() {
        return this;
    }

    @Override
    public List<BookDataModel> getBookDataModelList() {
        return favouritesList;
    }
}
