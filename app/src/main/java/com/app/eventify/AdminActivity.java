package com.app.eventify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.app.eventify.fragments.AddEventFragment;
import com.app.eventify.fragments.AddNewsFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity
{

    private LinearLayout fab_item1,fab_item2;
    private FloatingActionButton fab, fab_news,fab_events;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private Fragment currentFragment = null;
    private AddNewsFragment addNewsFragment = new AddNewsFragment();
    private AddEventFragment addEventFragment = new AddEventFragment();

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.menu_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Admin Panel");

        fab_item1 = findViewById(R.id.fab_item1);
        fab_item2 = findViewById(R.id.fab_item2);

        fab = findViewById(R.id.fab);
        fab_news = findViewById(R.id.fab_news);
        fab_events = findViewById(R.id.fab_events);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                fabAnimation();
            }
        });

        fab_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fabAnimation();
                fab.hide();
                fab.setClickable(false);
                setNonClickable();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        replaceFragment(addNewsFragment,"ADD_NEWS_FRAGMENT");
                    }
                }, 300);
            }
        });

        fab_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fabAnimation();
                fab.hide();
                fab.setClickable(false);
                setNonClickable();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        replaceFragment(addEventFragment,"ADD_EVENT_FRAGMENT");
                    }
                }, 300);
            }
        });
    }
    private void replaceFragment(Fragment fragment, String tag)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, fragment, tag)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        currentFragment = fragment;
    }
    private void fabAnimation()
    {
        if(fab_item1.getVisibility() == View.VISIBLE && fab_item2.getVisibility() == View.VISIBLE)
        {
            fab_item1.setVisibility(View.GONE);
            fab_item2.setVisibility(View.GONE);
            fab.startAnimation(rotateBackward);
            fab_item1.startAnimation(fabClose);
            fab_item2.startAnimation(fabClose);
            setNonClickable();
        }
        else
        {
            fab_item1.setVisibility(View.VISIBLE);
            fab_item2.setVisibility(View.VISIBLE);
            fab.startAnimation(rotateForward);
            fab_item1.startAnimation(fabOpen);
            fab_item2.startAnimation(fabOpen);
            setClickable();
        }
    }
    private void removeFragment()
    {
        if(currentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            currentFragment = null;
            fab.show();
            fab.setClickable(true);
        }
    }
    private void setClickable()
    {
        fab_events.setClickable(true);
        fab_news.setClickable(true);
    }
    private void setNonClickable()
    {
        fab_events.setClickable(false);
        fab_news.setClickable(false);
    }
    @Override
    public void onBackPressed()
    {
        if(currentFragment == null)
            super.onBackPressed();
        else
            removeFragment();
    }
}

