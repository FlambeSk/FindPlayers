package eu.findplayers.app.findplayers.Data;

public class NewsData {

    String key, fromName, fromImage, message, type, timestamp, image;
    Integer fromID;

    public NewsData(String key, String fromName, String fromImage, String message, String type, String timestamp, String image, Integer fromID) {
        this.key = key;
        this.fromName = fromName;
        this.fromImage = fromImage;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.image = image;
        this.fromID = fromID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getFromID() {
        return fromID;
    }

    public void setFromID(Integer fromID) {
        this.fromID = fromID;
    }
}
