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

package de.symeda.sormas.app.backend.epidata;

import java.util.Date;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class EpiDataDao extends AbstractAdoDao<EpiData> {

	public EpiDataDao(Dao<EpiData, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<EpiData> getAdoClass() {
		return EpiData.class;
	}

	@Override
	public String getTableName() {
		return EpiData.TABLE_NAME;
	}

	@Override
	public EpiData queryUuid(String uuid) {
		EpiData data = super.queryUuid(uuid);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	@Override
	public EpiData querySnapshotByUuid(String uuid) {
		EpiData data = super.querySnapshotByUuid(uuid);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	@Override
	public EpiData queryForId(Long id) {
		EpiData data = super.queryForId(id);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	private EpiData initLazyData(EpiData epiData) {
		epiData.setExposures(DatabaseHelper.getExposureDao().getByEpiData(epiData));
		epiData.setActivitiesAsCase(DatabaseHelper.getActivityAsCaseDao().getByEpiData(epiData));

		return epiData;
	}

	@Override
	public EpiData saveAndSnapshot(EpiData ado) throws DaoException {

		EpiData snapshot = super.saveAndSnapshot(ado);

		DatabaseHelper.getExposureDao().saveCollectionWithSnapshot(DatabaseHelper.getExposureDao().getByEpiData(ado), ado.getExposures(), ado);
		DatabaseHelper.getActivityAsCaseDao()
			.saveCollectionWithSnapshot(DatabaseHelper.getActivityAsCaseDao().getByEpiData(ado), ado.getActivitiesAsCase(), ado);

		return snapshot;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date exposureDate = DatabaseHelper.getExposureDao().getLatestChangeDate();
		Date activityAsCaseDate = DatabaseHelper.getActivityAsCaseDao().getLatestChangeDate();

		if (exposureDate != null && (exposureDate.after(date) || activityAsCaseDate.after(date))) {
			date = exposureDate;
		}

		return date;
	}
}
