package eu.findplayers.app.findplayers.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import eu.findplayers.app.findplayers.R;

public class NotificationHelper extends ContextWrapper {

    private static final String EDMT_CHANNEL_ID = "eu.findplayers.app.findplayers.EDMTDEV";
    private static final String EDMT_CHANNEL_NAME = "EDMTDEV Channel";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        createChanels();
    }

    private void createChanels()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel edmtChanel = new NotificationChannel(EDMT_CHANNEL_ID, EDMT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            edmtChanel.enableLights(true);
            edmtChanel.enableVibration(true);
            edmtChanel.setLightColor(Color.GREEN);
            edmtChanel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(edmtChanel);
        }
    }

    public NotificationManager getManager()
    {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public Notification.Builder getEDMTChannelNotification(String title,String body)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            return new Notification.Builder(getApplicationContext(),EDMT_CHANNEL_ID)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher_fp)
                    .setAutoCancel(true);
        } else
        {
            return new Notification.Builder(getApplicationContext());
        }

    }
}
