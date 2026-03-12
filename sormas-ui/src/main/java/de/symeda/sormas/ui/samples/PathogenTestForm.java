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
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.diseasesection.DefaultDiseaseSectionLayout;
import de.symeda.sormas.ui.samples.diseasesection.DiseaseSectionLayout;
import de.symeda.sormas.ui.samples.diseasesection.PathogenTestFormConfig;
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
			fluidRowLocs(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(6,PathogenTestDto.TEST_RESULT, 4, PathogenTestDto.TEST_RESULT_VERIFIED, 2,PathogenTestDto.PRELIMINARY) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) +
			loc(DISEASE_SECTION_LOC) +
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

	private SampleDto sample;
	private EnvironmentSampleDto environmentSample;
	private AbstractSampleForm sampleForm;
	private final int caseSampleCount;
	private final boolean create;

	private Label pathogenTestHeadingLabel;

	private ComboBox testTypeField;
	private ComboBox diseaseField;
	private ComboBox testResultField;
	private TextField testTypeTextField;
	private Disease disease;
	private TextField typingIdField;

	// New instance fields promoted from addFields() locals
	private CheckBox viaLimsField;
	private ComboBox lab;
	private TextField labDetails;
	private TextField cqValueField;
	private NullableOptionGroup testResultVerifiedField;
	private CheckBox fourFoldIncrease;
	private Label prescriberHeadingLabel;
	private ComboBox diseaseVariantField;
	private TextField diseaseVariantDetailsField;
	private Consumer<Disease> updateDiseaseVariantField;

	// Disease section swap support
	private DiseaseSectionLayout activeSection = new DefaultDiseaseSectionLayout();
	private CustomLayout diseaseSectionPanel;
	private PathogenTestFormConfig formConfig;

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
		testTypeTextField.setValue(newFieldValue.getTestTypeText());
		if (!testResultField.isReadOnly()) {
			testResultField.setValue(newFieldValue.getTestResult());
		}
		typingIdField.setValue(newFieldValue.getTypingId());
		ComboBox specieFieldDynamic = getField(PathogenTestDto.SPECIE);
		if (specieFieldDynamic != null) {
			specieFieldDynamic.setValue(newFieldValue.getSpecie());
		}
		markAsDirty();
	}

	@Override
	protected void addFields() {
		addHeaderAndLayoutFields();
		addTestDateField();
		addLabFields();
		addDiseaseAndResultFields();
		addCtValueFields();
		addPrescriberFields();
		bindVisibilityRules();
		bindValueChangeListeners();
		finalizeForm();
	}

	private void addHeaderAndLayoutFields() {
		formConfig = PathogenTestFormConfig.fromCurrentConfig();

		pathogenTestHeadingLabel = new Label();
		pathogenTestHeadingLabel.addStyleName(H3);
		getContent().addComponent(pathogenTestHeadingLabel, PATHOGEN_TEST_HEADING_LOC);

		// Install the disease section panel — a nested CustomLayout whose template is swapped on disease change
		diseaseSectionPanel = new CustomLayout();
		diseaseSectionPanel.setTemplateContents(activeSection.getHtmlLayout());
		diseaseSectionPanel.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(diseaseSectionPanel, DISEASE_SECTION_LOC);

		addDateField(PathogenTestDto.REPORT_DATE, DateField.class, 0);
		viaLimsField = addField(PathogenTestDto.VIA_LIMS);
		addField(PathogenTestDto.EXTERNAL_ID);
		addField(PathogenTestDto.EXTERNAL_ORDER_ID);
		testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		testTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		testTypeField.setImmediate(true);
		testTypeTextField = addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		FieldHelper.addSoftRequiredStyle(testTypeTextField);
	}

	private DateTimeField addTestDateField() {
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
		return testDateField;
	}

	private void addLabFields() {
		lab = addInfrastructureField(PathogenTestDto.LAB);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		labDetails = addField(PathogenTestDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		typingIdField = addField(PathogenTestDto.TYPING_ID, TextField.class);
		typingIdField.setVisible(false);
	}

	private void addDiseaseAndResultFields() {
		// Tested Disease or Tested Pathogen, depending on sample type
		diseaseField = addDiseaseField(PathogenTestDto.TESTED_DISEASE, true, create, false);
		addField(PathogenTestDto.TESTED_DISEASE_DETAILS, TextField.class);
		diseaseVariantField = addCustomizableEnumField(PathogenTestDto.TESTED_DISEASE_VARIANT);
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setVisible(false);
		diseaseVariantDetailsField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);
		if (DiseaseHelper.SUBTYPE_ALLOWED_DISEASES.contains(disease)) {
			diseaseVariantField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariant));
			diseaseVariantDetailsField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariantDetails));
		}
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

		if (!formConfig.isLuxembourg) {
			testResultField.removeItem(PathogenTestResultType.NOT_APPLICABLE);
		}
		// Bind the initial disease section (default = no-op; swapped via swapDiseaseSection() on disease change)
		activeSection = DiseaseSectionLayout.forDisease(disease);
		diseaseSectionPanel.setTemplateContents(activeSection.getHtmlLayout());
		activeSection.bindFields(getFieldGroup(), diseaseSectionPanel, disease, formConfig);
	}

	private void addCtValueFields() {
		cqValueField = addField(FieldConfiguration.withConversionError(PathogenTestDto.CQ_VALUE, Validations.onlyNumbersAllowed));
		if (!formConfig.isLuxembourg) {
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
	}

	private void addPrescriberFields() {
		testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, NullableOptionGroup.class);
		addField(PathogenTestDto.PRELIMINARY).addStyleName(CssStyles.VSPACE_4);

		// Make TEST_RESULT_VERIFIED required only when the test comes via LIMS (laboratory is directly connected)
		viaLimsField.addValueChangeListener(e -> {
			boolean isViaLims = Boolean.TRUE.equals(e.getProperty().getValue());
			testResultVerifiedField.setRequired(isViaLims);
		});

		// Set initial required state based on current viaLims value
		testResultVerifiedField.setRequired(Boolean.TRUE.equals(viaLimsField.getValue()));

		fourFoldIncrease = addField(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
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

		prescriberHeadingLabel = new Label(I18nProperties.getCaption(Captions.PathogenTest_prescriber));
		prescriberHeadingLabel.addStyleName(H3);
		getContent().addComponent(prescriberHeadingLabel, PRESCRIBER_HEADING_LOC);
	}

	private void bindVisibilityRules() {
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

		updateDiseaseVariantField = d -> {
			List<DiseaseVariant> diseaseVariants = FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, d);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField
				.setVisible(d != null && isVisibleAllowed(PathogenTestDto.TESTED_DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		};

		updateDiseaseVariantField.accept((Disease) diseaseField.getValue());
	}

	private void bindValueChangeListeners() {
		diseaseField.addValueChangeListener(valueChangeEvent -> {
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
			} else {
				testResultField.clear();
				testResultField.setEnabled(true);
			}

			if (RESULT_FIELD_DECISION_MAP.containsKey(disease) && RESULT_FIELD_DECISION_MAP.get(disease).contains(testType)) {
				testResultField.setValue(PathogenTestResultType.POSITIVE);
			} else {
				testResultField.clear();
			}

			activeSection.onTestTypeChanged(
				testType,
				(Disease) diseaseField.getValue(),
				(com.vaadin.v7.ui.AbstractField<PathogenTestResultType>) (Object) testResultField,
				formConfig);
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
			setCqValueVisibility(diseaseField, cqValueField, testType, (PathogenTestResultType) testResultField.getValue());
		});

		testResultField.addValueChangeListener(e -> {
			PathogenTestResultType testResult = (PathogenTestResultType) e.getProperty().getValue();
			setCqValueVisibility(diseaseField, cqValueField, (PathogenTestType) testTypeField.getValue(), testResult);
		});
	}

	private void finalizeForm() {
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

	/** Replaces the active disease section with the one appropriate for the given disease. */
	private void swapDiseaseSection(Disease newDisease) {
		DiseaseSectionLayout newSection = DiseaseSectionLayout.forDisease(newDisease);
		if (newSection.getClass() == activeSection.getClass()) {
			return; // same section type, nothing to swap
		}

		removeFromAllowedLists(activeSection.getFieldIds());
		activeSection.unbindFields(getFieldGroup(), diseaseSectionPanel);
		activeSection = newSection;
		diseaseSectionPanel.setTemplateContents(newSection.getHtmlLayout());
		newSection.bindFields(getFieldGroup(), diseaseSectionPanel, newDisease, formConfig);
		rebuildSectionFieldAllowances(newSection.getFieldIds());
	}

	private void rebuildSectionFieldAllowances(Collection<String> fieldIds) {
		for (String id : fieldIds) {
			com.vaadin.v7.ui.Field<?> f = getField(id);
			if (f != null) {
				addToVisibleAllowedFields(f);
			}
		}
	}

	private void removeFromAllowedLists(Collection<String> fieldIds) {
		for (String id : fieldIds) {
			com.vaadin.v7.ui.Field<?> f = getField(id);
			if (f != null) {
				removeFromVisibleAllowedFields(f);
				removeFromEditableAllowedFields(f);
			}
		}
	}

}
