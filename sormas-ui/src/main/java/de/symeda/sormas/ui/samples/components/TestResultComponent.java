package de.symeda.sormas.ui.samples.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.RadioButtonGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
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
 * Four-fold increase and CT/CQ values are in {@link FourFoldCtCqComponent}.
 */
public class TestResultComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final boolean isLuxembourg;

	private ComboBox<PathogenTestResultType> testResultField;
	private RadioButtonGroup<Boolean> testResultVerifiedField;
	private RadioButtonGroup<Boolean> preliminaryField;

	private Disease currentDisease;
	private PathogenTestType currentTestType;

	public TestResultComponent(FormEventBus eventBus, boolean isLuxembourg, Disease initialDisease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.isLuxembourg = isLuxembourg;
		this.currentDisease = initialDisease;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		testResultField = createComboBox(PathogenTestDto.TEST_RESULT, PathogenTestDto.I18N_PREFIX);
		List<PathogenTestResultType> resultTypes = new ArrayList<>(java.util.Arrays.asList(PathogenTestResultType.values()));
		resultTypes.remove(PathogenTestResultType.NOT_DONE);
		if (!isLuxembourg) {
			resultTypes.remove(PathogenTestResultType.NOT_APPLICABLE);
		}
		testResultField.setItems(resultTypes);
		testResultField.setItemCaptionGenerator(PathogenTestResultType::toString);

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
	}

	private void bindFields() {
		binder.forField(testResultField).asRequired().bind(PathogenTestDto::getTestResult, PathogenTestDto::setTestResult);
		binder.forField(testResultVerifiedField).bind(PathogenTestDto::getTestResultVerified, PathogenTestDto::setTestResultVerified);
		binder.forField(preliminaryField).bind(PathogenTestDto::getPreliminary, PathogenTestDto::setPreliminary);
	}

	private void wireEvents() {
		// Test result changed -> fire event
		track(testResultField.addValueChangeListener(e -> {
			eventBus.fire(new TestResultChangedEvent(e.getValue()));
		}));

		// Listen for test type changes -> clear result when type is cleared
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			if (currentTestType == null) {
				testResultField.clear();
				testResultField.setEnabled(true);
			}
		}));

		// Disease sections fire this to request a specific test result value
		track(eventBus.on(SetTestResultEvent.class, event -> {
			if (event.getTestResult() != null) {
				testResultField.setValue(event.getTestResult());
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
