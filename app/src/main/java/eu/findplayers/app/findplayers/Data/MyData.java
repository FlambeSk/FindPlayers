package eu.findplayers.app.findplayers.Data;

/**
 * Created by DOMA on 26.2.2018.
 */

public class MyData {

    private int id, loggin_id;
    private String name,small_image;

    public MyData(int id, String name, String small_image, int loggin_id) {
        this.id = id;
        this.name = name;
        this.small_image = small_image;
        this.loggin_id = loggin_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoggin_id(){
        return loggin_id;
    }

    public void setLoggin_id(int loggin_id) {
        this.loggin_id = loggin_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall_image() {
        return small_image;
    }

    public void setSmall_image(String small_image) {
        this.small_image = small_image;
    }
}
