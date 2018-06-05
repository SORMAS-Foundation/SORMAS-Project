package de.symeda.sormas.app.contact.list;

import java.util.List;

import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 07/12/2017.
 */
public interface IContactsSearchStrategy {
    List<Contact> search();
}
