/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    public static final String YEAR = "year";
    public static final String EPI_WEEK = "epiWeek";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
    private Facility healthFacility;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private User informant;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date reportDateTime;

    @Column(nullable = false)
    private Integer totalNumberOfCases;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer epiWeek;

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getEpiWeek() {
        return epiWeek;
    }

    public void setEpiWeek(Integer epiWeek) {
        this.epiWeek = epiWeek;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }

}
