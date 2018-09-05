package de.symeda.sormas.app.contact;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum ContactSection implements StatusElaborator {

    CONTACT_INFO(R.string.caption_contact_information, R.drawable.ic_drawer_contact_blue_24dp),
    PERSON_INFO(R.string.caption_person_information, R.drawable.ic_person_black_24dp),
    VISITS(R.string.caption_contact_visits, R.drawable.ic_recent_actors_black_24dp),
    TASKS(R.string.caption_contact_tasks, R.drawable.ic_drawer_user_task_blue_24dp);

    private int friendlyNameResourceId;
    private int iconResourceId;

    ContactSection(int friendlyNameResourceId, int iconResourceId) {
        this.friendlyNameResourceId = friendlyNameResourceId;
        this.iconResourceId = iconResourceId;
    }

    public static ContactSection fromOrdinal(int ordinal) {
        return ContactSection.values()[ordinal];
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
