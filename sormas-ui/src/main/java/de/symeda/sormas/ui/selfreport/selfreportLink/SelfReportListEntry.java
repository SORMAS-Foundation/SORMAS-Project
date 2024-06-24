package de.symeda.sormas.ui.selfreport.selfreportLink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportListEntryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class SelfReportListEntry extends SideComponentField {

	public SelfReportListEntry(SelfReportListEntryDto selfReportListEntryDto) {

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		addComponentToField(topLayout);

		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);

			Label uuidLabel = new Label(
				I18nProperties.getPrefixCaption(SelfReportListEntryDto.I18N_PREFIX, SelfReportDto.UUID) + ": "
					+ DataHelper.getShortUuid(selfReportListEntryDto.getUuid()));
			CssStyles.style(uuidLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			uuidLabel.setWidth(50, Unit.PERCENTAGE);
			topLeftLayout.addComponent(uuidLabel);

			Label reportDateLabel = new Label(
				I18nProperties.getPrefixCaption(SelfReportListEntryDto.I18N_PREFIX, SelfReportDto.REPORT_DATE) + ": "
					+ DateFormatHelper.formatDate(selfReportListEntryDto.getReportingDate()));
			topLeftLayout.addComponent(reportDateLabel);

			Label caseReferenceLabel = new Label(
				I18nProperties.getPrefixCaption(SelfReportListEntryDto.I18N_PREFIX, SelfReportDto.CASE_REFERENCE) + ": "
					+ selfReportListEntryDto.getCaseReference());
			topLeftLayout.addComponent(caseReferenceLabel);

			Label diseaseLabel = new Label(
				I18nProperties.getPrefixCaption(SelfReportListEntryDto.I18N_PREFIX, SelfReportDto.DISEASE) + ": "
					+ selfReportListEntryDto.getDisease());
			topLeftLayout.addComponent(diseaseLabel);

			Label dateOfTestLabel = new Label(
				I18nProperties.getPrefixCaption(SelfReportListEntryDto.I18N_PREFIX, SelfReportDto.DATE_OF_TEST) + ": "
					+ DateFormatHelper.formatDate(selfReportListEntryDto.getDateOfTest()));
			topLeftLayout.addComponent(dateOfTestLabel);
		}

		topLayout.addComponent(topLeftLayout);
		topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);
	}
}
