package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.ASPLENIA;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.ASTHMA;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CHRONIC_HEART_FAILURE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CHRONIC_KIDNEY_DISEASE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CHRONIC_LIVER_DISEASE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CHRONIC_NEUROLOGIC_CONDITION;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CHRONIC_PULMONARY_DISEASE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.COMPLIANCE_WITH_TREATMENT;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CONGENITAL_SYPHILIS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.CURRENT_SMOKER;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.DIABETES;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.DOWN_SYNDROME;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.FORMER_SMOKER;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.HEPATITIS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.HIV;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.HIV_ART;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.I18N_PREFIX;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.IMMUNODEFICIENCY_INCLUDING_HIV;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.IMMUNODEFICIENCY_OTHER_THAN_HIV;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.MALIGNANCY_CHEMOTHERAPY;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.OBESITY;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.OTHER_CONDITIONS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.PREVIOUS_TUBERCULOSIS_TREATMENT;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.SICKLE_CELL_DISEASE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.TUBERCULOSIS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.TUBERCULOSIS_INFECTED_YEAR;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class HealthConditionsForm extends AbstractEditForm<HealthConditionsDto> {

	private static final long serialVersionUID = 1L;

	private static final String HEALTH_CONDITIONS_HEADINGS_LOC = "healthConditionsHeadingLoc";
	private static final String CONFIDENTIAL_LABEL_LOC = "confidentialLabel";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(HEALTH_CONDITIONS_HEADINGS_LOC) +
					fluidRow(
							fluidColumn(6, 0, locs(
									TUBERCULOSIS, PREVIOUS_TUBERCULOSIS_TREATMENT, ASPLENIA, HEPATITIS, DIABETES, IMMUNODEFICIENCY_OTHER_THAN_HIV,
									IMMUNODEFICIENCY_INCLUDING_HIV, HIV, HIV_ART, CONGENITAL_SYPHILIS, DOWN_SYNDROME,
									CHRONIC_LIVER_DISEASE, MALIGNANCY_CHEMOTHERAPY)),
							fluidColumn(6, 0, locs(
									CHRONIC_HEART_FAILURE, CHRONIC_PULMONARY_DISEASE, CHRONIC_KIDNEY_DISEASE,
									CHRONIC_NEUROLOGIC_CONDITION, CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION,
									OBESITY, CURRENT_SMOKER, FORMER_SMOKER, ASTHMA, SICKLE_CELL_DISEASE, TUBERCULOSIS_INFECTED_YEAR, COMPLIANCE_WITH_TREATMENT))
					) + loc(OTHER_CONDITIONS) + loc(CONFIDENTIAL_LABEL_LOC);
	//@formatter:on

	private static final List<String> fieldsList = List.of(
		TUBERCULOSIS,
		PREVIOUS_TUBERCULOSIS_TREATMENT,
		ASPLENIA,
		HEPATITIS,
		DIABETES,
		HIV,
		HIV_ART,
		CHRONIC_LIVER_DISEASE,
		MALIGNANCY_CHEMOTHERAPY,
		CHRONIC_HEART_FAILURE,
		CHRONIC_PULMONARY_DISEASE,
		CHRONIC_KIDNEY_DISEASE,
		CHRONIC_NEUROLOGIC_CONDITION,
		DOWN_SYNDROME,
		CONGENITAL_SYPHILIS,
		IMMUNODEFICIENCY_OTHER_THAN_HIV,
		CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION,
		OBESITY,
		CURRENT_SMOKER,
		FORMER_SMOKER,
		ASTHMA,
		SICKLE_CELL_DISEASE,
		IMMUNODEFICIENCY_INCLUDING_HIV,
		COMPLIANCE_WITH_TREATMENT);

	public HealthConditionsForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(HealthConditionsDto.class, I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected void addFields() {

		Label healthConditionsHeadingLabel = new Label(I18nProperties.getString(Strings.headingHealthConditions));
		healthConditionsHeadingLabel.addStyleName(H3);
		getContent().addComponent(healthConditionsHeadingLabel, HEALTH_CONDITIONS_HEADINGS_LOC);

		addFields(fieldsList);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {

			ComboBox tbInfectedYear = addField(HealthConditionsDto.TUBERCULOSIS_INFECTED_YEAR, ComboBox.class);
			tbInfectedYear
				.setCaption(I18nProperties.getPrefixCaption(HealthConditionsDto.I18N_PREFIX, HealthConditionsDto.TUBERCULOSIS_INFECTED_YEAR));
			tbInfectedYear.setNullSelectionAllowed(true);
			tbInfectedYear.addItems(DateHelper.getYearsToNow());
			tbInfectedYear.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
			tbInfectedYear.setInputPrompt(I18nProperties.getString(Strings.year));

			FieldHelper.setVisibleWhen(getFieldGroup(), TUBERCULOSIS_INFECTED_YEAR, TUBERCULOSIS, Arrays.asList(YesNoUnknown.YES), true);
			FieldHelper.setVisibleWhen(getFieldGroup(), PREVIOUS_TUBERCULOSIS_TREATMENT, TUBERCULOSIS, Arrays.asList(YesNoUnknown.YES), true);
			FieldHelper
				.setVisibleWhen(getFieldGroup(), COMPLIANCE_WITH_TREATMENT, PREVIOUS_TUBERCULOSIS_TREATMENT, Arrays.asList(YesNoUnknown.YES), true);

			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				PREVIOUS_TUBERCULOSIS_TREATMENT,
				Arrays.asList(COMPLIANCE_WITH_TREATMENT),
				Arrays.asList(PREVIOUS_TUBERCULOSIS_TREATMENT));
		}

		TextArea otherConditions = addField(OTHER_CONDITIONS, TextArea.class);
		otherConditions.setRows(6);
		otherConditions.setDescription(
			I18nProperties.getPrefixDescription(HealthConditionsDto.I18N_PREFIX, OTHER_CONDITIONS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		FieldHelper.setVisibleWhen(getFieldGroup(), HIV_ART, HIV, Arrays.asList(YesNoUnknown.YES), true);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected <F extends Field> F addFieldToLayout(CustomLayout layout, String propertyId, F field) {
		field.addValueChangeListener(e -> fireValueChange(false));

		return super.addFieldToLayout(layout, propertyId, field);
	}

	public void setInaccessible() {
		fieldsList.stream().forEach(field -> {
			getContent().getComponent(field).setVisible(false);
		});
		getContent().getComponent(OTHER_CONDITIONS).setVisible(false);
		Label confidentialLabel = new Label(I18nProperties.getCaption(Captions.inaccessibleValue));
		confidentialLabel.addStyleName(CssStyles.INACCESSIBLE_LABEL);
		getContent().addComponent(confidentialLabel, CONFIDENTIAL_LABEL_LOC);
	}
}
