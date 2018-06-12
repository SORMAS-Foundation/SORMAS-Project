package de.symeda.sormas.app.dashboard.task;

import java.util.List;

import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.dashboard.SummaryObservableDataResult;

/**
 * Created by Orson on 09/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class TaskObservableDataResult extends SummaryObservableDataResult {

    private List<SummaryPieData> mPieData;

    public TaskObservableDataResult(List<SummaryTotalData> totalData, List<SummaryCircularData> circularData, List<SummaryPieData> pieData) {
        super(totalData, circularData);
        this.mPieData = pieData;
    }

    public List<SummaryPieData> getPieData() {
        return mPieData;
    }
}
