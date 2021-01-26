package de.symeda.sormas.ui.contact.contactlink;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactListComponent extends VerticalLayout {

    private ContactList list;

    public ContactListComponent(PersonReferenceDto personReferenceDto) {
        createContactListComponent(new ContactList(personReferenceDto), I18nProperties.getString(Strings.entityContacts));
    }

    private void createContactListComponent(ContactList contactList, String heading) {
        setWidth(100, Unit.PERCENTAGE);
        setMargin(false);
        setSpacing(false);

        HorizontalLayout componentHeader = new HorizontalLayout();
        componentHeader.setMargin(false);
        componentHeader.setSpacing(false);
        componentHeader.setWidth(100, Unit.PERCENTAGE);
        addComponent(componentHeader);

        list = contactList;
        addComponent(list);
        list.reload();

        Label eventLabel = new Label(heading);
        eventLabel.addStyleName(CssStyles.H3);
        componentHeader.addComponent(eventLabel);
    }
}
