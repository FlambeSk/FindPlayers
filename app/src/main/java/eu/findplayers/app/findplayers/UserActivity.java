package eu.findplayers.app.findplayers;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Adapters.ProfileGamesAdapter;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.Data.NotificationsData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    TextView profile_name;
    Integer profile_id,logged_id;
    ImageView profile_image, friendRequest, back_arrow;
    RecyclerView recyclerView;
    List<MyData> data_list;
    GridLayoutManager gridLayoutManager;
    private ProfileGamesAdapter adapter;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Bundle bundle;
    String loggedName, loggedImage, profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);



        //Getting ID of logged user
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.
        loggedName = prefs.getString("login_name", "");
        loggedImage = prefs.getString("login_image", "");


        bundle = getIntent().getExtras();
        profile_name = (TextView)findViewById(R.id.profile_name);
        profile_image = (ImageView)findViewById(R.id.profile_image);
        friendRequest = (ImageView) findViewById(R.id.friendRequest);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        profile_id = bundle.getInt("user_id");

        profile_name.setText(bundle.getString("user_name"));
        get_user(profile_id);

        //Setting Friend request button
        friend_request_buttons(logged_id, profile_id);

        //Populate RecyclerView with Games
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_user_games);
        data_list = new ArrayList<>();
        load_user_games_from_server(profile_id);
        gridLayoutManager = new GridLayoutManager(UserActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ProfileGamesAdapter(UserActivity.this, data_list);
        recyclerView.setAdapter(adapter);


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

        //On back button click
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void load_user_games_from_server(final int id)
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

    private void friend_request_buttons(final int logged_id, final int profile_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/isFriend.php?loggedId="+logged_id+"&profileId="+profile_id, new com.android.volley.Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String msg = jsonObject.getString("msg");

                    if(msg.equals("isFriend"))
                    {
                        friendRequest.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp);
                        friendRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //Sweet alert
                                new SweetAlertDialog(UserActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Are you sure?")
                                        .setContentText("Remove friend?")
                                        .setConfirmText("Yes!")
                                        .setCancelText("No!")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                //Remove friend
                                                add_friend(logged_id, profile_id, false);
                                                sweetAlertDialog.dismissWithAnimation();
                                                finish();
                                                overridePendingTransition( 0, 0);
                                                startActivity(getIntent());
                                                overridePendingTransition( 0, 0);

                                            }
                                        })
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }else if (msg.equals("friendPending"))
                    {
                        friendRequest.setBackgroundResource(R.drawable.ic_query_builder);
                    }
                    else
                    {
                        friendRequest.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                        friendRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Add friend
                                add_friend(logged_id, profile_id, true);
                                sendNotificationToDatabase(profile_id,logged_id,loggedName);
                                sendNotification(profile_id,logged_id, loggedName, "Friend request");
                                finish();
                                overridePendingTransition( 0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);
                            }
                        });
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserActivity.this, "Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {

        };
        MySingleton.getInstance(UserActivity.this).addToRequestque(stringRequest);
    }

    public void add_friend(final Integer logged_id, final Integer user_id, final boolean action)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/friendship.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                String logged_id_string = logged_id.toString();
                String user_id_string = user_id.toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("logged_id", logged_id_string);
                params.put("user_id", user_id_string);
                if (action)params.put("add", "add");

                return params;
            }

        };
        MySingleton.getInstance(UserActivity.this).addToRequestque(stringRequest);
    }

    public void sendNotification(final Integer id_to, final Integer id_from,final String friend_name, final String body)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/View/firebase/send.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
            }
        },
                new com.android.volley.Response.ErrorListener() {
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
                params.put("notification", "friendRequest");
                params.put("image", loggedImage);

                return params;
            }
        };
        MySingleton.getInstance(UserActivity.this).addToRequestque(stringRequest);
    }

    public void sendNotificationToDatabase(Integer id_to, Integer id_from, String from_name)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("notifications");

        String keys = myRef.push().getKey();

        Map time = ServerValue.TIMESTAMP;
        String timestamp = time.toString();

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        NotificationsData notificationsData = new NotificationsData(id_from, id_to, "Friend Request", from_name, keys, loggedImage ,ts, false);

        String idTo=id_to.toString();
        myRef.child(idTo).child(keys).setValue(notificationsData);


    }

    public void get_user(final int from)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/user.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String respons = jsonObject.getString("response");
                    profileImage = jsonObject.getString("profile_image");
                    Picasso.with(UserActivity.this).load(profileImage).transform(new CropCircleTransformation()).into(profile_image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                String userID = String.valueOf(from);
                Map<String, String> params = new HashMap<String, String>();
                params.put("get_user_data", "true");
                params.put("userID", userID);
                return params;
            }
        };
        MySingleton.getInstance(UserActivity.this).addToRequestque(stringRequest);
    }
}
