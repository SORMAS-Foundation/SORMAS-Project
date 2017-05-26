package de.symeda.sormas.app.backend.hospitalization;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDao extends AbstractAdoDao<Hospitalization> {

    public HospitalizationDao(Dao<Hospitalization,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Hospitalization> getAdoClass() {
        return Hospitalization.class;
    }

    @Override
    public String getTableName() {
        return Hospitalization.TABLE_NAME;
    }

    @Override
    public Hospitalization queryUuid(String uuid) {
        Hospitalization data = super.queryUuid(uuid);
        if (data != null) {
            initLazyData(data);
        }
        return data;
    }

    @Override
    public Hospitalization querySnapshotByUuid(String uuid) {
        Hospitalization data = super.querySnapshotByUuid(uuid);
        if (data != null) {
            initLazyData(data);
        }
        return data;
    }

    @Override
    public Hospitalization queryForId(Long id) {
        Hospitalization data = super.queryForId(id);
        if (data != null) {
            initLazyData(data);
        }
        return data;
    }

    private Hospitalization initLazyData(Hospitalization hospitalization) {
        hospitalization.setPreviousHospitalizations(DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization));
        return hospitalization;
    }

    @Override
    public Hospitalization saveAndSnapshot(Hospitalization ado) throws DaoException {

        Hospitalization snapshot = super.saveAndSnapshot(ado);

        DatabaseHelper.getPreviousHospitalizationDao().saveCollectionWithSnapshot(
                DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(ado),
                ado.getPreviousHospitalizations(), ado);

        return snapshot;
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date prevDate = DatabaseHelper.getPreviousHospitalizationDao().getLatestChangeDate();
        if (prevDate != null && prevDate.after(date)) {
            date = prevDate;
        }

        return date;
    }
}
