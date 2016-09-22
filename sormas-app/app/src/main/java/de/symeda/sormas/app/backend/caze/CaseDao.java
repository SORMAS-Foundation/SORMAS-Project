package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
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


    public void changeCaseStatus(Case caze, CaseStatus targetStatus) {
        caze.setCaseStatus(targetStatus);

        switch (targetStatus) {
            case INVESTIGATED:
                caze.setInvestigatedDate(new Date());
                break;
            case CONFIRMED:
                caze.setConfirmedDate(new Date());
                break;
            case NO_CASE:
                caze.setNoCaseDate(new Date());
                break;
            case RECOVERED:
                caze.setRecoveredDate(new Date());
                break;
            case SUSPECT:
                caze.setSuspectDate(new Date());
                break;
            // TODO others...
            // TODO what about going back and forth?
            default:
                break;
        }

        save(caze);
    }

}
