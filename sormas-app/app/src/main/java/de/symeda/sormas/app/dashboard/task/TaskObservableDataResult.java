package de.symeda.sormas.app.dashboard.task;

import java.util.List;

import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;

/**
 * Created by Orson on 09/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class TaskObservableDataResult {

    private List<SummaryTotalData> mTotalData;
    private List<SummaryPieData> mPieData;
    private List<SummaryCircularData> mCircularData;

    public TaskObservableDataResult(List<SummaryTotalData> totalData, List<SummaryPieData> pieData, List<SummaryCircularData> circularData) {
        this.mTotalData = totalData;
        this.mPieData = pieData;
        this.mCircularData = circularData;
    }

    public List<SummaryTotalData> getTotalData() {
        return mTotalData;
    }

    public List<SummaryPieData> getPieData() {
        return mPieData;
    }

    public List<SummaryCircularData> getCircularData() {
        return mCircularData;
    }
}
