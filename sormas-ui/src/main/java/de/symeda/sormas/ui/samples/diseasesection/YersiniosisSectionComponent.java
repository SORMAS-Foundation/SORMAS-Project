/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.Biotype;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class YersiniosisSectionComponent extends AbstractDiseaseSectionComponent {

	private static final List<PathogenSpecie> YERS_SPECIES = Arrays.asList(
		PathogenSpecie.YERSINIA_ENTEROCOLITICA,
		PathogenSpecie.YERSINIA_PSEUDOTUBERCULOSIS,
		PathogenSpecie.YERSINIA_SPP,
		PathogenSpecie.OTHER,
		PathogenSpecie.UNKNOWN);

	private static final List<Serotype> YERS_SEROTYPES = Arrays.asList(
		Serotype.YERSINIOSIS_1,
		Serotype.YERSINIOSIS_2,
		Serotype.YERSINIOSIS_3,
		Serotype.YERSINIOSIS_4,
		Serotype.YERSINIOSIS_5,
		Serotype.YERSINIOSIS_5_27,
		Serotype.YERSINIOSIS_8,
		Serotype.YERSINIOSIS_9,
		Serotype.OTHER,
		Serotype.UNKNOWN);

	private static final List<Biotype> YERS_BIOTYPES = Arrays.asList(
		Biotype.YERSINIOSIS_1,
		Biotype.YERSINIOSIS_1A,
		Biotype.YERSINIOSIS_1B,
		Biotype.YERSINIOSIS_2,
		Biotype.YERSINIOSIS_3,
		Biotype.YERSINIOSIS_4,
		Biotype.YERSINIOSIS_5,
		Biotype.UNKNOWN);

	private ComboBox<PathogenSpecie> specieField;
	private TextField specieTextField;
	private Label specieTextSpacer;

	private ComboBox<Serotype> serotypeField;
	private TextField serotypeTextField;
	private Label serotypeTextSpacer;

	private ComboBox<Biotype> biotypeField;
	private RadioButtonGroup<YesNoUnknown> wgsPerformedField;
	private TextField wgsClusterIdField;
	private CheckBox virulenceGenesDetectedField;

	private PathogenTestType currentTestType;

	@Override
	protected void buildLayout() {
		specieField = createComboBox(PathogenTestDto.SPECIE);
		specieField.setItems(YERS_SPECIES);
		specieField.setItemCaptionGenerator(
			specie -> specie == PathogenSpecie.UNKNOWN ? PathogenTestResultType.INDETERMINATE.toString() : specie.toString());
		specieField.setVisible(false);
		specieTextField = createTextField(PathogenTestDto.SPECIE_TEXT);
		specieTextField.setVisible(false);
		specieTextSpacer = createSpacer();
		addToggleRow(specieField, specieTextField, specieTextSpacer);

		serotypeField = createComboBox(PathogenTestDto.SEROTYPE);
		serotypeField.setItems(YERS_SEROTYPES);
		serotypeField.setItemCaptionGenerator(Serotype::toString);
		serotypeField.setVisible(false);
		serotypeTextField = createTextField(PathogenTestDto.SEROTYPE_TEXT);
		serotypeTextField.setVisible(false);
		serotypeTextSpacer = createSpacer();
		addToggleRow(serotypeField, serotypeTextField, serotypeTextSpacer);

		biotypeField = createComboBox(PathogenTestDto.BIOTYPE);
		biotypeField.setItems(YERS_BIOTYPES);
		biotypeField.setItemCaptionGenerator(Biotype::toString);
		biotypeField.setVisible(false);

		wgsPerformedField = createEnumRadioButtonGroup(PathogenTestDto.WGS_PERFORMED, YesNoUnknown.class);
		wgsPerformedField.setVisible(false);
		addRow(biotypeField, wgsPerformedField);

		wgsClusterIdField = createTextField(PathogenTestDto.WGS_CLUSTER_ID);
		wgsClusterIdField.setVisible(false);
		addRow(wgsClusterIdField, createSpacer());

		virulenceGenesDetectedField = createCheckBox(PathogenTestDto.VIRULENCE_GENES_DETECTED);
		virulenceGenesDetectedField.setVisible(false);
		addRow(virulenceGenesDetectedField, createSpacer());

		binder.forField(specieField).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(specieTextField).bind(PathogenTestDto::getSpecieText, PathogenTestDto::setSpecieText);
		binder.forField(serotypeField).bind(PathogenTestDto::getSerotype, PathogenTestDto::setSerotype);
		binder.forField(serotypeTextField).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);
		binder.forField(biotypeField).bind(PathogenTestDto::getBiotype, PathogenTestDto::setBiotype);
		binder.forField(wgsPerformedField).bind(PathogenTestDto::getWgsPerformed, PathogenTestDto::setWgsPerformed);
		binder.forField(wgsClusterIdField).bind(PathogenTestDto::getWgsClusterId, PathogenTestDto::setWgsClusterId);
		binder.forField(virulenceGenesDetectedField).bind(PathogenTestDto::getVirulenceGenesDetected, PathogenTestDto::setVirulenceGenesDetected);
	}

	@Override
	protected void wireVisibility() {
		track(specieField.addValueChangeListener(e -> {
			boolean showText = specieField.isVisible() && e.getValue() == PathogenSpecie.OTHER;
			specieTextField.setVisible(showText);
			specieTextSpacer.setVisible(!showText);
			if (!showText) {
				specieTextField.clear();
			}
			updateRowAndSelfVisibility();
		}));

		track(serotypeField.addValueChangeListener(e -> {
			boolean showText = serotypeField.isVisible() && e.getValue() == Serotype.OTHER;
			serotypeTextField.setVisible(showText);
			serotypeTextSpacer.setVisible(!showText);
			if (!showText) {
				serotypeTextField.clear();
			}
			updateRowAndSelfVisibility();
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
		}));
	}

	private void updateVisibility() {
		boolean showIsolationFields = currentTestType == PathogenTestType.ISOLATION;
		boolean showVirulence = currentTestType == PathogenTestType.ISOLATION
			|| currentTestType == PathogenTestType.NAAT
			|| currentTestType == PathogenTestType.PCR_RT_PCR;

		specieField.setVisible(showIsolationFields);
		serotypeField.setVisible(showIsolationFields);
		biotypeField.setVisible(showIsolationFields);
		wgsPerformedField.setVisible(showIsolationFields);
		wgsClusterIdField.setVisible(showIsolationFields);
		virulenceGenesDetectedField.setVisible(showVirulence);

		if (!showIsolationFields) {
			setVisibleClear(
				false,
				specieField,
				specieTextField,
				serotypeField,
				serotypeTextField,
				biotypeField,
				wgsPerformedField,
				wgsClusterIdField);
			specieTextSpacer.setVisible(true);
			serotypeTextSpacer.setVisible(true);
		}

		if (!showVirulence) {
			virulenceGenesDetectedField.clear();
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
		dto.setSerotype(null);
		dto.setSerotypeText(null);
		dto.setBiotype(null);
		dto.setWgsPerformed(null);
		dto.setWgsClusterId(null);
		dto.setVirulenceGenesDetected(null);
	}
}
