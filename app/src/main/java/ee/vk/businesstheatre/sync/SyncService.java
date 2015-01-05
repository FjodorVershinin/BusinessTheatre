package ee.vk.businesstheatre.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by fvershinin on 1/1/15.
 */
public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private SyncAdapter mAuthenticator;

    @Override
    public void onCreate() {
        if (!Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Sync Service started.");
        }
        mAuthenticator = new SyncAdapter(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        if (!Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Sync Service stopped.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (!Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getBinder()...  returning the Sync Adapter binder for intent "
                    + intent);
        }
        return mAuthenticator.getSyncAdapterBinder();
    }
}
