package ee.vk.businesstheatre.provider.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import ee.vk.businesstheatre.provider.SQLiteTableProvider;

/**
 * Created by fvershinin on 1/1/15.
 */
public class UserProvider extends SQLiteTableProvider {
    public static final String TABLE_NAME = "users";
    public static final Uri URI = Uri.parse("content://ee.vk.businesstheatre/" + TABLE_NAME);

    public UserProvider() {
        super(TABLE_NAME);
    }

    public static int getBaseId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns._ID));
    }

    public static int getId(Cursor c) {
        return c.getInt(c.getColumnIndex(Columns.ID));
    }

    public static String getFirstName(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.FIRST_NAME));
    }

    public static String getLastName(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.LAST_NAME));
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users (_id integer primary key autoincrement not null, id integer, first_name text, last_name text)");
    }
}
