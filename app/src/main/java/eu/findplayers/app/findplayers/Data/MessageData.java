package eu.findplayers.app.findplayers.Data;

import java.util.Map;

/**
 * Created by DOMA on 28.2.2018.
 */

public class MessageData {

    private String message, friendName;
    private boolean myMessage;
    private int from_id, to_id, order;
    public Map timestamp;

    public MessageData(String message, String friendName, boolean myMessage, int from_id, int to_id, Map timestamp, int order) {
        this.message = message;
        this.friendName = friendName;
        this.myMessage = myMessage;
        this.from_id = from_id;
        this.to_id = to_id;
        this.timestamp = timestamp;
        this.order = order;
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

    public Map getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map timestamp) {
        this.timestamp = timestamp;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
