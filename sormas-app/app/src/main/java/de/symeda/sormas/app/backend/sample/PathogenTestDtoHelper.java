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

package de.symeda.sormas.app.backend.sample;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PathogenTestDtoHelper extends AdoDtoHelper<PathogenTest, PathogenTestDto> {

	@Override
	protected Class<PathogenTest> getAdoClass() {
		return PathogenTest.class;
	}

	@Override
	protected Class<PathogenTestDto> getDtoClass() {
		return PathogenTestDto.class;
	}

	@Override
	protected Call<List<PathogenTestDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getSampleTestFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<PathogenTestDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getSampleTestFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PathogenTestDto> pathogenTestDtos) throws NoConnectionException {
		return RetroProvider.getSampleTestFacade().pushAll(pathogenTestDtos);
	}

	@Override
	protected void fillInnerFromDto(PathogenTest target, PathogenTestDto source) {

		target.setSample(DatabaseHelper.getSampleDao().getByReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestResultVerified(source.getTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setFourFoldIncreaseAntibodyTiter(source.isFourFoldIncreaseAntibodyTiter());
		target.setSerotype(source.getSerotype());
		target.setCqValue(source.getCqValue());
		target.setLab(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setLabUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getLabUser()));

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	protected void fillInnerFromAdo(PathogenTestDto target, PathogenTest source) {
		if (source.getSample() != null) {
			Sample sample = DatabaseHelper.getSampleDao().queryForId(source.getSample().getId());
			target.setSample(SampleDtoHelper.toReferenceDto(sample));
		} else {
			target.setSample(null);
		}
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());

		if (source.getLab() != null) {
			Facility lab = DatabaseHelper.getFacilityDao().queryForId(source.getLab().getId());
			target.setLab(FacilityDtoHelper.toReferenceDto(lab));
		} else {
			target.setLab(null);
		}
		target.setLabDetails(source.getLabDetails());

		target.setTestResultVerified(source.getTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setFourFoldIncreaseAntibodyTiter(source.isFourFoldIncreaseAntibodyTiter());
		target.setSerotype(source.getSerotype());
		target.setCqValue(source.getCqValue());

		if (source.getLabUser() != null) {
			User user = DatabaseHelper.getUserDao().queryForId(source.getLabUser().getId());
			target.setLabUser(UserDtoHelper.toReferenceDto(user));
		} else {
			target.setLabUser(null);
		}

		target.setPseudonymized(source.isPseudonymized());
	}
}
