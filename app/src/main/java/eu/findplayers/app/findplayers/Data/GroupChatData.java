package eu.findplayers.app.findplayers.Data;

/**
 * Created by DOMA on 27.3.2018.
 */

public class GroupChatData {

    private String message, notification_data, about, fromName, fromImage, timestamp, tournamentName;
    private int from_id,  to_tournament_id;
    boolean isMyMessage;

    public GroupChatData(String message, String notification_data, String about, String fromName, String fromImage, String timestamp, String tournamentName, int from_id, int to_tournament_id, boolean isMyMessage) {
        this.message = message;
        this.notification_data = notification_data;
        this.about = about;
        this.fromName = fromName;
        this.fromImage = fromImage;
        this.timestamp = timestamp;
        this.tournamentName = tournamentName;
        this.from_id = from_id;
        this.to_tournament_id = to_tournament_id;
        this.isMyMessage = isMyMessage;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotification_data() {
        return notification_data;
    }

    public void setNotification_data(String notification_data) {
        this.notification_data = notification_data;
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

    public String getFromImage() {
        return fromImage;
    }

    public void setFromImage(String fromImage) {
        this.fromImage = fromImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_tournament_id() {
        return to_tournament_id;
    }

    public void setTo_tournament_id(int to_tournament_id) {
        this.to_tournament_id = to_tournament_id;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        isMyMessage = myMessage;
    }
}
