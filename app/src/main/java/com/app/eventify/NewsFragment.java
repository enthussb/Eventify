package com.app.eventify;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.eventify.Utils.DatabaseUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class NewsFragment extends Fragment{


    public static final String IMG_URL = "image_url";
    public static final String DESC = "description";
    public static final String TITLE = "title";
    public static final String IMAGE_TRANSITION_NAME = "title";

    private RecyclerView news_recyclerView;
    private List<NewsInfo> news_list;
    private FirebaseDatabase firebaseDatabase;
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int TOTAL_ITEM_EACH_LOAD = 6;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private Long lastKey = null;
    private Long FirstKey = null;
    private long total = 0;
    private long newTotal = 0;
    private int ctr = 1;
    private boolean flag = true;
    private NewsInfo lastItem;

    public NewsFragment()
    {
        // Required empty public constructor
    }
    private void resetValues()
    {
        newTotal = 0;
        lastKey = null;
        total = 0;
        ctr = 1;
        flag = true;
        isScrolling = false;
        lastItem = null;
        currentItems = 0;
    }
    public void scrollTotop()
    {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(news_recyclerView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(0);
        mLayoutManager.startSmoothScroll(smoothScroller);
    }
    private void refreshData()
    {
        final Query query;
        query = firebaseDatabase.getReference().child("News");
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                newTotal = dataSnapshot.getChildrenCount();
                Log.d(TAG, "onDataChange: NewTotal"+newTotal);
                //
                if(total == newTotal)
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                else
                {
                    resetValues();
                    news_list.clear();
                    newsRecyclerAdapter.notifyDataSetChanged();
                    loadData();
                }
                query.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    private void loadData()
    {
        firebaseDatabase = DatabaseUtil.getDatabase();
        final Query query,start;
        if(lastKey == null) {
            query = firebaseDatabase.getReference().child("News")
                    .orderByChild("timestamp")
                    .limitToFirst(TOTAL_ITEM_EACH_LOAD);
        }
        else {
            query = firebaseDatabase.getReference().child("News")
                    .orderByChild("timestamp")
                    .startAt(lastKey)
                    .limitToFirst(TOTAL_ITEM_EACH_LOAD);
        }
        query.keepSynced(true);
        start = firebaseDatabase.getReference().child("News");
        start.keepSynced(true);
        start.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                total = dataSnapshot.getChildrenCount();
                Log.d(TAG, "onDataChangeTotal: " + total);
                query.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(!dataSnapshot.hasChildren())
                        {
                            Toast.makeText(getContext(), "No more data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            NewsInfo newsInfo = data.getValue(NewsInfo.class);
                            Log.d(TAG, "onDataChange: "+newsInfo.getTitle());
                            if(ctr == 1)
                                FirstKey = newsInfo.getTimestamp();
                                news_list.add(newsInfo);
                            lastKey = newsInfo.getTimestamp();
                            ctr++;
                        }
                        news_list.remove(news_list.size()-1);
                        Log.d(TAG, "onDataChange: ctr"+ctr);
                        newsRecyclerAdapter.notifyDataSetChanged();
                        ctr = ctr - 1;
                        mProgressBar.setVisibility(View.GONE);
                        query.removeEventListener(this);
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                start.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadRemainingData(int n)
    {
        Log.d(TAG, "loadRemainingData: Called"+n);
        firebaseDatabase = DatabaseUtil.getDatabase();
        final Query query;
        query = firebaseDatabase.getReference().child("News")
                .orderByChild("timestamp")
                .startAt(lastKey)
                .limitToFirst(n+1);
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    ++ctr;
                    NewsInfo newsInfo = data.getValue(NewsInfo.class);
                    news_list.add(newsInfo);
                    newsRecyclerAdapter.notifyDataSetChanged();
                }
                mProgressBar.setVisibility(View.GONE);
                query.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void loadMoreData()
    {
        if(ctr == total)
        {
            return;
        }
        else if(ctr + TOTAL_ITEM_EACH_LOAD  > total)
        {
            loadRemainingData((int)total - ctr);
            flag = false;
        }
        else
        {
            loadData();
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        resetValues();
        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        mProgressBar = view.findViewById(R.id.progressBar_news);
        swipeRefreshLayout = view.findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        news_recyclerView = view.findViewById(R.id.news_recyclerView);
        news_list = new ArrayList<>();
        newsRecyclerAdapter = new NewsRecyclerAdapter(news_list);

        news_recyclerView.addItemDecoration((new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL)));
        news_recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        news_recyclerView.setLayoutManager(mLayoutManager);

        news_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = mLayoutManager.findFirstVisibleItemPosition();
                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    if(flag)
                        loadMoreData();
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        newsRecyclerAdapter.setOnItemClickListener(new NewsRecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position, ImageView sharedImageView, int state)
            {
                NewsInfo clickedItem = news_list.get(position);
                if(state == 0)
                {
                    Intent dataIntent = new Intent(getActivity(), NewsDetailActivity.class);
                    dataIntent.putExtra(IMG_URL, clickedItem.getImage_url());
                    dataIntent.putExtra(TITLE, clickedItem.getTitle());
                    dataIntent.putExtra(DESC, clickedItem.getDescription());
                    dataIntent.putExtra(IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), sharedImageView, ViewCompat.getTransitionName(sharedImageView));

                    startActivity(dataIntent, options.toBundle());
                }
                else
                {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_enlarge_layout, null);
                    PhotoView photoView = mView.findViewById(R.id.photo_view);

                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .dontAnimate()
                            .dontTransform();

                    Glide.with(getContext())
                            .load(clickedItem.getImage_url())
                            .apply(options)
                            .into(photoView);

                    mBuilder.setView(photoView);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            }
        });
        news_recyclerView.setAdapter(newsRecyclerAdapter);
        loadData();
//        firebaseDatabase = DatabaseUtil.getDatabase();
//        Query query = firebaseDatabase.getReference().child("News/").orderByChild("timestamp");
//        query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//            {
//                NewsInfo newsInfo = dataSnapshot.getValue(NewsInfo.class);
//                news_list.add(newsInfo);
//                newsRecyclerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//            {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
//            {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//            {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//
//            }
//        });

        return view;

    }
}
