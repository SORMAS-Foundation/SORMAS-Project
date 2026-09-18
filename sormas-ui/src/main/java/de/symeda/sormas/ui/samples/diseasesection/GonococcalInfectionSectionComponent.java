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
	private PathogenTestType currentTestType;

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
			currentTestType = event.getTestType();
			setGenotypingVisible(currentTestType == PathogenTestType.GENOTYPING);
			boolean astVisible = drugSusceptibilityField.updateFieldsVisibility(disease, currentTestType);
			setDrugSusceptibilityRowVisible(astVisible);
			if (currentTestType == PathogenTestType.GENOTYPING || currentTestType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.NOT_APPLICABLE));
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	private void setGenotypingVisible(boolean visible) {
		porBAllele.setVisible(visible);
		tbpBAllele.setVisible(visible);
		sequenceType.setVisible(visible);
		genogroup.setVisible(visible);
		updateRowAndSelfVisibility();
	}

	@Override
	protected boolean hasVisibleContent() {
		return porBAllele.isVisible() || super.hasVisibleContent();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setPorBAllele(null);
		dto.setTbpBAllele(null);
		dto.setSequenceType(null);
		dto.setGenogroup(null);
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
