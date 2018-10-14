package com.app.eventify;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.eventify.fragments.OngoingEventsFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import static com.app.eventify.fragments.OngoingEventsFragment.DESC_EVENT;
import static com.app.eventify.fragments.OngoingEventsFragment.IMG_URL_EVENT;
import static com.app.eventify.fragments.OngoingEventsFragment.TITLE_EVENT;

public class EventDetailActivity extends AppCompatActivity
{
    private ImageView mainImageView;
    private TextView titleTextView, bodyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Bundle extras = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainImageView = findViewById(R.id.main_img_event);
        titleTextView = findViewById(R.id.text_title_event);
        bodyTextView = findViewById(R.id.text_body_event);


        mainImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw()
            {
                mainImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra(IMG_URL_EVENT);
        String title = intent.getStringExtra(TITLE_EVENT);
        String desc = intent.getStringExtra(DESC_EVENT);
        String imageTransitionName = extras.getString(OngoingEventsFragment.IMAGE_TRANSITION_NAME_EVENT);
        mainImageView.setTransitionName(imageTransitionName);


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .dontTransform()
                .dontAnimate();

        supportPostponeEnterTransition();
        Glide.with(this)
                .load(imgUrl)
                .apply(options)
                .listener(new RequestListener<Drawable>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(mainImageView);
        titleTextView.setText(title);
        bodyTextView.setText(desc);
    }
}
