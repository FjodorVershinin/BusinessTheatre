package ee.vk.businesstheatre.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;

import ee.vk.businesstheatre.Constants;

/**
 * Created by fvershinin on 1/3/15.
 */
public abstract class ExtendedListFragment extends ListFragment {
    private SyncStatusObserver syncStatusObserver;
    private Object syncObserverHandle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncStatusObserver = new SyncStatusObserver() {
            @Override
            public void onStatusChanged(int which) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Account account = AccountManager.get(getActivity()).getAccountsByType(Constants.ACCOUNT_TYPE)[0];
                        boolean sync_active = ContentResolver.isSyncActive(account, "ee.vk.businesstheatre");
                        boolean sync_pending = ContentResolver.isSyncPending(account, "ee.vk.businesstheatre");
                        Test(sync_active || sync_pending);
                    }
                });
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        syncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        syncObserverHandle = ContentResolver.addStatusChangeListener(mask, syncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (syncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(syncObserverHandle);
            syncObserverHandle = null;
        }
    }

    protected abstract void Test(Boolean x);
}
