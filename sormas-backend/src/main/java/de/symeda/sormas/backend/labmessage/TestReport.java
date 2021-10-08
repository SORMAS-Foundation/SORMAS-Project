package de.symeda.sormas.backend.labmessage;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.sample.PathogenTest;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

@Entity(name = TestReport.TABLE_NAME)
@Audited
public class TestReport extends CoreAdo {

	private static final long serialVersionUID = -9164498173635523905L;

	public static final String TABLE_NAME = "testreport";

	public static final String LAB_MESSAGE = "labMessage";
	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_EXTERNAL_ID = "testLabExternalId";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TEST_LAB_CITY = "testLabCity";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String PATHOGEN_TEST = "pathogenTest";

	private LabMessage labMessage;
	private String testLabName;
	private String testLabExternalId;
	private String testLabPostalCode;
	private String testLabCity;

	private PathogenTestType testType;
	private Date testDateTime;
	private PathogenTestResultType testResult;
	private Boolean testResultVerified;
	private String testResultText;
	private String typingId;

	private PathogenTest pathogenTest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public LabMessage getLabMessage() {
		return labMessage;
	}

	public void setLabMessage(LabMessage labMessage) {
		this.labMessage = labMessage;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTestLabExternalId() {
		return testLabExternalId;
	}

	public void setTestLabExternalId(String testLabExternalId) {
		this.testLabExternalId = testLabExternalId;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTestLabPostalCode() {
		return testLabPostalCode;
	}

	public void setTestLabPostalCode(String testLabPostalCode) {
		this.testLabPostalCode = testLabPostalCode;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTestLabCity() {
		return testLabCity;
	}

	public void setTestLabCity(String testLabCity) {
		this.testLabCity = testLabCity;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Column
	public Boolean isTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PathogenTest getPathogenTest() {
		return pathogenTest;
	}

	public void setPathogenTest(PathogenTest pathogenTest) {
		this.pathogenTest = pathogenTest;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}
}
