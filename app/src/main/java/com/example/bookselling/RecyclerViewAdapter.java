package com.example.bookselling;

import static com.example.bookselling.ExploreFragment.inAnimation;
import static com.example.bookselling.ExploreFragment.mAuth;
import static com.example.bookselling.ExploreFragment.mUsersReference;
import static com.example.bookselling.ExploreFragment.outAnimation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    static private String phone;
    public Context mContext;
    private List<BookDataModel> bookDataModelList;
    private OnItemListener mOnItemListener;


    public RecyclerViewAdapter(List<BookDataModel> modelList, Context context,
            OnItemListener mOnItemListener) {
        bookDataModelList = modelList;
        mContext = context;
        this.mOnItemListener = mOnItemListener;
    }

    public static void setImage(BookDataModel bookDataModel, final ImageView cardImageView,
            Context mContext) {


        // FirebaseStorage storage=FirebaseStorage.getInstance();
        //StorageReference httpsReference = storage.getReferenceFromUrl(bookDataModel
        // .getDownloadUri());


        Picasso.with(mContext).load(bookDataModel.getDownloadUri()).placeholder(
                R.drawable.ic_dashboard_black_24dp).into(cardImageView);


       /* try {
            final File localFile = File.createTempFile("images", "jpg");

            httpsReference.getFile(localFile).addOnSuccessListener(new
            OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // Return a new view holder
        return new MyViewHolder(view, mOnItemListener, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(bookDataModelList.get(position), mContext);
    }

    // View holder class whose objects represent each list item

    @Override
    public int getItemCount() {
        return bookDataModelList.size();
    }


    public interface OnItemListener {
        default void OnBookClick(int position) {
            Log.i("Book", Integer.toString(position));

            Intent intent = new Intent(getCon(), BookDetailsActivity.class);
            intent.putExtra("position", position);
            getCon().startActivity(intent);
        }

        void OnBookLongClick(int position, View view);

        default void OnButton1Click(int position, View view) {


            Intent intent = new Intent(Intent.ACTION_DIAL);
            mUsersReference.child(getBookDataModelList().get(position).getUserId()).child(
                    "phone number").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            phone = dataSnapshot.getValue().toString();
                            if (!phone.isEmpty()) {
                                Log.e("phoje", phone);
                                intent.setData(Uri.parse("tel:" + phone));
                                getCon().startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }


        void OnButton2Click(int position, View view);

        default void OnFavButtonClick(int position, View view) {
            final ImageButton btn = view.findViewById(R.id.favouriteButton);
            outAnimation.setAnimationListener(new Animation.AnimationListener() {

                // Other callback methods omitted for clarity.

                @Override
                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {

                    // Modify the resource of the ImageButton
                    Drawable unselected = getCon().getResources().getDrawable(
                            R.drawable.ic_favorite_black_24dp);
                    Drawable selected = getCon().getResources().getDrawable(
                            R.drawable.ic_favorite_orange_24dp);
                    Drawable btnDrawable = btn.getDrawable();

                    if (btnDrawable.getConstantState() == unselected.getConstantState()) {

                        btn.setImageResource(R.drawable.ic_favorite_orange_24dp);
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child(
                                "Favourites").child(
                                getBookDataModelList().get(position).getRefKey()).setValue("True");

                        // Create the new Animation to apply to the ImageButton.
                        btn.startAnimation(inAnimation);
                    } else {
                        btn.setImageResource(R.drawable.ic_favorite_black_24dp);
                        btn.startAnimation(inAnimation);
                        mUsersReference.child(mAuth.getCurrentUser().getUid()).child(
                                "Favourites").child(
                                getBookDataModelList().get(position).getRefKey()).removeValue();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        public ImageView cardImageView;
        public TextView titleTextView;
        public TextView subTitleTextView;
        OnItemListener onItemListener;
        Button Action1;
        Button Action2;
        ImageButton favouriteButton;
        ImageView sellerImageView;
        TextView sellerNameTextView;
        Context viewHolderContext;


        private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        private DatabaseReference muserReference = mdatabase.getReference().child("users").child(
                mAuth.getCurrentUser().getUid());

        public MyViewHolder(@NonNull final View itemView, final OnItemListener onItemListener,
                Context viewHolderContext) {
            super(itemView);
            Action1 = itemView.findViewById(R.id.action_button_1);
            this.viewHolderContext = viewHolderContext;
            Action2 = itemView.findViewById(R.id.action_button_2);
            this.onItemListener = onItemListener;
            cardImageView = itemView.findViewById(R.id.bookImageView);
            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            sellerImageView = itemView.findViewById(R.id.sellerImageView);
            sellerNameTextView = itemView.findViewById(R.id.sellerNameTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    onItemListener.OnFavButtonClick(getAdapterPosition(), itemView);
                }
            });
            Action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.OnButton1Click(getAdapterPosition(), itemView);
                }
            });
            Action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.OnButton2Click(getAdapterPosition(), itemView);
                }
            });


        }


        public void bindData(final BookDataModel bookDataModel, final Context context) {
            /*cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable
            .ic_dashboard_black_24dp));
            ti0tleTextView.setText(bookDataModel.getTitle());
            subTitleTextView.setText(bookDataModel.getSubTitle());*/

            //cardImageView.setImageBitmap(bookDataModel.getImage());
//            Log.i("ye ha url",bookDataModel.getDownloadUri());


            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference muserReference = mdatabase.getReference().child("users").child(
                    mAuth.getCurrentUser().getUid());
            DatabaseReference msellerReference = mdatabase.getReference().child("users").child(
                    bookDataModel.getUserId());

            muserReference.child("Favourites").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(bookDataModel.getRefKey())) {
                                favouriteButton.setImageResource(
                                        R.drawable.ic_favorite_orange_24dp);
                            } else {
                                favouriteButton.setImageResource(
                                        R.drawable.ic_favorite_black_24dp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            setImage(bookDataModel, cardImageView, context);
            titleTextView.setText(bookDataModel.getTitle());
            subTitleTextView.setText(bookDataModel.getAuthor());

            msellerReference.child("image url").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.getValue(String.class);

                            Picasso.with(context).load(url).placeholder(
                                    R.drawable.ic_round_account_button_with_user_inside).into(
                                    sellerImageView);
//                    Log.e("user image url",url);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            msellerReference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);
                    sellerNameTextView.setText(userName);

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
            onItemListener.OnBookLongClick(getAdapterPosition(), view);
            return true;
        }
    }

}


