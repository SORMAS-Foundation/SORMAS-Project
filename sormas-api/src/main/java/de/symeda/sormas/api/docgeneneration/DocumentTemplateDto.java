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

package de.symeda.sormas.api.docgeneneration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class DocumentTemplateDto extends EntityDto {

	private static final long serialVersionUID = 8591649635400893169L;

	public static final String I18N_PREFIX = "DocumentTemplate";
	public static final String DISEASE = "disease";
	public static final String FILE_NAME = "fileName";

	@NotNull
	private DocumentWorkflow workflow;
	private Disease disease;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String fileName;

	public static DocumentTemplateDto build(DocumentWorkflow documentWorkflow, String fileName) {
		DocumentTemplateDto dto = new DocumentTemplateDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setWorkflow(documentWorkflow);
		dto.setFileName(fileName);

		return dto;
	}

	public static DocumentTemplateDto build(DocumentWorkflow documentWorkflow, String fileName, Disease disease) {
		DocumentTemplateDto dto = build(documentWorkflow, fileName);
		dto.setDisease(disease);

		return dto;
	}

	public DocumentWorkflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(DocumentWorkflow workflow) {
		this.workflow = workflow;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DocumentTemplateReferenceDto toReference() {
		return new DocumentTemplateReferenceDto(getUuid(), fileName);
	}
}
