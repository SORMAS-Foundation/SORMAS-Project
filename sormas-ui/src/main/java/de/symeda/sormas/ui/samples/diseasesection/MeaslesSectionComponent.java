package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class MeaslesSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<GenoTypeResult> genoTypeResultField;
	private TextField genoTypeResultTextField;
	private Label genoTypeResultTextSpacer;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		genoTypeResultField = createComboBox(PathogenTestDto.GENOTYPE_RESULT);
		genoTypeResultField.setItemCaptionGenerator(GenoTypeResult::toString);
		genoTypeResultField.setVisible(false);
		updateGenoTypeItems(disease);

		genoTypeResultTextField = createTextField(PathogenTestDto.GENOTYPE_RESULT_TEXT);
		genoTypeResultTextField.setVisible(false);

		genoTypeResultTextSpacer = createSpacer();
		addToggleRow(genoTypeResultField, genoTypeResultTextField, genoTypeResultTextSpacer);

		binder.forField(genoTypeResultField).bind(PathogenTestDto::getGenoTypeResult, PathogenTestDto::setGenoTypeResult);
		binder.forField(genoTypeResultTextField).bind(PathogenTestDto::getGenoTypeResultText, PathogenTestDto::setGenoTypeResultText);
	}

	@Override
	protected void wireVisibility() {
		// Self-managed: genoTypeResultText visible only when OTHER selected
		genoTypeResultField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == GenoTypeResult.OTHER && genoTypeResultField.isVisible();
			genoTypeResultTextField.setVisible(showText);
			genoTypeResultTextSpacer.setVisible(!showText);
			if (!showText) {
				genoTypeResultTextField.clear();
			}
		});

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
			updateGenoTypeItems(disease);

			// Auto-set test result
			if (currentTestType == PathogenTestType.GENOTYPING) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			updateVisibility();
		}));

	}

	private void updateVisibility() {
		boolean visible = currentTestType == PathogenTestType.GENOTYPING && currentResult == PathogenTestResultType.POSITIVE;
		genoTypeResultField.setVisible(visible);
		if (!visible) {
			genoTypeResultField.clear();
			genoTypeResultTextField.setVisible(false);
			genoTypeResultTextField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setGenoTypeResult(null);
		dto.setGenoTypeResultText(null);
	}

	private void updateGenoTypeItems(Disease disease) {
		updateComboBoxByDisease(genoTypeResultField, GenoTypeResult.class, disease);
	}

}
