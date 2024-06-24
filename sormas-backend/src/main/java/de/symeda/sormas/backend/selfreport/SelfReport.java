/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.selfreport;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportInvestigationStatus;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.disease.DiseaseVariantConverter;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;

@Entity(name = "selfreports")
public class SelfReport extends CoreAdo {

	private static final long serialVersionUID = 6676716702984236618L;

	public static final String TYPE = "type";
	public static final String REPORT_DATE = "reportDate";
	public static final String CASE_REFERENCE = "caseReference";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String EMAIL = "email";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String ADDRESS = "address";
	public static final String DATE_OF_TEST = "dateOfTest";
	public static final String DATE_OF_SYMPTOMS = "dateOfSymptoms";
	public static final String WORKPLACE = "workplace";
	public static final String DATE_WORKPLACE = "dateWorkplace";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String CONTACT_DATE = "contactDate";
	public static final String COMMENT = "comment";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PROCESSING_STATUS = "processingStatus";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String RESULTING_CONTACT = "resultingContact";

	private SelfReportType type;
	private Date reportDate;
	private String caseReference;
	private Disease disease;
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	// person data
	private String firstName;
	private String lastName;
	private Sex sex;
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;
	private String nationalHealthId;
	private String email;
	private String phoneNumber;
	private Location address;
	// end person data
	private Date dateOfTest;
	private Date dateOfSymptoms;
	private String workplace;
	private Date dateWorkplace;
	private Date isolationDate;
	private Date contactDate;
	private String comment;
	private User responsibleUser;
	private SelfReportInvestigationStatus investigationStatus;
	private SelfReportProcessingStatus processingStatus;

	private Case resultingCase;
	private Contact resultingContact;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SelfReportType getType() {
		return type;
	}

	public void setType(SelfReportType type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getCaseReference() {
		return caseReference;
	}

	public void setCaseReference(String caseReference) {
		this.caseReference = caseReference;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Column
	@Convert(converter = DiseaseVariantConverter.class)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@Column(nullable = false, length = CHARACTER_LIMIT_DEFAULT)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = false, length = CHARACTER_LIMIT_DEFAULT)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Enumerated(EnumType.STRING)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Column
	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	@Column
	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	@Column
	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfTest() {
		return dateOfTest;
	}

	public void setDateOfTest(Date dateOfTest) {
		this.dateOfTest = dateOfTest;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfSymptoms() {
		return dateOfSymptoms;
	}

	public void setDateOfSymptoms(Date dateOfSymptoms) {
		this.dateOfSymptoms = dateOfSymptoms;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateWorkplace() {
		return dateWorkplace;
	}

	public void setDateWorkplace(Date dateWorkplace) {
		this.dateWorkplace = dateWorkplace;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getContactDate() {
		return contactDate;
	}

	public void setContactDate(Date contactDate) {
		this.contactDate = contactDate;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(User responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(SelfReportProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Case getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(Case resultingCaze) {
		this.resultingCase = resultingCaze;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Contact getResultingContact() {
		return resultingContact;
	}

	public void setResultingContact(Contact resultingContact) {
		this.resultingContact = resultingContact;
	}
}
