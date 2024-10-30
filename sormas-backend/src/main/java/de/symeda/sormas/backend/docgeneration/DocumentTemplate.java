/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "documenttemplates")
public class DocumentTemplate extends AbstractDomainObject {

	private static final long serialVersionUID = -8191658284208086022L;

	public static final String WORKFLOW = "workflow";
	public static final String DISEASE = "disease";
	public static final String DOCUMENT_PATH = "documentPath";

	private DocumentWorkflow workflow;
	private Disease disease;
	private String fileName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public DocumentWorkflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(DocumentWorkflow workflow) {
		this.workflow = workflow;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_BIG)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
