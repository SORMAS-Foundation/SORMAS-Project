package de.symeda.sormas.ui.contact.contactlink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

public class ContactList extends PaginationList<ContactListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final String personUuid;
	private final Label noContactLabel;

	public ContactList(PersonReferenceDto personReferenceDto) {
		super(MAX_DISPLAYED_ENTRIES);
		this.personUuid = personReferenceDto != null ? personReferenceDto.getUuid() : null;
		noContactLabel = new Label(I18nProperties.getCaption(Captions.personNoContactLinkedToPerson));
	}

	@Override
	public void reload() {
		List<ContactListEntryDto> contactIndexDtoList = FacadeProvider.getContactFacade().getEntriesList(personUuid, 0, maxDisplayedEntries * 20);

		setEntries(contactIndexDtoList);
		if (!contactIndexDtoList.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noContactLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<ContactListEntryDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			final ContactListEntryDto contactListEntryDto = displayedEntries.get(i);
			final ContactListEntry listEntry = new ContactListEntry(contactListEntryDto);
			final boolean isActiveContact = contactListEntryDto.getUuid().equals(getActiveUuid());
			if (isActiveContact) {
				listEntry.setActive();
			}
			if (UiUtil.permitted(UserRight.CONTACT_EDIT) && !isActiveContact) {
				listEntry.addEditButton(
					"edit-contact-" + i,
					(Button.ClickListener) event -> ControllerProvider.getContactController()
						.navigateToData(listEntry.getContactListEntryDto().getUuid()));
			}

			listLayout.addComponent(listEntry);
		}
	}
}
