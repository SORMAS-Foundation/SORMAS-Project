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

package de.symeda.sormas.backend.event;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventIdentificationSource;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventManagementStatus;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.event.SpecificRisk;

public class EventIndexDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -4148979506301338752L;

	@Override
	public EventIndexDto transformTuple(Object[] tuple, String[] aliases) {
		int index = -1;

		//@formatter:off
        return new EventIndexDto(
				(Long) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
				(EventStatus) tuple[++index], (RiskLevel) tuple[++index], (SpecificRisk) tuple[++index], (EventInvestigationStatus) tuple[++index],
				(EventManagementStatus) tuple[++index], (Disease) tuple[++index], (DiseaseVariant) tuple[++index], (String) tuple[++index],
				(Date) tuple[++index], (Date) tuple[++index], (Date) tuple[++index], (String) tuple[++index],
				(String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], 
				(String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], 
				(EventSourceType) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
				(Date) tuple[++index], (String) tuple[++index],(String) tuple[++index], (String) tuple[++index],
				(String) tuple[++index], (String) tuple[++index], (String) tuple[++index], 
				(Boolean) tuple[++index], (EventIdentificationSource) tuple[++index],
				(DeletionReason) tuple[++index], (String) tuple[++index]
				);
        //@formatter:on
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
