package com.example.bookselling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener{
    private FirebaseAuth mAuth;
    private FirebaseDatabase mdatabase;
    private DatabaseReference muserReference;
    private DatabaseReference mBooksReference;
    private ChildEventListener mChildEventListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<DataModel> favouritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explore);

        favouritesList=new ArrayList<>();

        mdatabase=FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        muserReference=mdatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mBooksReference=mdatabase.getReference().child("books");

        addItems();



        /*for (int i = 1; i <= 20; ++i) {
            dataModelList.add(new DataModel(i));
        }*/

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // specify an adapter and pass in our data model list

        mAdapter = new RecyclerViewAdapter(favouritesList,this ,this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();




    }
    private void addItems(){
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("idk",dataSnapshot.getKey());
                mBooksReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i("gdgf",dataSnapshot.getValue().toString());
                        DataModel dataModel=dataSnapshot.getValue(DataModel.class);
                        dataModel.setRefKey(dataSnapshot.getKey());
                        Log.i("gdgf",dataModel.getAuthor());
                        favouritesList.add(dataModel);
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
    public void OnBookClick(int position) {

    }

    @Override
    public void OnBookLongClick(int position, View view) {

    }

    @Override
    public void OnButton1Click(int position, View view) {

    }

    @Override
    public void OnButton2Click(int position, View view) {

    }

    @Override
    public void OnFavButtonClick(int position, View view) {

    }
}
