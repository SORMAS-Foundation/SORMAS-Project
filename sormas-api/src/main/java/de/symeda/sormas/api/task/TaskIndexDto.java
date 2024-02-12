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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.EmptyValuePseudonymizer;

public class TaskIndexDto extends PseudonymizableIndexDto implements ITask {

	private static final long serialVersionUID = 2439546041916003653L;

	public static final String I18N_PREFIX = "Task";

	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String ASSIGNED_BY_USER = "assignedByUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String EVENT = "event";
	public static final String TRAVEL_ENTRY = "travelEntry";
	public static final String ENVIRONMENT = "environment";
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
	public static final String COMMUNITY = "community";
	public static final String FACILITY = "facility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String DISEASE = "disease";

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
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Pseudonymizer(EmptyValuePseudonymizer.class)
	private TravelEntryReferenceDto travelEntry;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Pseudonymizer(EmptyValuePseudonymizer.class)
	private EnvironmentReferenceDto environment;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private FacilityReferenceDto facility;
	private PointOfEntryReferenceDto pointOfEntry;

	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Disease disease;

	private UserReferenceDto creatorUser;
	private String creatorComment;
	private UserReferenceDto assigneeUser;
	private UserReferenceDto assignedByUser;
	private String assigneeReply;

	private TaskJurisdictionFlagsDto taskJurisdictionFlagsDto;

	//@formatter:off
	public TaskIndexDto(String uuid, TaskContext taskContext, String caseUuid, String caseFirstName, String caseLastName,
			String eventUuid, String eventTitle, Disease eventDisease, String eventDiseaseDetails, EventStatus eventStatus, EventInvestigationStatus eventInvestigationStatus, Date eventDate,
			String contactUuid, String contactFirstName, String contactLastName, String contactCaseUuid, String contactCaseFirstName, String contactCaseLastName,
			String travelEntryUuid, String travelEntryExternalId, String travelEntryFirstName, String travelEntryLastName, String environmentUuid, String environmentName,
			TaskType taskType, TaskPriority priority, Date dueDate, Date suggestedStart, TaskStatus taskStatus, Disease disease,
			String creatorUserUuid, String creatorUserFirstName, String creatorUserLastName, String creatorComment,
			String assigneeUserUuid, String assigneeUserFirstName, String assigneeUserLastName, String assigneeReply,
			String assignedByUserUuid, String assignedByUserFirstName, String assignedByUsedLastName,
			String regionUuid, String regionName, String districtUuid, String districtName, String communityUuid, String communityName,
			String facilityUuid, String facilityName, String pointOfEntryUuid, String pointOfEntryName,
			boolean isInJurisdiction, boolean isCaseInJurisdiction, boolean isContactInJurisdiction,  boolean isContactCaseInJurisdiction, 
			boolean isEventInJurisdiction, boolean isTravelEntryInJurisdiction, boolean isEnvironmentInJurisdiction) {
	//@formatter:on

		super(uuid);
		this.taskContext = taskContext;

		if (caseUuid != null) {
			this.caze = new CaseReferenceDto(caseUuid, caseFirstName, caseLastName);
		}

		if (eventUuid != null) {
			if (StringUtils.isNotBlank(eventTitle)) {
				this.event = new EventReferenceDto(eventUuid, StringUtils.capitalize(eventTitle));
			} else {
				this.event = new EventReferenceDto(eventUuid, eventDisease, eventDiseaseDetails, eventStatus, eventInvestigationStatus, eventDate);
			}
		}

		if (contactUuid != null) {
			this.contact = new ContactReferenceDto(contactUuid, contactFirstName, contactLastName, new CaseReferenceDto(contactCaseUuid, contactCaseFirstName, contactCaseLastName));
		}

		if (travelEntryUuid != null) {
			this.travelEntry = new TravelEntryReferenceDto(travelEntryUuid, travelEntryExternalId, travelEntryFirstName, travelEntryLastName);
		}

		if (environmentUuid != null) {
			this.environment = new EnvironmentReferenceDto(environmentUuid, environmentName);
		}

		this.taskType = taskType;
		this.priority = priority;
		this.dueDate = dueDate;
		this.suggestedStart = suggestedStart;
		this.taskStatus = taskStatus;
		this.disease = disease;
		this.creatorUser = new UserReferenceDto(creatorUserUuid, creatorUserFirstName, creatorUserLastName);
		this.creatorComment = creatorComment;
		this.assigneeUser = new UserReferenceDto(assigneeUserUuid, assigneeUserFirstName, assigneeUserLastName);
		this.assignedByUser = new UserReferenceDto(assignedByUserUuid, assignedByUserFirstName, assignedByUsedLastName);
		this.assigneeReply = assigneeReply;

		if (regionUuid != null) {
			this.region = new RegionReferenceDto(regionUuid, regionName, null);
		}
		if (districtUuid != null) {
			this.district = new DistrictReferenceDto(districtUuid, districtName, null);
		}
		if (communityUuid != null) {
			this.community = new CommunityReferenceDto(communityUuid, communityName, null);
		}
		if (facilityUuid != null) {
			this.facility = new FacilityReferenceDto(facilityUuid, facilityName, null);
		}
		if (pointOfEntryUuid != null) {
			this.pointOfEntry = new PointOfEntryReferenceDto(pointOfEntryUuid, pointOfEntryName, null, null);
		}

		this.taskJurisdictionFlagsDto = new TaskJurisdictionFlagsDto(
			isInJurisdiction,
			isCaseInJurisdiction,
			isContactInJurisdiction,
			isContactCaseInJurisdiction,
			isEventInJurisdiction,
			isTravelEntryInJurisdiction,
			isEnvironmentInJurisdiction);

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

	public TravelEntryReferenceDto getTravelEntry() {
		return travelEntry;
	}

	public void setTravelEntry(TravelEntryReferenceDto travelEntry) {
		this.travelEntry = travelEntry;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
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

	public UserReferenceDto getAssignedByUser() {
		return assignedByUser;
	}

	public void setAssignedByUser(UserReferenceDto assignedByUser) {
		this.assignedByUser = assignedByUser;
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
		case TRAVEL_ENTRY:
			return getTravelEntry();
		case ENVIRONMENT:
			return getEnvironment();
		case GENERAL:
			return null;
		default:
			throw new IndexOutOfBoundsException(taskContext.toString());
		}
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public FacilityReferenceDto getFacility() {
		return facility;
	}

	public void setFacility(FacilityReferenceDto facility) {
		this.facility = facility;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public TaskJurisdictionFlagsDto getTaskJurisdictionFlagsDto() {
		return taskJurisdictionFlagsDto;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
