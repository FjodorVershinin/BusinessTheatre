package ee.vk.businesstheatre.provider.user_group_assign;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import ee.vk.businesstheatre.provider.SQLiteTableProvider;

public class UserGroupAssignProvider extends SQLiteTableProvider {
    public static final String TABLE_NAME = "user_group_assigns";
    public static final Uri URI = Uri.parse("content://ee.vk.businesstheatre/" + TABLE_NAME);

    public UserGroupAssignProvider() {
        super(TABLE_NAME);
    }

    public static int getBaseId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns._ID));
    }

    public static int getId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.ID));
    }

    public static int getUserId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.USER_ID));
    }

    public static int getGroupId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.GROUP_ID));
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user_group_assigns (_id integer primary key autoincrement not null, id integer, group_id integer, user_id integer)");
    }
}