package ee.vk.businesstheatre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/1/15.
 */
public class User implements Synchronization.Test2 {
    @Expose
    private int baseId;
    @SerializedName("id")
    private int id;
    @SerializedName("first_name")
    private String user_name;
    @SerializedName("last_name")
    private String last_name;

    public int getBaseId() {
        return baseId;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
