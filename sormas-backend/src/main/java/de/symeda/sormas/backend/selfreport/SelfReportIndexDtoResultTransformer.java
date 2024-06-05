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

package de.symeda.sormas.backend.selfreport;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportInvestigationStatus;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SelfReportIndexDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -1958369992543717701L;

	@Override
	public SelfReportIndexDto transformTuple(Object[] tuple, String[] aliases) {
		int index = -1;

		//@formatter:off
		return new SelfReportIndexDto(
			(String) tuple[++index], (SelfReportType) tuple[++index], (Date) tuple[++index],
			(Disease) tuple[++index], (String) tuple[++index], (String) tuple[++index],
			new BirthDateDto((Integer)tuple[++index], (Integer) tuple[++index], (Integer) tuple[++index]), (Sex) tuple[++index],
			(String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
			(String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
			new UserReferenceDto((String) tuple[++index], (String) tuple[++index], (String) tuple[++index]),
			(SelfReportInvestigationStatus) tuple[++index], (SelfReportProcessingStatus) tuple[++index],
			(DeletionReason) tuple[++index], (String) tuple[++index]);
		//@formatter:on
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
