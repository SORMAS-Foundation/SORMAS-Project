package de.symeda.sormas.app.contact.list;

import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 07/12/2017.
 */
public class ContactsSearchByCaseStrategy implements IContactsSearchStrategy {

    private String caseUuid;

    public ContactsSearchByCaseStrategy(String caseUuid) {
        this.caseUuid = caseUuid;
    }

    @Override
    public List<Contact> search() {
        //TODO: Make changes here
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        final Case caze = caseDao.queryUuid(caseUuid);

        return DatabaseHelper.getContactDao().getByCase(caze);
    }
}
