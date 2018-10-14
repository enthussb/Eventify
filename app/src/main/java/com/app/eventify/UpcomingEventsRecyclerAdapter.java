package com.app.eventify;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingEventsRecyclerAdapter extends RecyclerView.Adapter<UpcomingEventsRecyclerAdapter.ViewHolder>
{
    private List<EventInfo> events_List;
    public Context context;
    private UpcomingEventsRecyclerAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position, ImageView imageView);
    }

    public void setOnItemClickListener(UpcomingEventsRecyclerAdapter.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public UpcomingEventsRecyclerAdapter(List<EventInfo> events_List)
    {
        this.events_List = events_List;
    }
    @NonNull
    @Override
    public UpcomingEventsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item,parent,false);
        return new UpcomingEventsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingEventsRecyclerAdapter.ViewHolder holder, int position)
    {
        EventInfo eventItem = events_List.get(position);
        ViewCompat.setTransitionName(holder.eventImageView,eventItem.getTitle());
        holder.setTitle(eventItem.getTitle());
        holder.setEventImage(eventItem.getImg_url());
        holder.setDate(eventItem.getStartDate());
    }

    @Override
    public int getItemCount() {
        return events_List.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView eventImageView;
        private TextView dateTextView;
        private TextView headTextView;
        public ViewHolder(View itemView)
        {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImgView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            headTextView = itemView.findViewById(R.id.eventHeadTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(onItemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            onItemClickListener.onItemClick(position,eventImageView);
                        }
                    }
                }
            });

        }
        public void setTitle(String title)
        {
            headTextView.setText(title);
        }
        public void setEventImage(String downloadURI)
        {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.colorLight);
            Glide.with(context)
                    .load(downloadURI)
                    .apply(options)
                    .into(eventImageView);
        }
        public void setDate(String date)
        {
            String day,month;
            Date d = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
            SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM",Locale.US);
            //SimpleDateFormat weekDayFormatter = new SimpleDateFormat("EE",Locale.US);
            day= date.substring(0,2).toUpperCase();
            try {
                d = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            month = monthFormatter.format(d).toUpperCase();
            //weekday = weekDayFormatter.format(d).toUpperCase();

            SpannableString spannableString = new SpannableString(month+"\n"+day+"\nOnwards");
            spannableString.setSpan(new ForegroundColorSpan(Color.rgb(3,155,230)),0,4,0);
            spannableString.setSpan(new RelativeSizeSpan(1.7f), 4, 7, 0);
            dateTextView.setText(spannableString);
        }
    }
}
