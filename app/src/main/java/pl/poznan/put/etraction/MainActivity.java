package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: Refactor duplicated code in fragments

    private int mCurrentDrawerPosition;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null)
            navigateToHomeFragment();

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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
                default:
                    fragmentClass = new StatementsFragment();
                    break;
            }

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentClass).commit();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToHomeFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new StatementsFragment()).commit();
        mCurrentDrawerPosition = R.id.nav_statements;
        mNavigationView.getMenu().getItem(1).setChecked(true);
    }
}
