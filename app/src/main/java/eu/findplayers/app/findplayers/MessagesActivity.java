package eu.findplayers.app.findplayers;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.MessageAdapter;
import eu.findplayers.app.findplayers.Data.MessageData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class MessagesActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private List<MessageData> messageData;
    public String friendName, msg, logged_name;
    public Integer friendId, loggedId;
    EditText send_news;
    TextView Friend_name;
    ImageView back_arrow;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    //Help
    //ServerValue.TIMESTAMP
    String MSG_friendName,MSG_true,MSG_message_key;
    Integer MSG_childValue,MSG_child2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Friend_name = (TextView)findViewById(R.id.friendName);
        back_arrow = (ImageView)findViewById(R.id.back_arrow);


        send_news = (EditText)findViewById(R.id.send_news);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_messages);
        messageData = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(MessagesActivity.this, messageData);
        recyclerView.setAdapter(messageAdapter);




        //Getting values from FriendAdapter OR Notifications
        Bundle bundle = getIntent().getExtras();
        friendId = bundle.getInt("friend_id");
        loggedId = bundle.getInt("logged_id");
        friendName = bundle.getString("friend_name");

        //Set Friend name
        Friend_name.setText(friendName);

//        Toast.makeText(this, friendId + loggedId, Toast.LENGTH_SHORT).show();

        //set firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("messages");
        final DatabaseReference messageRef = database.getReference();
        final DatabaseReference mess = messageRef.child("messages");


        mess.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String eq_from_id = loggedId.toString();
                String eq_to_id = friendId.toString();
                String childValue = String.valueOf(dataSnapshot.child("from_id").getValue());
                String child2 = String.valueOf(dataSnapshot.child("to_id").getValue());
                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String message_key = String.valueOf(dataSnapshot.child("message_key").getValue());
                String friendName = String.valueOf(dataSnapshot.child("friendName").getValue());
                String time = String.valueOf(dataSnapshot.child("timestamp").getValue());
                msg = String.valueOf(dataSnapshot.child("message").getValue());

                ////////////
                MSG_friendName = friendName;
                MSG_true = "true";
                MSG_child2 = Integer.valueOf(child2);
                MSG_childValue = Integer.valueOf(childValue);
                MSG_message_key = message_key;

                //TODO timestamp

               if (childValue.equals(eq_from_id) && child2.equals(eq_to_id) || childValue.equals(eq_to_id) && child2.equals(eq_from_id))
                {
                    if (childValue.equals(eq_from_id) && child2.equals(eq_to_id)){
                        MessageData data = new MessageData(msg, friendName, isRead, message_key, time,true,  Integer.valueOf(childValue), Integer.valueOf(child2));
                        messageData.add(data);
                        messageAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                    } else{
                        MessageData data = new MessageData(msg, friendName, "true", message_key, time,false, Integer.valueOf(childValue), Integer.valueOf(child2));
                        messageData.add(data);
                        messageAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                        //myRef.child(message_key).setValue(data);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Getting NAME of logged user
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        logged_name = prefs.getString("login_name", "");//"No name defined" is the default value.


        //Send message after ENTER from TextView
        send_news.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (i == KeyEvent.KEYCODE_ENTER))
                //(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    //Getting message
                    String new_news = send_news.getText().toString();
                    //generatyng key for Firebase DB
                    String id = myRef.push().getKey();
                    // myRef.child(id).setValue("njkk");
                    if(!new_news.equals(""))
                    {

                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();
                        //Message Data
                        MessageData messageData = new MessageData(new_news, friendName, "false",id, timestamp, true, loggedId, friendId);

                        //Save data to Firebase Database
                        myRef.child(id).setValue(messageData);

                        //Send notification to another user
                       sendNotification(friendId,loggedId, logged_name,new_news);
                       String id_from_string = loggedId.toString();
                       String id_to_string = friendId.toString();

                       // Toast.makeText(MessagesActivity.this, id_from_string + id_to_string, Toast.LENGTH_SHORT).show();
                        //After Enter Clear EditText
                        send_news.setText("");
                    } else
                    {
                        Toast.makeText(MessagesActivity.this, "Fill message box", Toast.LENGTH_SHORT).show();
                    }


                    // Toast.makeText(getActivity(), new_news, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;

            }
        });

        //Back button
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void sendNotification(final Integer id_to, final Integer id_from,final String friend_name, final String body)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://findplayers.eu/View/firebase/send.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error
                        //Log.d("Error.Response", error);
                        // Toast.makeText(MessagesActivity.this, "Error", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {

                String id_to_string = id_to.toString();
                String id_from_string = id_from.toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id_to_string);
                params.put("from", id_from_string);
                params.put("friend_name", friend_name);
                params.put("body", body);
                params.put("notification", "message");
                params.put("image", "a");

                return params;
            }
        };
        MySingleton.getInstance(MessagesActivity.this).addToRequestque(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //set firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("messages");
        final DatabaseReference messageRef = database.getReference();
        final DatabaseReference mess = messageRef.child("messages");

        mess.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String eq_from_id = loggedId.toString();
                String eq_to_id = friendId.toString();
                String childValue = String.valueOf(dataSnapshot.child("from_id").getValue());
                String child2 = String.valueOf(dataSnapshot.child("to_id").getValue());
                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String message_key = String.valueOf(dataSnapshot.child("message_key").getValue());
                String friendName = String.valueOf(dataSnapshot.child("friendName").getValue());
                String time = String.valueOf(dataSnapshot.child("timestamp").getValue());
                msg = String.valueOf(dataSnapshot.child("message").getValue());

                //TODO timestamp

                if (childValue.equals(eq_from_id) && child2.equals(eq_to_id) || childValue.equals(eq_to_id) && child2.equals(eq_from_id))
                {
                    if (childValue.equals(eq_from_id) && child2.equals(eq_to_id)){

                    } else{
                        MessageData data = new MessageData(msg, friendName, "true", message_key, time,false, Integer.valueOf(childValue), Integer.valueOf(child2));

                        myRef.child(message_key).setValue(data);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
