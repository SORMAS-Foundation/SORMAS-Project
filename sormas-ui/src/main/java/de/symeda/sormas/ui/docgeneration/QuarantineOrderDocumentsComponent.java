/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.docgeneration;

import static de.symeda.sormas.ui.docgeneration.DocGenerationHelper.isDocGenerationAllowed;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class QuarantineOrderDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String QUARANTINE_LOC = "quarantine";

	public static void addComponentToLayout(CustomLayout targetLayout, CaseDataDto caze, DocumentListComponent documentList) {
		SampleCriteria sampleCriteria = new SampleCriteria().caze(caze.toReference());
		VaccinationListCriteria vaccinationCriteria = new VaccinationListCriteria.Builder(caze.getPerson()).withDisease(caze.getDisease()).build();
		addComponentToLayout(
			targetLayout,
			caze.toReference(),
			DocumentWorkflow.QUARANTINE_ORDER_CASE,
			sampleCriteria,
			vaccinationCriteria,
			documentList);
	}

	public static void addComponentToLayout(CustomLayout targetLayout, ContactDto contact, DocumentListComponent documentList) {
		VaccinationListCriteria vaccinationCriteria =
			new VaccinationListCriteria.Builder(contact.getPerson()).withDisease(contact.getDisease()).build();
		SampleCriteria sampleCriteria = new SampleCriteria().contact(contact.toReference());

		addComponentToLayout(
			targetLayout,
			contact.toReference(),
			DocumentWorkflow.QUARANTINE_ORDER_CONTACT,
			sampleCriteria,
			vaccinationCriteria,
			documentList);
	}

	public static void addComponentToLayout(CustomLayout targetLayout, TravelEntryReferenceDto travelEntry, DocumentListComponent documentList) {
		addComponentToLayout(targetLayout, travelEntry, DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY, null, null, documentList);
	}

	public static void addComponentToLayout(
		CustomLayout targetLayout,
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		VaccinationListCriteria vaccinationCriteria) {
		if (isDocGenerationAllowed()) {
			QuarantineOrderDocumentsComponent docGenerationComponent =
				new QuarantineOrderDocumentsComponent(referenceDto, workflow, sampleCriteria, vaccinationCriteria);
			docGenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docGenerationComponent, QUARANTINE_LOC);
		}
	}

	public static void addComponentToLayout(
		CustomLayout targetLayout,
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		VaccinationListCriteria vaccinationCriteria,
		DocumentListComponent documentListComponent) {
		if (isDocGenerationAllowed()) {
			QuarantineOrderDocumentsComponent docGenerationComponent =
				new QuarantineOrderDocumentsComponent(referenceDto, workflow, sampleCriteria, vaccinationCriteria, documentListComponent);
			docGenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docGenerationComponent, QUARANTINE_LOC);
		}
	}

	public QuarantineOrderDocumentsComponent(
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		VaccinationListCriteria vaccinationCriteria) {
		super();
		addDocumentBar(
			() -> ControllerProvider.getDocGenerationController().showQuarantineOrderDocumentDialog(referenceDto, workflow, sampleCriteria, vaccinationCriteria, null),
			Captions.DocumentTemplate_QuarantineOrder);
	}

	public QuarantineOrderDocumentsComponent(
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		VaccinationListCriteria vaccinationCriteria,
		DocumentListComponent documentListComponent) {
		super();
		addDocumentBar(
			() -> ControllerProvider.getDocGenerationController()
				.showQuarantineOrderDocumentDialog(referenceDto, workflow, sampleCriteria, vaccinationCriteria, documentListComponent),
			Captions.DocumentTemplate_QuarantineOrder);
	}
}
