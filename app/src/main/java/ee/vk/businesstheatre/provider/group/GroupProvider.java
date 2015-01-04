package ee.vk.businesstheatre.provider.group;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import ee.vk.businesstheatre.provider.SQLiteTableProvider;

/**
 * Created by fvershinin on 1/2/15.
 */
public class GroupProvider extends SQLiteTableProvider {
    public static final String TABLE_NAME = "groups";
    public static final Uri URI = Uri.parse("content://ee.vk.businesstheatre/" + TABLE_NAME);

    public GroupProvider() {
        super(TABLE_NAME);
    }

    public static int getBaseId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns._ID));
    }

    public static int getId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.ID));
    }

    public static String getGroupName(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.GROUP_NAME));
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table groups (_id integer primary key autoincrement not null, id integer, group_name text)");
    }
}
