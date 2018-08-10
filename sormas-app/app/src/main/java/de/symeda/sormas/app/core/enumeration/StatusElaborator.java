package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

public interface StatusElaborator {

    String getFriendlyName(Context context);
    int getColorIndicatorResource();
    Enum getValue();
    int getIconResourceId();
}
