package de.symeda.sormas.app.contact.list;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
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
        List<Contact> list = new ArrayList<>();

        if (caseUuid == null || caseUuid.isEmpty())
            return list;

        final Case caze = DatabaseHelper.getCaseDao().queryUuidReference(caseUuid);
        if (caze != null) {
            list = DatabaseHelper.getContactDao().getByCase(caze);
        }

        return list;
    }
}
