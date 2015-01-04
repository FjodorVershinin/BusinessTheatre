package ee.vk.businesstheatre.drawer_menu;

/**
 * Created by fvershinin on 1/2/15.
 */
public class DrawerMenuModelItem {
    private String title;
    private int iconRes;

    public String getTitle() {
        return title;
    }

    public DrawerMenuModelItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getIconRes() {
        return iconRes;
    }

    public DrawerMenuModelItem setIconRes(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }
}
