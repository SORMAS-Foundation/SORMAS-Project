package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.app.R;

public class EventTypeElaborator implements IStatusElaborator {

    private EventType status = null;

    public EventTypeElaborator(EventType status) {
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
