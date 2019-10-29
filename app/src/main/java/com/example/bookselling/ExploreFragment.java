package com.example.bookselling;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.List;

public class ExploreFragment extends Fragment implements RecyclerViewAdapter.OnItemListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static public List<BookDataModel> bookDataModelList;

    private FirebaseDatabase mdatabase;
    private DatabaseReference mBooksReference;
    private DatabaseReference mUsersReference;
    private ChildEventListener mChildEventListener;
    Animation outAnimation;
    Animation inAnimation;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_explore,null);
        mRecyclerView = view.findViewById(R.id.recyclerView);


        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
    inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);



mAuth=FirebaseAuth.getInstance();
Log.i("abc","Oncreateview");


      //

        mdatabase=FirebaseDatabase.getInstance();
        mBooksReference=mdatabase.getReference().child("books");
        mUsersReference=mdatabase.getReference().child("users");

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
      //  mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // specify an adapter and pass in our data model list

        mAdapter = new RecyclerViewAdapter(bookDataModelList, getContext(),this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }

    private void addItems(){
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("fsd",dataSnapshot.getValue().toString());
                BookDataModel bookDataModel =dataSnapshot.getValue(BookDataModel.class);
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
        Log.i("Book",Integer.toString(position));

        Intent intent=new Intent(getContext(),BookDetailsActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    @Override
    public void OnBookLongClick(int position,View view) {
        Toast.makeText(getContext(),"long clik"+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnButton1Click(int position, View view) {
Toast.makeText(getContext(),"1"+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnButton2Click(int position, View view) {
                Toast.makeText(getContext(),"2"+position,Toast.LENGTH_SHORT).show();

                String pushId=bookDataModelList.get(position).getRefKey();

        generateDynamicLink(generateDeepLinkUrl(pushId));
    }

    @Override
    public void OnFavButtonClick(final int position, View view) {
        final    ImageButton btn=view.findViewById(R.id.favouriteButton);
        outAnimation.setAnimationListener(new Animation.AnimationListener(){

            // Other callback methods omitted for clarity.

            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation){

                // Modify the resource of the ImageButton
                Drawable unselected=getResources().getDrawable(R.drawable.ic_favorite_black_24dp);
                Drawable  selected=getResources().getDrawable(R.drawable.ic_favorite_orange_24dp);
                Drawable btnDrawable=btn.getDrawable();

                if(btnDrawable.getConstantState()==unselected.getConstantState()) {

                    btn.setImageResource(R.drawable.ic_favorite_orange_24dp);
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(bookDataModelList.get(position).getRefKey()).setValue("True");

                    // Create the new Animation to apply to the ImageButton.
                    btn.startAnimation(inAnimation);
                }

                else{
                    btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                    btn.startAnimation(inAnimation);
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(bookDataModelList.get(position).getRefKey()).removeValue();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
            btn.startAnimation(outAnimation);


    }

    /**
     * This will generate my link with the pushKey of the data stored above
     * @param pushID of the current set of data stored in Firebase Realtime Database
     * @return Returns a link that matches my AndroidManifest data block
     */
    private String generateDeepLinkUrl(String pushID) {


  String url= "https://bookselling.com/shared_content=" + pushID;

  return url;

    }

    /**
     * This will return a shrinked link using Firebase Dynamic Links , this method will shrink this lik myawesomeapp.com/shared_content=pushID
     * @param  url of the custom page we created above with the custom data of the user
     */
    private void generateDynamicLink(final String url) {

//Since this will take a little bit to generate I just make a simple dialog that is the same as a ProgressDialog displaying to the user a message that says that the link to share is beign generated

//        final Dialog dialog = new Dialog(getContext());
//        String generandoRecorrido = getString(R.string.generando_recorrido);
//        DialogsUtils.iniSaveDialog(dialog, generandoRecorrido);

//setDomainUriPrefix should host a link like this https://myawesomeapp.page.link , remember to use .page.link !!

//The androidParameters is just the package name of the app , this is because if the app is not installed it will prompt the user to the playstore to download it, package example com.gaston.myapp

//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse(url))
//                .setDomainUriPrefix(getString(R.string.page_link))
//                .setAndroidParameters(
//                        new DynamicLink.AndroidParameters.Builder("com.example.bookselling")
//                                .setMinimumVersion(102)
//                                .build())
//                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT).addOnCompleteListener( new OnCompleteListener<ShortDynamicLink>() {
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
//                    Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
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



        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                .setDomainUriPrefix("https://bookselling.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
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
     * @param  url generated by the method above
     */
    private void shareDeepLink(String url) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! check this content out  " + url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this out !");
        startActivity(Intent.createChooser(shareIntent, "Share this cool content"));

    }
}

