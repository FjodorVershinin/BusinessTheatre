package ee.vk.businesstheatre.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;

/**
 * Created by fvershinin on 1/1/15.
 */
public abstract class SQLiteTableProvider {
    private final String name;

    protected SQLiteTableProvider(String name) {
        this.name = name;
    }

    public abstract Uri getBaseUri();

    public Cursor query(SQLiteDatabase db, String[] columns, String where, String[] whereArgs, String orderBy) {
        return db.query(name, columns, where, whereArgs, null, null, orderBy);
    }

    public long insert(SQLiteDatabase db, ContentValues values) {
        return db.insert(name, BaseColumns._ID, values);
    }

    public int delete(SQLiteDatabase db, String where, String[] whereArgs) {
        return db.delete(name, where, whereArgs);
    }

    public int update(SQLiteDatabase db, ContentValues values, String where, String[] whereArgs) {
        return db.update(name, values, where, whereArgs);
    }

    public void onContentChanged(Context context, int operation, Bundle extras) {
    }

    public abstract void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + name + ";");
        onCreate(db);
    }
}
