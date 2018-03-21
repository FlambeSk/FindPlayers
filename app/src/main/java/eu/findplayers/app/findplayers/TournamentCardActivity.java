package eu.findplayers.app.findplayers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;

import eu.findplayers.app.findplayers.Fragments.Tab1Tournament;
import eu.findplayers.app.findplayers.Fragments.Tab2players;
import eu.findplayers.app.findplayers.Fragments.Tab3Settings;
import eu.findplayers.app.findplayers.Fragments.TournamentsFragment;

public class TournamentCardActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Integer logged_id;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private ImageView image;
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_card);
        final Bundle bundle = getIntent().getExtras();

        image =  new ImageView(TournamentCardActivity.this);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Picasso.with(this).load(bundle.getString("tournamentImage")).into(image, new Callback() {
            @Override
            public void onSuccess() {
                appBarLayout.setBackground(image.getDrawable());
            }

            @Override
            public void onError() {

            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //Getting ID of logged user
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        toolbar.setTitle("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tournament_card, menu);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    Tab1Tournament tab1Tournament = new Tab1Tournament();
                    return tab1Tournament;
                case 1:
                    Tab2players tab2players = new Tab2players();
                    return tab2players;
                case 2:
                    Tab3Settings tab3Settings = new Tab3Settings();
                    return tab3Settings;
                default:
                        return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

/*

    @Override
    public void onBackPressed() {
        TournamentsFragment tournamentsFragment = new TournamentsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, tournamentsFragment, "TournamentFragment");
        fragmentTransaction.commit();
    }
*/

}
