/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;

public class AreaDao extends AbstractInfrastructureAdoDao<Area> {
    public AreaDao(Dao<Area, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<Area> getAdoClass() {
        return Area.class;
    }

    @Override
    public String getTableName() {
        return Area.TABLE_NAME;
    }
}
