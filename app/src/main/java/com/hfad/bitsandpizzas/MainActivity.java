package com.hfad.bitsandpizzas;

import android.app.FragmentManager;
import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

public class MainActivity extends Activity {

    private ShareActionProvider shareActionProvider;

    private String [] titles;
    private ListView drawerList;

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.app_name);

        titles = getResources().getStringArray(R.array.titles);

        drawerList = findViewById(R.id.drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1,
                titles));

        drawerLayout = findViewById(R.id.drawer_layout);

        if (savedInstanceState!=null){
            currentPosition = (int)savedInstanceState.get("position");
            setActionBarTitle(currentPosition);
        }
        else{
            selectItem(0);
        }

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);


        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getFragmentManager().findFragmentByTag("visible_fragment");
                if (fragment instanceof TopFragment)
                    currentPosition = 0;
                else if (fragment instanceof PizzaFragment)
                    currentPosition = 1;
                else if (fragment instanceof PastaFragment)
                    currentPosition = 2;
                else if (fragment instanceof StoresFragment)
                    currentPosition = 3;

                setActionBarTitle(currentPosition);
                drawerList.setItemChecked(currentPosition,true);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void selectItem(int position) {

        Fragment fragment;
        currentPosition = position;
        switch(position) {
            case 1:
                fragment = new PizzaFragment();
                break;
            case 2:
                fragment = new PastaFragment();
                break;
            case 3:
                fragment = new StoresFragment();
                break;
            default:
                fragment = new TopFragment();

        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_frame,fragment,"visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setActionBarTitle(position);
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position){
        String title;
        if (position == 0)
            title = getResources().getString(R.string.app_name);
        else
            title = titles[position];

        getActionBar().setTitle(title);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        setIntent("This is example text");
        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_create_order:
                Intent i = new Intent(this,OrderActivity.class);
                startActivity(i);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position",currentPosition);
        super.onSaveInstanceState(outState);
    }


}
