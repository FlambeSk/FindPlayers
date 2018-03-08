package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.UserActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by DOMA on 27.2.2018.
 */

public class ProfileFriendsAdapter extends RecyclerView.Adapter<ProfileFriendsAdapter.ViewHolder>{

    private static Context context;
    private List<FriendsData> friendsData;

    public ProfileFriendsAdapter(Context context, List<FriendsData> friendsData) {

        this.context = context;
        this.friendsData = friendsData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_friends_in_profile, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.friend_name.setText(friendsData.get(position).getUsername());
        Picasso.with(context).load(friendsData.get(position).getProfile_image()).transform(new CropCircleTransformation()).into(holder.friend_image);

        //After click on profile image open ProfileActivity
        holder.friend_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer user_id = friendsData.get(position).getFriend_id();
                String user_name = friendsData.get(position).getUsername();
                String profile_img = friendsData.get(position).getProfile_image();
                Intent intent = new Intent(context, UserActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putInt("user_id",user_id);
                bundle.putString("user_name", user_name);
                bundle.putString("profile_image", profile_img);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView friend_image;
        TextView friend_name;


        public ViewHolder(View itemView) {
            super(itemView);

            friend_image = itemView.findViewById(R.id.friend_image);
            friend_name = itemView.findViewById(R.id.friend_name);
        }
    }



}
