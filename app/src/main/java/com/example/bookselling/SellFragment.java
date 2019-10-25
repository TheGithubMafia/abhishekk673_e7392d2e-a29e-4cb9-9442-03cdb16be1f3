package com.example.bookselling;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class SellFragment extends Fragment {

   private String title;
   private String author;
   private String description;
   private int price;
   private Bitmap image;
   private FirebaseDatabase mdatabase;
   private DatabaseReference mBooksReference;
   private FirebaseStorage mFirebaseStorageInstance;
   private StorageReference mStorageReference;
   private Uri selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sell, null);
        mdatabase=FirebaseDatabase.getInstance();
        mBooksReference=mdatabase.getReference().child("books");
        mFirebaseStorageInstance=FirebaseStorage.getInstance();
        mStorageReference=mFirebaseStorageInstance.getReference().child("images");



        FloatingActionButton fab=view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });

        Button doneButton=view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleTV=view.findViewById(R.id.titleTextView);
                EditText authorTV=view.findViewById(R.id.authorTextView);
                EditText descriptionTV=view.findViewById(R.id.descriptionTextView);
                EditText priceTV=view.findViewById(R.id.priceTextView);

                title=titleTV.getText().toString();
                author=authorTV.getText().toString();
                description=descriptionTV.getText().toString();
                price=Integer.parseInt(priceTV.getText().toString());
               // ImageView imageView=getView().findViewById(R.id.imageView2);
               // image= ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                final StorageReference photoref=mStorageReference.child(selectedImage.getLastPathSegment());
              UploadTask uploadTask=  photoref.putFile(selectedImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
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
                            String userId=user.getUid();
                            BookDataModel book=new BookDataModel(title,author,description,price,downloadUri.toString(),userId);

                            mBooksReference.push().setValue(book);
                            Log.i("ye",downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });




               // ExploreFragment.bookDataModelList.add(new BookDataModel(title,author,description,price,downloadImageUri));







            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImage=data.getData();
        if(requestCode==1&&resultCode==RESULT_OK&&selectedImage!=null){
            try{
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImage);
                ImageView imageView=getView().findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }
}
