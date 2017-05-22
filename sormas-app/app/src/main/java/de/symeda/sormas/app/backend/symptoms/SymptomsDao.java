package de.symeda.sormas.app.backend.symptoms;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class SymptomsDao extends AbstractAdoDao<Symptoms> {

    public SymptomsDao(Dao<Symptoms,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public boolean save(Symptoms symptoms) throws DaoException {

        if (symptoms.getIllLocation() != null) {
            DatabaseHelper.getLocationDao().save(symptoms.getIllLocation());
        }

        return super.save(symptoms);
    }

    @Override
    public boolean saveUnmodified(Symptoms symptoms) throws DaoException {

        if (symptoms.getIllLocation() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(symptoms.getIllLocation());
        }

        return super.saveUnmodified(symptoms);
    }

    @Override
    protected Class<Symptoms> getAdoClass() {
        return Symptoms.class;
    }

    @Override
    public String getTableName() {
        return Symptoms.TABLE_NAME;
    }

    @Override
    public Symptoms create() {
        Symptoms symptoms = super.create();

        symptoms.setIllLocation(DatabaseHelper.getLocationDao().create());

        return symptoms;
    }

}
