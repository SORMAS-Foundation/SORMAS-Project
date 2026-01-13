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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.formfield.FormField;
import de.symeda.sormas.app.backend.infrastructure.AbstractInfrastructureAdoDao;

public class FormBuilderDao extends AbstractInfrastructureAdoDao<FormBuilder> {

	private Dao<FormBuilderFormField, Long> formBuilderFormFieldDao;

	public FormBuilderDao(Dao<FormBuilder, Long> innerDao) {
		super(innerDao);
	}

	public FormBuilderDao(Dao<FormBuilder, Long> innerDao, Dao<FormBuilderFormField, Long> formBuilderFormFieldDao) {
		super(innerDao);
		this.formBuilderFormFieldDao = formBuilderFormFieldDao;
	}

	@Override
	protected Class<FormBuilder> getAdoClass() {
		return FormBuilder.class;
	}

	@Override
	public String getTableName() {
		return FormBuilder.TABLE_NAME;
	}

	@Override
	public FormBuilder saveAndSnapshot(FormBuilder source) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get FormBuilder by FormType and Disease
	 */
	public FormBuilder getFormBuilder(FormType formType, Disease disease) {
		try {
			QueryBuilder<FormBuilder, Long> builder = queryBuilder();
			Where<FormBuilder, Long> where = builder.where();
			where.and(
				where.eq(FormBuilder.FORM_TYPE, formType),
				where.eq(FormBuilder.DISEASE, disease),
				where.eq(AbstractDomainObject.SNAPSHOT, false));
			return builder.queryForFirst();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getFormBuilder", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get all FormBuilders for a Disease
	 */
	public List<FormBuilder> getFormBuilders(Disease disease) {
		try {
			QueryBuilder<FormBuilder, Long> builder = queryBuilder();
			Where<FormBuilder, Long> where = builder.where();
			where.and(
				where.eq(FormBuilder.DISEASE, disease),
				where.eq(AbstractDomainObject.SNAPSHOT, false));
			return builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getFormBuilders", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get ordered FormFields for a FormBuilder
	 */
	public List<FormField> getOrderedFormBuilderFormFields(FormBuilder formBuilder) {
		// Return empty list if FormBuilder hasn't been saved yet
		if (formBuilder.getId() == null) {
			return new ArrayList<>();
		}
		
		try {
			// Query junction table ordered by displayOrder
			QueryBuilder<FormBuilderFormField, Long> queryBuilder = DatabaseHelper.getFormBuilderFormFieldDao().queryBuilder();
			queryBuilder.where().eq("form_id", formBuilder.getId());
			queryBuilder.orderBy("displayOrder", true); // true = ascending

			// Get ordered junction entries
			List<FormBuilderFormField> formBuilderFields = queryBuilder.query();

			// Map to FormField objects and refresh them from database to ensure all fields are loaded
			return formBuilderFields.stream()
				.map(formBuilderFormField -> {
					FormField formField = formBuilderFormField.getFormField();
					if (formField != null && formField.getId() != null) {
						// Refresh the FormField from database to ensure all fields are loaded
						return DatabaseHelper.getFormFieldDao().queryForId(formField.getId());
					}
					return formField;
				})
				.filter(formField -> formField != null)
				.collect(Collectors.toList());
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not retrieve ordered form fields", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Clear existing FormBuilder-FormField relationships
	 */
	public void clearFormBuilderFormFields(FormBuilder formBuilder) {
		// Only clear relationships if the FormBuilder has been saved (has an ID)
		if (formBuilder.getId() == null) {
			return;
		}
		
		try {
			QueryBuilder<FormBuilderFormField, Long> queryBuilder = DatabaseHelper.getFormBuilderFormFieldDao().queryBuilder();
			queryBuilder.where().eq("form_id", formBuilder.getId());
			List<FormBuilderFormField> existingRelations = queryBuilder.query();

			for (FormBuilderFormField relation : existingRelations) {
				DatabaseHelper.getFormBuilderFormFieldDao().delete(relation);
			}
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not clear form builder form fields", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a FormBuilder-FormField relationship
	 */
	public void createFormBuilderFormField(FormBuilderFormField junction) {
		try {
			DatabaseHelper.getFormBuilderFormFieldDao().create(junction);
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not create form builder form field", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create(FormBuilder data) throws SQLException {
		super.create(data);
		if (data.getFormFields() != null) {
			Date now = new Date();
			for (int i = 0; i < data.getFormFields().size(); i++) {
				FormField formField = data.getFormFields().get(i);
				FormBuilderFormField junction = new FormBuilderFormField(data, formField);
				// Initialize required AbstractDomainObject fields
				junction.setUuid(de.symeda.sormas.api.utils.DataHelper.createUuid());
				junction.setCreationDate(now);
				junction.setChangeDateForNew();
				junction.setLocalChangeDate(now);
				junction.setDisplayOrder(i); // Use index as order
				createFormBuilderFormField(junction);
			}
		}
	}

	private List<FormBuilderFormField> loadFormBuilderFormField(Long formBuilderId) {
		try {
			QueryBuilder builder = formBuilderFormFieldDao.queryBuilder();
			Where where = builder.where();
			where.eq("form_id", formBuilderId);
			return (List<FormBuilderFormField>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform loadUserRoles");
			throw new RuntimeException(e);
		}
	}


	@Override
	protected void update(FormBuilder data) throws SQLException {
		super.update(data);

		// Clear existing relationships
		clearFormBuilderFormFields(data);

		// Create new relationships with order
		if (data.getFormFields() != null) {
			Date now = new Date();
			for (int i = 0; i < data.getFormFields().size(); i++) {
				FormField formField = data.getFormFields().get(i);
				FormBuilderFormField junction = new FormBuilderFormField(data, formField);
				// Initialize required AbstractDomainObject fields
				junction.setUuid(de.symeda.sormas.api.utils.DataHelper.createUuid());
				junction.setCreationDate(now);
				junction.setChangeDateForNew();
				junction.setLocalChangeDate(now);
				junction.setDisplayOrder(i);
				createFormBuilderFormField(junction);
			}
		}
	}
}

