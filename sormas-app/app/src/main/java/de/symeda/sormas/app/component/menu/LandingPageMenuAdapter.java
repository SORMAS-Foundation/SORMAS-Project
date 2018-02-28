package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    private ArrayList<LandingPageMenuItem> data;
    private int cellLayout;
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
    public void initialize(ArrayList<LandingPageMenuItem> data, int cellLayout, int positionColor, int positionActiveColor, int titleColor, int titleActiveColor) {
        this.data = data;
        this.cellLayout = cellLayout;
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

        Context appContext = this.context;
        ArrayList<LandingPageMenuItem> menuItems = this.data;

        int icon;
        String iconName;
        String defType;

        View layout;
        ViewHolder viewHolder;

        if (convertView == null) {
            // if it's not recycled, initializeDialog some attributes
            LayoutInflater inflater = (LayoutInflater)appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(this.cellLayout, parent, false);
        } else {
            layout = (View) convertView;
        }

        viewHolder = new ViewHolder(layout);

        LandingPageMenuItem landingPageMenuItem = menuItems.get(position);
        viewHolder.txtNotificationCounter.setText(String.valueOf(landingPageMenuItem.getNotificationCount()));

        iconName = landingPageMenuItem.getIcon().getIconName();
        defType = landingPageMenuItem.getIcon().getDefType();
        icon = appContext.getResources().getIdentifier(iconName, defType, appContext.getPackageName());
        viewHolder.imgMenuItemIcon.setImageResource(icon);

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
