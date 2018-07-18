package de.symeda.sormas.app.component.menu;

import android.os.Parcel;
import android.os.Parcelable;


public class PageMenuItem implements Parcelable {
    private int key;
    private int notificationCount;
    private PageMenuItemIcon icon;
    private String title;
    private String description;
    private boolean active;

    public PageMenuItem(Parcel in ) {
        readFromParcel( in );
    }

    public PageMenuItem(int key, String title, String description, PageMenuItemIcon icon, int notificationCount, boolean active) {
        this.key = key;
        this.notificationCount = notificationCount;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public PageMenuItem(int key, String title, String description, PageMenuItemIcon icon, boolean active) {
        this.key = key;
        this.notificationCount = 0;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public void setIcon(PageMenuItemIcon icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNotificationCount() {
        return this.notificationCount;
    }

    public PageMenuItemIcon getIcon() {
        return this.icon;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new PageMenuItem(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new PageMenuItem[0];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.key);
        dest.writeInt(this.notificationCount);
        dest.writeParcelable(this.icon, flags);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.active ? 1 : 0);
    }

    private void readFromParcel(Parcel in ) {

        this.key = in.readInt();
        this.notificationCount = in.readInt();
        this.icon = in.readParcelable(PageMenuItemIcon.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.active = in.readInt() == 1;
    }
}
