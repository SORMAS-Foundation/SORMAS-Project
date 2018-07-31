package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SampleDao extends AbstractAdoDao<Sample> {

    public SampleDao(Dao<Sample, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Sample> getAdoClass() {
        return Sample.class;
    }

    @Override
    public Sample build() {
        throw new UnsupportedOperationException();
    }

    public Sample build(Case associatedCase) {
        Sample sample = super.build();
        sample.setAssociatedCase(associatedCase);
        sample.setReportDateTime(new Date());
        sample.setReportingUser(ConfigProvider.getUser());
        return sample;
    }

    public List<Sample> queryByCase(Case caze) {
        if (caze.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            return queryBuilder()
                    .orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .where().eq(Sample.ASSOCIATED_CASE + "_id", caze)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryByCase on Sample");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all not shipped samples (ignore received and referred)
     * Ordered by sampling date
     *
     * @return
     */
    public List<Sample> queryNotShipped() {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(AbstractDomainObject.SNAPSHOT, false),
                    where.eq(Sample.SHIPPED, false),
                    where.eq(Sample.RECEIVED, false),
                    where.or(
                            where.eq(Sample.REFERRED_TO_UUID, ""),
                            where.isNull(Sample.REFERRED_TO_UUID)
                    )
            );

            return builder
                    .orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryNotShipped on Sample");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all shipped samples (ignore received and referred)
     * Ordered by sampling date
     *
     * @return
     */
    public List<Sample> queryShipped() {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(AbstractDomainObject.SNAPSHOT, false),
                    where.eq(Sample.SHIPPED, true),
                    where.eq(Sample.RECEIVED, false),
                    where.or(
                            where.eq(Sample.REFERRED_TO_UUID, ""),
                            where.isNull(Sample.REFERRED_TO_UUID)
                    )
            );

            return builder
                    .orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryShipped on Sample");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all received samples (ignore referred)
     * Ordered by sampling date
     *
     * @return
     */
    public List<Sample> queryReceived() {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(AbstractDomainObject.SNAPSHOT, false),
                    where.eq(Sample.RECEIVED, true),
                    where.or(
                            where.eq(Sample.REFERRED_TO_UUID, ""),
                            where.isNull(Sample.REFERRED_TO_UUID)
                    )
            );

            return builder
                    .orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryReceived on Sample");
            throw new RuntimeException(e);
        }
    }

    public List<Sample> queryReferred() {
        return queryForNotNull(Sample.REFERRED_TO_UUID);
    }

    @Override
    public String getTableName() {
        return Sample.TABLE_NAME;
    }

}
