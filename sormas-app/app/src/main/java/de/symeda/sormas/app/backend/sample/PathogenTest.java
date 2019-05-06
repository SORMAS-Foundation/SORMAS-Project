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

package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name= PathogenTest.TABLE_NAME)
@DatabaseTable(tableName = PathogenTest.TABLE_NAME)
public class PathogenTest extends AbstractDomainObject {

    private static final long serialVersionUID = 2290351143518627813L;

    public static final String TEST_DATE_TIME = "testDateTime";
    public static final String SAMPLE = "sample";

    public static final String TABLE_NAME = "pathogenTests";
    public static final String I18N_PREFIX = "PathogenTest";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Sample sample;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PathogenTestType testType;

    @Enumerated(EnumType.STRING)
    private Disease testedDisease;

    @Column(length = 512)
    private String testedDiseaseDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PathogenTestResultType testResult;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date testDateTime;

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public PathogenTestType getTestType() {
        return testType;
    }

    public void setTestType(PathogenTestType testType) {
        this.testType = testType;
    }

    public Disease getTestedDisease() {
        return testedDisease;
    }

    public void setTestedDisease(Disease testedDisease) {
        this.testedDisease = testedDisease;
    }

    public String getTestedDiseaseDetails() {
        return testedDiseaseDetails;
    }

    public void setTestedDiseaseDetails(String testedDiseaseDetails) {
        this.testedDiseaseDetails = testedDiseaseDetails;
    }

    public PathogenTestResultType getTestResult() {
        return testResult;
    }

    public void setTestResult(PathogenTestResultType testResult) {
        this.testResult = testResult;
    }

    public Date getTestDateTime() {
        return testDateTime;
    }

    public void setTestDateTime(Date testDateTime) {
        this.testDateTime = testDateTime;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }

    @Override
    public String toString() {
        return super.toString() + DateHelper.formatLocalShortDate(getTestDateTime());
    }
}
