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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    private List<BookDataModel> bookDataModelList;
    private Context mContext;
    private OnItemListener mOnItemListener;

    public SearchAdapter(List<BookDataModel> modelList, Context context,
            OnItemListener mOnItemListener) {
        bookDataModelList = modelList;
        mContext = context;
        this.mOnItemListener = mOnItemListener;
    }

    public static void setImage(BookDataModel bookDataModel, final ImageView cardImageView,
            Context mContext) {

        Picasso.get().load(bookDataModel.getDownloadUri()).placeholder(
                R.drawable.ic_dashboard_black_24dp).into(cardImageView);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        // Return a new view holder
        return new ViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            intent.setData(Uri.parse("tel:9999999999"));
            getCon().startActivity(intent);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        public ImageView cardImageView;
        public TextView titleTextView;
        public TextView subTitleTextView;
        TextView priceTextView;
        OnItemListener onItemListener;
        Button Action1;
        Button Action2;
        ImageButton favouriteButton;

        private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        private DatabaseReference muserReference = mdatabase.getReference().child("users").child(
                mAuth.getCurrentUser().getUid());

        public ViewHolder(@NonNull final View itemView, final OnItemListener onItemListener) {
            super(itemView);
            Action1 = itemView.findViewById(R.id.contactSellerButton);
            Action2 = itemView.findViewById(R.id.shareButton);
            this.onItemListener = onItemListener;
            cardImageView = itemView.findViewById(R.id.bookImageView);
            titleTextView = itemView.findViewById(R.id.nameTextView);
            subTitleTextView = itemView.findViewById(R.id.authorTextView);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            priceTextView = itemView.findViewById(R.id.priceTextView);
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


        public void bindData(final BookDataModel bookDataModel, Context context) {

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


