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

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.ConverterUtil;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.GenoTypeResult;
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

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
			fluidRowLocs(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(6,PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 2,PathogenTestDto.PRELIMINARY) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) +
			fluidRowLocs(PathogenTestDto.RIFAMPICIN_RESISTANT, PathogenTestDto.ISONIAZID_RESISTANT, "", "") +
			fluidRowLocs(PathogenTestDto.TEST_SCALE, "") +
			fluidRowLocs(PathogenTestDto.STRAIN_CALL_STATUS, "") +
			fluidRowLocs(PathogenTestDto.SPECIE, "") +
			fluidRowLocs(PathogenTestDto.PATTERN_PROFILE, "") +
			fluidRowLocs(PathogenTestDto.DRUG_SUSCEPTIBILITY) +
			fluidRowLocs(4,PathogenTestDto.SEROTYPE, 4,PathogenTestDto.SEROTYPING_METHOD, 4,PathogenTestDto.SERO_TYPING_METHOD_TEXT) +
			fluidRowLocs(6,PathogenTestDto.SERO_GROUP_SPECIFICATION , 6, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT) +
			fluidRowLocs(4,PathogenTestDto.GENOTYPE_RESULT,6, PathogenTestDto.GENOTYPE_RESULT_TEXT) +
			fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			fluidRowLocs(PathogenTestDto.CQ_VALUE, "") +
			fluidRowLocs(PathogenTestDto.CT_VALUE_E, PathogenTestDto.CT_VALUE_N) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_RDRP, PathogenTestDto.CT_VALUE_S) +
			fluidRowLocs(PathogenTestDto.CT_VALUE_ORF_1, PathogenTestDto.CT_VALUE_RDRP_S) +
			fluidRowLocs(PathogenTestDto.TUBE_NIL, PathogenTestDto.TUBE_NIL_GT10) +
			fluidRowLocs(PathogenTestDto.TUBE_AG_TB1, PathogenTestDto.TUBE_AG_TB1_GT10) +
			fluidRowLocs(PathogenTestDto.TUBE_AG_TB2, PathogenTestDto.TUBE_AG_TB2_GT10) +
			fluidRowLocs(PathogenTestDto.TUBE_MITOGENE, PathogenTestDto.TUBE_MITOGENE_GT10) +
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

	// map to decide the result type field value and enable/disable state
	public static final Map<Disease, ArrayList<PathogenTestType>> RESULT_FIELD_DECISION_MAP = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(
				Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
				new ArrayList<>(
					List.of(
						PathogenTestType.SEROGROUPING,
						PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
						PathogenTestType.SLIDE_AGGLUTINATION,
						PathogenTestType.WHOLE_GENOME_SEQUENCING,
						PathogenTestType.SEQUENCING,
						PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY)));
			put(
				Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
				new ArrayList<>(
					List.of(
						PathogenTestType.SEROGROUPING,
						PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
						PathogenTestType.SLIDE_AGGLUTINATION,
						PathogenTestType.WHOLE_GENOME_SEQUENCING,
						PathogenTestType.SEQUENCING,
						PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY)));
			put(Disease.MEASLES, new ArrayList<>(List.of(PathogenTestType.GENOTYPING)));
			put(Disease.RESPIRATORY_SYNCYTIAL_VIRUS, new ArrayList<>(List.of(PathogenTestType.SEQUENCING, PathogenTestType.WHOLE_GENOME_SEQUENCING)));
			put(Disease.INFLUENZA, new ArrayList<>(List.of(PathogenTestType.ISOLATION)));
			put(Disease.CRYPTOSPORIDIOSIS, new ArrayList<>(List.of(PathogenTestType.GENOTYPING)));
		}
	});

	public static final Map<Object, List<Object>> RIFAMPICIN_RESISTANT_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.LATENT_TUBERCULOSIS, Disease.TUBERCULOSIS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.PCR_RT_PCR)));
			put(PathogenTestDto.TEST_RESULT, Collections.unmodifiableList(Arrays.asList(PathogenTestResultType.POSITIVE)));
		}
	});

	public static final Map<Object, List<Object>> TEST_SCALE_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.LATENT_TUBERCULOSIS, Disease.TUBERCULOSIS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.MICROSCOPY)));
		}
	});

	public static final Map<Object, List<Object>> STRAIN_CALL_STATUS_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.LATENT_TUBERCULOSIS, Disease.TUBERCULOSIS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.BEIJINGGENOTYPING)));
		}
	});

	public static final Map<Object, List<Object>> SPECIE_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.LATENT_TUBERCULOSIS, Disease.TUBERCULOSIS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.SPOLIGOTYPING)));
			put(PathogenTestDto.TEST_RESULT, Collections.unmodifiableList(Arrays.asList(PathogenTestResultType.POSITIVE)));
		}
	});

	public static final Map<Object, List<Object>> PATTERN_PROFILE_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.LATENT_TUBERCULOSIS, Disease.TUBERCULOSIS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE)));
		}
	});

	public static final Map<Object, List<Object>> PCR_TEST_SPECIFICATION_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.CORONAVIRUS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.PCR_RT_PCR)));
		}
	});

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
	private ComboBox specieField;
	private ComboBox genoTypingCB;
	private TextField genoTypingResultTextTF;

	private ComboBox seroGrpSepcCB;
	private TextField seroGrpSpecTxt;

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

	private static void setCqValueVisibility(
		ComboBox diseaseField,
		TextField cqValueField,
		PathogenTestType testType,
		PathogenTestResultType testResultType) {

		if (diseaseField.getValue() == null || !List.of(Disease.TUBERCULOSIS).contains((Disease) diseaseField.getValue())) {
			if (((testType == PathogenTestType.PCR_RT_PCR && testResultType == PathogenTestResultType.POSITIVE))
				|| testType == PathogenTestType.CQ_VALUE_DETECTION) {
				cqValueField.setVisible(true);
			} else {
				cqValueField.setVisible(false);
				cqValueField.clear();
			}
		}
	}

	private void updateDrugSusceptibilityFieldSpecifications(PathogenTestType testType, Disease disease) {

		// Hide or show drug susceptibility fields based on the disease and test type (if disease is null then drug susceptibility should be hidden)
		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.updateFieldsVisibility(disease, testType);
		}

		// if the disease is null, means that we are dealing with a environment sample
		// and we don't need to update the result field
		if (disease == null) {
			return;
		}

		// if the test type is null we just clear the result field
		if (testType == null) {
			testResultField.setValue(null);
			return;
		}

		// FIXME: why was this here originally?
		// TODO: move this to another place, should be in listeners for disease/testType.

		if ((FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG))) {

			// testResult=NOT_APPLICABLE for Tuberculosis diseases, test types BEIJINGGENOTYPING,MIRU_PATTERN_CODE,ANTIBIOTIC_SUSCEPTIBILITY
			if ((disease == Disease.LATENT_TUBERCULOSIS || disease == Disease.TUBERCULOSIS)
				&& (testType == PathogenTestType.BEIJINGGENOTYPING
					|| testType == PathogenTestType.MIRU_PATTERN_CODE
					|| testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY)) {
				testResultField.setValue(PathogenTestResultType.NOT_APPLICABLE);
			}

			// testResult=POSITIVE for Tuberculosis diseases, test type SPOLIGOTYPING
			if ((disease == Disease.LATENT_TUBERCULOSIS || disease == Disease.TUBERCULOSIS) && (testType == PathogenTestType.SPOLIGOTYPING)) {
				testResultField.setValue(PathogenTestResultType.POSITIVE);
			}

			// testResult=POSITIVE for IMI and IPI, test type ANTIBIOTIC_SUSCEPTIBILITY
			if ((disease == Disease.INVASIVE_MENINGOCOCCAL_INFECTION || disease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION)
				&& testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				testResultField.setValue(PathogenTestResultType.POSITIVE);
			}
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
		testTypeField.setValue(newFieldValue.getTestType());
		pcrTestSpecification.setValue(newFieldValue.getPcrTestSpecification());
		testTypeTextField.setValue(newFieldValue.getTestTypeText());
		if (!testResultField.isReadOnly()) {
			testResultField.setValue(newFieldValue.getTestResult());
		}
		typingIdField.setValue(newFieldValue.getTypingId());
		specieField.setValue(newFieldValue.getSpecie());
		if (!genoTypingCB.isReadOnly()) {
			genoTypingCB.setValue(newFieldValue.getGenoTypeResult());

		}

		if (!genoTypingResultTextTF.isReadOnly()) {
			genoTypingResultTextTF.setValue(newFieldValue.getGenoTypeResultText());
		}

		if (!seroGrpSepcCB.isReadOnly()) {
			seroGrpSepcCB.setValue(newFieldValue.getSeroGroupSpecification());
		}

		if (!seroGrpSpecTxt.isReadOnly()) {
			seroGrpSpecTxt.setValue(newFieldValue.getSeroGroupSpecificationText());
		}

		drugSusceptibilityField.forceUpdateDrugSusceptibilityFields();
		markAsDirty();
	}

	@Override
	protected void addFields() {

		pathogenTestHeadingLabel = new Label();
		pathogenTestHeadingLabel.addStyleName(H3);
		getContent().addComponent(pathogenTestHeadingLabel, PATHOGEN_TEST_HEADING_LOC);

		addDateField(PathogenTestDto.REPORT_DATE, DateField.class, 0);
		CheckBox viaLimsField = addField(PathogenTestDto.VIA_LIMS);
		addField(PathogenTestDto.EXTERNAL_ID);
		addField(PathogenTestDto.EXTERNAL_ORDER_ID);
		testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		testTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		testTypeField.setImmediate(true);
		TextField seroTypingMethodText = addField(PathogenTestDto.SERO_TYPING_METHOD_TEXT);
		seroTypingMethodText.setVisible(false);
		pcrTestSpecification = addField(PathogenTestDto.PCR_TEST_SPECIFICATION, ComboBox.class);
		testTypeTextField = addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		DateTimeField testDateField = addField(PathogenTestDto.TEST_DATE_TIME, DateTimeField.class);
		testDateField.removeAllValidators();
		testDateField.addValidator(
			new DateComparisonValidator(
				testDateField,
				this::getSampleDate,
				false,
				false,
				true,
				I18nProperties.getValidationError(
					Validations.afterDateWithDate,
					testDateField.getCaption(),
					I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME),
					DateFormatHelper.formatDate(getSampleDate()))));
		testDateField.addValueChangeListener(e -> {
			boolean hasTime =
				getSampleDate() != null && !getSampleDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalTime.MIDNIGHT);

			if (!hasTime) {
				return;
			}

			testDateField.removeAllValidators();
			testDateField.addValidator(
				new DateComparisonValidator(
					testDateField,
					this::getSampleDate,
					false,
					false,
					false,
					I18nProperties.getValidationError(
						Validations.afterDateWithDate,
						testDateField.getCaption(),
						I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME),
						DateFormatHelper.formatLocalDateTime(getSampleDate()))));

		});
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
		diseaseVariantField.setVisible(false);
		TextField diseaseVariantDetailsField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);
		if (DiseaseHelper.SUBTYPE_ALLOWED_DISEASES.contains(disease)) {
			diseaseVariantField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariant));
			diseaseVariantDetailsField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariantDetails));
		}
		genoTypingCB = addField(PathogenTestDto.GENOTYPE_RESULT, ComboBox.class);
		genoTypingCB.setVisible(true);
		genoTypingResultTextTF = addField(PathogenTestDto.GENOTYPE_RESULT_TEXT, TextField.class);
		genoTypingResultTextTF.setVisible(true);

		ComboBox testedPathogenField = addCustomizableEnumField(PathogenTestDto.TESTED_PATHOGEN);
		TextField testedPathogenDetailsField = addField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, TextField.class);
		testedPathogenDetailsField.setVisible(false);
		FieldHelper
			.updateItems(testedPathogenField, FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, disease));
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

		specieField = addField(PathogenTestDto.SPECIE, ComboBox.class);
		specieField.setVisible(false);

		TextField patternProfileField = addField(PathogenTestDto.PATTERN_PROFILE, TextField.class);
		patternProfileField.setVisible(false);

		drugSusceptibilityField = (DrugSusceptibilityForm) addField(
			PathogenTestDto.DRUG_SUSCEPTIBILITY,
			new DrugSusceptibilityForm(
				FieldVisibilityCheckers.getNoop(),
				UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale())));
		drugSusceptibilityField.setCaption(null);
		//drugSusceptibilityField.setVisible(false);
		addToVisibleAllowedFields(drugSusceptibilityField);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			//tuberculosis-pcr test specification
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.RIFAMPICIN_RESISTANT, RIFAMPICIN_RESISTANT_VISIBILITY_CONDITIONS, true);

			//tuberculosis-microscopy test specification
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TEST_SCALE, TEST_SCALE_VISIBILITY_CONDITIONS, true);

			//tuberculosis-beijinggenotyping test specification
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.STRAIN_CALL_STATUS, STRAIN_CALL_STATUS_VISIBILITY_CONDITIONS, true);

			//tuberculosis-spoligotyping test specification
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SPECIE, SPECIE_VISIBILITY_CONDITIONS, true);

			//tuberculosis-miru-code test specification
			Map<Object, List<Object>> tuberculosisMiruCodeDependencies = new HashMap<>() {

				{
					put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS, Disease.LATENT_TUBERCULOSIS));
					put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE));
				}
			};
			FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies, true);
			//FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, tuberculosisMiruCodeDependencies);
		}

		seroTypeTF.setVisible(false);

		ComboBox seroTypeMetCB = addField(PathogenTestDto.SEROTYPING_METHOD, ComboBox.class);
		seroTypeMetCB.setVisible(false);
		seroGrpSepcCB = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION, ComboBox.class);
		seroGrpSepcCB.setVisible(false);
		seroGrpSpecTxt = addField(PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, TextField.class);

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

		//@formatter:off
		addFields(
			FieldConfiguration.builder(PathogenTestDto.TUBE_NIL)
				.validationMessageProperty(Validations.onlyNumbersAllowed)
				.valueChangeListener(new TuberculosisIGRAInputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_NIL,PathogenTestDto.TUBE_NIL_GT10))
				.build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB1)
				.validationMessageProperty(Validations.onlyNumbersAllowed)
				.valueChangeListener(new TuberculosisIGRAInputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_AG_TB1,PathogenTestDto.TUBE_AG_TB1_GT10)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB2)
				.validationMessageProperty(Validations.onlyNumbersAllowed)
				.valueChangeListener(new TuberculosisIGRAInputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_AG_TB2,PathogenTestDto.TUBE_AG_TB2_GT10)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_MITOGENE)
				.validationMessageProperty(Validations.onlyNumbersAllowed)
				.valueChangeListener(new TuberculosisIGRAInputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_MITOGENE,PathogenTestDto.TUBE_MITOGENE_GT10)).build());
		//@formatter:on

		//@formatter:off
		addFields(
			FieldConfiguration.builder(PathogenTestDto.TUBE_NIL_GT10).valueChangeListener(new TuberculosisIGRAGT10InputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_NIL_GT10,PathogenTestDto.TUBE_NIL)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB1_GT10).valueChangeListener(new TuberculosisIGRAGT10InputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_AG_TB1_GT10,PathogenTestDto.TUBE_AG_TB1)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB2_GT10).valueChangeListener(new TuberculosisIGRAGT10InputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_AG_TB2_GT10,PathogenTestDto.TUBE_AG_TB2)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_MITOGENE_GT10).valueChangeListener(new TuberculosisIGRAGT10InputValueChangeListener(getFieldGroup(), PathogenTestDto.TUBE_MITOGENE_GT10,PathogenTestDto.TUBE_MITOGENE)).build());			
		//@formatter:on

		setVisibleClear(
			false,
			PathogenTestDto.TUBE_NIL,
			PathogenTestDto.TUBE_NIL_GT10,
			PathogenTestDto.TUBE_AG_TB1,
			PathogenTestDto.TUBE_AG_TB1_GT10,
			PathogenTestDto.TUBE_AG_TB2,
			PathogenTestDto.TUBE_AG_TB2_GT10,
			PathogenTestDto.TUBE_MITOGENE,
			PathogenTestDto.TUBE_MITOGENE_GT10);

		NullableOptionGroup testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, NullableOptionGroup.class);
		addField(PathogenTestDto.PRELIMINARY).addStyleName(CssStyles.VSPACE_4);

		// Make TEST_RESULT_VERIFIED required only when the test comes via LIMS (laboratory is directly connected)
		viaLimsField.addValueChangeListener(e -> {
			boolean isViaLims = Boolean.TRUE.equals(e.getProperty().getValue());
			testResultVerifiedField.setRequired(isViaLims);
		});

		// Set initial required state based on current viaLims value
		testResultVerifiedField.setRequired(Boolean.TRUE.equals(viaLimsField.getValue()));

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

		pcrTestSpecification.setVisible(false);

		Label prescriberHeadingLabel = new Label(I18nProperties.getCaption(Captions.PathogenTest_prescriber));
		prescriberHeadingLabel.addStyleName(H3);
		getContent().addComponent(prescriberHeadingLabel, PRESCRIBER_HEADING_LOC);

		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.PCR_TEST_SPECIFICATION, PCR_TEST_SPECIFICATION_VISIBILITY_CONDITIONS, true);
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

		// Serotype field visibility specification for CSM disease
		Map<Object, List<Object>> serotypeVisibilityDependencies = new HashMap<Object, List<Object>>() {

			private static final long serialVersionUID = 1967952323596082247L;

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CSM));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(PathogenTestDto.SEROTYPE), serotypeVisibilityDependencies, true);
		// End of Serotype field visibility specification for CSM disease

		// IPI visibility check with a positive test result, show serotype and serotyping method fields
		Map<Object, List<Object>> ipiSeroTypeAndMethodVisibilityDependencies = new HashMap<Object, List<Object>>() {

			private static final long serialVersionUID = 1967952323596082247L;
			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
				put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.SEROGROUPING));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PathogenTestDto.SEROTYPE, PathogenTestDto.SEROTYPING_METHOD),
			ipiSeroTypeAndMethodVisibilityDependencies,
			true);
		Map<Object, List<Object>> ipiSeroTypeVisibilityDependencies = new HashMap<Object, List<Object>>() {

			private static final long serialVersionUID = 1967952323596082247L;
			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
				put(
					PathogenTestDto.TEST_TYPE,
					Arrays.asList(
						PathogenTestType.WHOLE_GENOME_SEQUENCING,
						PathogenTestType.SLIDE_AGGLUTINATION,
						PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
						PathogenTestType.SEROGROUPING));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SEROTYPE, ipiSeroTypeVisibilityDependencies, true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_TYPING_METHOD_TEXT,
			PathogenTestDto.SEROTYPING_METHOD,
			SerotypingMethod.OTHER,
			true);
		// End of IPI visibility check

		//IMI serogroup specification
		Map<Object, List<Object>> imiSeroTypingDependencies = new HashMap<>() {

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
				put(
					PathogenTestDto.TEST_TYPE,
					Arrays.asList(
						PathogenTestType.SEROGROUPING,
						PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
						PathogenTestType.SLIDE_AGGLUTINATION,
						PathogenTestType.WHOLE_GENOME_SEQUENCING));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SERO_GROUP_SPECIFICATION, imiSeroTypingDependencies, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT,
			PathogenTestDto.SERO_GROUP_SPECIFICATION,
			SeroGroupSpecification.OTHER,
			true);
		// End of IMI serogroup specification
		//Cryptosporidiosis for all countries Genotyping specification
		Map<Object, List<Object>> cryptoGenoTypingDependencies = new HashMap<>() {

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.MEASLES, Disease.CRYPTOSPORIDIOSIS));
				put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.GENOTYPING));
				put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.GENOTYPE_RESULT, cryptoGenoTypingDependencies, true);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), PathogenTestDto.GENOTYPE_RESULT_TEXT, PathogenTestDto.GENOTYPE_RESULT, GenoTypeResult.OTHER, true);

		//disease variant specifications for RSV and Influenza
		Map<Object, List<Object>> diseaseVariantDependencies = new HashMap<>() {

			{
				put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INFLUENZA));
				put(
					PathogenTestDto.TEST_TYPE,
					Arrays.asList(
						PathogenTestType.SEQUENCING,
						PathogenTestType.WHOLE_GENOME_SEQUENCING,
						PathogenTestType.PCR_RT_PCR,
						PathogenTestType.ISOLATION,
						PathogenTestType.OTHER));
			}
		};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TESTED_DISEASE_VARIANT, diseaseVariantDependencies, true);

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

				updateDrugSusceptibilityFieldSpecifications((PathogenTestType) testTypeField.getValue(), disease);
			}
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			if (diseaseVariant != null) {
				testResultField.setValue(PathogenTestResultType.POSITIVE);
			} else {
				testResultField.clear();
			}
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

				if (diseaseField.getValue() == null || !List.of(Disease.TUBERCULOSIS).contains((Disease) diseaseField.getValue())) {
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
						false,
						PathogenTestDto.CQ_VALUE,
						PathogenTestDto.CT_VALUE_E,
						PathogenTestDto.CT_VALUE_N,
						PathogenTestDto.CT_VALUE_RDRP,
						PathogenTestDto.CT_VALUE_S,
						PathogenTestDto.CT_VALUE_ORF_1,
						PathogenTestDto.CT_VALUE_RDRP_S);
				}
				// Show tube IGRA fields only for IGRA tests and Luxembourg
				setVisibleClear(
					PathogenTestType.IGRA == testType && FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG),
					PathogenTestDto.TUBE_NIL,
					PathogenTestDto.TUBE_NIL_GT10,
					PathogenTestDto.TUBE_AG_TB1,
					PathogenTestDto.TUBE_AG_TB1_GT10,
					PathogenTestDto.TUBE_AG_TB2,
					PathogenTestDto.TUBE_AG_TB2_GT10,
					PathogenTestDto.TUBE_MITOGENE,
					PathogenTestDto.TUBE_MITOGENE_GT10);
				FieldHelper.updateItems((Disease) diseaseField.getValue(), genoTypingCB, GenoTypeResult.class);
			} else {
				setVisibleClear(
					testTypeField.getValue() != null,
					PathogenTestDto.SEROTYPE,
					PathogenTestDto.SEROTYPING_METHOD,
					PathogenTestDto.SERO_GROUP_SPECIFICATION);
				// hide tube fields when no test type selected
				setVisibleClear(
					false,
					PathogenTestDto.TUBE_NIL,
					PathogenTestDto.TUBE_NIL_GT10,
					PathogenTestDto.TUBE_AG_TB1,
					PathogenTestDto.TUBE_AG_TB1_GT10,
					PathogenTestDto.TUBE_AG_TB2,
					PathogenTestDto.TUBE_AG_TB2_GT10,
					PathogenTestDto.TUBE_MITOGENE,
					PathogenTestDto.TUBE_MITOGENE_GT10);
				testResultField.clear();
				testResultField.setEnabled(true);
			}

			if (RESULT_FIELD_DECISION_MAP.containsKey(disease) && RESULT_FIELD_DECISION_MAP.get(disease).contains(testType)) {
				testResultField.setValue(PathogenTestResultType.POSITIVE);
			} else {
				testResultField.clear();
			}

			updateDrugSusceptibilityFieldSpecifications(testType, (Disease) diseaseField.getValue());
		});

		lab.addValueChangeListener(event ->

		{
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
			setCqValueVisibility(diseaseField, cqValueField, testType, (PathogenTestResultType) testResultField.getValue());
		});

		testResultField.addValueChangeListener(e -> {
			PathogenTestResultType testResult = (PathogenTestResultType) e.getProperty().getValue();
			setCqValueVisibility(diseaseField, cqValueField, (PathogenTestType) testTypeField.getValue(), testResult);
		});

		if (SamplePurpose.INTERNAL.equals(getSamplePurpose())) { // this only works for already saved samples
			setRequired(true, PathogenTestDto.LAB);
		}
		setRequired(true, PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_RESULT);

		initializeAccessAndAllowedAccesses();
		initializeVisibilitiesAndAllowedVisibilities();

		// Hide/show prescriber heading after the visibilities have been initialized
		prescriberHeadingLabel.setVisible(
			isVisibleAllowed(PathogenTestDto.PRESCRIBER_PHYSICIAN_CODE)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_FIRST_NAME)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_LAST_NAME)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_PHONE_NUMBER)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_ADDRESS)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_POSTAL_CODE)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_CITY)
				|| isVisibleAllowed(PathogenTestDto.PRESCRIBER_COUNTRY));
	}

	/**
	 * This class is to be used for the Tuberculosis IGRA input value change listeners.
	 * It will check/uncheck the Tuberculosis IGRA greater than 10 checkbox dependiong on the value of the input field.
	 * <p>
	 * Note: ideally a custom component should be used for both fields, to avoid potential race conditions between the two listeners.
	 */
	protected static class TuberculosisIGRAInputValueChangeListener implements ValueChangeListener {

		private final String igraInputFieldId;
		private final String igraGT10FieldId;
		private final BeanFieldGroup<PathogenTestDto> fieldGroup;

		public TuberculosisIGRAInputValueChangeListener(BeanFieldGroup<PathogenTestDto> fg, String igraInputFieldId, String igraGT10FieldId) {
			this.igraInputFieldId = igraInputFieldId;
			this.igraGT10FieldId = igraGT10FieldId;
			this.fieldGroup = fg;
		}

		@SuppressWarnings({
			"unchecked",
			"rawtypes" })
		@Override
		public void valueChange(Property.ValueChangeEvent event) {

			final Field<?> igraInputField = fieldGroup.getField(igraInputFieldId);

			if (igraInputField == null) {
				return;
			}

			if (igraInputField instanceof AbstractComponent) {
				((AbstractComponent) igraInputField).setComponentError(null);
			}

			final BeanItem<?> beanItemDataSource = fieldGroup.getItemDataSource();

			// the input field is always a TextField with a String as value
			// we need to make a hard assumtion that the input field value is a Float

			// we check to see if the model property is numeric
			// the model at this point will not be updated, so we only check type
			final Property<?> igraValueProp = beanItemDataSource.getItemProperty(igraInputFieldId);

			if (!Number.class.isAssignableFrom(igraValueProp.getType())) {
				// we will not deal with non-numeric values
				return;
			}

			// we know that the model property is numeric
			// we could get the original value with: igraValueProp.getValue();

			// we need to convert the value to number
			// and we need to do it locale aware and need to finagle with types

			Number igraNewValue = null;

			try {
				igraNewValue = igraInputField.getValue() == null
					? null
					: (Number) ConverterUtil
						.getConverter(igraInputField.getType(), (Class) igraValueProp.getType(), null /* current session */)
						.convertToModel(igraInputField.getValue(), igraValueProp.getType(), igraInputField.getLocale());
			} catch (ConversionException e) {
				if (igraInputField instanceof AbstractComponent) {
					((AbstractComponent) igraInputField).setComponentError(new UserError(I18nProperties.getString(Strings.errorInvalidValue)));
				}
				return;
			}

			final Boolean checked = igraNewValue == null ? null : igraNewValue.floatValue() > 10;

			// now we need to set the value of the GT10 field
			@SuppressWarnings("unchecked")
			final Field<Object> igraGT10Field = (Field<Object>) fieldGroup.getField(igraGT10FieldId);
			if (igraGT10Field == null) {
				// if we can't find the field, we don't care
				return;
			}

			// lets make sure the property is a boolean
			final Property<?> igraGT10Prop = beanItemDataSource.getItemProperty(igraGT10FieldId);
			if (igraGT10Prop == null || !Boolean.class.isAssignableFrom(igraGT10Prop.getType())) {
				// if we can't find the property, or we can't set it we don't care
				return;
			}

			// now field is supposed to be a boolean
			// booleans come in two flavors: collection based and primitive
			final boolean isCollection = Collection.class.isAssignableFrom(igraGT10Field.getType());

			if (!isCollection) {
				// primitive booleans are easy
				final boolean currentChecked = Boolean.TRUE.equals(igraGT10Field.getValue());
				if (checked != null && checked.booleanValue() != currentChecked) {
					igraGT10Field.setValue(checked);
				}
			} else {
				// well have to do it the hard way
				final Collection<?> currentSet = (Collection<?>) igraGT10Field.getValue();
				final boolean currentChecked = currentSet != null && !currentSet.isEmpty() && currentSet.contains(Boolean.TRUE);
				if (checked != null && checked.booleanValue() != currentChecked) {
					final HashSet<Boolean> set = new HashSet<>();
					set.add(checked);
					igraGT10Field.setValue(Collections.unmodifiableSet(set));
				}
			}
		}
	}

	/**
	 * This class is to be used for the Tuberculosis IGRA greater than 10 checkboxes value change listeners.
	 * It will clear the associated input field if the checkbox is checked and the
	 * value is less than or equal to 10.
	 * In reverse if the value is greater than 10 and the checkbox is not checked it will clear the input field.
	 * <p>
	 * Note: ideally a custom component should be used for both fields, to avoid potential race conditions between the two listeners.
	 */
	protected static class TuberculosisIGRAGT10InputValueChangeListener implements ValueChangeListener {

		private final String igraInputFieldId;
		private final String igraGT10FieldId;
		private final BeanFieldGroup<PathogenTestDto> fieldGroup;

		public TuberculosisIGRAGT10InputValueChangeListener(BeanFieldGroup<PathogenTestDto> fg, String igraGT10FieldId, String igraInputFieldId) {
			this.igraInputFieldId = igraInputFieldId;
			this.igraGT10FieldId = igraGT10FieldId;
			this.fieldGroup = fg;
		}

		@SuppressWarnings({
			"rawtypes",
			"unchecked" })
		@Override
		public void valueChange(Property.ValueChangeEvent event) {

			// let's try to get the numeric input field and converted value
			final Field<?> igraInputField = fieldGroup.getField(igraInputFieldId);
			if (igraInputField == null) {
				return;
			}

			if (igraInputField instanceof AbstractComponent) {
				((AbstractComponent) igraInputField).setComponentError(null);
			}

			final BeanItem<?> beanItemDataSource = fieldGroup.getItemDataSource();

			final Property<?> igraValueProp = beanItemDataSource.getItemProperty(igraInputFieldId);
			if (igraValueProp == null || !Number.class.isAssignableFrom(igraValueProp.getType())) {
				return;
			}

			// lets make sure the GT10 property is a boolean
			final Property<?> igraGT10Prop = beanItemDataSource.getItemProperty(igraGT10FieldId);
			if (igraGT10Prop == null || !Boolean.class.isAssignableFrom(igraGT10Prop.getType())) {
				// if we can't find the property, or we can't set it we don't care
				return;
			}

			Number igraNewValue = null;

			try {
				igraNewValue = igraInputField.getValue() == null
					? null
					: (Number) ConverterUtil
						.getConverter(igraInputField.getType(), (Class) igraValueProp.getType(), null /* current session */)
						.convertToModel(igraInputField.getValue(), igraValueProp.getType(), igraInputField.getLocale() /* current locale */);
			} catch (ConversionException e) {
				if (igraInputField instanceof AbstractComponent) {
					((AbstractComponent) igraInputField).setComponentError(new UserError(I18nProperties.getString(Strings.errorInvalidValue)));
				}
				return;
			}

			// now let's try to determine if the checkbox is checked (we know it's a boolean)
			@SuppressWarnings("unchecked")
			final Field<Object> igraGT10Field = (Field<Object>) fieldGroup.getField(igraGT10FieldId);
			if (igraGT10Field == null) {
				// if we can't find the field, we don't care
				return;
			}

			// booleans come in two flavors: collection based and primitive
			final boolean isCollection = Collection.class.isAssignableFrom(igraGT10Field.getType());

			Boolean checked = false;

			// value can be true or false/null(presumed false)
			if (!isCollection) {
				// primitive booleans are easy
				checked = igraGT10Field.getValue() == null ? null : Boolean.TRUE.equals(igraGT10Field.getValue());
			} else {
				Collection<?> set = (Collection<?>) igraGT10Field.getValue();
				checked = set == null || set.isEmpty() ? null : set.contains(Boolean.TRUE);
			}

			if (checked == null) { // the checbox is neither checked nor unchecked
				checked = igraNewValue != null && igraNewValue.floatValue() > 10;

				if (!isCollection) {
					// primitive booleans are easy
					igraGT10Field.setValue(checked);
				} else {
					final HashSet<Boolean> set = new HashSet<>();
					set.add(checked);
					igraGT10Field.setValue(Collections.unmodifiableSet(set));
				}

				// don't need to clear anything else because there was no check/uncheck before
				return;
			}

			if ((checked && igraNewValue != null && igraNewValue.floatValue() <= 10) // checked but value is filled in and less than 10
				|| (!checked && igraNewValue != null && igraNewValue.floatValue() > 10) // not checked but value is filled in an greater than 10
			) {
				try {
					igraInputField.clear();
				} catch (ReadOnlyException ex) {
					// ignore read-only
				}
			}

		}
	}
}
