/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventState;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiHelper;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeizureType;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class AefiListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {

		//@formatter:off
		String adverseEvents = AefiHelper
			.buildAdverseEventsString((AdverseEventState) objects[5], (boolean) objects[6], (boolean) objects[7],
					(AdverseEventState) objects[8], (SeizureType) objects[9], (AdverseEventState) objects[10],
					(AdverseEventState) objects[11], (AdverseEventState) objects[12], (AdverseEventState) objects[13],
					(AdverseEventState) objects[14], (AdverseEventState) objects[15], (AdverseEventState) objects[16],
					(String) objects[17]);
		//@formatter:on

		return new AefiListEntryDto(
			(String) objects[0],
			(YesNoUnknown) objects[1],
			(Vaccine) objects[2],
			(String) objects[3],
			(Date) objects[4],
			adverseEvents);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
