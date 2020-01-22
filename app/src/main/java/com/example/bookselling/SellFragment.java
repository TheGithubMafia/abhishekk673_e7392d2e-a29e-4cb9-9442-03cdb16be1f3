package com.example.bookselling;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

public class SellFragment extends Fragment {

    FirebaseAuth mAuth;
    private String title;
    private String author;
    private String description;
    private int price;
    private Bitmap image;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mBooksReference;
    private FirebaseStorage mFirebaseStorageInstance;
    private StorageReference mStorageReference;
    private DatabaseReference muserReference;
    private Uri selectedImage;
    private View view;
    private EditText titleTV;
    private EditText authorTV;
    private EditText descriptionTV;
    private EditText priceTV;
    private boolean profileCompleted=true;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sell, null);

        setHasOptionsMenu(true);

        mdatabase = FirebaseDatabase.getInstance();
        mBooksReference = mdatabase.getReference().child("books");
        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorageInstance.getReference().child("images");


        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        muserReference = mdatabase.getReference().child("users").child(
                mAuth.getCurrentUser().getUid());

            return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && selectedImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                        selectedImage);
                ImageView imageView = getView().findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.sell_menu_done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.doneButton:
                postBook(view);
        }
        return super.onOptionsItemSelected(item);
    }


    private void postBook(View view) {
        titleTV = view.findViewById(R.id.titleTextView);
        authorTV = view.findViewById(R.id.authorTextView);
        descriptionTV = view.findViewById(R.id.descriptionTextView);
        priceTV = view.findViewById(R.id.priceTextView);

        title = titleTV.getText().toString();
        author = authorTV.getText().toString();
        description = descriptionTV.getText().toString();
        if (!priceTV.getText().toString().isEmpty()) {
            price = Integer.parseInt(priceTV.getText().toString());
        }

        // ImageView imageView=getView().findViewById(R.id.imageView2);
        // image= ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        muserReference.child("image url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) profileCompleted = false;
                else  profileCompleted=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!profileCompleted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please provide your details in account section!")
                    .setTitle("Complete your profile first");

// Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button

                }
            });

// Set other dialog properties


// Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();


        }else  if (validate()) {

                final StorageReference photoref = mStorageReference.child(
                        selectedImage.getLastPathSegment());
                UploadTask uploadTask = photoref.putFile(selectedImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                    throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return photoref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();
                            BookDataModel book = new BookDataModel(title, author, description,
                                    price,
                                    downloadUri.toString(), userId);

                            mBooksReference.push().setValue(book);
                            Log.i("ye", downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

                //*dirty trick to make dyno of heroku active on posting new book*//
                new Thread() {
                    public void run() {

                        try {
                            URL url = new URL("https://tranquil-mountain-80007.herokuapp.com/");

                            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                            urlc.setConnectTimeout(1000 * 30);// mTimeout is in seconds
                            urlc.connect();
                            urlc.getContent();
                            Log.e("ping", "heroku00");

                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        }



    private boolean validate() {

        boolean valid = true;

        if (title.isEmpty()) {
            titleTV.setError("Please enter a title");
            valid = false;
        }
        if (author.isEmpty()) {
            authorTV.setError("Please enter an author");
            valid = false;
        }
        if (description.isEmpty()) {
            descriptionTV.setError("Please enter a description");
            valid = false;
        }
        if (priceTV.getText().toString().isEmpty()) {
            priceTV.setError("Please enter the price of book");
            valid = false;
        }
        if (selectedImage == null) {
            Toast.makeText(getContext(), "Please upload an image of book!",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
}
