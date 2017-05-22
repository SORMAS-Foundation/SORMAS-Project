package de.symeda.sormas.app.backend.caze;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DataUtils;

public class CaseDao extends AbstractAdoDao<Case> {

    public CaseDao(Dao<Case,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Case> getAdoClass() {
        return Case.class;
    }

    @Override
    public String getTableName() {
        return Case.TABLE_NAME;
    }

    @Override
    public boolean save(Case caze) throws DaoException {
        return super.save(caze);
    }

    @Override
    public boolean saveUnmodified(Case caze) throws DaoException {
        // saving unmodified also includes cascading sub entities
        if (caze.getSymptoms() != null) {
            DatabaseHelper.getSymptomsDao().saveUnmodified(caze.getSymptoms());
        }
        if (caze.getHospitalization() != null) {
            DatabaseHelper.getHospitalizationDao().saveUnmodified(caze.getHospitalization());
        }
        if (caze.getEpiData() != null) {
            DatabaseHelper.getEpiDataDao().saveUnmodified(caze.getEpiData());
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
        Date hospitalizationDate = DatabaseHelper.getHospitalizationDao().getLatestChangeDate();
        if (hospitalizationDate != null && hospitalizationDate.after(cazeDate)) {
            cazeDate = hospitalizationDate;
        }
        Date epiDataDate = DatabaseHelper.getEpiDataDao().getLatestChangeDate();
        if (epiDataDate != null && epiDataDate.after(cazeDate)) {
            cazeDate = epiDataDate;
        }

        return cazeDate;
    }

    // TODO #69 create some date filter for finding the right case (this is implemented in CaseService.java too)
    public Case getByPersonAndDisease(Person person, Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Case.PERSON + "_id", person),
                    where.eq(Case.DISEASE, disease)
            );

            return (Case) builder.queryForFirst();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByPersonAndDisease");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Case create() {
        throw new UnsupportedOperationException("Use create(Person) instead");
    }

    public Case create(Person person) {
        Case caze = super.create();
        caze.setPerson(person);

        caze.setReportDate(new Date());
        caze.setReportingUser(ConfigProvider.getUser());

        // Symptoms
        caze.setSymptoms(DatabaseHelper.getSymptomsDao().create());

        // Hospitalization
        caze.setHospitalization(DatabaseHelper.getHospitalizationDao().create());

        // Epi Data
        caze.setEpiData(DatabaseHelper.getEpiDataDao().create());

        // Location
        User currentUser = ConfigProvider.getUser();
        caze.setRegion(currentUser.getRegion());
        caze.setDistrict(currentUser.getDistrict());
        caze.setHealthFacility(currentUser.getHealthFacility());
        if (caze.getHealthFacility() != null) {
            caze.setCommunity(caze.getHealthFacility().getCommunity());
        }

        return caze;
    }
}
