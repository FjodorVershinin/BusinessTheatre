package ee.vk.businesstheatre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/2/15.
 */
public class Group implements Synchronization.Test2 {
    @Expose
    private int baseId;
    @SerializedName("id")
    private int id;
    @SerializedName("group_name")
    private String groupName;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
