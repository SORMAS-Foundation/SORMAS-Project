package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskSearchByContactStrategy implements ISearchStrategy<Task> {
    private String recordId;

    public TaskSearchByContactStrategy(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public List<Task> search() {
        List<Task> result = new ArrayList<>();

        if (recordId == null || recordId.isEmpty())
            return result;

        ContactDao contactDao = DatabaseHelper.getContactDao();
        Contact contact = contactDao.queryUuid(recordId);

        if (contact != null) {
            result = DatabaseHelper.getTaskDao().queryByContact(contact);
        }

        return result;
    }
}
