package de.symeda.sormas.api.selfreport;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SelfReportExportDto extends AbstractUuidDto {

	private static final long serialVersionUID = 1L;

	public static final String I18N_PREFIX = "SelfReportExport";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";

	public static final String TYPE = "type";
	public static final String REPORT_DATE = "reportDate";

	//TODO: define case reference caption
	public static final String CASE_REFERENCE = "caseReference";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String BIRTH_DATE = "birthdate";

	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String EMAIL = "email";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String ADDRESS = "address";
	public static final String COMMENT = "comment";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PROCESSING_STATUS = "processingStatus";

	// District.NAME,
	// Location.STREET,
	// Location.HOUSE_NUMBER,
	// Location.POSTAL_CODE,
	// Location.CITY,

	// selfReport.get(SelfReport.DELETION_REASON),
	// selfReport.get(SelfReport.OTHER_DELETION_REASON)),

	private SelfReportType type;
	private Date reportDate;
	private String caseReference;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private String firstName;
	private String lastName;
	private Sex sex;

	private String district;
	private String street;
	private String houseNumber;
	private String postalCode;
	private String city;

	private BirthDateDto birthDate;
	private String nationalHealthId;
	private String email;
	private String phoneNumber;
	private String comment;
	private UserReferenceDto responsibleUser;
	private SelfReportInvestigationStatus investigationStatus;
	private SelfReportProcessingStatus processingStatus;
	private DeletionReason deletionReason;
	private String otherDeletionReason;

	public SelfReportExportDto(
		String uuid,
		SelfReportType type,
		Date reportDate,
		String caseReference,
		Disease disease,
		DiseaseVariant diseaseVariant,
		String firstName,
		String lastName,
		Sex sex,

		String district,
		String street,
		String houseNumber,
		String postalCode,
		String city,

		BirthDateDto birthDate,
		String nationalHealthId,
		String email,
		String phoneNumber,
		String comment,
		UserReferenceDto responsibleUser,
		SelfReportInvestigationStatus investigationStatus,
		SelfReportProcessingStatus processingStatus,
		DeletionReason deletionReason,
		String otherDeletionReason) {

		super(uuid);
		this.type = type;
		this.reportDate = reportDate;
		this.caseReference = caseReference;
		this.disease = disease;
		this.diseaseVariant = diseaseVariant;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.district = district;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.birthDate = birthDate;
		this.nationalHealthId = nationalHealthId;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.comment = comment;
		this.responsibleUser = responsibleUser;
		this.investigationStatus = investigationStatus;
		this.processingStatus = processingStatus;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
	}

	@Order(0)
	//TODO: Add @ExportProperty, @ExportGroup
	public String getUuid() {
		return super.getUuid();
	}

	@Order(1)
	public SelfReportType getType() {
		return type;
	}

	public void setType(SelfReportType type) {
		this.type = type;
	}

	@Order(2)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Order(3)
	public String getCaseReference() {
		return caseReference;
	}

	public void setCaseReference(String caseReference) {
		this.caseReference = caseReference;
	}

	@Order(4)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Order(5)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@Order(6)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Order(7)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Order(8)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Order(9)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(10)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(11)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(12)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Order(13)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(14)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthdate) {
		this.birthDate = birthdate;
	}

	@Order(15)
	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	@Order(16)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Order(17)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Order(18)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Order(19)
	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Order(20)
	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@Order(21)
	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(SelfReportProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}

	@Order(22)
	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	@Order(23)
	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
