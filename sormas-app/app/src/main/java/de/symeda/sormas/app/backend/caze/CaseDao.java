package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class CaseDao extends AbstractAdoDao<Case> {

    public CaseDao(Dao<Case,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Case.TABLE_NAME;
    }

    @Override
    public boolean saveUnmodified(Case caze) {

        if (caze.getIllLocation() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(caze.getIllLocation());
        }
        if (caze.getSymptoms() != null) {
            DatabaseHelper.getSymptomsDao().saveUnmodified(caze.getSymptoms());
        }

        return super.saveUnmodified(caze);
    }

    @Override
    public Date getLatestChangeDate() {

        Date cazeDate = super.getLatestChangeDate();
        if (cazeDate == null) {
            return null;
        }
        Date symptomsDate = DatabaseHelper.getSymptomsDao().getLatestChangeDate();
        if (symptomsDate != null && symptomsDate.after(cazeDate)) {
            cazeDate = symptomsDate;
        }
        return cazeDate;
    }
    public void markAsModified(String uuid) {
        Case caze = queryUuid(uuid);
        save(caze);
    }
}
