package de.symeda.sormas.app.event.landing;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;

/**
 * Created by Orson on 01/12/2017.
 */

public class EventsLandingSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

    private Context context;

    public EventsLandingSummaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewTypeEnum getEnumFromPosition(int position) {
        ViewTypeEnum viewType;

        switch (position) {
            case PositionHelper.TOTAL_EVENTS: {
                viewType = ViewTypeEnum.TOTAL;
                break;
            }
            case PositionHelper.POSSIBLE_EVENTS:
            case PositionHelper.CONFIRMED_EVENTS:
            case PositionHelper.NOT_AN_EVENT:
            case PositionHelper.RUMOR:
            case PositionHelper.OUTBREAK: {
                viewType = ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS;
                break;
            }
            default:
                throw new IllegalArgumentException("The value of position is invalid.");
        }
        return viewType;
    }

    @Override
    public ViewTypeEnum getEnumFromOrdinal(int ordinal) {
        return ViewTypeEnum.values()[ordinal];
    }

    public IAdapterConfiguration startConfig() {
        return new AdapterConfiguration<ViewTypeEnum>(this.context, this);
    }

    static class PositionHelper {
        static final int TOTAL_EVENTS = 0;
        static final int POSSIBLE_EVENTS = 1;
        static final int CONFIRMED_EVENTS = 2;
        static final int NOT_AN_EVENT = 3;
        static final int RUMOR = 4;
        static final int OUTBREAK = 5;
    }

}
