package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactSearchByStatusStrategy implements ISearchStrategy<Contact> {

    private FollowUpStatus status;

    public ContactSearchByStatusStrategy(FollowUpStatus status) {
        this.status = status;
    }

    @Override
    public List<Contact> search() {
        return DatabaseHelper.getContactDao().queryForEq(Contact.FOLLOW_UP_STATUS, status, Contact.REPORT_DATE_TIME, false);
    }
}