package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotImplementedException;

public class EventTypeElaborator implements StatusElaborator {

    private EventType status = null;

    public EventTypeElaborator(EventType status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName(Context context) {
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
    public Enum getValue() {
        return this.status;
    }

    @Override
    public int getIconResourceId() {
        throw new NotImplementedException("getIconResourceId");
    }
}
