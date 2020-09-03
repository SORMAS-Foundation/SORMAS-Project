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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDao extends AbstractAdoDao<EpiDataGathering> {

	public EpiDataGatheringDao(Dao<EpiDataGathering, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<EpiDataGathering> getAdoClass() {
		return EpiDataGathering.class;
	}

	@Override
	public void delete(EpiDataGathering data) throws SQLException {
		DatabaseHelper.getLocationDao().delete(data.getGatheringAddress());
		super.delete(data);
	}

	public List<EpiDataGathering> getByEpiData(EpiData epiData) {
		if (epiData.isSnapshot()) {
			return querySnapshotsForEq(EpiDataGathering.EPI_DATA + "_id", epiData, EpiDataGathering.CHANGE_DATE, false);
		}
		return queryForEq(EpiDataGathering.EPI_DATA + "_id", epiData, EpiDataGathering.CHANGE_DATE, false);
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, EpiDataGathering.GATHERING_ADDRESS);
		if (locationDate != null && locationDate.after(date)) {
			date = locationDate;
		}

		return date;
	}

	@Override
	public String getTableName() {
		return EpiDataGathering.TABLE_NAME;
	}
}
