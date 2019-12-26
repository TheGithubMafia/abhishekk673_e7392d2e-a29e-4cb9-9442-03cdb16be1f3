package com.example.bookselling;

import static com.example.bookselling.ExploreFragment.inAnimation;
import static com.example.bookselling.ExploreFragment.mAuth;
import static com.example.bookselling.ExploreFragment.mUsersReference;
import static com.example.bookselling.ExploreFragment.outAnimation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {



    private List<BookDataModel> bookDataModelList;
    public Context mContext;
    private OnItemListener mOnItemListener;




    public RecyclerViewAdapter(List<BookDataModel> modelList, Context context, OnItemListener mOnItemListener) {
        bookDataModelList = modelList;
        mContext = context;
        this.mOnItemListener=mOnItemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // Return a new view holder
        return new MyViewHolder(view,mOnItemListener,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(bookDataModelList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return bookDataModelList.size();
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
        ImageView userImageview;
        TextView userNameTextView;
        Context viewHolderContext;

        private FirebaseDatabase mdatabase=FirebaseDatabase.getInstance();
        private FirebaseAuth mAuth=FirebaseAuth.getInstance();

        private DatabaseReference muserReference=mdatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        public MyViewHolder(@NonNull final View itemView, final OnItemListener onItemListener,Context viewHolderContext) {
            super(itemView);
            Action1=itemView.findViewById(R.id.action_button_1);
            this.viewHolderContext=viewHolderContext;
            Action2=itemView.findViewById(R.id.action_button_2);
            this.onItemListener=onItemListener;
            cardImageView = itemView.findViewById(R.id.bookImageView);
            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
            favouriteButton=itemView.findViewById(R.id.favouriteButton);
            userImageview=itemView.findViewById(R.id.userImageView);
            userNameTextView=itemView.findViewById(R.id.userNameTextView);
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


        public void bindData(final BookDataModel bookDataModel, final Context context) {
            /*cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dashboard_black_24dp));
            ti0tleTextView.setText(bookDataModel.getTitle());
            subTitleTextView.setText(bookDataModel.getSubTitle());*/

            //cardImageView.setImageBitmap(bookDataModel.getImage());
//            Log.i("ye ha url",bookDataModel.getDownloadUri());


            FirebaseDatabase mdatabase=FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference muserReference=mdatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

            muserReference.child("Favourites").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(bookDataModel.getRefKey()))
                        favouriteButton.setImageResource(R.drawable.ic_favorite_orange_24dp);
                    else favouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            setImage(bookDataModel,cardImageView,context);
            titleTextView.setText(bookDataModel.getTitle());
            subTitleTextView.setText(bookDataModel.getAuthor());

            muserReference.child("image url").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   String url=dataSnapshot.getValue(String.class);

                   Picasso.with(context).load(url).placeholder(R.drawable.ic_round_account_button_with_user_inside).into(userImageview);
                    Log.e("user image url",url);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            muserReference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName=dataSnapshot.getValue(String.class);
                    userNameTextView.setText(userName);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        @Override
        public void onClick(View v) {
            onItemListener.OnBookClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(viewHolderContext, "long clik" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
          onItemListener.OnBookLongClick(getAdapterPosition(),view);
          return true;
        }
    }


    public static void setImage(BookDataModel bookDataModel, final ImageView cardImageView, Context mContext){


       // FirebaseStorage storage=FirebaseStorage.getInstance();
        //StorageReference httpsReference = storage.getReferenceFromUrl(bookDataModel.getDownloadUri());


        Picasso.with(mContext).load(bookDataModel.getDownloadUri()).placeholder(R.drawable.ic_dashboard_black_24dp).into(cardImageView);


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
        default void OnBookClick(int position){
            Log.i("Book", Integer.toString(position));

            Intent intent = new Intent(getCon(), BookDetailsActivity.class);
            intent.putExtra("position", position);
            getCon().startActivity(intent);
        }
        void OnBookLongClick(int position,View view);

        default void OnButton1Click(int position,View view){
            Toast.makeText(getCon(), "1" + position, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:9999999999"));
            getCon().startActivity(intent);
        }



        void OnButton2Click(int position,View view);

        default void OnFavButtonClick(int position,View view){
            final ImageButton btn = view.findViewById(R.id.favouriteButton);
            outAnimation.setAnimationListener(new Animation.AnimationListener() {

                // Other callback methods omitted for clarity.

                @Override
                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {

                    // Modify the resource of the ImageButton
                    Drawable unselected = getCon().getResources().getDrawable(R.drawable.ic_favorite_black_24dp);
                    Drawable selected = getCon().getResources().getDrawable(R.drawable.ic_favorite_orange_24dp);
                    Drawable btnDrawable = btn.getDrawable();

                    if (btnDrawable.getConstantState() == unselected.getConstantState()) {

                        btn.setImageResource(R.drawable.ic_favorite_orange_24dp);
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(getBookDataModelList().get(position).getRefKey()).setValue("True");

                        // Create the new Animation to apply to the ImageButton.
                        btn.startAnimation(inAnimation);
                    } else {
                        btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                        btn.startAnimation(inAnimation);
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child("Favourites").child(getBookDataModelList().get(position).getRefKey()).removeValue();
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            btn.startAnimation(outAnimation);
        }
        Context getCon();
        List<BookDataModel> getBookDataModelList();
    }

}


