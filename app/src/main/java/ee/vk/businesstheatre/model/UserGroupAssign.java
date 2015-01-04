package ee.vk.businesstheatre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/3/15.
 */
public class UserGroupAssign implements Synchronization.Test2 {
    @Expose
    private int baseId;
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("group_id")
    private int groupId;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
