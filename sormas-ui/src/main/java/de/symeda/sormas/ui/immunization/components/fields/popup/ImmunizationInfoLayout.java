package de.symeda.sormas.ui.immunization.components.fields.popup;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Shows the start and end date of an immunization in the similar immunization popup.
 *
 * @deprecated Start and end date were superseded by the validity period fields (valid from / valid until) and are no longer
 *             shown in the immunization forms. Kept for now in case the similarity check is revised and needed again.
 */
@Deprecated
public class ImmunizationInfoLayout extends HorizontalLayout {

	ImmunizationInfoLayout(ImmunizationDto immunization) {
		setSpacing(true);
		setSizeUndefined();

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
	}
}
