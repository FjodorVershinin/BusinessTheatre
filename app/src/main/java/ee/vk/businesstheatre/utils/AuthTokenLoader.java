package ee.vk.businesstheatre.utils;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.authentication.AuthenticatorActivity;

/**
 * Created by fvershinin on 12/31/14.
 */
public class AuthTokenLoader {
    public static String authenticate(Context context, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(AuthenticatorActivity.PARAM_USERNAME, username);
        params.put(AuthenticatorActivity.PARAM_PASSWORD, password);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Constants.URL + "token/", new JSONObject(params), future, future);
        VolleyRequest.getInstance(context).addToRequestQueue(request);

        try {
            JSONObject response = future.get();
            return response.getString("token");
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
