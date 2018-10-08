package com.app.eventify;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>
{

    private List<NewsInfo> news_list;
    //private SwipeRefreshLayout swipeRefreshLayout;
    public Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position, ImageView imageView);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
     {
        this.onItemClickListener = onItemClickListener;
    }

    public NewsRecyclerAdapter(List<NewsInfo> news_list)
    {

        this.news_list = news_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_type1, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }
    public void setFadeAnimation(View view)
    {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
    @Override
    public void onBindViewHolder(@NonNull NewsRecyclerAdapter.ViewHolder holder, int position)
    {
        //setFadeAnimation(holder.itemView);
        NewsInfo newsItem = news_list.get(position);
        ViewCompat.setTransitionName(holder.newsThumbView,newsItem.title);

            holder.headView.setBackground(null);
            holder.timestampView.setBackground(null);
            String title_data = newsItem.getTitle();
            holder.setHeading(title_data);

            String thumb_uri = newsItem.getThumbnail_url();
            holder.setThumbnail(thumb_uri);

            long timestamp = newsItem.getTimestamp();
            holder.setTimestamp(timestamp);
    }

    @Override
    public int getItemCount()
    {
        return news_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;
        private TextView headView;
        private RoundedImageView newsThumbView;
        private TextView timestampView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            newsThumbView = mView.findViewById(R.id.news_thumb);
            headView = mView.findViewById(R.id.news_heading);
            timestampView = mView.findViewById(R.id.news_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            onItemClickListener.onItemClick(position, newsThumbView);
                        }
                    }
                }
            });
        }

        public void setHeading(String heading)
        {
            headView.setText(heading);
        }
        public void setThumbnail(String downloadURI)
        {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.colorLight);
            Glide.with(context)
                    .load(downloadURI)
                    .apply(options)
                    .into(newsThumbView);
        }
        public void setTimestamp(long timestamp)
        {
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(new Date(Math.abs(timestamp)));
            timestampView.setText(ago);
        }
    }
}
