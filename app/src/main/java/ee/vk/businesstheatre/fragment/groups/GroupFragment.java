package ee.vk.businesstheatre.fragment.groups;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import ee.vk.businesstheatre.R;
import ee.vk.businesstheatre.SendMessageActivity;
import ee.vk.businesstheatre.fragment.ExtendedListFragment;
import ee.vk.businesstheatre.provider.group.Columns;
import ee.vk.businesstheatre.provider.group.GroupProvider;

/**
 * Created by fvershinin on 1/2/15.
 */
public class GroupFragment extends ExtendedListFragment implements AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private GroupCursorAdapter groupCursorAdapter;
    private View mProgressContainer;
    private int[] list = new int[10];

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        groupCursorAdapter = new GroupCursorAdapter(getActivity().getBaseContext());
        getListView().setAdapter(groupCursorAdapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_content, container, false);
        mProgressContainer = view.findViewById(R.id.progressContainer);
        return view;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        final Cursor cursor = getActivity().getContentResolver().query(GroupProvider.URI, null, Columns._ID + "=" + id, null, null);
        int real_id = -1;
        if (cursor.moveToFirst()) {
            real_id = GroupProvider.getId(cursor);
        }
        if (checked) {
            list[position] = real_id;
        } else {
            list[position] = -1;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_send_message_to) {
            final Intent intent = new Intent(getActivity(), SendMessageActivity.class);
            intent.putExtra("GROUPS_ID", list);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), 0, 0);
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        list = null;
        list = new int[10];
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getApplicationContext(), GroupProvider.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        groupCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        groupCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void Test(Boolean x) {
        if (x)
            mProgressContainer.setVisibility(View.VISIBLE);
        else
            mProgressContainer.setVisibility(View.INVISIBLE);
    }
}
