package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public class ImmunizationInfoLayout extends HorizontalLayout {

	ImmunizationInfoLayout(ImmunizationDto immunization) {
		setSpacing(true);
		setSizeUndefined();

		Label uuidField = new Label();
		uuidField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.UUID));
		uuidField.setValue(DataHelper.getShortUuid(immunization.getUuid()));
		uuidField.setWidthUndefined();
		addComponent(uuidField);

		Label meansOfImmunizationField = new Label();
		meansOfImmunizationField.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.MEANS_OF_IMMUNIZATION));
		meansOfImmunizationField.setValue(immunization.getMeansOfImmunization().toString());
		meansOfImmunizationField.setWidthUndefined();
		addComponent(meansOfImmunizationField);

		Label managementStatusField = new Label();
		managementStatusField
			.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS));
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
