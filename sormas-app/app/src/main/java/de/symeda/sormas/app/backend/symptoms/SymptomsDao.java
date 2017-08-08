package de.symeda.sormas.app.backend.symptoms;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
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
    protected Class<Symptoms> getAdoClass() {
        return Symptoms.class;
    }

    @Override
    public String getTableName() {
        return Symptoms.TABLE_NAME;
    }

    public void updateIsSymptomatic(Symptoms symptoms) {
        SymptomsDtoHelper symptomsDtoHelper = new SymptomsDtoHelper();
        SymptomsDto symptomsDto = new SymptomsDto();
        symptomsDtoHelper.fillInnerFromAdo(symptomsDto, symptoms);
        SymptomsHelper.updateIsSymptomatic(symptomsDto);
        symptoms.setSymptomatic(symptomsDto.getSymptomatic());
    }

}
