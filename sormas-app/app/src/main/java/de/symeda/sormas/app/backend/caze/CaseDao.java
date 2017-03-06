package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.util.DataUtils;

public class CaseDao extends AbstractAdoDao<Case> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

    public CaseDao(Dao<Case,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Case.TABLE_NAME;
    }

    @Override
    public boolean saveUnmodified(Case caze) {
        try {

            if (caze.getIllLocation() != null) {
                DatabaseHelper.getLocationDao().saveUnmodified(caze.getIllLocation());
            }
            if (caze.getSymptoms() != null) {
                DatabaseHelper.getSymptomsDao().saveUnmodified(caze.getSymptoms());
            }
            if (caze.getHospitalization() != null) {
                DatabaseHelper.getHospitalizationDao().saveUnmodified(caze.getHospitalization());
                if (caze.getHospitalization().getPreviousHospitalizations() != null && !caze.getHospitalization().getPreviousHospitalizations().isEmpty()) {
                    DatabaseHelper.getPreviousHospitalizationDao().deleteByHospitalization(caze.getHospitalization());
                    for (PreviousHospitalization previousHospitalization : caze.getHospitalization().getPreviousHospitalizations()) {
                        DatabaseHelper.getPreviousHospitalizationDao().saveUnmodified(previousHospitalization);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e, "saveUnmodified(Case caze) threw exception on: " + e.getCause());
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

    // TODO #69 create some date filter for finding the right case (this is implemented in CaseService.java too)
    public Case getByPersonAndDisease(Person person, Disease disease) {
        try {

            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                    where.eq(Case.PERSON+"_id", person),
                    where.eq(Case.DISEASE, disease)
            );

            return (Case) builder.queryForFirst();

        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "query getByPersonAndDisease threw exception");
            throw new RuntimeException(e);
        }
    }

    public static Case createCase(Person person) throws InstantiationException, IllegalAccessException {
        Case caze = DataUtils.createNew(Case.class);
        caze.setPerson(person);

        // Symptoms
        caze.setSymptoms(DataUtils.createNew(Symptoms.class));

        // Hospitalization
        caze.setHospitalization(DataUtils.createNew(Hospitalization.class));

        return caze;
    }
}
