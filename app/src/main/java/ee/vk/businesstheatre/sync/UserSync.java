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

import ee.vk.businesstheatre.model.User;
import ee.vk.businesstheatre.provider.user.Columns;
import ee.vk.businesstheatre.provider.user.UserProvider;
import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/4/15.
 */
public class UserSync extends Synchronization<User> {
    private static final String NAME = "users";
    private final ContentProviderClient providerClient;

    public UserSync(Context context, ContentProviderClient providerClient, SyncResult syncResult, Map<String, String> params) {
        super(context, syncResult, params, NAME);
        this.providerClient = providerClient;
    }

    public void sync() {
        getRequest();
    }

    @Override
    protected Cursor setCursor() throws RemoteException {
        return providerClient.query(UserProvider.URI, null, null, null, null);
    }

    @Override
    protected Map<String, User> jsonToMap(JSONArray response) {
        Map<String, User> userMap = new HashMap<>();
        User[] users = new Gson().fromJson(String.valueOf(String.valueOf(response)), User[].class);
        for (User user : users) {
            userMap.put(String.valueOf(user.getId()), user);
        }
        return userMap;
    }

    @Override
    protected User providerToModel(Cursor cursor) {
        final User user = new User();
        user.setBaseId(UserProvider.getBaseId(cursor));
        user.setId(UserProvider.getId(cursor));
        user.setUser_name(UserProvider.getFirstName(cursor));
        user.setLast_name(UserProvider.getLastName(cursor));
        return user;
    }

    @Override
    protected ContentValues getContentValues(Map.Entry<String, User> entry) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID, entry.getValue().getId());
        contentValues.put(Columns.FIRST_NAME, entry.getValue().getUser_name());
        contentValues.put(Columns.LAST_NAME, entry.getValue().getLast_name());
        return contentValues;
    }

    @Override
    protected void update(String id, ContentValues contentValues) throws RemoteException {
        providerClient.update(Uri.withAppendedPath(UserProvider.URI, id), contentValues, null, null);
    }

    @Override
    protected void insert(ContentValues contentValues) throws RemoteException {
        providerClient.insert(UserProvider.URI, contentValues);
    }

    @Override
    protected void remove(String id) throws RemoteException {
        providerClient.delete(Uri.withAppendedPath(UserProvider.URI, id), null, null);
    }
}
