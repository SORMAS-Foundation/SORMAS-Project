package de.symeda.sormas.app.component.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 01/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenu implements Parcelable {

    private String name;
    private String title;
    private List<LandingPageMenuItem> menuItems;

    public LandingPageMenu(Parcel in ) {
        readFromParcel( in );
    }

    public LandingPageMenu(String name, String title) {
        this.name = name;
        this.title = title;
        this.menuItems = new ArrayList<LandingPageMenuItem>();
    }

    public LandingPageMenu(String name, String title, List<LandingPageMenuItem> menuItems) {
        this.name = name;
        this.title = title;
        this.menuItems = menuItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<LandingPageMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<LandingPageMenuItem> list) {
        this.menuItems = list;
    }

    public void addMenuItem(LandingPageMenuItem menuItem) {
        if (this.menuItems == null)
            throw new IllegalArgumentException("The menu item list is null.");

        this.menuItems.add(menuItem);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new LandingPageMenu(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new LandingPageMenu[0];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeTypedList(this.menuItems);
    }

    private void readFromParcel(Parcel in ) {
        if (this.menuItems == null)
            this.menuItems = in.createTypedArrayList(LandingPageMenuItem.CREATOR);

        this.name = in.readString();
        this.title = in.readString();
        in.readTypedList(this.menuItems, LandingPageMenu.CREATOR);
    }
}
