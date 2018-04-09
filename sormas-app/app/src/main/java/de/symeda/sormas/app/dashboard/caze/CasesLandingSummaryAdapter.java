package de.symeda.sormas.app.dashboard.caze;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;

/**
 * Created by Orson on 01/12/2017.
 */

public class CasesLandingSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

    private Context context;

    public CasesLandingSummaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewTypeEnum getEnumFromPosition(int position) {
        ViewTypeEnum viewType;

        switch (position) {
            case PositionHelper.TOTAL_CASES: {
                viewType = ViewTypeEnum.TOTAL;
                break;
            }
            case PositionHelper.CONFIRMED_CASES:
            case PositionHelper.PROBABLE_CASES:
            case PositionHelper.SUSPECTED_CASES:
            case PositionHelper.FATALITIES:
            case PositionHelper.CASE_FATALITY_RATE: {
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
        static final int TOTAL_CASES = 0;
        static final int CONFIRMED_CASES = 1;
        static final int PROBABLE_CASES = 2;
        static final int SUSPECTED_CASES = 3;
        static final int FATALITIES = 4;
        static final int CASE_FATALITY_RATE = 5;
    }
}
