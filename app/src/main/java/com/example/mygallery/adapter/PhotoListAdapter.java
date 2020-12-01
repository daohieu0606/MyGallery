package com.example.mygallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.activity.PhotoPlayerActivity;
import com.example.mygallery.data.model.MyPicture;

import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {

    private Context context;
    private List<MyPicture> myPictures;

    private static final int PHOTO_PLAYER_ACTIVITY = 1;

    public PhotoListAdapter(Context newContext, List<MyPicture> newMyPictures) {
        context = newContext;
        myPictures = newMyPictures;
    }


    @NonNull
    @Override
    public PhotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder result;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.photo_item, parent, false);
        result = new ViewHolder(view);
        return  result;
    }

    public void updateData(List<MyPicture> newPics) {
        myPictures = newPics;
    }
    @Override
    public void onBindViewHolder(@NonNull PhotoListAdapter.ViewHolder holder, final int position) {
        Glide.with(context)
                .load(myPictures.get(position).getpContent().getPicturePath())
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,(ImageView) v,"imageTransition");
                Intent intent = new Intent(context, PhotoPlayerActivity.class);
                intent.putExtra("IMAGE_ID", myPictures.get(position).getpContent().getPictureId());
                ((Activity) context).startActivityForResult(intent, PHOTO_PLAYER_ACTIVITY, activityOptionsCompat.toBundle());
            }
        });
    }


    @Override
    public int getItemCount() {
        return myPictures.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imgPhotoItem);
        }
    }

}

