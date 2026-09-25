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
package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.Biotype;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.TargetTest;
import de.symeda.sormas.api.sample.TestRunStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetResultTextEvent;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

/**
 * Disease-specific section of the pathogen test form for Diphtheria.
 * Builds the fields for species, biotype, target test (PCR), whole-genome-sequencing (WGS)
 * run status/metadata and drug susceptibility, and drives their visibility based on the
 * selected {@link PathogenTestType} and {@link PathogenTestResultType}.
 * See {@link #updateFieldVisibility()} for the field-visibility rules.
 */
public class DiphtheriaSectionComponent extends AbstractDiseaseSectionComponent {

	private PathogenTestType testType;
	private PathogenTestResultType testResult;
	private FieldVisibilityCheckers visibilityCheckers;

	private DrugSusceptibilityForm drugSusceptibilityField;
	private ComboBox<PathogenSpecie> specieField;
	private TextField specieTextField;

	private ComboBox<Biotype> bioTypeField;
	private TextField bioTypeTextField;

	private ComboBox<TargetTest> targetTestField;
	private TextField targetTestTextField;
	private HorizontalLayout targetTestRow;

	private ComboBox<TestRunStatus> testRunStatus;
	private TextField mlstSequenceTypeTextField;
	private TextField cgMlstClusterTextField;

	private RadioButtonGroup<YesNoUnknown> sequenceDataUploadedToPublicRepField;
	private TextField sraRunIdTextField;
	private TextField accessionNumberTextField;

	/**
	 * Creates and binds all Diphtheria-specific fields. All fields start hidden;
	 * {@link #wireVisibility()} and {@link #updateFieldVisibility()} control when they appear.
	 */
	@Override
	protected void buildLayout() {

		visibilityCheckers = FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());

		specieField = createComboBox(PathogenTestDto.SPECIE);
		specieField.setItemCaptionGenerator(PathogenSpecie::toString);
		// Display the specie, if the test result POSITIVE only.
		specieField.setVisible(false);
		updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, testType);

		specieTextField = createTextField(PathogenTestDto.SPECIE_TEXT);
		specieTextField.setVisible(false);

		addRow(specieField, specieTextField);

		bioTypeField = createComboBox(PathogenTestDto.BIOTYPE);
		bioTypeField.setItemCaptionGenerator(Biotype::toString);
		bioTypeField.setVisible(false);
		updateComboBoxByDisease(bioTypeField, Biotype.class, disease);

		bioTypeTextField = createTextField(PathogenTestDto.BIOTYPE_TEXT);
		bioTypeTextField.setVisible(false);
		addRow(bioTypeField, bioTypeTextField);

		// targetTest
		targetTestField = createComboBox(PathogenTestDto.TARGET_TEST);
		targetTestField.setItemCaptionGenerator(TargetTest::toString);
		updateComboBoxByDisease(targetTestField, TargetTest.class, disease);
		targetTestField.setVisible(false);

		targetTestTextField = createTextField(PathogenTestDto.TARGET_TEST_TEXT);
		targetTestTextField.setVisible(false);
		addRow(targetTestField, targetTestTextField);
		// Rendered above testResultComponent (Diphtheria only) instead of in this section's own body.
		targetTestRow = createRow(targetTestField, targetTestTextField);
		targetTestRow.setVisible(false);
		setPreResultComponent(targetTestRow);

		testRunStatus = createComboBox(PathogenTestDto.TEST_RUN_STATUS);
		testRunStatus.setItemCaptionGenerator(TestRunStatus::toString);
		updateComboBoxByDisease(testRunStatus, TestRunStatus.class, disease);
		testRunStatus.setVisible(false);
		addRow(testRunStatus);

		sequenceDataUploadedToPublicRepField =
			createEnumRadioButtonGroup(PathogenTestDto.SEQUENCE_DATA_UPLOADED_TO_PUBLIC_REPOSITORY, YesNoUnknown.class);
		sequenceDataUploadedToPublicRepField.setVisible(false);
		addRow(sequenceDataUploadedToPublicRepField);
		sraRunIdTextField = createTextField(PathogenTestDto.SRA_RUN_ID);
		sraRunIdTextField.setVisible(false);
		accessionNumberTextField = createTextField(PathogenTestDto.ACCESSION_NUMBER);
		accessionNumberTextField.setVisible(false);

		addRow(sraRunIdTextField, accessionNumberTextField);

		mlstSequenceTypeTextField = createTextField(PathogenTestDto.MLST_SEQUENCE_TYPE);
		mlstSequenceTypeTextField.setVisible(false);
		cgMlstClusterTextField = createTextField(PathogenTestDto.CG_MLST_CLUSTER);
		cgMlstClusterTextField.setVisible(false);
		addRow(mlstSequenceTypeTextField, cgMlstClusterTextField);

		// DrugSusceptibilityForm
		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);

		addDrugSusceptibilityField(drugSusceptibilityField);

		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);

		binder.forField(bioTypeField).bind(PathogenTestDto::getBiotype, PathogenTestDto::setBiotype);
		binder.forField(bioTypeTextField).bind(PathogenTestDto::getBiotypeText, PathogenTestDto::setBiotypeText);
		binder.forField(targetTestField).bind(PathogenTestDto::getTargetTest, PathogenTestDto::setTargetTest);
		binder.forField(targetTestTextField).bind(PathogenTestDto::getTargetTestText, PathogenTestDto::setTargetTestText);
		binder.forField(specieField).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(specieTextField).bind(PathogenTestDto::getSpecieText, PathogenTestDto::setSpecieText);
		binder.forField(testRunStatus).bind(PathogenTestDto::getTestRunStatus, PathogenTestDto::setTestRunStatus);
		binder.forField(sequenceDataUploadedToPublicRepField)
			.bind(PathogenTestDto::getSequenceDataUploadedToPublicRepository, PathogenTestDto::setSequenceDataUploadedToPublicRepository);
		binder.forField(sraRunIdTextField).bind(PathogenTestDto::getSraRunId, PathogenTestDto::setSraRunId);
		binder.forField(accessionNumberTextField).bind(PathogenTestDto::getAccessionNumber, PathogenTestDto::setAccessionNumber);
		binder.forField(mlstSequenceTypeTextField).bind(PathogenTestDto::getMlstSequenceType, PathogenTestDto::setMlstSequenceType);
		binder.forField(cgMlstClusterTextField).bind(PathogenTestDto::getCgMlstCluster, PathogenTestDto::setCgMlstCluster);
	}

	/**
	 * Registers listeners that recompute field visibility whenever the test type, test result,
	 * or a value driving a dependent field (specie, biotype, target test, sequence-upload flag) changes.
	 */

	@Override
	protected void wireVisibility() {

		// specie visible only when test is Culture and its result is POSITIVE
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			eventBus.fire(new SetTestResultEvent(null));
			testType = event.getTestType();
			updateFieldVisibility();
			updateDrugSusceptibility(testType);
			updateComboBoxByDiseaseAndTestType(specieField, PathogenSpecie.class, disease, testType);
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			testResult = event.getTestResult();
			updateFieldVisibility();
		}));

		track(specieField.addValueChangeListener(e -> {
			PathogenSpecie specie = e.getValue();
			// serotypingMethodText visible only when OTHER
			boolean showText = specie == PathogenSpecie.OTHER;
			specieTextField.setVisible(showText);
			if (!showText) {
				specieTextField.clear();
			}
			updateFieldVisibility();
		}));

		track(bioTypeField.addValueChangeListener(e -> {
			Biotype biotype = e.getValue();
			boolean isBioTypeOther = biotype != null && Biotype.OTHER == biotype;
			bioTypeTextField.setVisible(isBioTypeOther);
			if (!isBioTypeOther) {
				setVisibleClear(false, bioTypeTextField);
			}
		}));

		track(targetTestField.addValueChangeListener(e -> {
			TargetTest test = e.getValue();
			updateFieldVisibility();
			boolean showText = TargetTest.OTHER == test;
			targetTestTextField.setVisible(showText);
			if (!showText) {
				setVisibleClear(false, targetTestTextField);
			}

		}));

		track(sequenceDataUploadedToPublicRepField.addValueChangeListener(e -> {
			updateFieldVisibility();
		}));

	}

	/**
	 * Applies the Diphtheria field-visibility rules:
	 * <ul>
	 * <li>Culture test, POSITIVE result → specie field; specie = Corynebacterium diphtheriae → biotype field</li>
	 * <li>PCR test → target test field; target = Species identification → specie field; target = Other → free-text field;
	 * target = Toxin production → result comment pre-filled with "Tox gene detected"</li>
	 * <li>WGS test → run status, MLST sequence type, cgMLST cluster, and the "uploaded to public repository" field;
	 * that field set to YES → SRA run ID and accession number fields</li>
	 * </ul>
	 */
	private void updateFieldVisibility() {
		if (specieField == null || bioTypeField == null) {
			return;
		}
		// Specie should be visible for the culture + positive test.
		boolean isPositive = testResult == PathogenTestResultType.POSITIVE;
		boolean isCulturePositive = isPositive && testType == PathogenTestType.CULTURE;
		boolean isCorynebacteriumSpecie = isCulturePositive && specieField.getValue() == PathogenSpecie.DIPH_CORY_DIPH;

		// PCR test
		boolean isPCRTest = testType == PathogenTestType.PCR_RT_PCR;
		boolean showTargetTest = isPCRTest && isAllowed(PathogenTestDto.TARGET_TEST);
		boolean isPositiveTargetSpecieIdentified = isPositive && showTargetTest && targetTestField.getValue() == TargetTest.SPECIES_IDENTIFICATION;
		boolean isPositiveToxisProduction = isPositive && showTargetTest && targetTestField.getValue() == TargetTest.TOXIN_PRODUCTION;
		boolean isTargetOther = showTargetTest && targetTestField.getValue() == TargetTest.OTHER;

		boolean isWGSTest = testType == PathogenTestType.WHOLE_GENOME_SEQUENCING;
		boolean isSequenceDataUploaded = isWGSTest && sequenceDataUploadedToPublicRepField.getValue() == YesNoUnknown.YES;

		// --- Culture branch ---
		boolean showSpecie = isCulturePositive || isPositiveTargetSpecieIdentified;
		specieField.setVisible(showSpecie);
		if (!showSpecie) {
			setVisibleClear(false, specieField, specieTextField, bioTypeField, bioTypeTextField);
		}

		bioTypeField.setVisible(isCorynebacteriumSpecie);
		if (!isCorynebacteriumSpecie) {
			setVisibleClear(false, bioTypeField, bioTypeTextField);
		}
		if (!isCulturePositive) {
			setVisibleClear(false, bioTypeField);
		}

		// --- PCR branch ---
		showOrClear(showTargetTest, targetTestField, PathogenTestDto.TARGET_TEST);
		targetTestRow.setVisible(showTargetTest);
		showOrClear(isTargetOther, targetTestTextField, PathogenTestDto.TARGET_TEST_TEXT);
		// If PCR positive with toxin production, pre-fill the test result details; otherwise clear the pre-filled text.
		String toxGeneText = I18nProperties.getString(Strings.infoToxGeneDetected);
		if (isPositiveToxisProduction) {
			eventBus.fire(new SetResultTextEvent(toxGeneText, toxGeneText));
		} else {
			eventBus.fire(new SetResultTextEvent(null, toxGeneText));
		}

		// --- WGS branch — deliberately independent of isCulturePositive, which is always false for a WGS test.
		showOrClear(isWGSTest, testRunStatus, PathogenTestDto.TEST_RUN_STATUS);
		showOrClear(isWGSTest, sequenceDataUploadedToPublicRepField, PathogenTestDto.SEQUENCE_DATA_UPLOADED_TO_PUBLIC_REPOSITORY);
		showOrClear(isWGSTest, mlstSequenceTypeTextField, PathogenTestDto.MLST_SEQUENCE_TYPE);
		showOrClear(isWGSTest, cgMlstClusterTextField, PathogenTestDto.CG_MLST_CLUSTER);
		showOrClear(isSequenceDataUploaded, sraRunIdTextField, PathogenTestDto.SRA_RUN_ID);
		showOrClear(isSequenceDataUploaded, accessionNumberTextField, PathogenTestDto.ACCESSION_NUMBER);

		updateRowAndSelfVisibility();
	}

	/** True when @Diseases / @HideForCountriesExcept on PathogenTestDto allow this property for the current disease and country. */
	private boolean isAllowed(String propertyId) {
		return visibilityCheckers.isVisible(PathogenTestDto.class, propertyId);
	}

	/** Shows the field only if the rule wants it AND the DTO annotations allow it; otherwise hides and clears it. */
	private void showOrClear(boolean show, AbstractComponent field, String propertyId) {
		boolean visible = show && isAllowed(propertyId);
		field.setVisible(visible);
		if (!visible) {
			setVisibleClear(false, field);
		}
	}

	private void updateDrugSusceptibility(PathogenTestType testType) {
		if (drugSusceptibilityField != null) {
			boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, testType);
			setDrugSusceptibilityRowVisible(visible);
		}
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setBiotype(null);
		dto.setSpecie(null);
		dto.setSpecieText(null);
		dto.setTargetTest(null);
		dto.setTargetTestText(null);
		dto.setTestRunStatus(null);
		dto.setSraRunId(null);
		dto.setAccessionNumber(null);
		dto.setMlstSequenceType(null);
		dto.setCgMlstCluster(null);
		dto.setBiotypeText(null);
		dto.setSequenceDataUploadedToPublicRepository(null);
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}

}
