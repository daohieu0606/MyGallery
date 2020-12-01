package com.example.mygallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private List<Uri> uris;
    private Context context;

    public ImageAdapter(Context newContext, List<Uri> newUris) {
        context = newContext;
        uris = newUris;
    }
    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Object getItem(int position) {
        return uris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_image_list_view, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imgItemListVieww);
        bind(imageView, uris.get(position));
        return convertView;
    }

    private void bind(ImageView imgItem, Uri uri) {
        Glide.with(context.getApplicationContext()).load(uri)
                .apply(new RequestOptions().centerCrop())
                .into(imgItem);
    }
}
