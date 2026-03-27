package de.symeda.sormas.ui.samples.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.samples.events.ViaLimsChangedEvent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Test result, verified, preliminary, 4-fold increase, CQ/CT values, result text.
 * Vaadin 8 components with own Binder, self-managed visibility.
 */
public class TestResultComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final int caseSampleCount;
	private final boolean isLuxembourg;

	private final CtCqValueComponent ctCqValueComponent;

	private ComboBox<PathogenTestResultType> testResultField;
	private RadioButtonGroup<Boolean> testResultVerifiedField;
	private RadioButtonGroup<Boolean> preliminaryField;
	private CheckBox fourFoldIncrease;
	private TextArea resultText;

	private Disease currentDisease;
	private PathogenTestType currentTestType;

	public TestResultComponent(FormEventBus eventBus, int caseSampleCount, boolean isLuxembourg, Disease initialDisease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.caseSampleCount = caseSampleCount;
		this.isLuxembourg = isLuxembourg;
		this.currentDisease = initialDisease;
		this.ctCqValueComponent = new CtCqValueComponent(isLuxembourg);
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		// Test result
		testResultField = createComboBox(PathogenTestDto.TEST_RESULT, PathogenTestDto.I18N_PREFIX);
		List<PathogenTestResultType> resultTypes = new ArrayList<>(java.util.Arrays.asList(PathogenTestResultType.values()));
		resultTypes.remove(PathogenTestResultType.NOT_DONE);
		if (!isLuxembourg) {
			resultTypes.remove(PathogenTestResultType.NOT_APPLICABLE);
		}
		testResultField.setItems(resultTypes);
		testResultField.setItemCaptionGenerator(PathogenTestResultType::toString);

		// Test result verified (Yes/No)
		testResultVerifiedField = createBooleanRadioGroup(PathogenTestDto.TEST_RESULT_VERIFIED, PathogenTestDto.I18N_PREFIX);

		// Preliminary
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

		// Four-fold increase
		fourFoldIncrease = createCheckBox(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, PathogenTestDto.I18N_PREFIX);
		CssStyles.style(fourFoldIncrease, CssStyles.VSPACE_3, CssStyles.VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);
		addComponent(fourFoldIncrease);

		// CT/CQ values (delegated to CtCqValueComponent)
		addComponent(ctCqValueComponent);

		// Result text
		resultText = createTextArea(PathogenTestDto.TEST_RESULT_TEXT, PathogenTestDto.I18N_PREFIX);
		resultText.setRows(6);
		addFullWidthRow(resultText);
	}

	private void bindFields() {
		binder.forField(testResultField).asRequired().bind(PathogenTestDto::getTestResult, PathogenTestDto::setTestResult);
		binder.forField(testResultVerifiedField).bind(PathogenTestDto::getTestResultVerified, PathogenTestDto::setTestResultVerified);
		binder.forField(preliminaryField).bind(PathogenTestDto::getPreliminary, PathogenTestDto::setPreliminary);
		binder.forField(fourFoldIncrease).bind(PathogenTestDto::isFourFoldIncreaseAntibodyTiter, PathogenTestDto::setFourFoldIncreaseAntibodyTiter);
		binder.forField(resultText).bind(PathogenTestDto::getTestResultText, PathogenTestDto::setTestResultText);
	}

	private void wireEvents() {
		// Test result changed -> fire event + update CQ visibility
		track(testResultField.addValueChangeListener(e -> {
			eventBus.fire(new TestResultChangedEvent(e.getValue()));
			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, e.getValue());
		}));

		// Listen for test type changes
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType testType = event.getTestType();
			currentTestType = testType;

			if (testType != null) {
				// Four fold increase visibility
				if (testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY) {
					fourFoldIncrease.setVisible(true);
					fourFoldIncrease.setEnabled(caseSampleCount >= 2);
				} else {
					fourFoldIncrease.setVisible(false);
					fourFoldIncrease.setEnabled(false);
				}

				// CT fields visibility
				ctCqValueComponent.updateCtVisibility(currentDisease, testType);
			} else {
				testResultField.clear();
				testResultField.setEnabled(true);
				ctCqValueComponent.updateCtVisibility(currentDisease, null);
			}

			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, testResultField.getValue());
		}));

		// Disease sections fire this to request a specific test result value
		track(eventBus.on(SetTestResultEvent.class, event -> {
			if (event.getTestResult() != null) {
				testResultField.setValue(event.getTestResult());
			} else {
				testResultField.clear();
			}
			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, testResultField.getValue());
		}));

		// Listen for disease changes
		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			Disease oldDisease = currentDisease;
			currentDisease = event.getDisease();
			if (currentDisease != oldDisease && currentTestType != null) {
				testResultField.clear();
			}
			ctCqValueComponent.updateCtVisibility(currentDisease, currentTestType);
			ctCqValueComponent.updateCqVisibility(currentDisease, currentTestType, testResultField.getValue());
		}));

		// VIA LIMS -> required state on test result verified
		track(eventBus.on(ViaLimsChangedEvent.class, event -> setTestResultVerifiedRequired(event.isViaLims())));
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

	@Override
	public void setDto(PathogenTestDto dto) {
		super.setDto(dto);
		ctCqValueComponent.setDto(dto);
	}

	@Override
	public void validate() {
		super.validate();
		ctCqValueComponent.validate();
	}

	@Override
	public void applyVisibility(FieldVisibilityCheckers checkers, Class<?> dtoClass) {
		super.applyVisibility(checkers, dtoClass);
		ctCqValueComponent.applyVisibility(checkers, dtoClass);
	}

	@Override
	public void applyAccess(UiFieldAccessCheckers checkers, Class<?> dtoClass) {
		super.applyAccess(checkers, dtoClass);
		ctCqValueComponent.applyAccess(checkers, dtoClass);
	}
}
