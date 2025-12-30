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

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class FormFieldDtoHelper extends AdoDtoHelper<FormField, FormFieldsDto> {

	@Override
	protected Class<FormField> getAdoClass() {
		return FormField.class;
	}

	@Override
	protected Class<FormFieldsDto> getDtoClass() {
		return FormFieldsDto.class;
	}

	@Override
	protected Call<List<FormFieldsDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getFormFieldFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<FormFieldsDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getFormFieldFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<FormFieldsDto> dtos) throws NoConnectionException {
		return RetroProvider.getFormFieldFacade().pushAll(dtos);
	}

	@Override
	public void fillInnerFromDto(FormField target, FormFieldsDto source) {
		target.setFormType(source.getFormType());
		target.setFieldName(source.getFieldName());
		target.setDescription(source.getDescription());
		target.setActive(source.getActive());

	}


	@Override
	public void fillInnerFromAdo(FormFieldsDto target, FormField source) {
		target.setFormType(source.getFormType());
		target.setFieldName(source.getFieldName());
		target.setDescription(source.getDescription());
		target.setActive(source.getActive());
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

}



