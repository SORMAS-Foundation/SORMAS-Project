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

		VerticalLayout leftLayout = new VerticalLayout();
		leftLayout.setMargin(false);
		leftLayout.setSpacing(false);

		Label contactUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(contactIndexDto.getUuid())));
		contactUuidLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		contactUuidLabel.setDescription(contactIndexDto.getUuid());
		leftLayout.addComponent(contactUuidLabel);

		Label diseaseLabel = new Label(contactIndexDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		diseaseLabel.setDescription(contactIndexDto.getDisease().toString());
		leftLayout.addComponent(diseaseLabel);

		Label classificationLabel = new Label(contactIndexDto.getContactClassification().toString());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		classificationLabel.setDescription(contactIndexDto.getContactClassification().toString());
		leftLayout.addComponent(classificationLabel);

		Label statusLabel = new Label(contactIndexDto.getContactStatus().toString());
		statusLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		statusLabel.setDescription(contactIndexDto.getContactStatus().toString());
		leftLayout.addComponent(statusLabel);

		final ContactCategory contactCategory = contactIndexDto.getContactCategory();
		if (contactCategory != null) {
			Label categoryLabel = new Label(contactCategory.toString());
			categoryLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			categoryLabel.setDescription(contactCategory.toString());
			leftLayout.addComponent(categoryLabel);
		}

		mainLayout.addComponent(leftLayout);
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
