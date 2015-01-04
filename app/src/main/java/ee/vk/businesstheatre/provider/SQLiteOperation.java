package ee.vk.businesstheatre.provider;

/**
 * Created by fvershinin on 1/1/15.
 */
public interface SQLiteOperation {
    int INSERT = 1;
    int UPDATE = 2;
    int DELETE = 3;

    String KEY_LAST_ID = "ee.vk.businesstheatre.provider.KEY_LAST_ID";
    String KEY_AFFECTED_ROWS = "ee.vk.businesstheatre.provider.KEY_AFFECTED_ROWS";
}
