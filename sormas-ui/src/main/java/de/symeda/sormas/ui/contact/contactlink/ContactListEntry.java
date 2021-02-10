package de.symeda.sormas.ui.contact.contactlink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactListEntry extends HorizontalLayout {

	public static final String SEPARATOR = ": ";

	private final ContactIndexDto contactIndexDto;

	private Button editButton;

	public ContactListEntry(ContactIndexDto contactIndexDto) {
		this.contactIndexDto = contactIndexDto;
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		HorizontalLayout uuidStatusLayout = new HorizontalLayout();
		uuidStatusLayout.setMargin(false);
		uuidStatusLayout.setSpacing(true);

		Label contactUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(contactIndexDto.getUuid())));
		contactUuidLabel.setDescription(contactIndexDto.getUuid());

		Label statusLabel = new Label(contactIndexDto.getContactStatus().toString());
		statusLabel.addStyleNames(CssStyles.LABEL_BOLD);
		statusLabel.setDescription(contactIndexDto.getContactStatus().toString());

		uuidStatusLayout.addComponent(contactUuidLabel);
		uuidStatusLayout.addComponent(statusLabel);
		uuidStatusLayout.setWidthFull();
		uuidStatusLayout.setComponentAlignment(contactUuidLabel, Alignment.MIDDLE_LEFT);
		uuidStatusLayout.setComponentAlignment(statusLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(uuidStatusLayout);

		HorizontalLayout diseaseClassificationLayout = new HorizontalLayout();
		diseaseClassificationLayout.setMargin(false);
		diseaseClassificationLayout.setSpacing(true);

		Label diseaseLabel = new Label(contactIndexDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(contactIndexDto.getDisease().toString());

		Label classificationLabel = new Label(contactIndexDto.getContactClassification().toString());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD);
		classificationLabel.setDescription(contactIndexDto.getContactClassification().toString());

		diseaseClassificationLayout.addComponent(diseaseLabel);
		diseaseClassificationLayout.addComponent(classificationLabel);
		diseaseClassificationLayout.setWidthFull();
		diseaseClassificationLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseaseClassificationLayout.setComponentAlignment(classificationLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(diseaseClassificationLayout);

		final ContactCategory contactCategory = contactIndexDto.getContactCategory();
		if (contactCategory != null) {
			Label categoryLabel = new Label(contactCategory.toString());
			if (ContactCategory.HIGH_RISK == contactCategory || ContactCategory.HIGH_RISK_MED == contactCategory) {
				categoryLabel.addStyleName(CssStyles.LABEL_IMPORTANT);
			} else {
				categoryLabel.addStyleName(CssStyles.LABEL_NEUTRAL);
			}
			categoryLabel.setDescription(contactCategory.toString());
			mainLayout.addComponent(categoryLabel);
		}
	}

	public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-participant-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public ContactIndexDto getContactIndexDto() {
		return contactIndexDto;
	}
}
