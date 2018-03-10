package eu.findplayers.app.findplayers;

import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.NotificationsAdapter;
import eu.findplayers.app.findplayers.Data.NotificationsData;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NotificationsAdapter notificationsAdapter;
    private List<NotificationsData> notificationsDataList;
    private ArrayList<String> mKeys = new ArrayList<>();
    String friendName, about, notificationImage;
    Integer friendId, loggedId;
    ImageView back_arrow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //Getting values from FriendAdapter
        Bundle bundle = getIntent().getExtras();

        friendName = bundle.getString("friend_name");
        about = bundle.getString("about");
        friendId = bundle.getInt("friend_id");
        loggedId = bundle.getInt("logged_id");
        notificationImage = bundle.getString("image");

        //Toast.makeText(NotificationsActivity.this, "friendName:"+friendName + "About:"+about+"Friend ID: "+friendId.toString()+ "Logged ID: "+ loggedId.toString(), Toast.LENGTH_LONG).show();

        //Set notifications
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_notifications);
        notificationsDataList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationsAdapter = new NotificationsAdapter(NotificationsActivity.this, notificationsDataList);
        recyclerView.setAdapter(notificationsAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference messageRef = database.getReference();
        final DatabaseReference mess = messageRef.child("notifications").child(loggedId.toString());

        mess.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String whoSendNotification = String.valueOf(dataSnapshot.child("fromName").getValue());
                Integer from_id = dataSnapshot.child("from_id").getValue(Integer.class);
                Integer to_id = dataSnapshot.child("to_id").getValue(Integer.class);
                String aboutNotification = String.valueOf(dataSnapshot.child("about").getValue());
                String notifi_data = String.valueOf(dataSnapshot.child("notification_data").getValue());
                String notification_image = String.valueOf(dataSnapshot.child("image").getValue());

                String casik = String.valueOf(dataSnapshot.child("timestamp").getValue());


                NotificationsData data = new NotificationsData(from_id,to_id,aboutNotification, whoSendNotification, notifi_data, notification_image,casik, false);
                notificationsDataList.add(data);

                String key = dataSnapshot.getKey();
                mKeys.add(key);

                notificationsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
