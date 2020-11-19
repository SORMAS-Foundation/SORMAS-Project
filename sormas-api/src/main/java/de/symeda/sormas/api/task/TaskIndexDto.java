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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.EmptyValuePseudonymizer;

public class TaskIndexDto extends PseudonymizableIndexDto implements Serializable {

	private static final long serialVersionUID = 2439546041916003653L;

	public static final String I18N_PREFIX = "Task";

	public static final String UUID = "uuid";
	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String EVENT = "event";
	public static final String CREATOR_COMMENT = "creatorComment";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DUE_DATE = "dueDate";
	public static final String SUGGESTED_START = "suggestedStart";
	public static final String TASK_CONTEXT = "taskContext";
	public static final String TASK_STATUS = "taskStatus";
	public static final String TASK_TYPE = "taskType";
	public static final String CONTEXT_REFERENCE = "contextReference";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";

	private String uuid;
	private TaskContext taskContext;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Pseudonymizer(EmptyValuePseudonymizer.class)
	private CaseReferenceDto caze;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Pseudonymizer(EmptyValuePseudonymizer.class)
	private EventReferenceDto event;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Pseudonymizer(EmptyValuePseudonymizer.class)
	private ContactReferenceDto contact;
	private String region;
	private String district;

	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;

	private UserReferenceDto creatorUser;
	private String creatorComment;
	private UserReferenceDto assigneeUser;
	private String assigneeReply;

	private TaskJurisdictionDto jurisdiction;

	//@formatter:off
	public TaskIndexDto(String uuid, TaskContext taskContext, String caseUuid, String caseFirstName, String caseLastName,
			String eventUuid, String eventTitle, Disease eventDisease, String eventDiseaseDetails, EventStatus eventStatus, EventInvestigationStatus eventInvestigationStatus, Date eventDate,
			String contactUuid, String contactFirstName, String contactLastName, String contactCaseFirstName, String contactCaseLastName,
			TaskType taskType, TaskPriority priority, Date dueDate, Date suggestedStart, TaskStatus taskStatus,
			String creatorUserUuid, String creatorUserFirstName, String creatorUserLastName, String creatorComment,
			String assigneeUserUuid, String assigneeUserFirstName, String assigneeUserLastName, String assigneeReply,
			String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUuid, String caseHealthFacilityUuid, 
			String casePointOfEntryUuid, String contactReportingUserUuid, String contactRegionUuid, String contactDistrictUuid, String contactCommunityUuid,
			String contactCaseReportingUserUuid, String contactCaseRegionUuid, String contactCaseDistrictUuid, String contactCaseCommunityUuid, 
			String contactCaseHealthFacilityUuid, String contactCasePointOfEntryUuid, String eventReportingUserUuid, String eventOfficerUuid, String eventRegionUuid, 
			String eventDistrictUuid, String eventCommunityUuid, String region, String district) {
	//@formatter:on

		this.setUuid(uuid);
		this.taskContext = taskContext;

		CaseJurisdictionDto caseJurisdiction = null;
		if (caseUuid != null) {
			this.caze = new CaseReferenceDto(caseUuid, caseFirstName, caseLastName);
			caseJurisdiction = new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUuid,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		}

		EventJurisdictionDto eventJurisdiction = null;
		if (eventUuid != null) {
			eventJurisdiction =
				new EventJurisdictionDto(eventReportingUserUuid, eventOfficerUuid, eventRegionUuid, eventDistrictUuid, eventCommunityUuid);

			if (StringUtils.isNotBlank(eventTitle)) {
				this.event = new EventReferenceDto(eventUuid, StringUtils.capitalize(eventTitle));
			} else {
				this.event = new EventReferenceDto(eventUuid, eventDisease, eventDiseaseDetails, eventStatus, eventInvestigationStatus, eventDate);
			}
		}

		ContactJurisdictionDto contactJurisdiction = null;
		if (contactUuid != null) {
			this.contact = new ContactReferenceDto(contactUuid, contactFirstName, contactLastName, contactCaseFirstName, contactCaseLastName);

			CaseJurisdictionDto contactCaseJurisdiction = contactCaseReportingUserUuid == null
				? null
				: new CaseJurisdictionDto(
					contactCaseReportingUserUuid,
					contactCaseRegionUuid,
					contactCaseDistrictUuid,
					contactCaseCommunityUuid,
					contactCaseHealthFacilityUuid,
					contactCasePointOfEntryUuid);
			contactJurisdiction = new ContactJurisdictionDto(
				contactReportingUserUuid,
				contactRegionUuid,
				contactDistrictUuid,
				contactCommunityUuid,
				contactCaseJurisdiction);
		}

		this.taskType = taskType;
		this.priority = priority;
		this.dueDate = dueDate;
		this.suggestedStart = suggestedStart;
		this.taskStatus = taskStatus;
		this.creatorUser = new UserReferenceDto(creatorUserUuid, creatorUserFirstName, creatorUserLastName, null);
		this.creatorComment = creatorComment;
		this.assigneeUser = new UserReferenceDto(assigneeUserUuid, assigneeUserFirstName, assigneeUserLastName, null);
		this.assigneeReply = assigneeReply;
		this.district = district;
		this.region = region;

		this.jurisdiction = new TaskJurisdictionDto(creatorUserUuid, assigneeUserUuid, caseJurisdiction, contactJurisdiction, eventJurisdiction);
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public TaskJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
}
