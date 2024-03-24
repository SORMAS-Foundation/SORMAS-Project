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
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventState;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiHelper;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiOutcome;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeizureType;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class AefiIndexDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {

		Integer age = objects[6] != null ? (int) objects[6] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[7];
		Integer birthdateDD = objects[8] != null ? (int) objects[8] : null;
		Integer birthdateMM = objects[9] != null ? (int) objects[9] : null;
		Integer birthdateYYYY = objects[10] != null ? (int) objects[10] : null;

		//@formatter:off
		String adverseEvents = AefiHelper
				.buildAdverseEventsString((AdverseEventState) objects[21], (boolean) objects[22], (boolean) objects[23],
						(AdverseEventState) objects[24], (SeizureType) objects[25], (AdverseEventState) objects[26],
						(AdverseEventState) objects[27], (AdverseEventState) objects[28], (AdverseEventState) objects[29],
						(AdverseEventState) objects[30], (AdverseEventState) objects[31], (AdverseEventState) objects[32],
						(String) objects[33]);
		//@formatter:on

		return new AefiIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[3],
			(String) objects[4],
			(String) objects[5],
			(Disease) objects[2],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(Sex) objects[11],
			(String) objects[12],
			(String) objects[13],
			(YesNoUnknown) objects[14],
			(Vaccine) objects[15],
			(String) objects[16],
			(AefiOutcome) objects[17],
			(Date) objects[18],
			(Date) objects[19],
			(Date) objects[20],
			adverseEvents,
			(DeletionReason) objects[34],
			(String) objects[35],
			(boolean) objects[36]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
