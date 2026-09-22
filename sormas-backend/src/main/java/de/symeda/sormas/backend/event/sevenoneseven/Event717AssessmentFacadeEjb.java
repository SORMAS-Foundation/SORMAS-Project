/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.event.sevenoneseven;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentFacade;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckReferenceDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessCalculator;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "Event717AssessmentFacade")
@RightsAllowed(UserRight._EVENT_717_ASSESSMENT_VIEW)
public class Event717AssessmentFacadeEjb implements Event717AssessmentFacade {

	@EJB
	private Event717AssessmentService service;
	@EJB
	private EventService eventService;
	@EJB
	private UserService userService;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@Override
	public Event717AssessmentDto getByEventUuid(String eventUuid) {

		checkFeatureEnabled();
		getAccessibleEvent(eventUuid);
		return toDto(service.getByEventUuid(eventUuid));
	}

	@Override
	public Event717AssessmentDto getByUuid(String uuid) {

		checkFeatureEnabled();
		Event717Assessment assessment = service.getByUuid(uuid);
		if (assessment != null) {
			getAccessibleEvent(assessment.getEvent().getUuid());
		}
		return toDto(assessment);
	}

	@Override
	public boolean existsForEvent(String eventUuid) {

		checkFeatureEnabled();
		return service.getByEventUuid(eventUuid) != null;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_717_ASSESSMENT_EDIT)
	public Event717AssessmentDto save(@Valid @NotNull Event717AssessmentDto dto) {

		checkFeatureEnabled();

		if (dto.getEvent() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.EVENT)));
		}

		Event event = getEditableEvent(dto.getEvent().getUuid());

		Event717Assessment existingAssessment = service.getByUuid(dto.getUuid());
		if (existingAssessment != null && !existingAssessment.getEvent().equals(event)) {
			// an assessment can not be moved to another event
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorEvent717AssessmentAlreadyExists));
		}
		Event717Assessment assessmentOfEvent = service.getByEvent(event);
		if (assessmentOfEvent != null && !assessmentOfEvent.getUuid().equals(dto.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorEvent717AssessmentAlreadyExists));
		}

		validate(dto);

		// the completion date is always derived from the early response action dates
		dto.setEarlyResponseCompletionDate(Event717TimelinessCalculator.calculateEarlyResponseCompletionDate(dto));

		Event717Assessment assessment = fillOrBuildEntity(dto, existingAssessment, true);
		assessment.setEvent(event);
		service.ensurePersisted(assessment);

		return toDto(assessment);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_717_ASSESSMENT_EDIT)
	public void deleteByEventUuid(String eventUuid) {

		checkFeatureEnabled();
		Event event = getEditableEvent(eventUuid);
		service.deletePermanentByEvent(event);
	}

	@Override
	public Event717TimelinessDto getTimelinessByEventUuid(String eventUuid) {

		Event717AssessmentDto assessment = getByEventUuid(eventUuid);
		return assessment != null ? Event717TimelinessCalculator.calculate(assessment) : null;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_717_ASSESSMENT_VIEW)
	public Event717TimelinessDto calculateTimeliness(Event717AssessmentDto dto) {

		checkFeatureEnabled();
		return Event717TimelinessCalculator.calculate(dto);
	}

	@Override
	public void validate(Event717AssessmentDto dto) throws ValidationRuntimeException {

		Date endOfToday = DateHelper.getEndOfDay(new Date());
		validateNotInFuture(dto.getDateOfEmergence(), Event717AssessmentDto.DATE_OF_EMERGENCE, endOfToday);
		validateNotInFuture(dto.getDateOfDetection(), Event717AssessmentDto.DATE_OF_DETECTION, endOfToday);
		validateNotInFuture(dto.getDateOfNotification(), Event717AssessmentDto.DATE_OF_NOTIFICATION, endOfToday);
		validateNotInFuture(dto.getOutbreakEndDate(), Event717AssessmentDto.OUTBREAK_END_DATE, endOfToday);
		validateNotInFuture(dto.getReportCompletedDate(), Event717AssessmentDto.REPORT_COMPLETED_DATE, endOfToday);

		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			String dateProperty = Event717AssessmentDto.getEarlyResponseActionDateProperty(action);
			Date actionDate = dto.getEarlyResponseActionDate(action);
			if (dto.isEarlyResponseActionNotApplicable(action) && actionDate != null) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(Validations.event717EarlyResponseActionDateWithNotApplicable, assessmentCaption(dateProperty)));
			}
			validateNotInFuture(actionDate, dateProperty, endOfToday);
		}

		validateBottlenecks(dto.getBottlenecks());
		validateEnablers(dto.getEnablers());
		validateCorrectiveActions(dto.getCorrectiveActions(), dto.getBottlenecks());
	}

	@Override
	public EditPermissionType getEditPermissionType(String eventUuid) {

		checkFeatureEnabled();
		Event event = getAccessibleEvent(eventUuid);
		if (event.isDeleted()) {
			return EditPermissionType.REFUSED;
		}
		return eventService.getEditPermissionType(event);
	}

	private void validateBottlenecks(List<Event717BottleneckDto> bottlenecks) {

		validateIntervalEntries(
			bottlenecks,
			Event717BottleneckDto::getUuid,
			Event717BottleneckDto::getTimelinessInterval,
			Event717BottleneckDto::getDescription,
			Event717BottleneckDto.I18N_PREFIX,
			Event717AssessmentDto.BOTTLENECKS);

		for (Event717BottleneckDto bottleneck : bottlenecks) {
			if (bottleneck.getCategory() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.event717BottleneckCategoryRequired));
			}
			if (bottleneck.getCategory() == Event717BottleneckCategory.OTHER && StringUtils.isBlank(bottleneck.getOtherCategoryDetails())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.event717OtherCategoryDetailsRequired));
			}
		}
	}

	private void validateEnablers(List<Event717EnablerDto> enablers) {

		validateIntervalEntries(
			enablers,
			Event717EnablerDto::getUuid,
			Event717EnablerDto::getTimelinessInterval,
			Event717EnablerDto::getDescription,
			Event717EnablerDto.I18N_PREFIX,
			Event717AssessmentDto.ENABLERS);
	}

	/**
	 * Validates what bottlenecks and enablers have in common: unique uuids, a required interval and description and a
	 * maximum number of entries per interval.
	 */
	private static <T> void validateIntervalEntries(
		List<T> entries,
		Function<T, String> uuidGetter,
		Function<T, Event717Interval> intervalGetter,
		Function<T, String> descriptionGetter,
		String i18nPrefix,
		String listProperty) {

		Set<String> uuids = new HashSet<>();
		Map<Event717Interval, Integer> counts = new EnumMap<>(Event717Interval.class);

		for (T entry : entries) {
			if (!uuids.add(uuidGetter.apply(entry))) {
				throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorEntityOutdated));
			}
			Event717Interval interval = intervalGetter.apply(entry);
			if (interval == null) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(
						Validations.required,
						I18nProperties.getPrefixCaption(i18nPrefix, Event717BottleneckDto.TIMELINESS_INTERVAL)));
			}
			if (StringUtils.isBlank(descriptionGetter.apply(entry))) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.event717DescriptionRequired));
			}
			if (counts.merge(interval, 1, Integer::sum) > Event717AssessmentDto.MAX_ENTRIES_PER_INTERVAL) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(
						Validations.event717MaxEntriesExceeded,
						Event717AssessmentDto.MAX_ENTRIES_PER_INTERVAL,
						assessmentCaption(listProperty),
						interval));
			}
		}
	}

	private void validateCorrectiveActions(List<Event717CorrectiveActionDto> actions, List<Event717BottleneckDto> bottlenecks) {

		Set<String> bottleneckUuids = bottlenecks.stream().map(Event717BottleneckDto::getUuid).collect(Collectors.toSet());
		Set<String> uuids = new HashSet<>();

		for (Event717CorrectiveActionDto action : actions) {
			if (!uuids.add(action.getUuid())) {
				throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorEntityOutdated));
			}
			if (StringUtils.isBlank(action.getProposedAction())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.event717ProposedActionRequired));
			}
			if (action.getBottleneck() != null && !bottleneckUuids.contains(action.getBottleneck().getUuid())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.event717CorrectiveActionBottleneckInvalid));
			}
			if (action.getTargetStartDate() != null
				&& action.getTargetEndDate() != null
				&& DateHelper.getStartOfDay(action.getTargetEndDate()).before(DateHelper.getStartOfDay(action.getTargetStartDate()))) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(
						Validations.afterDate,
						I18nProperties.getPrefixCaption(Event717CorrectiveActionDto.I18N_PREFIX, Event717CorrectiveActionDto.TARGET_END_DATE),
						I18nProperties.getPrefixCaption(Event717CorrectiveActionDto.I18N_PREFIX, Event717CorrectiveActionDto.TARGET_START_DATE)));
			}
		}
	}

	private static void validateNotInFuture(Date date, String property, Date endOfToday) {

		if (date != null && date.after(endOfToday)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.futureDateStrict, assessmentCaption(property)));
		}
	}

	private static String assessmentCaption(String property) {
		return I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, property);
	}

	private void checkFeatureEnabled() {

		if (featureConfigurationFacade.isFeatureDisabled(FeatureType.EVENT_717_ASSESSMENT)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEvent717AssessmentFeatureDisabled));
		}
	}

	private Event getAccessibleEvent(String eventUuid) {

		Event event = eventService.getByUuid(eventUuid);
		if (event == null) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorEvent717AssessmentEventNotFound));
		}
		if (event.isArchived() && !userService.hasRight(UserRight.EVENT_VIEW_ARCHIVED)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorAccessDenied));
		}
		return event;
	}

	private Event getEditableEvent(String eventUuid) {

		Event event = getAccessibleEvent(eventUuid);
		if (event.isDeleted() || !eventService.isEditAllowed(event)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEvent717AssessmentNotEditable));
		}
		return event;
	}

	public Event717Assessment fillOrBuildEntity(Event717AssessmentDto source, Event717Assessment target, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Event717Assessment::new, checkChangeDate);

		target.setDateOfEmergence(source.getDateOfEmergence());
		target.setEmergenceNarrative(source.getEmergenceNarrative());
		target.setDateOfDetection(source.getDateOfDetection());
		target.setDetectionNarrative(source.getDetectionNarrative());
		target.setDateOfNotification(source.getDateOfNotification());
		target.setNotificationNarrative(source.getNotificationNarrative());
		target.setInvestigationDate(source.getInvestigationDate());
		target.setInvestigationNotApplicable(source.isInvestigationNotApplicable());
		target.setInvestigationNarrative(source.getInvestigationNarrative());
		target.setEpiAnalysisDate(source.getEpiAnalysisDate());
		target.setEpiAnalysisNotApplicable(source.isEpiAnalysisNotApplicable());
		target.setEpiAnalysisNarrative(source.getEpiAnalysisNarrative());
		target.setLabConfirmationDate(source.getLabConfirmationDate());
		target.setLabConfirmationNotApplicable(source.isLabConfirmationNotApplicable());
		target.setLabConfirmationNarrative(source.getLabConfirmationNarrative());
		target.setCaseManagementDate(source.getCaseManagementDate());
		target.setCaseManagementNotApplicable(source.isCaseManagementNotApplicable());
		target.setCaseManagementNarrative(source.getCaseManagementNarrative());
		target.setCountermeasuresDate(source.getCountermeasuresDate());
		target.setCountermeasuresNotApplicable(source.isCountermeasuresNotApplicable());
		target.setCountermeasuresNarrative(source.getCountermeasuresNarrative());
		target.setRiskCommunicationDate(source.getRiskCommunicationDate());
		target.setRiskCommunicationNotApplicable(source.isRiskCommunicationNotApplicable());
		target.setRiskCommunicationNarrative(source.getRiskCommunicationNarrative());
		target.setCoordinationDate(source.getCoordinationDate());
		target.setCoordinationNotApplicable(source.isCoordinationNotApplicable());
		target.setCoordinationNarrative(source.getCoordinationNarrative());
		target.setEarlyResponseCompletionNarrative(source.getEarlyResponseCompletionNarrative());
		target.setOutbreakEndDate(source.getOutbreakEndDate());
		target.setReportCompletedDate(source.getReportCompletedDate());
		target.setReportCompletedByName(source.getReportCompletedByName());
		target.setGeneralNotes(source.getGeneralNotes());
		target.setEarlyResponseCompletionDate(source.getEarlyResponseCompletionDate());
		target.setReportCompletedByUser(userService.getByReferenceDto(source.getReportCompletedByUser()));

		// Children are only looked up in the collections of this assessment, so they can't be moved between assessments
		Map<String, Event717Bottleneck> existingBottlenecks = mapByUuid(target.getBottlenecks());
		List<Event717Bottleneck> bottlenecks = new ArrayList<>();
		Map<String, Event717Bottleneck> bottlenecksByUuid = new HashMap<>();
		for (Event717BottleneckDto bottleneckDto : source.getBottlenecks()) {
			Event717Bottleneck bottleneck = fillOrBuildEntity(bottleneckDto, existingBottlenecks.get(bottleneckDto.getUuid()), checkChangeDate);
			bottleneck.setAssessment(target);
			bottlenecks.add(bottleneck);
			bottlenecksByUuid.put(bottleneck.getUuid(), bottleneck);
		}

		Map<String, Event717Enabler> existingEnablers = mapByUuid(target.getEnablers());
		List<Event717Enabler> enablers = new ArrayList<>();
		for (Event717EnablerDto enablerDto : source.getEnablers()) {
			Event717Enabler enabler = fillOrBuildEntity(enablerDto, existingEnablers.get(enablerDto.getUuid()), checkChangeDate);
			enabler.setAssessment(target);
			enablers.add(enabler);
		}

		Map<String, Event717CorrectiveAction> existingActions =
			mapByUuid(target.getCorrectiveActions());
		List<Event717CorrectiveAction> actions = new ArrayList<>();
		for (Event717CorrectiveActionDto actionDto : source.getCorrectiveActions()) {
			Event717CorrectiveAction action = fillOrBuildEntity(actionDto, existingActions.get(actionDto.getUuid()), checkChangeDate);
			action.setAssessment(target);
			action.setBottleneck(actionDto.getBottleneck() != null ? bottlenecksByUuid.get(actionDto.getBottleneck().getUuid()) : null);
			actions.add(action);
		}

		if (!DataHelper.equalContains(target.getBottlenecks(), bottlenecks)
			|| !DataHelper.equalContains(target.getEnablers(), enablers)
			|| !DataHelper.equalContains(target.getCorrectiveActions(), actions)) {
			// note: DataHelper.equal does not work here, because the lists may be a PersistentBag when using lazy loading
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getBottlenecks().clear();
		target.getBottlenecks().addAll(bottlenecks);
		target.getEnablers().clear();
		target.getEnablers().addAll(enablers);
		target.getCorrectiveActions().clear();
		target.getCorrectiveActions().addAll(actions);

		return target;
	}

	private static <T extends AbstractDomainObject> Map<String, T> mapByUuid(List<T> entities) {
		return entities.stream().collect(Collectors.toMap(AbstractDomainObject::getUuid, Function.identity()));
	}

	public Event717Bottleneck fillOrBuildEntity(Event717BottleneckDto source, Event717Bottleneck target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Event717Bottleneck::new, checkChangeDate);

		target.setTimelinessInterval(source.getTimelinessInterval());
		target.setDescription(source.getDescription());
		target.setCategory(source.getCategory());
		target.setOtherCategoryDetails(source.getOtherCategoryDetails());
		if (source.getCategory() != Event717BottleneckCategory.OTHER) {
			target.setOtherCategoryDetails(null);
		}

		return target;
	}

	public Event717Enabler fillOrBuildEntity(Event717EnablerDto source, Event717Enabler target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Event717Enabler::new, checkChangeDate);

		target.setTimelinessInterval(source.getTimelinessInterval());
		target.setDescription(source.getDescription());

		return target;
	}

	public Event717CorrectiveAction fillOrBuildEntity(Event717CorrectiveActionDto source, Event717CorrectiveAction target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Event717CorrectiveAction::new, checkChangeDate);

		target.setProposedAction(source.getProposedAction());
		target.setPrioritization(source.getPrioritization());
		target.setResponsibleAuthority(source.getResponsibleAuthority());
		target.setTargetStartDate(source.getTargetStartDate());
		target.setTargetEndDate(source.getTargetEndDate());
		target.setPlanningFundingOpportunities(source.getPlanningFundingOpportunities());
		target.setProgressStatus(source.getProgressStatus());
		target.setNextSteps(source.getNextSteps());

		return target;
	}

	public static Event717AssessmentDto toDto(Event717Assessment source) {

		if (source == null) {
			return null;
		}

		Event717AssessmentDto target = new Event717AssessmentDto();
		DtoHelper.fillDto(target, source);

		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setDateOfEmergence(source.getDateOfEmergence());
		target.setEmergenceNarrative(source.getEmergenceNarrative());
		target.setDateOfDetection(source.getDateOfDetection());
		target.setDetectionNarrative(source.getDetectionNarrative());
		target.setDateOfNotification(source.getDateOfNotification());
		target.setNotificationNarrative(source.getNotificationNarrative());
		target.setInvestigationDate(source.getInvestigationDate());
		target.setInvestigationNotApplicable(source.isInvestigationNotApplicable());
		target.setInvestigationNarrative(source.getInvestigationNarrative());
		target.setEpiAnalysisDate(source.getEpiAnalysisDate());
		target.setEpiAnalysisNotApplicable(source.isEpiAnalysisNotApplicable());
		target.setEpiAnalysisNarrative(source.getEpiAnalysisNarrative());
		target.setLabConfirmationDate(source.getLabConfirmationDate());
		target.setLabConfirmationNotApplicable(source.isLabConfirmationNotApplicable());
		target.setLabConfirmationNarrative(source.getLabConfirmationNarrative());
		target.setCaseManagementDate(source.getCaseManagementDate());
		target.setCaseManagementNotApplicable(source.isCaseManagementNotApplicable());
		target.setCaseManagementNarrative(source.getCaseManagementNarrative());
		target.setCountermeasuresDate(source.getCountermeasuresDate());
		target.setCountermeasuresNotApplicable(source.isCountermeasuresNotApplicable());
		target.setCountermeasuresNarrative(source.getCountermeasuresNarrative());
		target.setRiskCommunicationDate(source.getRiskCommunicationDate());
		target.setRiskCommunicationNotApplicable(source.isRiskCommunicationNotApplicable());
		target.setRiskCommunicationNarrative(source.getRiskCommunicationNarrative());
		target.setCoordinationDate(source.getCoordinationDate());
		target.setCoordinationNotApplicable(source.isCoordinationNotApplicable());
		target.setCoordinationNarrative(source.getCoordinationNarrative());
		target.setEarlyResponseCompletionNarrative(source.getEarlyResponseCompletionNarrative());
		target.setOutbreakEndDate(source.getOutbreakEndDate());
		target.setReportCompletedDate(source.getReportCompletedDate());
		target.setReportCompletedByName(source.getReportCompletedByName());
		target.setGeneralNotes(source.getGeneralNotes());
		target.setEarlyResponseCompletionDate(source.getEarlyResponseCompletionDate());
		target.setReportCompletedByUser(UserFacadeEjb.toReferenceDto(source.getReportCompletedByUser()));

		target.setBottlenecks(
			source.getBottlenecks().stream().map(Event717AssessmentFacadeEjb::toDto).collect(Collectors.toCollection(ArrayList::new)));
		target.setEnablers(source.getEnablers().stream().map(Event717AssessmentFacadeEjb::toDto).collect(Collectors.toCollection(ArrayList::new)));
		target.setCorrectiveActions(
			source.getCorrectiveActions().stream().map(Event717AssessmentFacadeEjb::toDto).collect(Collectors.toCollection(ArrayList::new)));

		return target;
	}

	public static Event717BottleneckDto toDto(Event717Bottleneck source) {

		Event717BottleneckDto target = new Event717BottleneckDto();
		DtoHelper.fillDto(target, source);

		target.setTimelinessInterval(source.getTimelinessInterval());
		target.setDescription(source.getDescription());
		target.setCategory(source.getCategory());
		target.setOtherCategoryDetails(source.getOtherCategoryDetails());

		return target;
	}

	public static Event717EnablerDto toDto(Event717Enabler source) {

		Event717EnablerDto target = new Event717EnablerDto();
		DtoHelper.fillDto(target, source);

		target.setTimelinessInterval(source.getTimelinessInterval());
		target.setDescription(source.getDescription());

		return target;
	}

	public static Event717CorrectiveActionDto toDto(Event717CorrectiveAction source) {

		Event717CorrectiveActionDto target = new Event717CorrectiveActionDto();
		DtoHelper.fillDto(target, source);

		target.setProposedAction(source.getProposedAction());
		target.setPrioritization(source.getPrioritization());
		target.setResponsibleAuthority(source.getResponsibleAuthority());
		target.setTargetStartDate(source.getTargetStartDate());
		target.setTargetEndDate(source.getTargetEndDate());
		target.setPlanningFundingOpportunities(source.getPlanningFundingOpportunities());
		target.setProgressStatus(source.getProgressStatus());
		target.setNextSteps(source.getNextSteps());
		target.setBottleneck(toReferenceDto(source.getBottleneck()));

		return target;
	}

	public static Event717BottleneckReferenceDto toReferenceDto(Event717Bottleneck entity) {

		if (entity == null) {
			return null;
		}
		return new Event717BottleneckReferenceDto(entity.getUuid(), entity.getDescription());
	}

	@LocalBean
	@Stateless
	public static class Event717AssessmentFacadeEjbLocal extends Event717AssessmentFacadeEjb {

	}
}
