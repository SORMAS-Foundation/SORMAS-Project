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

package de.symeda.sormas.app.backend.hospitalization;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class HospitalizationDao extends AbstractAdoDao<Hospitalization> {

	public HospitalizationDao(Dao<Hospitalization, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<Hospitalization> getAdoClass() {
		return Hospitalization.class;
	}

	@Override
	public String getTableName() {
		return Hospitalization.TABLE_NAME;
	}

	@Override
	public Hospitalization queryUuid(String uuid) {
		Hospitalization data = super.queryUuid(uuid);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	@Override
	public Hospitalization querySnapshotByUuid(String uuid) {
		Hospitalization data = super.querySnapshotByUuid(uuid);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	@Override
	public Hospitalization queryForId(Long id) {
		Hospitalization data = super.queryForId(id);
		if (data != null) {
			initLazyData(data);
		}
		return data;
	}

	private Hospitalization initLazyData(Hospitalization hospitalization) {
		hospitalization.setPreviousHospitalizations(DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization));
		return hospitalization;
	}

	@Override
	public Hospitalization saveAndSnapshot(Hospitalization ado) throws DaoException {

		Hospitalization snapshot = super.saveAndSnapshot(ado);

		DatabaseHelper.getPreviousHospitalizationDao()
			.saveCollectionWithSnapshot(
				DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(ado),
				ado.getPreviousHospitalizations(),
				ado);

		return snapshot;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date prevDate = DatabaseHelper.getPreviousHospitalizationDao().getLatestChangeDate();
		if (prevDate != null && prevDate.after(date)) {
			date = prevDate;
		}

		return date;
	}
}
