package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactSearchByCaseStrategy implements ISearchStrategy<Contact> {
    private String recordId;

    public ContactSearchByCaseStrategy(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public List<Contact> search() {
        List<Contact> result = new ArrayList<>();

        if (recordId == null || recordId.isEmpty())
            return result;

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuidReference(recordId);

        if (caze != null) {
            result = DatabaseHelper.getContactDao().getByCase(caze);
        }

        return result;
    }
}

