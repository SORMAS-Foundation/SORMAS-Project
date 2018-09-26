package de.symeda.sormas.app.visit;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum VisitSection implements StatusElaborator {

    VISIT_INFO(R.string.caption_visit_information, R.drawable.ic_recent_actors_black_24dp),
    SYMPTOMS(R.string.caption_visit_symptoms, R.drawable.ic_healing_black_24dp);

    private int friendlyNameResourceId;
    private int iconResourceId;

    VisitSection(int friendlyNameResourceId, int iconResourceId) {
        this.friendlyNameResourceId = friendlyNameResourceId;
        this.iconResourceId = iconResourceId;
    }

    public static VisitSection fromOrdinal(int ordinal) {
        return VisitSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        return context.getResources().getString(friendlyNameResourceId);
    }

    @Override
    public int getColorIndicatorResource() {
        return 0;
    }

    @Override
    public Enum getValue() {
        return this;
    }

    @Override
    public int getIconResourceId() {
        return iconResourceId;
    }
}
