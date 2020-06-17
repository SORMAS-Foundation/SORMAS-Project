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

package de.symeda.sormas.app.backend.clinicalcourse;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class ClinicalVisitDtoHelper extends AdoDtoHelper<ClinicalVisit, ClinicalVisitDto> {

	private SymptomsDtoHelper symptomsDtoHelper = new SymptomsDtoHelper();

	@Override
	protected Class<ClinicalVisit> getAdoClass() {
		return ClinicalVisit.class;
	}

	@Override
	protected Class<ClinicalVisitDto> getDtoClass() {
		return ClinicalVisitDto.class;
	}

	@Override
	protected Call<List<ClinicalVisitDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getClinicalVisitFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<ClinicalVisitDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getClinicalVisitFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<ClinicalVisitDto> clinicalVisitDtos) throws NoConnectionException {
		return RetroProvider.getClinicalVisitFacade().pushAll(clinicalVisitDtos);
	}

	@Override
	public void fillInnerFromDto(ClinicalVisit target, ClinicalVisitDto source) {
		target.setClinicalCourse(DatabaseHelper.getClinicalCourseDao().getByReferenceDto(source.getClinicalCourse()));
		target.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(target.getSymptoms(), source.getSymptoms()));
		target.setDisease(source.getDisease());
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(ClinicalVisitDto target, ClinicalVisit source) {
		if (source.getClinicalCourse() != null) {
			ClinicalCourse clinicalCourse = DatabaseHelper.getClinicalCourseDao().queryForId(source.getClinicalCourse().getId());
			target.setClinicalCourse(ClinicalCourseDtoHelper.toReferenceDto(clinicalCourse));
		} else {
			target.setClinicalCourse(null);
		}

		if (source.getSymptoms() != null) {
			Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryForId(source.getSymptoms().getId());
			target.setSymptoms(symptomsDtoHelper.adoToDto(symptoms));
		} else {
			target.setSymptoms(null);
		}

		target.setDisease(source.getDisease());
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

		target.setPseudonymized(source.isPseudonymized());
	}
}
