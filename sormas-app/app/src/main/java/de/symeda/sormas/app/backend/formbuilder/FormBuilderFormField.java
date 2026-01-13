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

package de.symeda.sormas.app.backend.formbuilder;

import javax.persistence.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.formfield.FormField;

@Entity(name = FormBuilderFormField.TABLE_NAME)
@DatabaseTable(tableName = FormBuilderFormField.TABLE_NAME)
public class FormBuilderFormField extends AbstractDomainObject {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "forms_form_fields";
	public static final String I18N_PREFIX = "FormBuilderFormField";

	@DatabaseField(foreign = true, columnName = "form_id", canBeNull = false)
	private FormBuilder formBuilder;

	@DatabaseField(foreign = true, columnName = "formField_id", canBeNull = false)
	private FormField formField;

	@DatabaseField(columnName = "displayOrder")
	private Integer displayOrder;

	public FormBuilderFormField() {
	}

	public FormBuilderFormField(FormBuilder formBuilder, FormField formField) {
		this.formBuilder = formBuilder;
		this.formField = formField;
	}

	public FormBuilder getFormBuilder() {
		return formBuilder;
	}

	public void setFormBuilder(FormBuilder formBuilder) {
		this.formBuilder = formBuilder;
	}

	public FormField getFormField() {
		return formField;
	}

	public void setFormField(FormField formField) {
		this.formField = formField;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}


