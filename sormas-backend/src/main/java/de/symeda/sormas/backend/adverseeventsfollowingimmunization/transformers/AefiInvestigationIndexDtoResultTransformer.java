/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassification;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStage;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PatientStatusAtAefiInvestigation;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PlaceOfVaccination;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationActivity;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationSite;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;

public class AefiInvestigationIndexDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {

		Integer age = objects[6] != null ? (int) objects[6] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[7];
		Integer birthdateDD = objects[8] != null ? (int) objects[8] : null;
		Integer birthdateMM = objects[9] != null ? (int) objects[9] : null;
		Integer birthdateYYYY = objects[10] != null ? (int) objects[10] : null;

		return new AefiInvestigationIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(Disease) objects[3],
			(String) objects[4],
			(String) objects[5],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(Sex) objects[11],
			(String) objects[12],
			(String) objects[13],
			(PlaceOfVaccination) objects[14],
			(VaccinationActivity) objects[15],
			(Date) objects[16],
			(Date) objects[17],
			(Date) objects[18],
			(AefiInvestigationStage) objects[19],
			(VaccinationSite) objects[20],
			(Date) objects[21],
			(Date) objects[22],
			(Date) objects[23],
			(PatientStatusAtAefiInvestigation) objects[24],
			(Vaccine) objects[25],
			(String) objects[26],
			(AefiInvestigationStatus) objects[27],
			(AefiClassification) objects[28],
			(DeletionReason) objects[29],
			(String) objects[30],
			(boolean) objects[31]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
