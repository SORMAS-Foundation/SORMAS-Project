package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.api.i18n.I18nProperties.getPrefixCaption;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class SampleCreateForm extends AbstractSampleForm {

	private static final long serialVersionUID = 1L;

	private static final String HORIZONTAL_RULE = "horizontalRule";

	private static final String HTML_LAYOUT = SAMPLE_COMMON_HTML_LAYOUT
		+ fluidRowLocs(Captions.sampleIncludeTestOnCreation)
		+ fluidRowLocs(HORIZONTAL_RULE)
		+ fluidRowLocs(PathogenTestDto.REPORT_DATE, PathogenTestDto.VIA_LIMS)
		+ fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED)
		+ fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.PCR_TEST_SPECIFICATION)
		+ fluidRowLocs(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TESTED_DISEASE_VARIANT)
		+ fluidRowLocs(PathogenTestDto.CQ_VALUE, PathogenTestDto.TYPING_ID)
		+ fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.TEST_RESULT_TEXT);

	CheckBox includeTestField;

	public SampleCreateForm() {
		super(SampleDto.class, SampleDto.I18N_PREFIX);
		setPathogenTestFieldCaptions();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addCommonFields();
		includeTestField = addCustomField(Captions.sampleIncludeTestOnCreation, Boolean.class, CheckBox.class);
		Label horizontalRule = new Label("<br><hr /><br>", ContentMode.HTML);
		horizontalRule.setWidth(100f, Unit.PERCENTAGE);
		horizontalRule.setVisible(false);
		getContent().addComponent(horizontalRule, HORIZONTAL_RULE);
		ComboBox pathogenTestResultField = addCustomField(PathogenTestDto.TEST_RESULT, PathogenTestResultType.class, ComboBox.class);
		pathogenTestResultField.removeItem(PathogenTestResultType.NOT_DONE);
		NullableOptionGroup testVerifiedField = addCustomField(PathogenTestDto.TEST_RESULT_VERIFIED, Boolean.class, NullableOptionGroup.class);
		DateField reportDateField;
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			reportDateField = addCustomField(
				PathogenTestDto.REPORT_DATE,
				I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.REPORT_DATE),
				Date.class,
				DateField.class);

			CheckBox viaLimsField = addCustomField(PathogenTestDto.VIA_LIMS, Boolean.class, CheckBox.class);

			FieldHelper.setVisibleWhen(includeTestField, Arrays.asList(reportDateField, viaLimsField), Collections.singletonList(true), true);
		}
		ComboBox testTypeField = addCustomField(PathogenTestDto.TEST_TYPE, PathogenTestType.class, ComboBox.class);
		ComboBox pcrTestSpecification = addCustomField(PathogenTestDto.PCR_TEST_SPECIFICATION, PCRTestSpecification.class, ComboBox.class);
		ComboBox testedDiseaseField = addCustomField(PathogenTestDto.TESTED_DISEASE, Disease.class, ComboBox.class);
		ComboBox diseaseVariantField = addCustomField(PathogenTestDto.TESTED_DISEASE_VARIANT, DiseaseVariant.class, ComboBox.class);
		TextField cqValueField = addCustomField(PathogenTestDto.CQ_VALUE, Float.class, TextField.class);
		cqValueField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, cqValueField.getCaption()));
		TextField typingIdField = addCustomField(PathogenTestDto.TYPING_ID, String.class, TextField.class);
		DateTimeField testDateField = addCustomField(
			PathogenTestDto.TEST_DATE_TIME,
			I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME),
			Date.class,
			DateTimeField.class);
		TextArea testDetailsField = addCustomField(PathogenTestDto.TEST_RESULT_TEXT, String.class, TextArea.class);

		initializeRequestedTestFields();

		addValidators();

		setVisibilities();

		diseaseVariantField.setVisible(false);
		pcrTestSpecification.setVisible(false);
		cqValueField.setVisible(false);
		typingIdField.setVisible(false);

		Map<Field, List<Object>> pcrTestSpecificationVisibilityDependencies = new HashMap<Field, List<Object>>() {

			{
				put(testedDiseaseField, Arrays.asList(Disease.CORONAVIRUS));
				put(testTypeField, Arrays.asList(PathogenTestType.PCR_RT_PCR));
			}
		};
		FieldHelper.setVisibleWhen(pcrTestSpecification, pcrTestSpecificationVisibilityDependencies, true);

		FieldHelper.setVisibleWhen(
			includeTestField,
			Arrays.asList(pathogenTestResultField, testVerifiedField, testTypeField, testedDiseaseField, testDateField, testDetailsField),
			Arrays.asList(true),
			true);

		FieldHelper.setRequiredWhen(includeTestField, Arrays.asList(pathogenTestResultField), Arrays.asList(true), false, null);

		FieldHelper.setRequiredWhen(
			pathogenTestResultField,
			Arrays.asList(testVerifiedField, testTypeField, testedDiseaseField),
			Arrays.asList(
				PathogenTestResultType.POSITIVE,
				PathogenTestResultType.NEGATIVE,
				PathogenTestResultType.PENDING,
				PathogenTestResultType.INDETERMINATE),
			false,
			null);

		final DateTimeField sampleDateField = getField(SampleDto.SAMPLE_DATE_TIME);
		testDateField.addValidator(
			new DateComparisonValidator(
				testDateField,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, testDateField.getCaption(), sampleDateField.getCaption())));

		testedDiseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField.setVisible(
				disease != null && isVisibleAllowed(PathogenTestDto.TESTED_DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
			PathogenTestType testType = (PathogenTestType) testTypeField.getValue();
			showPcrTestSpecificationField(pcrTestSpecification, testType, disease);
		});

		pathogenTestResultField.addValueChangeListener(e -> {
			PathogenTestResultType testResult = (PathogenTestResultType) e.getProperty().getValue();
			PathogenTestType testType = (PathogenTestType) testTypeField.getValue();
			showCqValueField(cqValueField, testType, testResult);
		});

		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			PathogenTestResultType testResult = (PathogenTestResultType) pathogenTestResultField.getValue();
			showCqValueField(cqValueField, testType, testResult);
			showPcrTestSpecificationField(pcrTestSpecification, testType, (Disease) testedDiseaseField.getValue());
			if (testType == PathogenTestType.PCR_RT_PCR || testType == PathogenTestType.DNA_MICROARRAY || testType == PathogenTestType.SEQUENCING) {
				typingIdField.setVisible(true);
			} else {
				typingIdField.setVisible(false);
				typingIdField.clear();
			}
		});

		includeTestField.addValueChangeListener(e -> {
			final Boolean includeTest = (Boolean) e.getProperty().getValue();
			horizontalRule.setVisible(includeTest);
			if (includeTest) {
				pathogenTestResultField.setNullSelectionAllowed(false);
				pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
				testedDiseaseField.setValue(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
			} else {
				pathogenTestResultField.setNullSelectionAllowed(true);
				pathogenTestResultField.setValue(null);
			}
		});

		addValueChangeListener(e -> {
			defaultValueChangeListener();
			final NullableOptionGroup samplePurposeField = (NullableOptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
			samplePurposeField.setValue(SamplePurpose.EXTERNAL);
		});
	}

	private void showCqValueField(TextField cqValueField, PathogenTestType testType, PathogenTestResultType testResult) {
		if ((testType == PathogenTestType.PCR_RT_PCR && testResult == PathogenTestResultType.POSITIVE)
			|| testType == PathogenTestType.CQ_VALUE_DETECTION) {
			cqValueField.setVisible(true);
		} else {
			cqValueField.setVisible(false);
			cqValueField.clear();
		}
	}

	private void showPcrTestSpecificationField(ComboBox pcrTestSpecificationField, PathogenTestType testType, Disease disease) {
		if (testType == PathogenTestType.PCR_RT_PCR && disease == Disease.CORONAVIRUS) {
			pcrTestSpecificationField.setVisible(true);
		} else {
			pcrTestSpecificationField.setVisible(false);
			pcrTestSpecificationField.clear();
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private void setPathogenTestFieldCaptions() {

		final ComboBox testResult = (ComboBox) getField(PathogenTestDto.TEST_RESULT);
		final NullableOptionGroup testResultVerified = (NullableOptionGroup) getField(PathogenTestDto.TEST_RESULT_VERIFIED);
		final ComboBox testTypeField = (ComboBox) getField(PathogenTestDto.TEST_TYPE);
		final ComboBox pcrTestSpecification = (ComboBox) getField(PathogenTestDto.PCR_TEST_SPECIFICATION);
		final ComboBox testedDiseaseField = (ComboBox) getField(PathogenTestDto.TESTED_DISEASE);
		final ComboBox testedDiseaseVariantField = (ComboBox) getField(PathogenTestDto.TESTED_DISEASE_VARIANT);
		final TextField cqValueField = (TextField) getField(PathogenTestDto.CQ_VALUE);
		final TextField typingIdField = (TextField) getField(PathogenTestDto.TYPING_ID);
		final DateTimeField testDateField = (DateTimeField) getField(PathogenTestDto.TEST_DATE_TIME);
		final TextArea testTextField = (TextArea) getField(PathogenTestDto.TEST_RESULT_TEXT);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			final DateField reportDateField = getField(PathogenTestDto.REPORT_DATE);
			reportDateField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.REPORT_DATE));

			final CheckBox viaLimsField = getField(PathogenTestDto.VIA_LIMS);
			viaLimsField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.VIA_LIMS));
		}
		testResult.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT));
		testResultVerified.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED));
		testTypeField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE));
		pcrTestSpecification.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.PCR_TEST_SPECIFICATION));
		testedDiseaseField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE));
		testedDiseaseVariantField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT));
		cqValueField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.CQ_VALUE));
		typingIdField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TYPING_ID));
		testDateField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME));
		testTextField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_TEXT));
	}
}
