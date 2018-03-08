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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.findplayers.app.findplayers.Adapters.ProfileFriendsAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GamePageActivity extends AppCompatActivity {

    Integer game_id;
    TextView gameName;
    ImageView gameImage, back_arrow_button;
    private RecyclerView recyclerUsers;
    private LinearLayoutManager linearLayoutManager;
    private ProfileFriendsAdapter userAdapter;
    private List<FriendsData> user_list;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences editor;
    Integer id_after_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        gameName = (TextView)findViewById(R.id.game_Name);
        gameImage = (ImageView)findViewById(R.id.game_Image);
        back_arrow_button = (ImageView)findViewById(R.id.back_arrow);

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
    }

    //Load users whose have game
    private void load_users_from_server(final int id, final int loggedID)
    {
        @SuppressLint("StaticFieldLeak")AsyncTask<Integer, Void, Void> task= new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://findplayers.eu/android/game.php?id="+id+"&users=true&loggedID="+loggedID).build();

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
}
