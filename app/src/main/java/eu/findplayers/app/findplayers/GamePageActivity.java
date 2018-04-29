package eu.findplayers.app.findplayers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.findplayers.app.findplayers.Adapters.CommentAdapter;
import eu.findplayers.app.findplayers.Adapters.ProfileFriendsAdapter;
import eu.findplayers.app.findplayers.Data.CommentData;
import eu.findplayers.app.findplayers.Data.FriendsData;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GamePageActivity extends AppCompatActivity {

    Integer game_id;
    TextView gameName;
    ImageView gameImage, back_arrow_button, send_comment;
    private RecyclerView recyclerUsers, recyclerComments;
    private LinearLayoutManager linearLayoutManager, linearLayoutManagerComments;
    private ProfileFriendsAdapter userAdapter;
    private List<FriendsData> user_list;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences editor;
    Integer id_after_login;
    private CommentAdapter commentAdapter;
    private List<CommentData> commentDataList;
    EditText comment_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        gameName = (TextView)findViewById(R.id.game_Name);
        gameImage = (ImageView)findViewById(R.id.game_Image);
        back_arrow_button = (ImageView)findViewById(R.id.back_arrow);
        send_comment = (ImageView) findViewById(R.id.send_comment);
        comment_text = (EditText) findViewById(R.id.comment_text);

        Bundle bundle = getIntent().getExtras();
        gameName.setText(bundle.getString("game_name"));
        game_id = bundle.getInt("game_id");
        Picasso.with(GamePageActivity.this).load(bundle.getString("game_image")).into(gameImage);

        //Get loggined user ID
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        id_after_login = editor.getInt("login_id", 0);

        //Populate userList
        load_users_from_server(bundle.getInt("game_id"), id_after_login);
        user_list = new ArrayList<>();
        recyclerUsers = (RecyclerView) findViewById(R.id.recycler_view_users);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerUsers.setLayoutManager(linearLayoutManager);
        userAdapter = new ProfileFriendsAdapter(GamePageActivity.this, user_list);
        recyclerUsers.setAdapter(userAdapter);


        //Set Back arrow
        back_arrow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
              finish();
            }
        });

        //Show comments
        showComments(game_id.toString(), id_after_login);
        recyclerComments = (RecyclerView) findViewById(R.id.recycler_view_game_comments);
        commentDataList = new ArrayList<>();
        linearLayoutManagerComments = new LinearLayoutManager(this);
        linearLayoutManagerComments.setStackFromEnd(true);
        recyclerComments.setLayoutManager(linearLayoutManagerComments);
        commentAdapter = new CommentAdapter(GamePageActivity.this, commentDataList);
        recyclerComments.setAdapter(commentAdapter);

        //Send comment button
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("game_comments");
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comment_text.getText().toString();
                if (comment.equals(""))
                {
                    Toast.makeText(GamePageActivity.this, "Write comment", Toast.LENGTH_SHORT).show();
                } else
                {
                    //Timestamp
                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();
                    //Get new key
                    String new_key = myRef.push().getKey();

                    CommentData dataC = new CommentData(comment, "image", timestamp, game_id.toString(),new_key, id_after_login, 0);
                    myRef.child(game_id.toString()).child(new_key).setValue(dataC);
                    comment_text.setText("");
                }
            }
        });
    }

    //Load users whose have game
    private void load_users_from_server(final int id, final int loggedID)
    {
        @SuppressLint("StaticFieldLeak")AsyncTask<Integer, Void, Void> task= new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://findplayers.eu/android/game.php?id="+id+"&users=true&loggedID="+loggedID).build();

                try{
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        FriendsData data = new FriendsData(object.getString("profile_image"), object.getString("username"),  "info", object.getInt("friend_id"), id);

                        user_list.add(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of content");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                userAdapter.notifyDataSetChanged();
            }
        };
        task.execute(id);
    }

    private void showComments(final String gameID, final Integer loggedID)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference helpRef = database.getReference();
        final DatabaseReference mess = helpRef.child("game_comments").child(gameID);

        mess.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String comment = String.valueOf(dataSnapshot.child("comment").getValue());
                String comment_id = String.valueOf(dataSnapshot.child("comment_id").getValue());
                Integer fromID = dataSnapshot.child("fromID").getValue(Integer.class);
                String fromImage = String.valueOf(dataSnapshot.child("fromImage").getValue());
                String news_key = String.valueOf(dataSnapshot.child("news_key").getValue());
                String timestamp = String.valueOf(dataSnapshot.child("timestamp").getValue());

                CommentData cmdata = new CommentData(comment,fromImage,timestamp,news_key,comment_id,fromID, loggedID);
                commentDataList.add(cmdata);
                commentAdapter.notifyDataSetChanged();
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
