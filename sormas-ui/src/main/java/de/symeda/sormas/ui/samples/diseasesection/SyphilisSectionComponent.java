package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SyphilisSerologyMethod;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class SyphilisSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<SyphilisSerologyMethod> serologyMethodField;
	private TextField serologyMethodTextField;
	private Label serologyMethodTextSpacer;

	private PathogenTestType currentTestType;

	@Override
	protected void buildLayout() {
		serologyMethodField = createComboBox(PathogenTestDto.SYPHILIS_SEROLOGY_METHOD);
		serologyMethodField.setItemCaptionGenerator(SyphilisSerologyMethod::toString);
		serologyMethodField.setVisible(false);

		serologyMethodTextField = createTextField(PathogenTestDto.SYPHILIS_SEROLOGY_METHOD_TEXT);
		serologyMethodTextField.setVisible(false);
		serologyMethodTextSpacer = createSpacer();

		addToggleRow(serologyMethodField, serologyMethodTextField, serologyMethodTextSpacer);

		binder.forField(serologyMethodField)
			.bind(PathogenTestDto::getSyphilisSerologyMethod, PathogenTestDto::setSyphilisSerologyMethod);
		binder.forField(serologyMethodTextField)
			.bind(PathogenTestDto::getSyphilisSerologyMethodText, PathogenTestDto::setSyphilisSerologyMethodText);
	}

	@Override
	protected void wireVisibility() {
		track(serologyMethodField.addValueChangeListener(event -> {
			boolean showText = event.getValue() == SyphilisSerologyMethod.OTHER && serologyMethodField.isVisible();
			serologyMethodTextField.setVisible(showText);
			serologyMethodTextSpacer.setVisible(!showText);
			if (!showText) {
				serologyMethodTextField.clear();
			}
			updateRowAndSelfVisibility();
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType previousTestType = currentTestType;
			currentTestType = event.getTestType();
			if (currentTestType != previousTestType && isSerologySubcategory(previousTestType)) {
				serologyMethodField.clear();
			}
			updateVisibility();
		}));
	}

	private static boolean isSerologySubcategory(PathogenTestType testType) {
		return testType == PathogenTestType.NON_TREPONEMAL_TESTS || testType == PathogenTestType.TREPONEMAL_TESTS;
	}

	private void updateVisibility() {
		boolean visible = isSerologySubcategory(currentTestType);
		serologyMethodField.setVisible(visible);
		if (visible) {
			updateComboBoxByDiseaseAndTestType(serologyMethodField, SyphilisSerologyMethod.class, disease, currentTestType);
		} else {
			setVisibleClear(false, serologyMethodField, serologyMethodTextField);
			serologyMethodTextSpacer.setVisible(true);
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSyphilisSerologyMethod(null);
		dto.setSyphilisSerologyMethodText(null);
	}
}
