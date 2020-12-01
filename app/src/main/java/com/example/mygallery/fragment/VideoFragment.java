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

import com.example.mygallery.R;
import com.example.mygallery.activity.MainActivity;
import com.example.mygallery.adapter.AutoFitGridLayoutManager;
import com.example.mygallery.adapter.CatalogAdapter;
import com.example.mygallery.adapter.VideoListAdapter;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.DAO.VideoDAOImpl;
import com.example.mygallery.data.filter.FileFilter;
import com.example.mygallery.data.model.MyVideo;
import com.example.mygallery.data.sorthelper.SortingMode;
import com.example.mygallery.data.sorthelper.VideoComparator;
import com.example.mygallery.response.FragCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoFragment extends Fragment implements FragCallBack {

    private static final String UPDATE_VIDEO_LIST_SIZE = "UPDATE_VIDEO_LIST_SIZE";
    private static final String CHANGE_VIDEO_CATALOG = "CHANGE_VIDEO_CATALOG";
    private static final String SAVED_SEARCH_STRING = "SAVED_SEARCH_STRING";
    private static final String SAVED_POPUP_MENU = "SAVED_POPUP_MENU";

    private RecyclerView recyclerView;
    private List<String> videoCatalogues;
    private CatalogAdapter catalogAdapter;

    private RecyclerView videoGridView;
    private VideoListAdapter videoListAdapter;
    private List<MyVideo> currentVideoList;

    private SearchView searchView;
    private VideoDAOImpl videoDAO;

    private MainActivity activity;
    private PopupMenu sortingMenu;
    private String searchQuery;
    private boolean isShowingPopupMenu;
    private boolean isFinishedOnPause;

    private TextView txtVideoCount2;
    private String currentCatalogue;

    public VideoFragment() {
    }

    public VideoFragment(MainActivity newActivity) {
        activity = newActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoDAO = new VideoDAOImpl(activity);
        isFinishedOnPause = false;
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFinishedOnPause = true;
        sortingMenu.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SEARCH_STRING, searchQuery);
        outState.putBoolean(SAVED_POPUP_MENU, isShowingPopupMenu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem( R.id.btnSearch ).getActionView();
        final Menu copMenu = menu;
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copMenu.findItem(R.id.btnSorting).setVisible(false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (copMenu != null) {

                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(searchQuery)) {
            searchView.setIconified(false);
            menu.findItem(R.id.btnSearch).expandActionView();
            searchView.setQuery(searchQuery, false);
            changeVideoCatalog(currentCatalogue, searchQuery);
            menu.findItem(R.id.btnSorting).setVisible(true);
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
                changeVideoCatalog(currentCatalogue, newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.btnSorting:
                isShowingPopupMenu = true;
                sortingMenu.show();
                break;
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        setUp(view);
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SAVED_SEARCH_STRING);
            isShowingPopupMenu = savedInstanceState.getBoolean(SAVED_POPUP_MENU);
        }
        return view;
    }

    @SuppressLint("WrongConstant")
    private void setUp(View view) {
        if (activity == null) {
            activity= (MainActivity) getActivity();
        }
        setUpVideoGridView(view);
        setUpCatologList(view);

        View anchor = view.findViewById(R.id.videoAnchor);
        createSortingMenu(anchor);

        txtVideoCount2 = view.findViewById(R.id.txtVideoCount2);
        txtVideoCount2.setText("Number of videos: " + String.valueOf(currentVideoList.size()));

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
        Collections.sort(currentVideoList, VideoComparator.getComparator(type));
        videoListAdapter.notifyDataSetChanged();
    }

    private void setUpVideoGridView(View view) {
        videoGridView = view.findViewById(R.id.gridVideo);
        currentVideoList = videoDAO.getAllMediaFiles();
        currentCatalogue = "All";
        videoGridView.setLayoutManager(new AutoFitGridLayoutManager(activity.getApplicationContext(), 500));
        videoListAdapter = new VideoListAdapter(getContext(), currentVideoList);
        videoGridView.setAdapter(videoListAdapter);
    }

    @SuppressLint("WrongConstant")
    private void setUpCatologList(View view) {
        recyclerView = view.findViewById(R.id.rccVideoType);

        videoCatalogues = new ArrayList<>();
        videoCatalogues.add("All");
        for (MyVideo vid:
                currentVideoList) {
            String vidPath = vid.getvContent().getPath();
            String vidFolder = FileHelper.getFolder(vidPath);
            if (!videoCatalogues.contains(vidFolder)) {
                videoCatalogues.add(vidFolder);

            }
        }
        catalogAdapter = new CatalogAdapter(activity, this, videoCatalogues);
        recyclerView.setAdapter(catalogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

    }

    private void setChangeVideoCatalog(String catalog) {
        currentVideoList.clear();
        currentCatalogue = catalog;

        if (catalog.equals("All")) {
            currentVideoList.addAll(new VideoDAOImpl(getActivity()).getAllMediaFiles());
        } else {
            currentVideoList.addAll(FileFilter.filterVideoByFolder(new VideoDAOImpl(getActivity()).getAllMediaFiles(), catalog));
        }
        videoListAdapter.notifyDataSetChanged();
        txtVideoCount2.setText("Number of videos: " + String.valueOf(currentVideoList.size()));
    }

    private void changeVideoCatalog(String catalog, String subName) {
        currentVideoList.clear();
        List<MyVideo> tmpList = new ArrayList<>();
        if (catalog.equals("All")) {
            currentCatalogue = catalog;
            tmpList.addAll(new VideoDAOImpl(getActivity()).getAllMediaFiles());

        } else {
            currentCatalogue = catalog;
            tmpList.addAll(FileFilter.filterVideoByFolder(new VideoDAOImpl(getActivity()).getAllMediaFiles(), catalog));
        }

        if (TextUtils.isEmpty(subName)) {
            currentVideoList.addAll(tmpList);
        } else {
            for (MyVideo video:
                    tmpList) {
                if (video.getvContent().getVideoName().contains(subName)) {
                    currentVideoList.add(video);
                }
            }
        }

        videoListAdapter.notifyDataSetChanged();
        txtVideoCount2.setText("Number of pictures: "+ String.valueOf(currentVideoList.size()));
    }

    @Override
    public void onMsgFromMainToFrag(String sender, String message) {
        if (sender.equals(CHANGE_VIDEO_CATALOG)) {
            setChangeVideoCatalog(message);
        } else if (message.equals(UPDATE_VIDEO_LIST_SIZE)) {
            setChangeVideoCatalog(currentCatalogue);
        }
    }
}