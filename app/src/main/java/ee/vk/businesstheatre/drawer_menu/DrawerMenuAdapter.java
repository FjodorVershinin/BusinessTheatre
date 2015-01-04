package ee.vk.businesstheatre.drawer_menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ee.vk.businesstheatre.R;

/**
 * Created by fvershinin on 1/2/15.
 */
class DrawerMenuAdapter extends ArrayAdapter<DrawerMenuModelItem> {

    private final int mResource;

    public DrawerMenuAdapter(Context context) {
        super(context, R.layout.drawer_list_item, listImpl(context.getResources().getStringArray(R.array.drawer_menu_titles),
                context.getResources().obtainTypedArray(R.array.drawer_menu_icons)));
        this.mResource = R.layout.drawer_list_item;
    }

    private static List<DrawerMenuModelItem> listImpl(String[] titles, TypedArray icons) {
        final List<DrawerMenuModelItem> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            items.add(new DrawerMenuModelItem().setTitle(titles[i]).setIconRes(icons.getResourceId(i, -1)));
        }
        return items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        DrawerMenuViewHolder viewHolder;
        DrawerMenuModelItem item = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(mResource, null, false);
            viewHolder = new DrawerMenuViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) view.findViewById(R.id.text1);
            view.setTag(viewHolder);
        } else
            viewHolder = (DrawerMenuViewHolder) view.getTag();
        viewHolder.textView.setText(item.getTitle());
        viewHolder.imageView.setImageResource(item.getIconRes());
        return view;
    }

    public static class DrawerMenuViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
