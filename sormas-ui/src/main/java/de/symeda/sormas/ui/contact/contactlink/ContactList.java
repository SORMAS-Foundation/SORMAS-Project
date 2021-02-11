package de.symeda.sormas.ui.contact.contactlink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class ContactList extends PaginationList<ContactIndexDto> {

	private final ContactCriteria contactCriteria = new ContactCriteria();
	private final Label noContactLabel;

	public ContactList(PersonReferenceDto personRef) {
		super(5);
		contactCriteria.setPerson(personRef);
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		noContactLabel = new Label(I18nProperties.getCaption(Captions.personNoContactLinkedToPerson));
	}

	@Override
	public void reload() {
		List<ContactIndexDto> contactIndexDtoList =
			FacadeProvider.getContactFacade().getIndexList(contactCriteria, 0, maxDisplayedEntries * 20, null);

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
		List<ContactIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			final ContactIndexDto contactIndexDto = displayedEntries.get(i);
			final ContactListEntry listEntry = new ContactListEntry(contactIndexDto);
			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
				listEntry.addEditListener(
					i,
					(Button.ClickListener) event -> ControllerProvider.getContactController()
						.navigateToData(listEntry.getContactIndexDto().getUuid()));
			}

			listLayout.addComponent(listEntry);
		}
	}
}
