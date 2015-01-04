package ee.vk.businesstheatre.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by fvershinin on 12/31/14.
 */
public class VolleyRequest {
    private static final VolleyRequest volleyRequest = null;
    private final RequestQueue requestQueue;

    private VolleyRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static VolleyRequest getInstance(Context context) {
        if (volleyRequest == null)
            return new VolleyRequest(context);
        return volleyRequest;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }
}
