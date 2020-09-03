/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.therapy;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class TherapyDtoHelper extends AdoDtoHelper<Therapy, TherapyDto> {

	@Override
	protected Class<Therapy> getAdoClass() {
		return Therapy.class;
	}

	@Override
	protected Class<TherapyDto> getDtoClass() {
		return TherapyDto.class;
	}

	@Override
	protected Call<List<TherapyDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<TherapyDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<TherapyDto> therapyDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(Therapy target, TherapyDto source) {
		// No fields in Therapy
	}

	@Override
	public void fillInnerFromAdo(TherapyDto target, Therapy source) {
		// No fields in Therapy
	}

	public static TherapyReferenceDto toReferenceDto(Therapy ado) {
		if (ado == null) {
			return null;
		}
		return new TherapyReferenceDto(ado.getUuid());
	}
}
