package de.symeda.sormas.app.contact;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum ContactSection implements StatusElaborator {

    CONTACT_INFO,
    PERSON_INFO,
    VISITS,
    TASKS;

    public static ContactSection fromOrdinal(int ordinal) {
        return ContactSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        switch(this) {
            case CONTACT_INFO:
                return context.getResources().getString(R.string.caption_contact_information);
            case PERSON_INFO:
                return context.getResources().getString(R.string.caption_person_information);
            case VISITS:
                return context.getResources().getString(R.string.caption_contact_visits);
            case TASKS:
                return context.getResources().getString(R.string.caption_contact_tasks);
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
