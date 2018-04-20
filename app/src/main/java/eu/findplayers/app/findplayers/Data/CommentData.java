package eu.findplayers.app.findplayers.Data;

public class CommentData {

    private String comment, fromImage, timestamp, news_key, comment_key;
    private Integer fromID, loggedID;

    public CommentData(String comment, String fromImage, String timestamp, String news_key, String comment_key, Integer fromID, Integer loggedID) {
        this.comment = comment;
        this.fromImage = fromImage;
        this.timestamp = timestamp;
        this.news_key = news_key;
        this.comment_key = comment_key;
        this.fromID = fromID;
        this.loggedID = loggedID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getFromID() {
        return fromID;
    }

    public void setFromID(Integer fromID) {
        this.fromID = fromID;
    }

    public String getNews_key() {
        return news_key;
    }

    public void setNews_key(String news_key) {
        this.news_key = news_key;
    }

    public String getComment_key() {
        return comment_key;
    }

    public void setComment_key(String comment_key) {
        this.comment_key = comment_key;
    }

    public Integer getLoggedID() {
        return loggedID;
    }

    public void setLoggedID(Integer loggedID) {
        this.loggedID = loggedID;
    }
}
