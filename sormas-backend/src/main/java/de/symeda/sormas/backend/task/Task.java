/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.task;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity
@Audited
public class Task extends AbstractDomainObject {

	private static final long serialVersionUID = -4754578341242164661L;

	public static final String TABLE_NAME = "task";

	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String CREATOR_COMMENT = "creatorComment";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DUE_DATE = "dueDate";
	public static final String SUGGESTED_START = "suggestedStart";
	public static final String EVENT = "event";
	public static final String PERCEIVED_START = "perceivedStart";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TASK_CONTEXT = "taskContext";
	public static final String TASK_STATUS = "taskStatus";
	public static final String TASK_TYPE = "taskType";
	public static final String CLOSED_LAT = "closedLat";
	public static final String CLOSED_LON = "closedLon";

	public static final String LABCERTIFICATEGUID = "labCertificateGuid";
	public static final String PAYER_NUMBER = "payerNumber";
	public static final String DOCTOR_NUMBER = "doctorNumber";
	public static final String OPERATING_FACILITY_NUMBER = "operatingFacilityNumber";
	public static final String LAB_NUMBER = "labNumber";
	public static final String TEST_V = "testV";
	public static final String SELF_PAYING = "selfPaying";
	public static final String SPECIAL_AGREEMENT = "specialAgreement";
	public static final String FIRST_TEST = "firstTest";
	public static final String NEXT_TEST = "nextTest";
	public static final String CONTACT_PERSON = "contactPerson";
	public static final String CORONA_APP = "coronaApp";
	public static final String OUTBREAK = "outbreak";
	public static final String OUTBREAK_PREVENTION = "outbreakPrevention";
	public static final String WORKING_IN_FACILITY = "workingInFacility";
	public static final String LIVING_IN_FACILITY = "livingInFacility";
	public static final String MEDICAL_FACILITY = "medicalFacility";
	public static final String COM_FACILITY = "communityFacility";
	public static final String CARE_FACILITY = "careFacility";
	public static final String OTHER_FACILITY = "otherFacility";
	public static final String AGREED_TO_GDPR = "agreedToGdpr";
	public static final String SPECIAL_AGREEMENT_CODE = "specialAgreementCode";
	public static final String HEALTH_DEPARTMENT = "healthDepartment";

	private TaskContext taskContext;
	private Case caze;
	private Contact contact;
	private Event event;

	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;

	private User creatorUser;
	private String creatorComment;
	private User assigneeUser;
	private String assigneeReply;

	private Double closedLat;
	private Double closedLon;
	private Float closedLatLonAccuracy;

	private String labCertificateGuid;
	private String payerNumber;
	private String doctorNumber;
	private String operatingFacilityNumber;
	private String labNumber;
	private boolean testV;
	private boolean selfPaying;
	private boolean specialAgreement;
	private boolean firstTest;
	private boolean nextTest;
	private boolean contactPerson;
	private boolean coronaApp;
	private boolean outbreak;
	private boolean outbreakPrevention;
	private boolean workingInFacility;
	private boolean livingInFacility;
	private boolean medicalFacility;
	private boolean communityFacility;
	private boolean careFacility;
	private boolean otherFacility;
	private boolean agreedToGdpr;
	private String specialAgreementCode;
	private Facility healthDepartment;

	@Enumerated(EnumType.STRING)
	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	@ManyToOne(cascade = {})
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne(cascade = {})
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne(cascade = {})
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Enumerated(EnumType.STRING)
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Enumerated(EnumType.STRING)
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPerceivedStart() {
		return perceivedStart;
	}

	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}

	@ManyToOne(cascade = {})
	public User getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getCreatorComment() {
		return creatorComment;
	}

	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}

	@ManyToOne(cascade = {})
	public User getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(User assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getAssigneeReply() {
		return assigneeReply;
	}

	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}

	@Enumerated(EnumType.STRING)
	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSuggestedStart() {
		return suggestedStart;
	}

	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}

	public Double getClosedLat() {
		return closedLat;
	}

	public void setClosedLat(Double closedLat) {
		this.closedLat = closedLat;
	}

	public Double getClosedLon() {
		return closedLon;
	}

	public void setClosedLon(Double closedLon) {
		this.closedLon = closedLon;
	}

	public Float getClosedLatLonAccuracy() {
		return closedLatLonAccuracy;
	}

	public void setClosedLatLonAccuracy(Float closedLatLonAccuracy) {
		this.closedLatLonAccuracy = closedLatLonAccuracy;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getLabCertificateGuid() {
		return labCertificateGuid;
	}

	public void setLabCertificateGuid(String labCertificateGuid) {
		this.labCertificateGuid = labCertificateGuid;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPayerNumber() {
		return payerNumber;
	}

	public void setPayerNumber(String payerNumber) {
		this.payerNumber = payerNumber;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getDoctorNumber() {
		return doctorNumber;
	}

	public void setDoctorNumber(String doctorNumber) {
		this.doctorNumber = doctorNumber;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getOperatingFacilityNumber() {
		return operatingFacilityNumber;
	}

	public void setOperatingFacilityNumber(String operatingFacilityNumber) {
		this.operatingFacilityNumber = operatingFacilityNumber;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getLabNumber() {
		return labNumber;
	}

	public void setLabNumber(String labNumber) {
		this.labNumber = labNumber;
	}

	@Column(nullable = true)
	public boolean isTestV() {
		return testV;
	}

	public void setTestV(boolean testV) {
		this.testV = testV;
	}

	@Column
	public boolean isSelfPaying() {
		return selfPaying;
	}

	public void setSelfPaying(boolean selfPaying) {
		this.selfPaying = selfPaying;
	}

	@Column
	public boolean isSpecialAgreement() {
		return specialAgreement;
	}

	public void setSpecialAgreement(boolean specialAgreement) {
		this.specialAgreement = specialAgreement;
	}

	@Column
	public boolean isFirstTest() {
		return firstTest;
	}

	public void setFirstTest(boolean firstTest) {
		this.firstTest = firstTest;
	}

	@Column
	public boolean isNextTest() {
		return nextTest;
	}

	public void setNextTest(boolean nextTest) {
		this.nextTest = nextTest;
	}

	@Column
	public boolean isContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(boolean contactPerson) {
		this.contactPerson = contactPerson;
	}

	@Column
	public boolean isCoronaApp() {
		return coronaApp;
	}

	public void setCoronaApp(boolean coronaApp) {
		this.coronaApp = coronaApp;
	}

	@Column
	public boolean isOutbreak() {
		return outbreak;
	}

	public void setOutbreak(boolean outbreak) {
		this.outbreak = outbreak;
	}

	@Column
	public boolean isOutbreakPrevention() {
		return outbreakPrevention;
	}

	public void setOutbreakPrevention(boolean outbreakPrevention) {
		this.outbreakPrevention = outbreakPrevention;
	}

	@Column
	public boolean isWorkingInFacility() {
		return workingInFacility;
	}

	public void setWorkingInFacility(boolean workingInFacility) {
		this.workingInFacility = workingInFacility;
	}

	@Column
	public boolean isLivingInFacility() {
		return livingInFacility;
	}

	public void setLivingInFacility(boolean livingInFacility) {
		this.livingInFacility = livingInFacility;
	}

	@Column
	public boolean isMedicalFacility() {
		return medicalFacility;
	}

	public void setMedicalFacility(boolean medicalFacility) {
		this.medicalFacility = medicalFacility;
	}

	@Column
	public boolean isCommunityFacility() {
		return communityFacility;
	}

	public void setCommunityFacility(boolean communityFacility) {
		this.communityFacility = communityFacility;
	}

	@Column
	public boolean isCareFacility() {
		return careFacility;
	}

	public void setCareFacility(boolean careFacility) {
		this.careFacility = careFacility;
	}

	@Column
	public boolean isOtherFacility() {
		return otherFacility;
	}

	public void setOtherFacility(boolean otherFacility) {
		this.otherFacility = otherFacility;
	}

	@Column
	public boolean isAgreedToGdpr() {
		return agreedToGdpr;
	}

	public void setAgreedToGdpr(boolean agreedToGdpr) {
		this.agreedToGdpr = agreedToGdpr;
	}

	@Column
	public String getSpecialAgreementCode() {
		return specialAgreementCode;
	}

	public void setSpecialAgreementCode(String specialAgreementCode) {
		this.specialAgreementCode = specialAgreementCode;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = true)
	public Facility getHealthDepartment() {
		return healthDepartment;
	}

	public void setHealthDepartment(Facility healthDepartment) {
		this.healthDepartment = healthDepartment;
	}
}
