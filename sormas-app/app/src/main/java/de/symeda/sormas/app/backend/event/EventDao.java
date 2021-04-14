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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.util.LocationService;

public class EventDao extends AbstractAdoDao<Event> {

	public EventDao(Dao<Event, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<Event> getAdoClass() {
		return Event.class;
	}

	@Override
	public String getTableName() {
		return Event.TABLE_NAME;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Event.EVENT_LOCATION);
		if (locationDate != null && locationDate.after(date)) {
			date = locationDate;
		}

		return date;
	}

	@Override
	public Event build() {

		Event event = super.build();

		event.setReportDateTime(new Date());
		event.setReportingUser(ConfigProvider.getUser());
		event.getEventLocation().setRegion(ConfigProvider.getUser().getRegion());
		event.getEventLocation().setDistrict(ConfigProvider.getUser().getDistrict());
		event.setEventStatus(EventStatus.SIGNAL);
		event.setEventInvestigationStatus(EventInvestigationStatus.PENDING);

		return event;
	}

	@Override
	public Event saveAndSnapshot(final Event event) throws DaoException {
		// If a new event is created, use the last available location to update its report latitude and longitude
		if (event.getId() == null) {
			android.location.Location location = LocationService.instance().getLocation();
			if (location != null) {
				event.setReportLat(location.getLatitude());
				event.setReportLon(location.getLongitude());
				event.setReportLatLonAccuracy(location.getAccuracy());
			}
		}

		return super.saveAndSnapshot(event);
	}

	public void deleteEventAndAllDependingEntities(String eventUuid) throws SQLException {
		Event event = queryUuidWithEmbedded(eventUuid);

		// Cancel if not in local database
		if (event == null) {
			return;
		}

		// Delete event tasks
		List<Task> tasks = DatabaseHelper.getTaskDao().queryByEvent(event);
		for (Task task : tasks) {
			DatabaseHelper.getTaskDao().deleteCascade(task);
		}

		// Delete event participants
		List<EventParticipant> eventParticipants = DatabaseHelper.getEventParticipantDao().getByEvent(event);
		for (EventParticipant eventParticipant : eventParticipants) {
			DatabaseHelper.getEventParticipantDao().deleteCascade(eventParticipant);
		}

		// Delete event
		deleteCascade(event);
	}

	public long countByCriteria(EventCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Event");
			throw new RuntimeException(e);
		}
	}

	public List<Event> queryByCriteria(EventCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Event.LOCAL_CHANGE_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Event");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Event, Long> buildQueryBuilder(EventCriteria criteria) throws SQLException {
		QueryBuilder<Event, Long> queryBuilder = queryBuilder();
		List<Where<Event, Long>> whereStatements = new ArrayList<>();
		Where<Event, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		if (criteria.getDisease() != null) {

			queryBuilder.distinct();
			QueryBuilder<EventParticipant, Long> eventParticipantQueryBuilder = DatabaseHelper.getEventParticipantDao().queryBuilder();

			QueryBuilder<Case, Long> caseLongQueryBuilder = DatabaseHelper.getCaseDao().queryBuilder();
			eventParticipantQueryBuilder = eventParticipantQueryBuilder.join(
				EventParticipant.RESULTING_CASE_UUID,
				Case.UUID,
				caseLongQueryBuilder,
				QueryBuilder.JoinType.LEFT,
				QueryBuilder.JoinWhereOperation.AND);
			queryBuilder.leftJoin(eventParticipantQueryBuilder);

			whereStatements.add(
				where.or(
					where.and(
						where.eq(Event.DISEASE, criteria.getDisease()),
						where.raw(Case.TABLE_NAME + "." + Case.UUID + "!= '" + criteria.getCaze().getUuid() + "'")),
					where.and(where.raw(Case.TABLE_NAME + "." + Case.UUID + " IS NULL"), where.eq(Event.DISEASE, criteria.getDisease()))));

		} else {
			if (criteria.getCaze() != null) {
				QueryBuilder<EventParticipant, Long> eventParticipantQueryBuilder = DatabaseHelper.getEventParticipantDao().queryBuilder();

				QueryBuilder<Case, Long> caseLongQueryBuilder = DatabaseHelper.getCaseDao().queryBuilder();
				eventParticipantQueryBuilder =
					eventParticipantQueryBuilder.join(EventParticipant.RESULTING_CASE_UUID, Case.UUID, caseLongQueryBuilder);
				queryBuilder.leftJoin(eventParticipantQueryBuilder);

				whereStatements.add(where.raw(Case.TABLE_NAME + "." + Case.UUID + "= '" + criteria.getCaze().getUuid() + "'"));

			} else {

				if (criteria.getEventStatus() != null) {
					whereStatements.add(where.eq(Event.EVENT_STATUS, criteria.getEventStatus()));
				}
			}
		}

		if (!whereStatements.isEmpty()) {
			Where<Event, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}
		return queryBuilder;
	}
}
