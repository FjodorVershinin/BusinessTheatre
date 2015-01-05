package ee.vk.businesstheatre.drawer_menu;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.MainActivity;
import ee.vk.businesstheatre.R;
import ee.vk.businesstheatre.fragment.groups.GroupFragment;
import ee.vk.businesstheatre.fragment.messages.MessageFragment;
import ee.vk.businesstheatre.provider.user.Columns;
import ee.vk.businesstheatre.provider.user.UserProvider;

/**
 * Created by fvershinin on 12/31/14.
 */
public class DrawerMenuActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Fragment tempFragment;
    private TextView full_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        full_name = (TextView) findViewById(R.id.nav_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerList.setOnItemClickListener(this);
        final ArrayAdapter arrayAdapter = new DrawerMenuAdapter(this);
        mDrawerList.setAdapter(arrayAdapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                FragmentManager fragmentManager = getFragmentManager();
                if (tempFragment != null)
                    fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.content_frame, tempFragment).commit();
                tempFragment = null;
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        switch (item.getItemId()) {
            case R.id.action_settings:
                ContentResolver.requestSync(AccountManager.get(this).getAccountsByType(Constants.ACCOUNT_TYPE)[0], "ee.vk.businesstheatre", settingsBundle);
                break;
            case R.id.action_logout:
                AccountManager.get(this).removeAccount(AccountManager.get(this).getAccountsByType(Constants.ACCOUNT_TYPE)[0], null, null);
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;

        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1:
                tempFragment = new MessageFragment();
                break;
            case 3:
                tempFragment = new GroupFragment();
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Account account = AccountManager.get(this).getAccountsByType(Constants.ACCOUNT_TYPE)[0];
        String selection = Columns.ID + " = " + AccountManager.get(this).getUserData(account, "SELF_USER");
        return new CursorLoader(this, UserProvider.URI, null, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        full_name.setText(UserProvider.getFirstName(data) + " " + UserProvider.getLastName(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
