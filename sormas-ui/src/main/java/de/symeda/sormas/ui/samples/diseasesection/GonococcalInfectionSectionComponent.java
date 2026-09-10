/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License.
 */
package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;

public class GonococcalInfectionSectionComponent extends AbstractDiseaseSectionComponent {

	private TextField porBAllele;
	private TextField tbpBAllele;
	private TextField sequenceType;
	private TextField genogroup;
	private DrugSusceptibilityForm drugSusceptibilityField;

	@Override
	protected void buildLayout() {
		porBAllele = createTextField(PathogenTestDto.POR_B_ALLELE);
		tbpBAllele = createTextField(PathogenTestDto.TBP_B_ALLELE);
		sequenceType = createTextField(PathogenTestDto.SEQUENCE_TYPE);
		genogroup = createTextField(PathogenTestDto.GENOGROUP);
		addRow(porBAllele, tbpBAllele);
		addRow(sequenceType, genogroup);
		binder.forField(porBAllele).bind(PathogenTestDto::getPorBAllele, PathogenTestDto::setPorBAllele);
		binder.forField(tbpBAllele).bind(PathogenTestDto::getTbpBAllele, PathogenTestDto::setTbpBAllele);
		binder.forField(sequenceType).bind(PathogenTestDto::getSequenceType, PathogenTestDto::setSequenceType);
		binder.forField(genogroup).bind(PathogenTestDto::getGenogroup, PathogenTestDto::setGenogroup);
		setGenotypingVisible(false);

		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		addDrugSusceptibilityField(drugSusceptibilityField);
	}

	@Override
	protected void wireVisibility() {
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType type = event.getTestType();
			setGenotypingVisible(type == PathogenTestType.GENOTYPING);
			boolean astVisible = drugSusceptibilityField.updateFieldsVisibility(disease, type);
			setDrugSusceptibilityRowVisible(astVisible);
			if (type == PathogenTestType.GENOTYPING || type == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.NOT_APPLICABLE));
			} else if (type != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	@Override
	public void setDto(PathogenTestDto dto) {
		super.setDto(dto);
		PathogenTestType type = dto == null ? null : dto.getTestType();
		setGenotypingVisible(type == PathogenTestType.GENOTYPING);
		setDrugSusceptibilityRowVisible(drugSusceptibilityField.updateFieldsVisibility(disease, type));
	}

	private void setGenotypingVisible(boolean visible) {
		porBAllele.setVisible(visible);
		tbpBAllele.setVisible(visible);
		sequenceType.setVisible(visible);
		genogroup.setVisible(visible);
		if (!visible) {
			porBAllele.clear();
			tbpBAllele.clear();
			sequenceType.clear();
			genogroup.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected boolean hasVisibleContent() {
		return porBAllele.isVisible() || super.hasVisibleContent();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto != null) {
			dto.setPorBAllele(null);
			dto.setTbpBAllele(null);
			dto.setSequenceType(null);
			dto.setGenogroup(null);
			dto.setDrugSusceptibility(null);
		}
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}
}
