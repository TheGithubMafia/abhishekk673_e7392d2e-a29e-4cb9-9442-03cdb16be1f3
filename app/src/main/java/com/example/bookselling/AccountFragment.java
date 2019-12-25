package com.example.bookselling;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class AccountFragment extends Fragment {

    View view;

    private TextView hostelTextView;
    private TextView nameTextView;
    private TextView rollNumbertextView;
    private TextView phonenumbertextView;
    private TextView emailTV;
    private DatabaseReference mUsersReference;
    private FirebaseDatabase mdatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage mFirebaseStorageInstance;
    private StorageReference mStorageReference;


    private String hostelName;
    private String name;
    private String phoneNumber;
    private String rollNumber;

     private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 422;
    private int GALLERY = 1, CAMERA = 2;

    private Uri selectedImage;
    private   Bitmap selectedImageBitmap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, null);
        setHasOptionsMenu(true);

        FloatingActionButton imageButton=view.findViewById(R.id.floatingActionButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showPictureDialog();
                choosePhotoFromGallery();
            }
        });


        hostelTextView = view.findViewById(R.id.hostelTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        rollNumbertextView = view.findViewById(R.id.rollNumberTextView);
        phonenumbertextView = view.findViewById(R.id.phoneTextView);
        emailTV = view.findViewById(R.id.emailTV);

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance();
        mUsersReference = mdatabase.getReference().child("users");
        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorageInstance.getReference().child("Userimages");


        emailTV.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button myBooksButton = view.findViewById(R.id.myBooksButton);
        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyBooks.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.account_save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.saveButton:
                postUser();
        }
        return super.onOptionsItemSelected(item);
    }


    private void postUser() {

        if (validate()) {


            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("hostel name").setValue(hostelName);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("phone number").setValue(phoneNumber);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("roll number").setValue(rollNumber);


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
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("image url").setValue(downloadUri.toString());

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }

    }

    private boolean validate() {
        boolean valid = true;


        hostelName = hostelTextView.getText().toString();
        name = nameTextView.getText().toString();
        phoneNumber = phonenumbertextView.getText().toString();
        rollNumber = rollNumbertextView.getText().toString();


        if (hostelName.isEmpty()) {
            hostelTextView.setError("please enter your hostel name");
            valid = false;
        }

        if (name.isEmpty()) {
            nameTextView.setError("please enter your name");
        }

        if (phoneNumber.isEmpty() || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            phonenumbertextView.setError("Please enter a valid phone number");
        }

        if (rollNumber.isEmpty()) {
            rollNumbertextView.setError("please enter your roll number");
        }

        return valid;
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {

        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri  = Uri.parse("file:///sdcard/photo.jpg");
        photo.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(photo,CAMERA);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            selectedImage=data.getData();
            try{
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImage);
                ImageView imageView=getView().findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }


        } else if (requestCode == CAMERA) {

            File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
            Uri uri = Uri.fromFile(file);
            selectedImage=uri;
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
              //  bitmap = cropAndScale(bitmap, 300); // if you mind scaling
                ImageView imageView=getView().findViewById(R.id.imageView2);
            imageView.setImageBitmap(bitmap);
               // profileImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            selectedImage=data.getData();
//            selectedImageBitmap = (Bitmap) data.getExtras().get("data");
//            ImageView imageView=getView().findViewById(R.id.imageView2);
//            imageView.setImageBitmap(selectedImageBitmap);
//
//            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
//            } else {
//
//            }



        }

    }








}
