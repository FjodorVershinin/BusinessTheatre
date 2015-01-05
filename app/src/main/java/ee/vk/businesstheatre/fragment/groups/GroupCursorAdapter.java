package ee.vk.businesstheatre.fragment.groups;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ee.vk.businesstheatre.provider.group.GroupProvider;

/**
 * Created by fvershinin on 1/2/15.
 */
class GroupCursorAdapter extends CursorAdapter {
    public GroupCursorAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_activated_1, null);
        GroupHolder groupHolder = new GroupHolder();
        groupHolder.groupName = (TextView) view.findViewById(android.R.id.text1);
        view.setTag(groupHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GroupHolder groupHolder = (GroupHolder) view.getTag();
        groupHolder.groupName.setText(GroupProvider.getGroupName(cursor));
    }

    private static class GroupHolder {
        private TextView groupName;
    }
}
