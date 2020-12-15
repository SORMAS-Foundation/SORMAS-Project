package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.api.i18n.I18nProperties.getPrefixCaption;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
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

	private static final String HTML_LAYOUT = SAMPLE_COMMON_HTML_LAYOUT
		+ fluidRowLocs(Captions.sampleIncludeTestOnCreation)
		+ fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED)
		+ fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TESTED_DISEASE)
		+ fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.TEST_RESULT_TEXT);

	public SampleCreateForm() {
		super(SampleDto.class, SampleDto.I18N_PREFIX);
		setPathogenTestFieldCaptions();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		addCommonFields();
		CheckBox includeTestField = addCustomField(Captions.sampleIncludeTestOnCreation, Boolean.class, CheckBox.class);
		ComboBox pathogenTestResultField = addCustomField(PathogenTestDto.TEST_RESULT, PathogenTestResultType.class, ComboBox.class);
		NullableOptionGroup testVerifiedField = addCustomField(PathogenTestDto.TEST_RESULT_VERIFIED, Boolean.class, NullableOptionGroup.class);
		ComboBox testTypeField = addCustomField(PathogenTestDto.TEST_TYPE, PathogenTestType.class, ComboBox.class);
		ComboBox testDiseaseField = addCustomField(PathogenTestDto.TESTED_DISEASE, Disease.class, ComboBox.class);
		DateTimeField testDateField = addCustomField(
			PathogenTestDto.TEST_DATE_TIME,
			I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME),
			Date.class,
			DateTimeField.class);
		TextArea testDetailsField = addCustomField(PathogenTestDto.TEST_RESULT_TEXT, String.class, TextArea.class);

		initializeRequestedTestFields();

		addValidators();

		setVisibilities();

		FieldHelper.setVisibleWhen(
			includeTestField,
			Arrays.asList(pathogenTestResultField, testVerifiedField, testTypeField, testDiseaseField, testDateField, testDetailsField),
			Arrays.asList(true),
			true);

		FieldHelper.setRequiredWhen(includeTestField, Arrays.asList(pathogenTestResultField), Arrays.asList(true), false, null);

		FieldHelper.setRequiredWhen(
			pathogenTestResultField,
			Arrays.asList(testVerifiedField, testTypeField, testDiseaseField, testDateField),
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

		includeTestField.addValueChangeListener(e -> {
			final Boolean includeTest = (Boolean) e.getProperty().getValue();
			if (includeTest) {
				pathogenTestResultField.setNullSelectionAllowed(false);
				pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
			} else {
				pathogenTestResultField.setNullSelectionAllowed(true);
				pathogenTestResultField.setValue(null);
			}
		});

		addValueChangeListener(e -> {
			defaultValueChangeListener();
			final NullableOptionGroup samplePurposeField = (NullableOptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
			samplePurposeField.setValue(SamplePurpose.EXTERNAL);
			getField(SampleDto.PATHOGEN_TEST_RESULT).setVisible(false);
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private void setPathogenTestFieldCaptions() {

		final ComboBox testResult = (ComboBox) getField(PathogenTestDto.TEST_RESULT);
		final NullableOptionGroup testResultVerified = (NullableOptionGroup) getField(PathogenTestDto.TEST_RESULT_VERIFIED);
		final ComboBox testTypeField = (ComboBox) getField(PathogenTestDto.TEST_TYPE);
		final ComboBox testedDiseaseField = (ComboBox) getField(PathogenTestDto.TESTED_DISEASE);
		final DateTimeField testDateField = (DateTimeField) getField(PathogenTestDto.TEST_DATE_TIME);
		final TextArea testTextField = (TextArea) getField(PathogenTestDto.TEST_RESULT_TEXT);

		testResult.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT));
		testResultVerified.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED));
		testTypeField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE));
		testedDiseaseField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE));
		testDateField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME));
		testTextField.setCaption(getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_TEXT));
	}
}
