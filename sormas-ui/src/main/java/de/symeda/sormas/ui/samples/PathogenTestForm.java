/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class PathogenTestForm extends AbstractEditForm<PathogenTestDto> {

	private static final long serialVersionUID = -1218707278398543154L;

	private static final String PATHOGEN_TEST_HEADING_LOC = "pathogenTestHeadingLoc";

	private static final String PRESCRIBER_HEADING_LOC = "prescriberHeading";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(PATHOGEN_TEST_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.REPORT_DATE, PathogenTestDto.VIA_LIMS) +
			fluidRowLocs(PathogenTestDto.EXTERNAL_ID, PathogenTestDto.EXTERNAL_ORDER_ID) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.PCR_TEST_SPECIFICATION, "") +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) +
			fluidRowLocs(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(6,PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 2,PathogenTestDto.PRELIMINARY) +
			fluidRowLocs(PathogenTestDto.RIFAMPICIN_RESISTANT, PathogenTestDto.ISONIAZID_RESISTANT, "", "") +
			fluidRowLocs(PathogenTestDto.TEST_SCALE, "") +
			fluidRowLocs(PathogenTestDto.STRAIN_CALL_STATUS, "") +
			fluidRowLocs(PathogenTestDto.SPECIE, "") +
			fluidRowLocs(PathogenTestDto.PATTERN_PROFILE, "") +
			fluidRowLocs(PathogenTestDto.DRUG_SUSCEPTIBILITY) +
			fluidRowLocs(4,PathogenTestDto.SEROTYPE, 4,PathogenTestDto.SEROTYPING_METHOD, 4,PathogenTestDto.SERO_TYPING_METHOD_TEXT) +
			fluidRowLocs(6,PathogenTestDto.SERO_GROUP_SPECIFICATION , 6, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT) +
			fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			fluidRowLocs(PathogenTestDto.CQ_VALUE, "") +
			fluidRowLocs(PathogenTestDto.CT_VALUE_E, PathogenTestDto.CT_VALUE_N) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_RDRP, PathogenTestDto.CT_VALUE_S) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_ORF_1, PathogenTestDto.CT_VALUE_RDRP_S) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT) +
			fluidRowLocs(PRESCRIBER_HEADING_LOC) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE, "") +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_FIRST_NAME, PathogenTestDto.PRESCRIBER_LAST_NAME) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_PHONE_NUMBER, "") +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_ADDRESS, PathogenTestDto.PRESCRIBER_POSTAL_CODE) +
			fluidRowLocs(PathogenTestDto.PRESCRIBER_CITY, PathogenTestDto.PRESCRIBER_COUNTRY) +
			fluidRowLocs(PathogenTestDto.DELETION_REASON) +
			fluidRowLocs(PathogenTestDto.OTHER_DELETION_REASON);
	//@formatter:on

	private SampleDto sample;
	private EnvironmentSampleDto environmentSample;
	private AbstractSampleForm sampleForm;
	private final int caseSampleCount;
	private final boolean create;

	private Label pathogenTestHeadingLabel;

	private ComboBox testTypeField;
	private ComboBox diseaseField;
	private ComboBox testResultField;
	private DrugSusceptibilityForm drugSusceptibilityField;
	private TextField testTypeTextField;
	private ComboBox pcrTestSpecification;
	private Disease disease;
	private TextField typingIdField;
	// List of tests that are used for serogrouping
	List<PathogenTestType> seroGrpTests = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING);

	public PathogenTestForm(
		AbstractSampleForm sampleForm,
		boolean create,
		int caseSampleCount,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Disease disease) {
		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sampleForm = sampleForm;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(SampleDto sample, boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {

		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sample = sample;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(EnvironmentSampleDto sample, boolean create, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {

		this(create, 0, isPseudonymized, inJurisdiction, disease);
		this.environmentSample = sample;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		super(
			PathogenTestDto.class,
			PathogenTestDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(create || inJurisdiction, !create && isPseudonymized));// Jurisdiction doesn't matter for creation forms  // Pseudonymization doesn't matter for creation forms

		this.caseSampleCount = caseSampleCount;
		this.create = create;
		setWidth(900, Unit.PIXELS);
	}

	private static void setCqValueVisibility(TextField cqValueField, PathogenTestType testType, PathogenTestResultType testResultType) {
		if (((testType == PathogenTestType.PCR_RT_PCR && testResultType == PathogenTestResultType.POSITIVE))
			|| testType == PathogenTestType.CQ_VALUE_DETECTION) {
			cqValueField.setVisible(true);
		} else {
			cqValueField.setVisible(false);
			cqValueField.clear();
		}
	}

	private void updateTuberculosisFieldSpecifications(PathogenTestType testType, Disease disease) {
		if ((FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG))) {
			boolean wasReadOnly = testResultField.isReadOnly();

			if (disease == Disease.TUBERCULOSIS && testType != null) {
				if (Arrays.asList(PathogenTestType.BEIJINGGENOTYPING, PathogenTestType.MIRU_PATTERN_CODE, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY)
					.contains(testType)) {
					if (wasReadOnly) {
						testResultField.setReadOnly(false);
					}
					testResultField.setValue(PathogenTestResultType.NOT_APPLICABLE);
					if (wasReadOnly) {
						testResultField.setReadOnly(true);
					}
				} else if (testType == PathogenTestType.SPOLIGOTYPING) {
					if (wasReadOnly) {
						testResultField.setReadOnly(false);
					}
					testResultField.setValue(PathogenTestResultType.POSITIVE);
					if (wasReadOnly) {
						testResultField.setReadOnly(true);
					}
				} else if (wasReadOnly) {
					// Field was read-only but no longer meets conditions for auto-set values
					testResultField.setReadOnly(false);
					testResultField.setValue(null);
				}
			} else if (wasReadOnly) {
				// Disease is not TB or testType is null, but field was read-only
				testResultField.setReadOnly(false);
				testResultField.setValue(null);
			}

			drugSusceptibilityField.updateFieldsVisibility(disease, testType);
		}
	}

	private Date getSampleDate() {
		if (sample != null) {
			return sample.getSampleDateTime();
		}
		if (sampleForm != null) {
			return (Date) sampleForm.getField(SampleDto.SAMPLE_DATE_TIME).getValue();
		}
		if (environmentSample != null) {
			return environmentSample.getSampleDateTime();
		}
		return null;
	}

	private SamplePurpose getSamplePurpose() {
		if (sample != null) {
			return sample.getSamplePurpose();
		}
		if (sampleForm != null) {
			return (SamplePurpose) sampleForm.getField(SampleDto.SAMPLE_PURPOSE).getValue();
		}
		return null;
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setHeading(String heading) {
		pathogenTestHeadingLabel.setValue(heading);
	}

	@Override
	public void setValue(PathogenTestDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		pcrTestSpecification.setValue(newFieldValue.getPcrTestSpecification());
		testTypeTextField.setValue(newFieldValue.getTestTypeText());
		typingIdField.setValue(newFieldValue.getTypingId());
	}

	@Override
	protected void addFields() {

		pathogenTestHeadingLabel = new Label();
		pathogenTestHeadingLabel.addStyleName(H3);
		getContent().addComponent(pathogenTestHeadingLabel, PATHOGEN_TEST_HEADING_LOC);

		addDateField(PathogenTestDto.REPORT_DATE, DateField.class, 0);
		addField(PathogenTestDto.VIA_LIMS);
		addField(PathogenTestDto.EXTERNAL_ID);
		addField(PathogenTestDto.EXTERNAL_ORDER_ID);
		testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		testTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		testTypeField.setImmediate(true);
		TextField seroTypingMethodText = addField(PathogenTestDto.SERO_TYPING_METHOD_TEXT);
		seroTypingMethodText.setVisible(false);
		pcrTestSpecification = addField(PathogenTestDto.PCR_TEST_SPECIFICATION, ComboBox.class);
		testTypeTextField = addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		FieldHelper.addSoftRequiredStyle(testTypeTextField);
		DateTimeField testDateField = addField(PathogenTestDto.TEST_DATE_TIME, DateTimeField.class);
		testDateField.addValidator(
			new DateComparisonValidator(
				testDateField,
				this::getSampleDate,
				false,
				false,
				I18nProperties.getValidationError(
					Validations.afterDateWithDate,
					testDateField.getCaption(),
					I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME),
					DateFormatHelper.formatDate(getSampleDate()))));
		ComboBox lab = addInfrastructureField(PathogenTestDto.LAB);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		TextField labDetails = addField(PathogenTestDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		typingIdField = addField(PathogenTestDto.TYPING_ID, TextField.class);
		typingIdField.setVisible(false);

		// Tested Desease or Tested Pathogen, depending on sample type
		diseaseField = addDiseaseField(PathogenTestDto.TESTED_DISEASE, true, create, false);
		addField(PathogenTestDto.TESTED_DISEASE_DETAILS, TextField.class);
		ComboBox diseaseVariantField = addCustomizableEnumField(PathogenTestDto.TESTED_DISEASE_VARIANT);
		diseaseVariantField.setNullSelectionAllowed(true);
		TextField diseaseVariantDetailsField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		ComboBox testedPathogenField = addCustomizableEnumField(PathogenTestDto.TESTED_PATHOGEN);
		TextField testedPathogenDetailsField = addField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, TextField.class);
		testedPathogenDetailsField.setVisible(false);
		FieldHelper.updateItems(testedPathogenField, FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, null));
		testedPathogenField.addValueChangeListener(e -> {
			Pathogen pathogen = (Pathogen) e.getProperty().getValue();
			if (pathogen != null && pathogen.isHasDetails()) {
				testedPathogenDetailsField.setVisible(true);
			} else {
				testedPathogenDetailsField.clear();
				testedPathogenDetailsField.setVisible(false);
			}
		});

		if (environmentSample == null) {
			diseaseField.setVisible(true);
			diseaseField.setRequired(true);

			testedPathogenField.setVisible(false);
			testedPathogenField.setRequired(false);
		} else {
			diseaseField.setVisible(false);
			diseaseField.setRequired(false);

			testedPathogenField.setVisible(true);
			testedPathogenField.setRequired(true);
		}

		testResultField = addField(PathogenTestDto.TEST_RESULT, ComboBox.class);
		testResultField.removeItem(PathogenTestResultType.NOT_DONE);

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			testResultField.removeItem(PathogenTestResultType.NOT_APPLICABLE);
		}
		TextField seroTypeTF = addField(PathogenTestDto.SEROTYPE, TextField.class);

		NullableOptionGroup rifampicinResistantField = addField(PathogenTestDto.RIFAMPICIN_RESISTANT, NullableOptionGroup.class);
		rifampicinResistantField.setVisible(false);

		NullableOptionGroup isoniazidResistantField = addField(PathogenTestDto.ISONIAZID_RESISTANT, NullableOptionGroup.class);
		isoniazidResistantField.setVisible(false);

		ComboBox testScaleField = addField(PathogenTestDto.TEST_SCALE, ComboBox.class);
		testScaleField.setVisible(false);

		ComboBox strainCallStatusField = addField(PathogenTestDto.STRAIN_CALL_STATUS, ComboBox.class);
		strainCallStatusField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		strainCallStatusField.setVisible(false);

		ComboBox specieField = addField(PathogenTestDto.SPECIE, ComboBox.class);
		specieField.setVisible(false);

		TextField patternProfileField = addField(PathogenTestDto.PATTERN_PROFILE, TextField.class);
		patternProfileField.setVisible(false);

		drugSusceptibilityField = (DrugSusceptibilityForm) addField(
			PathogenTestDto.DRUG_SUSCEPTIBILITY,
			new DrugSusceptibilityForm(
				FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
				UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale())));
		drugSusceptibilityField.setCaption(null);
		drugSusceptibilityField.setVisible(false);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			//tuberculosis-pcr test specification
			Map<Object, List<Object>> tuberculosisPcrDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR));
					put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.RIFAMPICIN_RESISTANT, tuberculosisPcrDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.RIFAMPICIN_RESISTANT, tuberculosisPcrDependencies);
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.ISONIAZID_RESISTANT, tuberculosisPcrDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.ISONIAZID_RESISTANT, tuberculosisPcrDependencies);

			//tuberculosis-microscopy test specification
			Map<Object, List<Object>> tuberculosisMicroscopyDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MICROSCOPY));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TEST_SCALE, tuberculosisMicroscopyDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.TEST_SCALE, tuberculosisMicroscopyDependencies);

			//tuberculosis-beijinggenotyping test specification
			Map<Object, List<Object>> tuberculosisBeijingDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.BEIJINGGENOTYPING));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.STRAIN_CALL_STATUS, tuberculosisBeijingDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.STRAIN_CALL_STATUS, tuberculosisBeijingDependencies);

			//tuberculosis-spoligotyping test specification
			Map<Object, List<Object>> tuberculosisSpoligotypingDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.SPOLIGOTYPING));
					put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SPECIE, tuberculosisSpoligotypingDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.SPECIE, tuberculosisSpoligotypingDependencies);

			//tuberculosis-miru-code test specification
			Map<Object, List<Object>> tuberculosisMiruCodeDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies);

			//tuberculosis-antibiotic test specification
			Map<Object, List<Object>> tuberculosisAntibioticDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.DRUG_SUSCEPTIBILITY, tuberculosisAntibioticDependencies, true);

			//test result - read only
			Map<Object, List<Object>> tuberculosisTestResultReadOnlyDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS));
					put(
						PathogenTestDto.TEST_TYPE,
						Arrays.asList(
							PathogenTestType.BEIJINGGENOTYPING,
							PathogenTestType.SPOLIGOTYPING,
							PathogenTestType.MIRU_PATTERN_CODE,
							PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
				}
			};
			FieldHelper.setReadOnlyWhen(getFieldGroup(), PathogenTestDto.TEST_RESULT, tuberculosisTestResultReadOnlyDependencies, false, false);
		}

		seroTypeTF.setVisible(false);
		ComboBox seroTypeMetCB = addField(PathogenTestDto.SEROTYPING_METHOD, ComboBox.class);
		seroTypeMetCB.setVisible(false);
		ComboBox seroGrpSepcCB = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION, ComboBox.class);
		seroGrpSepcCB.setVisible(false);
		TextField seroGrpSpecTxt = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, TextField.class);
		TextField cqValueField = addField(FieldConfiguration.withConversionError(PathogenTestDto.CQ_VALUE, Validations.onlyNumbersAllowed));
		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			cqValueField.setVisible(false);
		}

		addFields(
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_E, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_N, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_RDRP, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_S, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_ORF_1, Validations.onlyNumbersAllowed),
			FieldConfiguration.withConversionError(PathogenTestDto.CT_VALUE_RDRP_S, Validations.onlyNumbersAllowed));

		setVisibleClear(
			false,
			PathogenTestDto.CQ_VALUE,
			PathogenTestDto.CT_VALUE_E,
			PathogenTestDto.CT_VALUE_N,
			PathogenTestDto.CT_VALUE_RDRP,
			PathogenTestDto.CT_VALUE_S,
			PathogenTestDto.CT_VALUE_ORF_1,
			PathogenTestDto.CT_VALUE_RDRP_S);
		NullableOptionGroup testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, NullableOptionGroup.class);
		testResultVerifiedField.setRequired(true);
		addField(PathogenTestDto.PRELIMINARY).addStyleName(CssStyles.VSPACE_4);
		CheckBox fourFoldIncrease = addField(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
		CssStyles.style(fourFoldIncrease, VSPACE_3, VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);

		addField(PathogenTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(6);

		addFields(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE, PathogenTestDto.PRESCRIBER_FIRST_NAME, PathogenTestDto.PRESCRIBER_LAST_NAME);
		TextField proscriberPhoneField = addField(PathogenTestDto.PRESCRIBER_PHONE_NUMBER, TextField.class);
		proscriberPhoneField.addValidator(
			new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, proscriberPhoneField.getCaption())));

		addFields(PathogenTestDto.PRESCRIBER_ADDRESS, PathogenTestDto.PRESCRIBER_POSTAL_CODE, PathogenTestDto.PRESCRIBER_CITY);
		ComboBox prescriberCountrField = addInfrastructureField(PathogenTestDto.PRESCRIBER_COUNTRY);
		FieldHelper.updateItems(prescriberCountrField, FacadeProvider.getCountryFacade().getAllActiveAsReference());

		addField(PathogenTestDto.DELETION_REASON);
		addField(PathogenTestDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, PathogenTestDto.DELETION_REASON, PathogenTestDto.OTHER_DELETION_REASON);

		initializeAccessAndAllowedAccesses();
		initializeVisibilitiesAndAllowedVisibilities();

		pcrTestSpecification.setVisible(false);

		if (isVisibleAllowed(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE)) {
			Label prescriberHeadingLabel = new Label(I18nProperties.getCaption(Captions.PathogenTest_prescriber));
			prescriberHeadingLabel.addStyleName(H3);
			getContent().addComponent(prescriberHeadingLabel, PRESCRIBER_HEADING_LOC);
		}

		Map<Object, List<Object>> pcrTestSpecificationVisibilityDependencies = new HashMap<>() {

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CORONAVIRUS));
				put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PCR_TEST_SPECIFICATION, pcrTestSpecificationVisibilityDependencies, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TEST_TYPE_TEXT,
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TESTED_DISEASE_DETAILS,
			PathogenTestDto.TESTED_DISEASE,
			Arrays.asList(Disease.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.TYPING_ID,
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.DNA_MICROARRAY, PathogenTestType.SEQUENCING),
			true);

		Map<Object, List<Object>> serotypeVisibilityDependencies = new HashMap<Object, List<Object>>() {

			private static final long serialVersionUID = 1967952323596082247L;

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CSM));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SEROTYPE, serotypeVisibilityDependencies, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_TYPING_METHOD_TEXT,
			PathogenTestDto.SEROTYPING_METHOD,
			SerotypingMethod.OTHER,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT,
			PathogenTestDto.SERO_GROUP_SPECIFICATION,
			SeroGroupSpecification.OTHER,
			true);

		Consumer<Disease> updateDiseaseVariantField = disease -> {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField.setVisible(
				disease != null && isVisibleAllowed(PathogenTestDto.TESTED_DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		};

		updateDiseaseVariantField.accept((Disease) diseaseField.getValue());

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease latestDisease = (Disease) valueChangeEvent.getProperty().getValue();
			// If the disease changed, test type field should be updated with its respective test types
			if (latestDisease != disease) {
				testTypeField.clear();
			}
			disease = latestDisease;
			updateDiseaseVariantField.accept(disease);

			FieldHelper.updateItems(
				testTypeField,
				Arrays.asList(PathogenTestType.values()),
				FieldVisibilityCheckers.withDisease(disease),
				PathogenTestType.class);

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				FieldHelper.updateItems(
					strainCallStatusField,
					Arrays.asList(PathogenStrainCallStatus.values()),
					FieldVisibilityCheckers.withDisease(disease),
					PathogenStrainCallStatus.class);

				updateTuberculosisFieldSpecifications((PathogenTestType) testTypeField.getValue(), disease);
			}
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			if (testType != null) {
				if (testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY) {
					fourFoldIncrease.setVisible(true);
					fourFoldIncrease.setEnabled(caseSampleCount >= 2);
				} else {
					fourFoldIncrease.setVisible(false);
					fourFoldIncrease.setEnabled(false);
				}
				// If disease is IMI or IPI and test type is serogrouping, then test result is set to positive and not editable
				if (seroGrpTests.contains(testType)) {
					testResultField.setValue(PathogenTestResultType.POSITIVE);
				} else {
					testResultField.clear();
				}
				seroTypeMetCB.setVisible(disease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION && PathogenTestType.SEROGROUPING.equals(testType));
				seroTypeTF.setVisible(disease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION && seroGrpTests.contains(testType));
				seroGrpSepcCB.setVisible(disease == Disease.INVASIVE_MENINGOCOCCAL_INFECTION && seroGrpTests.contains(testType));
				testResultField.setEnabled(!seroGrpTests.contains(testType));
				setVisibleClear(
					PathogenTestType.PCR_RT_PCR == testType,
					PathogenTestDto.CQ_VALUE,
					PathogenTestDto.CT_VALUE_E,
					PathogenTestDto.CT_VALUE_N,
					PathogenTestDto.CT_VALUE_RDRP,
					PathogenTestDto.CT_VALUE_S,
					PathogenTestDto.CT_VALUE_ORF_1,
					PathogenTestDto.CT_VALUE_RDRP_S);
			} else {
				setVisibleClear(
					testTypeField.getValue() != null,
					PathogenTestDto.SEROTYPE,
					PathogenTestDto.SEROTYPING_METHOD,
					PathogenTestDto.SERO_GROUP_SPECIFICATION);
				testResultField.clear();
				testResultField.setEnabled(true);
			}

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				updateTuberculosisFieldSpecifications(testType, (Disease) diseaseField.getValue());
			}
		});
		lab.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null
				&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
				labDetails.setVisible(true);
				labDetails.setRequired(isEditableAllowed(labDetails));
			} else {
				labDetails.setVisible(false);
				labDetails.setRequired(false);
				labDetails.clear();
			}
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			setCqValueVisibility(cqValueField, testType, (PathogenTestResultType) testResultField.getValue());
		});

		testResultField.addValueChangeListener(e -> {
			PathogenTestResultType testResult = (PathogenTestResultType) e.getProperty().getValue();
			setCqValueVisibility(cqValueField, (PathogenTestType) testTypeField.getValue(), testResult);
		});

		if (SamplePurpose.INTERNAL.equals(getSamplePurpose())) { // this only works for already saved samples
			setRequired(true, PathogenTestDto.LAB);
		}
		setRequired(true, PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_RESULT);
	}
}
