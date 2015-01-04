package ee.vk.businesstheatre.fragment.messages;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.R;
import ee.vk.businesstheatre.fragment.ExtendedListFragment;
import ee.vk.businesstheatre.provider.message.Columns;
import ee.vk.businesstheatre.provider.message.MessageProvider;

/**
 * Created by fvershinin on 1/3/15.
 */
public class MessageFragment extends ExtendedListFragment implements AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private MessageCursorAdapter messageCursorAdapter;
    private View mProgressContainer;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageCursorAdapter = new MessageCursorAdapter(getActivity().getBaseContext());
        getListView().setAdapter(messageCursorAdapter);
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

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Account account = AccountManager.get(getActivity()).getAccountsByType(Constants.ACCOUNT_TYPE)[0];
        final String self_user = AccountManager.get(getActivity()).getUserData(account, "SELF_USER");
        final String selection = String.format("%s = %s", Columns.TO_USER, self_user);
        return new CursorLoader(getActivity().getApplicationContext(), MessageProvider.URI, null, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        messageCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        messageCursorAdapter.swapCursor(null);
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
