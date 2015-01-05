package ee.vk.businesstheatre.provider;

import android.net.Uri;
import android.text.TextUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by fvershinin on 1/1/15.
 */
class SQLiteUriMatcher {
    public static final int NO_MATCH = -1;
    public static final int MATCH_ID = 2;
    private static final int MATCH_ALL = 1;
    private final Set<String> authorities = new CopyOnWriteArraySet<>();

    public void addAuthority(String authority) {
        authorities.add(authority);
    }

    public int match(Uri uri) {
        if (authorities.contains(uri.getAuthority())) {
            final List<String> pathSegments = uri.getPathSegments();
            final int pathSegmentsSize = pathSegments.size();
            if (pathSegmentsSize == 1) {
                return MATCH_ALL;
            } else if (pathSegmentsSize == 2 && TextUtils.isDigitsOnly(pathSegments.get(1))) {

                return MATCH_ID;
            }

        }
        return NO_MATCH;
    }
}
