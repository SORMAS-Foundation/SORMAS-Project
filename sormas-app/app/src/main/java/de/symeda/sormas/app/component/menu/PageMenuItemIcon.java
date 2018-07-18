package de.symeda.sormas.app.component.menu;

import android.os.Parcel;
import android.os.Parcelable;

public class PageMenuItemIcon implements Parcelable {
    private String iconName;
    private String defType;

    public PageMenuItemIcon(Parcel in ) {
        readFromParcel( in );
    }

    public PageMenuItemIcon(String iconName, String defType) {
        this.iconName = iconName;
        this.defType = defType;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setDefType(String defType) {
        this.defType = defType;
    }

    public String getIconName() {
        return this.iconName;
    }

    public String getDefType() {
        return this.defType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new PageMenuItemIcon(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new PageMenuItemIcon[0];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iconName);
        dest.writeString(this.defType);
    }

    private void readFromParcel(Parcel in ) {

        this.iconName = in.readString();
        this.defType = in.readString();
    }
}
