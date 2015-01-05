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

import ee.vk.businesstheatre.model.UserGroupAssign;
import ee.vk.businesstheatre.provider.user_group_assign.Columns;
import ee.vk.businesstheatre.provider.user_group_assign.UserGroupAssignProvider;
import ee.vk.businesstheatre.utils.Synchronization;

/**
 * Created by fvershinin on 1/4/15.
 */
public class UGASync extends Synchronization<UserGroupAssign> {
    private static final String NAME = "user-group-assign";
    private final ContentProviderClient providerClient;


    public UGASync(Context context, ContentProviderClient providerClient, SyncResult syncResult, Map<String, String> params) {
        super(context, syncResult, params, NAME);
        this.providerClient = providerClient;
    }

    public void sync() {
        getRequest();
    }

    @Override
    protected Cursor setCursor() throws RemoteException {
        return providerClient.query(UserGroupAssignProvider.URI, null, null, null, null);
    }

    @Override
    protected Map<String, UserGroupAssign> jsonToMap(JSONArray response) {
        Map<String, UserGroupAssign> userGroupAssignMap = new HashMap<>();
        UserGroupAssign[] userGroupAssigns = new Gson().fromJson(String.valueOf(response), UserGroupAssign[].class);
        for (UserGroupAssign userGroupAssign : userGroupAssigns) {
            userGroupAssignMap.put(String.valueOf(userGroupAssign.getId()), userGroupAssign);
        }
        return userGroupAssignMap;
    }

    @Override
    protected UserGroupAssign providerToModel(Cursor cursor) {
        final UserGroupAssign userGroupAssign = new UserGroupAssign();
        userGroupAssign.setBaseId(UserGroupAssignProvider.getBaseId(cursor));
        userGroupAssign.setId(UserGroupAssignProvider.getId(cursor));
        userGroupAssign.setUserId(UserGroupAssignProvider.getUserId(cursor));
        userGroupAssign.setGroupId(UserGroupAssignProvider.getGroupId(cursor));
        return userGroupAssign;
    }

    @Override
    protected ContentValues getContentValues(Map.Entry<String, UserGroupAssign> entry) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID, entry.getValue().getId());
        contentValues.put(Columns.USER_ID, entry.getValue().getUserId());
        contentValues.put(Columns.GROUP_ID, entry.getValue().getGroupId());
        return contentValues;
    }

    @Override
    protected void update(String id, ContentValues contentValues) throws RemoteException {
        providerClient.update(Uri.withAppendedPath(UserGroupAssignProvider.URI, id), contentValues, null, null);
    }

    @Override
    protected void insert(ContentValues contentValues) throws RemoteException {
        providerClient.insert(UserGroupAssignProvider.URI, contentValues);
    }

    @Override
    protected void remove(String id) throws RemoteException {
        providerClient.delete(Uri.withAppendedPath(UserGroupAssignProvider.URI, id), null, null);
    }
}
