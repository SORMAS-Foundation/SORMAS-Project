package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

@Entity(name=SampleTest.TABLE_NAME)
@DatabaseTable(tableName = SampleTest.TABLE_NAME)
public class SampleTest extends AbstractDomainObject {

    private static final long serialVersionUID = 2290351143518627813L;

    public static final String TEST_DATE_TIME = "testDateTime";
    public static final String SAMPLE = "sample";

    public static final String TABLE_NAME = "sampleTests";
    public static final String I18N_PREFIX = "SampleTest";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Sample sample;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleTestType testType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleTestResultType testResult;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date testDateTime;

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public SampleTestType getTestType() {
        return testType;
    }

    public void setTestType(SampleTestType testType) {
        this.testType = testType;
    }

    public SampleTestResultType getTestResult() {
        return testResult;
    }

    public void setTestResult(SampleTestResultType testResult) {
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
