package de.symeda.sormas.backend.externalmessage;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.disease.DiseaseVariantConverter;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity(name = ExternalMessage.TABLE_NAME)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class ExternalMessage extends AbstractDomainObject {

	public static final String TABLE_NAME = "externalmessage";

	public static final String SAMPLE_REPORTS = "sampleReports";

	public static final String TYPE = "type";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String CASE_REPORT_DATE = "caseReportDate";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_EXTERNAL_IDS = "reporterExternalIds";
	public static final String REPORTER_POSTAL_CODE = "reporterPostalCode";
	public static final String REPORTER_CITY = "reporterCity";
	public static final String PERSON_EXTERNAL_ID = "personExternalId";
	public static final String PERSON_NATIONAL_HEALTH_ID = "personNationalHealthId";
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
	public static final String PERSON_COUNTRY = "personCountry";
	public static final String PERSON_FACILITY = "personFacility";
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_PHONE_NUMBER_TYPE = "personPhoneNumberType";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String EXTERNAL_MESSAGE_DETAILS = "externalMessageDetails";
	public static final String STATUS = "status";
	public static final String REPORT_ID = "reportId";
	public static final String REPORT_MESSAGE_ID = "reportMessageId";
	public static final String ASSIGNEE = "assignee";
	public static final String SURVEILLANCE_REPORT = "surveillanceReport";
	public static final String TSV = "tsv";

	private ExternalMessageType type;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	private Date messageDateTime;

	private Date caseReportDate;
	private String reporterName;
	private List<String> reporterExternalIds;
	private String reporterPostalCode;
	private String reporterCity;

	private String personExternalId;
	private String personNationalHealthId;
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
	private Country personCountry;
	private Facility personFacility;
	private String personHouseNumber;
	private String personPhone;
	private PhoneNumberType personPhoneNumberType;
	private String personEmail;
	private String externalMessageDetails;
	//External messages related to each other should have the same reportId
	private String reportId;
	private String reportMessageId;

	private ExternalMessageStatus status = ExternalMessageStatus.UNPROCESSED;
	private User assignee;

	private List<SampleReport> sampleReports;
	private SurveillanceReport surveillanceReport;
	private String tsv;

	@Enumerated(EnumType.STRING)
	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column
	@Convert(converter = DiseaseVariantConverter.class)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
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
	public String getPersonExternalId() {
		return personExternalId;
	}

	public void setPersonExternalId(String personExternalId) {
		this.personExternalId = personExternalId;
	}

	public String getPersonNationalHealthId() {
		return personNationalHealthId;
	}

	public void setPersonNationalHealthId(String personNationalHealthId) {
		this.personNationalHealthId = personNationalHealthId;
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

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getPersonCountry() {
		return personCountry;
	}

	public void setPersonCountry(Country personCountry) {
		this.personCountry = personCountry;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getPersonFacility() {
		return personFacility;
	}

	public void setPersonFacility(Facility personFacility) {
		this.personFacility = personFacility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	@Enumerated(EnumType.STRING)
	public PhoneNumberType getPersonPhoneNumberType() {
		return personPhoneNumberType;
	}

	public void setPersonPhoneNumberType(PhoneNumberType personPhoneNumberType) {
		this.personPhoneNumberType = personPhoneNumberType;
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportMessageId() {
		return reportMessageId;
	}

	public void setReportMessageId(String reportMessageId) {
		this.reportMessageId = reportMessageId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public SurveillanceReport getSurveillanceReport() {
		return surveillanceReport;
	}

	public void setSurveillanceReport(SurveillanceReport surveillanceReport) {
		this.surveillanceReport = surveillanceReport;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = SampleReport.LAB_MESSAGE, fetch = FetchType.LAZY)
	public List<SampleReport> getSampleReports() {
		return sampleReports;
	}

	public void setSampleReports(List<SampleReport> sampleReports) {
		this.sampleReports = sampleReports;
	}

	@Column(insertable = false, updatable = false)
	public String getTsv() {
		return tsv;
	}

	public void setTsv(String tsv) {
		this.tsv = tsv;
	}
}
