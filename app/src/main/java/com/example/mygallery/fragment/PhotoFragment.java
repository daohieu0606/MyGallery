package com.example.mygallery.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.mygallery.activity.MainActivity;
import com.example.mygallery.adapter.AutoFitGridLayoutManager;
import com.example.mygallery.adapter.CatalogAdapter;
import com.example.mygallery.R;
import com.example.mygallery.adapter.PhotoListAdapter;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.DAO.ImageDAOImpl;
import com.example.mygallery.data.filter.FileFilter;
import com.example.mygallery.data.model.MyPicture;
import com.example.mygallery.data.sorthelper.PhotoComparator;
import com.example.mygallery.data.sorthelper.SortingMode;
import com.example.mygallery.response.FragCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoFragment extends Fragment implements FragCallBack {

    private static final String UPDATE_PICTURE_LIST_SIZE = "UPDATE_PICTURE_LIST_SIZE";
    private static final String CHANGE_PHOTO_CATALOG = "CHANGE_PHOTO_CATALOG";
    private static final String SAVED_SEARCH_STRING = "SAVED_SEARCH_STRING";
    private static final String SAVED_POPUP_MENU = "SAVED_POPUP_MENU";
    private static final String TAG = "photofrag";

    private RecyclerView recyclerView;
    private List<String> photoCatalogues;
    private CatalogAdapter catalogAdapter;

    private RecyclerView photoGridView;
    private PhotoListAdapter photoListAdapter;
    private List<MyPicture> currentPictureList;

    private SearchView searchView;
    private ImageDAOImpl imageDAO;
    private MainActivity activity;
    private PopupMenu sortingMenu;

    private TextView txtPictureCount;

    private String currentCatalogue;
    private String searchQuery;
    private boolean isShowingPopupMenu;
    private boolean isFinishedOnPause;

    public PhotoFragment() {
    }

    public PhotoFragment(MainActivity newActivity) {
        activity = newActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activity == null) {
            activity = (MainActivity) getActivity();
        }

        imageDAO = new ImageDAOImpl(activity);
        currentPictureList = imageDAO.getAllMediaFiles();
        currentCatalogue = "All";
        photoCatalogues = new ArrayList<>();
        photoCatalogues.add("All");
        for (MyPicture pic:
                currentPictureList) {
            String picPath = pic.getpContent().getPicturePath();
            String picFolder = FileHelper.getFolder(picPath);
            if (!photoCatalogues.contains(picFolder)) {
                photoCatalogues.add(picFolder);
            }
        }
        isFinishedOnPause = false;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem( R.id.btnSearch ).getActionView();
        if (!TextUtils.isEmpty(searchQuery)) {
            searchView.setIconified(false);
            menu.findItem(R.id.btnSearch).expandActionView();
            searchView.setQuery(searchQuery, false);
            changePhotoCatalog(currentCatalogue, searchQuery);
        } else if (isShowingPopupMenu) {
            isShowingPopupMenu = false;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    sortingMenu.show();
                }
            }, 10);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    searchQuery = null;
                } else {
                    searchQuery = query;
                }
                return  true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                changePhotoCatalog(currentCatalogue, newText);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.btnSorting:
                sortingMenu.show();
                isShowingPopupMenu = true;
                break;
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        setUp(view);
        View anchor = view.findViewById(R.id.photoAnchor);
        createSortingMenu(anchor);

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SAVED_SEARCH_STRING);
            isShowingPopupMenu = savedInstanceState.getBoolean(SAVED_POPUP_MENU);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SEARCH_STRING, searchQuery);
        outState.putBoolean(SAVED_POPUP_MENU, isShowingPopupMenu);
    }

    @SuppressLint("WrongConstant")
    private void setUp(View view) {
        setUpPhotoGridView(view);
        setUpCatalogue(view);

        txtPictureCount = view.findViewById(R.id.txtPictureCount);
    }

    @Override
    public void onResume() {
        super.onResume();
        txtPictureCount.setText("Number of pictures: "+ String.valueOf(currentPictureList.size()));
    }

    @Override
    public void onPause() {
        super.onPause();
        isFinishedOnPause = true;
        sortingMenu.dismiss();
    }

    private void createSortingMenu(View anchor) {
        sortingMenu = new PopupMenu(getContext(), anchor);
        sortingMenu.getMenuInflater().inflate(R.menu.menu_sort_type, sortingMenu.getMenu());
        sortingMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_sort_by_name:
                        processSorting(SortingMode.NAME);
                        break;
                    case  R.id.item_sort_by_added_date:
                        processSorting(SortingMode.DATE);
                        break;
                    case  R.id.item_sort_by_size:
                        processSorting(SortingMode.SIZE);
                        break;
                    case R.id.item_sort_by_type:
                        processSorting(SortingMode.TYPE);
                        break;
                }
                return true;
            }
        });
        sortingMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                if (!isFinishedOnPause) {
                    isShowingPopupMenu = false;
                }
            }
        });
    }

    private void processSorting(SortingMode type) {
        Collections.sort(currentPictureList, PhotoComparator.getComparator(type));
        photoListAdapter.notifyDataSetChanged();
    }

    private void setUpPhotoGridView(View view) {
        photoGridView = view.findViewById(R.id.gridPhoto);
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(activity.getApplicationContext(), 500);
        photoGridView.setLayoutManager(layoutManager);
        photoListAdapter = new PhotoListAdapter(getContext(), currentPictureList);
        photoGridView.setAdapter(photoListAdapter);
    }

    @SuppressLint("WrongConstant")
    private void setUpCatalogue(View view) {
        recyclerView = view.findViewById(R.id.rccPhotoTypeButtons);

        catalogAdapter = new CatalogAdapter(activity, this, photoCatalogues);
        recyclerView.setAdapter(catalogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
    }

    private void updateCatalogue() {
        for (MyPicture pic:
                new ImageDAOImpl(activity).getAllMediaFiles()) {
            String picPath = pic.getpContent().getPicturePath();
            String picFolder = FileHelper.getFolder(picPath);
            if (!photoCatalogues.contains(picFolder)) {
                photoCatalogues.add(picFolder);
                catalogAdapter.notifyItemInserted(photoCatalogues.size() - 1);
            }
        }
        catalogAdapter.notifyDataSetChanged();
    }
    private void changePhotoCatalog(String catalog) {
        currentPictureList.clear();
        if (catalog.equals("All")) {
            currentCatalogue = catalog;
            currentPictureList.addAll(new ImageDAOImpl(getActivity()).getAllMediaFiles());
        } else {
            currentCatalogue = catalog;
            currentPictureList.addAll(FileFilter.filterPictureByFolder(new ImageDAOImpl(getActivity()).getAllMediaFiles(), catalog));
        }
        photoListAdapter.notifyDataSetChanged();
        txtPictureCount.setText("Number of pictures: "+ String.valueOf(currentPictureList.size()));
    }

    private void changePhotoCatalog(String catalog, String subName) {
        currentPictureList.clear();
        List<MyPicture> tmpList = new ArrayList<>();
        if (catalog.equals("All")) {
            currentCatalogue = catalog;
            tmpList.addAll(new ImageDAOImpl(getActivity()).getAllMediaFiles());

        } else {
            currentCatalogue = catalog;
            tmpList.addAll(FileFilter.filterPictureByFolder(new ImageDAOImpl(getActivity()).getAllMediaFiles(), catalog));
        }

        if (TextUtils.isEmpty(subName)) {
            currentPictureList.addAll(tmpList);
        } else {
            for (MyPicture picture:
                 tmpList) {
                if (picture.getpContent().getPicturName().contains(subName)) {
                    currentPictureList.add(picture);
                }
            }
        }

        photoListAdapter.notifyDataSetChanged();
        txtPictureCount.setText("Number of pictures: "+ String.valueOf(currentPictureList.size()));
    }

    @Override
    public void onMsgFromMainToFrag(String sender, String message) {
        if (sender.equals(CHANGE_PHOTO_CATALOG)) {
            updateCatalogue();
            changePhotoCatalog(message);
        } else if (message.equals(UPDATE_PICTURE_LIST_SIZE)) {
            updateCatalogue();
            changePhotoCatalog(currentCatalogue);
        }
    }
}