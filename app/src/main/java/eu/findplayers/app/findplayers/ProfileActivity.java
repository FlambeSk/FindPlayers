package eu.findplayers.app.findplayers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.ProfileFriendsAdapter;
import eu.findplayers.app.findplayers.Adapters.ProfileGamesAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
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
    Button choose_image, save_image;
    private static final int PICK_IMAGE_REQUEST = 22;
    private static final int STORAGE_PERMISSION_CODE = 2342;
    private Uri filePath;
    private Bitmap bitmap;
    ProgressDialog progressDialog;
    Integer user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
        setContentView(R.layout.activity_profile);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (TextView) findViewById(R.id.profile_name);
        choose_image = (Button) findViewById(R.id.choose_profile_image);
        save_image = (Button) findViewById(R.id.save_profile_image);

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
        user_id = bundle.getInt("profile_id");
        load_friends_from_server(user_id);
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

        //Edit profile image
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        //Save profile Image
        save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("Uploading image");
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                uploadImage(bitmap, user_id);
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

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //Storage request
    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);

                Picasso.with(ProfileActivity.this).load(filePath).transform(new CropCircleTransformation()).into(profile_image);
                choose_image.setVisibility(View.GONE);
                save_image.setVisibility(View.VISIBLE);

            } catch (IOException e) {

            }
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
        }
    }
    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    public void uploadImage(final Bitmap bitmap, final int user_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/user.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                progressDialog.dismiss();
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressDialog.dismiss();
                //error
                //Log.d("Error.Response", error);
                // Toast.makeText(MessagesActivity.this, "Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                String image = getStringImage(bitmap);
                String id = String.valueOf(user_id);

                Map<String, String> params = new HashMap<String, String>();
                params.put("upload_profile_image", "true");
                params.put("profile_id", id);
                params.put("image", image);
                return params;
            }
        };
        MySingleton.getInstance(ProfileActivity.this).addToRequestque(stringRequest);
    }
}
