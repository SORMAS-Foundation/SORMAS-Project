package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseSearchByAllStrategy implements ISearchStrategy<Case> {
    @Override
    public List<Case> search() {
        return DatabaseHelper.getCaseDao().queryForAll(Case.REPORT_DATE, false);
    }
}
