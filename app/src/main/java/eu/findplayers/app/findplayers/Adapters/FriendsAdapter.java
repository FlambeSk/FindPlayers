package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.MessagesActivity;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by DOMA on 27.2.2018.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    private static Context context;
    private List<FriendsData> friendsData;

    public FriendsAdapter(Context context, List<FriendsData> friendsData) {

        this.context = context;
        this.friendsData = friendsData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder holder, final int position) {

        holder.username.setText(friendsData.get(position).getUsername());
        Picasso.with(context).load(friendsData.get(position).getProfile_image()).transform(new CropCircleTransformation()).into(holder.imageView);
        //Firebase New Messages
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();
        final DatabaseReference count = reference.child("messages");

        count.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                String fromID = String.valueOf(dataSnapshot.child("from_id").getValue());
                String lastMSG = String.valueOf(dataSnapshot.child("message").getValue());
                if (childer.equals(String.valueOf(friendsData.get(position).getLogged_id())) && fromID.equals(String.valueOf(friendsData.get(position).getFriend_id())) && isRead.equals("false")){
                    holder.username.setTextSize(22);
                    holder.username.setTypeface(holder.username.getTypeface(), Typeface.BOLD);
                    holder.lastMessage.setText("New message: " +lastMSG);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                String fromID = String.valueOf(dataSnapshot.child("from_id").getValue());
                String lastMSG = String.valueOf(dataSnapshot.child("message").getValue());
                if (childer.equals(String.valueOf(friendsData.get(position).getLogged_id())) && fromID.equals(String.valueOf(friendsData.get(position).getFriend_id())) && isRead.equals("false")){

                    holder.username.setTextSize(22);
                    holder.username.setTypeface(holder.username.getTypeface(), Typeface.BOLD);
                    holder.lastMessage.setText("New message: " +lastMSG);
                } else
                {

                    holder.username.setTypeface(holder.username.getTypeface(), Typeface.NORMAL);
                    holder.username.setTextSize(16);
                    holder.lastMessage.setText("");
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String isRead = String.valueOf(dataSnapshot.child("isRead").getValue());
                String childer = String.valueOf(dataSnapshot.child("to_id").getValue());
                String fromID = String.valueOf(dataSnapshot.child("from_id").getValue());
                if (childer.equals(String.valueOf(friendsData.get(position).getLogged_id())) && fromID.equals(String.valueOf(friendsData.get(position).getFriend_id())) && isRead.equals("false")){

                    holder.username.setTextSize(22);
                    //holder.username.setTypeface(holder.username.getTypeface(), Typeface.BOLD);
                }
                else
                {
                    holder.username.setTextSize(16);
                   // holder.username.setTypeface(holder.username.getTypeface(), Typeface.NORMAL);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, friends_data.get(position).getId() + "_"+ friends_data.get(position).getLogged_id(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MessagesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("friend_id", friendsData.get(position).getFriend_id());
                bundle.putInt("logged_id", friendsData.get(position).getLogged_id());
                bundle.putString("friend_name", friendsData.get(position).getUsername());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, friendsData.get(position).getUsername(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView username, lastMessage;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);


        }
    }
}
