package com.example.bookselling;

import static com.example.bookselling.ExploreFragment.mUsersReference;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class BookDetailsActivity extends AppCompatActivity {
    ImageView bookImageView;
    TextView title;
    TextView author;
    TextView description;
    TextView price;
    Button contact;
    Button share;
    BookDataModel bookDataModel;
    String phone;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookImageView = findViewById(R.id.bookIV);
        title = findViewById(R.id.titleTV);
        author = findViewById(R.id.authorTV);
        description = findViewById(R.id.descriptuonTV);
        price = findViewById(R.id.priceTV);

        contact = findViewById(R.id.contact);
        share = findViewById(R.id.share);


        if (getIntent().getExtras() != null) {
            int pos = getIntent().getIntExtra("position", 0);
            bookDataModel = ExploreFragment.bookDataModelList.get(pos);
            setData(bookDataModel);
        } else if (getIntent().getData() != null) {
            String url = getIntent().getData().toString();
            getAndParseSharedData(url);
        }


    }


//    private void getDynamicLink() {
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        // Get deep link from result (may be null if no link is found)
//                        Uri deepLink = null;
//                        if (pendingDynamicLinkData != null) {
//                            deepLink = pendingDynamicLinkData.getLink();
//                            Log.e("deep",deepLink.toString());
//                        }
//
//
//                        // Handle the deep link. For example, open the linked
//                        // content, or apply promotional credit to the user's
//                        // account.
//                        // ...
//
//                        // ...
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w( "getDynamicLink:onFailur", e.toString());
//                    }
//                });
//    }


    /**
     * We just parse the pushKey data to show to the user
     *
     * @param url with the pushKey as query parameter
     */
    private void getAndParseSharedData(String url) {

        final Dialog dialog = new Dialog(this);

        String pushKey = "";
        if (url.contains("-")) {
            String[] parts = url.split("=");
            pushKey = parts[1];
            Log.d("getAndParseSharedData: ", pushKey);
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Query query = database.child("books").orderByKey().equalTo(pushKey);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    bookDataModel = singleSnapshot.getValue(BookDataModel.class);
                    Log.e("author", bookDataModel.getAuthor());
                    setData(bookDataModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", databaseError.toException().toString());
            }
        });


//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//
//
//        Query phoneQuery = database.orderByChild("books").equalTo(pushKey);
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    BookDataModel bookDataModel = singleSnapshot.getValue(BookDataModel.class);
//                    Log.e("author",bookDataModel.getAuthor());
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("onCancelled", databaseError.toException().toString());
//            }
//        });


    }

    private void setData(BookDataModel book) {

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescription());
        price.setText("RS. " + String.valueOf(book.getPrice()));

        Picasso.with(this).load(book.getDownloadUri()).placeholder(
                R.drawable.ic_dashboard_black_24dp).into(bookImageView);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                mUsersReference.child(bookDataModel.getUserId()).child(
                        "phone number").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                phone = dataSnapshot.getValue().toString();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String pushId = book.getRefKey();

                progressDialog = new ProgressDialog(BookDetailsActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                generateDynamicLink(generateDeepLinkUrl(pushId));

            }
        });


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
}
