package de.symeda.sormas.api.selfreport;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SelfReportExportDto extends AbstractUuidDto {

	private static final long serialVersionUID = 1L;

	public static final String I18N_PREFIX = "SelfReportExport";
	public static final String BIRTH_DATE = "birthdate";

	private SelfReportType type;
	private Date reportDate;
	@SensitiveData
	private String caseReference;
	private Disease disease;
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	@PersonalData
	@SensitiveData
	private String firstName;
	@PersonalData
	@SensitiveData
	private String lastName;
	private Sex sex;
	@PersonalData
	@SensitiveData
	private String street;
	@PersonalData
	@SensitiveData
	private String houseNumber;
	@PersonalData
	@SensitiveData
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String city;
	@PersonalData
	@SensitiveData
	private BirthDateDto birthDate;
	@SensitiveData
	private String nationalHealthId;
	@SensitiveData
	private String email;
	@SensitiveData
	private String phoneNumber;
	private Date dateOfTest;
	private Date dateOfSymptoms;
	@SensitiveData
	private String workplace;
	private Date dateWorkplace;
	private Date isolationDate;
	private Date contactDate;
	@SensitiveData
	private String comment;
	private UserReferenceDto responsibleUser;
	private SelfReportInvestigationStatus investigationStatus;
	private SelfReportProcessingStatus processingStatus;
	private DeletionReason deletionReason;
	@SensitiveData
	private String otherDeletionReason;

	public SelfReportExportDto(
		String uuid,
		SelfReportType type,
		Date reportDate,
		String caseReference,
		Disease disease,
		String diseaseDetails,
		DiseaseVariant diseaseVariant,
		String diseaseVariantDetails,
		String firstName,
		String lastName,
		Sex sex,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		BirthDateDto birthDate,
		String nationalHealthId,
		String email,
		String phoneNumber,
		Date dateOfTest,
		Date dateOfSymptoms,
		String workplace,
		Date dateWorkplace,
		Date isolationDate,
		Date contactDate,
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
		this.diseaseDetails = diseaseDetails;
		this.diseaseVariant = diseaseVariant;
		this.diseaseVariantDetails = diseaseVariantDetails;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.birthDate = birthDate;
		this.nationalHealthId = nationalHealthId;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.dateOfTest = dateOfTest;
		this.dateOfSymptoms = dateOfSymptoms;
		this.workplace = workplace;
		this.dateWorkplace = dateWorkplace;
		this.isolationDate = isolationDate;
		this.contactDate = contactDate;
		this.comment = comment;
		this.responsibleUser = responsibleUser;
		this.investigationStatus = investigationStatus;
		this.processingStatus = processingStatus;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
	}

	@Order(0)
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
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Order(6)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@Order(7)
	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	@Order(8)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Order(9)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Order(10)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Order(11)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(12)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(13)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Order(14)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(15)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthDate) {
		this.birthDate = birthDate;
	}

	@Order(16)

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	@Order(17)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Order(18)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Order(19)
	public Date getDateOfTest() {
		return dateOfTest;
	}

	public void setDateOfTest(Date dateOfTest) {
		this.dateOfTest = dateOfTest;
	}

	@Order(20)
	public Date getDateOfSymptoms() {
		return dateOfSymptoms;
	}

	public void setDateOfSymptoms(Date dateOfSymptoms) {
		this.dateOfSymptoms = dateOfSymptoms;
	}

	@Order(21)
	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	@Order(22)
	public Date getDateWorkplace() {
		return dateWorkplace;
	}

	public void setDateWorkplace(Date dateWorkplace) {
		this.dateWorkplace = dateWorkplace;
	}

	@Order(23)
	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	@Order(24)
	public Date getContactDate() {
		return contactDate;
	}

	public void setContactDate(Date contactDate) {
		this.contactDate = contactDate;
	}

	@Order(25)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Order(26)
	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Order(27)
	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@Order(28)
	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(SelfReportProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}

	@Order(29)
	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	@Order(30)
	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
