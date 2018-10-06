package com.app.eventify;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.eventify.Utils.DatabaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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

    public NewsFragment()
    {
        // Required empty public constructor
    }

    public void scrollTotop()
    {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(news_recyclerView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(newsRecyclerAdapter.getItemCount());
        mLayoutManager.startSmoothScroll(smoothScroller);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        news_recyclerView = view.findViewById(R.id.news_recyclerView);
        news_list = new ArrayList<>();
        newsRecyclerAdapter = new NewsRecyclerAdapter(news_list);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        news_recyclerView.addItemDecoration((new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL)));
        news_recyclerView.setLayoutManager(mLayoutManager);
        news_recyclerView.setAdapter(newsRecyclerAdapter);

        newsRecyclerAdapter.setOnItemClickListener(new NewsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ImageView sharedImageView)
            {
                Intent dataIntent = new Intent(getActivity(),NewsDetailActivity.class);
                NewsInfo clickedItem = news_list.get(position);
                dataIntent.putExtra(IMG_URL,clickedItem.getImage_url());
                dataIntent.putExtra(TITLE,clickedItem.getTitle());
                dataIntent.putExtra(DESC,clickedItem.getDescription());
                dataIntent.putExtra(IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), sharedImageView, ViewCompat.getTransitionName(sharedImageView));

                startActivity(dataIntent, options.toBundle());
            }
        });



        firebaseDatabase = DatabaseUtil.getDatabase();

        firebaseDatabase.getReference().child("News").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                NewsInfo newsInfo = dataSnapshot.getValue(NewsInfo.class);
                news_list.add(newsInfo);
                newsRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        return view;

    }
}
