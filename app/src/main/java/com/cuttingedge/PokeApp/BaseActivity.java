package com.cuttingedge.PokeApp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cuttingedge.PokeApp.Backpack.BackpackActivity;
import com.cuttingedge.PokeApp.BillsPC.BillsPCActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by Robbe Sneyders
 *
 * This activity is the base_menu for all other activities and handles the drawer layout.
 * This activity is not important for the library.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected Toolbar toolbar;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Pokedex.setup(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract void reset();

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch ( item.getItemId()) {

            case R.id.nav_backpack:
                if (!(this instanceof BackpackActivity)) {
                    Intent backpackIntent = new Intent(this, BackpackActivity.class);
                    startActivity(backpackIntent);
                    overridePendingTransition(0, 0);
                }
                break;
            case R.id.nav_bills_pc:
                if (!(this instanceof BillsPCActivity)) {
                    Intent billsPCIntent = new Intent(this, BillsPCActivity.class);
                    startActivity(billsPCIntent);
                    overridePendingTransition(0, 0);
                }
                break;
            case R.id.nav_share:
                String message = getString(R.string.share_message);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(share, getString(R.string.share)));
                break;
            case R.id.nav_github:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RobbeSneyders/UndoRecycler"));
                startActivity(i);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toast(String message, int duration) {
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(this, message, duration);
        toast.show();
    }
}
