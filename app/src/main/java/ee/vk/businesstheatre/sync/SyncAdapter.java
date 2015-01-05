package ee.vk.businesstheatre.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.utils.VolleyRequest;

/**
 * Created by fvershinin on 1/1/15.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter implements Response.Listener<JSONObject>, Response.ErrorListener {
    private final Map<String, String> headers;
    private UserSync userSync;
    private GroupSync groupSync;
    private MessageSync messageSync;
    private UGASync mUGASync;

    public SyncAdapter(Context context) {
        super(context, true);
        headers = new HashMap<>();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final long test = extras.getLong("test", -1);
        headers.put("Authorization", " token " + AccountManager.get(getContext()).peekAuthToken(account, Constants.ACCOUNT_TYPE));
        userSync = new UserSync(getContext(), provider, syncResult, headers);
        groupSync = new GroupSync(getContext(), provider, syncResult, headers);
        messageSync = new MessageSync(getContext(), provider, syncResult, headers);
        mUGASync = new UGASync(getContext(), provider, syncResult, headers);

        if (test > 0) {
            messageSync.insertToServer(test);
        } else {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.URL + "self/", null, this, this) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return headers;
                }
            };
            VolleyRequest.getInstance(getContext()).addToRequestQueue(request);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            final Account account = AccountManager.get(getContext()).getAccountsByType(Constants.ACCOUNT_TYPE)[0];
            AccountManager.get(getContext()).setUserData(account, "SELF_USER", response.getString("id"));
            userSync.sync();
            groupSync.sync();
            messageSync.sync();
            mUGASync.sync();
            final Intent i = new Intent("SYNC_FINISHED");
            getContext().sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
