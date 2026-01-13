/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.infrastructure.fields;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class FormFieldIndexDto extends AbstractUuidDto {

	public static final String I18N_PREFIX = "FormFields";

	public static final String FORM_TYPE = "formType";
	public static final String FIELD_NAME = "fieldName";
	public static final String DESCRIPTION = "description";
	public static final String ACTIVE = "active";

	private FormType formType;
	private String fieldName;
	private String description;
	private Boolean active;

	public FormFieldIndexDto() {
		super((String) null);
	}

	public FormFieldIndexDto(String uuid) {
		super(uuid);
	}

	public FormFieldIndexDto(String uuid, FormType formType, String fieldName, String description, Boolean active) {
		super(uuid);
		this.formType = formType;
		this.fieldName = fieldName;
		this.description = description;
		this.active = active;
	}

	public FormType getFormType() {
		return formType;
	}

	public void setFormType(FormType formType) {
		this.formType = formType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

