package de.symeda.sormas.app.backend.report;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */
@Entity(name = WeeklyReport.TABLE_NAME)
@DatabaseTable(tableName = WeeklyReport.TABLE_NAME)
public class WeeklyReport extends AbstractDomainObject {

    private static final long serialVersionUID = 2192478891179257201L;

    public static final String TABLE_NAME = "weeklyreport";
    public static final String I18N_PREFIX = "WeeklyReport";

    public static final String HEALTH_FACILITY = "healthFacility";
    public static final String INFORMANT = "informant";
    public static final String REPORT_DATE_TIME = "reportDateTime";
    public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
    private Facility healthFacility;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private User informant;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date reportDateTime;

    @Column(nullable = false)
    private Integer totalNumberOfCases;

    public Facility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(Facility healthFacility) {
        this.healthFacility = healthFacility;
    }

    public User getInformant() {
        return informant;
    }

    public void setInformant(User informant) {
        this.informant = informant;
    }

    public Date getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(Date reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public Integer getTotalNumberOfCases() {
        return totalNumberOfCases;
    }

    public void setTotalNumberOfCases(Integer totalNumberOfCases) {
        this.totalNumberOfCases = totalNumberOfCases;
    }

}
