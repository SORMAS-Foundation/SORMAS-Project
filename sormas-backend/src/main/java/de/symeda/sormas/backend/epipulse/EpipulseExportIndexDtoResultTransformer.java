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

package de.symeda.sormas.backend.epipulse;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;

public class EpipulseExportIndexDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = 6930950007148262844L;

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {

		int index = -1;

		//@formatter:off
        return new EpipulseExportIndexDto(
                (String) objects[++index],
                (EpipulseSubjectCode) objects[++index],
                (Date) objects[++index],
                (Date) objects[++index],
                (EpipulseExportStatus) objects[++index],
                (Date) objects[++index],
                (Long) objects[++index],
                (String) objects[++index],
                (Long) objects[++index],
                (Date) objects[++index],
                (String) objects[++index],
                (String) objects[++index],
                (String) objects[++index]
        );
        //@formatter:on
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
