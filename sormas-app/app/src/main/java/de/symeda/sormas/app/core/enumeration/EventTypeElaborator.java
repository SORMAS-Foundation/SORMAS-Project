package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventTypeElaborator implements IStatusElaborator {

    private Resources resources = null;
    private EventType status = null;

    public EventTypeElaborator(EventType status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == EventType.RUMOR) {
            return resources.getString(R.string.type_event_rumor);
        } else if (status == EventType.OUTBREAK) {
            return resources.getString(R.string.type_event_outbreak);
        }

        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == EventType.RUMOR) {
            return R.color.indicatorRumorEvent;
        } else if (status == EventType.OUTBREAK) {
            return R.color.indicatorOutbreakEvent;
        }

        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_EVENT_TYPE;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
