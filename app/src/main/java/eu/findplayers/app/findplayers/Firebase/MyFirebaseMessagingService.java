package eu.findplayers.app.findplayers.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import eu.findplayers.app.findplayers.AutoLoginActivity;
import eu.findplayers.app.findplayers.Fragments.HomeFragment;
import eu.findplayers.app.findplayers.MessagesActivity;
import eu.findplayers.app.findplayers.NotificationsActivity;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 26.2.2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    JSONObject object;
    String  friend_name, text, notification;
    Integer from_id, to_id;
    String title;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.d(TAG, "FROM: " + remoteMessage);

        //Check if message contains data
        if(remoteMessage.getData().size() >0) {
            Log.d(TAG, "Message data:" +remoteMessage.getData());
            Map<String, String> custom_data = remoteMessage.getData();
            try{
                object = new JSONObject(custom_data);
                to_id = object.getInt("to_id");
                from_id = object.getInt("from_id");
                friend_name = object.getString("friend_name");
                text = object.getString("text");
                notification = object.getString("notification");
                if (notification.equals("friendRequest"))
                {
                    sendNotification(title, friend_name, from_id, to_id, "FriendRequest", "FriendRequest");
                } else if (notification.equals("message")){
                    sendNotification(title, text,from_id, to_id, friend_name, "message");
                }


            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        //Check if message contain notification
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Body:" + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            Map<String, String> custom_data = remoteMessage.getData();
            try{
                object = new JSONObject(custom_data);
                to_id = object.getInt("to_id");
                from_id = object.getInt("from_id");
                friend_name = object.getString("friend_name");
                sendNotification(title, remoteMessage.getNotification().getBody(),from_id, to_id, friend_name, "message");

            } catch (JSONException e){
                e.printStackTrace();
            }


            Log.d(TAG, "Object data:" +object);



        }
    }

    private void sendNotification(String title, String body,Integer from_id, Integer to_id, String friend_name, String about)
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        //TODO changed ID
        //Add intent to message
        if (about.equals("FriendRequest"))
        {
            intent = new Intent(this, NotificationsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            bundle.putString("friend_name", body);
        } else if (about.equals("message"))
        {
            intent = new Intent(this, MessagesActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            bundle.putString("friend_name", friend_name);
        }

        //Setting bundle
        //After click notification open messageActivity

        bundle.putInt("friend_id", from_id);
        bundle.putInt("logged_id", to_id);
        bundle.putString("about", about);
        intent.putExtras(bundle);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Uri defaultSountUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(friend_name)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSountUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
