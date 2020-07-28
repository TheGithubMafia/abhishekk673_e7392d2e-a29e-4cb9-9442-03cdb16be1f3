package com.example.bookselling;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment implements RecyclerViewAdapter.OnItemListener {
    static public List<BookDataModel> bookDataModelList;
    static Animation outAnimation;
    static Animation inAnimation;
    static DatabaseReference mUsersReference;
    static FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mBooksReference;
    private ChildEventListener mChildEventListener;
    private String phone;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, null);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progress_circular);


        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);


        mAuth = FirebaseAuth.getInstance();
        Log.i("abc", "Oncreateview");


        //

        mdatabase = FirebaseDatabase.getInstance();
        mBooksReference = mdatabase.getReference().child("books");
        mUsersReference = mdatabase.getReference().child("users");

        addItems();



        /*for (int i = 1; i <= 20; ++i) {
            bookDataModelList.add(new BookDataModel(i));
        }*/

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
        //  DividerItemDecoration.VERTICAL));

        // specify an adapter and pass in our data model list

        mAdapter = new RecyclerViewAdapter(bookDataModelList, getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }

    private void addItems() {
        progressBar.setVisibility(View.VISIBLE);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("fsd", dataSnapshot.getValue().toString());
                progressBar.setVisibility(View.GONE);
                BookDataModel bookDataModel = dataSnapshot.getValue(BookDataModel.class);
                bookDataModel.setRefKey(dataSnapshot.getKey());
                bookDataModelList.add(bookDataModel);
                mAdapter.notifyDataSetChanged();
                Log.i("expfr", bookDataModel.getDownloadUri());
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

        mBooksReference.addChildEventListener(mChildEventListener);

    }

    @Override
    public void OnBookClick(int position) {
        Log.i("Book", Integer.toString(position));

        Intent intent = new Intent(getContext(), BookDetailsActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void OnBookLongClick(int position, View view) {

    }

    @Override
    public void OnButton1Click(int position, View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        mUsersReference.child(bookDataModelList.get(position).getUserId()).child(
                "phone number").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        phone = dataSnapshot.getValue().toString();
                        if (!phone.isEmpty()) {
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    @Override
    public void OnFavButtonClick(final int position, View view) {
        final ImageButton btn = view.findViewById(R.id.favouriteButton);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {

            // Other callback methods omitted for clarity.

            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {

                // Modify the resource of the ImageButton
                Drawable unselected = getResources().getDrawable(R.drawable.ic_favorite_black_24dp);
                Drawable selected = getResources().getDrawable(R.drawable.ic_favorite_orange_24dp);
                Drawable btnDrawable = btn.getDrawable();

                if (btnDrawable.getConstantState() == unselected.getConstantState()) {

                    btn.setImageResource(R.drawable.ic_favorite_orange_24dp);
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child(
                            "Favourites").child(
                            bookDataModelList.get(position).getRefKey()).setValue("True");

                    // Create the new Animation to apply to the ImageButton.
                    btn.startAnimation(inAnimation);
                } else {
                    btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                    btn.startAnimation(inAnimation);
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child(
                            "Favourites").child(
                            bookDataModelList.get(position).getRefKey()).removeValue();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btn.startAnimation(outAnimation);


    }

    @Override
    public Context getCon() {
        return getContext();
    }

    @Override
    public List<BookDataModel> getBookDataModelList() {
        return bookDataModelList;
    }

    @Override
    public void OnButton2Click(int position, View view) {

        String pushId = bookDataModelList.get(position).getRefKey();

        progressDialog = new ProgressDialog(getContext());
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
                        .addOnCompleteListener(getActivity(),
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

