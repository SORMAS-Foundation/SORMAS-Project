package de.symeda.sormas.app.backend.feature;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class FeatureConfigurationDao extends AbstractAdoDao<FeatureConfiguration> {

	public FeatureConfigurationDao(Dao<FeatureConfiguration, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<FeatureConfiguration> getAdoClass() {
		return FeatureConfiguration.class;
	}

	@Override
	public String getTableName() {
		return FeatureConfiguration.TABLE_NAME;
	}

	public List<Disease> getDiseasesWithLineListing() {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ")
			.append(FeatureConfiguration.DISEASE)
			.append(" FROM ")
			.append(getTableName())
			.append(" WHERE ")
			.append(FeatureConfiguration.FEATURE_TYPE)
			.append(" = '")
			.append(FeatureType.LINE_LISTING)
			.append("'")
			.append(" AND ")
			.append(FeatureConfiguration.END_DATE)
			.append(" <= '")
			.append(DateHelper.getStartOfDay(new Date()))
			.append("'");
		GenericRawResults<Object[]> rawResult = queryRaw(
			queryBuilder.toString(),
			new DataType[] {
				DataType.ENUM_STRING });
		List<Disease> diseases = new ArrayList<>();
		for (Object[] result : rawResult) {
			diseases.add(Disease.valueOf((String) result[0]));
		}
		return diseases;
	}

	public boolean isFeatureDisabled(FeatureType featureType) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(FeatureConfiguration.FEATURE_TYPE, featureType);
			where.and().eq(FeatureConfiguration.ENABLED, false);
			return builder.countOf() > 0;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform isFeatureDisabled");
			throw new RuntimeException(e);
		}
	}

	public void deleteExpiredFeatureConfigurations() {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.isNotNull(FeatureConfiguration.END_DATE);
			where.and().lt(FeatureConfiguration.END_DATE, new Date());
			List<FeatureConfiguration> result = builder.query();
			if (result != null) {
				for (FeatureConfiguration config : result) {
					delete(config);
				}
			}
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform deleteExpiredFeatureConfigurations");
			throw new RuntimeException(e);
		}
	}
}
