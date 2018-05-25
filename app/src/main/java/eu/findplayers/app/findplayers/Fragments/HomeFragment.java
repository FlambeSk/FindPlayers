package eu.findplayers.app.findplayers.Fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Adapters.NewsAdapter;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.Data.NewsData;
import eu.findplayers.app.findplayers.Firebase.NotificationHelper;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.MainActivity;
import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    ImageView profile_image,send_news_button, add_news_image, image_for_upload;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;
    EditText send_news;
    String profileName, profileImage, image_for_upload_name, userFriends, userIDString ;
    String[] friendsOneByOne;
    Integer userID, friendsSize ;

    RecyclerView recycler_view_news;
    LinearLayoutManager linearLayoutManager;
    NewsAdapter newsAdapter;
    List<NewsData> newsData;

    NestedScrollView nestedScrollView;
    ProgressBar load_news;

    private static final int PICK_IMAGE_REQUEST = 22;
    private static final int STORAGE_PERMISSION_CODE = 2342;
    private Uri filePath;
    private Bitmap bitmap;

    ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.home));

        profile_image = (ImageView) getActivity().findViewById(R.id.profile_image);
        send_news = (EditText) getActivity().findViewById(R.id.send_news);
        send_news_button = (ImageView) getActivity().findViewById(R.id.send_news_button);
        add_news_image = (ImageView) getActivity().findViewById(R.id.add_news_image);
        image_for_upload = (ImageView) getActivity().findViewById(R.id.image_for_upload);
        nestedScrollView = (NestedScrollView) getActivity().findViewById(R.id.NestedScrollView);
        load_news = (ProgressBar) getActivity().findViewById(R.id.load_news);
        //Getting logged user and set header
        final Bundle bundle = getActivity().getIntent().getExtras();

        //Get User data from Database
        getUserData(bundle.getInt("id"));

        userID = bundle.getInt("id");
        userIDString = userID.toString();

        //uklada hodnoty na autologin
        editor = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt("login_id", bundle.getInt("id"));
        editor.putString("login_name", bundle.getString("name"));
        editor.putString("login_image", bundle.getString("profile_image"));
        editor.apply();


        //Click on Profile user image
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                bundle.putInt("profile_id", bundle.getInt("id"));
                bundle.putString("profile_name", profileName);
                bundle.putString("profile_image", bundle.getString("profile_image"));
                intent.putExtras(bundle);
                    getActivity().startActivity(intent);

            }
        });

        //set firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference helpRef = database.getReference();
        final DatabaseReference myRef = database.getReference("news");
        final DatabaseReference mess = helpRef.child("news");

        //Show news
        recycler_view_news = (RecyclerView) getActivity().findViewById(R.id.recycler_view_news);
        recycler_view_news.setNestedScrollingEnabled(false);
        newsData = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        recycler_view_news.setLayoutManager(linearLayoutManager);
        newsAdapter = new NewsAdapter(getActivity(), newsData);
        recycler_view_news.setAdapter(newsAdapter);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    if (i3 == 0)
                    {
                       // Toast.makeText(getActivity(), "Bottom", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }




        //On send icon click
        send_news_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Notification.Builder builder = helper.getEDMTChannelNotification("aaa","bbb");
                //helper.getManager().notify(new Random().nextInt(), builder.build());

                String news = send_news.getText().toString();

                if (news.equals(""))
                {
                    Toast.makeText(getActivity(), "Message is empty", Toast.LENGTH_SHORT).show();
                } else
                {
                    if (bitmap == null)
                    {


                        //Send news to Firebase
                        //get key
                        String key = myRef.push().getKey();
                        //get time
                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();
                        //set Data
                        NewsData newsData = new NewsData(key, profileName, profileImage, news, "text", timestamp, "no image", userID, 0);
                        String userid = userID.toString();
                        myRef.child(key).setValue(newsData);

                        send_news.setText("");
                    } else
                    {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setTitle("Uploading status");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setIndeterminate(false);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);

                        //Toast.makeText(getActivity(), "Je tu nieco", Toast.LENGTH_SHORT).show();
                        //Send news to Firebase
                        //get key
                        String key = myRef.push().getKey();
                        //get time
                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();
                        //set Data
                        NewsData newsData = new NewsData(key, profileName, profileImage, news, "image", timestamp, "http://findplayers.eu/uploads/news/"+image_for_upload_name+".jpeg", userID, 0);
                        String userid = userID.toString();

                        send_image_news(image_for_upload_name, bitmap);

                        myRef.child(key).setValue(newsData);

                        send_news.setText("");
                    }

                }

            }
        });

        //On add image click
        add_news_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                showFileChooser();
            }
        });

        //SWIPE DOWN - refresh Activity
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Fragment fragment = null;
                        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("HomeFragment");
                        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.detach(fragment);
                        fragmentTransaction.attach(fragment);
                        fragmentTransaction.commit();
                    }
                },1000);
            }
        });

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    public void getUserData(final int logged_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/user.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String respons = jsonObject.getString("response");
                    profileName = jsonObject.getString("username");
                    profileImage = jsonObject.getString("profile_image");
                    userFriends = jsonObject.getString("friends");
                    Picasso.with(getContext()).load(profileImage).transform(new CropCircleTransformation()).into(profile_image);
                    Log.d("Res", profileImage);
                    friendsOneByOne = userFriends.split(",");
                    friendsSize = friendsOneByOne.length;

                    load_news();


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
                String userID = String.valueOf(logged_id);
                Map<String, String> params = new HashMap<String, String>();
                params.put("get_user_data", "true");
                params.put("userID", userID);
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //Storage request
    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
               // profile_image.setImageBitmap(bitmap);
                //random name
                String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                Random RANDOM = new Random();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 20; i++) {
                    sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
                }

                String rnd = sb.toString();

                image_for_upload_name = "news_"+ rnd;
                image_for_upload.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(filePath).resize(500,300).centerCrop().into(image_for_upload);

            } catch (IOException e) {

            }
        } else {
            Toast.makeText(getActivity(), "Something is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    public void send_image_news(final String image_namee, final Bitmap bitmap)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/news.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response_news", response);
                progressDialog.dismiss();

                //Refresh fragment
                Fragment fragment = null;
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag("HomeFragment");
                final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
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

                Map<String, String> params = new HashMap<String, String>();
                params.put("news_with_image", "true");
                params.put("image_name", image_namee);
                params.put("image", image);
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }


    public void load_news()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference helpRef = database.getReference();
        final DatabaseReference mess = helpRef.child("news");
        //Order news by Time
        //New are first
        Query query = mess.orderByChild("timestamp");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = String.valueOf(dataSnapshot.child("fromName").getValue());
                String message = String.valueOf(dataSnapshot.child("message").getValue());
                String key = String.valueOf(dataSnapshot.child("key").getValue());
                String fromImage = String.valueOf(dataSnapshot.child("fromImage").getValue());
                String image = String.valueOf(dataSnapshot.child("image").getValue());
                String timestamp = String.valueOf(dataSnapshot.child("timestamp").getValue());
                String type = String.valueOf(dataSnapshot.child("type").getValue());
                Integer fromID = dataSnapshot.child("fromID").getValue(Integer.class);
                String fromIDString = String.valueOf(dataSnapshot.child("fromID").getValue());


                if (friendsSize != null)
                {
                    //Showing only friends news
                    for (int i=1; i<friendsSize; i++)
                    {
                        if (fromIDString.equals(friendsOneByOne[i])){
                            NewsData data = new NewsData(key, name, fromImage, message, type, timestamp, image, fromID, userID);
                            newsData.add(data);
                            newsAdapter.notifyDataSetChanged();
                            linearLayoutManager.scrollToPosition(recycler_view_news.getAdapter().getItemCount()-1);
                        }
                    }

                    //showing my news
                    if (fromIDString.equals(userIDString)){
                        NewsData data = new NewsData(key, name, fromImage, message, type, timestamp, image, fromID, userID);
                        newsData.add(data);
                        newsAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(recycler_view_news.getAdapter().getItemCount()-1);
                    }
                    load_news.setVisibility(View.GONE);
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
