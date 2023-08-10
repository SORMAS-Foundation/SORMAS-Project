package de.symeda.sormas.app.backend.environment;

import java.security.spec.EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.util.LocationService;

public class EnvironmentDao extends AbstractAdoDao<Environment> {

	public EnvironmentDao(Dao<Environment, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<Environment> getAdoClass() {
		return Environment.class;
	}

	@Override
	public String getTableName() {
		return Environment.TABLE_NAME;
	}

	public List<Environment> getAll() {
		try {
			QueryBuilder<Environment, Long> queryBuilder = queryBuilder();
			return queryBuilder.orderBy(Environment.CHANGE_DATE, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllActive on Environment", e);
			throw new RuntimeException();
		}
	}

	public long countByCriteria(EnvironmentCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Environment", e);
			throw new RuntimeException();
		}
	}

	public List<Environment> queryByCriteria(EnvironmentCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Environment.LOCAL_CHANGE_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Environment", e);
			throw new RuntimeException();
		}
	}

	private QueryBuilder<Environment, Long> buildQueryBuilder(EnvironmentCriteria criteria) throws SQLException {
		QueryBuilder<Environment, Long> queryBuilder = queryBuilder();
		List<Where<Environment, Long>> whereStatements = new ArrayList<>();
		Where<Environment, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		addEqualsCriteria(whereStatements, where, criteria.getInvestigationStatus(), Environment.INVESTIGATION_STATUS);

		if (!whereStatements.isEmpty()) {
			Where<Environment, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}

		return queryBuilder;
	}

	@Override
	public Environment build() {
		Environment environment = super.build();
		environment.setReportingUser(ConfigProvider.getUser());
		environment.setInvestigationStatus(InvestigationStatus.PENDING);
		return environment;
	}

	public void deleteEnvironmentAndAllDependingEntities(String environmentUuid) throws SQLException {
		Environment environment = queryUuidWithEmbedded(environmentUuid);

		// Cancel if not in local database
		if (environment == null) {
			return;
		}

		// Delete case
		deleteCascade(environment);
	}

	@Override
	public Environment saveAndSnapshot(final Environment environment) throws DaoException {
		// If a new environment is created, use the last available location to update its report latitude and longitude
		if (environment.getId() == null) {
			android.location.Location location = LocationService.instance().getLocation();
			if (location != null) {
				environment.getLocation().setLatitude(location.getLatitude());
				environment.getLocation().setLongitude(location.getLongitude());
				environment.getLocation().setLatLonAccuracy(location.getAccuracy());
			}
		}

		return super.saveAndSnapshot(environment);
	}
}
