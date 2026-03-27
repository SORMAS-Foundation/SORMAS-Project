package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

public class ImiSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> IMI_TEST_TYPES = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING,
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY);

	private ComboBox<SeroGroupSpecification> seroGroupSpecField;
	private TextField seroGroupSpecTextField;
	private Label seroGroupSpecTextSpacer;
	private DrugSusceptibilityForm drugSusceptibilityField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {

		seroGroupSpecField = createComboBox(PathogenTestDto.SERO_GROUP_SPECIFICATION);
		seroGroupSpecField.setItems(SeroGroupSpecification.values());
		seroGroupSpecField.setItemCaptionGenerator(SeroGroupSpecification::toString);
		seroGroupSpecField.setVisible(false);

		seroGroupSpecTextField = createTextField(PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT);
		seroGroupSpecTextField.setVisible(false);

		seroGroupSpecTextSpacer = createSpacer();
		addToggleRow(seroGroupSpecField, seroGroupSpecTextField, seroGroupSpecTextSpacer);

		binder.forField(seroGroupSpecField).bind(PathogenTestDto::getSeroGroupSpecification, PathogenTestDto::setSeroGroupSpecification);
		binder.forField(seroGroupSpecTextField).bind(PathogenTestDto::getSeroGroupSpecificationText, PathogenTestDto::setSeroGroupSpecificationText);

		// DrugSusceptibilityForm — legacy v7, bound via parent FieldGroup
		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		addDrugSusceptibilityField(drugSusceptibilityField);
	}

	@Override
	protected void wireVisibility() {
		// Self-managed: seroGroupSpecText visible only when OTHER
		seroGroupSpecField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == SeroGroupSpecification.OTHER;
			seroGroupSpecTextField.setVisible(showText);
			seroGroupSpecTextSpacer.setVisible(!showText);
			if (!showText) {
				seroGroupSpecTextField.clear();
			}
		});

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();

			// Drug susceptibility visibility
			if (drugSusceptibilityField != null) {
				boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, currentTestType);
				setDrugSusceptibilityRowVisible(visible);
			}

			// Auto-set test result (applies to all countries; Luxembourg-only guard
			// on ANTIBIOTIC_SUSCEPTIBILITY was previously redundant with this list)
			if (currentTestType != null && AUTO_POSITIVE_TYPES.contains(currentTestType)) {
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
		boolean visible = currentResult == PathogenTestResultType.POSITIVE && IMI_TEST_TYPES.contains(currentTestType);
		seroGroupSpecField.setVisible(visible);
		if (!visible) {
			seroGroupSpecField.clear();
			seroGroupSpecTextField.setVisible(false);
			seroGroupSpecTextField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSeroGroupSpecification(null);
		dto.setSeroGroupSpecificationText(null);
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}

}
