package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Data.CommentData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.UserActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static Context context;
    private List<CommentData> commentData;

    public CommentAdapter(Context context, List<CommentData> commentData)
    {
        this.context = context;
        this.commentData = commentData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.comment.setText(commentData.get(position).getComment());
        //Picasso.with(context).load(commentData.get(position).getFromImage()).transform(new CropCircleTransformation()).into(holder.comment_profile_image);
        load_user(commentData.get(position).getFromID(), holder.comment_profile_image);

        //On user image click
        holder.comment_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (commentData.get(position).getFromID() == commentData.get(position).getLoggedID())
                {
                    Intent intent = new Intent(context, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    bundle.putInt("profile_id", commentData.get(position).getFromID());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else
                {
                    Intent intent = new Intent(context, UserActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", commentData.get(position).getFromID());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView comment;
        ImageView comment_profile_image;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.comment);
            comment_profile_image = (ImageView) itemView.findViewById(R.id.comment_profile_image);
        }
    }

    public void load_user(final Integer userID, final ImageView iw)
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
                   String profileImage = jsonObject.getString("profile_image");
                    Picasso.with(context).load(profileImage).transform(new CropCircleTransformation()).into(iw);


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
                String id = userID.toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("get_user_data", "true");
                params.put("userID", id);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestque(stringRequest);
    }
}
