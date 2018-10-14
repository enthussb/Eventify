package com.app.eventify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.eventify.fragments.EventsFragment;
import com.app.eventify.fragments.NewsFragment;
import com.app.eventify.fragments.ProfileFragment;
import com.app.eventify.utils.DatabaseUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID, uname, email, img_url;
    private Fragment currentFragment;
    private NewsFragment newsFragment = new NewsFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private EventsFragment eventsFragment = new EventsFragment();
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Context context;
    private int clickedNavItem = 0;
    private String startLoad = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.navigation_news:
                    if(currentFragment != newsFragment)
                    {
                        replaceFragment(newsFragment,"NEWS_FRAGMENT");
                        navigationView.getMenu().getItem(0).setChecked(true);
                    }
                    else
                    {
                        newsFragment.scrollTotop();
                        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
                        appBarLayout.setExpanded(true,true);
                    }
                    return true;
                case R.id.navigation_events:
                    if(currentFragment != eventsFragment)
                    {
                        navigationView.getMenu().getItem(0).setChecked(false);
                        replaceFragment(eventsFragment,"NEWS_FRAGMENT");
                    }
                    return true;
            }
            return false;
        }
    };

    public TabLayout getTabLayout()
    {
        return (TabLayout)findViewById(R.id.tabs);
    }

    public interface OnDataReceiveCallback {
        void onDataReceived(String username, String email, String img_url, Context context);
    }
    private void retrieveFirebase(final OnDataReceiveCallback callback)
    {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = DatabaseUtil.getDatabase();
        myRef = mFirebaseDatabase.getReference("Users");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uname = dataSnapshot.child(userID).child("userName").getValue(String.class);
                email = dataSnapshot.child(userID).child("emailId").getValue(String.class);
                img_url = dataSnapshot.child(userID).child("profilePic").getValue(String.class);
                callback.onDataReceived(uname,email,img_url,context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void replaceFragment(Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        currentFragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startLoad = getIntent().getStringExtra("login");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        context = this;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        View headerView = navigationView.getHeaderView(0);
        final TextView navEmail = headerView.findViewById(R.id.navheader_email);
        final TextView navUsername = headerView.findViewById(R.id.navheader_username);
        final ImageView navImg = headerView.findViewById(R.id.navheader_img);

        navigationView.setCheckedItem(R.id.nav_home);

        if(startLoad !=null && startLoad.equals("Yload"))
        {
            Bundle bundle = new Bundle();
            bundle.putString("params",startLoad);
            newsFragment.setArguments(bundle);
        }
        replaceFragment(newsFragment,"NEWS_FRAGMENT");

        retrieveFirebase(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(String username, String email, String img_url, Context context) {
                navUsername.setText(uname);
                navEmail.setText(email);
                Bitmap bm = ((BitmapDrawable) navImg.getDrawable()).getBitmap();
                Drawable d = new BitmapDrawable(getResources(), bm);
                RequestOptions options = new RequestOptions()
                        .placeholder(d);
                Glide.with(context)
                        .load(img_url)
                        .apply(options)
                        .into(navImg);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        final Intent intent = new Intent(this, StartActivity.class);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView)
            {
                switch (clickedNavItem)
                {
                    case R.id.nav_home:
                        if (currentFragment != newsFragment)
                            replaceFragment(newsFragment,"NEWS_FRAGMENT");
                        else
                        {
                            newsFragment.scrollTotop();
                            AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
                            appBarLayout.setExpanded(true, true);
                        }
                        bottomNavigationView.setSelectedItemId(R.id.navigation_news);
                        break;

                    case R.id.nav_profile:
                        if (currentFragment != profileFragment)
                            replaceFragment(profileFragment,"PROFILE_FRAGMENT");
                        break;
                    case R.id.nav_sign_out:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        String shareStr = "App Share Testing";
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Coming Soon");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStr);
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        break;

                    case R.id.nav_send:
                        break;
                }
                clickedNavItem = 0;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
         {
             if(currentFragment == newsFragment)
             {
                 super.onBackPressed();
             }
             else
             {
                 replaceFragment(newsFragment,"NEWS_FRAGMENT");
                 navigationView.getMenu().getItem(0).setChecked(true);
                 bottomNavigationView.setSelectedItemId(R.id.navigation_news);
             }
         }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up round_edge_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle bottom_navigation_menu view item clicks here.
        switch(item.getItemId())
        {
            case R.id.nav_home:
                navigationView.getMenu().getItem(0).setChecked(true);
                clickedNavItem = R.id.nav_home;
                break;

            case R.id.nav_profile:
                clickedNavItem = R.id.nav_profile;
                break;

            case  R.id.nav_sign_out:
                clickedNavItem = R.id.nav_sign_out;
                break;

            case  R.id.nav_share:
                clickedNavItem = R.id.nav_share;
                break;

            case  R.id.nav_send:
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
