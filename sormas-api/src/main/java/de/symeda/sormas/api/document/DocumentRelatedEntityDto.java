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

package de.symeda.sormas.api.document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class DocumentRelatedEntityDto extends PseudonymizableDto {

	public static final String RELATED_ENTITY_UUID = "relatedEntityUuid";
	public static final String RELATED_ENTITY_TYPE = "relatedEntityType";

	@NotNull
	private DocumentReferenceDto document;
	@NotBlank(message = Validations.requiredField)
	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = FieldConstraints.CHARACTER_LIMIT_UUID_MIN, max = FieldConstraints.CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String relatedEntityUuid;
	@NotNull(message = Validations.requiredField)
	private DocumentRelatedEntityType relatedEntityType;

	public DocumentRelatedEntityDto() {
	}

	public static DocumentRelatedEntityDto build(DocumentRelatedEntityType documentRelatedEntityType, String relatedEntityUuid) {
		DocumentRelatedEntityDto documentRelatedEntities = new DocumentRelatedEntityDto();
		documentRelatedEntities.setUuid(DataHelper.createUuid());
		documentRelatedEntities.setRelatedEntityType(documentRelatedEntityType);
		documentRelatedEntities.setRelatedEntityUuid(relatedEntityUuid);
		return documentRelatedEntities;
	}

	public DocumentReferenceDto getDocument() {
		return document;
	}

	public void setDocument(DocumentReferenceDto document) {
		this.document = document;
	}

	public String getRelatedEntityUuid() {
		return relatedEntityUuid;
	}

	public void setRelatedEntityUuid(String relatedEntityUuid) {
		this.relatedEntityUuid = relatedEntityUuid;
	}

	public DocumentRelatedEntityType getRelatedEntityType() {
		return relatedEntityType;
	}

	public void setRelatedEntityType(DocumentRelatedEntityType relatedEntityType) {
		this.relatedEntityType = relatedEntityType;
	}
}
