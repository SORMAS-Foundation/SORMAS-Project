package de.symeda.sormas.ui.contact.contactlink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class ContactListEntry extends SideComponentField {

	private final ContactListEntryDto contactListEntryDto;

	public ContactListEntry(ContactListEntryDto contactListEntryDto) {
		this.contactListEntryDto = contactListEntryDto;

		HorizontalLayout uuidStatusLayout = new HorizontalLayout();
		uuidStatusLayout.setMargin(false);
		uuidStatusLayout.setSpacing(true);

		Label contactUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(contactListEntryDto.getUuid())));
		contactUuidLabel.setDescription(contactListEntryDto.getUuid());

		Label statusLabel = new Label(contactListEntryDto.getContactStatus().toString());
		statusLabel.addStyleNames(CssStyles.LABEL_BOLD);
		statusLabel.setDescription(contactListEntryDto.getContactStatus().toString());

		uuidStatusLayout.addComponent(contactUuidLabel);
		uuidStatusLayout.addComponent(statusLabel);
		uuidStatusLayout.setWidthFull();
		uuidStatusLayout.setComponentAlignment(contactUuidLabel, Alignment.MIDDLE_LEFT);
		uuidStatusLayout.setComponentAlignment(statusLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(uuidStatusLayout);

		HorizontalLayout diseaseClassificationLayout = new HorizontalLayout();
		diseaseClassificationLayout.setMargin(false);
		diseaseClassificationLayout.setSpacing(true);

		Label diseaseLabel = new Label(contactListEntryDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(contactListEntryDto.getDisease().toString());

		Label classificationLabel = new Label(contactListEntryDto.getContactClassification().toString());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD);
		classificationLabel.setDescription(contactListEntryDto.getContactClassification().toString());

		diseaseClassificationLayout.addComponent(diseaseLabel);
		diseaseClassificationLayout.addComponent(classificationLabel);
		diseaseClassificationLayout.setWidthFull();
		diseaseClassificationLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseaseClassificationLayout.setComponentAlignment(classificationLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(diseaseClassificationLayout);

		final ContactCategory contactCategory = contactListEntryDto.getContactCategory();
		if (contactCategory != null) {
			Label categoryLabel = new Label(contactCategory.toString());
			if (ContactCategory.HIGH_RISK == contactCategory || ContactCategory.HIGH_RISK_MED == contactCategory) {
				categoryLabel.addStyleName(CssStyles.LABEL_IMPORTANT);
			} else {
				categoryLabel.addStyleName(CssStyles.LABEL_NEUTRAL);
			}
			categoryLabel.setDescription(contactCategory.toString());
			addComponentToField(categoryLabel);
		}
	}

	public ContactListEntryDto getContactListEntryDto() {
		return contactListEntryDto;
	}
}
