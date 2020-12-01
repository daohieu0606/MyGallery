package com.example.mygallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;
import com.example.mygallery.activity.VideoPlayerActivity;
import com.example.mygallery.data.model.MyVideo;
import com.example.mygallery.response.ItemClickListener;

import java.util.List;

public class HorizontalVideoListAdapter extends RecyclerView.Adapter<HorizontalVideoListAdapter.NearestPhotoViewHolder> {

    private List<MyVideo> thumbnails;
    private VideoPlayerActivity activity;

    private static final String TAG = "NearestVideoAdapter";

    public HorizontalVideoListAdapter(List<MyVideo> newThumbnails, VideoPlayerActivity newActivity) {
        thumbnails = newThumbnails;
        activity = newActivity;
    }

    @NonNull
    @Override
    public NearestPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NearestPhotoViewHolder result;

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.nearest_photo_item, parent, false);

        result = new NearestPhotoViewHolder(view);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull NearestPhotoViewHolder holder, int position) {
        ImageView imgItem = holder.imageView;
        bind(imgItem, thumbnails.get(position).getvContent().getPath());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                int currentVideoID = (int) thumbnails.get(position).getvContent().getVideoId();
                activity.onMsgToMain(TAG, String.valueOf(currentVideoID));
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    public class NearestPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView imageView;
        private ItemClickListener itemClickListener;

        public NearestPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgNearestItem);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener newItemClickListener)
        {
            itemClickListener = newItemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    private void bind(ImageView imgItem, String path) {
        Glide.with(activity).load(path)
                .apply(new RequestOptions().centerCrop())
                .into(imgItem);
    }
}
