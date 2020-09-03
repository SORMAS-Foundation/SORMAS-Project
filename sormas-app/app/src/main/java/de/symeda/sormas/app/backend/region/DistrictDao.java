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

public class DistrictDao extends AbstractInfrastructureAdoDao<District> {

	public DistrictDao(Dao<District, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<District> getAdoClass() {
		return District.class;
	}

	@Override
	public String getTableName() {
		return District.TABLE_NAME;
	}

	public List<District> getByRegion(Region region) {
		return queryActiveForEq(District.REGION + "_id", region, District.NAME, true);
	}

	@Override
	public District saveAndSnapshot(District source) throws DaoException {
		throw new UnsupportedOperationException();
	}
}
