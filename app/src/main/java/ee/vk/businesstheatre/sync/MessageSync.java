package ee.vk.businesstheatre.sync;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import ee.vk.businesstheatre.model.Message;
import ee.vk.businesstheatre.provider.message.Columns;
import ee.vk.businesstheatre.provider.message.MessageProvider;
import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/4/15.
 */
public class MessageSync extends Synchronization<Message> {
    private static final String NAME = "messages";
    private final ContentProviderClient providerClient;


    public MessageSync(Context context, ContentProviderClient providerClient, SyncResult syncResult, Map<String, String> params) {
        super(context, syncResult, params, NAME);
        this.providerClient = providerClient;
    }

    public void sync() {
        getRequest();
    }

    public void insertToServer(long id) {
        try {
            insetToServer(providerClient.query(MessageProvider.URI, null, "_id = " + id, null, null), (int) id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Cursor setCursor() throws RemoteException {
        return providerClient.query(MessageProvider.URI, null, null, null, null);
    }

    @Override
    protected Map<String, Message> jsonToMap(JSONArray response) {
        Map<String, Message> messageMap = new HashMap<>();
        Message[] messages = new Gson().fromJson(String.valueOf(response), Message[].class);
        for (Message message : messages) {
            messageMap.put(String.valueOf(message.getId()), message);
        }
        return messageMap;
    }

    @Override
    protected Message providerToModel(Cursor cursor) {
        final Message message = new Message();
        message.setBaseId(MessageProvider.getBaseId(cursor));
        message.setId(MessageProvider.getId(cursor));
        message.setFromUser(MessageProvider.getFromUser(cursor));
        message.setToUser(MessageProvider.getToUser(cursor));
        message.setSubject(MessageProvider.getSubject(cursor));
        message.setMessage(MessageProvider.getMessage(cursor));
        return message;
    }

    @Override
    protected ContentValues getContentValues(Map.Entry<String, Message> entry) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID, entry.getValue().getId());
        contentValues.put(Columns.FROM_USER, entry.getValue().getFromUser());
        contentValues.put(Columns.TO_USER, entry.getValue().getToUser());
        contentValues.put(Columns.SUBJECT, entry.getValue().getSubject());
        contentValues.put(Columns.MESSAGE, entry.getValue().getMessage());
        return contentValues;
    }

    @Override
    protected void update(String id, ContentValues contentValues) throws RemoteException {
        providerClient.update(Uri.withAppendedPath(MessageProvider.URI, id), contentValues, null, null);

    }

    @Override
    protected void insert(ContentValues contentValues) throws RemoteException {
        providerClient.insert(MessageProvider.URI, contentValues);
    }

    @Override
    protected void remove(String id) throws RemoteException {
        providerClient.delete(Uri.withAppendedPath(MessageProvider.URI, id), null, null);
    }

    @Override
    protected Map<String, String> modelToMap(Message message) {
        Map<String, String> temp = new HashMap<>();
        temp.put("id", String.valueOf(message.getId()));
        temp.put("fromuser", String.valueOf(message.getFromUser()));
        temp.put("touser", String.valueOf(message.getToUser()));
        temp.put("subject", String.valueOf(message.getSubject()));
        temp.put("message", String.valueOf(message.getMessage()));
        temp.put("send", "0");
        temp.put("read", "0");
        return temp;
    }
}
