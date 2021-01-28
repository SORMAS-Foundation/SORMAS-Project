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

import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_CASE;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_CONTACT;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_EVENT;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_EVENT_ACTIONS;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_EVENT_PARTICIPANT;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_EVENT_PARTICIPANTS;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_PATHOGEN_TEST;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_SAMPLE;
import static de.symeda.sormas.api.docgeneneration.RootEntityName.ROOT_USER;
import static de.symeda.sormas.api.docgeneneration.TemplateFileType.DOCX;
import static de.symeda.sormas.api.docgeneneration.TemplateFileType.HTML;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum DocumentWorkflow {

	// rootEntityNames define the root variables in a document template.
	// These names are arbitrarily chosen per workflow.

	// In templates for quarantine orders, the root variable "case" refers
	// to either a CaseDataDto or a ContactDto, depending on from where
	// it is called. So "${case.person.firstName}" in the template refers
	// to the case's or contact's person's first name in either case.
	QUARANTINE_ORDER_CASE(Captions.DocumentTemplate_TemplatesCases, "quarantine", DOCX, ROOT_CASE, ROOT_USER, ROOT_SAMPLE, ROOT_PATHOGEN_TEST),
	QUARANTINE_ORDER_CONTACT(Captions.DocumentTemplate_TemplatesContacts,
		"quarantineContact",
		DOCX,
		ROOT_CONTACT,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST),
	QUARANTINE_ORDER_EVENT_PARTICIPANT(Captions.DocumentTemplate_TemplatesEventParticipants,
		"quarantineEventParticipant",
		DOCX,
		ROOT_EVENT_PARTICIPANT,
		ROOT_USER,
		ROOT_SAMPLE,
		ROOT_PATHOGEN_TEST),
	EVENT_HANDOUT(Captions.DocumentTemplate_TemplatesEvents,
		"eventHandout",
		HTML,
		ROOT_EVENT,
		ROOT_USER,
		ROOT_EVENT_ACTIONS,
		ROOT_EVENT_PARTICIPANTS);

	private String name;
	private String templateDirectory;
	private TemplateFileType fileType;
	private List<String> rootEntityNames;

	DocumentWorkflow(String name, String templateDirectory, TemplateFileType fileType, String... rootEntityNames) {
		this.name = name;
		this.templateDirectory = templateDirectory;
		this.fileType = fileType;
		this.rootEntityNames = new ArrayList<>();
		for (String rootEntityName : rootEntityNames) {
			this.rootEntityNames.add(rootEntityName.toLowerCase());
		}
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public TemplateFileType getFileType() {
		return fileType;
	}

	public List<String> getRootEntityNames() {
		return rootEntityNames;
	}

	public String getFileExtension() {
		return fileType.getExtension();
	}

	public boolean isDocx() {
		return fileType == DOCX;
	}

	public String toString() {
		return I18nProperties.getCaption(name);
	}
}
