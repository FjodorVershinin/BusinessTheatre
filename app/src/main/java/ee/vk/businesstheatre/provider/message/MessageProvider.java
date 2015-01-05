package ee.vk.businesstheatre.provider.message;

import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import ee.vk.businesstheatre.Constants;
import ee.vk.businesstheatre.provider.SQLiteOperation;
import ee.vk.businesstheatre.provider.SQLiteTableProvider;

/**
 * Created by fvershinin on 1/3/15.
 */
public class MessageProvider extends SQLiteTableProvider {
    public static final String TABLE_NAME = "messages";
    public static final Uri URI = Uri.parse("content://ee.vk.businesstheatre/" + TABLE_NAME);

    public MessageProvider() {
        super(TABLE_NAME);
    }

    public static int getBaseId(Cursor c) {
        return c.getInt(c.getColumnIndex(ee.vk.businesstheatre.provider.group.Columns._ID));
    }

    public static int getId(Cursor c) {
        return c.getInt(c.getColumnIndex(ee.vk.businesstheatre.provider.group.Columns.ID));
    }

    public static int getFromUser(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.FROM_USER));
    }

    public static int getToUser(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.TO_USER));
    }

    public static String getSubject(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.SUBJECT));
    }

    public static String getMessage(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.MESSAGE));
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table messages (_id integer primary key autoincrement not null, id integer, fromuser integer, touser integer, subject Text, message Text)");
    }

    @Override
    public void onContentChanged(Context context, int operation, Bundle extras) {
        if (operation == SQLiteOperation.INSERT) {
            extras.keySet();
            final Bundle syncExtras = new Bundle();
            syncExtras.putLong("test", extras.getLong(SQLiteOperation.KEY_LAST_ID, -1));
            ContentResolver.requestSync(AccountManager.get(context).getAccountsByType(Constants.ACCOUNT_TYPE)[0], Constants.ACCOUNT_TYPE, syncExtras);
        }
    }
}
