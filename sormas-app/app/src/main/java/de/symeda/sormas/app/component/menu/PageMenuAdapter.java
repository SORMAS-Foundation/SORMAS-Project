package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.R;

public class PageMenuAdapter extends BaseAdapter {

    private Context context;
    private List<PageMenuItem> data;
    private int cellLayout;

    private int counterBackgroundColor;
    private int counterBackgroundActiveColor;
    private int iconColor;
    private int iconActiveColor;

    private int positionColor;
    private int positionActiveColor;
    private int titleColor;
    private int titleActiveColor;

    public PageMenuAdapter(Context context) {
        this.context = context;
    }

    public void initialize(int cellLayout,
                           int counterBackgroundColor, int counterBackgroundActiveColor,
                           int iconColor, int iconActiveColor,
                           int positionColor, int positionActiveColor, int titleColor, int titleActiveColor) {
        this.cellLayout = cellLayout;
        this.counterBackgroundColor = counterBackgroundColor;
        this.counterBackgroundActiveColor = counterBackgroundActiveColor;
        this.iconColor = iconColor;
        this.iconActiveColor = iconActiveColor;
        this.positionColor = positionColor;
        this.positionActiveColor = positionActiveColor;
        this.titleColor = titleColor;
        this.titleActiveColor = titleActiveColor;
    }

    public void setData(List<PageMenuItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (this.data == null)
            return 0;

        return this.data.size();
    }

    @Override
    public Object getItem(int position) {

        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<PageMenuItem> menuItems = this.data;

        Drawable icon;
        View layout;
        ViewHolder viewHolder;

        if (convertView == null) {
            // if it's not recycled, initializeDialog some attributes
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(this.cellLayout, parent, false);
        } else {
            layout = (View) convertView;
        }

        PageMenuItem pageMenuItem = menuItems.get(position);
        viewHolder = new ViewHolder(layout);

        if (viewHolder.txtNotificationCounter != null) {
            viewHolder.txtNotificationCounter.setText(String.valueOf(pageMenuItem.getNotificationCount()));
            // TODO show notifications count
            viewHolder.txtNotificationCounter.setVisibility(View.GONE);

            Drawable counterDrawable = viewHolder.txtNotificationCounter.getBackground();
            if (pageMenuItem.isActive()) {
                counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundActiveColor));
            } else {
                counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundColor));
            }
        }

        if (pageMenuItem.getIcon() > 0) {
            icon = context.getResources().getDrawable(pageMenuItem.getIcon());
            if (pageMenuItem.isActive()) {
                icon.setTint(context.getResources().getColor(this.iconActiveColor));
                icon.setAlpha(255);
            } else {
                icon.setTint(context.getResources().getColor(this.iconColor));
                icon.setAlpha(128);
            }
            viewHolder.imgMenuItemIcon.setImageDrawable(icon);
        }

        if (viewHolder.txtPosition != null) {
            viewHolder.txtPosition.setText(String.valueOf(pageMenuItem.getKey() + 1));
            if (pageMenuItem.isActive()) {
                viewHolder.txtPosition.setTextColor(context.getResources().getColor(this.positionActiveColor));
            } else {
                viewHolder.txtPosition.setTextColor(context.getResources().getColor(this.positionColor));
            }
        }

        viewHolder.txtMenuItemTitle.setText(pageMenuItem.getTitle());
        if (pageMenuItem.isActive()) {
            viewHolder.txtMenuItemTitle.setTextColor(context.getResources().getColor(this.titleActiveColor));
        } else {
            viewHolder.txtMenuItemTitle.setTextColor(context.getResources().getColor(this.titleColor));
        }


        return layout;
    }

    static class ViewHolder {
        View layout;
        TextView txtNotificationCounter;
        ImageView imgMenuItemIcon;
        TextView txtMenuItemTitle;
        TextView txtPosition;

        public ViewHolder(View layout) {
            this.layout = layout;

            txtNotificationCounter = (TextView) this.layout.findViewById(R.id.counter);
            imgMenuItemIcon = (ImageView) this.layout.findViewById(R.id.icon);
            txtMenuItemTitle = (TextView) this.layout.findViewById(R.id.title);
            txtPosition = (TextView) this.layout.findViewById(R.id.navigation_number);
        }
    }
}
