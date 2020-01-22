package com.example.bookselling;

import static android.app.Activity.RESULT_CANCELED;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 422;
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
    private int GALLERY = 1, CAMERA = 2;

    private Uri selectedImage;
    private Bitmap selectedImageBitmap;
    private ImageView userImageView;

//
//    private FirebaseDatabase mdatabase=FirebaseDatabase.getInstance();
//    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private DatabaseReference muserReference=mdatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, null);
        setHasOptionsMenu(true);

        FloatingActionButton imageButton = view.findViewById(R.id.floatingActionButton);
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
        userImageView = view.findViewById(R.id.userImageView);

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance();
        mUsersReference = mdatabase.getReference().child("users");
        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorageInstance.getReference().child("Userimages");


        emailTV.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        setValues();


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

        if (accountValidate()) {


            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("hostel name").setValue(
                    hostelName);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("phone number").setValue(
                    phoneNumber);
            mUsersReference.child(mAuth.getCurrentUser().getUid()).child("roll number").setValue(
                    rollNumber);


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
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child(
                                "image url").setValue(downloadUri.toString());

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }

    }

    private boolean accountValidate() {
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
        if(selectedImage==null||selectedImage.toString().isEmpty()){
            Toast.makeText(getContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
            valid=false;
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {

        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.parse("file:///sdcard/photo.jpg");
        photo.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(photo, CAMERA);

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
            selectedImage = data.getData();
            try {
                 selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                        selectedImage);
                userImageView.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (requestCode == CAMERA) {

            File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
            Uri uri = Uri.fromFile(file);
            selectedImage = uri;
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                //  bitmap = cropAndScale(bitmap, 300); // if you mind scaling
                ImageView imageView = getView().findViewById(R.id.imageView2);
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
//            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest
//            .permission.WRITE_EXTERNAL_STORAGE);
//
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
//            } else {
//
//            }


        }

    }

    private void setValues(){

        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName=dataSnapshot.getValue(String.class);
                nameTextView.setText(userName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("hostel name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String hostelName=dataSnapshot.getValue(String.class);
                hostelTextView.setText(hostelName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("roll number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rollNumber=dataSnapshot.getValue(String.class);
                rollNumbertextView.setText(rollNumber);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("phone number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phoneNumber=dataSnapshot.getValue(String.class);
                phonenumbertextView.setText(phoneNumber);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("image url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url=dataSnapshot.getValue(String.class);

                if(url!=null)
                    if(!url.isEmpty()){
                Picasso.with(getContext()).load(url).placeholder(R.drawable.ic_round_account_button_with_user_inside).into(userImageView);
                    selectedImage=Uri.parse(url);}
                //Log.e("user image url",url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
