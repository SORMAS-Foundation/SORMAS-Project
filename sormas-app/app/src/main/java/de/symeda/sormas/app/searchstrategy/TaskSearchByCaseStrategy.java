package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskSearchByCaseStrategy implements ISearchStrategy<Task> {
    private String recordId;

    public TaskSearchByCaseStrategy(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public List<Task> search() {
        List<Task> result = new ArrayList<>();

        if (recordId == null || recordId.isEmpty())
            return result;

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(recordId);

        if (caze != null) {
            result = DatabaseHelper.getTaskDao().queryByCase(caze);
        }

        return result;
    }
}
