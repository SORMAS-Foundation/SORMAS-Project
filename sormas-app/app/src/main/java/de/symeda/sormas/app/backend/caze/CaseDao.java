package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;

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

    public void changeCaseStatus(Case caze, CaseStatus targetStatus) {
        caze.setCaseStatus(targetStatus);

        switch (targetStatus) {
            case INVESTIGATED:
                caze.setInvestigatedDate(new Date());
                break;
//            case CONFIRMED:
//                caze.setConfirmedDate(new Date());
//                break;
//            case NO_CASE:
//                caze.setNoCaseDate(new Date());
//                break;
//            case RECOVERED:
//                caze.setRecoveredDate(new Date());
//                break;
//            case SUSPECT:
//                caze.setSuspectDate(new Date());
//                break;
            // TODO others...
            // TODO what about going back and forth?
            default:
                break;
        }

        save(caze);
    }

}
