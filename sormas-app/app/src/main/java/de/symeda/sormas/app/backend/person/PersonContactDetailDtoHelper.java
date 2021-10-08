/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.person;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class PersonContactDetailDtoHelper extends AdoDtoHelper<PersonContactDetail, PersonContactDetailDto> {

	@Override
	protected Class<PersonContactDetail> getAdoClass() {
		return PersonContactDetail.class;
	}

	@Override
	protected Class<PersonContactDetailDto> getDtoClass() {
		return PersonContactDetailDto.class;
	}

	@Override
	protected Call<List<PersonContactDetailDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PersonContactDetailDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PersonContactDetailDto> personContactDetailDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(PersonContactDetail target, PersonContactDetailDto source) {
		target.setPrimaryContact(source.isPrimaryContact());
		target.setPersonContactDetailType(source.getPersonContactDetailType());
		target.setPhoneNumberType(source.getPhoneNumberType());
		target.setDetails(source.getDetails());
		target.setContactInformation(source.getContactInformation());
		target.setAdditionalInformation(source.getAdditionalInformation());
		target.setThirdParty(source.isThirdParty());
		target.setThirdPartyRole(source.getThirdPartyRole());
		target.setThirdPartyName(source.getThirdPartyName());
	}

	@Override
	protected void fillInnerFromAdo(PersonContactDetailDto target, PersonContactDetail source) {
		target.setPrimaryContact(source.isPrimaryContact());
		target.setPersonContactDetailType(source.getPersonContactDetailType());
		target.setPhoneNumberType(source.getPhoneNumberType());
		target.setDetails(source.getDetails());
		target.setContactInformation(source.getContactInformation());
		target.setAdditionalInformation(source.getAdditionalInformation());
		target.setThirdParty(source.isThirdParty());
		target.setThirdPartyRole(source.getThirdPartyRole());
		target.setThirdPartyName(source.getThirdPartyName());
	}
}
