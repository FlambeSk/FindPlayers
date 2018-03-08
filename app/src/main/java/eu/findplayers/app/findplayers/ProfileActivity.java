package eu.findplayers.app.findplayers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import eu.findplayers.app.findplayers.Adapters.ProfileGamesAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.Data.MyData;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_image, back_button;
    TextView profile_name;
    private RecyclerView recyclerView, recyclerViewFriends;
    private GridLayoutManager gridLayoutManager;
    private ProfileGamesAdapter adapter;
    private ProfileFriendsAdapter friendAdapter;
    private List<MyData> data_list;
    private List<FriendsData> friends_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (TextView) findViewById(R.id.profile_name);

        //Getting info from bundle
        Bundle bundle = getIntent().getExtras();
        Picasso.with(ProfileActivity.this).load(bundle.getString("profile_image")).transform(new CropCircleTransformation()).into(profile_image);
        profile_name.setText(bundle.getString("profile_name"));

        //Populate RecyclerView with Games
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_frnd);
        data_list = new ArrayList<>();
        load_data_from_server(bundle.getInt("profile_id"));
        gridLayoutManager = new GridLayoutManager(ProfileActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ProfileGamesAdapter(ProfileActivity.this, data_list);
        recyclerView.setAdapter(adapter);

        //Populate Friends Recycler View
        load_friends_from_server(bundle.getInt("profile_id"));
        friends_list = new ArrayList<>();
        recyclerViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends_profile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFriends.setLayoutManager(layoutManager);
        friendAdapter = new ProfileFriendsAdapter(ProfileActivity.this, friends_list);
        recyclerViewFriends.setAdapter(friendAdapter);

        //BACK BUTTON
        back_button = (ImageView)findViewById(R.id.back_arrow);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });




        //SWIPE DOWN - refresh Activity
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_down_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                    }
                },1000);
            }
        });
    }

    private void load_data_from_server(final int id)
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://findplayers.eu/android/user_games.php?id="+id).build();

               // Bundle bundle = getActivity().getIntent().getExtras();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        MyData data = new MyData(object.getInt("id"), object.getString("name"), object.getString("small_image"), id);


                        data_list.add(data);
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
                adapter.notifyDataSetChanged();
            }
        };

        task.execute(id);

    }

    private void load_friends_from_server(final int id)
    {
        @SuppressLint("StaticFieldLeak")AsyncTask<Integer, Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://findplayers.eu/android/friend_list.php?id="+id).build();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        FriendsData data = new FriendsData(object.getString("profile_image"), object.getString("username"),  "info", object.getInt("friend_id"), id);

                        friends_list.add(data);
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
                friendAdapter.notifyDataSetChanged();
            }
        };
        task.execute(id);
    }
}
