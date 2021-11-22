/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.immunization;

import static de.symeda.sormas.app.backend.immunization.ImmunizationDaoHelper.overlappingDateRangeImmunizations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class ImmunizationDao extends AbstractAdoDao<Immunization> {

	public ImmunizationDao(Dao<Immunization, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Immunization> getAdoClass() {
		return Immunization.class;
	}

	@Override
	public String getTableName() {
		return Immunization.TABLE_NAME;
	}

	@Override
	public Immunization queryUuid(String uuid) {
		Immunization immunization = super.queryUuid(uuid);
		if (immunization != null) {
			initVaccinations(immunization);
		}
		return immunization;
	}

	@Override
	public Immunization queryForId(Long id) {
		Immunization immunization = super.queryForId(id);
		if (immunization != null) {
			initVaccinations(immunization);
		}
		return immunization;
	}

	@Override
	public Immunization querySnapshotByUuid(String uuid) {
		Immunization immunization = super.querySnapshotByUuid(uuid);
		if (immunization != null) {
			initVaccinations(immunization);
		}
		return immunization;
	}

	public List<Immunization> getAll() {
		try {
			QueryBuilder<Immunization, Long> queryBuilder = queryBuilder();
			return queryBuilder.orderBy(Immunization.CHANGE_DATE, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllActive on Immunization");
			throw new RuntimeException(e);
		}
	}

	public List<Immunization> queryByCriteria(ImmunizationCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Immunization.CHANGE_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Immunization");
			throw new RuntimeException(e);
		}
	}

	public List<Immunization> queryAllByCriteria(ImmunizationCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).orderBy(Immunization.CHANGE_DATE, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Immunization");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(ImmunizationCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Immunization");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Immunization, Long> buildQueryBuilder(ImmunizationCriteria criteria) throws SQLException {
		QueryBuilder<Immunization, Long> queryBuilder = queryBuilder();

		List<Where<Immunization, Long>> whereStatements = new ArrayList<>();
		Where<Immunization, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		addDateFromCriteria(whereStatements, where, criteria.getPositiveTestResultDateFrom(), Immunization.POSITIVE_TEST_RESULT_DATE);
		addDateToCriteria(whereStatements, where, criteria.getPositiveTestResultDateTo(), Immunization.POSITIVE_TEST_RESULT_DATE);

		addDateFromCriteria(whereStatements, where, criteria.getRecoveryDateFrom(), Immunization.RECOVERY_DATE);
		addDateToCriteria(whereStatements, where, criteria.getRecoveryDateTo(), Immunization.RECOVERY_DATE);

		addDateFromCriteria(whereStatements, where, criteria.getReportDateFrom(), Immunization.REPORT_DATE);
		addDateToCriteria(whereStatements, where, criteria.getReportDateTo(), Immunization.REPORT_DATE);

		addDateFromCriteria(whereStatements, where, criteria.getStartDateFrom(), Immunization.START_DATE);
		addDateToCriteria(whereStatements, where, criteria.getEndDateTo(), Immunization.END_DATE);

		addDateFromCriteria(whereStatements, where, criteria.getValidFrom(), Immunization.VALID_FROM);
		addDateToCriteria(whereStatements, where, criteria.getValidUntil(), Immunization.VALID_UNTIL);

		addEqualsCriteria(whereStatements, where, criteria.getImmunizationStatus(), Immunization.IMMUNIZATION_STATUS);
		addEqualsCriteria(whereStatements, where, criteria.getImmunizationManagementStatus(), Immunization.IMMUNIZATION_MANAGEMENT_STATUS);
		addEqualsCriteria(whereStatements, where, criteria.getMeansOfImmunization(), Immunization.MEANS_OF_IMMUNIZATION);
		addEqualsCriteria(whereStatements, where, criteria.getDisease(), Immunization.DISEASE);
		addEqualsCriteria(whereStatements, where, criteria.getPerson(), Immunization.PERSON + "_id");

		if (!whereStatements.isEmpty()) {
			Where<Immunization, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}

		return queryBuilder;
	}

	public void deleteImmunizationAndAllDependingEntities(String immunizationUuid) throws SQLException {
		Immunization immunization = queryUuidWithEmbedded(immunizationUuid);

		// Cancel if not in local database
		if (immunization == null) {
			return;
		}

		// Delete case
		deleteCascade(immunization);
	}

	public Immunization build(Person person) {
		Immunization immunization = super.build();
		immunization.setPerson(person);
		immunization.setImmunizationManagementStatus(ImmunizationManagementStatus.SCHEDULED);

		User user = ConfigProvider.getUser();
		immunization.setReportingUser(user);

		// Location
		User currentUser = ConfigProvider.getUser();

		// Set the disease if a default disease is available
		Disease defaultDisease = DiseaseConfigurationCache.getInstance().getDefaultDisease();
		if (defaultDisease != null) {
			immunization.setDisease(defaultDisease);
		}

		if (UserRole.isPortHealthUser(currentUser.getUserRoles())) {
			immunization.setResponsibleRegion(currentUser.getRegion());
			immunization.setResponsibleDistrict(currentUser.getDistrict());
			immunization.setDisease(Disease.UNDEFINED);
		} else if (currentUser.getHealthFacility() != null) {
			immunization.setResponsibleRegion(currentUser.getHealthFacility().getRegion());
			immunization.setResponsibleDistrict(currentUser.getHealthFacility().getDistrict());
			immunization.setResponsibleCommunity(currentUser.getHealthFacility().getCommunity());
		} else {
			immunization.setResponsibleRegion(currentUser.getRegion());
			immunization.setResponsibleDistrict(currentUser.getDistrict());
			immunization.setResponsibleCommunity(currentUser.getCommunity());
		}

		return immunization;
	}

	public List<Immunization> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria) {
		try {
			QueryBuilder<Immunization, Long> queryBuilder = queryBuilder();
			QueryBuilder<Person, Long> personQueryBuilder = DatabaseHelper.getPersonDao().queryBuilder();

			Where<Immunization, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);
			ImmunizationCriteria immunizationCriteria = criteria.getImmunizationCriteria();
			where.and().ne(Immunization.UUID, criteria.getImmunizationUuid());
			where.and().eq(Immunization.DISEASE, immunizationCriteria.getDisease());
			if (criteria.getPersonUuid() != null) {
				where.and().raw(Person.TABLE_NAME + "." + Person.UUID + " = '" + criteria.getPersonUuid() + "'");
			}
			if (immunizationCriteria.getMeansOfImmunization() != null) {
				where.and().eq(Immunization.MEANS_OF_IMMUNIZATION, immunizationCriteria.getMeansOfImmunization());
			}
			queryBuilder.setWhere(where);
			queryBuilder = queryBuilder.leftJoin(personQueryBuilder);

			List<Immunization> immunizations = queryBuilder.orderBy(Immunization.CREATION_DATE, false).query();

			final Date startDate = criteria.getStartDate();
			final Date endDate = criteria.getEndDate();
			return overlappingDateRangeImmunizations(immunizations, startDate, endDate);
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getSimilarImmunizations on immunization");
			throw new RuntimeException(e);
		}
	}

	public Immunization initVaccinations(Immunization immunization) {
		immunization.setVaccinations(DatabaseHelper.getVaccinationDao().getByImmunization(immunization));
		return immunization;
	}

	@Override
	public Immunization saveAndSnapshot(final Immunization immunization) throws DaoException {

		Immunization snapshot = super.saveAndSnapshot(immunization);

		return snapshot;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		String query = "SELECT MAX(v." + AbstractDomainObject.CHANGE_DATE + ") FROM " + Vaccination.TABLE_NAME + " AS v" + " LEFT JOIN "
			+ Immunization.TABLE_NAME + " AS i ON i." + AbstractDomainObject.ID + " = v." + Vaccination.IMMUNIZATION + "_ID";
		Date vaccinationDate = getLatestChangeDateJoinFromQuery(query);

		if (vaccinationDate != null && vaccinationDate.after(date)) {
			date = vaccinationDate;
		}

		return date;
	}
}
