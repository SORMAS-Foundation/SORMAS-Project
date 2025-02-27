/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.survey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.docgeneration.DocumentTemplate;

@Entity(name = "surveys")
public class Survey extends AbstractDomainObject {

	private static final long serialVersionUID = -4596338259032187530L;

	public static final String NAME = "name";
	public static final String DISEASE = "disease";
	public static final String DOCUMENT_TEMPLATE = "documentTemplate";
	public static final String EMAIL_TEMPLATE = "emailTemplate";

	private String name;
	private Disease disease;
	private DocumentTemplate documentTemplate;
	private DocumentTemplate emailTemplate;

	@Column(nullable = false, length = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public DocumentTemplate getDocumentTemplate() {
		return documentTemplate;
	}

	public void setDocumentTemplate(DocumentTemplate documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public DocumentTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(DocumentTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
