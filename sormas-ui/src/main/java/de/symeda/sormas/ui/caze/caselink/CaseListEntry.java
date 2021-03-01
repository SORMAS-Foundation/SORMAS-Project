package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactDto;
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

		HorizontalLayout uuidReportDateLayout = new HorizontalLayout();
		uuidReportDateLayout.setMargin(false);
		uuidReportDateLayout.setSpacing(true);

		Label caseUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(caseIndexDto.getUuid())));
		caseUuidLabel.setDescription(caseIndexDto.getUuid());
		uuidReportDateLayout.addComponent(caseUuidLabel);

		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseIndexDto.REPORT_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatDate(caseIndexDto.getReportDate()));
		uuidReportDateLayout.addComponent(reportDateLabel);

		uuidReportDateLayout.setWidthFull();
		uuidReportDateLayout.setComponentAlignment(caseUuidLabel, Alignment.MIDDLE_LEFT);
		uuidReportDateLayout.setComponentAlignment(reportDateLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(uuidReportDateLayout);

		HorizontalLayout diseaseClassificationLayout = new HorizontalLayout();
		diseaseClassificationLayout.setMargin(false);
		diseaseClassificationLayout.setSpacing(true);

		Label diseaseLabel = new Label(caseIndexDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(caseIndexDto.getDisease().toString());

		Label classificationLabel = new Label(caseIndexDto.getCaseClassification().toString());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD);
		classificationLabel.setDescription(caseIndexDto.getCaseClassification().toString());

		diseaseClassificationLayout.addComponent(diseaseLabel);
		diseaseClassificationLayout.addComponent(classificationLabel);
		diseaseClassificationLayout.setWidthFull();
		diseaseClassificationLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseaseClassificationLayout.setComponentAlignment(classificationLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(diseaseClassificationLayout);

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
