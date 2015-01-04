package ee.vk.businesstheatre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/3/15.
 */
public class Message implements Synchronization.Test2 {
    @Expose
    private int baseId;
    @SerializedName("id")
    private int id;
    @SerializedName("fromuser")
    private int fromUser;
    @SerializedName("touser")
    private int toUser;
    @SerializedName("subject")
    private String subject;
    @SerializedName("message")
    private String message;

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

    public int getFromUser() {
        return fromUser;
    }

    public void setFromUser(int fromUser) {
        this.fromUser = fromUser;
    }

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
