package de.symeda.sormas.app.backend.hospitalization;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDao extends AbstractAdoDao<PreviousHospitalization> {

    public PreviousHospitalizationDao(Dao<PreviousHospitalization, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<PreviousHospitalization> getAdoClass() {
        return PreviousHospitalization.class;
    }

    @Override
    public String getTableName() {
        return PreviousHospitalization.TABLE_NAME;
    }

    public List<PreviousHospitalization> getByHospitalization(Hospitalization hospitalization) {
        if (hospitalization.isSnapshot()) {
            return querySnapshotsForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
        }
        return queryForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
    }

    public PreviousHospitalization buildPreviousHospitalizationFromHospitalization(Case caze) {
        PreviousHospitalization previousHospitalization = super.build();
        Hospitalization hospitalization = caze.getHospitalization();

        if (hospitalization.getAdmissionDate() != null) {
            previousHospitalization.setAdmissionDate(hospitalization.getAdmissionDate());
        } else {
            previousHospitalization.setAdmissionDate(caze.getReportDate());
        }

        if (hospitalization.getDischargeDate() != null) {
            previousHospitalization.setDischargeDate(hospitalization.getDischargeDate());
        } else {
            previousHospitalization.setDischargeDate(new Date());
        }

        previousHospitalization.setRegion(caze.getRegion());
        previousHospitalization.setDistrict(caze.getDistrict());
        previousHospitalization.setCommunity(caze.getCommunity());
        previousHospitalization.setHealthFacility(caze.getHealthFacility());
        previousHospitalization.setHealthFacilityDetails(caze.getHealthFacilityDetails());
        previousHospitalization.setHospitalization(caze.getHospitalization());
        previousHospitalization.setIsolated(hospitalization.getIsolated());

        return previousHospitalization;
    }
}
