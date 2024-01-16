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

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;

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

	@Override
	public EventParticipant build() {

		EventParticipant eventParticipant = super.build();
		eventParticipant.setReportingUser(ConfigProvider.getUser());

		return eventParticipant;
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

	public boolean eventParticipantAlreadyExists(Event event, Person person) {

		if (event.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			List<EventParticipant> eventParticipants = queryBuilder().where()
				.eq(EventParticipant.EVENT + "_id", event)
				.and()
				.eq(AbstractDomainObject.SNAPSHOT, false)
				.and()
				.eq(EventParticipant.PERSON + "_id", person)
				.query();
			return eventParticipants.size() > 0;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByEvent on EventParticipant");
			throw new RuntimeException(e);
		}
	}

	public Long countByEvent(Event event) {
		if (event.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}
		try {
			return queryBuilder().where().eq(EventParticipant.EVENT + "_id", event).and().eq(AbstractDomainObject.SNAPSHOT, false).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByEvent on EventParticipant");
			throw new RuntimeException(e);
		}
	}

	public List<EventParticipant> getByCase(Case caze) {

		if (caze.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			return queryBuilder().where()
				.eq(EventParticipant.RESULTING_CASE_UUID, caze.getUuid())
				.and()
				.eq(AbstractDomainObject.SNAPSHOT, false)
				.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByEvent on EventParticipant");
			throw new RuntimeException(e);
		}
	}

	public void deleteEventParticipant(String eventParticipantUuid) throws SQLException {
		deleteEventParticipant(queryUuidWithEmbedded(eventParticipantUuid));
	}

	public void deleteEventParticipant(EventParticipant eventParticipant) throws SQLException {

		// Cancel if not in local database
		if (eventParticipant == null) {
			return;
		}

		// Delete eventParticipant
		deleteCascade(eventParticipant);
	}

	public void deleteEventParticipantAndAllDependingEntities(String eventParticipantUuid) throws SQLException {
		EventParticipant eventParticipant = queryUuidWithEmbedded(eventParticipantUuid);

		deleteEventParticipant(eventParticipant);
	}

	// TODO #704
//    @Override
//    public void markAsRead(EventParticipant eventParticipant) {
//        super.markAsRead(eventParticipant);
//        DatabaseHelper.getPersonDao().markAsRead(eventParticipant.getPerson());
//    }
}
