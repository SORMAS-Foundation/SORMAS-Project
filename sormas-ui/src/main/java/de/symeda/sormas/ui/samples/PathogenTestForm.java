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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.CustomLayout;
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
import de.symeda.sormas.api.DiseaseHelper;
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
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.sample.SerotypingMethod;
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

	private static final String DISEASE_SECTION_LOC = "diseaseSectionLoc";

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
			loc(DISEASE_SECTION_LOC) +
			fluidRowLocs(4,PathogenTestDto.SEROTYPE, 4,PathogenTestDto.SEROTYPING_METHOD, 4,PathogenTestDto.SERO_TYPING_METHOD_TEXT) +
			fluidRowLocs(6,PathogenTestDto.SERO_GROUP_SPECIFICATION , 6, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT) +
			fluidRowLocs(4,PathogenTestDto.GENOTYPE_RESULT,6, PathogenTestDto.GENOTYPE_RESULT_TEXT) +
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
	private ComboBox genoTypingCB;
	private TextField genoTypingResultTextTF;

	private ComboBox seroGrpSepcCB;
	private TextField seroGrpSpecTxt;

	// Disease section swap support
	private DiseaseSectionLayout activeSection = new DefaultDiseaseSectionLayout();
	private CustomLayout diseaseSectionPanel;

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
		ComboBox specieFieldDynamic = getField(PathogenTestDto.SPECIE);
		if (specieFieldDynamic != null) {
			specieFieldDynamic.setValue(newFieldValue.getSpecie());
		}
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

		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.forceUpdateDrugSusceptibilityFields();
		}
		markAsDirty();
	}

	@Override
	protected void addFields() {

		pathogenTestHeadingLabel = new Label();
		pathogenTestHeadingLabel.addStyleName(H3);
		getContent().addComponent(pathogenTestHeadingLabel, PATHOGEN_TEST_HEADING_LOC);

		// Install the disease section panel — a nested CustomLayout whose template is swapped on disease change
		diseaseSectionPanel = new CustomLayout();
		diseaseSectionPanel.setTemplateContents(activeSection.getHtmlLayout());
		diseaseSectionPanel.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(diseaseSectionPanel, DISEASE_SECTION_LOC);

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
		FieldHelper.addSoftRequiredStyle(testTypeTextField);
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

		// Bind the initial disease section (default = no-op; swapped via swapDiseaseSection() on disease change)
		activeSection = DiseaseSectionLayout.forDisease(disease);
		diseaseSectionPanel.setTemplateContents(activeSection.getHtmlLayout());
		activeSection.bindFields(getFieldGroup(), diseaseSectionPanel, disease);

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
			swapDiseaseSection(latestDisease);

			FieldHelper.updateItems(
				testTypeField,
				Arrays.asList(PathogenTestType.values()),
				FieldVisibilityCheckers.withDisease(disease),
				PathogenTestType.class);

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				ComboBox strainCallStatusField = getField(PathogenTestDto.STRAIN_CALL_STATUS);
				if (strainCallStatusField != null) {
					FieldHelper.updateItems(
						strainCallStatusField,
						Arrays.asList(PathogenStrainCallStatus.values()),
						FieldVisibilityCheckers.withDisease(disease),
						PathogenStrainCallStatus.class);
				}

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

	static class TestTypeValueChangeListener implements ValueChangeListener {

		@Override
		public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
			// TODO Auto-generated method stub

		}
	}

	// -----------------------------------------------------------------------
	// Disease section swap support
	// -----------------------------------------------------------------------

	/** Replaces the active disease section with the one appropriate for the given disease. */
	private void swapDiseaseSection(Disease newDisease) {
		DiseaseSectionLayout newSection = DiseaseSectionLayout.forDisease(newDisease);
		if (newSection.getClass() == activeSection.getClass()) {
			return; // same section type, nothing to swap
		}

		activeSection.unbindFields(getFieldGroup(), diseaseSectionPanel);
		activeSection = newSection;
		diseaseSectionPanel.setTemplateContents(newSection.getHtmlLayout());
		newSection.bindFields(getFieldGroup(), diseaseSectionPanel, newDisease);
	}

	/** Package-private: adds a disease-section field to the nested diseaseSectionPanel layout. */
	<F extends com.vaadin.v7.ui.Field> F addSectionField(String propertyId, Class<F> fieldType) {
		F field = getFieldGroup().buildAndBind(propertyId, (Object) propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		diseaseSectionPanel.addComponent(field, propertyId);
		return field;
	}

	/** Package-private: adds the DrugSusceptibilityForm to the disease section panel. */
	void addSectionDrugSusceptibilityField() {
		drugSusceptibilityField = (DrugSusceptibilityForm) addField(
			diseaseSectionPanel,
			PathogenTestDto.DRUG_SUSCEPTIBILITY,
			new DrugSusceptibilityForm(
				de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers.getNoop(),
				de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale())));
		drugSusceptibilityField.setCaption(null);
		addToVisibleAllowedFields(drugSusceptibilityField);
	}

	/** Package-private: adds all tube IGRA fields to the disease section panel. */
	void addSectionTubeFields() {
		addFields(
			diseaseSectionPanel,
			FieldConfiguration.builder(PathogenTestDto.TUBE_NIL)
				.validationMessageProperty(de.symeda.sormas.api.i18n.Validations.onlyNumbersAllowed)
				.valueChangeListener(e -> handleTubeNilChange((String) e.getProperty().getValue()))
				.build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB1)
				.validationMessageProperty(de.symeda.sormas.api.i18n.Validations.onlyNumbersAllowed)
				.valueChangeListener(e -> handleTubeAgTb1Change((String) e.getProperty().getValue()))
				.build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB2)
				.validationMessageProperty(de.symeda.sormas.api.i18n.Validations.onlyNumbersAllowed)
				.valueChangeListener(e -> handleTubeAgTb2Change((String) e.getProperty().getValue()))
				.build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_MITOGENE)
				.validationMessageProperty(de.symeda.sormas.api.i18n.Validations.onlyNumbersAllowed)
				.valueChangeListener(e -> handleTubeMitogeneChange((String) e.getProperty().getValue()))
				.build());
		addFields(
			diseaseSectionPanel,
			FieldConfiguration.builder(PathogenTestDto.TUBE_NIL_GT10).valueChangeListener(e -> handleTubeNilGt10Change(e)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB1_GT10).valueChangeListener(e -> handleTubeAgTb1Gt10Change(e)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_AG_TB2_GT10).valueChangeListener(e -> handleTubeAgTb2Gt10Change(e)).build(),
			FieldConfiguration.builder(PathogenTestDto.TUBE_MITOGENE_GT10).valueChangeListener(e -> handleTubeMitogeneGt10Change(e)).build());

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
	}

	/** Package-private: removes a field from both the disease section panel and the field group. */
	void removeSectionField(String propertyId) {
		com.vaadin.v7.ui.Field<?> field = getField(propertyId);
		if (field != null) {
			// Unbind first so listeners on sibling fields don't NPE when they fire during clear()
			getFieldGroup().unbind(field);
			diseaseSectionPanel.removeComponent(field);
		}
	}

	/** Returns the currently selected disease — used by section implementations. */
	Disease getCurrentDisease() {
		return disease;
	}

	// Tube field helpers — delegates from listeners in the original addFields() block
	private void handleTubeNilChange(String val) {
		NullableOptionGroup gt10 = getField(PathogenTestDto.TUBE_NIL_GT10);
		if (gt10 == null)
			return;
		if (val == null) {
			gt10.select(false);
			return;
		}
		try {
			gt10.select(Float.parseFloat(val) > 10);
		} catch (NumberFormatException e) {
			getField(PathogenTestDto.TUBE_NIL).clear();
			gt10.select(false);
		}
	}

	private void handleTubeAgTb1Change(String val) {
		NullableOptionGroup gt10 = getField(PathogenTestDto.TUBE_AG_TB1_GT10);
		if (gt10 == null)
			return;
		if (val == null) {
			gt10.select(false);
			return;
		}
		try {
			gt10.select(Float.parseFloat(val) > 10);
		} catch (NumberFormatException e) {
			getField(PathogenTestDto.TUBE_AG_TB1).clear();
			gt10.select(false);
		}
	}

	private void handleTubeAgTb2Change(String val) {
		NullableOptionGroup gt10 = getField(PathogenTestDto.TUBE_AG_TB2_GT10);
		if (gt10 == null)
			return;
		if (val == null) {
			gt10.select(false);
			return;
		}
		try {
			gt10.select(Float.parseFloat(val) > 10);
		} catch (NumberFormatException e) {
			getField(PathogenTestDto.TUBE_AG_TB2).clear();
			gt10.select(false);
		}
	}

	private void handleTubeMitogeneChange(String val) {
		NullableOptionGroup gt10 = getField(PathogenTestDto.TUBE_MITOGENE_GT10);
		if (gt10 == null)
			return;
		if (val == null) {
			gt10.select(false);
			return;
		}
		try {
			gt10.select(Float.parseFloat(val) > 10);
		} catch (NumberFormatException e) {
			getField(PathogenTestDto.TUBE_MITOGENE).clear();
			gt10.select(false);
		}
	}

	private void handleTubeNilGt10Change(com.vaadin.v7.data.Property.ValueChangeEvent e) {
		Object v = e.getProperty().getValue() instanceof Collection
			? ((Collection<?>) e.getProperty().getValue()).stream().findFirst().orElse(null)
			: e.getProperty().getValue();
		handleGt10CheckboxChange(v, getField(PathogenTestDto.TUBE_NIL));
	}

	private void handleTubeAgTb1Gt10Change(com.vaadin.v7.data.Property.ValueChangeEvent e) {
		Object v = e.getProperty().getValue() instanceof Collection
			? ((Collection<?>) e.getProperty().getValue()).stream().findFirst().orElse(null)
			: e.getProperty().getValue();
		handleGt10CheckboxChange(v, getField(PathogenTestDto.TUBE_AG_TB1));
	}

	private void handleTubeAgTb2Gt10Change(com.vaadin.v7.data.Property.ValueChangeEvent e) {
		Object v = e.getProperty().getValue() instanceof Collection
			? ((Collection<?>) e.getProperty().getValue()).stream().findFirst().orElse(null)
			: e.getProperty().getValue();
		handleGt10CheckboxChange(v, getField(PathogenTestDto.TUBE_AG_TB2));
	}

	private void handleTubeMitogeneGt10Change(com.vaadin.v7.data.Property.ValueChangeEvent e) {
		Object v = e.getProperty().getValue() instanceof Collection
			? ((Collection<?>) e.getProperty().getValue()).stream().findFirst().orElse(null)
			: e.getProperty().getValue();
		handleGt10CheckboxChange(v, getField(PathogenTestDto.TUBE_MITOGENE));
	}

	private void handleGt10CheckboxChange(Object singleValue, com.vaadin.v7.ui.Field<?> numericField) {
		if (numericField == null)
			return;
		String numVal = (String) numericField.getValue();
		if (singleValue == null || numVal == null)
			return;
		boolean checked = Boolean.TRUE.equals(singleValue);
		try {
			float f = Float.valueOf(numVal);
			if (checked && f <= 10)
				numericField.clear();
			else if (!checked && f > 10)
				numericField.clear();
		} catch (NumberFormatException ex) {
			numericField.clear();
		}
	}
}
