package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.R;

public class EventStatusElaborator implements IStatusElaborator {

    private EventStatus status = null;

    public EventStatusElaborator(EventStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName() {
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
    public String getStatekey() {
        return ARG_EVENT_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
