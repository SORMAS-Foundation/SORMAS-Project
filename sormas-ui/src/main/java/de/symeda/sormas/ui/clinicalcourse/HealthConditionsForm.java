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
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.RECURRENT_BRONCHIOLITIS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.SICKLE_CELL_DISEASE;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.TUBERCULOSIS;
import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.TUBERCULOSIS_INFECTION_YEAR;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
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
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class HealthConditionsForm extends AbstractEditForm<HealthConditionsDto> {

	private static final long serialVersionUID = 1L;

	private static final String HEALTH_CONDITIONS_HEADINGS_LOC = "healthConditionsHeadingLoc";
	private static final String CONFIDENTIAL_LABEL_LOC = "confidentialLabel";
	private static final String DIAGNOSIS_LABEL_LOC = "diagnosisLabel"; //TODO Dependency with Obinna
	private Disease disease;

	//@formatter:off
	public static final String TB_INFECTION_YEAR_LAYOUT = fluidRowLocs(6, "LBL_TUBERCULOSIS_INFECTION_YEAR", 6, TUBERCULOSIS_INFECTION_YEAR);
	public static final String TBA_LAYOUT = fluidRowLocs(6, "LBL_COMPLIANCE_WITH_TREATMENT", 6, COMPLIANCE_WITH_TREATMENT);
	private static final String HTML_LAYOUT =
			loc(HEALTH_CONDITIONS_HEADINGS_LOC) +
					fluidRow(
							fluidColumn(6, 0, locs(
									TUBERCULOSIS, PREVIOUS_TUBERCULOSIS_TREATMENT, ASPLENIA, HEPATITIS, DIABETES, IMMUNODEFICIENCY_OTHER_THAN_HIV,
									IMMUNODEFICIENCY_INCLUDING_HIV, HIV, HIV_ART, CONGENITAL_SYPHILIS, DOWN_SYNDROME,
									CHRONIC_LIVER_DISEASE, MALIGNANCY_CHEMOTHERAPY, RECURRENT_BRONCHIOLITIS)),
							fluidColumn(6, 0, locs(
									"TUBERCULOSIS_INFECTION_YEAR_LAYOUT","COMPLIANCE_WITH_TREATMENT_LAYOUT",CHRONIC_HEART_FAILURE, CHRONIC_PULMONARY_DISEASE, CHRONIC_KIDNEY_DISEASE,
									CHRONIC_NEUROLOGIC_CONDITION, CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION,
									OBESITY, CURRENT_SMOKER, FORMER_SMOKER, ASTHMA, SICKLE_CELL_DISEASE))
					) + loc(OTHER_CONDITIONS) + loc(CONFIDENTIAL_LABEL_LOC)+loc(DIAGNOSIS_LABEL_LOC);
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
		RECURRENT_BRONCHIOLITIS);

	public HealthConditionsForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(HealthConditionsDto.class, I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	public HealthConditionsForm(Disease disease, FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(HealthConditionsDto.class, I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);
		this.disease = disease;
		addFields();
	}

	@Override
	protected void addFields() {

		Label healthConditionsHeadingLabel = new Label(I18nProperties.getString(Strings.headingHealthConditions));
		healthConditionsHeadingLabel.addStyleName(H3);
		getContent().addComponent(healthConditionsHeadingLabel, HEALTH_CONDITIONS_HEADINGS_LOC);

		addFields(fieldsList);

		TextArea otherConditions = addField(OTHER_CONDITIONS, TextArea.class);
		otherConditions.setRows(6);
		otherConditions.setDescription(
			I18nProperties.getPrefixDescription(HealthConditionsDto.I18N_PREFIX, OTHER_CONDITIONS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		FieldHelper.setVisibleWhen(getFieldGroup(), HIV_ART, HIV, Arrays.asList(YesNoUnknown.YES), true);

		//Below requirement (showing the treatment year and its compliances only applicable for LUX)
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			if (Disease.TUBERCULOSIS.equals(disease)) {
				FieldHelper.setVisibleWhen(
					getFieldGroup(),
					Arrays.asList(PREVIOUS_TUBERCULOSIS_TREATMENT),
					TUBERCULOSIS,
					Arrays.asList(YesNoUnknown.YES),
					true);

				CustomLayout infectionYearLayout = new CustomLayout();
				infectionYearLayout.setTemplateContents(TB_INFECTION_YEAR_LAYOUT);

				// infection year label
				Label lblInfectionYear = new Label(I18nProperties.getCaption(Captions.HealthConditions_tuberculosisInfectionYear));
				infectionYearLayout.addComponent(lblInfectionYear, "LBL_TUBERCULOSIS_INFECTION_YEAR");
				getContent().addComponent(infectionYearLayout, "TUBERCULOSIS_INFECTION_YEAR_LAYOUT");

				// infection year combobox
				ComboBox tempInfectionYearCB = addField(infectionYearLayout, TUBERCULOSIS_INFECTION_YEAR, ComboBox.class);
				tempInfectionYearCB.addItems(DateHelper.getYearsToNow());
				tempInfectionYearCB.setNullSelectionAllowed(true);
				tempInfectionYearCB.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
				tempInfectionYearCB.setInputPrompt(I18nProperties.getString(Strings.year));
				tempInfectionYearCB.setCaption(null);
				infectionYearLayout.addComponent(tempInfectionYearCB, TUBERCULOSIS_INFECTION_YEAR);

				// validation for visibility
				fieldVisibilityCheck(getField(TUBERCULOSIS), tempInfectionYearCB, lblInfectionYear);

				// compliance with treatment layout
				CustomLayout complianceTreatmentLayout = new CustomLayout();
				complianceTreatmentLayout.setTemplateContents(TBA_LAYOUT);
				complianceTreatmentLayout.setStyleName("compliance-padding");
				complianceTreatmentLayout.setVisible(true);

				// compliance with treatment label
				Label lblComplianceWithTreatment = new Label(I18nProperties.getCaption(Captions.HealthConditions_complianceWithTreatment));
				complianceTreatmentLayout.addComponent(lblComplianceWithTreatment, "LBL_COMPLIANCE_WITH_TREATMENT");

				// compliance with treatment combobox
				ComboBox complianceWithTreatmentCB = addField(complianceTreatmentLayout, COMPLIANCE_WITH_TREATMENT, ComboBox.class);
				complianceWithTreatmentCB.setId(COMPLIANCE_WITH_TREATMENT);
				complianceWithTreatmentCB.addItems(ComplianceWithTreatment.values());
				complianceTreatmentLayout.addComponent(complianceWithTreatmentCB, COMPLIANCE_WITH_TREATMENT);
				getContent().addComponent(complianceTreatmentLayout, "COMPLIANCE_WITH_TREATMENT_LAYOUT");

				complianceWithTreatmentCB.setCaption(null);
				complianceWithTreatmentCB.setVisible(false);
				// compliance with treatment validation
				fieldVisibilityCheck(getField(PREVIOUS_TUBERCULOSIS_TREATMENT), complianceWithTreatmentCB, lblComplianceWithTreatment);

				// Only for TUBERCULOSIS disease below fields are visible,
				// for all other diseases all health conditions should visible.
				// This requirement is LUX + TB specific
				List<String> visibilityHealthConditions =
					Arrays.asList(TUBERCULOSIS, PREVIOUS_TUBERCULOSIS_TREATMENT, HIV, HIV_ART, OTHER_CONDITIONS);
				fieldVisibilityCheck(getFieldGroup(), visibilityHealthConditions);
			}
		}

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (Disease.TUBERCULOSIS.equals(disease)) {
			Field<?> other = getField(OTHER_CONDITIONS);
			if (other != null) {
				other.setReadOnly(false);
				other.setEnabled(true);

				if (other instanceof AbstractTextField) {
					((AbstractTextField) other).setInputPrompt("");
				}
			}
		}
	}

	/**
	 * visibility check of the fields based on disease(which is not belongs to its dto)
	 *
	 * @param fieldGroup
	 * @param visibilities
	 */
	private void fieldVisibilityCheck(FieldGroup fieldGroup, List<String> visibilities) {
		fieldGroup.getFields().stream().forEach(field -> {
			if (!visibilities.contains(field.getId())) {
				field.setVisible(false);
				field.clear();
			}
		});
	}

	/**
	 * Visibility check for the YesNoUnknown fields and their relevant labels
	 *
	 * @param input
	 * @param field
	 * @param label
	 */
	private void fieldVisibilityCheck(NullableOptionGroup input, Field field, Label label) {
		input.addValueChangeListener(event -> {
			Set<Object> set = (Set<Object>) event.getProperty().getValue();
			boolean visible = set.contains(YesNoUnknown.YES) ? true : false;
			field.setVisible(visible);
			label.setVisible(visible);
			field.clear();
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected <F extends Field> F addFieldToLayout(CustomLayout layout, String propertyId, F field) {
		field.addValueChangeListener(e -> {
			if (this.isModified()) {
				fireValueChange(false);
			}
		});

		return super.addFieldToLayout(layout, propertyId, field);
	}

	public void setInaccessible() {
		final HashSet<String> disableFields = new HashSet<>(fieldsList);
		if (disease != Disease.TUBERCULOSIS) {
			disableFields.add(OTHER_CONDITIONS);
		}
		final List<Field<?>> fields = disableFields.stream()
			.map(e -> getContent().getComponent(e))
			.filter(f -> f instanceof Field<?>)
			.map(f -> (Field<?>) f)
			.collect(Collectors.toList());
		fields.forEach(field -> {
			field.setVisible(false);
			getFieldGroup().unbind(field);
		});

		Label confidentialLabel = new Label(I18nProperties.getCaption(Captions.inaccessibleValue));
		confidentialLabel.addStyleName(CssStyles.INACCESSIBLE_LABEL);
		getContent().addComponent(confidentialLabel, CONFIDENTIAL_LABEL_LOC);
	}
}
