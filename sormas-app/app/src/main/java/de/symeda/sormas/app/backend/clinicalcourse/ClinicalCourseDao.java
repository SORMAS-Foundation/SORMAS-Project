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

import java.util.Date;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class ClinicalCourseDao extends AbstractAdoDao<ClinicalCourse> {

	public ClinicalCourseDao(Dao<ClinicalCourse, Long> innerDao) {
		super(innerDao);
	}

	@Override
	public ClinicalCourse build() {
		ClinicalCourse clinicalCourse = super.build();
		clinicalCourse.setHealthConditions(DatabaseHelper.getHealthConditionsDao().build());
		return clinicalCourse;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date healthConditionsDate = getLatestChangeDateJoin(HealthConditions.TABLE_NAME, ClinicalCourse.HEALTH_CONDITIONS);
		if (healthConditionsDate != null && healthConditionsDate.after(date)) {
			date = healthConditionsDate;
		}

		return date;
	}

	@Override
	protected Class<ClinicalCourse> getAdoClass() {
		return ClinicalCourse.class;
	}

	@Override
	public String getTableName() {
		return ClinicalCourse.TABLE_NAME;
	}
}
