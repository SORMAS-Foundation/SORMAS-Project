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

package de.symeda.sormas.app.backend.event;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class EventParticipantDao extends AbstractAdoDao<EventParticipant> {

	public EventParticipantDao(Dao<EventParticipant, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<EventParticipant> getAdoClass() {
		return EventParticipant.class;
	}

	@Override
	public String getTableName() {
		return EventParticipant.TABLE_NAME;
	}

	public List<EventParticipant> getByEvent(Event event) {

		if (event.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			return queryBuilder().where().eq(EventParticipant.EVENT + "_id", event).and().eq(AbstractDomainObject.SNAPSHOT, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByEvent on EventParticipant");
			throw new RuntimeException(e);
		}
	}

	// TODO #704
//    @Override
//    public void markAsRead(EventParticipant eventParticipant) {
//        super.markAsRead(eventParticipant);
//        DatabaseHelper.getPersonDao().markAsRead(eventParticipant.getPerson());
//    }
}
