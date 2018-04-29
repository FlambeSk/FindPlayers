package eu.findplayers.app.findplayers.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Data.CommentData;
import eu.findplayers.app.findplayers.Data.NewsData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.UserActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private static Context context;
    private List<NewsData> newsData;
    private int fromID;
    String profileImage, profileImageString;

    public NewsAdapter(Context context, List<NewsData> newsData)
    {
        this.context = context;
        this.newsData = newsData;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (newsData.get(position).getType().equals("text"))
        {
            return 1;
        }
        else if (newsData.get(position).getType().equals("image")){
            return 2;
        } else
        {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_text, parent,  false);
            return new ViewHolder(itemView);
        } else if (viewType == 2)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_image, parent,  false);
            return new ViewHolder(itemView);
        } else
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_text, parent,  false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String news_key, fromImage;
        final Integer logged_id;
        holder.name.setText(newsData.get(position).getFromName());
        holder.message.setText(newsData.get(position).getMessage());
        //Glide.with(context).load(newsData.get(position).getImage()).into(holder.image);

        //get news key
        news_key = newsData.get(position).getKey();

        //get logged_id
        logged_id = newsData.get(position).getLoggedID();

        //User Image
        get_user_imageView(newsData.get(position).getFromID(), holder.news_user_image);

        //Picasso.with(context).load(newsData.get(position).getFromImage()).transform(new CropCircleTransformation()).into(holder.news_user_image);
        final Uri uri = Uri.parse(newsData.get(position).getImage());
        holder.draweeView.setImageURI(uri);

        final String[] a={newsData.get(position).getImage()};

        //On image lick
        holder.draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(context, a).setStartPosition(0).show();

            }
        });

        //set time
        String TimeHelp = newsData.get(position).getTimestamp();
        Long TimeHelpInt = Long.valueOf(TimeHelp);
        TimeHelpInt = TimeHelpInt*1000;

        // CharSequence a = DateUtils.getRelativeDateTimeString(context,TimeHelpInt,DateUtils.SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS, 0);
        CharSequence t = DateUtils.getRelativeTimeSpanString(TimeHelpInt,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, 0);
        holder.timestamp.setText(t);

        get_user(newsData.get(position).getFromID());

        //On profile image click
        holder.news_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newsData.get(position).getLoggedID() != newsData.get(position).getFromID())
                {
                    Intent intent = new Intent(context, UserActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id",newsData.get(position).getFromID());
                    bundle.putString("user_name", newsData.get(position).getFromName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    bundle.putInt("profile_id", newsData.get(position).getLoggedID());
                    bundle.putString("profile_name", newsData.get(position).getFromName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

            }
        });

        //On Comment button click
        holder.comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComments(holder.comment_popup, news_key, logged_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name, message, timestamp;
        public SimpleDraweeView draweeView;
        public String type;
        public ImageView news_user_image, comment_image;
        public Dialog comment_popup;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.image2);
            draweeView.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
            news_user_image = (ImageView) itemView.findViewById(R.id.news_user_image);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            comment_image = (ImageView) itemView.findViewById(R.id.comment_image);
            comment_popup = new Dialog(context);
        }
    }

    public void showComments(Dialog dialog, final String key, final Integer loggedID)
    {
        //set firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("news_comments");


        final ImageView send_comment;
        final EditText comment_text;
        RecyclerView recycler_comments;
        LinearLayoutManager linearLayoutManager;
        CommentAdapter commentAdapter;
        List<CommentData> commentDataList;



        dialog.setContentView(R.layout.comments_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        send_comment = (ImageView) dialog.findViewById(R.id.send_comment);
        comment_text = (EditText) dialog.findViewById(R.id.comment_text);
        recycler_comments = (RecyclerView) dialog.findViewById(R.id.recycler_comments);

        //Show comments
        recycler_comments.setNestedScrollingEnabled(false);
        commentDataList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recycler_comments.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(context, commentDataList);
        recycler_comments.setAdapter(commentAdapter);
        //load comments
        load_comments(key,commentDataList, commentAdapter, loggedID);

        get_user(loggedID);

        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comment_text.getText().toString();
                if (comment.equals(""))
                {
                    Toast.makeText(context, "Write comment", Toast.LENGTH_SHORT).show();
                } else
                {

                    //Timestamp
                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();
                    //Get new key
                    String new_key = myRef.push().getKey();

                    CommentData data = new CommentData(comment, profileImageString, timestamp, key, new_key, loggedID, 0);
                     myRef.child(key).child(new_key).setValue(data);
                     comment_text.setText("");
                }

            }
        });


    }

    public void get_user_imageView(final int from, final ImageView a)
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
                    profileImage = jsonObject.getString("profile_image");
                    Picasso.with(context).load(profileImage).transform(new CropCircleTransformation()).into(a);


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
        MySingleton.getInstance(context).addToRequestque(stringRequest);
    }

    public void get_user(final int from)
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
                    profileImageString = jsonObject.getString("profile_image");

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
        MySingleton.getInstance(context).addToRequestque(stringRequest);
    }

    public void load_comments(final String ID, final List<CommentData> commentDataList, final  CommentAdapter commentAdapter, final Integer loggedID)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference helpRef = database.getReference();
        final DatabaseReference mess = helpRef.child("news_comments").child(ID);

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
