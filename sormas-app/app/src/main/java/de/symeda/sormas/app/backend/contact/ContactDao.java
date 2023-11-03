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
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
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
			qb.orderBy(Contact.LOCAL_CHANGE_DATE, false);
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
				where.and(where, where.eq(Contact.DISEASE_COLUMN, disease));
			}
			qb.orderBy(Contact.LOCAL_CHANGE_DATE, false);
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
		if (user.getCommunity() != null) {
			contact.setCommunity(user.getCommunity());
		}

		// Epi Data
		contact.setEpiData(DatabaseHelper.getEpiDataDao().build());

		contact.setHealthConditions(DatabaseHelper.getHealthConditionsDao().build());

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
			return buildQueryBuilder(criteria).orderBy(Contact.LOCAL_CHANGE_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Contact");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Contact, Long> buildQueryBuilder(ContactCriteria contactCriteria) throws SQLException {
		QueryBuilder<Contact, Long> queryBuilder = queryBuilder();

		QueryBuilder<Case, Long> caseQueryBuilder = DatabaseHelper.getCaseDao().queryBuilder();
		queryBuilder.join(Contact.CASE_UUID, Case.UUID, caseQueryBuilder, QueryBuilder.JoinType.LEFT, QueryBuilder.JoinWhereOperation.AND);

		Where<Contact, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		// Only use user filter if no restricting case is specified
		if (contactCriteria == null || contactCriteria.getCaze() == null) {
			if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions().equals(true)) {
				where.and();
				createJurisdictionFilter(where, contactCriteria);
				where.or();
				createJurisdictionFilterWithoutCase(where, contactCriteria);
			} else {
				where.and();
				createJurisdictionFilterWithoutCase(where, contactCriteria);
			}
		}

		if (contactCriteria != null) {
			createCriteriaFilter(where, contactCriteria);
		}

		queryBuilder.setWhere(where);
		return queryBuilder;
	}

	public Where<Contact, Long> createJurisdictionFilter(Where<Contact, Long> where, ContactCriteria contactCriteria) {
		List<Where<Contact, Long>> whereJurisdictionFilterStatements = new ArrayList<>();

		User currentUser = ConfigProvider.getUser();
		if (currentUser == null) {
			return null;
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();

		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			//whoever created the case or is assigned to it is allowed to access it
			if (contactCriteria.getIncludeContactsFromOtherJurisdictions().equals(true)) {
				whereJurisdictionFilterStatements.add(
					where.or(
						where.raw(Case.TABLE_NAME + "." + Case.REPORTING_USER + "= '" + currentUser.getId() + "'"),
						where.raw(Case.TABLE_NAME + "." + Case.SURVEILLANCE_OFFICER + "= '" + currentUser.getId() + "'"),
						where.raw(Case.TABLE_NAME + "." + Case.CASE_OFFICER + "= '" + currentUser.getId() + "'")));
			}

			switch (jurisdictionLevel) {
			case REGION:
				Region region = currentUser.getRegion();
				if (region != null) {
					whereJurisdictionFilterStatements.add(
						where.or(
							where.raw(Case.TABLE_NAME + "." + Case.REGION + "= '" + region + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.RESPONSIBLE_REGION + "= '" + region.getId() + "'")));
				}
				break;

			case DISTRICT:
				District district = currentUser.getDistrict();
				if (district != null) {
					whereJurisdictionFilterStatements.add(
						where.or(
							where.raw(Case.TABLE_NAME + "." + Case.DISTRICT + "= '" + district + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.RESPONSIBLE_DISTRICT + "= '" + district.getId() + "'")));
				}
				break;

			case HEALTH_FACILITY:
				Facility healthFacility = currentUser.getHealthFacility();
				if (healthFacility != null) {
					whereJurisdictionFilterStatements.add(where.raw(Case.TABLE_NAME + "." + Case.HEALTH_FACILITY + "= '" + healthFacility + "'"));
				}
				break;

			case COMMUNITY:
				Community community = currentUser.getCommunity();
				if (community != null) {
					whereJurisdictionFilterStatements.add(
						where.or(
							where.raw(Case.TABLE_NAME + "." + Case.COMMUNITY + "= '" + community + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.RESPONSIBLE_COMMUNITY + "= '" + community.getId() + "'")));
				}
				break;

			case POINT_OF_ENTRY:
				PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
				if (pointOfEntry != null) {
					whereJurisdictionFilterStatements
						.add(where.raw(Case.TABLE_NAME + "." + Case.POINT_OF_ENTRY + "= '" + pointOfEntry.getId() + "'"));
				}
				break;
			default:
			}
		}

		if (!whereJurisdictionFilterStatements.isEmpty()) {
			where.or(whereJurisdictionFilterStatements.size());
		}
		return where;
	}

	public Where<Contact, Long> createJurisdictionFilterWithoutCase(Where<Contact, Long> where, ContactCriteria contactCriteria) throws SQLException {
		List<Where<Contact, Long>> whereUserFilterStatements = new ArrayList<>();

		User currentUser = ConfigProvider.getUser();
		if (currentUser == null) {
			return null;
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();

		// whoever created it or is assigned to it is allowed to access it
		if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions()) {
			whereUserFilterStatements
				.add(where.or(where.eq(Contact.REPORTING_USER, currentUser.getId()), where.eq(Contact.CONTACT_OFFICER, currentUser.getId())));
		}

		switch (jurisdictionLevel) {
		case REGION:
			Region region = currentUser.getRegion();
			if (region != null) {
				whereUserFilterStatements.add(where.eq(Contact.REGION, currentUser.getRegion().getId()));
			}
			break;

		case DISTRICT:
			District district = currentUser.getDistrict();
			if (district != null) {
				whereUserFilterStatements.add(where.eq(Contact.DISTRICT, currentUser.getDistrict().getId()));
			}
			break;

		case COMMUNITY:
			Community community = currentUser.getCommunity();
			if (community != null) {
				whereUserFilterStatements.add(where.eq(Contact.COMMUNITY, currentUser.getCommunity().getId()));
			}
			break;
		default:
		}

		if (!whereUserFilterStatements.isEmpty()) {
			where.or(whereUserFilterStatements.size());
		}

		return where;
	}

	public Where<Contact, Long> createCriteriaFilter(Where<Contact, Long> where, ContactCriteria criteria) throws SQLException {

		if (criteria.getCaze() != null) {
			where.and();
			where.eq(Contact.CASE_UUID, criteria.getCaze().getUuid());
		} else {
			if (criteria.getFollowUpStatus() != null) {
				where.and();
				where.eq(Contact.FOLLOW_UP_STATUS, criteria.getFollowUpStatus());
			}
			if (criteria.getContactClassification() != null) {
				where.and();
				where.eq(Contact.CONTACT_CLASSIFICATION, criteria.getContactClassification());
			}
			if (criteria.getDisease() != null) {
				where.and();
				where.eq("caseDisease", criteria.getDisease());
			}
			if (criteria.getReportDateFrom() != null) {
				where.and();
				where.ge(Contact.REPORT_DATE_TIME, DateHelper.getStartOfDay(criteria.getReportDateFrom()));
			}
			if (criteria.getReportDateTo() != null) {
				where.and();
				where.le(Contact.REPORT_DATE_TIME, DateHelper.getEndOfDay(criteria.getReportDateTo()));
			}
			if (!StringUtils.isEmpty(criteria.getTextFilter())) {
				String[] textFilters = criteria.getTextFilter().split("\\s+");
				for (String filter : textFilters) {
					String textFilter = "%" + filter.toLowerCase() + "%";
					if (!StringUtils.isEmpty(textFilter)) {
						where.and();
						where.or(
							where.raw(Contact.TABLE_NAME + "." + Contact.UUID + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.FIRST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.LAST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'"));
					}
				}
			}
		}

		return where;
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

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date epiDataDate = getLatestChangeDateJoin(EpiData.TABLE_NAME, Contact.EPI_DATA);
		if (epiDataDate != null && epiDataDate.after(date)) {
			date = epiDataDate;
		}

		Date exposureDate = getLatestChangeDateSubJoin(EpiData.TABLE_NAME, Contact.EPI_DATA, Exposure.TABLE_NAME);
		if (exposureDate != null && exposureDate.after(date)) {
			date = exposureDate;
		}

		Date healthConditionsDate = getLatestChangeDateJoin(HealthConditions.TABLE_NAME, Contact.HEALTH_CONDITIONS);
		if (healthConditionsDate != null && healthConditionsDate.after(date)) {
			date = healthConditionsDate;
		}

		return date;
	}
}
