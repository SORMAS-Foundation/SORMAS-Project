package de.symeda.sormas.app.backend.facility;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class FacilityDao extends AbstractAdoDao<Facility> {

    public FacilityDao(Dao<Facility,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Facility.TABLE_NAME;
    }

}
