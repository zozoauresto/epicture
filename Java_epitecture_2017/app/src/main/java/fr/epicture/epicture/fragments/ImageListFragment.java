package fr.epicture.epicture.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.adapters.ImageListRecyclerAdapter;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.interfaces.ImageListAdapterInterface;
import fr.epicture.epicture.interfaces.ImageListInterface;
import fr.epicture.epicture.interfaces.RetractableToolbarInterface;
import fr.epicture.epicture.utils.HidingScrollListener;
import fr.epicture.epicture.utils.StaticTools;

public class ImageListFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private TextView noItem;

    private boolean init;
    private int page;
    private boolean retractableToolbar;

    private ImageListRecyclerAdapter adapter;

    private String textToSearch;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init = false;
        page = 1;
        retractableToolbar = getActivity() instanceof RetractableToolbarInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_list_fragment, container, false);

        swipe = (SwipeRefreshLayout)view.findViewById(R.id.imagelist_swipe);
        recyclerView = (RecyclerView)view.findViewById(R.id.imagelist_recyclerview);
        noItem = (TextView)view.findViewById(R.id.noitem);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int toolbarSize = TypedValue.complexToDimensionPixelSize(tv.data, getActivity().getResources().getDisplayMetrics());
            int top = (int) StaticTools.convertDpToPixel(12.f, getContext());
            swipe.setProgressViewOffset(false, 0, toolbarSize + top);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int cLast = manager.findLastCompletelyVisibleItemPosition();

                if (cLast == adapter.getItemCount() - 1 && page > 1) {
                    ((ImageListInterface)getActivity()).onRequestImageList(page, textToSearch, userID);
                }
            }
        });

        adapter = new ImageListRecyclerAdapter(getActivity(), retractableToolbar, new ImageListAdapterInterface() {
            @Override
            public void onImageClick(APIImageElement element) {
                ((ImageListInterface)getActivity()).onImageClick(element);
            }

            @Override
            public void onCommentClick(APIImageElement element) {
                ((ImageListInterface)getActivity()).onCommentClick(element);
            }

            @Override
            public void onImageDelete(APIImageElement element) {
                refresh();
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                if (retractableToolbar) {
                    ((RetractableToolbarInterface) getActivity()).onHideToolbar();
                }
            }

            @Override
            public void onShow() {
                if (retractableToolbar) {
                    ((RetractableToolbarInterface) getActivity()).onShowToolbar();
                }
            }
        });

        return view;
    }

    public void setSearch(String userid, String text) {
        this.userID = userid;
        this.textToSearch = text;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!init) {
            refresh();
            init = true;
        }
    }

    public void refresh() {
        page = 1;
        adapter.clear();
        ((ImageListInterface)getActivity()).onRequestImageList(page, textToSearch, userID);
    }

    public void refreshList(@Nullable List<APIImageElement> imageElementList) {
        if (imageElementList != null) {
            adapter.addList(imageElementList);
            ++page;
        }
        refreshSwipe(imageElementList == null);
        if (adapter.getItemCount() == 0) {
            noItem.setVisibility(View.VISIBLE);
        } else {
            noItem.setVisibility(View.GONE);
        }
    }

    private void refreshSwipe(final boolean loading) {
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(loading);
            }
        });
    }

}
