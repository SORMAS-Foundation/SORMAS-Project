package de.symeda.sormas.app.backend.environment.environmentsample;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
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

    public void deleteEnvironmentSampleAndAllDependingEntities(String environmentUuid) throws SQLException {
        EnvironmentSample sample = queryUuidWithEmbedded(environmentUuid);

        // Cancel if not in local database
        if (sample == null) {
            return;
        }

        // Delete case
        deleteCascade(sample);
    }

    @Override
    public void create(EnvironmentSample data) throws SQLException {
        super.create(data);
    }
}
