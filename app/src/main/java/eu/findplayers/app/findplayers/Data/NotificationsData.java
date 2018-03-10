package eu.findplayers.app.findplayers.Data;

import java.util.Map;

/**
 * Created by DOMA on 6.3.2018.
 */

public class NotificationsData {

    private int from_id, to_id;
    private String about, fromName, notification_data, image, timestamp;
    private boolean isReaded;

    public NotificationsData(int from_id, int to_id, String about, String fromName, String notification_data,String image, String timestamp, boolean isReaded) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.about = about;
        this.fromName = fromName;
        this.notification_data = notification_data;
        this.image = image;
        this.timestamp = timestamp;
        this.isReaded = isReaded;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getNotification_data() {
        return notification_data;
    }

    public void setNotification_data(String notification_data) {
        this.notification_data = notification_data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }
}
