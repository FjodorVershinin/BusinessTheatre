package ee.vk.businesstheatre;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import ee.vk.businesstheatre.drawer_menu.DrawerMenuActivity;


public class MainActivity extends ActionBarActivity implements AccountManagerCallback<Bundle> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AccountManager am = AccountManager.get(this);
        if (am.getAccountsByType(Constants.ACCOUNT_TYPE).length == 0) {
            am.addAccount(Constants.ACCOUNT_TYPE, Constants.AUTHTOKEN_TYPE, null, null, this, this, null);
        } else {
            if (savedInstanceState == null) {
                am.getAuthToken(am.getAccountsByType(Constants.ACCOUNT_TYPE)[0], Constants.ACCOUNT_TYPE, null, true, this, null);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
        try {
            final Bundle result = future.getResult();
            if (future.getResult().getString(AccountManager.KEY_AUTHTOKEN) == null) {
                final AccountManager am = AccountManager.get(this);
                am.getAuthToken(am.getAccountsByType(Constants.ACCOUNT_TYPE)[0], Constants.ACCOUNT_TYPE, null, true, this, null);
            } else {
                final Intent intent = new Intent(this, DrawerMenuActivity.class);
                intent.putExtra("ee.vk.businesstheatre.MainActivity", result);
                startActivity(intent);
                MainActivity.this.finish();
            }
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            MainActivity.this.finish();
        }
    }

}