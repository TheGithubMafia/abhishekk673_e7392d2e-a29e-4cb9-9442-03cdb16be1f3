package com.example.bookselling;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
                Log.i("fsd",dataSnapshot.getValue().toString());
                DataModel dataModel=dataSnapshot.getValue(DataModel.class);
                dataModel.setRefKey(dataSnapshot.getKey());
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
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(dataModelList.get(position).getRefKey()).setValue("True");

                    // Create the new Animation to apply to the ImageButton.
                    btn.startAnimation(inAnimation);
                }

                else{
                    btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                    btn.startAnimation(inAnimation);
                    mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(dataModelList.get(position).getRefKey()).removeValue();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
            btn.startAnimation(outAnimation);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.explore_action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent=new Intent(getContext(),FavouritesActivity.class);
                startActivity(intent);
                Log.i("asdad","sad");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

