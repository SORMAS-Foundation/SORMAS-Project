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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class QuarantineOrderDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String QUARANTINE_LOC = "quarantine";

	public static void addComponentToLayout(CustomLayout targetLayout, CaseReferenceDto caze, DocumentListComponent documentList) {
		addComponentToLayout(targetLayout, caze, DocumentWorkflow.QUARANTINE_ORDER_CASE, new SampleCriteria().caze(caze), documentList);
	}

	public static void addComponentToLayout(CustomLayout targetLayout, ContactReferenceDto contact, DocumentListComponent documentList) {
		addComponentToLayout(targetLayout, contact, DocumentWorkflow.QUARANTINE_ORDER_CONTACT, new SampleCriteria().contact(contact), documentList);
	}

	public static void addComponentToLayout(CustomLayout targetLayout, TravelEntryReferenceDto travelEntry, DocumentListComponent documentList) {
		addComponentToLayout(targetLayout, travelEntry, DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY, null, documentList);
	}

	public static void addComponentToLayout(
		CustomLayout targetLayout,
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria) {
		if (isDocGenerationAllowed()) {
			QuarantineOrderDocumentsComponent docGenerationComponent = new QuarantineOrderDocumentsComponent(referenceDto, workflow, sampleCriteria);
			docGenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docGenerationComponent, QUARANTINE_LOC);
		}
	}

	public static void addComponentToLayout(
		CustomLayout targetLayout,
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		DocumentListComponent documentListComponent) {
		if (isDocGenerationAllowed()) {
			QuarantineOrderDocumentsComponent docGenerationComponent =
				new QuarantineOrderDocumentsComponent(referenceDto, workflow, sampleCriteria, documentListComponent);
			docGenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docGenerationComponent, QUARANTINE_LOC);
		}
	}

	public QuarantineOrderDocumentsComponent(ReferenceDto referenceDto, DocumentWorkflow workflow, SampleCriteria sampleCriteria) {
		super();
		addDocumentBar(
			() -> ControllerProvider.getDocGenerationController().showQuarantineOrderDocumentDialog(referenceDto, workflow, sampleCriteria, null),
			Captions.DocumentTemplate_QuarantineOrder);
	}

	public QuarantineOrderDocumentsComponent(
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		DocumentListComponent documentListComponent) {
		super();
		addDocumentBar(
			() -> ControllerProvider.getDocGenerationController()
				.showQuarantineOrderDocumentDialog(referenceDto, workflow, sampleCriteria, documentListComponent),
			Captions.DocumentTemplate_QuarantineOrder);
	}
}
