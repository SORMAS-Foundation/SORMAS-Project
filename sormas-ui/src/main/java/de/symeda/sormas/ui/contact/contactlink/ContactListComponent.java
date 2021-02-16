package de.symeda.sormas.ui.contact.contactlink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactListComponent extends VerticalLayout {

	private ContactList list;

	public ContactListComponent(PersonReferenceDto personReferenceDto) {
		createContactListComponent(
			new ContactList(personReferenceDto),
			I18nProperties.getString(Strings.entityContacts),
			clickEvent -> ControllerProvider.getContactController().navigateTo(new ContactCriteria().setPerson(personReferenceDto)));
	}

	private void createContactListComponent(ContactList contactList, String heading, Button.ClickListener clickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label label = new Label(heading);
		label.addStyleName(CssStyles.H3);
		componentHeader.addComponent(label);

		list = contactList;
		addComponent(list);
		list.reload();

		if (!list.isEmpty()) {
			final Button seeContacts = new Button(I18nProperties.getCaption(Captions.personLinkToContacts));
			CssStyles.style(seeContacts, ValoTheme.BUTTON_PRIMARY);
			seeContacts.addClickListener(clickListener);
			addComponent(seeContacts);
			setComponentAlignment(seeContacts, Alignment.MIDDLE_LEFT);
		}
	}
}
