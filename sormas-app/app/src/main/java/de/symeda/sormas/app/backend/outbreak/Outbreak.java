package de.symeda.sormas.app.backend.outbreak;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 17.01.2018.
 */

@Entity(name= Outbreak.TABLE_NAME)
@DatabaseTable(tableName = Outbreak.TABLE_NAME)
public class Outbreak extends AbstractDomainObject {

    private static final long serialVersionUID = 6517638433928902578L;

    public static final String TABLE_NAME = "outbreak";
    public static final String I18N_PREFIX = "Outbreak";

    public static final String DISTRICT = "district_id";
    public static final String DISEASE = "disease";
    public static final String REPORTING_USER = "reportingUser_id";
    public static final String REPORT_DATE = "reportDate";

    @DatabaseField(foreign = true,  foreignAutoRefresh = true)
    private District district;
    @Enumerated(EnumType.STRING)
    private Disease disease;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User reportingUser;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date reportDate;

    public District getDistrict() {
        return district;
    }
    public void setDistrict(District district) {
        this.district = district;
    }

    public Disease getDisease() {
        return disease;
    }
    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public User getReportingUser() {
        return reportingUser;
    }
    public void setReportingUser(User reportingUser) {
        this.reportingUser = reportingUser;
    }

    public Date getReportDate() {
        return reportDate;
    }
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
