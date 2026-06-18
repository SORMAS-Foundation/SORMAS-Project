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
package de.symeda.sormas.ui.samples.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.ResultValueType;
import de.symeda.sormas.api.sample.SmearGrade;
import de.symeda.sormas.api.sample.WesternBlotInterpretation;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.samples.events.ViaLimsChangedEvent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Test result, verified, and preliminary fields.
 * as a standalone composable component
 */
public class TestResultComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final boolean isLuxembourg;
	private final boolean resultRequired;

	private ComboBox<PathogenTestResultType> testResultField;
	private RadioButtonGroup<Boolean> testResultVerifiedField;
	private RadioButtonGroup<Boolean> preliminaryField;
	private TextField quantitativeValueField;
	private TextField quantitativeUnitField;
	private TextField quantitativeTextField;
	private RadioButtonGroup<YesNoUnknown> quantitativeBooleanField;
	private ComboBox<SmearGrade> smearGradeField;
	private ComboBox<WesternBlotInterpretation> westernBlotInterpretationField;
	private HorizontalLayout quantitativeValueRow;
	private HorizontalLayout quantitativeEnumRow;
	private HorizontalLayout quantitativeTextRow;

	private List<PathogenTestResultType> resultTypes;

	private Disease currentDisease;
	private PathogenTestType currentTestType;

	public TestResultComponent(FormEventBus eventBus, boolean isLuxembourg, Disease initialDisease, boolean resultRequired) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.isLuxembourg = isLuxembourg;
		this.resultRequired = resultRequired;
		this.currentDisease = initialDisease;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		testResultField = createComboBox(PathogenTestDto.TEST_RESULT, PathogenTestDto.I18N_PREFIX);
		resultTypes = new ArrayList<>(java.util.Arrays.asList(PathogenTestResultType.values()));
		resultTypes.remove(PathogenTestResultType.NOT_DONE);
		if (!isLuxembourg) {
			resultTypes.remove(PathogenTestResultType.NOT_APPLICABLE);
		}
		testResultField.setItems(resultTypes);
		testResultField.setItemCaptionGenerator(PathogenTestResultType::toString);
		testResultField.setRequiredIndicatorVisible(resultRequired);

		testResultVerifiedField = createBooleanRadioGroup(PathogenTestDto.TEST_RESULT_VERIFIED, PathogenTestDto.I18N_PREFIX);

		preliminaryField = createBooleanRadioGroup(PathogenTestDto.PRELIMINARY, PathogenTestDto.I18N_PREFIX);
		preliminaryField.removeStyleName(CssStyles.CAPTION_ON_TOP);
		CssStyles.style(preliminaryField, CssStyles.VSPACE_4);

		addRow(
			new float[] {
				6.4f,
				4.1f,
				2 },
			testResultField,
			testResultVerifiedField,
			preliminaryField);

		quantitativeValueField = createTextField(PathogenTestDto.QUANTITATIVE_VALUE, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		quantitativeUnitField = createTextField(PathogenTestDto.QUANTITATIVE_UNIT, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		quantitativeTextField = createTextField(PathogenTestDto.QUANTITATIVE_TEXT, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		quantitativeBooleanField = createEnumRadioGroup(PathogenTestDto.QUANTITATIVE_BOOLEAN, PathogenTestDto.I18N_PREFIX, YesNoUnknown.class);
		smearGradeField = createComboBox(PathogenTestDto.SMEAR_GRADE, PathogenTestDto.I18N_PREFIX);
		smearGradeField.setItems(SmearGrade.values());
		smearGradeField.setItemCaptionGenerator(SmearGrade::toString);
		westernBlotInterpretationField = createComboBox(PathogenTestDto.WESTERN_BLOT_INTERPRETATION, PathogenTestDto.I18N_PREFIX);
		westernBlotInterpretationField.setItems(WesternBlotInterpretation.values());
		westernBlotInterpretationField.setItemCaptionGenerator(WesternBlotInterpretation::toString);

		// All result-bearing fields are mandatory only when the administrator requires a result (#13948
		// issue #13958). The same flag that gates the Pos/Neg selector gates the quantitative result fields.
		quantitativeBooleanField.setRequiredIndicatorVisible(resultRequired);
		smearGradeField.setRequiredIndicatorVisible(resultRequired);
		westernBlotInterpretationField.setRequiredIndicatorVisible(resultRequired);

		quantitativeValueRow = addRow(
			new float[] {
				2.4f,
				2.4f,
				5 },
			quantitativeValueField,
			quantitativeUnitField,
			createSpacer());
		quantitativeEnumRow = addRow(
			new float[] {
				1.6f,
				1.6f,
				1.6f,
				5 },
			quantitativeBooleanField,
			smearGradeField,
			westernBlotInterpretationField,
			createSpacer());
		quantitativeTextRow = addRow(quantitativeTextField, createSpacer());

		updateQuantitativeFieldsVisibility(currentTestType);
	}

	private void bindFields() {
		binder.forField(testResultField).bind(PathogenTestDto::getTestResult, PathogenTestDto::setTestResult);
		binder.forField(testResultVerifiedField).bind(PathogenTestDto::getTestResultVerified, PathogenTestDto::setTestResultVerified);
		binder.forField(preliminaryField).bind(PathogenTestDto::getPreliminary, PathogenTestDto::setPreliminary);

		binder.forField(quantitativeValueField)
			.withConverter(new de.symeda.sormas.ui.utils.StringToFloatNullableConverter(quantitativeValueField.getCaption()))
			.bind(PathogenTestDto::getQuantitativeValue, PathogenTestDto::setQuantitativeValue);
		binder.forField(quantitativeUnitField).bind(PathogenTestDto::getQuantitativeUnit, PathogenTestDto::setQuantitativeUnit);
		binder.forField(quantitativeTextField).bind(PathogenTestDto::getQuantitativeText, PathogenTestDto::setQuantitativeText);
		binder.forField(quantitativeBooleanField).bind(PathogenTestDto::getQuantitativeBoolean, PathogenTestDto::setQuantitativeBoolean);
		binder.forField(smearGradeField).bind(PathogenTestDto::getSmearGrade, PathogenTestDto::setSmearGrade);
		binder.forField(westernBlotInterpretationField)
			.bind(PathogenTestDto::getWesternBlotInterpretation, PathogenTestDto::setWesternBlotInterpretation);
	}

	private void wireEvents() {
		// Test result changed -> fire event, re-evaluate the numeric value (only shown for a positive result)
		track(testResultField.addValueChangeListener(e -> {
			eventBus.fire(new TestResultChangedEvent(e.getValue()));
			updateQuantitativeFieldsVisibility(currentTestType);
		}));

		// Listen for test type changes -> clear result when type is cleared, update quantitative fields
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			if (currentTestType == null) {
				testResultField.clear();
				testResultField.setEnabled(true);
			}
			updateQuantitativeFieldsVisibility(currentTestType);
		}));

		// Disease sections fire this (last in the test-type cascade) to request a specific result value.
		track(eventBus.on(SetTestResultEvent.class, event -> {
			if (event.getTestResult() != null) {
				testResultField.setValue(event.getTestResult());
			} else if (!hasQualitativeResult(currentTestType)) {
				// The method has no Positive/Negative result and the selector is hidden, but testResult is
				// mandatory — default it to Not applicable rather than clearing it to an unsaveable null.
				ensureNotApplicableSelectable();
				testResultField.setValue(PathogenTestResultType.NOT_APPLICABLE);
			} else {
				testResultField.clear();
			}
		}));

		// Listen for disease changes -> clear result if disease changed
		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			Disease oldDisease = currentDisease;
			currentDisease = event.getDisease();
			if (currentDisease != oldDisease && currentTestType != null) {
				testResultField.clear();
			}
		}));

		// VIA LIMS -> required state on test result verified
		track(eventBus.on(ViaLimsChangedEvent.class, event -> setTestResultVerifiedRequired(event.isViaLims())));
	}

	/**
	 * Shows the result fields that match the selected method's {@link ResultValueType}s: the qualitative
	 * Positive/Negative selector (hidden when the method has no qualitative component, e.g. a titre or a
	 * sequence), a numeric value with unit, a free-text value, a yes/no detected-flag, a smear grade or a
	 * Western Blot interpretation. Hidden fields are cleared so stale values are not saved.
	 */
	private void updateQuantitativeFieldsVisibility(PathogenTestType testType) {
		Set<ResultValueType> valueTypes = PathogenTestType.getResultValueTypes(testType);

		boolean hasQualitative = testType == null || valueTypes.contains(ResultValueType.QUALITATIVE);
		boolean hasNumeric = valueTypes.contains(ResultValueType.NUMERIC);
		boolean showText = valueTypes.contains(ResultValueType.TEXT);
		boolean showBoolean = valueTypes.contains(ResultValueType.BOOLEAN);
		boolean showSmearGrade = valueTypes.contains(ResultValueType.SMEAR_GRADE);
		boolean showWesternBlot = valueTypes.contains(ResultValueType.WESTERN_BLOT);

		// The qualitative selector is also hidden when a disease section has set the result to "Not
		// applicable" (e.g. AST, whose real result is the drug-susceptibility grid) — keep the value so
		// the stored Not-applicable is preserved, just don't show an irrelevant selector.
		boolean resultNotApplicable = testResultField.getValue() == PathogenTestResultType.NOT_APPLICABLE;
		boolean showQualitative = hasQualitative && !resultNotApplicable;

		// Not applicable is only added on demand for methods without a qualitative result (see
		// ensureNotApplicableSelectable). Once the method has a qualitative result again, drop it back out of
		// the non-Luxembourg combo so it cannot be picked as a stray option, unless it is the current value.
		if (!isLuxembourg && hasQualitative && !resultNotApplicable) {
			removeNotApplicableSelectable();
		}

		// A numeric value only makes sense for a positive result: for a qualitative+numeric method (e.g.
		// RT-PCR) the Ct/value appears only once Positive is selected - for a purely numeric method (no
		// qualitative component) it always applies.
		boolean showNumeric = hasNumeric && (!hasQualitative || testResultField.getValue() == PathogenTestResultType.POSITIVE);

		// Only toggle the selector's visibility — never clear its value here. Typing methods (Genotyping,
		// Serogrouping, ... are value-type TEXT) hide the selector but rely on a disease section having set
		// the result to Positive to reveal their own field; clearing it would wipe that and hide them. The
		// result is managed by the disease sections and the SetTestResultEvent flow, not here.
		testResultField.setVisible(showQualitative);
		setQuantitativeFieldVisible(quantitativeValueField, showNumeric);
		setQuantitativeFieldVisible(quantitativeUnitField, showNumeric);
		setQuantitativeFieldVisible(quantitativeTextField, showText);
		setQuantitativeFieldVisible(quantitativeBooleanField, showBoolean);
		setQuantitativeFieldVisible(smearGradeField, showSmearGrade);
		setQuantitativeFieldVisible(westernBlotInterpretationField, showWesternBlot);

		quantitativeTextField.setRequiredIndicatorVisible(resultRequired && showText && !showWesternBlot);

		updateRowVisibility(quantitativeValueRow);
		updateRowVisibility(quantitativeEnumRow);
		updateRowVisibility(quantitativeTextRow);
	}

	/**
	 * Adds {@link PathogenTestResultType#NOT_APPLICABLE} to the result combo's items if it was filtered
	 * out (it is hidden for non-Luxembourg in {@link #buildLayout}), so the field can hold it as the
	 * default for a method whose result is not Positive/Negative. Vaadin 8 only accepts a value that is
	 * among the combo's items.
	 */
	private void ensureNotApplicableSelectable() {
		if (!resultTypes.contains(PathogenTestResultType.NOT_APPLICABLE)) {
			resultTypes.add(PathogenTestResultType.NOT_APPLICABLE);
			testResultField.setItems(resultTypes);
		}
	}

	/**
	 * Inverse of {@link #ensureNotApplicableSelectable()}: removes {@link PathogenTestResultType#NOT_APPLICABLE}
	 * from the result combo's items again so it does not linger as a stray option in non-Luxembourg workflows
	 * once a method with a qualitative result is selected. The caller must ensure it is not the current value.
	 */
	private void removeNotApplicableSelectable() {
		if (resultTypes.remove(PathogenTestResultType.NOT_APPLICABLE)) {
			testResultField.setItems(resultTypes);
		}
	}

	private static boolean hasQualitativeResult(PathogenTestType testType) {
		return testType == null || PathogenTestType.getResultValueTypes(testType).contains(ResultValueType.QUALITATIVE);
	}

	private void setQuantitativeFieldVisible(com.vaadin.data.HasValue<?> field, boolean visible) {
		((com.vaadin.ui.Component) field).setVisible(visible);
		if (!visible && !field.isEmpty()) {
			field.clear();
		}
	}

	@Override
	public void validate() {
		super.validate();

		// A result is only mandatory when the administrator has enabled it (#13948 issue #13958) - otherwise
		// every result-bearing field may be left empty (the Pos/Neg result then defaults to PENDING on
		// save). The flag gates the qualitative selector and the quantitative result fields alike.
		if (resultRequired) {
			requireIfVisible(testResultField, PathogenTestDto.TEST_RESULT);
			requireIfVisible(quantitativeBooleanField, PathogenTestDto.QUANTITATIVE_BOOLEAN);
			requireIfVisible(smearGradeField, PathogenTestDto.SMEAR_GRADE);
			requireIfVisible(westernBlotInterpretationField, PathogenTestDto.WESTERN_BLOT_INTERPRETATION);
			if (!westernBlotInterpretationField.isVisible()) {
				requireIfVisible(quantitativeTextField, PathogenTestDto.QUANTITATIVE_TEXT);
			}
		}
	}

	private void requireIfVisible(com.vaadin.data.HasValue<?> field, String propertyId) {
		if (((com.vaadin.ui.Component) field).isVisible() && field.isEmpty()) {
			throw new com.vaadin.v7.data.Validator.InvalidValueException(
				de.symeda.sormas.api.i18n.I18nProperties.getValidationError(
					de.symeda.sormas.api.i18n.Validations.required,
					de.symeda.sormas.api.i18n.I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, propertyId)));
		}
	}

	public ComboBox<PathogenTestResultType> getTestResultField() {
		return testResultField;
	}

	public RadioButtonGroup<Boolean> getTestResultVerifiedField() {
		return testResultVerifiedField;
	}

	private void setTestResultVerifiedRequired(boolean required) {
		binder.removeBinding(testResultVerifiedField);
		if (required) {
			binder.forField(testResultVerifiedField)
				.asRequired()
				.bind(PathogenTestDto::getTestResultVerified, PathogenTestDto::setTestResultVerified);
		} else {
			binder.forField(testResultVerifiedField).bind(PathogenTestDto::getTestResultVerified, PathogenTestDto::setTestResultVerified);
		}
	}
}
