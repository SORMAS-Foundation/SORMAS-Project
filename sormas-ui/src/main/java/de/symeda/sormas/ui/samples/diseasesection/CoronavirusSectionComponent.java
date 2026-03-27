package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class CoronavirusSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<PCRTestSpecification> pcrTestSpecification;
	private PathogenTestType currentTestType;

	@Override
	protected void buildLayout() {

		pcrTestSpecification = createComboBox(PathogenTestDto.PCR_TEST_SPECIFICATION);
		pcrTestSpecification.setItems(PCRTestSpecification.values());
		pcrTestSpecification.setItemCaptionGenerator(PCRTestSpecification::toString);
		pcrTestSpecification.setVisible(false);
		addRow(pcrTestSpecification);

		binder.forField(pcrTestSpecification).bind(PathogenTestDto::getPcrTestSpecification, PathogenTestDto::setPcrTestSpecification);
	}

	@Override
	protected void wireVisibility() {
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();

			// Clear test result on test type change (no auto-set for Coronavirus)
			if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setPcrTestSpecification(null);
	}

	private void updateVisibility() {
		boolean visible = currentTestType == PathogenTestType.PCR_RT_PCR;
		pcrTestSpecification.setVisible(visible);
		if (!visible) {
			pcrTestSpecification.clear();
		}
		updateRowAndSelfVisibility();
	}

}
