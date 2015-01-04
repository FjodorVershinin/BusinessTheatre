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

import ee.vk.businesstheatre.model.Group;
import ee.vk.businesstheatre.provider.group.Columns;
import ee.vk.businesstheatre.provider.group.GroupProvider;
import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/4/15.
 */
public class GroupSync extends Synchronization<Group> {
    private static final String NAME = "groups";
    private final ContentProviderClient providerClient;


    public GroupSync(Context context, ContentProviderClient providerClient, SyncResult syncResult, Map<String, String> params) {
        super(context, syncResult, params, NAME);
        this.providerClient = providerClient;
    }

    public void sync() {
        getRequest();
    }

    @Override
    protected Cursor setCursor() throws RemoteException {
        return providerClient.query(GroupProvider.URI, null, null, null, null);
    }

    @Override
    protected Map<String, Group> jsonToMap(JSONArray response) {
        final Map<String, Group> groupMap = new HashMap<>();
        final Group[] groups = new Gson().fromJson(String.valueOf(response), Group[].class);
        for (Group group : groups) {
            groupMap.put(String.valueOf(group.getId()), group);
        }
        return groupMap;
    }

    @Override
    protected Group providerToModel(Cursor cursor) {
        final Group group = new Group();
        group.setBaseId(GroupProvider.getBaseId(cursor));
        group.setId(GroupProvider.getId(cursor));
        group.setGroupName(GroupProvider.getGroupName(cursor));
        return group;
    }

    @Override
    protected ContentValues getContentValues(Map.Entry<String, Group> entry) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID, entry.getValue().getId());
        contentValues.put(Columns.GROUP_NAME, entry.getValue().getGroupName());
        return contentValues;
    }

    @Override
    protected void update(String id, ContentValues contentValues) throws RemoteException {
        providerClient.update(Uri.withAppendedPath(GroupProvider.URI, id), contentValues, null, null);
    }

    @Override
    protected void insert(ContentValues contentValues) throws RemoteException {
        providerClient.insert(GroupProvider.URI, contentValues);
    }

    @Override
    protected void remove(String id) throws RemoteException {
        providerClient.delete(Uri.withAppendedPath(GroupProvider.URI, id), null, null);
    }
}
