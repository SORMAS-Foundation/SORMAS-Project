package de.symeda.sormas.backend.externalmessage;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.user.User;

@Entity(name = ExternalMessage.TABLE_NAME)
@Audited
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class ExternalMessage extends AbstractDomainObject {

	public static final String TABLE_NAME = "externalmessage";

	public static final String SAMPLE_REPORTS = "sampleReports";

	public static final String TYPE = "type";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_EXTERNAL_IDS = "reporterExternalIds";
	public static final String REPORTER_POSTAL_CODE = "reporterPostalCode";
	public static final String REPORTER_CITY = "reporterCity";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_SEX = "personSex";
	public static final String PERSON_BIRTH_DATE_DD = "personBirthDateDD";
	public static final String PERSON_BIRTH_DATE_MM = "personBirthDateMM";
	public static final String PERSON_BIRTH_DATE_YYYY = "personBirthDateYYYY";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String PERSON_CITY = "personCity";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String REPORTER_MESSAGE_DETAILS = "externalMessageDetails";
	public static final String STATUS = "status";
	public static final String REPORT_ID = "reportId";
	public static final String ASSIGNEE = "assignee";
	public static final String CAZE = "caze";

	private ExternalMessageType type;
	private Disease testedDisease;
	private Date messageDateTime;

	private String reporterName;
	private List<String> reporterExternalIds;
	private String reporterPostalCode;
	private String reporterCity;

	private String personFirstName;
	private String personLastName;
	private Sex personSex;
	private PresentCondition personPresentCondition;
	private Integer personBirthDateDD;
	private Integer personBirthDateMM;
	private Integer personBirthDateYYYY;
	private String personPostalCode;
	private String personCity;
	private String personStreet;
	private String personHouseNumber;
	private String personPhone;
	private String personEmail;
	private String externalMessageDetails;
	//External messages related to each other should have the same reportId
	private String reportId;

	private ExternalMessageStatus status = ExternalMessageStatus.UNPROCESSED;
	private User assignee;

	private List<SampleReport> sampleReports;
	private Case caze;

	@Enumerated(EnumType.STRING)
	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String labName) {
		this.reporterName = labName;
	}

	@Type(type = "list-array")
	@Column(name = "reporterexternalids", columnDefinition = "VARCHAR(255) ARRAY")
	public List<String> getReporterExternalIds() {
		return reporterExternalIds;
	}

	public void setReporterExternalIds(List<String> reporterExternalIds) {
		this.reporterExternalIds = reporterExternalIds;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterPostalCode() {
		return reporterPostalCode;
	}

	public void setReporterPostalCode(String testLabPostalCode) {
		this.reporterPostalCode = testLabPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterCity() {
		return reporterCity;
	}

	public void setReporterCity(String labCity) {
		this.reporterCity = labCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	@Enumerated(EnumType.STRING)
	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	public PresentCondition getPersonPresentCondition() {
		return personPresentCondition;
	}

	public void setPersonPresentCondition(PresentCondition personPresentCondition) {
		this.personPresentCondition = personPresentCondition;
	}

	@Column(name = "personbirthdatedd")
	public Integer getPersonBirthDateDD() {
		return personBirthDateDD;
	}

	public void setPersonBirthDateDD(Integer personBirthDateDD) {
		this.personBirthDateDD = personBirthDateDD;
	}

	@Column(name = "personbirthdatemm")
	public Integer getPersonBirthDateMM() {
		return personBirthDateMM;
	}

	public void setPersonBirthDateMM(Integer personBirthDateMM) {
		this.personBirthDateMM = personBirthDateMM;
	}

	@Column(name = "personbirthdateyyyy")
	public Integer getPersonBirthDateYYYY() {
		return personBirthDateYYYY;
	}

	public void setPersonBirthDateYYYY(Integer personBirthDateYYYY) {
		this.personBirthDateYYYY = personBirthDateYYYY;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	@Column
	public String getExternalMessageDetails() {
		return externalMessageDetails;
	}

	public void setExternalMessageDetails(String labMessageDetails) {
		this.externalMessageDetails = labMessageDetails;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ExternalMessageStatus getStatus() {
		return status;
	}

	public void setStatus(ExternalMessageStatus status) {
		this.status = status;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@ManyToOne
	@JoinColumn
	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = SampleReport.LAB_MESSAGE, fetch = FetchType.LAZY)
	public List<SampleReport> getSampleReports() {
		return sampleReports;
	}

	public void setSampleReports(List<SampleReport> sampleReports) {
		this.sampleReports = sampleReports;
	}
}
