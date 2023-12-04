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

import org.apache.commons.collections.CollectionUtils;

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
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
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
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			fluidRowLocs(PathogenTestDto.PCR_TEST_SPECIFICATION, "") +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) +
			fluidRowLocs(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.TESTED_PATHOGEN_DETAILS) +
			fluidRowLocs(PathogenTestDto.TYPING_ID, "") +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED) +
			fluidRowLocs(PathogenTestDto.PRELIMINARY, "") +
			fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			fluidRowLocs(PathogenTestDto.SEROTYPE, "") + 
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

	private TextField testTypeTextField;
	private ComboBox pcrTestSpecification;
	private TextField typingIdField;

	public PathogenTestForm(AbstractSampleForm sampleForm, boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction) {
		this(create, caseSampleCount, isPseudonymized, inJurisdiction);
		this.sampleForm = sampleForm;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(SampleDto sample, boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction) {

		this(create, caseSampleCount, isPseudonymized, inJurisdiction);
		this.sample = sample;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(EnvironmentSampleDto sample, boolean create, boolean isPseudonymized, boolean inJurisdiction) {

		this(create, 0, isPseudonymized, inJurisdiction);
		this.environmentSample = sample;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction) {
		super(
			PathogenTestDto.class,
			PathogenTestDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(null).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.forDataAccessLevel(
				UserProvider.getCurrent().getPseudonymizableDataAccessLevel(create || inJurisdiction), // Jurisdiction doesn't matter for creation forms
				!create && isPseudonymized)); // Pseudonymization doesn't matter for creation forms

		this.caseSampleCount = caseSampleCount;
		this.create = create;
		setWidth(900, Unit.PIXELS);
	}

	private static void setCqValueVisibility(TextField cqValueField, PathogenTestType testType, PathogenTestResultType testResultType) {
		if ((FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
			|| (testType == PathogenTestType.PCR_RT_PCR && testResultType == PathogenTestResultType.POSITIVE))
			|| testType == PathogenTestType.CQ_VALUE_DETECTION) {
			cqValueField.setVisible(true);
		} else {
			cqValueField.setVisible(false);
			cqValueField.clear();
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
		ComboBox testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		testTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		testTypeField.setImmediate(true);
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
		ComboBox diseaseField = addDiseaseField(PathogenTestDto.TESTED_DISEASE, true, create);
		addField(PathogenTestDto.TESTED_DISEASE_DETAILS, TextField.class);
		ComboBox diseaseVariantField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT, ComboBox.class);
		diseaseVariantField.setNullSelectionAllowed(true);
		TextField diseaseVariantDetailsField = addField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		ComboBox testedPathogenField = addField(PathogenTestDto.TESTED_PATHOGEN, ComboBox.class);
		TextField testedPathogenDetailsField = addField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, TextField.class);
		testedPathogenDetailsField.setVisible(false);
		FieldHelper.updateItems(testedPathogenField, FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, null));
		testedPathogenField.addValueChangeListener(e -> {
			Pathogen pathogen = (Pathogen) e.getProperty().getValue();
			if (pathogen.isHasDetails()) {
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

		ComboBox testResultField = addField(PathogenTestDto.TEST_RESULT, ComboBox.class);
		testResultField.removeItem(PathogenTestResultType.NOT_DONE);
		addField(PathogenTestDto.SEROTYPE, TextField.class);

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

		Consumer<Disease> updateDiseaseVariantField = disease -> {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField.setVisible(
				disease != null && isVisibleAllowed(PathogenTestDto.TESTED_DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		};

		// trigger the update, as the disease may already be set
		updateDiseaseVariantField.accept((Disease) diseaseField.getValue());

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			updateDiseaseVariantField.accept(disease);

			FieldHelper.updateItems(
				testTypeField,
				Arrays.asList(PathogenTestType.values()),
				FieldVisibilityCheckers.withDisease(disease),
				PathogenTestType.class);
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			if (testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY) {
				fourFoldIncrease.setVisible(true);
				fourFoldIncrease.setEnabled(caseSampleCount >= 2);
			} else {
				fourFoldIncrease.setVisible(false);
				fourFoldIncrease.setEnabled(false);
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
