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

import com.j256.ormlite.stmt.QueryBuilder;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.formfield.FormField;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class FormBuilderDtoHelper extends AdoDtoHelper<FormBuilder, FormBuilderDto> {

	@Override
	protected Class<FormBuilder> getAdoClass() {
		return FormBuilder.class;
	}

	@Override
	protected Class<FormBuilderDto> getDtoClass() {
		return FormBuilderDto.class;
	}

	@Override
	protected Call<List<FormBuilderDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getFormBuilderFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<FormBuilderDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getFormBuilderFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<FormBuilderDto> dtos) throws NoConnectionException {
		return RetroProvider.getFormBuilderFacade().pushAll(dtos);
	}

	@Override
	protected void fillInnerFromDto(FormBuilder target, FormBuilderDto source) {
		target.setFormType(source.getFormType());
		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		
		// Don't populate formFields here - will be handled in handlePulledDto after merge
		// This avoids merge issues where FormField entities with IDs would cause "Merged source is not allowed to have an id" error
		target.setFormFields(new ArrayList<>());
	}

	@Override
	protected FormBuilder handlePulledDto(AbstractAdoDao<FormBuilder> dao, FormBuilderDto dto) throws SQLException {
		FormBuilder existing = dao.queryUuid(dto.getUuid());
		FormBuilder existingOrNew = fillOrCreateFromDto(existing, dto);
		
		// Save the FormBuilder first to get an ID
		dao.updateOrCreate(existingOrNew);
		
		// Now handle formFields relationships after the FormBuilder has an ID
		if (dto.getFormFields() != null && dto.getFormFields().size() > 0) {
			// Clear existing relationships
			DatabaseHelper.getFormBuilderDao().clearFormBuilderFormFields(existingOrNew);
			
			// Sort by displayOrder to ensure correct order
			List<FormFieldReferenceDto> sortedFields = new ArrayList<>(dto.getFormFields());
			sortedFields.sort((a, b) -> {
				Integer orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : Integer.MAX_VALUE;
				Integer orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : Integer.MAX_VALUE;
				return orderA.compareTo(orderB);
			});
			
			// Create new relationships with displayOrder
			Date now = new Date();
			for (int i = 0; i < sortedFields.size(); i++) {
				FormFieldReferenceDto formFieldRef = sortedFields.get(i);
				FormField formField = DatabaseHelper.getFormFieldDao().getByReferenceDto(formFieldRef);
				
				if (formField != null) {
					FormBuilderFormField junction = new FormBuilderFormField(existingOrNew, formField);
					// Initialize required AbstractDomainObject fields
					junction.setUuid(de.symeda.sormas.api.utils.DataHelper.createUuid());
					junction.setCreationDate(now);
					junction.setChangeDateForNew();
					junction.setLocalChangeDate(now);
					
					Integer displayOrder = formFieldRef.getDisplayOrder();
					if (displayOrder == null) {
						displayOrder = i; // Fallback to index
					}
					junction.setDisplayOrder(displayOrder);
					DatabaseHelper.getFormBuilderDao().createFormBuilderFormField(junction);
				}
			}
		} else {
			// Clear relationships if no formFields in DTO
			DatabaseHelper.getFormBuilderDao().clearFormBuilderFormFields(existingOrNew);
		}
		
		return existingOrNew;
	}

	@Override
	protected void fillInnerFromAdo(FormBuilderDto target, FormBuilder source) {
		target.setFormType(source.getFormType());
		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		
		// Populate formFields with displayOrder when pushing to server
		try {
			QueryBuilder<FormBuilderFormField, Long> queryBuilder = DatabaseHelper.getFormBuilderFormFieldDao().queryBuilder();
			queryBuilder.where().eq("form_id", source.getId());
			queryBuilder.orderBy("displayOrder", true); // true = ascending
			List<FormBuilderFormField> formBuilderFields = queryBuilder.query();
			
			if (formBuilderFields != null && !formBuilderFields.isEmpty()) {
				List<FormFieldReferenceDto> formFieldRefs = new ArrayList<>();
				for (FormBuilderFormField junction : formBuilderFields) {
					if (junction.getFormField() != null) {
						FormFieldReferenceDto refDto = new FormFieldReferenceDto(junction.getFormField().getUuid());
						refDto.setDisplayOrder(junction.getDisplayOrder());
						formFieldRefs.add(refDto);
					}
				}
				target.setFormFields(formFieldRefs);
			}
		} catch (SQLException e) {
			// Log error but don't fail - formFields will be empty
			android.util.Log.e(FormBuilderDtoHelper.class.getSimpleName(), "Could not populate formFields", e);
		}
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 500; // Estimate ~500 bytes per FormBuilder (includes relationships)
	}
}



