package ee.vk.businesstheatre;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import ee.vk.businesstheatre.provider.message.Columns;
import ee.vk.businesstheatre.provider.message.MessageProvider;
import ee.vk.businesstheatre.provider.user_group_assign.UserGroupAssignProvider;

/**
 * Created by fvershinin on 1/3/15.
 */
public class SendMessageActivity extends ActionBarActivity implements Toolbar.OnMenuItemClickListener {
    private EditText message;
    private EditText subject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        setSupportActionBar(toolbar);
        final ActionBar abs = this.getSupportActionBar();
        abs.setDisplayHomeAsUpEnabled(true);
        abs.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_send_message) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send_message:
                int[] x = getIntent().getIntArrayExtra("GROUPS_ID");
                final Account account = AccountManager.get(this).getAccountsByType(Constants.ACCOUNT_TYPE)[0];
                final String self_user = AccountManager.get(this).getUserData(account, "SELF_USER");
                for (Integer integer : x) {
                    if (integer > 0) {
                        final String selection = String.format("%s = %s", ee.vk.businesstheatre.provider.user_group_assign.Columns.GROUP_ID, integer);
                        Cursor userGroupAssignCursor = getContentResolver().query(UserGroupAssignProvider.URI, null, selection, null, null);
                        if (userGroupAssignCursor.moveToFirst()) {
                            do {
                                final ContentValues values = new ContentValues();
                                values.put(Columns.FROM_USER, self_user);
                                values.put(Columns.TO_USER, UserGroupAssignProvider.getUserId(userGroupAssignCursor));
                                values.put(Columns.SUBJECT, subject.getText().toString());
                                values.put(Columns.MESSAGE, message.getText().toString());
                                getContentResolver().insert(MessageProvider.URI, values);
                            }
                            while (userGroupAssignCursor.moveToNext());
                        }
                        userGroupAssignCursor.close();
                    }
                }
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}