package com.rx.kishor.rxjavaexamples;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rx.kishor.rxjavaexamples.examples.BufferExample;
import com.rx.kishor.rxjavaexamples.examples.ConcurrencyWithScheduler;
import com.rx.kishor.rxjavaexamples.examples.DebounceSearchEmitter;
import com.rx.kishor.rxjavaexamples.examples.DoubleBinding;
import com.rx.kishor.rxjavaexamples.examples.FormValidation;
import com.rx.kishor.rxjavaexamples.examples.RssReader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_holder, new IntroFragment(), IntroFragment.class.getCanonicalName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        String TAG = "";
        if (id == R.id.click_buffer_example) {
            TAG = BufferExample.class.getCanonicalName();
        } else if (id == R.id.concurrency_with_scheduler) {
            TAG = ConcurrencyWithScheduler.class.getCanonicalName();
        } else if (id == R.id.debounce_eearch_emitter) {
            TAG = DebounceSearchEmitter.class.getCanonicalName();
        } else if (id == R.id.form_validation) {
            TAG = FormValidation.class.getCanonicalName();
        } else if (id == R.id.rss_reader) {
            TAG = RssReader.class.getCanonicalName();
        } else if (id == R.id.double_binding) {
            TAG = DoubleBinding.class.getCanonicalName();
        }

        ft.replace(R.id.fragment_holder, checkFragmentExistence(TAG), TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment checkFragmentExistence(String TAG) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            if (TAG.equalsIgnoreCase(BufferExample.class.getCanonicalName())) {
                return new BufferExample();
            }
            if (TAG.equalsIgnoreCase(ConcurrencyWithScheduler.class.getCanonicalName())) {
                return new ConcurrencyWithScheduler();
            }
            if (TAG.equalsIgnoreCase(RssReader.class.getCanonicalName())) {
                return new RssReader();
            }
            if (TAG.equalsIgnoreCase(FormValidation.class.getCanonicalName())) {
                return new FormValidation();
            }
            if (TAG.equalsIgnoreCase(DebounceSearchEmitter.class.getCanonicalName())) {
                return new DebounceSearchEmitter();
            }
            if (TAG.equalsIgnoreCase(DoubleBinding.class.getCanonicalName())) {
                return new DoubleBinding();
            }
        } else {
            return fragment;
        }
        return null;
    }
}
