package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseListEntryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class CaseListEntry extends SideComponentField {

	public static final String SEPARATOR = ": ";
	private final CaseListEntryDto caseListEntryDto;

	public CaseListEntry(CaseListEntryDto caseListEntryDto) {
		this.caseListEntryDto = caseListEntryDto;

		HorizontalLayout uuidReportDateLayout = new HorizontalLayout();
		uuidReportDateLayout.setMargin(false);
		uuidReportDateLayout.setSpacing(true);

		Label caseUuidLabel = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(caseListEntryDto.getUuid())));
		caseUuidLabel.setDescription(caseListEntryDto.getUuid());
		uuidReportDateLayout.addComponent(caseUuidLabel);

		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseListEntryDto.REPORT_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatDate(caseListEntryDto.getReportDate()));
		uuidReportDateLayout.addComponent(reportDateLabel);

		uuidReportDateLayout.setWidthFull();
		uuidReportDateLayout.setComponentAlignment(caseUuidLabel, Alignment.MIDDLE_LEFT);
		uuidReportDateLayout.setComponentAlignment(reportDateLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(uuidReportDateLayout);

		HorizontalLayout diseaseClassificationLayout = new HorizontalLayout();
		diseaseClassificationLayout.setMargin(false);
		diseaseClassificationLayout.setSpacing(true);

		Label diseaseLabel = new Label(caseListEntryDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(caseListEntryDto.getDisease().toString());

		Label classificationLabel = new Label(caseListEntryDto.getCaseClassification().toString());
		classificationLabel.addStyleNames(CssStyles.LABEL_BOLD);
		classificationLabel.setDescription(caseListEntryDto.getCaseClassification().toString());

		diseaseClassificationLayout.addComponent(diseaseLabel);
		diseaseClassificationLayout.addComponent(classificationLabel);
		diseaseClassificationLayout.setWidthFull();
		diseaseClassificationLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseaseClassificationLayout.setComponentAlignment(classificationLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(diseaseClassificationLayout);

	}

	public CaseListEntryDto getCaseListEntryDto() {
		return caseListEntryDto;
	}
}
