package ee.vk.businesstheatre.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.R;
import ee.vk.businesstheatre.utils.AuthTokenLoader;

/**
 * Created by fvershinin on 12/31/14.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    private static final String TAG = "AuthenticatorActivity";
    private final BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finishLogin();
        }
    };
    private String mUsername;
    private String mPassword;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private boolean mRequestNewAccount = false;
    private UserLoginTask mAuthTask = null;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i(TAG, "onCreate(" + icicle + ")");

        Log.i(TAG, "loading data from Intent");
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = mUsername == null;

        Log.i(TAG, "    request new: " + mRequestNewAccount);
        setContentView(R.layout.activity_login);
        final Button mLoginButton = (Button) findViewById(R.id.sign_in_button);
        mUsernameEdit = (EditText) findViewById(R.id.login);
        mPasswordEdit = (EditText) findViewById(R.id.password);
        if (!TextUtils.isEmpty(mUsername)) mUsernameEdit.setText(mUsername);

        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(syncFinishedReceiver, new IntentFilter("SYNC_FINISHED"));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(syncFinishedReceiver);
    }

    @Override
    public void onClick(View v) {
        if (mRequestNewAccount) {
            mUsername = mUsernameEdit.getText().toString();
        }
        mPassword = mPasswordEdit.getText().toString();
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "Login or password are empty!", Toast.LENGTH_SHORT).show();
        } else {
            mAuthTask = new UserLoginTask();
            progressDialog = ProgressDialog.show(AuthenticatorActivity.this, "", "Обновление", true);
            progressDialog.show();
            mAuthTask.execute();
        }
    }

    private void finishLogin() {
        Log.i(TAG, "finishLogin()");

        progressDialog.dismiss();
        finish();
    }

    void onAuthenticationResult(String authToken) {
        final AccountManager accountManager = AccountManager.get(this);
        final Bundle result = new Bundle();
        final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, mPassword, Bundle.EMPTY)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            accountManager.setAuthToken(account, account.type, authToken);
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Account already exists!");
        }
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        mAuthTask = null;
        ContentResolver.setSyncAutomatically(account, Constants.AUTHORITY, true);
    }

    void onAuthCancel() {
        mAuthTask = null;
        progressDialog.hide();
    }

    private class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            try {
                return AuthTokenLoader.authenticate(getApplicationContext(), mUsername, mPassword);
            } catch (Exception ex) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String authToken) {
            if (!TextUtils.isEmpty(authToken))
                onAuthenticationResult(authToken);
            else
                onCancelled();
        }

        @Override
        protected void onCancelled() {
            onAuthCancel();
        }
    }
}