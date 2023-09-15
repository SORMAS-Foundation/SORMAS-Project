package de.symeda.sormas.app.backend.environment.environmentsample;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.location.Location;

public class EnvironmentSampleDao extends AbstractAdoDao<EnvironmentSample> {

	public EnvironmentSampleDao(Dao<EnvironmentSample, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<EnvironmentSample> getAdoClass() {
		return EnvironmentSample.class;
	}

	@Override
	public EnvironmentSample build() {
		throw new UnsupportedOperationException();
	}

	public EnvironmentSample build(Environment associatedEnvironment) {

		EnvironmentSample sample = super.build();
		sample.setEnvironment(associatedEnvironment);
		sample.setReportDate(new Date());
		sample.setReportingUser(ConfigProvider.getUser());
		sample.setSampleDateTime(new Date());
		sample.setLocation(associatedEnvironment.getLocation().asNewLocation());
		return sample;
	}

	@Override
	public String getTableName() {
		return EnvironmentSample.TABLE_NAME;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, EnvironmentSample.LOCATION);
		if (locationDate != null && locationDate.after(date)) {
			date = locationDate;
		}

		return date;
	}

	public long countByCriteria(EnvironmentSampleCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on EnvironmentSample");
			throw new RuntimeException(e);
		}
	}

	public List<EnvironmentSample> queryByCriteria(EnvironmentSampleCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(EnvironmentSample.SAMPLE_DATE_TIME, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on EnvironmentSample");
			throw new RuntimeException(e);
		}
	}

	public void deleteEnvironmentSampleAndAllDependingEntities(String environmentUuid) throws SQLException {
		EnvironmentSample sample = queryUuidWithEmbedded(environmentUuid);

		// Cancel if not in local database
		if (sample == null) {
			return;
		}

		// Delete case
		deleteCascade(sample);
	}

	private QueryBuilder<EnvironmentSample, Long> buildQueryBuilder(EnvironmentSampleCriteria criteria) throws SQLException {

		QueryBuilder<EnvironmentSample, Long> queryBuilder = queryBuilder();
		Where<EnvironmentSample, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		if (criteria.getEnvironment() != null) {
			where.and().eq(EnvironmentSample.ENVIRONMENT + "_id", criteria.getEnvironment());
		} else {
			if (criteria.getShipmentStatus() != null) {
				switch (criteria.getShipmentStatus()) {
				case NOT_SHIPPED:
					where.and().and(where.eq(EnvironmentSample.DISPATCHED, false), where.eq(EnvironmentSample.RECEIVED, false));
					break;
				case SHIPPED:
					where.and().and(where.eq(EnvironmentSample.DISPATCHED, true), where.eq(EnvironmentSample.RECEIVED, false));
					break;
				case RECEIVED:
					where.and().eq(EnvironmentSample.RECEIVED, true);
					break;
				default:
					throw new IllegalArgumentException(criteria.getShipmentStatus().toString());
				}
			}
		}

		queryBuilder.setWhere(where);
		return queryBuilder;

	}

	@Override
	public void create(EnvironmentSample data) throws SQLException {
		super.create(data);
	}
}
