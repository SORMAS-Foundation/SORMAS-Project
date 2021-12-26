package de.symeda.sormas.ui.immunization.immunizationlink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class ImmunizationListEntry extends SideComponentField {

	public static final String SEPARATOR = ": ";

	private final ImmunizationListEntryDto immunization;

	public ImmunizationListEntry(ImmunizationListEntryDto immunization) {
		this.immunization = immunization;

		HorizontalLayout uuidReportLayout = new HorizontalLayout();
		uuidReportLayout.setMargin(false);
		uuidReportLayout.setSpacing(true);

		Label immunizationUuidLabel = new Label(DataHelper.getShortUuid(immunization.getUuid()));
		immunizationUuidLabel.setDescription(immunization.getUuid());
		CssStyles.style(immunizationUuidLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		uuidReportLayout.addComponent(immunizationUuidLabel);

		Label diseaseLabel = new Label(DataHelper.toStringNullable(immunization.getDisease()));
		CssStyles.style(diseaseLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		uuidReportLayout.addComponent(diseaseLabel);

		uuidReportLayout.setWidthFull();
		uuidReportLayout.setComponentAlignment(immunizationUuidLabel, Alignment.MIDDLE_LEFT);
		uuidReportLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(uuidReportLayout);

		HorizontalLayout meansOfImmunizationLayout = new HorizontalLayout();
		Label meansOfImmunizationLabel = new Label(
			I18nProperties.getPrefixCaption(ImmunizationListEntryDto.I18N_PREFIX, ImmunizationListEntryDto.MEANS_OF_IMMUNIZATION)
				+ SEPARATOR
				+ DataHelper.toStringNullable(immunization.getMeansOfImmunization()));
		meansOfImmunizationLayout.addComponent(meansOfImmunizationLabel);
		addComponentToField(meansOfImmunizationLayout);

		HorizontalLayout immunizationStatusLayout = new HorizontalLayout();
		Label immunizationStatusLabel = new Label(
			I18nProperties.getPrefixCaption(ImmunizationListEntryDto.I18N_PREFIX, ImmunizationListEntryDto.IMMUNIZATION_STATUS)
				+ SEPARATOR
				+ DataHelper.toStringNullable(immunization.getImmunizationStatus()));
		immunizationStatusLayout.addComponent(immunizationStatusLabel);
		addComponentToField(immunizationStatusLayout);

		HorizontalLayout managementStatusLayout = new HorizontalLayout();
		Label managementStatusLabel = new Label(
			I18nProperties.getPrefixCaption(ImmunizationListEntryDto.I18N_PREFIX, ImmunizationListEntryDto.IMMUNIZATION_MANAGEMENT_STATUS)
				+ SEPARATOR
				+ DataHelper.toStringNullable(immunization.getImmunizationManagementStatus()));
		managementStatusLayout.addComponent(managementStatusLabel);
		addComponentToField(managementStatusLayout);

		HorizontalLayout immunizationPeriodLayout = new HorizontalLayout();
		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(ImmunizationListEntryDto.I18N_PREFIX, ImmunizationListEntryDto.IMMUNIZATION_PERIOD)
				+ SEPARATOR
				+ DateFormatHelper.buildPeriodString(immunization.getStartDate(), immunization.getEndDate()));
		immunizationPeriodLayout.addComponent(reportDateLabel);
		addComponentToField(immunizationPeriodLayout);
	}

	public ImmunizationListEntryDto getImmunizationEntry() {
		return immunization;
	}
}
