package de.symeda.sormas.app.sample.landing;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;

/**
 * Created by Orson on 01/12/2017.
 */

public class SampleLandingSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

    private Context context;

    public SampleLandingSummaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewTypeEnum getEnumFromPosition(int position) {
        ViewTypeEnum viewType;

        switch (position) {
            case PositionHelper.TOTAL_SAMPLES: {
                viewType = ViewTypeEnum.TOTAL;
                break;
            }
            case PositionHelper.POSITIVE_RESULTS:
            case PositionHelper.NEGATIVE_RESULTS:
            case PositionHelper.PENDING_RESULTS:
            case PositionHelper.INDETERMINATE_RESULTS:
            case PositionHelper.INADEQUATE_SPECIMEN: {
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
        static final int TOTAL_SAMPLES = 0;
        static final int POSITIVE_RESULTS = 1;
        static final int NEGATIVE_RESULTS = 2;
        static final int PENDING_RESULTS = 3;
        static final int INDETERMINATE_RESULTS = 4;
        static final int INADEQUATE_SPECIMEN = 5;
    }
}

