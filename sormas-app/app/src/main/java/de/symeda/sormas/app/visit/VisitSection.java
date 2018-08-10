package de.symeda.sormas.app.visit;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum VisitSection implements StatusElaborator {

    VISIT_INFO,
    SYMPTOMS;

    public static VisitSection fromOrdinal(int ordinal) {
        return VisitSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        switch (this) {
            case VISIT_INFO:
                return context.getResources().getString(R.string.caption_visit_information);
            case SYMPTOMS:
                return context.getResources().getString(R.string.caption_visit_symptoms);
            default:
                throw new IllegalArgumentException(this.toString());
        }
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
        return 0;
    }
}
