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
package de.symeda.sormas.api.task;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Required;

public class TaskDto extends EntityDto {

	private static final long serialVersionUID = 2439546041916003653L;

	public static final String I18N_PREFIX = "Task";

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
	public static final String CONTEXT_REFERENCE = "contextReference";
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

	@Required
	private TaskContext taskContext;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private CaseReferenceDto caze;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private EventReferenceDto event;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private ContactReferenceDto contact;

	@Required
	private TaskType taskType;
	private TaskPriority priority;
	@Required
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;

	private UserReferenceDto creatorUser;
	private String creatorComment;
	@Required
	private UserReferenceDto assigneeUser;
	private String assigneeReply;

	private Double closedLat;
	private Double closedLon;
	private Float closedLatLonAccuracy;

	/* ALERI FIELDS */
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String labCertificateGuid;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String payerNumber;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String doctorNumber;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String operatingFacilityNumber;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String labNumber;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean testV;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean selfPaying;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean specialAgreement;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean firstTest;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean nextTest;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean contactPerson;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean coronaApp;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean outbreak;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean outbreakPrevention;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean workingInFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean livingInFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean medicalFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean communityFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean careFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean otherFacility;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean agreedToGdpr;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private String specialAgreementCode;

	private FacilityReferenceDto healthDepartment;

	public static TaskDto build(TaskContext context, ReferenceDto entityRef) {

		TaskDto task = new TaskDto();
		task.setUuid(DataHelper.createUuid());
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setTaskStatus(TaskStatus.PENDING);
		task.setPriority(TaskPriority.NORMAL);
		task.setTaskContext(context);
		switch (context) {
		case CASE:
			task.setCaze((CaseReferenceDto) entityRef);
			break;
		case CONTACT:
			task.setContact((ContactReferenceDto) entityRef);
			break;
		case EVENT:
			task.setEvent((EventReferenceDto) entityRef);
			break;
		case GENERAL:
			break;
		}
		return task;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public void setContact(ContactReferenceDto contact) {
		this.contact = contact;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getSuggestedStart() {
		return suggestedStart;
	}

	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public Date getPerceivedStart() {
		return perceivedStart;
	}

	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}

	public UserReferenceDto getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(UserReferenceDto creatorUser) {
		this.creatorUser = creatorUser;
	}

	public String getCreatorComment() {
		return creatorComment;
	}

	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}

	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	public String getAssigneeReply() {
		return assigneeReply;
	}

	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
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

	public ReferenceDto getContextReference() {

		switch (taskContext) {
		case CASE:
			return getCaze();
		case CONTACT:
			return getContact();
		case EVENT:
			return getEvent();
		case GENERAL:
			return null;
		default:
			throw new IndexOutOfBoundsException(taskContext.toString());
		}
	}

	public Float getClosedLatLonAccuracy() {
		return closedLatLonAccuracy;
	}

	public void setClosedLatLonAccuracy(Float closedLatLonAccuracy) {
		this.closedLatLonAccuracy = closedLatLonAccuracy;
	}
	
	public String getLabCertificateGuid() {
		return labCertificateGuid;
	}

	public void setLabCertificateGuid(String labCertificateGuid) {
		this.labCertificateGuid = labCertificateGuid;
	}

	public String getPayerNumber() {
		return payerNumber;
	}

	public void setPayerNumber(String payerNumber) {
		this.payerNumber = payerNumber;
	}

	public String getDoctorNumber() {
		return doctorNumber;
	}

	public void setDoctorNumber(String doctorNumber) {
		this.doctorNumber = doctorNumber;
	}

	public String getOperatingFacilityNumber() {
		return operatingFacilityNumber;
	}

	public void setOperatingFacilityNumber(String operatingFacilityNumber) {
		this.operatingFacilityNumber = operatingFacilityNumber;
	}

	public String getLabNumber() {
		return labNumber;
	}

	public void setLabNumber(String labNumber) {
		this.labNumber = labNumber;
	}

	public boolean isTestV() {
		return testV;
	}

	public void setTestV(boolean testV) {
		this.testV = testV;
	}

	public boolean isSelfPaying() {
		return selfPaying;
	}

	public void setSelfPaying(boolean selfPaying) {
		this.selfPaying = selfPaying;
	}

	public boolean isSpecialAgreement() {
		return specialAgreement;
	}

	public void setSpecialAgreement(boolean specialAgreement) {
		this.specialAgreement = specialAgreement;
	}

	public boolean isFirstTest() {
		return firstTest;
	}

	public void setFirstTest(boolean firstTest) {
		this.firstTest = firstTest;
	}

	public boolean isNextTest() {
		return nextTest;
	}

	public void setNextTest(boolean nextTest) {
		this.nextTest = nextTest;
	}

	public boolean isContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(boolean contactPerson) {
		this.contactPerson = contactPerson;
	}

	public boolean isCoronaApp() {
		return coronaApp;
	}

	public void setCoronaApp(boolean coronaApp) {
		this.coronaApp = coronaApp;
	}

	public boolean isOutbreak() {
		return outbreak;
	}

	public void setOutbreak(boolean outbreak) {
		this.outbreak = outbreak;
	}

	public boolean isOutbreakPrevention() {
		return outbreakPrevention;
	}

	public void setOutbreakPrevention(boolean outbreakPrevention) {
		this.outbreakPrevention = outbreakPrevention;
	}

	public boolean isWorkingInFacility() {
		return workingInFacility;
	}

	public void setWorkingInFacility(boolean workingInFacility) {
		this.workingInFacility = workingInFacility;
	}

	public boolean isLivingInFacility() {
		return livingInFacility;
	}

	public void setLivingInFacility(boolean livingInFacility) {
		this.livingInFacility = livingInFacility;
	}

	public boolean isMedicalFacility() {
		return medicalFacility;
	}

	public void setMedicalFacility(boolean medicalFacility) {
		this.medicalFacility = medicalFacility;
	}

	public boolean isCommunityFacility() {
		return communityFacility;
	}

	public void setCommunityFacility(boolean communityFacility) {
		this.communityFacility = communityFacility;
	}

	public boolean isCareFacility() {
		return careFacility;
	}

	public void setCareFacility(boolean careFacility) {
		this.careFacility = careFacility;
	}

	public boolean isOtherFacility() {
		return otherFacility;
	}

	public void setOtherFacility(boolean otherFacility) {
		this.otherFacility = otherFacility;
	}

	public boolean isAgreedToGdpr() {
		return agreedToGdpr;
	}

	public void setAgreedToGdpr(boolean agreedToGdpr) {
		this.agreedToGdpr = agreedToGdpr;
	}

	public String getSpecialAgreementCode() {
		return specialAgreementCode;
	}

	public void setSpecialAgreementCode(String specialAgreementCode) {
		this.specialAgreementCode = specialAgreementCode;
	}

	public FacilityReferenceDto getHealthDepartment() {
		return healthDepartment;
	}

	public void setHealthDepartment(FacilityReferenceDto healthDepartment) {
		this.healthDepartment = healthDepartment;
	}
}
