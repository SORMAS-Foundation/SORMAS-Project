package de.symeda.sormas.ui.contact.contactlink;

import static de.symeda.sormas.api.user.UserRight.CONTACT_CREATE;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ContactListComponent extends SideComponent {

	private ContactList contactList;

	public ContactListComponent(PersonReferenceDto personReferenceDto, String activeUuid, Consumer<Runnable> actionCallback, boolean isEditAllowed) {
		super(I18nProperties.getString(Strings.entityContacts), actionCallback);

		if (UiUtil.permitted(isEditAllowed, CONTACT_CREATE)) {
			addCreateButton(I18nProperties.getCaption(Captions.contactNewContact), () -> {
				ControllerProvider.getContactController().create(personReferenceDto);
			}, CONTACT_CREATE);
		}

		contactList = new ContactList(personReferenceDto);
		contactList.setActiveUuid(activeUuid);
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

	public List<ContactListEntryDto> getEntries() {
		return contactList.getEntries();
	}
}
