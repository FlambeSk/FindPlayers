package eu.findplayers.app.findplayers;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Pair;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import eu.findplayers.app.findplayers.Fragments.FriendsFragment;
import eu.findplayers.app.findplayers.Fragments.GamesFragment;
import eu.findplayers.app.findplayers.Fragments.HomeFragment;
import eu.findplayers.app.findplayers.Fragments.TournamentsFragment;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public String name_after_login, password_after_login, logged_name;
    SharedPreferences editor;
    SharedPreferences.Editor editShared;
    String logged_id;
    TextView nav_username;
    ImageView nav_profile_img;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Integer legged_id;
    long countNotifications;
    Switch openDrawer;
    Boolean Driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Getting logged user and set header
        final Bundle bundle = getIntent().getExtras();

    if (savedInstanceState == null)
    {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment, "HomeFragment");
        fragmentTransaction.commit();
    }

        //Check if switch is on or off
        //If drawer stay open or closed
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Driver =  editor.getBoolean("openedDriver", true);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //Change  Drawer Icon when new messages
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //If is set to always open Drawer
        if (Driver)
        {
            drawer.openDrawer(GravityCompat.START);
        }


        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);




        //Switch
        openDrawer = (Switch) findViewById(R.id.switch1);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.nav_switch);
        View actionToggleView = MenuItemCompat.getActionView(item);
        openDrawer = actionToggleView.findViewById(R.id.switch1);
        openDrawer.setChecked(Driver);
        openDrawer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String a = String.valueOf(b);
                editShared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editShared.putBoolean("openedDriver", b);
                editShared.apply();
            }
        });

        legged_id = bundle.getInt("id");
        logged_id = legged_id.toString();

        //create Messages Notifications
        MenuItem Messages = menu.findItem(R.id.nav_messages);


        //Firebase Notifications
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();
        final DatabaseReference count = reference.child("newMessages");
        LinearLayout linearLayoutt = (LinearLayout)findViewById(R.id.messages_notifi) ;

        count.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                if (childer.equals(logged_id) && isRead.equals("false")){
                        navigationView.getMenu().getItem(1).setActionView(R.layout.messages_news_icon);
                        toggle.setHomeAsUpIndicator(R.drawable.drawer_new);

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                if (childer.equals(logged_id)  && isRead.equals("false")){
                        navigationView.getMenu().getItem(1).setActionView(R.layout.messages_news_icon);
                        toggle.setHomeAsUpIndicator(R.drawable.drawer_new);
                }else
                {
                    navigationView.getMenu().getItem(1).setActionView(null);
                    toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                if (childer.equals(logged_id)  && isRead.equals("false")){
                    navigationView.getMenu().getItem(1).setActionView(null);
                    toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
                }else
                {
                    navigationView.getMenu().getItem(1).setActionView(null);
                    toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //Set subscribe notification to User ID
        FirebaseMessaging.getInstance().subscribeToTopic(logged_id);

        //Set Navigation Username & Img
        nav_username = (TextView)header.findViewById(R.id.nav_user_name);
        nav_profile_img = (ImageView)header.findViewById(R.id.nav_profile_img);
        nav_username.setText(bundle.getString("name"));
        Picasso.with(MainActivity.this).load(bundle.getString("profile_image")).transform(new CropCircleTransformation()).into(nav_profile_img);

        //Click on Profile user image
        nav_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                Bundle profile_bundle = new Bundle();
                profile_bundle.putInt("profile_id", bundle.getInt("id"));
                profile_bundle.putString("profile_name", bundle.getString("name"));
                profile_bundle.putString("profile_image", bundle.getString("profile_image"));
                intent.putExtras(profile_bundle);
                MainActivity.this.startActivity(intent);
            }
        });

        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.o
        getMenuInflater().inflate(R.menu.main, menu);


        //Firebase Notifications
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();
        final DatabaseReference count = reference.child("notifications");

        count.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                countNotifications = dataSnapshot.getChildrenCount();
                String childer = dataSnapshot.getKey();
                if (childer.equals(logged_id)){
                    if (Long.toString(countNotifications).equals("0"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_0));
                    } else if (Long.toString(countNotifications).equals("1"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_1));
                    } else if (Long.toString(countNotifications).equals("2"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_2));
                    } else if (Long.toString(countNotifications).equals("3"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_3));
                    } else if (Long.toString(countNotifications).equals("4"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_4));
                    }else if (Long.toString(countNotifications).equals("5"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_5));
                    }else if (Long.toString(countNotifications).equals("6"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_6));
                    }else if (Long.toString(countNotifications).equals("7"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_7));
                    }else if (Long.toString(countNotifications).equals("8"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_8));
                    }else if (Long.toString(countNotifications).equals("9"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_9));
                    } else
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_10));
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                countNotifications = dataSnapshot.getChildrenCount();
                String childer = dataSnapshot.getKey();
                if (childer.equals(logged_id)){
                    if (Long.toString(countNotifications).equals("0"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_0));
                    } else if (Long.toString(countNotifications).equals("1"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_1));
                    }else if (Long.toString(countNotifications).equals("2"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_2));
                    } else if (Long.toString(countNotifications).equals("3"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_3));
                    } else if (Long.toString(countNotifications).equals("4"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_4));
                    }else if (Long.toString(countNotifications).equals("5"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_5));
                    }else if (Long.toString(countNotifications).equals("6"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_6));
                    }else if (Long.toString(countNotifications).equals("7"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_7));
                    }else if (Long.toString(countNotifications).equals("8"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_8));
                    }else if (Long.toString(countNotifications).equals("9"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_9));
                    } else
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_10));
                    }

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                countNotifications = dataSnapshot.getChildrenCount();
                String childer = dataSnapshot.getKey();
                if (childer.equals(logged_id)){
                    if (Long.toString(countNotifications).equals("0"))
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_0));
                    } else
                    {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.notifi_0));
                    }

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        } else if ( id == R.id.action_notifications) {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Bundle notifiBundle = new Bundle();
            notifiBundle.putInt("logged_id", legged_id);
            intent.putExtras(notifiBundle);
            startActivity(intent);
        } else if (id == R.id.action_search)
        {
            Intent intent = new Intent(this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, homeFragment, "HomeFragment");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_messages) {

            FriendsFragment friendsFragment = new FriendsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, friendsFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_games) {

            GamesFragment gamesFragment = new GamesFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, gamesFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_tournaments) {

            TournamentsFragment tournamentsFragment = new TournamentsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, tournamentsFragment, "TournamentFragment");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {

            editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            name_after_login = editor.getString("name_login", null);//"No name defined" is the default value.

            //Clear saved variables for autologin
            editor.edit().clear().commit();

            //Unsubscribe notification to User ID
            FirebaseMessaging.getInstance().unsubscribeFromTopic(logged_id);

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("LOGOUT", "logout");
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
