package com.example.mygallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygallery.R;
import com.example.mygallery.activity.MainActivity;
import com.example.mygallery.response.ItemClickListener;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private MainActivity mainActivity;
    private List<String> Catalogues;
    private Fragment fragment;
    private View selectedItem;

    private static final String CHANGE_PHOTO_CATALOG = "CHANGE_PHOTO_CATALOG";
    private static final String CHANGE_VIDEO_CATALOG = "CHANGE_VIDEO_CATALOG";

    public CatalogAdapter(MainActivity newContext, Fragment newFragment, List<String> newCatalogues) {
        mainActivity = newContext;
        Catalogues = newCatalogues;
        fragment = newFragment;
        selectedItem = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder result;

        LayoutInflater inflater = LayoutInflater.from(mainActivity.getApplicationContext());
        View view = inflater.inflate(R.layout.item_catalogue, parent, false);

        result = new ViewHolder(view);
        return  result;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String Catalogue = Catalogues.get(position);
        holder.txtCatalogue.setText(Catalogue);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick)
                    Toast.makeText(mainActivity.getApplicationContext(), "Xu ly su kien long click o day ", Toast.LENGTH_SHORT).show();
                else {
                    if (fragment.getClass().toString().contains("PhotoFragment")) {
                        mainActivity.onMsgToMain(CHANGE_PHOTO_CATALOG, Catalogue);
                    } else {
                        mainActivity.onMsgToMain(CHANGE_VIDEO_CATALOG, Catalogue);
                    }
                   // changeSelectedCatalogueBackground(holder.txtCatalogue);
                }
            }
        });
/*
        if (position == 0) {
            selectedItem = holder.txtCatalogue;
            selectedItem.setBackgroundResource(R.drawable.prime_background);
        }*/
    }
/*
    private void changeSelectedCatalogueBackground(View newSelectedView) {
        if (selectedItem != null) {
            selectedItem.setBackgroundResource(R.drawable.prime_frame);
        }
        selectedItem = newSelectedView;
        selectedItem.setBackgroundResource(R.drawable.prime_background);
    }*/

    private void setSelectedItemBackground(int newPosition) {
        if (newPosition < 0 || newPosition >= Catalogues.size()) {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return Catalogues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView txtCatalogue;
        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCatalogue = itemView.findViewById(R.id.txtCatalogue);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener newItemClickListener)
        {
            itemClickListener = newItemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),true);
            return true;
        }
    }
}
