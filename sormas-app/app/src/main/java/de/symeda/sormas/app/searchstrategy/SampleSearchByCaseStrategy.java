package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SampleSearchByCaseStrategy implements ISearchStrategy<Sample> {
    private String recordId;

    public SampleSearchByCaseStrategy(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public List<Sample> search() {
        List<Sample> result = new ArrayList<>();

        if (recordId == null || recordId.isEmpty())
            return result;

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(recordId);

        if (caze != null) {
            result = DatabaseHelper.getSampleDao().queryByCase(caze);
        }

        return result;
    }
}
