/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.YersiniaBiotype;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

/**
 * Disease-specific section for Yersiniosis pathogen tests.
 * Displays species, serotype, biotype, WGS, virulence genes, and AST fields
 * based on test type and result.
 */
public class YersiniosisSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenTestType> ISOLATION_TYPES = Arrays.asList(PathogenTestType.ISOLATION);

	private static final List<PathogenTestType> PCR_TYPES = Arrays.asList(PathogenTestType.PCR_RT_PCR);

	private static final List<PathogenTestType> AUTO_POSITIVE_TYPES =
		Arrays.asList(PathogenTestType.CULTURE, PathogenTestType.ISOLATION);

	// Fields for ISOLATION test type
	private ComboBox<PathogenSpecie> specieField;
	private TextField specieTextField;
	private final Label specieTextSpacer = createSpacer();

	private TextField serotypeTextField;

	private ComboBox<YersiniaBiotype> biotypeField;
	private TextField biotypeTextField;
	private final Label biotypeTextSpacer = createSpacer();

	private ComboBox<YesNoUnknown> wgsPerformedField;
	private TextField wgsClusterIdField;

	// Fields for both ISOLATION and PCR
	private ComboBox<YesNoUnknown> virulenceGenesDetectedField;
	private TextArea virulenceGenesDetailsField;

	private DrugSusceptibilityForm drugSusceptibilityField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		// Species
		specieField = createComboBox(PathogenTestDto.SPECIE);
		specieField.setItemCaptionGenerator(PathogenSpecie::toString);
		specieField.setVisible(false);
		updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, currentTestType);

		specieTextField = createTextField(PathogenTestDto.SPECIE_TEXT);
		specieTextField.setVisible(false);

		addToggleRow(specieField, specieTextField, specieTextSpacer);

		// Serotype (free text for Yersinia serotypes like O:3, O:5,27, etc.)
		serotypeTextField = createTextField(PathogenTestDto.SEROTYPE_TEXT);
		serotypeTextField.setVisible(false);
		addRow(serotypeTextField, createSpacer());

		// Biotype
		biotypeField = createComboBox(PathogenTestDto.BIOTYPE);
		biotypeField.setItems(YersiniaBiotype.values());
		biotypeField.setItemCaptionGenerator(YersiniaBiotype::toString);
		biotypeField.setVisible(false);

		biotypeTextField = createTextField(PathogenTestDto.BIOTYPE_TEXT);
		biotypeTextField.setVisible(false);

		addToggleRow(biotypeField, biotypeTextField, biotypeTextSpacer);

		// WGS
		wgsPerformedField = createComboBox(PathogenTestDto.WGS_PERFORMED);
		wgsPerformedField.setItems(YesNoUnknown.values());
		wgsPerformedField.setItemCaptionGenerator(YesNoUnknown::toString);
		wgsPerformedField.setVisible(false);

		wgsClusterIdField = createTextField(PathogenTestDto.WGS_CLUSTER_ID);
		wgsClusterIdField.setVisible(false);
		addRow(wgsPerformedField, wgsClusterIdField);

		// Virulence genes
		virulenceGenesDetectedField = createComboBox(PathogenTestDto.VIRULENCE_GENES_DETECTED);
		virulenceGenesDetectedField.setItems(YesNoUnknown.values());
		virulenceGenesDetectedField.setItemCaptionGenerator(YesNoUnknown::toString);
		virulenceGenesDetectedField.setVisible(false);
		addRow(virulenceGenesDetectedField, createSpacer());

		virulenceGenesDetailsField = createTextArea(PathogenTestDto.VIRULENCE_GENES_DETAILS, PathogenTestDto.I18N_PREFIX);
		virulenceGenesDetailsField.setRows(2);
		virulenceGenesDetailsField.setVisible(false);
		addRow(virulenceGenesDetailsField);

		// Bind fields to DTO
		binder.forField(specieField).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(specieTextField).bind(PathogenTestDto::getSpecieText, PathogenTestDto::setSpecieText);
		binder.forField(serotypeTextField).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);
		binder.forField(biotypeField).bind(PathogenTestDto::getBiotype, PathogenTestDto::setBiotype);
		binder.forField(biotypeTextField).bind(PathogenTestDto::getBiotypeText, PathogenTestDto::setBiotypeText);
		binder.forField(wgsPerformedField).bind(PathogenTestDto::getWgsPerformed, PathogenTestDto::setWgsPerformed);
		binder.forField(wgsClusterIdField).bind(PathogenTestDto::getWgsClusterId, PathogenTestDto::setWgsClusterId);
		binder.forField(virulenceGenesDetectedField)
			.bind(PathogenTestDto::getVirulenceGenesDetected, PathogenTestDto::setVirulenceGenesDetected);
		binder.forField(virulenceGenesDetailsField)
			.bind(PathogenTestDto::getVirulenceGenesDetails, PathogenTestDto::setVirulenceGenesDetails);

		// Drug susceptibility (AST) - legacy v7 binding via FieldGroup
		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		addDrugSusceptibilityField(drugSusceptibilityField);
	}

	@Override
	protected void wireVisibility() {
		// Species text field visible when OTHER selected
		track(specieField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == PathogenSpecie.OTHER && specieField.isVisible();
			specieTextField.setVisible(showText);
			specieTextSpacer.setVisible(!showText);
			if (!showText) {
				specieTextField.clear();
			}
		}));

		// Biotype text field visible when OTHER selected
		track(biotypeField.addValueChangeListener(e -> {
			boolean showText = e.getValue() == YersiniaBiotype.OTHER && biotypeField.isVisible();
			biotypeTextField.setVisible(showText);
			biotypeTextSpacer.setVisible(!showText);
			if (!showText) {
				biotypeTextField.clear();
			}
		}));

		// WGS cluster ID visible when WGS performed = YES
		track(wgsPerformedField.addValueChangeListener(e -> {
			boolean showClusterId = e.getValue() == YesNoUnknown.YES && wgsPerformedField.isVisible();
			wgsClusterIdField.setVisible(showClusterId);
			if (!showClusterId) {
				wgsClusterIdField.clear();
			}
		}));

		// Virulence genes details visible when detected = YES
		track(virulenceGenesDetectedField.addValueChangeListener(e -> {
			boolean showDetails = e.getValue() == YesNoUnknown.YES && virulenceGenesDetectedField.isVisible();
			virulenceGenesDetailsField.setVisible(showDetails);
			if (!showDetails) {
				virulenceGenesDetailsField.clear();
			}
		}));

		// Test type changed
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, currentTestType);
			updateVisibility();

			// Drug susceptibility visibility
			if (drugSusceptibilityField != null) {
				boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, currentTestType);
				setDrugSusceptibilityRowVisible(visible);
			}

			// Auto-set positive result for Culture and Isolation
			if (currentTestType != null && AUTO_POSITIVE_TYPES.contains(currentTestType)) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		// Test result changed
		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			updateVisibility();
		}));
	}

	private void updateVisibility() {
		boolean isPositive = currentResult == PathogenTestResultType.POSITIVE;

		// ISOLATION fields: visible on ISOLATION test type + positive result
		boolean showIsolationFields = isPositive && ISOLATION_TYPES.contains(currentTestType);
		specieField.setVisible(showIsolationFields);
		serotypeTextField.setVisible(showIsolationFields);
		biotypeField.setVisible(showIsolationFields);
		wgsPerformedField.setVisible(showIsolationFields);

		if (!showIsolationFields) {
			specieField.clear();
			specieTextField.setVisible(false);
			specieTextField.clear();
			serotypeTextField.clear();
			biotypeField.clear();
			biotypeTextField.setVisible(false);
			biotypeTextField.clear();
			wgsPerformedField.clear();
			wgsClusterIdField.setVisible(false);
			wgsClusterIdField.clear();
		}

		// Virulence genes: visible on ISOLATION or PCR + positive result
		boolean showVirulenceGenes = isPositive && (ISOLATION_TYPES.contains(currentTestType) || PCR_TYPES.contains(currentTestType));
		virulenceGenesDetectedField.setVisible(showVirulenceGenes);

		if (!showVirulenceGenes) {
			virulenceGenesDetectedField.clear();
			virulenceGenesDetailsField.setVisible(false);
			virulenceGenesDetailsField.clear();
		}

		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSpecie(null);
		dto.setSpecieText(null);
		dto.setSerotypeText(null);
		dto.setBiotype(null);
		dto.setBiotypeText(null);
		dto.setWgsPerformed(null);
		dto.setWgsClusterId(null);
		dto.setVirulenceGenesDetected(null);
		dto.setVirulenceGenesDetails(null);
		dto.setDrugSusceptibility(null);
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}
}
