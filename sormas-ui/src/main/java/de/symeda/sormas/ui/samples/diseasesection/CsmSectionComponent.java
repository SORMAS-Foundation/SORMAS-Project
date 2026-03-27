package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class CsmSectionComponent extends AbstractDiseaseSectionComponent {

	private TextField serotypeField;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		serotypeField = createTextField(PathogenTestDto.SEROTYPE);
		serotypeField.setVisible(false);
		addRow(serotypeField, createSpacer());

		binder.forField(serotypeField).bind(PathogenTestDto::getSerotype, PathogenTestDto::setSerotype);
	}

	@Override
	protected void wireVisibility() {
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType testType = event.getTestType();

			// Clear test result on test type change (no auto-set for CSM)
			if (testType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			updateVisibility();
		}));
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSerotype(null);
	}

	private void updateVisibility() {
		boolean visible = currentResult == PathogenTestResultType.POSITIVE;
		serotypeField.setVisible(visible);
		if (!visible) {
			serotypeField.clear();
		}
		updateRowAndSelfVisibility();
	}

}
