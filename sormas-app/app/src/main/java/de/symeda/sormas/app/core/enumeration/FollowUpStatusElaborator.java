package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Orson on 25/12/2017.
 */
public class FollowUpStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private FollowUpStatus status = null;

    public FollowUpStatusElaborator(FollowUpStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == FollowUpStatus.FOLLOW_UP) {
            return resources.getString(R.string.status_followup_follow_up);
        } else if (status == FollowUpStatus.COMPLETED) {
            return resources.getString(R.string.status_followup_completed);
        } else if (status == FollowUpStatus.CANCELED) {
            return resources.getString(R.string.status_followup_canceled);
        } else if (status == FollowUpStatus.LOST) {
            return resources.getString(R.string.status_followup_lost);
        } else if (status == FollowUpStatus.NO_FOLLOW_UP) {
            return resources.getString(R.string.status_followup_no_follow_up);
        }

        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == FollowUpStatus.FOLLOW_UP) {
            return R.color.indicatorContactFollowUp;
        } else if (status == FollowUpStatus.COMPLETED) {
            return R.color.indicatorContactCompletedFollowUp;
        } else if (status == FollowUpStatus.CANCELED) {
            return R.color.indicatorContactCanceledFollowUp;
        } else if (status == FollowUpStatus.LOST) {
            return R.color.indicatorContactLostFollowUp;
        } else if (status == FollowUpStatus.NO_FOLLOW_UP) {
            return R.color.indicatorContactNoFollowUp;
        }

        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_FOLLOW_UP_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
