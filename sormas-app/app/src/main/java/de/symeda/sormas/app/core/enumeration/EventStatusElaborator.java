package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.event.EventStatus;

/**
 * Created by Orson on 25/12/2017.
 */
public class EventStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private EventStatus status = null;

    public EventStatusElaborator(EventStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == EventStatus.POSSIBLE) {
            return resources.getString(R.string.status_event_possible);
        } else if (status == EventStatus.CONFIRMED) {
            return resources.getString(R.string.status_event_confirmed);
        } else if (status == EventStatus.NO_EVENT) {
            return resources.getString(R.string.status_event_no_event);
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
