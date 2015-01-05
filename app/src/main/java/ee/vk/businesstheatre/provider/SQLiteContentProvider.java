package ee.vk.businesstheatre.provider;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ee.vk.businesstheatre.provider.group.GroupProvider;
import ee.vk.businesstheatre.provider.message.MessageProvider;
import ee.vk.businesstheatre.provider.user.UserProvider;
import ee.vk.businesstheatre.provider.user_group_assign.UserGroupAssignProvider;

/**
 * Created by fvershinin on 1/1/15.
 */
public class SQLiteContentProvider extends ContentProvider {
    private static final String DATABASE_NAME = "etapp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MIME_DIR = "vnd.android.cursor.dir/";
    private static final String MIME_ITEM = "vnd.android.cursor.item/";

    private static final Map<String, SQLiteTableProvider> SCHEMA = new ConcurrentHashMap<>();

    static {
        SCHEMA.put(UserProvider.TABLE_NAME, new UserProvider());
        SCHEMA.put(GroupProvider.TABLE_NAME, new GroupProvider());
        SCHEMA.put(MessageProvider.TABLE_NAME, new MessageProvider());
        SCHEMA.put(UserGroupAssignProvider.TABLE_NAME, new UserGroupAssignProvider());
    }

    private final SQLiteUriMatcher mUriMatcher = new SQLiteUriMatcher();

    private SQLiteOpenHelper dbHelper;

    private static ProviderInfo getProviderInfo(Context context, Class<? extends ContentProvider> provider)
            throws PackageManager.NameNotFoundException {
        return context.getPackageManager()
                .getProviderInfo(new ComponentName(context.getPackageName(), provider.getName()), 0);
    }

    private static String getTableName(Uri uri) {
        return uri.getPathSegments().get(0);
    }


    @Override
    public boolean onCreate() {
        try {
            final ProviderInfo pi = getProviderInfo(getContext(), getClass());
            final String[] authorities = TextUtils.split(pi.authority, ";");
            for (final String authority : authorities) {
                mUriMatcher.addAuthority(authority);
            }
            dbHelper = new SQLiteOpenHelperImpl(getContext());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            throw new SQLiteException(e.getMessage());
        }
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        final Cursor cursor = tableProvider.query(dbHelper.getReadableDatabase(), columns, where, whereArgs, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        } else if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            return MIME_ITEM + getTableName(uri);
        }
        return MIME_DIR + getTableName(uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            final int affectedRows = updateInternal(
                    tableProvider.getBaseUri(), tableProvider,
                    values, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()}
            );
            if (affectedRows > 0) {
                return uri;
            }
        }
        final long lastId = tableProvider.insert(dbHelper.getWritableDatabase(), values);
        getContext().getContentResolver().notifyChange(tableProvider.getBaseUri(), null, false);
        final Bundle extras = new Bundle();
        extras.putLong(SQLiteOperation.KEY_LAST_ID, lastId);
        tableProvider.onContentChanged(getContext(), SQLiteOperation.INSERT, extras);
        return uri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        final int affectedRows = tableProvider.delete(dbHelper.getWritableDatabase(), where, whereArgs);
        getContext().getContentResolver().notifyChange(tableProvider.getBaseUri(), null, false);
        if (affectedRows >= 0) {
            final Bundle extras = new Bundle();
            extras.putLong(SQLiteOperation.KEY_AFFECTED_ROWS, affectedRows);
            tableProvider.onContentChanged(getContext(), SQLiteOperation.DELETE, extras);
        }
        return affectedRows;
    }


    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        return updateInternal(tableProvider.getBaseUri(), tableProvider, values, where, whereArgs);
    }

    private int updateInternal(Uri uri, SQLiteTableProvider provider,
                               ContentValues values, String where, String[] whereArgs) {
        final int affectedRows = provider.update(dbHelper.getWritableDatabase(), values, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null, false);
            final Bundle extras = new Bundle();
            extras.putLong(SQLiteOperation.KEY_AFFECTED_ROWS, affectedRows);
            provider.onContentChanged(getContext(), SQLiteOperation.UPDATE, extras);
        }
        return affectedRows;
    }

    private static final class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

        public SQLiteOpenHelperImpl(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransactionNonExclusive();
            try {
                for (final SQLiteTableProvider table : SCHEMA.values()) {
                    table.onCreate(db);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.beginTransactionNonExclusive();
            try {
                for (final SQLiteTableProvider table : SCHEMA.values()) {
                    table.onUpgrade(db);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

    }
}