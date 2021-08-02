package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationInfo extends VerticalLayout {

	protected ImmunizationInfo(ImmunizationDto immunization, String heading) {
		setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(this, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label newCaseLabel = new Label(heading);
		CssStyles.style(newCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		addComponent(newCaseLabel);

		ImmunizationInfoLayout immunizationInfo = new ImmunizationInfoLayout(immunization);
		addComponent(immunizationInfo);
	}

	class ImmunizationInfoLayout extends HorizontalLayout {

		ImmunizationInfoLayout(ImmunizationDto immunization) {
			setSpacing(true);
			setSizeUndefined();

			Label meansOfImmunizationField = new Label();
			meansOfImmunizationField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.MEANS_OF_IMMUNIZATION));
			meansOfImmunizationField.setValue(immunization.getMeansOfImmunization().toString());
			meansOfImmunizationField.setWidthUndefined();
			addComponent(meansOfImmunizationField);

			Label managementStatusField = new Label();
			managementStatusField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.MANAGEMENT_STATUS));
			managementStatusField.setValue(immunization.getImmunizationManagementStatus().toString());
			managementStatusField.setWidthUndefined();
			addComponent(managementStatusField);

			Label immunizationStatusField = new Label();
			immunizationStatusField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_STATUS));
			immunizationStatusField.setValue(immunization.getImmunizationStatus().toString());
			immunizationStatusField.setWidthUndefined();
			addComponent(immunizationStatusField);

			Language userLanguage = I18nProperties.getUserLanguage();

			Label startDateField = new Label();
			startDateField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.START_DATE));
			startDateField.setValue(DateHelper.formatLocalDate(immunization.getStartDate(), userLanguage));
			startDateField.setWidthUndefined();
			addComponent(startDateField);

			Label endDateField = new Label();
			endDateField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.END_DATE));
			endDateField.setValue(DateHelper.formatLocalDate(immunization.getEndDate(), userLanguage));
			endDateField.setWidthUndefined();
			addComponent(endDateField);

			Label recoveryDateField = new Label();
			recoveryDateField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.RECOVERY_DATE));
			recoveryDateField.setValue(DateHelper.formatLocalDate(immunization.getRecoveryDate(), userLanguage));
			recoveryDateField.setWidthUndefined();
			addComponent(recoveryDateField);
		}
	}
}
