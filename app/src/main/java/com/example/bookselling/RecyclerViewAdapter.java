package com.example.bookselling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {



    private List<DataModel> dataModelList;
    private Context mContext;
    private OnItemListener mOnItemListener;

    public RecyclerViewAdapter(List<DataModel> modelList, Context context, OnItemListener mOnItemListener) {
        dataModelList = modelList;
        mContext = context;
        this.mOnItemListener=mOnItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // Return a new view holder
        return new MyViewHolder(view,mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(dataModelList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    // View holder class whose objects represent each list item

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView cardImageView;
        public TextView titleTextView;
        public TextView subTitleTextView;
        OnItemListener onItemListener;
        Button Action1;
        Button Action2;
        ImageButton favouriteButton;

        private FirebaseDatabase mdatabase=FirebaseDatabase.getInstance();
        private FirebaseAuth mAuth=FirebaseAuth.getInstance();

        private DatabaseReference muserReference=mdatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        public MyViewHolder(@NonNull final View itemView, final OnItemListener onItemListener) {
            super(itemView);
            Action1=itemView.findViewById(R.id.action_button_1);
            Action2=itemView.findViewById(R.id.action_button_2);
            this.onItemListener=onItemListener;
            cardImageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
            favouriteButton=itemView.findViewById(R.id.favouriteButton);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);



            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.OnFavButtonClick(getAdapterPosition(),itemView);
                }
            });
            Action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.OnButton1Click(getAdapterPosition(),itemView);
                }
            });
            Action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.OnButton2Click(getAdapterPosition(),itemView);
                }
            });


        }


        public void bindData(final DataModel dataModel, Context context) {
            /*cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dashboard_black_24dp));
            ti0tleTextView.setText(dataModel.getTitle());
            subTitleTextView.setText(dataModel.getSubTitle());*/

            //cardImageView.setImageBitmap(dataModel.getImage());
//            Log.i("ye ha url",dataModel.getDownloadUri());

            muserReference.child("Favourites").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(dataModel.getRefKey()))
                        favouriteButton.setImageResource(R.drawable.ic_favorite_orange_24dp);
                    else favouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            setImage(dataModel,cardImageView,context);
            titleTextView.setText(dataModel.getTitle());
            subTitleTextView.setText(dataModel.getAuthor());
        }

        @Override
        public void onClick(View v) {
            onItemListener.OnBookClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
          onItemListener.OnBookLongClick(getAdapterPosition(),view);
          return true;
        }
    }


    public static void setImage(DataModel dataModel, final ImageView cardImageView,Context mContext){


       // FirebaseStorage storage=FirebaseStorage.getInstance();
        //StorageReference httpsReference = storage.getReferenceFromUrl(dataModel.getDownloadUri());


        Picasso.with(mContext).load(dataModel.getDownloadUri()).placeholder(R.drawable.ic_dashboard_black_24dp).into(cardImageView);


       /* try {
            final File localFile = File.createTempFile("images", "jpg");

            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    cardImageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }*/

}

public interface OnItemListener{
        void OnBookClick(int position);
        void OnBookLongClick(int position,View view);
        void OnButton1Click(int position,View view);
    void OnButton2Click(int position,View view);
    void OnFavButtonClick(int position,View view);
    }

}


