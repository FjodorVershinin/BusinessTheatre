package ee.vk.businesstheatre.fragment.messages;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ee.vk.businesstheatre.R;
import ee.vk.businesstheatre.provider.message.MessageProvider;
import ee.vk.businesstheatre.provider.user.Columns;
import ee.vk.businesstheatre.provider.user.UserProvider;

/**
 * Created by fvershinin on 1/3/15.
 */
class MessageCursorAdapter extends CursorAdapter {

    public MessageCursorAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item, null);
        final MessageViewHolder messageViewHolder = new MessageViewHolder();
        messageViewHolder.subject = (TextView) view.findViewById(R.id.subject);
        messageViewHolder.fromUserName = (TextView) view.findViewById(R.id.fromUserName);
        messageViewHolder.message = (TextView) view.findViewById(R.id.message);
        view.setTag(messageViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Cursor cursorUser = context.getContentResolver().query(UserProvider.URI, null, Columns.ID + "=" + MessageProvider.getFromUser(cursor), null, null);
        cursorUser.moveToFirst();
        final MessageViewHolder messageViewHolder = (MessageViewHolder) view.getTag();
        messageViewHolder.subject.setText(MessageProvider.getSubject(cursor));
        messageViewHolder.fromUserName.setText(String.valueOf(UserProvider.getFirstName(cursorUser) + " " + UserProvider.getLastName(cursorUser)));
        messageViewHolder.message.setText(String.valueOf(MessageProvider.getMessage(cursor)));
        cursorUser.close();
    }

    private static class MessageViewHolder {
        private TextView subject;
        private TextView message;
        private TextView fromUserName;
    }
}
