package eu.findplayers.app.findplayers.Data;

import java.util.Map;

/**
 * Created by DOMA on 28.2.2018.
 */

public class MessageData {

    private String message, friendName,isRead, message_key, timestamp;
    private boolean myMessage;
    private int from_id, to_id;

    public MessageData(String message, String friendName, String isRead, String message_key, String timestamp, boolean myMessage, int from_id, int to_id) {
        this.message = message;
        this.friendName = friendName;
        this.isRead = isRead;
        this.message_key = message_key;
        this.timestamp = timestamp;
        this.myMessage = myMessage;
        this.from_id = from_id;
        this.to_id = to_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getMessage_key() {
        return message_key;
    }

    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMyMessage() {
        return myMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.myMessage = myMessage;
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
}
