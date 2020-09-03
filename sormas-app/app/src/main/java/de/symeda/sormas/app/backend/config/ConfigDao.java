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

package de.symeda.sormas.app.backend.config;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import android.util.Log;

/**
 * Some methods are copied from {@link com.j256.ormlite.dao.RuntimeExceptionDao}.
 *
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class ConfigDao {

	private Dao<Config, String> dao;

	public ConfigDao(Dao<Config, String> innerDao) throws SQLException {
		this.dao = innerDao;
	}

	/**
	 * @see Dao#queryForId(Object)
	 */
	public Config queryForId(String id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "queryForId threw exception on: " + id, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#delete(Object)
	 */
	public int delete(Config data) {
		try {
			return dao.delete(data);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "delete threw exception on: " + data, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#createOrUpdate(Object)
	 */
	public Dao.CreateOrUpdateStatus createOrUpdate(Config data) {
		try {
			return dao.createOrUpdate(data);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "createOrUpdate threw exception on: " + data, e);
			throw new RuntimeException(e);
		}
	}
}
