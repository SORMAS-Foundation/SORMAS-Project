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

package de.symeda.sormas.app.backend.formfield;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

@Entity(name = FormField.TABLE_NAME)
@DatabaseTable(tableName = FormField.TABLE_NAME)
public class FormField extends InfrastructureAdo {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "form_fields";
	public static final String I18N_PREFIX = "FormField";

	public static final String FORM_TYPE = "formType";
	public static final String FIELD_NAME = "fieldName";
	public static final String DESCRIPTION = "description";
	public static final String ACTIVE = "active";
	public static final String DEPENDING_ON = "dependingOn";
	public static final String DEPENDING_ON_VALUES = "dependingOnValues";

	@Enumerated(EnumType.STRING)
	@DatabaseField
	private FormType formType;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	@DatabaseField
	private String fieldName;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	@DatabaseField
	private String description;

	@DatabaseField
	private Boolean active;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	@DatabaseField
	private String dependingOn;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	@DatabaseField
	private String dependingOnValues;

	public FormField() {
	}

	public FormField(String uuid) {
		this.setUuid(uuid);
	}

	@Enumerated(EnumType.STRING)
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

	public String getDependingOn() {
		return dependingOn;
	}

	public void setDependingOn(String dependingOn) {
		this.dependingOn = dependingOn;
	}

	public String getDependingOnValues() {
		return dependingOnValues;
	}

	public void setDependingOnValues(String dependingOnValues) {
		this.dependingOnValues = dependingOnValues;
	}

	/**
	 * Helper method to parse dependingOnValues from comma-separated string to array
	 */
	public String[] getDependingOnValuesArray() {
		if (dependingOnValues == null || dependingOnValues.isEmpty()) {
			return new String[0];
		}
		// Parse comma-separated values
		return dependingOnValues.split(",");
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String buildCaption() {
		return getDescription() != null ? getDescription() : getFieldName();
	}
}




