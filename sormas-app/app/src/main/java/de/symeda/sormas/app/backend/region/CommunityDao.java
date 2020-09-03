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

package de.symeda.sormas.app.backend.region;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;

public class CommunityDao extends AbstractInfrastructureAdoDao<Community> {

	public CommunityDao(Dao<Community, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<Community> getAdoClass() {
		return Community.class;
	}

	@Override
	public String getTableName() {
		return Community.TABLE_NAME;
	}

	public List<Community> getByDistrict(District district) {
		return queryActiveForEq(Community.DISTRICT + "_id", district, Community.NAME, true);
	}

	@Override
	public Community saveAndSnapshot(Community source) throws DaoException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Community mergeOrCreate(Community source) throws DaoException {
		throw new UnsupportedOperationException();
	}
}
