package de.symeda.sormas.ui.contact.contactlink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ContactListComponent extends SideComponent {

	public ContactListComponent(PersonReferenceDto personReferenceDto) {
		super(I18nProperties.getString(Strings.entityContacts));

		ContactList contactList = new ContactList(personReferenceDto);
		addComponent(contactList);
		contactList.reload();

		if (!contactList.isEmpty()) {
			final Button seeContacts = ButtonHelper.createButton(I18nProperties.getCaption(Captions.personLinkToContacts));
			CssStyles.style(seeContacts, ValoTheme.BUTTON_PRIMARY);
			seeContacts.addClickListener(
				clickEvent -> ControllerProvider.getContactController().navigateTo(new ContactCriteria().setPerson(personReferenceDto)));
			addComponent(seeContacts);
			setComponentAlignment(seeContacts, Alignment.MIDDLE_LEFT);
		}
	}
}
