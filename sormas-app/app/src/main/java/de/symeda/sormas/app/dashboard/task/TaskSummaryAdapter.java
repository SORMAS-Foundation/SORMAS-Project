package de.symeda.sormas.app.dashboard.task;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;


public class TaskSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

    private Context context;

    public TaskSummaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewTypeEnum getEnumFromPosition(int position) {
        ViewTypeEnum viewType;

        switch (position) {
            case PositionHelper.TOTAL_TASKS: {
                viewType = ViewTypeEnum.TOTAL;
                break;
            }
            case PositionHelper.TASK_PRIORITY: {
                viewType = ViewTypeEnum.PIECHART_WITH_LEGEND;
                break;
            }
            case PositionHelper.PENDING_TAKS:
            case PositionHelper.DONE_TASKS:
            case PositionHelper.REMOVED_TASKS:
            case PositionHelper.NOT_EXECUTABLE_TASKS: {
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
        static final int TOTAL_TASKS = 0;
        static final int TASK_PRIORITY = 1;
        static final int PENDING_TAKS = 2;
        static final int DONE_TASKS = 3;
        static final int REMOVED_TASKS = 4;
        static final int NOT_EXECUTABLE_TASKS = 5;
    }

}
