/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.customizableenum;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CustomizableEnumValueDtoHelper extends AdoDtoHelper<CustomizableEnumValue, CustomizableEnumValueDto> {

	@Override
	protected Class<CustomizableEnumValue> getAdoClass() {
		return CustomizableEnumValue.class;
	}

	@Override
	protected Class<CustomizableEnumValueDto> getDtoClass() {
		return CustomizableEnumValueDto.class;
	}

	@Override
	protected Call<List<CustomizableEnumValueDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getCustomizableEnumValueFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<CustomizableEnumValueDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getCustomizableEnumValueFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<CustomizableEnumValueDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	public void fillInnerFromDto(CustomizableEnumValue target, CustomizableEnumValueDto source) {
		target.setDataType(source.getDataType());
		target.setValue(source.getValue());
		target.setCaption(source.getCaption());
		target.setTranslations(source.getTranslations());
		target.setDiseases(source.getDiseases());
		target.setDescription(source.getDescription());
		target.setDescriptionTranslations(source.getDescriptionTranslations());
		target.setProperties(source.getProperties());
		target.setDefaultValue(source.isDefaultValue());
		target.setActive(source.isActive());
	}

	@Override
	public void fillInnerFromAdo(CustomizableEnumValueDto target, CustomizableEnumValue source) {
		// Not supported
	}

	@Override
	protected void executeHandlePulledListAddition(int listSize) {
		if (listSize > 0) {
			// Clear the customizable enum value cache if values have changed
			DatabaseHelper.getCustomizableEnumValueDao().clearCache();
		}
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}
}
