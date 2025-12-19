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

package de.symeda.sormas.api.infrastructure.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class FormBuilderDto extends InfrastructureDto {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "forms";
	public static final String I18N_PREFIX = "FormBuilder";

	public static final String UUID = "uuid";
	public static final String FORM_TYPE = "formType";
	public static final String DISEASE = "disease";
	public static final String ACTIVE = "active";
	public static final String FORM_FIELDS = "formFields";

	private FormType formType;
	private Disease disease;
	private Boolean active;
	@Valid
	private List<FormFieldReferenceDto> formFields = new ArrayList<>();

	public static FormBuilderDto build() {
		FormBuilderDto dto = new FormBuilderDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	@Enumerated(EnumType.STRING)
	public FormType getFormType() {
		return formType;
	}

	public void setFormType(FormType formType) {
		this.formType = formType;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<FormFieldReferenceDto> getFormFields() {
		return formFields;
	}

	public void setFormFields(List<FormFieldReferenceDto> formFields) {
		this.formFields = formFields;
	}
}

