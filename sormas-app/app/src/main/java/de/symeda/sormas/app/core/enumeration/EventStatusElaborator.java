package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.R;

public class EventStatusElaborator implements StatusElaborator {

    private EventStatus status = null;

    public EventStatusElaborator(EventStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName(Context context) {
        if (status != null) {
            return status.toShortString();
        }

        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == EventStatus.POSSIBLE) {
            return R.color.indicatorPossibleEvent;
        } else if (status == EventStatus.CONFIRMED) {
            return R.color.indicatorConfirmedEvent;
        } else if (status == EventStatus.NO_EVENT) {
            return R.color.indicatorNoEvent;
        }

        return R.color.noColor;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }

    @Override
    public int getIconResourceId() {
        switch (status) {
            case POSSIBLE:
                return R.drawable.ic_lp_possible_alerts_192dp;
            case CONFIRMED:
                return R.drawable.ic_lp_confirmed_alerts_192dp;
            case NO_EVENT:
                return R.drawable.ic_lp_not_an_alert_192dp;
            default:
                throw new IllegalArgumentException(status.toString());
        }
    }
}
