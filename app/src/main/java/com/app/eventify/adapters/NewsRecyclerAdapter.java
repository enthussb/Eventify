package com.app.eventify.adapters;

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

import com.app.eventify.R;
import com.app.eventify.modal.NewsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private List<NewsInfo> news_list;
    private LayoutInflater layoutInflater;
    public Context context;
    private final int textImageDesc = 0;
    private final int textImage = 1;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position, ImageView imageView, int state);
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(context);

        switch (viewType)
        {
            case textImageDesc:
                View textImageDescVH = layoutInflater.inflate(R.layout.news_item_type1,parent,false);
                viewHolder = new TextImageDesc(textImageDescVH);
                break;
            case textImage:
                View textImageVH = layoutInflater.inflate(R.layout.news_item_type2,parent,false);
                viewHolder = new TextImage(textImageVH);
                break;
        }
        return viewHolder;
    }
    public void setFadeAnimation(View view)
    {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        //setFadeAnimation(holder.itemView);
        NewsInfo newsItem = news_list.get(position);
        switch (holder.getItemViewType())
        {
            case textImageDesc:
                TextImageDesc tid = (TextImageDesc)holder;
                ViewCompat.setTransitionName(tid.newsThumbView,newsItem.getTitle());

                String title_data = newsItem.getTitle();
                tid.setHeading(title_data);

                String thumb_uri = newsItem.getThumbnail_url();
                tid.setThumbnail(thumb_uri);

                long timestamp = newsItem.getTimestamp();
                tid.setTimestamp(timestamp);
                break;

            case textImage:
                TextImage ti = (TextImage)holder;

                String heading = newsItem.getTitle();
                ti.setBigImgHeading(heading);

                String img_uri = newsItem.getImage_url();
                ti.setBigImg(img_uri);

                long bigImgtimestamp = newsItem.getTimestamp();
                ti.setBigImgtimestamp(bigImgtimestamp);
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return news_list.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(news_list.get(position).getDescription().equals("null"))
            return textImage;
        else
            return textImageDesc;
    }

    public class TextImageDesc extends RecyclerView.ViewHolder
    {
        private View mView ;
        private TextView headView;
        private RoundedImageView newsThumbView;
        private TextView timestampView;

        public TextImageDesc(View itemView) {
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
                            onItemClickListener.onItemClick(position, newsThumbView,0);
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
    public class TextImage extends RecyclerView.ViewHolder
    {
        private ImageView bigImgView;
        private TextView bigImgHeadView;
        private TextView bigImgtimestampView;
        public TextImage(View itemView)
        {
            super(itemView);
            bigImgView = itemView.findViewById(R.id.news_bigImg);
            bigImgHeadView = itemView.findViewById(R.id.news_heading_bigImg);
            bigImgtimestampView = itemView.findViewById(R.id.news_time_bigImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            onItemClickListener.onItemClick(position, bigImgView,1);
                        }
                    }
                }
            });
        }

        public void setBigImgHeading(String heading)
        {
            bigImgHeadView.setText(heading);
        }
        public void setBigImg(String downloadURI)
        {

            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.colorLight);
            Glide.with(context)
                    .load(downloadURI)
                    .apply(options)
                    .into(bigImgView);
        }
        public void setBigImgtimestamp(long timestamp)
        {
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(new Date(Math.abs(timestamp)));
            bigImgtimestampView.setText(ago);
        }
    }
}
