package de.symeda.sormas.app.component.menu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuItemIcon implements Parcelable {
    private String iconName;
    private String defType;

    public LandingPageMenuItemIcon(Parcel in ) {
        readFromParcel( in );
    }

    public LandingPageMenuItemIcon(String iconName, String defType) {
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
            return new LandingPageMenuItemIcon(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new LandingPageMenuItemIcon[0];
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
