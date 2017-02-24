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

@Entity(name = Hospitalization.TABLE_NAME)
@DatabaseTable(tableName = Hospitalization.TABLE_NAME)
public class Hospitalization extends AbstractDomainObject {

    private static final long serialVersionUID = -8576270649634034244L;

    public static final String TABLE_NAME = "hospitalization";

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date admissionDate;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dischargeDate;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown isolated;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date isolationDate;

    @Enumerated(EnumType.STRING)
    private YesNoUnknown hospitalizedPreviously;

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

    public YesNoUnknown getIsolated() {
        return isolated;
    }

    public void setIsolated(YesNoUnknown isolated) {
        this.isolated = isolated;
    }

    public Date getIsolationDate() {
        return isolationDate;
    }

    public void setIsolationDate(Date isolationDate) {
        this.isolationDate = isolationDate;
    }

    public YesNoUnknown getHospitalizedPreviously() {
        return hospitalizedPreviously;
    }

    public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
        this.hospitalizedPreviously = hospitalizedPreviously;
    }
}
