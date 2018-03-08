package eu.findplayers.app.findplayers.Data;

/**
 * Created by DOMA on 27.2.2018.
 */

public class FriendsData {

    private String profile_image, username, message;
    private int friend_id, logged_id;

    public FriendsData(String profile_image, String username, String message, int friend_id, int logged_id) {
        this.profile_image = profile_image;
        this.username = username;
        this.message = message;
        this.friend_id = friend_id;
        this.logged_id = logged_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public int getLogged_id() {
        return logged_id;
    }

    public void setLogged_id(int logged_id) {
        this.logged_id = logged_id;
    }
}
