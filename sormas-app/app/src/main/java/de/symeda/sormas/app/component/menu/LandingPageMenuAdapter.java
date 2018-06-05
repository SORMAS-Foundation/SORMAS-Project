package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuAdapter extends BaseAdapter implements IPageMenuAdapter {

    private Context context;
    private List<LandingPageMenuItem> data;
    private int cellLayout;

    private int counterBackgroundColor;
    private int counterBackgroundActiveColor;
    private int iconColor;
    private int iconActiveColor;

    private int positionColor;
    private int positionActiveColor;
    private int titleColor;
    private int titleActiveColor;

    private boolean initialized = false;

    public LandingPageMenuAdapter(Context context, int cellLayout) {
        this.context = context;
        this.cellLayout = cellLayout;
        this.data = new ArrayList<>();
    }

    public LandingPageMenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void initialize(List<LandingPageMenuItem> data, int cellLayout,
                           int counterBackgroundColor, int counterBackgroundActiveColor,
                           int iconColor, int iconActiveColor,
                           int positionColor, int positionActiveColor, int titleColor, int titleActiveColor) {
        this.data = data;
        this.cellLayout = cellLayout;
        this.counterBackgroundColor = counterBackgroundColor;
        this.counterBackgroundActiveColor = counterBackgroundActiveColor;
        this.iconColor = iconColor;
        this.iconActiveColor = iconActiveColor;
        this.positionColor = positionColor;
        this.positionActiveColor = positionActiveColor;
        this.titleColor = titleColor;
        this.titleActiveColor = titleActiveColor;

        initialized = true;
    }

    @Override
    public int getCount() {
        checkInitStatus();

        if (this.data == null)
            return 0;

        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        checkInitStatus();

        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        checkInitStatus();

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        checkInitStatus();

        List<LandingPageMenuItem> menuItems = this.data;

        Drawable icon;
        String iconName;
        String defType;

        View layout;
        ViewHolder viewHolder;

        if (convertView == null) {
            // if it's not recycled, initializeDialog some attributes
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(this.cellLayout, parent, false);
        } else {
            layout = (View) convertView;
        }

        viewHolder = new ViewHolder(layout);

        LandingPageMenuItem landingPageMenuItem = menuItems.get(position);
        viewHolder.txtNotificationCounter.setText(String.valueOf(landingPageMenuItem.getNotificationCount()));

        Drawable counterDrawable = viewHolder.txtNotificationCounter.getBackground();

        if (landingPageMenuItem.isActive()) {
            counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundActiveColor));
        } else {
            counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundColor));
        }

        if (landingPageMenuItem.getIcon() != null) {
            iconName = landingPageMenuItem.getIcon().getIconName();
            defType = landingPageMenuItem.getIcon().getDefType();

            if ((iconName != null && !iconName.isEmpty()) && (defType != null && !defType.isEmpty())) {
                icon = context.getResources().getDrawable(context.getResources().getIdentifier(iconName, defType, context.getPackageName()));

                if (landingPageMenuItem.isActive()) {
                    icon.setTint(context.getResources().getColor(this.iconActiveColor));
                    icon.setAlpha(255);
                } else {
                    icon.setTint(context.getResources().getColor(this.iconColor));
                    icon.setAlpha(128);
                }

                viewHolder.imgMenuItemIcon.setImageDrawable(icon);
            }
        }

        viewHolder.txtMenuItemTitle.setText(landingPageMenuItem.getTitle());

        return layout;
    }

    private void checkInitStatus() {
        if (!initialized){
            try {
                throw new Exception("LandingPageMenuAdapter not initialized.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ViewHolder {
        View layout;
        TextView txtNotificationCounter;
        ImageView imgMenuItemIcon;
        TextView txtMenuItemTitle;

        public ViewHolder(View layout) {
            this.layout = layout;

            txtNotificationCounter = (TextView) this.layout.findViewById(R.id.txtNotificationCounter);
            imgMenuItemIcon = (ImageView) this.layout.findViewById(R.id.imgMenuItemIcon);
            txtMenuItemTitle = (TextView) this.layout.findViewById(R.id.txtMenuItemTitle);
        }
    }
}
