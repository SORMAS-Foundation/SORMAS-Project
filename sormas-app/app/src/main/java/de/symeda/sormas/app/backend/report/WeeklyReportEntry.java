package de.symeda.sormas.app.backend.report;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */
@Entity(name = WeeklyReportEntry.TABLE_NAME)
@DatabaseTable(tableName = WeeklyReportEntry.TABLE_NAME)
public class WeeklyReportEntry extends AbstractDomainObject {

    private static final long serialVersionUID = -4161597011857710604L;

    public static final String TABLE_NAME = "weeklyreportentry";
    public static final String I18N_PREFIX = "WeeklyReportEntry";

    public static final String WEEKLY_REPORT = "weeklyReport";
    public static final String DISEASE = "disease";
    public static final String NUMBER_OF_CASES = "numberOfCases";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private WeeklyReport weeklyReport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Disease disease;

    @Column(nullable = false)
    private Integer numberOfCases;

    public WeeklyReport getWeeklyReport() {
        return weeklyReport;
    }

    public void setWeeklyReport(WeeklyReport weeklyReport) {
        this.weeklyReport = weeklyReport;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Integer getNumberOfCases() {
        return numberOfCases;
    }

    public void setNumberOfCases(Integer numberOfCases) {
        this.numberOfCases = numberOfCases;
    }

}
