package de.symeda.sormas.backend.sample;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity(name="sampletest")
public class SampleTest extends AbstractDomainObject {

	private static final long serialVersionUID = 2290351143518627813L;
	
	public static final String SAMPLE = "sample";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_TYPE_TEXT = "testTypeText";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String LAB = "lab";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	
	private Sample sample;
	private SampleTestType testType;
	private String testTypeText;
	private Date testDateTime;
	private Facility lab;
	private User labUser;
	private SampleTestResultType testResult;
	private String testResultText;
	private boolean testResultVerified;
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Sample getSample() {
		return sample;
	}
	public void setSample(Sample sample) {
		this.sample = sample;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SampleTestType getTestType() {
		return testType;
	}
	public void setTestType(SampleTestType testType) {
		this.testType = testType;
	}
	
	@Column(length=512)
	public String getTestTypeText() {
		return testTypeText;
	}
	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getTestDateTime() {
		return testDateTime;
	}
	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Facility getLab() {
		return lab;
	}
	public void setLab(Facility lab) {
		this.lab = lab;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public User getLabUser() {
		return labUser;
	}
	public void setLabUser(User labUser) {
		this.labUser = labUser;
	}
	
	@Enumerated(EnumType.STRING)
	@JoinColumn(nullable = false)
	public SampleTestResultType getTestResult() {
		return testResult;
	}
	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}
	
	@Column(length=512, nullable = false)
	public String getTestResultText() {
		return testResultText;
	}
	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}
	
	@Column(nullable = false)
	public boolean isTestResultVerified() {
		return testResultVerified;
	}
	public void setTestResultVerified(boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

}
