package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

@Entity(name = PreviousHospitalization.TABLE_NAME)
@DatabaseTable(tableName = Hospitalization.TABLE_NAME)
public class PreviousHospitalization extends AbstractDomainObject {

    private static final long serialVersionUID = 768263094433806267L;

    public static final String TABLE_NAME = "previoushospitalization";

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date admissionDate;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dischargeDate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
    private Facility healthFacility;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown isolated;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Hospitalization hospitalization;

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Facility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(Facility healthFacility) {
        this.healthFacility = healthFacility;
    }

    public YesNoUnknown getIsolated() {
        return isolated;
    }

    public void setIsolated(YesNoUnknown isolated) {
        this.isolated = isolated;
    }

    public Hospitalization getHospitalization() {
        return hospitalization;
    }

    public void setHospitalization(Hospitalization hospitalization) {
        this.hospitalization = hospitalization;
    }
}
