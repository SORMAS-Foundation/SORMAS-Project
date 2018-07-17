package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.R;

public class FollowUpStatusElaborator implements IStatusElaborator {

    private FollowUpStatus status = null;

    public FollowUpStatusElaborator(FollowUpStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName() {
        if (status != null) {
            return status.toString();
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
