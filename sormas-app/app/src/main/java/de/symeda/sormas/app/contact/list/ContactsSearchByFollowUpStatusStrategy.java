package de.symeda.sormas.app.contact.list;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
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
            //return DatabaseHelper.getContactDao().queryForAll(Contact.REPORT_DATE_TIME, false);
            return new ArrayList<>();
        }


        if (status == FollowUpStatus.FOLLOW_UP) {
            list = MemoryDatabaseHelper.CONTACT.getFollowUpContacts(20);
        } else if (status == FollowUpStatus.COMPLETED) {
            list = MemoryDatabaseHelper.CONTACT.getCompletedFollowUpContacts(20);
        } else if (status == FollowUpStatus.CANCELED) {
            list = MemoryDatabaseHelper.CONTACT.getCanceledFollowUpContacts(20);
        } else if (status == FollowUpStatus.LOST) {
            list = MemoryDatabaseHelper.CONTACT.getLostFollowUpContacts(20);
        } else if (status == FollowUpStatus.NO_FOLLOW_UP) {
            list = MemoryDatabaseHelper.CONTACT.getNoFollowUpContacts(20);
        }

        return list;
    }
}
