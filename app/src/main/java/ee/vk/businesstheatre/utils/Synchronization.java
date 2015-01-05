package ee.vk.businesstheatre.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.RemoteException;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ee.vk.businesstheatre.Constants;

/**
 * Created by fvershinin on 1/1/15.
 */
public abstract class Synchronization<M extends Synchronization.Test2> implements Response.ErrorListener, Response.Listener<JSONObject> {
    private final Context context;
    private final SyncResult syncResult;
    private final String name;
    private final Map<String, String> headers;

    protected Synchronization(Context context, SyncResult syncResult, Map<String, String> headers, String name) {
        this.context = context;
        this.syncResult = syncResult;
        this.headers = headers;
        this.name = name;
    }

    protected abstract Cursor setCursor() throws RemoteException;

    protected abstract Map<String, M> jsonToMap(JSONArray response);

    protected abstract M providerToModel(Cursor cursor);

    protected abstract ContentValues getContentValues(Map.Entry<String, M> entry);

    protected abstract void update(String id, ContentValues contentValues) throws RemoteException;

    protected abstract void insert(ContentValues contentValues) throws RemoteException;

    protected abstract void remove(String id) throws RemoteException;

    protected Map<String, String> modelToMap(M model) {
        return null;
    }

    protected void insetToServer(Cursor cursor, int id) {
        if (cursor.moveToFirst()) {
            final Map<String, String> params = modelToMap(providerToModel(cursor));
            if (params.get("id").equals("0")) {
                params.remove("id");
                postRequest(params, id);
            }
        }
    }

    private void sync(Map<String, M> jsonMap, Map<String, M> providerMap) throws RemoteException {
        for (Map.Entry<String, M> entry : jsonMap.entrySet()) {
            final ContentValues contentValues = getContentValues(entry);
            if (providerMap.containsKey(entry.getKey())) {
                update(String.valueOf(providerMap.get(entry.getKey()).getBaseId()), contentValues);
                providerMap.remove(entry.getKey());
                syncResult.stats.numUpdates++;
            } else {
                insert(contentValues);
                providerMap.remove(entry.getKey());
                syncResult.stats.numInserts++;
            }
        }
        for (Map.Entry<String, M> entry : providerMap.entrySet()) {
            remove(String.valueOf(entry.getValue().getBaseId()));
            syncResult.stats.numDeletes++;
        }
    }

    private Map<String, M> providerToMap(Cursor cursor) {
        final Map<String, M> temp = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                final M model = providerToModel(cursor);
                temp.put(String.valueOf(model.getId()), model);
            }
            while (cursor.moveToNext());
        }
        return temp;
    }

    void postRequest(Map<String, String> params, final int id) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.URL + name + "/", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final JSONArray jsonArray = new JSONArray();
                    jsonArray.put(response);
                    for (Map.Entry<String, M> entry : jsonToMap(jsonArray).entrySet()) {
                        update(String.valueOf(id), getContentValues(entry));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        VolleyRequest.getInstance(context).addToRequestQueue(request);
    }

    protected void getRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.URL + name + "/", null, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        VolleyRequest.getInstance(context).addToRequestQueue(request);
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            sync(jsonToMap(response.getJSONArray("results")), providerToMap(setCursor()));
        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public interface Test2 {
        public int getBaseId();

        public int getId();
    }
}
