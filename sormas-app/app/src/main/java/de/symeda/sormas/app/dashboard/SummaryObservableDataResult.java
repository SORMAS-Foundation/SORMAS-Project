package de.symeda.sormas.app.dashboard;

import java.util.List;

import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;

/**
 * Created by Orson on 09/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class SummaryObservableDataResult {

    private List<SummaryTotalData> mTotalData;
    private List<SummaryCircularData> mCircularData;

    public SummaryObservableDataResult(List<SummaryTotalData> totalData, List<SummaryCircularData> circularData) {
        this.mTotalData = totalData;
        this.mCircularData = circularData;
    }

    public List<SummaryTotalData> getTotalData() {
        return mTotalData;
    }

    public List<SummaryCircularData> getCircularData() {
        return mCircularData;
    }
}