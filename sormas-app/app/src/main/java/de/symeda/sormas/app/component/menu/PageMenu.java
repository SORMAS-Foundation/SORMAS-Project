package de.symeda.sormas.app.component.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PageMenu implements Parcelable {

    private String name;
    private String title;
    private List<PageMenuItem> menuItems;

    public PageMenu(Parcel in) {
        readFromParcel(in);
    }

    public PageMenu(String name, String title) {
        this.name = name;
        this.title = title;
        this.menuItems = new ArrayList<PageMenuItem>();
    }

    public PageMenu(String name, String title, List<PageMenuItem> menuItems) {
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

    public List<PageMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<PageMenuItem> list) {
        this.menuItems = list;
    }

    public void addMenuItem(PageMenuItem menuItem) {
        if (this.menuItems == null)
            throw new IllegalArgumentException("The menu item list is null.");

        this.menuItems.add(menuItem);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new PageMenu(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new PageMenu[0];
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

    private void readFromParcel(Parcel in) {
        if (this.menuItems == null)
            this.menuItems = in.createTypedArrayList(PageMenuItem.CREATOR);

        this.name = in.readString();
        this.title = in.readString();
        in.readTypedList(this.menuItems, PageMenu.CREATOR);
    }
}
