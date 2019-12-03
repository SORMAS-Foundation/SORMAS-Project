package de.symeda.sormas.app.backend.feature;

import android.util.Log;

import com.google.android.gms.common.Feature;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.apache.commons.collections.ListUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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

    public boolean isLineListingEnabled(Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(FeatureConfiguration.FEATURE_TYPE, FeatureType.LINE_LISTING),
                    where.eq(FeatureConfiguration.DISEASE, disease),
                    where.ge(FeatureConfiguration.END_DATE, DateHelper.getStartOfDay(new Date())));
            List<FeatureConfiguration> result = builder.query();
            return result != null && !result.isEmpty();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform isLineListingEnabled");
            throw new RuntimeException(e);
        }
    }

    public void deleteExpiredFeatureConfigurations() {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.lt(FeatureConfiguration.END_DATE, new Date());
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
