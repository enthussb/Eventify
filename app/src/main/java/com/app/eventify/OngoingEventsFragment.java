package com.app.eventify;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.eventify.Utils.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class OngoingEventsFragment extends Fragment
{

    public static final String IMG_URL_EVENT = "img_url";
    public static final String DESC_EVENT = "description";
    public static final String TITLE_EVENT = "title";
    public static final String IMAGE_TRANSITION_NAME_EVENT = "title";

    private FirebaseDatabase firebaseDatabase;
    private List<EventInfo> eventInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OngoingEventsRecyclerAdapter ongoingEventsRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    private SimpleDateFormat formatter;
    private Date currentDate;
    public OngoingEventsFragment()
    {
        // Required empty public constructor
    }

    private Date getCurrentDate()
    {
        Date date=null;
        String str = formatter.format(new Date());
        try
        {
            date= formatter.parse(str);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    private Date parsetoDate(String str)
    {
        Date date = null;
        try
        {
            date = formatter.parse(str);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    private void loadEvents()
    {
        firebaseDatabase = DatabaseUtil.getDatabase();
        final Query query;
        query = firebaseDatabase.getReference().child("Events").orderByChild("timestamp");
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChildren())
                {
                    Toast.makeText(getContext(), "No data to display", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    EventInfo eventInfo = data.getValue(EventInfo.class);
                    Log.d(TAG, "onDataChange: "+eventInfo.getTitle());
                    String startDatestr = eventInfo.getStartDate();
                    String endDatestr = eventInfo.getEndDate();
                    Date startDate = parsetoDate(startDatestr);
                    Date endDate = parsetoDate(endDatestr);
                    if(startDate.equals(currentDate) || (startDate.before(currentDate) && (currentDate.equals(endDate) || currentDate.before(endDate))))
                        eventInfoList.add(eventInfo);
                    Log.d(TAG, "List Size: "+eventInfoList.isEmpty());
                }
                ongoingEventsRecyclerAdapter.notifyDataSetChanged();
                if(eventInfoList.isEmpty())
                    Toast.makeText(getContext(), "No Ongoing Events", Toast.LENGTH_SHORT).show();
                query.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ongoing_events, container, false);
        recyclerView = view.findViewById(R.id.ongoingEvent_recyclerView);
        formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        currentDate = getCurrentDate();
        ongoingEventsRecyclerAdapter = new OngoingEventsRecyclerAdapter(eventInfoList);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ongoingEventsRecyclerAdapter.setOnItemClickListener(new OngoingEventsRecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position, ImageView sharedImageView)
            {
                EventInfo clickedItem = eventInfoList.get(position);
                Intent dataIntent = new Intent(getActivity(), EventDetailActivity.class);
                dataIntent.putExtra(IMG_URL_EVENT, clickedItem.getImg_url());
                dataIntent.putExtra(TITLE_EVENT, clickedItem.getTitle());
                dataIntent.putExtra(DESC_EVENT, clickedItem.getDescription());
                dataIntent.putExtra(IMAGE_TRANSITION_NAME_EVENT, ViewCompat.getTransitionName(sharedImageView));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), sharedImageView, ViewCompat.getTransitionName(sharedImageView));
                startActivity(dataIntent, options.toBundle());
            }
        });

        recyclerView.setAdapter(ongoingEventsRecyclerAdapter);
        loadEvents();
        return view;
    }
}
