package com.example.mygallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.activity.VideoPlayerActivity;
import com.example.mygallery.data.model.MyVideo;
import com.example.mygallery.response.ItemClickListener;

import java.io.File;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> implements ItemClickListener {

    private Context context;
    private List<MyVideo> myVideos;

    private static final int VIDEO_PLAYER_ACTIVITY = 2;

    public VideoListAdapter(Context newContext, List<MyVideo> newMyVideos) {
        context = newContext;
        myVideos = newMyVideos;
    }


    @NonNull
    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder result;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_item, parent, false);
        result = new ViewHolder(view);
        return  result;
    }


    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.ViewHolder holder, final int position) {
        Glide.with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(myVideos.get(position).getvContent().getPath())))
                .into(holder.videoView);

        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,(ImageView) v,"videoTransition");
                Intent intent = new Intent(v.getContext(), VideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("VIDEO",myVideos.get(position).getvContent().getVideoId());
                ((Activity) context).startActivityForResult(intent, VIDEO_PLAYER_ACTIVITY, activityOptionsCompat.toBundle());
            }
        });
    }


    @Override
    public int getItemCount() {
        return myVideos.size();
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView videoView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.vidItem);
        }
    }
}

