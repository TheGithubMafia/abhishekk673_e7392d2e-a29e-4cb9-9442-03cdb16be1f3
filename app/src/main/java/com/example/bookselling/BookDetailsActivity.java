package com.example.bookselling;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookDetailsActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.detailsBookContainer, new BookDetailsFragment()).commit();

        if (getIntent().getData() != null) {
            String url = getIntent().getData().toString();
            Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

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
                    BookDataModel bookDataModel = singleSnapshot.getValue(BookDataModel.class);
                    Log.e("author", bookDataModel.getAuthor());
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
}
