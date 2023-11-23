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

package de.symeda.sormas.api.docgeneneration;

import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_CASE;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_CONTACT;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_EVENT;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_EVENT_ACTIONS;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_EVENT_PARTICIPANT;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_EVENT_PARTICIPANTS;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_PATHOGEN_TEST;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_PERSON;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_SAMPLE;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_TRAVEL_ENTRY;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_USER;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_VACCINATION;
import static de.symeda.sormas.api.docgeneneration.TemplateFileType.DOCX;
import static de.symeda.sormas.api.docgeneneration.TemplateFileType.HTML;
import static de.symeda.sormas.api.docgeneneration.TemplateFileType.TXT;

import java.util.Set;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;

public enum DocumentWorkflow {

	// rootEntityNames define the root variables in a document template.
	// These names are arbitrarily chosen per workflow.

	// In templates for quarantine orders, the root variable "case" refers
	// to either a CaseDataDto or a ContactDto, depending on from where
	// it is called. So "${case.person.firstName}" in the template refers
	// to the case's or contact's person's first name in either case.
	QUARANTINE_ORDER_CASE(DocumentWorkflowType.DOCUMENT,
		"quarantine",
		DOCX,
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		ROOT_CASE,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	QUARANTINE_ORDER_CONTACT(DocumentWorkflowType.DOCUMENT,
		"quarantineContact",
		DOCX,
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		ROOT_CONTACT,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	QUARANTINE_ORDER_EVENT_PARTICIPANT(DocumentWorkflowType.DOCUMENT,
		"quarantineEventParticipant",
		DOCX,
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		ROOT_EVENT_PARTICIPANT,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	QUARANTINE_ORDER_TRAVEL_ENTRY(DocumentWorkflowType.DOCUMENT,
		"quarantineTravelEntry",
		DOCX,
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		ROOT_TRAVEL_ENTRY,
		ROOT_PERSON,
		ROOT_USER),
	EVENT_HANDOUT(DocumentWorkflowType.DOCUMENT,
		"eventHandout",
		HTML,
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		ROOT_EVENT,
		ROOT_USER,
		ROOT_EVENT_ACTIONS,
		ROOT_EVENT_PARTICIPANTS),
	CASE_EMAIL(DocumentWorkflowType.EMAIL,
		Constants.EMAIL_TEMPLATES_FOLDER + "/cases",
		TXT,
		UserRight.EMAIL_TEMPLATE_MANAGEMENT,
		ROOT_CASE,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	CONTACT_EMAIL(DocumentWorkflowType.EMAIL,
		Constants.EMAIL_TEMPLATES_FOLDER + "/contacts",
		TXT,
		UserRight.EMAIL_TEMPLATE_MANAGEMENT,
		ROOT_CONTACT,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	EVENT_PARTICIPANT_EMAIL(DocumentWorkflowType.EMAIL,
		Constants.EMAIL_TEMPLATES_FOLDER + "/eventParticipants",
		TXT,
		UserRight.EMAIL_TEMPLATE_MANAGEMENT,
		ROOT_EVENT_PARTICIPANT,
		ROOT_PERSON,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST,
		ROOT_VACCINATION),
	TRAVEL_ENTRY_EMAIL(DocumentWorkflowType.EMAIL,
		Constants.EMAIL_TEMPLATES_FOLDER + "/travelEntries",
		TXT,
		UserRight.EMAIL_TEMPLATE_MANAGEMENT,
		ROOT_TRAVEL_ENTRY,
		ROOT_PERSON,
		ROOT_USER);

	private final DocumentWorkflowType type;
	private final String templateDirectory;
	private final TemplateFileType fileType;

	private final UserRight managementUserRight;
	private final Set<RootEntityType> rootEntityTypes;

	DocumentWorkflow(
		DocumentWorkflowType type,
		String templateDirectory,
		TemplateFileType fileType,
		UserRight managementUserRight,
		RootEntityType... rootEntityTypes) {
		this.type = type;
		this.templateDirectory = templateDirectory;
		this.fileType = fileType;
		this.managementUserRight = managementUserRight;
		this.rootEntityTypes = Set.of(rootEntityTypes);
	}

	public DocumentWorkflowType getType() {
		return type;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public TemplateFileType getFileType() {
		return fileType;
	}

	public Set<RootEntityType> getRootEntityTypes() {
		return rootEntityTypes;
	}

	public String getFileExtension() {
		return fileType.getExtension();
	}

	public boolean isDocx() {
		return fileType == DOCX;
	}

	public UserRight getManagementUserRight() {
		return managementUserRight;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	private static class Constants {

		public static final String EMAIL_TEMPLATES_FOLDER = "emailTemplates";
	}
}
