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

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassification;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStage;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PatientStatusAtAefiInvestigation;
import de.symeda.sormas.api.caze.Vaccine;

public class AefiInvestigationListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {

		return new AefiInvestigationListEntryDto(
			(String) objects[0],
			(String) objects[1],
			(Date) objects[2],
			(AefiInvestigationStage) objects[3],
			(PatientStatusAtAefiInvestigation) objects[4],
			(Vaccine) objects[5],
			(String) objects[6],
			(String) objects[7],
			(Date) objects[8],
			(AefiInvestigationStatus) objects[9],
			(AefiClassification) objects[10]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
