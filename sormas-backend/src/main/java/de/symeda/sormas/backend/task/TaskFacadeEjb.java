/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskExportDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskJurisdictionFlagsDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.CronService;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationJoins;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "TaskFacade")
@RightsAllowed(UserRight._TASK_VIEW)
public class TaskFacadeEjb implements TaskFacade {

	private static final int ARCHIVE_BATCH_SIZE = 1000;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private TaskService taskService;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal travelEntryFacade;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private NotificationService notificationService;

	public Task fillOrBuildEntity(TaskDto source, Task target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Task::new, checkChangeDate);

		target.setAssigneeUser(userService.getByReferenceDto(source.getAssigneeUser()));
		target.setAssignedByUser(userService.getByReferenceDto(source.getAssignedByUser()));
		target.setAssigneeReply(source.getAssigneeReply());
		target.setCreatorUser(userService.getByReferenceDto(source.getCreatorUser()));
		target.setCreatorComment(source.getCreatorComment());
		if (source.getObserverUsers() != null) {
			List<User> observerUsers = userService.getByReferenceDtos(source.getObserverUsers());
			target.setObserverUsers(observerUsers);
		}
		target.setPriority(source.getPriority());
		target.setDueDate(source.getDueDate());
		target.setSuggestedStart(source.getSuggestedStart());
		target.setPerceivedStart(source.getPerceivedStart());
		// TODO is this a good place to do this?
		if (target.getTaskStatus() != source.getTaskStatus()) {
			target.setStatusChangeDate(new Date());
		} else {
			target.setStatusChangeDate(source.getStatusChangeDate());
		}
		target.setTaskStatus(source.getTaskStatus());
		target.setTaskType(source.getTaskType());

		target.setClosedLat(source.getClosedLat());
		target.setClosedLon(source.getClosedLon());
		target.setClosedLatLonAccuracy(source.getClosedLatLonAccuracy());

		target.setTaskContext(source.getTaskContext());
		if (source.getTaskContext() != null) {
			switch (source.getTaskContext()) {
			case CASE:
				target.setCaze(caseService.getByReferenceDto(source.getCaze()));
				target.setContact(null);
				target.setEvent(null);
				target.setTravelEntry(null);
				target.setEnvironment(null);
				break;
			case CONTACT:
				target.setCaze(null);
				target.setContact(contactService.getByReferenceDto(source.getContact()));
				target.setEvent(null);
				target.setTravelEntry(null);
				target.setEnvironment(null);
				break;
			case EVENT:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(eventService.getByReferenceDto(source.getEvent()));
				target.setTravelEntry(null);
				target.setEnvironment(null);
				break;
			case TRAVEL_ENTRY:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(null);
				target.setTravelEntry(travelEntryService.getByReferenceDto(source.getTravelEntry()));
				target.setEnvironment(null);
				break;
			case ENVIRONMENT:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(null);
				target.setTravelEntry(null);
				target.setEnvironment(environmentService.getByReferenceDto(source.getEnvironment()));
				break;
			case GENERAL:
				target.setCaze(null);
				target.setContact(null);
				target.setEvent(null);
				target.setTravelEntry(null);
				target.setEnvironment(null);
				break;
			default:
				throw new UnsupportedOperationException(source.getTaskContext() + " is not implemented");
			}
		} else {
			target.setCaze(null);
			target.setContact(null);
			target.setEvent(null);
		}

		return target;
	}

	public TaskDto toDto(Task source, Pseudonymizer pseudonymizer) {

		if (source == null) {
			return null;
		}

		return toDto(source, pseudonymizer, taskService.getJurisdictionFlags(source));
	}

	private TaskDto toDto(Task source, Pseudonymizer pseudonymizer, TaskJurisdictionFlagsDto jurisdictionFlags) {

		TaskDto target = new TaskDto();

		DtoHelper.fillDto(target, source);

		target.setAssigneeUser(UserFacadeEjb.toReferenceDto(source.getAssigneeUser()));
		target.setAssignedByUser(UserFacadeEjb.toReferenceDto(source.getAssignedByUser()));
		target.setAssigneeReply(source.getAssigneeReply());
		target.setCreatorUser(UserFacadeEjb.toReferenceDto(source.getCreatorUser()));
		target.setCreatorComment(source.getCreatorComment());
		if (source.getObserverUsers() != null) {
			target.setObserverUsers(source.getObserverUsers().stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toSet()));
		}
		target.setPriority(source.getPriority());
		target.setDueDate(source.getDueDate());
		target.setSuggestedStart(source.getSuggestedStart());
		target.setPerceivedStart(source.getPerceivedStart());
		target.setStatusChangeDate(source.getStatusChangeDate());
		target.setTaskContext(source.getTaskContext());
		target.setTaskStatus(source.getTaskStatus());
		target.setTaskType(source.getTaskType());
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setContact(ContactFacadeEjb.toReferenceDto(source.getContact()));
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		if (source.getTravelEntry() != null) {
			target.setTravelEntry(travelEntryFacade.toRefDto(source.getTravelEntry()));
		} else {
			target.setTravelEntry(null);
		}
		target.setEnvironment(EnvironmentFacadeEjb.toReferenceDto(source.getEnvironment()));

		target.setClosedLat(source.getClosedLat());
		target.setClosedLon(source.getClosedLon());
		target.setClosedLatLonAccuracy(source.getClosedLatLonAccuracy());

		pseudonymizer.pseudonymizeDto(TaskDto.class, target, jurisdictionFlags.getInJurisdiction(), t -> {
			if (source.getCaze() != null) {
				pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, target.getCaze(), jurisdictionFlags.getCaseInJurisdiction(), null);
			}

			if (source.getContact() != null) {
				pseudonymizeContactReference(
					pseudonymizer,
					target.getContact(),
					jurisdictionFlags.getContactInJurisdiction(),
					jurisdictionFlags.getContactCaseInJurisdiction());
			}

			if (source.getEvent() != null) {
				pseudonymizer.pseudonymizeDto(EventReferenceDto.class, target.getEvent(), jurisdictionFlags.getEventInJurisdiction(), null);
			}

			if (source.getTravelEntry() != null) {
				pseudonymizer
					.pseudonymizeDto(TravelEntryReferenceDto.class, target.getTravelEntry(), jurisdictionFlags.getTravelEntryInJurisdiction(), null);
			}
		});

		return target;
	}

	private List<TaskDto> toPseudonymizedDtos(List<Task> entities) {

		Map<Long, TaskJurisdictionFlagsDto> jurisdictionFlags = taskService.getJurisdictionsFlags(entities);
		Pseudonymizer pseudonymizer = createPseudonymizer();
		List<TaskDto> dtos = entities.stream().map(p -> toDto(p, pseudonymizer, jurisdictionFlags.get(p.getId()))).collect(Collectors.toList());
		return dtos;
	}

	private Pseudonymizer createPseudonymizer() {
		return Pseudonymizer.getDefault(userService::hasRight);
	}

	@Override
	@RightsAllowed({
		UserRight._TASK_CREATE,
		UserRight._TASK_EDIT })
	public TaskDto saveTask(@Valid TaskDto dto) {
		Task existingTask = taskService.getByUuid(dto.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingTask, userService, UserRight.TASK_CREATE, UserRight.TASK_EDIT);

		// Let's retrieve the old assignee before updating the task
		User oldAssignee = existingTask != null ? existingTask.getAssigneeUser() : null;

		Task ado = fillOrBuildEntity(dto, existingTask, true);

		validate(dto);

		taskService.ensurePersisted(ado);

		User newAssignee = dto.getAssigneeUser() != null ? userService.getByUuid(dto.getAssigneeUser().getUuid()) : null;

		notifyAboutNewAssignee(ado, newAssignee, oldAssignee);

		// once we have to handle additional logic this should be moved to it's own function or even class
		if (ado.getTaskType() == TaskType.CASE_INVESTIGATION && ado.getCaze() != null) {
			caseFacade.updateInvestigationByTask(ado.getCaze());
		}

		if (ado.getTaskType() == TaskType.CONTACT_FOLLOW_UP && ado.getTaskStatus() == TaskStatus.DONE && ado.getContact() != null) {
			try {
				String message = String.format(
					I18nProperties.getString(MessageContents.CONTENT_VISIT_COMPLETED),
					DataHelper.getShortUuid(ado.getContact().getUuid()),
					DataHelper.getShortUuid(ado.getAssigneeUser().getUuid()));

				notificationService.sendNotifications(
					NotificationType.CONTACT_VISIT_COMPLETED,
					JurisdictionHelper.getContactRegions(ado.getContact()),
					ado.getObserverUsers(),
					MessageSubject.VISIT_COMPLETED,
					message);
			} catch (NotificationDeliveryFailedException e) {
				logger.error("NotificationDeliveryFailedException when trying to notify supervisors about the completion of a follow-up visit.");
			}
		}

		return toDto(ado, createPseudonymizer());
	}

	@Override
	@RightsAllowed(UserRight._TASK_EDIT)
	public List<ProcessedEntity> saveBulkTasks(
		List<String> taskUuidList,
		TaskDto updatedTempTask,
		boolean priorityChange,
		boolean assigneeChange,
		boolean taskStatusChange) {

		List<ProcessedEntity> processedTasks = new ArrayList<>();
		UserReferenceDto currentUser = userService.getCurrentUser().toReference();

		for (String taskUuid : taskUuidList) {

			try {
				Task task = taskService.getByUuid(taskUuid);
				TaskDto taskDto = toDto(task, createPseudonymizer());

				if (priorityChange) {
					taskDto.setPriority(updatedTempTask.getPriority());
				}
				if (assigneeChange) {
					taskDto.setAssigneeUser(updatedTempTask.getAssigneeUser());
					taskDto.setAssignedByUser(currentUser);
				}
				if (taskStatusChange) {
					taskDto.setTaskStatus(updatedTempTask.getTaskStatus());
				}

				saveTask(taskDto);
				processedTasks.add(new ProcessedEntity(taskUuid, ProcessedEntityStatus.SUCCESS));
			} catch (Exception e) {
				processedTasks.add(new ProcessedEntity(taskUuid, ProcessedEntityStatus.INTERNAL_FAILURE));
				logger.error("The task with uuid {} could not be saved due to an Exception", taskUuid, e);
			}
		}
		return processedTasks;
	}

	private void notifyAboutNewAssignee(Task task, User newAssignee, User oldAssignee) {
		// oldAssignee == null => it means it's a new task, this notification should only be sent in case the assignee is changed
		if (oldAssignee == null || Objects.equals(newAssignee, oldAssignee)) {
			return;
		}

		try {
			final TaskContext context = task.getTaskContext();
			final AbstractDomainObject associatedEntity = context == TaskContext.CASE
				? task.getCaze()
				: context == TaskContext.CONTACT ? task.getContact() : context == TaskContext.EVENT ? task.getEvent() : null;

			Map<User, String> userMessages = new HashMap<>();
			userMessages.put(
				oldAssignee,
				getTaskNotificationMessage(
					task,
					associatedEntity,
					MessageContents.CONTENT_TASK_GENERAL_UPDATED_ASSIGNEE_SOURCE,
					MessageContents.CONTENT_TASK_SPECIFIC_UPDATED_ASSIGNEE_SOURCE));
			if (newAssignee != null) {
				userMessages.put(
					newAssignee,
					getTaskNotificationMessage(
						task,
						associatedEntity,
						MessageContents.CONTENT_TASK_GENERAL_UPDATED_ASSIGNEE_TARGET,
						MessageContents.CONTENT_TASK_SPECIFIC_UPDATED_ASSIGNEE_TARGET));
			}
			notificationService.sendNotifications(NotificationType.TASK_UPDATED_ASSIGNEE, MessageSubject.TASK_UPDATED_ASSIGNEE, () -> userMessages);
		} catch (NotificationDeliveryFailedException e) {
			logger.error(String.format("EmailDeliveryFailedException when trying to notify a user about an updated task assignee."));
		}
	}

	private String getTaskNotificationMessage(Task task, AbstractDomainObject associatedEntity, String generalMessageTag, String specificMessageTag) {
		TaskContext context = task.getTaskContext();

		return context == TaskContext.GENERAL
			? String.format(I18nProperties.getString(generalMessageTag), task.getTaskType().toString())
			: buildSpecificTaskMessage(specificMessageTag, task.getTaskType(), context, associatedEntity);
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return taskService.getAllActiveUuids(user);
	}

	@Override
	public List<TaskDto> getAllActiveTasksAfter(Date date) {
		return getAllActiveTasksAfter(date, null, null);
	}

	@Override
	public List<TaskDto> getAllActiveTasksAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(taskService.getAllAfter(date, batchSize, lastSynchronizedUuid));
	}

	@Override
	public Page<TaskIndexDto> getIndexPage(TaskCriteria taskCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<TaskIndexDto> taskIndexList = getIndexList(taskCriteria, offset, size, sortProperties);
		long totalElementCount = count(taskCriteria);
		return new Page<TaskIndexDto>(taskIndexList, offset, size, totalElementCount);
	}

	@Override
	public long count(TaskCriteria taskCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> task = cq.from(Task.class);
		TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, task);
		TaskJoins joins = taskQueryContext.getJoins();

		Predicate filter = taskService.createUserFilter(taskQueryContext, taskCriteria);

		if (taskCriteria != null) {
			Predicate criteriaFilter = taskService.buildCriteriaFilter(taskCriteria, taskQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(task));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<TaskIndexDto> getIndexList(TaskCriteria taskCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(taskCriteria, first, max, sortProperties);

		List<TaskIndexDto> tasks = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TaskIndexDto> cq = cb.createQuery(TaskIndexDto.class);
			Root<Task> task = cq.from(Task.class);

			TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, task);
			TaskJoins joins = taskQueryContext.getJoins();

			// Filter select based on case/contact/event region/district/community and case facility/point of entry
			Expression<String> regionUuid = taskQueryContext.getRegionExpressionForIndex(Region.UUID);
			Expression<String> regionName = taskQueryContext.getRegionNameForIndex();
			Expression<String> districtUuid = taskQueryContext.getDistrictExpressionForIndex(District.UUID);
			Expression<String> districtName = taskQueryContext.getDistrictNameForIndex();
			Expression<String> communityUuid = taskQueryContext.getCommunityExpressionForIndex(Community.UUID);
			Expression<String> communityName = taskQueryContext.getCommunityExpressionForIndex(Community.NAME);

			List<Selection<?>> selections = new ArrayList<>(
				Arrays.asList(
					task.get(Task.UUID),
					task.get(Task.TASK_CONTEXT),
					joins.getCaze().get(Case.UUID),
					joins.getCasePerson().get(Person.FIRST_NAME),
					joins.getCasePerson().get(Person.LAST_NAME),
					joins.getEvent().get(Event.UUID),
					joins.getEvent().get(Event.EVENT_TITLE),
					joins.getEvent().get(Event.DISEASE),
					joins.getEvent().get(Event.DISEASE_DETAILS),
					joins.getEvent().get(Event.EVENT_STATUS),
					joins.getEvent().get(Event.EVENT_INVESTIGATION_STATUS),
					joins.getEvent().get(Event.START_DATE),
					joins.getContact().get(Contact.UUID),
					joins.getContactPerson().get(Person.FIRST_NAME),
					joins.getContactPerson().get(Person.LAST_NAME),
					joins.getContactCasePerson().get(Person.FIRST_NAME),
					joins.getContactCasePerson().get(Person.LAST_NAME),
					joins.getTravelEntry().get(TravelEntry.UUID),
					joins.getTravelEntry().get(TravelEntry.EXTERNAL_ID),
					joins.getTravelEntryPerson().get(Person.FIRST_NAME),
					joins.getTravelEntryPerson().get(Person.LAST_NAME),
					joins.getEnvironment().get(Environment.UUID),
					joins.getEnvironment().get(Environment.ENVIRONMENT_NAME),
					task.get(Task.TASK_TYPE),
					task.get(Task.PRIORITY),
					task.get(Task.DUE_DATE),
					task.get(Task.SUGGESTED_START),
					task.get(Task.TASK_STATUS),
					cb.selectCase()
						.when(cb.isNotNull(joins.getCaze()), joins.getCaze().get(Case.DISEASE))
						.when(cb.isNotNull(joins.getContact()), joins.getContact().get(Contact.DISEASE))
						.when(cb.isNotNull(joins.getEvent()), joins.getEvent().get(Event.DISEASE))
						.otherwise(joins.getTravelEntry().get(TravelEntry.DISEASE)),
					joins.getCreator().get(User.UUID),
					joins.getCreator().get(User.FIRST_NAME),
					joins.getCreator().get(User.LAST_NAME),
					task.get(Task.CREATOR_COMMENT),
					joins.getAssignee().get(User.UUID),
					joins.getAssignee().get(User.FIRST_NAME),
					joins.getAssignee().get(User.LAST_NAME),
					task.get(Task.ASSIGNEE_REPLY),
					joins.getAssignedBy().get(User.UUID),
					joins.getAssignedBy().get(User.FIRST_NAME),
					joins.getAssignedBy().get(User.LAST_NAME),
					regionUuid,
					regionName,
					districtUuid,
					districtName,
					communityUuid,
					communityName,
					joins.getCaseFacility().get(Facility.UUID),
					joins.getCaseFacility().get(Facility.NAME),
					joins.getCasePointOfEntry().get(PointOfEntry.UUID),
					joins.getCasePointOfEntry().get(PointOfEntry.NAME)));

			selections.addAll(taskService.getJurisdictionSelections(taskQueryContext));
			cq.multiselect(selections);

			cq.where(task.get(Task.ID).in(batchedIds));
			cq.orderBy(getOrderList(sortProperties, taskQueryContext));
			cq.distinct(true);

			tasks.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		if (!tasks.isEmpty()) {
			List<String> assigneeUserUuids = tasks.stream().map(t -> t.getAssigneeUser().getUuid()).collect(Collectors.toList());
			Map<String, Long> pendingTaskCounts = getPendingTaskCountPerUser(assigneeUserUuids);

			for (TaskIndexDto singleTask : tasks) {
				// Workaround for Vaadin renderers not having access to their row reference; we therefore update the caption
				// directly instead of storing the task count in TaskIndexDto
				UserReferenceDto assigneeUser = singleTask.getAssigneeUser();
				Long taskCount = pendingTaskCounts.get(assigneeUser.getUuid());
				assigneeUser.setCaption(assigneeUser.getCaption() + " (" + (taskCount != null ? taskCount.toString() : "") + ")");
			}

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			Pseudonymizer emptyValuePseudonymizer = createPseudonymizer();
			pseudonymizer
				.pseudonymizeDtoCollection(TaskIndexDto.class, tasks, t -> t.getTaskJurisdictionFlagsDto().getInJurisdiction(), (t, ignored) -> {
					final TaskJurisdictionFlagsDto taskJurisdictionFlagsDto = t.getTaskJurisdictionFlagsDto();
					if (t.getCaze() != null) {
						emptyValuePseudonymizer
							.pseudonymizeDto(CaseReferenceDto.class, t.getCaze(), taskJurisdictionFlagsDto.getCaseInJurisdiction(), null);
					}

					if (t.getContact() != null) {
						pseudonymizeContactReference(
							emptyValuePseudonymizer,
							t.getContact(),
							taskJurisdictionFlagsDto.getContactInJurisdiction(),
							taskJurisdictionFlagsDto.getContactCaseInJurisdiction());
					}

					if (t.getEvent() != null) {
						emptyValuePseudonymizer
							.pseudonymizeDto(EventReferenceDto.class, t.getEvent(), taskJurisdictionFlagsDto.getEventInJurisdiction(), null);
					}

					if (t.getTravelEntry() != null) {
						emptyValuePseudonymizer.pseudonymizeDto(
							TravelEntryReferenceDto.class,
							t.getTravelEntry(),
							taskJurisdictionFlagsDto.getTravelEntryInJurisdiction(),
							null);
					}

					if (t.getEnvironment() != null) {
						emptyValuePseudonymizer.pseudonymizeDto(
							EnvironmentReferenceDto.class,
							t.getEnvironment(),
							taskJurisdictionFlagsDto.getEventInJurisdiction(),
							null);
					}
				}, true);
		}

		return tasks;
	}

	private List<Long> getIndexListIds(TaskCriteria taskCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Task> task = cq.from(Task.class);

		TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, task);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(task.get(Task.ID));

		List<Order> orderList = getOrderList(sortProperties, taskQueryContext);
		List<Expression<?>> sortColumns = orderList.stream().map(Order::getExpression).collect(Collectors.toList());
		selections.addAll(sortColumns);

		cq.multiselect(selections);

		Predicate filter = taskService.createUserFilter(taskQueryContext, taskCriteria);

		if (taskCriteria != null) {
			Predicate criteriaFilter = taskService.buildCriteriaFilter(taskCriteria, taskQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, TaskQueryContext taskQueryContext) {
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		From<?, Task> taskRoot = taskQueryContext.getRoot();
		TaskJoins joins = taskQueryContext.getJoins();

		List<Order> orderList = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case TaskIndexDto.UUID:
				case TaskIndexDto.ASSIGNEE_REPLY:
				case TaskIndexDto.CREATOR_COMMENT:
				case TaskIndexDto.PRIORITY:
				case TaskIndexDto.DUE_DATE:
				case TaskIndexDto.SUGGESTED_START:
				case TaskIndexDto.TASK_CONTEXT:
				case TaskIndexDto.TASK_STATUS:
				case TaskIndexDto.TASK_TYPE:
					expression = taskRoot.get(sortProperty.propertyName);
					break;
				case TaskIndexDto.ASSIGNEE_USER:
					expression = joins.getAssignee().get(User.LAST_NAME);
					orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getAssignee().get(User.FIRST_NAME);
					break;
				case TaskIndexDto.CREATOR_USER:
					expression = joins.getCreator().get(User.LAST_NAME);
					orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getCreator().get(User.FIRST_NAME);
					break;
				case TaskIndexDto.ASSIGNED_BY_USER:
					expression = joins.getAssignedBy().get(User.LAST_NAME);
					orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getAssignedBy().get(User.FIRST_NAME);
					break;
				case TaskIndexDto.CAZE:
					expression = joins.getCasePerson().get(Person.LAST_NAME);
					orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getCasePerson().get(Person.FIRST_NAME);
					break;
				case TaskIndexDto.CONTACT:
					expression = joins.getContactPerson().get(Person.LAST_NAME);
					orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getContactPerson().get(Person.FIRST_NAME);
					break;
				case TaskIndexDto.EVENT:
					expression = joins.getEvent().get(Event.START_DATE);
					break;
				case TaskIndexDto.DISTRICT:
					expression = taskQueryContext.getDistrictNameForIndex();
					break;
				case TaskIndexDto.REGION:
					expression = taskQueryContext.getRegionNameForIndex();
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}

				orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}

		orderList.add(cb.desc(taskRoot.get(Task.DUE_DATE)));

		return orderList;
	}

	@Override
	@RightsAllowed(UserRight._TASK_EXPORT)
	public List<TaskExportDto> getExportList(TaskCriteria criteria, Collection<String> selectedRows, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskExportDto> cq = cb.createQuery(TaskExportDto.class);
		Root<Task> task = cq.from(Task.class);

		TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, task);
		TaskJoins joins = taskQueryContext.getJoins();
		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, joins.getCaseJoins());
		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, joins.getContactJoins());
		LocationJoins casePersonAddressJoins = caseQueryContext.getJoins().getPersonJoins().getAddressJoins();
		LocationJoins contactPersonAddressJoins = contactQueryContext.getJoins().getPersonJoins().getAddressJoins();

		//@formatter:off
		cq.multiselect(task.get(Task.UUID), task.get(Task.TASK_CONTEXT),
				joins.getCaze().get(Case.UUID), joins.getContact().get(Contact.UUID), joins.getEvent().get(Event.UUID),
				task.get(Task.TASK_TYPE), task.get(Task.PRIORITY), task.get(Task.DUE_DATE), task.get(Task.SUGGESTED_START), task.get(Task.TASK_STATUS),
				joins.getCreator().get(User.UUID), joins.getCreator().get(User.FIRST_NAME), joins.getCreator().get(User.LAST_NAME),
				task.get(Task.CREATOR_COMMENT),
				joins.getAssignee().get(User.UUID), joins.getAssignee().get(User.FIRST_NAME), joins.getAssignee().get(User.LAST_NAME),
				task.get(Task.ASSIGNEE_REPLY),
				CriteriaBuilderHelper.coalesce(cb, joins.getCaseRegion().get(Region.NAME), joins.getContactRegion().get(Region.NAME), joins.getEventRegion().get(Region.NAME)),
				CriteriaBuilderHelper.coalesce(cb, joins.getCaseDistrict().get(District.NAME), joins.getContactDistrict().get(District.NAME), joins.getEventDistrict().get(District.NAME)),
				CriteriaBuilderHelper.coalesce(cb, joins.getCaseCommunity().get(Community.NAME), joins.getContactCommunity().get(Community.NAME), joins.getEventCommunity().get(Community.NAME)),
				getPersonFieldPath(cb, joins, Person.FIRST_NAME), getPersonFieldPath(cb, joins, Person.LAST_NAME), getPersonFieldPath(cb, joins, Person.SEX),
				getPersonFieldPath(cb, joins, Person.BIRTHDATE_DD), getPersonFieldPath(cb, joins, Person.BIRTHDATE_MM), getPersonFieldPath(cb, joins, Person.BIRTHDATE_YYYY),
				CriteriaBuilderHelper.coalesce(cb, casePersonAddressJoins.getRegion().get(Region.NAME), contactPersonAddressJoins.getRegion().get(Region.NAME)),
				CriteriaBuilderHelper.coalesce(cb, casePersonAddressJoins.getDistrict().get(District.NAME), contactPersonAddressJoins.getDistrict().get(District.NAME)),
				CriteriaBuilderHelper.coalesce(cb, casePersonAddressJoins.getCommunity().get(Community.NAME), contactPersonAddressJoins.getCommunity().get(Community.NAME)),
				CriteriaBuilderHelper.coalesce(cb, casePersonAddressJoins.getFacility().get(Facility.NAME), contactPersonAddressJoins.getFacility().get(Facility.NAME)),
				getPersonAddressFieldPath(cb, joins, Location.FACILITY_DETAILS),
				getPersonAddressFieldPath(cb, joins, Location.CITY), getPersonAddressFieldPath(cb, joins, Location.STREET), getPersonAddressFieldPath(cb, joins, Location.HOUSE_NUMBER),
				getPersonAddressFieldPath(cb, joins, Location.POSTAL_CODE),
				CriteriaBuilderHelper.coalesce(cb, caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY), contactQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY)),
				CriteriaBuilderHelper.coalesce(cb, caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_OWNER_SUBQUERY), contactQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_OWNER_SUBQUERY)),
				CriteriaBuilderHelper.coalesce(cb, caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_EMAIL_SUBQUERY), contactQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_EMAIL_SUBQUERY)),
				CriteriaBuilderHelper.coalesce(cb, caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_OTHER_CONTACT_DETAILS_SUBQUERY), contactQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_OTHER_CONTACT_DETAILS_SUBQUERY)),
				JurisdictionHelper.booleanSelector(cb, taskService.inJurisdictionOrOwned(taskQueryContext))
			);
		//@formatter:on
		Predicate filter = taskService.createUserFilter(taskQueryContext, criteria);

		if (criteria != null) {
			Predicate criteriaFilter = taskService.buildCriteriaFilter(criteria, taskQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);

		cq.orderBy(cb.desc(task.get(Task.DUE_DATE)));

		List<TaskExportDto> tasks = QueryHelper.getResultList(em, cq, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(
			TaskExportDto.class,
			tasks,
			TaskExportDto::isInJurisdiction,
			(t, inJurisdiction) -> pseudonymizer.pseudonymizeDto(BirthDateDto.class, t.getPersonBirthDate(), inJurisdiction, null));

		return tasks;
	}

	private Expression<String> getPersonFieldPath(CriteriaBuilder cb, TaskJoins joins, String fieldName) {
		return CriteriaBuilderHelper.coalesce(cb, joins.getCasePerson().get(fieldName), joins.getContactPerson().get(fieldName));
	}

	private Expression<String> getPersonAddressFieldPath(CriteriaBuilder cb, TaskJoins joins, String fieldName) {
		return CriteriaBuilderHelper.coalesce(cb, joins.getCasePersonAddress().get(fieldName), joins.getContactPersonAddress().get(fieldName));
	}

	private void pseudonymizeContactReference(
		Pseudonymizer pseudonymizer,
		ContactReferenceDto contactReference,
		boolean isContactInJurisdiction,
		boolean isContactCaseInJurisdiction) {
		pseudonymizer.pseudonymizeDto(ContactReferenceDto.PersonName.class, contactReference.getContactName(), isContactInJurisdiction, null);

		if (contactReference.getCaseName() != null) {
			pseudonymizer.pseudonymizeDto(ContactReferenceDto.PersonName.class, contactReference.getCaseName(), isContactCaseInJurisdiction, null);
		}
	}

	@Override
	public List<TaskDto> getAllByCase(CaseReferenceDto caseRef) {

		if (caseRef == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(taskService.findBy(new TaskCriteria().caze(caseRef), false));
	}

	@Override
	public List<TaskDto> getAllByContact(ContactReferenceDto contactRef) {

		if (contactRef == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(taskService.findBy(new TaskCriteria().contact(contactRef), false));
	}

	@Override
	public List<TaskDto> getAllByEvent(EventReferenceDto eventRef) {

		if (eventRef == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(taskService.findBy(new TaskCriteria().event(eventRef), false));
	}

	@Override
	public List<TaskDto> getByUuids(List<String> uuids) {
		return toPseudonymizedDtos(taskService.getByUuids(uuids));
	}

	@Override
	public List<TaskDto> getAllPendingByCase(CaseReferenceDto caseRef) {

		if (caseRef == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(taskService.findBy(new TaskCriteria().caze(caseRef).taskStatus(TaskStatus.PENDING), false));
	}

	@Override
	public long getPendingTaskCountByContact(ContactReferenceDto contactRef) {

		if (contactRef == null) {
			return 0;
		}

		return taskService.getCount(new TaskCriteria().contact(contactRef).taskStatus(TaskStatus.PENDING));
	}

	@Override
	public long getPendingTaskCountByEvent(EventReferenceDto eventRef) {

		if (eventRef == null) {
			return 0;
		}

		return taskService.getCount(new TaskCriteria().event(eventRef).taskStatus(TaskStatus.PENDING));
	}

	@Override
	public Map<String, Long> getPendingTaskCountPerUser(List<String> userUuids) {

		Map<String, Long> taskCountMap = new HashMap<>();

		IterableHelper.executeBatched(userUuids, ModelConstants.PARAMETER_LIMIT, batchedUserUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<Task> from = cq.from(Task.class);
			Join<Task, User> userJoin = from.join(Task.ASSIGNEE_USER, JoinType.LEFT);

			cq.where(cb.equal(from.get(Task.TASK_STATUS), TaskStatus.PENDING), userJoin.get(User.UUID).in(batchedUserUuids));
			cq.multiselect(userJoin.get(User.UUID), cb.count(from));
			cq.groupBy(userJoin.get(User.UUID));

			List<Object[]> resultList = em.createQuery(cq).getResultList();
			resultList.forEach(r -> taskCountMap.put((String) r[0], (Long) r[1]));
		});

		return taskCountMap;
	}

	@Override
	public TaskDto getByUuid(String uuid) {
		return toDto(taskService.getByUuid(uuid), createPseudonymizer());
	}

	@Override
	@RightsAllowed(UserRight._TASK_DELETE)
	public void delete(String uuid) {
		if (!userService.hasRight(UserRight.TASK_DELETE)) {
			throw new AccessDeniedException(String.format("User %s is not allowed to delete tasks.", userService.getCurrentUser().getUuid()));
		}

		Task task = taskService.getByUuid(uuid);
		taskService.deletePermanent(task);
	}

	@Override
	@RightsAllowed(UserRight._TASK_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids) {
		List<ProcessedEntity> processedTasks = new ArrayList<>();
		List<Task> tasksToBeDeleted = taskService.getByUuids(uuids);

		if (tasksToBeDeleted != null) {
			tasksToBeDeleted.forEach(taskToBeDeleted -> {
				try {
					delete(taskToBeDeleted.getUuid());
					processedTasks.add(new ProcessedEntity(taskToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (AccessDeniedException e) {
					processedTasks.add(new ProcessedEntity(taskToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
					logger.error("The task with uuid {} could not be deleted due to an AccessDeniedException", taskToBeDeleted.getUuid(), e);
				} catch (Exception e) {
					processedTasks.add(new ProcessedEntity(taskToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The task with uuid {} could not be deleted due to an Exception", taskToBeDeleted.getUuid(), e);
				}
			});
		}
		return processedTasks;
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendNewAndDueTaskMessages() {

		final Calendar calendar = Calendar.getInstance();
		final Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, CronService.TASK_UPDATE_INTERVAL * -1);
		final Date before = calendar.getTime();

		try {
			notificationService.sendNotifications(
				NotificationType.TASK_START,
				MessageSubject.TASK_START,
				() -> buildTaskUserMessages(
					taskService.findBy(new TaskCriteria().taskStatus(TaskStatus.PENDING).startDateBetween(before, now), true),
					MessageContents.CONTENT_TASK_START_GENERAL,
					MessageContents.CONTENT_TASK_START_SPECIFIC));
		} catch (NotificationDeliveryFailedException e) {
			logger.error("EmailDeliveryFailedException when trying to notify a user about a starting task.");
		}

		try {
			Supplier<Map<User, String>> userMessageSupplier = () -> buildTaskUserMessages(
				taskService.findBy(new TaskCriteria().taskStatus(TaskStatus.PENDING).dueDateBetween(before, now), true),
				MessageContents.CONTENT_TASK_DUE_GENERAL,
				MessageContents.CONTENT_TASK_DUE_SPECIFIC);

			notificationService.sendNotifications(NotificationType.TASK_DUE, MessageSubject.TASK_DUE, userMessageSupplier);
		} catch (NotificationDeliveryFailedException e) {
			logger.error("EmailDeliveryFailedException when trying to notify a user about a due task.");
		}
	}

	private Map<User, String> buildTaskUserMessages(List<Task> tasks, String generalMessageTag, String specificMessageTag) {
		final Map<User, String> messages = new HashMap<>();

		for (Task task : tasks) {
			final TaskContext context = task.getTaskContext();
			final AbstractDomainObject associatedEntity = context == TaskContext.CASE
				? task.getCaze()
				: context == TaskContext.CONTACT ? task.getContact() : context == TaskContext.EVENT ? task.getEvent() : null;

			String message = getTaskNotificationMessage(task, associatedEntity, generalMessageTag, specificMessageTag);

			if (task.getAssigneeUser() != null) {
				messages.put(task.getAssigneeUser(), message);
			}

			if (task.getObserverUsers() != null) {
				String observerUserMessage = I18nProperties.getString(MessageContents.CONTENT_TASK_OBSERVER_INFORMATION) + "\n\n" + message;
				for (User observerUser : task.getObserverUsers()) {
					messages.put(observerUser, observerUserMessage);
				}
			}
		}

		return messages;
	}

	private void validate(TaskDto task) throws ValidationRuntimeException {

		if (task.getTaskContext() == TaskContext.CASE && task.getCaze() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.taskMissingCaseLink));
		}
		if (task.getTaskContext() == TaskContext.CONTACT && task.getContact() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.taskMissingContactLink));
		}
		if (task.getTaskContext() == TaskContext.EVENT && task.getEvent() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.taskMissingEventLink));
		}
	}

	@Override
	@RightsAllowed(UserRight._TASK_ARCHIVE)
	public void archive(String uuid) {
		archive(Collections.singletonList(uuid));
	}

	@Override
	@RightsAllowed(UserRight._TASK_ARCHIVE)
	public ProcessedEntity dearchive(String uuid) {
		return dearchive(Collections.singletonList(uuid)).get(0);
	}

	@Override
	@RightsAllowed(UserRight._TASK_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> taskUuids) {
		List<ProcessedEntity> processedTasks = new ArrayList<>();
		IterableHelper.executeBatched(taskUuids, ARCHIVE_BATCH_SIZE, e -> processedTasks.addAll(taskService.updateArchived(e, true)));

		return processedTasks;
	}

	@Override
	@RightsAllowed(UserRight._TASK_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> taskUuids) {
		List<ProcessedEntity> processedTasks = new ArrayList<>();
		IterableHelper.executeBatched(taskUuids, ARCHIVE_BATCH_SIZE, e -> processedTasks.addAll(taskService.updateArchived(e, false)));

		return processedTasks;
	}

	@Override
	public boolean isArchived(String taskUuid) {
		return taskService.isArchived(taskUuid);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return taskService.getArchivedUuidsSince(since);
	}

	@Override
	public List<String> getObsoleteUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return taskService.getObsoleteUuidsSince(since);
	}

	private String buildSpecificTaskMessage(String messageTemplate, TaskType taskType, TaskContext taskContext, AbstractDomainObject entity) {
		String entityReference = buildAssociatedEntityReference(taskContext, entity);
		String linkContent = buildAssociatedEntityLinkContent(taskContext, entity);
		return String.format(I18nProperties.getString(messageTemplate) + "%s", taskType, entityReference, linkContent);
	}

	private String buildAssociatedEntityReference(TaskContext taskContext, AbstractDomainObject entity) {
		if (taskContext.getUrlPattern() == null || entity == null) {
			return taskContext.toString();
		}

		return taskContext + " " + DataHelper.getShortUuid(entity.getUuid());
	}

	private String buildAssociatedEntityLinkContent(TaskContext taskContext, AbstractDomainObject entity) {
		if (taskContext.getUrlPattern() == null || entity == null) {
			return "";
		}

		String url = getUiUrl(taskContext, entity.getUuid());
		if (url == null) {
			return "";
		}

		String associatedEntityLinkMessage = taskContext.getAssociatedEntityLinkMessage();
		return "\n" + String.format(I18nProperties.getString(associatedEntityLinkMessage), url);
	}

	/**
	 * Return the url of the related entity.
	 * The url is bound to the Sormas UI made with Vaadin.
	 * This function will need to be modified if the UI will have URL modifications
	 * or in case the UI app is replaced by another one.
	 */
	private String getUiUrl(TaskContext taskContext, String uuid) {
		if (taskContext.getUrlPattern() == null || uuid == null) {
			return null;
		}

		String uiUrl = configFacade.getUiUrl();
		if (uiUrl == null) {
			return null;
		}

		StringBuilder uiUrlBuilder = new StringBuilder(uiUrl);
		if (!uiUrl.endsWith("/")) {
			uiUrlBuilder.append("/");
		}
		return uiUrlBuilder.append("#!").append(taskContext.getUrlPattern()).append("/data/").append(uuid).toString();
	}

	@Override
	public EditPermissionType getEditPermissionType(String uuid) {
		return taskService.getEditPermissionType(taskService.getByUuid(uuid));
	}

	@LocalBean
	@Stateless
	public static class TaskFacadeEjbLocal extends TaskFacadeEjb {

	}
}
