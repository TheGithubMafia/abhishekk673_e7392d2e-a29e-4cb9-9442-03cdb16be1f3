package com.example.bookselling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyBooks extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener,PopupMenu.OnMenuItemClickListener  {

    private List<DataModel> MyBooksList;
    private int selectedItem;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mBooksReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explore);

        MyBooksList=new ArrayList<>();

        mdatabase= FirebaseDatabase.getInstance();
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

        mAdapter = new RecyclerViewAdapter(MyBooksList,this ,this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }


    private void addItems(){
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataModel dataModel=dataSnapshot.getValue(DataModel.class);
                dataModel.setRefKey(dataSnapshot.getKey());
                MyBooksList.add(dataModel);
                mAdapter.notifyDataSetChanged();
                Log.i("expfr",dataModel.getDownloadUri());
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

        mBooksReference.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(mChildEventListener);

    }

    @Override
    public void OnBookClick(int position) {

    }

    @Override
    public void OnBookLongClick(int position, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        selectedItem=position;
        inflater.inflate(R.menu.pop_up_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                //archive(item);
                mBooksReference.child(MyBooksList.get(selectedItem).getRefKey()).removeValue();
                MyBooksList.remove(selectedItem);

                mAdapter.notifyDataSetChanged();
                Toast.makeText(this,"deleted"+selectedItem,Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}
