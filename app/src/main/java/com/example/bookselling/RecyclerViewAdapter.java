package com.example.bookselling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    private List<DataModel> dataModelList;
    private Context mContext;
    private OnItemListener mOnItemListener;

    public RecyclerViewAdapter(List<DataModel> modelList, Context context,OnItemListener mOnItemListener) {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView cardImageView;
        public TextView titleTextView;
        public TextView subTitleTextView;
        OnItemListener onItemListener;

        public MyViewHolder(@NonNull View itemView,OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener=onItemListener;
            cardImageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
            itemView.setOnClickListener(this);


        }


        public void bindData(DataModel dataModel, Context context) {
            /*cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dashboard_black_24dp));
            ti0tleTextView.setText(dataModel.getTitle());
            subTitleTextView.setText(dataModel.getSubTitle());*/

            //cardImageView.setImageBitmap(dataModel.getImage());
//            Log.i("ye ha url",dataModel.getDownloadUri());
          setImage(dataModel,cardImageView);
            titleTextView.setText(dataModel.getTitle());
            subTitleTextView.setText(dataModel.getAuthor());
        }

        @Override
        public void onClick(View v) {
            onItemListener.OnBookClick(getAdapterPosition());
        }
    }


    public static void setImage(DataModel dataModel, final ImageView cardImageView){


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(dataModel.getDownloadUri());

        try {
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
        }

}

public interface OnItemListener{
        void OnBookClick(int position);
    }

}


