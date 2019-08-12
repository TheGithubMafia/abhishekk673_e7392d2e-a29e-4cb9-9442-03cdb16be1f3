package com.example.bookselling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class ExploreFragment extends Fragment implements RecyclerViewAdapter.OnItemListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static public List<DataModel> dataModelList;

    private FirebaseDatabase mdatabase;
    private DatabaseReference mBooksReference;
    private ChildEventListener mChildEventListener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_explore,null);
        mRecyclerView = view.findViewById(R.id.recyclerView);

Log.i("abc","Oncreateview");


      //

        mdatabase=FirebaseDatabase.getInstance();
        mBooksReference=mdatabase.getReference().child("books");

     addItems();



        /*for (int i = 1; i <= 20; ++i) {
            dataModelList.add(new DataModel(i));
        }*/

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView

       mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // specify an adapter and pass in our data model list

        mAdapter = new RecyclerViewAdapter(dataModelList, getContext(),this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }

    private void addItems(){
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataModel dataModel=dataSnapshot.getValue(DataModel.class);
                dataModelList.add(dataModel);
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
    public void OnBookLongClick(int position) {
        Toast.makeText(getContext(),"long clik"+position,Toast.LENGTH_SHORT).show();
    }


}

