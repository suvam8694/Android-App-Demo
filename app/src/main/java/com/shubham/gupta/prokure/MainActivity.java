package com.shubham.gupta.prokure;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private ListView slideMenuListView;
    private String[] menuItemValues;
    private ActionBarDrawerToggle drawerToggle;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        slideMenuListView = (ListView) findViewById(R.id.slide_menu);

        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();

        // Set up the slide menu
        configureSlideMenu();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // If this click has been handled by drawerToggle then return
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.action_cart:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        // Highlight the selected item, update the title, and close the drawer
        switch (position)
        {
            case 0: // Home
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();
                break;
            case 1: // Search
                Intent intent = new Intent(this, SearchActivity.class);
                this.startActivity(intent);
                break;

        }
        slideMenuListView.setItemChecked(position, true);
        setTitle(menuItemValues[position]);
        drawerLayout.closeDrawer(slideMenuListView);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        try
        {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e)
        {
            android.util.Log.e("Prokure", "Can not set Title");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void configureSlideMenu()
    {
        // Load list item names
        menuItemValues = getResources().getStringArray(R.array.subSectionsArray);

        // load image resource ids
        TypedArray array = getResources().obtainTypedArray(R.array.slide_menu_image_array);
        int len = array.length();
        int[] menuItemImages = new int[len];
        for (int i = 0; i < len; i++)
            menuItemImages[i] = array.getResourceId(i, 0);
        array.recycle();

        // Set the adapter for the list view
        slideMenuListView.setAdapter(new SlideMenuArrayAdapter(this, R.layout.slide_menu_list_item,
                menuItemValues, menuItemImages));
        // Set the list's click listener
        slideMenuListView.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        )
        {
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        try
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (NullPointerException e)
        {
            android.util.Log.e("Prokure", "Can not define home button functionality");
        }
    }

}
