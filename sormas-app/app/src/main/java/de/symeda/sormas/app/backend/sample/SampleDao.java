package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.util.DataUtils;

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
    public Sample create() {
        throw new UnsupportedOperationException();
    }

    public Sample create(Case associatedCase) {
        Sample sample = super.create();
        sample.setAssociatedCase(associatedCase);
        sample.setReportDateTime(new Date());
        sample.setReportingUser(ConfigProvider.getUser());
        sample.setShipmentStatus(ShipmentStatus.NOT_SHIPPED);
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

    @Override
    public String getTableName() {
        return Sample.TABLE_NAME;
    }

}
