package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class CaseListEntry extends HorizontalLayout {

	public static final String SEPARATOR = ": ";
	private final CaseIndexDto caseIndexDto;
	private Button editButton;

	public CaseListEntry(CaseIndexDto caseIndexDto) {
		this.caseIndexDto = caseIndexDto;
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		Label caseUuidLabel = new Label(
			DataHelper
				.toStringNullable(I18nProperties.getCaption(Captions.CaseData_uuid) + SEPARATOR + DataHelper.getShortUuid(caseIndexDto.getUuid())));
		caseUuidLabel.addStyleNames(CssStyles.LABEL_BOLD);
		caseUuidLabel.setDescription(caseIndexDto.getUuid());
		mainLayout.addComponent(caseUuidLabel);

		Label classificationLabel =
			new Label(I18nProperties.getCaption(Captions.CaseData_caseClassification) + SEPARATOR + caseIndexDto.getCaseClassification());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD);
		classificationLabel.setDescription(caseIndexDto.getCaseClassification().toString());
		mainLayout.addComponent(classificationLabel);

		Label diseaseLabel =
			new Label(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.DISEASE) + SEPARATOR + caseIndexDto.getDisease());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(caseIndexDto.getDisease().toString());
		mainLayout.addComponent(diseaseLabel);

		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)
				+ SEPARATOR
				+ DateFormatHelper.formatDate(caseIndexDto.getReportDate()));
		reportDateLabel.addStyleNames(CssStyles.LABEL_BOLD);
		mainLayout.addComponent(reportDateLabel);
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

	public CaseIndexDto getCaseIndexDto() {
		return caseIndexDto;
	}
}
