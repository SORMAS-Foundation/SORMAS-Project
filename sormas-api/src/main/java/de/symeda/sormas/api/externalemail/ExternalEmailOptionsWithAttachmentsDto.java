/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.api.externalemail;

import java.io.Serializable;
import java.util.Properties;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EmailAttachementDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderDocumentOptionsDto;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.i18n.Validations;

@AuditedClass
public class ExternalEmailOptionsWithAttachmentsDto implements Serializable {

	private static final long serialVersionUID = 1005305870535265541L;

	public static final String I18N_PREFIX = "ExternalEmailOptionsWithAttachments";

	public static final String TEMPLATE_NAME = "templateName";
	public static final String RECIPIENT_EMAIL = "recipientEmail";
	public static final String ATTACHED_DOCUMENTS = "attachedDocuments";

	@NotNull(message = Validations.requiredField)
	private final DocumentWorkflow documentWorkflow;
	@NotNull(message = Validations.requiredField)
	private final RootEntityType rootEntityType;
	@NotNull(message = Validations.requiredField)
	@Size(min = 1, message = Validations.requiredField)
	private String templateName;
	private Set<EmailAttachementDto> attachedDocuments;

	private QuarantineOrderDocumentOptionsDto quarantineOrderDocumentOptionsDto;

	public ExternalEmailOptionsWithAttachmentsDto(DocumentWorkflow documentWorkflow, RootEntityType rootEntityType) {
		this.documentWorkflow = documentWorkflow;
		this.rootEntityType = rootEntityType;
	}

	public DocumentWorkflow getDocumentWorkflow() {
		return documentWorkflow;
	}

	public RootEntityType getRootEntityType() {
		return rootEntityType;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Set<EmailAttachementDto> getAttachedDocuments() {
		return attachedDocuments;
	}

	public void setAttachedDocuments(Set<EmailAttachementDto> attachedDocuments) {
		this.attachedDocuments = attachedDocuments;
	}

	public QuarantineOrderDocumentOptionsDto getQuarantineOrderDocumentOptionsDto() {
		return quarantineOrderDocumentOptionsDto;
	}

	public void setQuarantineOrderDocumentOptionsDto(QuarantineOrderDocumentOptionsDto quarantineOrderDocumentOptionsDto) {
		this.quarantineOrderDocumentOptionsDto = quarantineOrderDocumentOptionsDto;
	}
}
