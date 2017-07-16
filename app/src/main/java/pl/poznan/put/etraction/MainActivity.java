package pl.poznan.put.etraction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import pl.poznan.put.etraction.service.EtractionService;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int mCurrentDrawerPosition;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    EtractionService mService;
    boolean mBound = false;

    public static String deviceId;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EtractionService.LocalBinder binder = (EtractionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "SERVICE DISCONNECTED");
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        //Service
        Intent intent = new Intent(this, EtractionService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigateToHomeFragment();
        }

        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(getString(R.string.notification_bundle_frag_key))){
            int navId = getIntent().getExtras().getInt(getString(R.string.notification_bundle_frag_key));
            mNavigationView.getMenu().performIdentifierAction(navId,0);
            mNavigationView.setCheckedItem(navId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * If user is on chat or statements fragments and somehow listeners was not set, then the notifications will pop-up on notification bar.
     * When user click it, the already opened activity will be reloaded.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //If we are not in home fragment then navigate to it
            if (mCurrentDrawerPosition != R.id.nav_statements)
                navigateToHomeFragment();
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if (mCurrentDrawerPosition != item.getItemId()) {
            mCurrentDrawerPosition = item.getItemId();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragmentClass;
            switch (mCurrentDrawerPosition) {
                case R.id.nav_localization:
                    fragmentClass = new LocalizationFragment();
                    break;
                case R.id.nav_statements:
                    fragmentClass = new StatementsFragment();
                    break;
                case R.id.nav_movies:
                    fragmentClass = new MoviesFragment();
                    break;
                case R.id.nav_cameras:
                    fragmentClass = new CamerasFragment();
                    break;
                case R.id.nav_restaurant_menu:
                    fragmentClass = new RestaurantMenuFragment();
                    break;
                case R.id.nav_chat:
                    fragmentClass = new ChatFragment();
                    break;
                case R.id.nav_user_movies:
                    fragmentClass = new UserVideosTabsFragment();
                    break;
                default:
                    fragmentClass = new StatementsFragment();
                    break;
            }

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentClass).commit();

            if (mService != null) {
                if (mCurrentDrawerPosition == R.id.nav_chat) {
                    mService.setChatListener((ChatFragment) fragmentClass);
                    mService.removeStatementsListener();
                } else if (mCurrentDrawerPosition == R.id.nav_statements) {
                    mService.setStatementsListener((StatementsFragment) fragmentClass);
                    mService.removeChatListener();
                } else {
                    mService.removeChatListener();
                    mService.removeStatementsListener();
                }
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToHomeFragment() {
        mNavigationView.getMenu().performIdentifierAction(R.id.nav_statements,0);
        mNavigationView.setCheckedItem(R.id.nav_statements);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            //Intent intent = new Intent(this.getContext(), EtractionService.class);
            //getActivity().stopService(intent);
        }
    }
}
