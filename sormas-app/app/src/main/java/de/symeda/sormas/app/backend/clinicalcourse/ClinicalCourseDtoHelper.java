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
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class ClinicalCourseDtoHelper extends AdoDtoHelper<ClinicalCourse, ClinicalCourseDto> {

	private HealthConditionsDtoHelper healthConditionsDtoHelper = new HealthConditionsDtoHelper();

	@Override
	protected Class<ClinicalCourse> getAdoClass() {
		return ClinicalCourse.class;
	}

	@Override
	protected Class<ClinicalCourseDto> getDtoClass() {
		return ClinicalCourseDto.class;
	}

	@Override
	protected Call<List<ClinicalCourseDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<ClinicalCourseDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<ClinicalCourseDto> clinicalCourseDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(ClinicalCourse target, ClinicalCourseDto source) {
		target.setHealthConditions(healthConditionsDtoHelper.fillOrCreateFromDto(target.getHealthConditions(), source.getHealthConditions()));
	}

	@Override
	public void fillInnerFromAdo(ClinicalCourseDto target, ClinicalCourse source) {
		if (source.getHealthConditions() != null) {
			HealthConditions healthConditions = DatabaseHelper.getHealthConditionsDao().queryForId(source.getHealthConditions().getId());
			target.setHealthConditions(healthConditionsDtoHelper.adoToDto(healthConditions));
		} else {
			target.setHealthConditions(null);
		}
	}

	public static ClinicalCourseReferenceDto toReferenceDto(ClinicalCourse ado) {
		if (ado == null) {
			return null;
		}
		return new ClinicalCourseReferenceDto(ado.getUuid());
	}
}
