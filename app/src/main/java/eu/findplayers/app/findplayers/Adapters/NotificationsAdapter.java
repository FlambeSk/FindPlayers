package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.renderscript.Long2;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.Data.NotificationsData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.UserActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by DOMA on 6.3.2018.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private static Context context;
    private List<NotificationsData> my_data;

    public NotificationsAdapter(Context context, List<NotificationsData> my_data)
    {
        this.context = context;
        this.my_data = my_data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final int itemPos = position;
        final NotificationsData notificationsData = my_data.get(position);
        String TimeHelp;
         holder.aboutTextView.setText(my_data.get(position).getAbout());
        holder.fromTextView.setText(my_data.get(position).getFromName());
        holder.from_id = my_data.get(position).getFrom_id();
        holder.to_id = my_data.get(position).getTo_id();
        Picasso.with(context).load(my_data.get(position).getImage()).transform(new CropCircleTransformation()).into(holder.imageNotification);
        //holder.time.setText(my_data.get(position).getTimestamp());
        TimeHelp = my_data.get(position).getTimestamp();
        Long TimeHelpInt = Long.valueOf(TimeHelp);
        TimeHelpInt = TimeHelpInt*1000;

       // CharSequence a = DateUtils.getRelativeDateTimeString(context,TimeHelpInt,DateUtils.SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS, 0);
        CharSequence a = DateUtils.getRelativeTimeSpanString(TimeHelpInt,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, 0);
        holder.time.setText(a);

            holder.fromTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Sweet alert
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Add to friends?")
                            .setConfirmText("Yes!")
                            .setCancelText("No!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    //Remove pending from Firebase
                                    removeFromPending(notificationsData.getTo_id(),notificationsData.getNotification_data(), itemPos);
                                    //add friends in Findplyers
                                    addFriend(my_data.get(position).getTo_id(),my_data.get(position).getFrom_id());



                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    //Remove pending from Firebase
                                    removeFromPending(notificationsData.getTo_id(), notificationsData.getNotification_data(), itemPos);
                                    //Remove friend pending from Findplyers
                                    removeFriendPending(my_data.get(position).getTo_id(),my_data.get(position).getFrom_id());
                                }
                            })
                            .show();
                }
            });



    }

    private void removeFromPending(Integer toId,final String notification_data, final int position)
    {
        FirebaseDatabase.getInstance().getReference()
                .child("notifications").child(toId.toString()).child(notification_data).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            my_data.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, my_data.size());
                        }
                    }
                });

    }

    private void removeFriendPending(final Integer toId, final Integer fromId)
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
                String logged_id_string = toId.toString();
                String user_id_string = fromId.toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("logged_id", logged_id_string);
                params.put("user_id", user_id_string);
                params.put("removePending", "add");

                return params;
            }

        };
        MySingleton.getInstance(context).addToRequestque(stringRequest);
    }

    private void addFriend(final Integer toID, final Integer fromId) {

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
                String logged_id_string = toID.toString();
                String user_id_string = fromId.toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("logged_id", logged_id_string);
                params.put("user_id", user_id_string);
                params.put("confirmAdd", "add");

                return params;
            }

        };
        MySingleton.getInstance(context).addToRequestque(stringRequest);

    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView aboutTextView;
        public TextView fromTextView, time;
        public Integer from_id,to_id;
        public ImageView imageNotification;



        public ViewHolder(final View itemView)
        {
            super(itemView);
            aboutTextView = (TextView) itemView.findViewById(R.id.aboutTextView);
            fromTextView = (TextView) itemView.findViewById(R.id.fromTextView);
            imageNotification = (ImageView) itemView.findViewById(R.id.imageNotification);
            time = (TextView) itemView.findViewById(R.id.time);


        }

    }
}
