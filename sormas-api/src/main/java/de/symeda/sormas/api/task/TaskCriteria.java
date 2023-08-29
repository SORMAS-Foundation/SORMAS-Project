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

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class TaskCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -9174165215694877625L;

	public static final String FREE_TEXT = "freeText";
	public static final String ASSIGNEE_USER_LIKE = "assigneeUserLike";
	public static final String CREATOR_USER_LIKE = "creatorUserLike";
	public static final String ASSIGNED_BY_USER_LIKE = "assignedByUserLike";

	private TaskStatus taskStatus;
	private TaskContext taskContext;
	private String freeText;
	private TaskType taskType;
	private UserReferenceDto assigneeUser;
	private UserReferenceDto excludeAssigneeUser;
	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private PersonReferenceDto contactPerson;
	private EventReferenceDto event;
	private Date dueDateFrom;
	private Date dueDateTo;
	private Date startDateFrom;
	private Date startDateTo;
	private Date statusChangeDateFrom;
	private Date statusChangeDateTo;
	// Used to re-construct whether users have filtered by epi weeks or dates
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private EntityRelevanceStatus relevanceStatus;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String assigneeUserLike;
	private String creatorUserLike;
	private String assignedByUserLike;
	private TravelEntryReferenceDto travelEntry;
	private EnvironmentReferenceDto environment;
	private boolean excludeLimitedSyncRestrictions;

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskCriteria taskStatus(TaskStatus taskStatus) {
		setTaskStatus(taskStatus);
		return this;
	}

	public TaskCriteria taskType(TaskType taskType) {
		this.taskType = taskType;
		return this;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public TaskCriteria assigneeUser(UserReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
		return this;
	}

	public UserReferenceDto getAssigneeUser() {
		return assigneeUser;
	}

	public TaskCriteria excludeAssigneeUser(UserReferenceDto excludeAssigneeUser) {
		this.excludeAssigneeUser = excludeAssigneeUser;
		return this;
	}

	public UserReferenceDto getExcludeAssigneeUser() {
		return excludeAssigneeUser;
	}

	public TaskCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public TaskCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public TaskCriteria contactPerson(PersonReferenceDto contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public TaskCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}

	public PersonReferenceDto getContactPerson() {
		return contactPerson;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public boolean hasContextCriteria() {
		return getCaze() != null || getEvent() != null || getContact() != null || getTravelEntry() != null || getEnvironment() != null;
	}

	public TaskCriteria dueDateBetween(Date dueDateFrom, Date dueDateTo) {
		this.dueDateFrom = dueDateFrom;
		this.dueDateTo = dueDateTo;
		return this;
	}

	public TaskCriteria dueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
		return this;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public TaskCriteria dueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
		return this;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public TaskCriteria startDateBetween(Date startDateFrom, Date startDateTo) {
		this.startDateFrom = startDateFrom;
		this.startDateTo = startDateTo;
		return this;
	}

	public TaskCriteria startDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
		return this;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public TaskCriteria startDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
		return this;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public TaskCriteria statusChangeDateBetween(Date statusChangeDateFrom, Date statusChangeDateTo) {
		this.statusChangeDateFrom = statusChangeDateFrom;
		this.statusChangeDateTo = statusChangeDateTo;
		return this;
	}

	public TaskCriteria statusChangeDateFrom(Date statusChangeDateFrom) {
		this.statusChangeDateFrom = statusChangeDateFrom;
		return this;
	}

	public Date getStatusChangeDateFrom() {
		return statusChangeDateFrom;
	}

	public TaskCriteria statusChangeDateTo(Date statusChangeDateTo) {
		this.statusChangeDateTo = statusChangeDateTo;
		return this;
	}

	public Date getStatusChangeDateTo() {
		return statusChangeDateTo;
	}

	public TaskCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public TaskCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public TaskCriteria taskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public TaskCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public TaskCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public TaskCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public String getAssigneeUserLike() {
		return assigneeUserLike;
	}

	public void setAssigneeUserLike(String assigneeUserLike) {
		this.assigneeUserLike = assigneeUserLike;
	}

	public TaskCriteria assigneeUserLike(String assigneeUserLike) {
		this.assigneeUserLike = assigneeUserLike;
		return this;
	}

	public String getCreatorUserLike() {
		return creatorUserLike;
	}

	public void setCreatorUserLike(String creatorUserLike) {
		this.creatorUserLike = creatorUserLike;
	}

	public TaskCriteria creatorUserLike(String creatorUserLike) {
		this.creatorUserLike = creatorUserLike;
		return this;
	}

	public String getAssignedByUserLike() {
		return assignedByUserLike;
	}

	public void setAssignedByUserLike(String assignedByUserLike) {
		this.assignedByUserLike = assignedByUserLike;
	}

	public TaskCriteria assignedByUserLike(String assignedByUserLike) {
		this.assignedByUserLike = assignedByUserLike;
		return this;
	}

	public TravelEntryReferenceDto getTravelEntry() {
		return travelEntry;
	}

	public TaskCriteria travelEntry(TravelEntryReferenceDto travelEntry) {
		this.travelEntry = travelEntry;
		return this;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public TaskCriteria environment(EnvironmentReferenceDto environment) {
		this.environment = environment;
		return this;
	}

	/**
	 * Ignore user filter restrictions that would otherwise be applied by the limited synchronization feature.
	 * Necessary e.g. when retrieving UUIDs of tasks related to cases that are supposed to be removed from the
	 * mobile app, because otherwise the user filter would exclude those tasks.
	 */
	@IgnoreForUrl
	public boolean isExcludeLimitedSyncRestrictions() {
		return excludeLimitedSyncRestrictions;
	}

	public TaskCriteria excludeLimitedSyncRestrictions(boolean excludeLimitedSyncRestrictions) {
		this.excludeLimitedSyncRestrictions = excludeLimitedSyncRestrictions;
		return this;
	}
}
