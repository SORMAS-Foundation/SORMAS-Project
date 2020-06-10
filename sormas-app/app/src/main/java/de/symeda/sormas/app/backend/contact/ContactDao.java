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

package de.symeda.sormas.app.backend.contact;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.LocationService;

public class ContactDao extends AbstractAdoDao<Contact> {

	public ContactDao(Dao<Contact, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Contact> getAdoClass() {
		return Contact.class;
	}

	@Override
	public String getTableName() {
		return Contact.TABLE_NAME;
	}

	public List<Contact> getByCase(Case caze) {
		if (caze.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			QueryBuilder qb = queryBuilder();
			qb.where().eq(Contact.CASE_UUID, caze.getUuid()).and().eq(AbstractDomainObject.SNAPSHOT, false);
			qb.orderBy(Contact.LAST_CONTACT_DATE, false);
			return qb.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByCase on Contact");
			throw new RuntimeException(e);
		}
	}

	public int getCountByPersonAndDisease(@NonNull Person person, Disease disease) {
		if (person.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			QueryBuilder qb = queryBuilder();
			Where where = qb.where();
			where.and(where.eq(Contact.PERSON, person), where.eq(AbstractDomainObject.SNAPSHOT, false));
			if (disease != null) {
				where.and(where, where.eq(Contact.DISEASE, disease));
			}
			qb.orderBy(Contact.LAST_CONTACT_DATE, false);
			return (int) qb.countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getCountByPersonAndDisease on Contact");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Contact build() {
		Contact contact = super.build();
		User user = ConfigProvider.getUser();

		contact.setReportDateTime(new Date());
		contact.setReportingUser(user);

		if (user.getRegion() != null) {
			contact.setRegion(user.getRegion());
		}
		if (user.getDistrict() != null) {
			contact.setDistrict(user.getDistrict());
		}

		return contact;
	}

	// TODO #704
//    @Override
//    public void markAsRead(Contact contact) {
//        super.markAsRead(contact);
//        DatabaseHelper.getPersonDao().markAsRead(contact.getPerson());
//    }

	@Override
	public Contact saveAndSnapshot(final Contact contact) throws DaoException {
		// If a new contact is created, use the last available location to update its report latitude and longitude
		if (contact.getId() == null) {
			Location location = LocationService.instance().getLocation();
			if (location != null) {
				contact.setReportLat(location.getLatitude());
				contact.setReportLon(location.getLongitude());
				contact.setReportLatLonAccuracy(location.getAccuracy());
			}
		}

		updateFollowUpStatus(contact);

		return super.saveAndSnapshot(contact);
	}

	/**
	 * This is only the status. On the server we also update the follow up unitl field
	 * 
	 * @param contact
	 */
	private void updateFollowUpStatus(Contact contact) {
		Disease disease = contact.getDisease();
		boolean changeStatus = contact.getFollowUpStatus() != FollowUpStatus.CANCELED && contact.getFollowUpStatus() != FollowUpStatus.LOST;

		ContactProximity contactProximity = contact.getContactProximity();
		if (!DiseaseConfigurationCache.getInstance().hasFollowUp(disease) || (contactProximity != null && !contactProximity.hasFollowUp())) {
			contact.setFollowUpUntil(null);
			if (changeStatus) {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
		} else if (changeStatus) {
			contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
		}
	}

	public long countByCriteria(ContactCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Contact");
			throw new RuntimeException(e);
		}
	}

	public List<Contact> queryByCriteria(ContactCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Contact.REPORT_DATE_TIME, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Contact");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Contact, Long> buildQueryBuilder(ContactCriteria criteria) throws SQLException {
		QueryBuilder<Contact, Long> queryBuilder = queryBuilder();
		QueryBuilder<Person, Long> personQueryBuilder = DatabaseHelper.getPersonDao().queryBuilder();

		List<Where<Contact, Long>> whereStatements = new ArrayList<>();
		Where<Contact, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		if (criteria.getCaze() != null) {
			whereStatements.add(where.eq(Contact.CASE_UUID, criteria.getCaze().getUuid()));
		} else {
			if (criteria.getFollowUpStatus() != null) {
				whereStatements.add(where.eq(Contact.FOLLOW_UP_STATUS, criteria.getFollowUpStatus()));
			}
			if (criteria.getContactClassification() != null) {
				whereStatements.add(where.eq(Contact.CONTACT_CLASSIFICATION, criteria.getContactClassification()));
			}
			if (criteria.getDisease() != null) {
				whereStatements.add(where.eq("caseDisease", criteria.getDisease()));
			}
			if (criteria.getReportDateFrom() != null) {
				whereStatements.add(where.ge(Contact.REPORT_DATE_TIME, DateHelper.getStartOfDay(criteria.getReportDateFrom())));
			}
			if (criteria.getReportDateTo() != null) {
				whereStatements.add(where.le(Contact.REPORT_DATE_TIME, DateHelper.getEndOfDay(criteria.getReportDateTo())));
			}
			if (!StringUtils.isEmpty(criteria.getTextFilter())) {
				String[] textFilters = criteria.getTextFilter().split("\\s+");
				for (String filter : textFilters) {
					String textFilter = "%" + filter.toLowerCase() + "%";
					if (!StringUtils.isEmpty(textFilter)) {
						whereStatements.add(
							where.or(
								where.raw(Contact.TABLE_NAME + "." + Contact.UUID + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
								where.raw(Person.TABLE_NAME + "." + Person.FIRST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
								where.raw(Person.TABLE_NAME + "." + Person.LAST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'")));
					}
				}
			}
		}

		if (!whereStatements.isEmpty()) {
			Where<Contact, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}
		queryBuilder = queryBuilder.leftJoin(personQueryBuilder);
		return queryBuilder;
	}

	public void deleteContactAndAllDependingEntities(String contactUuid) throws SQLException {
		deleteContactAndAllDependingEntities(queryUuidWithEmbedded(contactUuid));
	}

	public void deleteContactAndAllDependingEntities(Contact contact) throws SQLException {
		// Cancel if not in local database
		if (contact == null) {
			return;
		}

		// Delete all visits associated ONLY with this contact
		List<Visit> visits = DatabaseHelper.getVisitDao().getByContact(contact);
		for (Visit visit : visits) {
			if (DatabaseHelper.getContactDao().getCountByPersonAndDisease(visit.getPerson(), visit.getDisease()) <= 1) {
				DatabaseHelper.getVisitDao().deleteCascade(visit);
			}
		}

		// Delete all tasks associated with this contact
		List<Task> tasks = DatabaseHelper.getTaskDao().queryByContact(contact);
		for (Task task : tasks) {
			DatabaseHelper.getTaskDao().deleteCascade(task);
		}

		deleteCascade(contact);
	}

	public int getContactCountByCaseUuid(String caseUuid) {
		try {
			return (int) queryBuilder().where().eq(Contact.CASE_UUID, caseUuid).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getContactCountByCaseUuid on Contact");
			throw new RuntimeException(e);
		}
	}
}
