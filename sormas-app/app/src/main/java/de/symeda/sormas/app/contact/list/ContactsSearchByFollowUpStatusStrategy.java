package de.symeda.sormas.app.contact.list;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 07/12/2017.
 */
public class ContactsSearchByFollowUpStatusStrategy implements IContactsSearchStrategy {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    private FollowUpStatus status = null;

    public ContactsSearchByFollowUpStatusStrategy(FollowUpStatus status) {
        this.status= status;
    }

    @Override
    public List<Contact> search() {
        List<Contact> list = new ArrayList<>();

        //TODO: Make changes here
        if (status == null) {
            return list;
        } else {
            list = DatabaseHelper.getContactDao().queryForEq(Contact.FOLLOW_UP_STATUS, status, Contact.REPORT_DATE_TIME, false);
        }

        return list;
    }
}
