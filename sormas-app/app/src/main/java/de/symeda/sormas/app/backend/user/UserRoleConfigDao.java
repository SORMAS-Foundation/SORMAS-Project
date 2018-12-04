/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;

public class UserRoleConfigDao extends AbstractAdoDao<UserRoleConfig> {

    public UserRoleConfigDao(Dao<UserRoleConfig, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<UserRoleConfig> getAdoClass() {
        return UserRoleConfig.class;
    }

    @Override
    public String getTableName() {
        return UserRoleConfig.TABLE_NAME;
    }

    @Override
    public UserRoleConfig saveAndSnapshot(UserRoleConfig source) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserRoleConfig mergeOrCreate(UserRoleConfig source) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
