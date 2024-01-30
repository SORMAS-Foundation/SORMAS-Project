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

package de.symeda.sormas.backend.visit;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserReference;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "VisitFacade")
@RightsAllowed({
	UserRight._CONTACT_VIEW,
	UserRight._CASE_VIEW })
public class VisitFacadeEjb extends AbstractBaseEjb<Visit, VisitDto, VisitIndexDto, VisitReferenceDto, VisitService, VisitCriteria>
	implements VisitFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private VisitService visitService;
	@EJB
	private ContactService contactService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PersonService personService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private NotificationService notificationService;

	public VisitFacadeEjb() {
	}

	@Inject
	protected VisitFacadeEjb(VisitService service) {
		super(Visit.class, VisitDto.class, service);
	}

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	/**
	 * Attention: For now this only returns the visits of contacts, since case visits are not yet implemented in the mobile app
	 */
	@Override
	public List<VisitDto> getAllActiveVisitsAfter(Date date) {
		return getAllActiveVisitsAfter(date, null, null);
	}

	@Override
	public List<VisitDto> getAllActiveVisitsAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		return toPseudonymizedDtos(service.getAllAfter(date, batchSize, lastSynchronizedUuid));
	}

	@Override
	public List<VisitDto> getAllAfter(Date date) {
		return toPseudonymizedDtos(service.getAllAfter(date, null, null));
	}

	@Override
	public VisitDto getLastVisitByContact(ContactReferenceDto contactRef) {

		Contact contact = contactService.getByReferenceDto(contactRef);
		return toPseudonymizedDto(contact.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null));
	}

	@Override
	public List<VisitDto> getVisitsByContact(ContactReferenceDto contactRef) {
		Contact contact = contactService.getByReferenceDto(contactRef);
		return contact.getVisits().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<VisitDto> getVisitsByContactAndPeriod(ContactReferenceDto contactRef, Date begin, Date end) {

		Contact contact = contactService.getByReferenceDto(contactRef);
		return toPseudonymizedDtos(
			contact.getVisits()
				.stream()
				.filter(visit -> visit.getVisitDateTime().after(begin) && visit.getVisitDateTime().before(end))
				.collect(Collectors.toList()));
	}

	@Override
	public VisitDto getLastVisitByCase(CaseReferenceDto caseRef) {
		Case caze = caseService.getByReferenceDto(caseRef);
		return toDto(caze.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null));
	}

	@Override
	public List<VisitDto> getVisitsByCase(CaseReferenceDto caseRef) {
		Case caze = caseService.getByReferenceDto(caseRef);
		return caze.getVisits().stream().map(this::toDto).collect(Collectors.toList());
	}

	/**
	 * Only allowed to use for ExternalVisits, merging and test puroses
	 */
	@Override
	@RightsAllowed({
		UserRight._VISIT_CREATE,
		UserRight._VISIT_EDIT })
	public VisitDto save(@Valid @NotNull VisitDto dto) {
		return saveVisit(dto, null, null);
	}

	@Override
	@RightsAllowed({
		UserRight._VISIT_CREATE,
		UserRight._VISIT_EDIT })
	public VisitDto saveVisit(@Valid VisitDto dto, Date allowedStartDate, Date allowedEndDate) {
		final String visitUuid = dto.getUuid();
		final Visit existingVisit = visitUuid != null ? service.getByUuid(visitUuid) : null;

		FacadeHelper.checkCreateAndEditRights(existingVisit, userService, UserRight.VISIT_CREATE, UserRight.VISIT_EDIT);
		return doSaveVisit(dto, existingVisit, allowedStartDate, allowedEndDate);
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_VISITS)
	public ExternalVisitDto saveExternalVisit(@Valid final ExternalVisitDto dto) {

		final String personUuid = dto.getPersonUuid();
		final UserReferenceDto currentUser = new UserReferenceDto(userService.getCurrentUser().getUuid());

		final VisitDto visitDto = VisitDto.build(
			new PersonReferenceDto(personUuid),
			dto.getDisease(),
			dto.getVisitDateTime(),
			currentUser,
			dto.getVisitStatus(),
			dto.getVisitRemarks(),
			dto.getSymptoms(),
			dto.getReportLat(),
			dto.getReportLon(),
			dto.getReportLatLonAccuracy(),
			VisitOrigin.EXTERNAL_JOURNAL);

		doSaveVisit(visitDto, null, null, null);

		return ExternalVisitDto.build(
			personUuid,
			visitDto.getDisease(),
			visitDto.getVisitDateTime(),
			visitDto.getVisitStatus(),
			visitDto.getVisitRemarks(),
			visitDto.getSymptoms(),
			visitDto.getReportLat(),
			visitDto.getReportLon(),
			visitDto.getReportLatLonAccuracy());
	}

	private VisitDto doSaveVisit(@Valid VisitDto dto, Visit existingVisit, Date allowedStartDate, Date allowedEndDate) {
		final VisitDto existingDto = toDto(existingVisit);

		restorePseudonymizedDto(dto, existingDto, existingVisit);

		this.validate(dto, allowedStartDate, allowedEndDate);

		if (dto.getVisitStatus().equals(VisitStatus.COOPERATIVE)) {
			SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());
		} else {
			dto.getSymptoms().setSymptomatic(null);
		}
		Visit entity = fillOrBuildEntity(dto, existingVisit, true);

		service.ensurePersisted(entity);

		onVisitChanged(existingDto, entity);

		return toPseudonymizedDto(entity, createPseudonymizer());
	}

	@Override
	public void validate(VisitDto dto) throws ValidationRuntimeException {
		validate(dto, null, null);
	}

	@Override
	public void validate(VisitDto visit, Date allowedStartDate, Date allowedEndDate) {

		if (visit.getVisitStatus() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitStatus));
		}
		if (visit.getSymptoms() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitSymptoms));
		}
		if (visit.getVisitDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitDate));
		} else if (allowedStartDate != null && DateHelper.isDateBefore(visit.getVisitDateTime(), allowedStartDate)) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.afterDate,
					I18nProperties.getPrefixCaption(VisitDto.I18N_PREFIX, VisitDto.VISIT_DATE_TIME),
					DateHelper.formatShortDate(allowedStartDate)));
		} else if (allowedEndDate != null && DateHelper.isDateAfter(visit.getVisitDateTime(), allowedEndDate)) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.beforeDate,
					I18nProperties.getPrefixCaption(VisitDto.I18N_PREFIX, VisitDto.VISIT_DATE_TIME),
					DateHelper.formatShortDate(allowedEndDate)));
		}
		if (visit.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		if (visit.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
	}

	@Override
	@RightsAllowed(UserRight._VISIT_DELETE)
	public void delete(String uuid) {
		Visit visit = service.getByUuid(uuid);
		service.deletePermanent(visit);
	}

	@Override
	@RightsAllowed(UserRight._VISIT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids) {
		List<ProcessedEntity> processedVisits = new ArrayList<>();
		List<Visit> visitsToBeDeleted = visitService.getByUuids(uuids);

		if (visitsToBeDeleted != null) {
			visitsToBeDeleted.forEach(visitToBeDeleted -> {
				try {
					delete(visitToBeDeleted.getUuid());
					processedVisits.add(new ProcessedEntity(visitToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (AccessDeniedException e) {
					processedVisits.add(new ProcessedEntity(visitToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
					logger.error("The visit with uuid {} could not be deleted due to a AccessDeniedException", visitToBeDeleted.getUuid(), e);
				} catch (Exception e) {
					processedVisits.add(new ProcessedEntity(visitToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The visit with uuid {} could not be deleted due to an Exception", visitToBeDeleted.getUuid(), e);
				}
			});
		}

		return processedVisits;
	}

	@Override
	public List<VisitIndexDto> getIndexList(VisitCriteria visitCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		if (visitCriteria == null || visitCriteria.isEmpty()) {
			return new ArrayList<>(); // Retrieving an index list independent of a contact/case is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

		Root<Visit> visit = cq.from(Visit.class);
		VisitQueryContext queryContext = new VisitQueryContext(cb, cq, visit);
		Join<Visit, Symptoms> symptoms = queryContext.getJoins().getSymptoms();
		Join<Visit, User> visitUser = queryContext.getJoins().getUser();

		List<Order> orderList = getOrderList(sortProperties, visit, cb, symptoms, cq);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						visit.get(Visit.ID),
						visit.get(Visit.UUID),
						visit.get(Visit.VISIT_DATE_TIME),
						visit.get(Visit.VISIT_STATUS),
						visit.get(Visit.VISIT_REMARKS),
						visit.get(Visit.DISEASE),
						symptoms.get(Symptoms.SYMPTOMATIC),
						symptoms.get(Symptoms.TEMPERATURE),
						symptoms.get(Symptoms.TEMPERATURE_SOURCE),
						visit.get(Visit.ORIGIN),
						visitUser.get(User.UUID),
						visitUser.get(User.FIRST_NAME),
						visitUser.get(User.LAST_NAME),
						jurisdictionSelector(queryContext)),
					// add order by expressions to select
					orderList.stream().map(Order::getExpression))
				.collect(Collectors.toList()));

		cq.distinct(true);
		cq.where(service.buildCriteriaFilter(visitCriteria, cb, visit));
		cq.orderBy(orderList);

		List<VisitIndexDto> indexList = QueryHelper.getResultList(em, cq, new VisitIndexDtoResultTransformer(), first, max);

		if (!indexList.isEmpty()) {
			Pseudonymizer<VisitIndexDto> pseudonymizer =
				Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			pseudonymizer.pseudonymizeDtoCollection(VisitIndexDto.class, indexList, VisitIndexDto::getInJurisdiction, null);
		}

		return indexList;
	}

	private static List<Order> getOrderList(
		List<SortProperty> sortProperties,
		Root<Visit> visit,
		CriteriaBuilder cb,
		Join<Visit, Symptoms> symptoms,
		CriteriaQuery<Tuple> cq) {
		List<Order> order = new ArrayList<>();
		if (sortProperties != null && !sortProperties.isEmpty()) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case VisitIndexDto.VISIT_DATE_TIME:
				case VisitIndexDto.VISIT_STATUS:
				case VisitIndexDto.DISEASE:
				case VisitIndexDto.ORIGIN:
					expression = visit.get(sortProperty.propertyName);
					break;
				case VisitIndexDto.VISIT_REMARKS:
					expression = cb.lower(visit.get(sortProperty.propertyName));
					break;
				case VisitIndexDto.SYMPTOMATIC:
				case VisitIndexDto.TEMPERATURE:
					expression = symptoms.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		} else {
			order.add(cb.desc(visit.get(Visit.VISIT_DATE_TIME)));
		}

		return order;
	}

	public Page<VisitIndexDto> getIndexPage(VisitCriteria visitCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<VisitIndexDto> visitIndexList = getIndexList(visitCriteria, offset, size, sortProperties);
		long totalElementCount = count(visitCriteria);
		return new Page<>(visitIndexList, offset, size, totalElementCount);
	}

	@Override
	public long count(VisitCriteria visitCriteria) {

		if (visitCriteria == null || visitCriteria.isEmpty()) {
			return 0L; // Retrieving a list count independent of a contact/case is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> root = cq.from(Visit.class);
		cq.where(service.buildCriteriaFilter(visitCriteria, cb, root));
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<VisitExportDto> getVisitsExportList(
		VisitCriteria visitCriteria,
		Collection<String> selectedRows,
		VisitExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitExportDto> cq = cb.createQuery(VisitExportDto.class);
		final Root<Visit> visitRoot = cq.from(Visit.class);

		final VisitQueryContext queryContext = new VisitQueryContext(cb, cq, visitRoot);
		final Join<Visit, Symptoms> symptomsJoin = queryContext.getJoins().getSymptoms();
		final Join<Visit, User> userJoin = queryContext.getJoins().getUser();
		final Join<Visit, Person> personJoin = queryContext.getJoins().getPerson();

		cq.multiselect(
			visitRoot.get(Visit.ID),
			visitRoot.get(Visit.UUID),
			personJoin.get(Person.ID),
			personJoin.get(Person.FIRST_NAME),
			personJoin.get(Person.LAST_NAME),
			symptomsJoin.get(Symptoms.ID),
			userJoin.get(User.ID),
			visitRoot.get(Visit.DISEASE),
			visitRoot.get(Visit.VISIT_DATE_TIME),
			visitRoot.get(Visit.VISIT_STATUS),
			visitRoot.get(Visit.VISIT_REMARKS),
			visitRoot.get(Visit.REPORT_LAT),
			visitRoot.get(Visit.REPORT_LON),
			visitRoot.get(Visit.ORIGIN),
			personJoin.get(Person.UUID),
			jurisdictionSelector(queryContext));

		Predicate filter = service.buildCriteriaFilter(visitCriteria, cb, visitRoot);
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, visitRoot.get(Visit.UUID));
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(visitRoot.get(Visit.VISIT_DATE_TIME)), cb.desc(visitRoot.get(Case.ID)));

		List<VisitExportDto> resultList = QueryHelper.getResultList(em, cq, first, max);

		if (!resultList.isEmpty()) {

			Map<Long, Symptoms> symptoms = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseDataDto.SYMPTOMS)) {
				List<Symptoms> symptomsList = null;
				CriteriaQuery<Symptoms> symptomsCq = cb.createQuery(Symptoms.class);
				Root<Symptoms> symptomsRoot = symptomsCq.from(Symptoms.class);
				Expression<String> symptomsIdsExpr = symptomsRoot.get(Symptoms.ID);
				symptomsCq.where(symptomsIdsExpr.in(resultList.stream().map(VisitExportDto::getSymptomsId).collect(Collectors.toList())));
				symptomsList = em.createQuery(symptomsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				symptoms = symptomsList.stream().collect(Collectors.toMap(Symptoms::getId, Function.identity()));
			}

			if (!resultList.isEmpty()) {

				Pseudonymizer<VisitExportDto> pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
				Set<Long> userIds = resultList.stream().map(VisitExportDto::getVisitUserId).filter(Objects::nonNull).collect(Collectors.toSet());
				Map<Long, UserReference> visitUsers = userIds.isEmpty()
					? null
					: userService.getUserReferencesByIds(userIds).stream().collect(Collectors.toMap(UserReference::getId, Function.identity()));
				for (VisitExportDto exportDto : resultList) {
					boolean inJurisdiction = exportDto.getInJurisdiction();

					UserReference user = visitUsers != null ? visitUsers.get(exportDto.getVisitUserId()) : null;

					if (user != null) {
						exportDto.setVisitUserName(user.getName());
						exportDto.setVisitUserRoles(user.getUserRoles().stream().map(UserRoleFacadeEjb::toReferenceDto).collect(Collectors.toSet()));
					}

					pseudonymizer.pseudonymizeDto(VisitExportDto.class, exportDto, inJurisdiction, null);

					if (symptoms != null) {
						Optional.ofNullable(symptoms.get(exportDto.getSymptomsId()))
							.ifPresent(symptom -> exportDto.setSymptoms(SymptomsFacadeEjb.toSymptomsDto(symptom)));
					}
				}
			}
		}

		return resultList;
	}

	private Expression<Object> jurisdictionSelector(VisitQueryContext queryContext) {
		return JurisdictionHelper.booleanSelector(queryContext.getCriteriaBuilder(), service.inJurisdictionOrOwned(queryContext));
	}

	public Visit fillOrBuildEntity(@NotNull VisitDto source, Visit target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);

		target = DtoHelper.fillOrBuildEntity(source, target, Visit::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getSymptoms(), source.getSymptoms());
		}

		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setSymptoms(symptomsFacade.fillOrBuildEntity(source.getSymptoms(), target.getSymptoms(), checkChangeDate));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(userService.getByReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		target.setOrigin(source.getOrigin());

		return target;
	}

	@Override
	protected void pseudonymizeDto(Visit source, VisitDto visitDto, Pseudonymizer<VisitDto> pseudonymizer, boolean inJurisdiction) {

		if (visitDto != null) {
			pseudonymizer.pseudonymizeDto(VisitDto.class, visitDto, inJurisdiction, null);
		}
	}

	@Override
	protected void restorePseudonymizedDto(VisitDto dto, VisitDto existingDto, Visit existingVisit, Pseudonymizer<VisitDto> pseudonymizer) {

		if (existingDto != null) {
			boolean isInJurisdiction = service.inJurisdictionOrOwned(existingVisit);

			pseudonymizer.restorePseudonymizedValues(VisitDto.class, dto, existingDto, isInJurisdiction);
		}
	}

	@Override
	protected VisitReferenceDto toRefDto(Visit visit) {
		return toReferenceDto(visit);
	}

	public static VisitReferenceDto toReferenceDto(Visit source) {
		return Optional.of(source).map(s -> new VisitReferenceDto(s.getUuid(), s.toString())).orElse(null);
	}

	@Override
	public VisitDto toDto(Visit source) {
		return toVisitDto(source);
	}

	public static VisitDto toVisitDto(Visit source) {
		if (source == null) {
			return null;
		}

		VisitDto target = new VisitDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setSymptoms(SymptomsFacadeEjb.toSymptomsDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(UserFacadeEjb.toReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		target.setOrigin(source.getOrigin());
		return target;
	}

	private void onVisitChanged(VisitDto existingVisit, Visit newVisit) {
		updateContactVisitAssociations(existingVisit, newVisit);
		updateCaseVisitAssociations(existingVisit, newVisit);

		// Email all responsible supervisors when the contact has become symptomatic
		boolean previousSymptomaticStatus = existingVisit != null && Boolean.TRUE.equals(existingVisit.getSymptoms().getSymptomatic());
		if (newVisit.getContacts() != null && !previousSymptomaticStatus && Boolean.TRUE.equals(newVisit.getSymptoms().getSymptomatic())) {
			for (Contact contact : newVisit.getContacts()) {
				// Skip if there is already a symptomatic visit for this contact
				if (contact.getVisits()
					.stream()
					.filter(v -> !v.equals(newVisit))
					.anyMatch(v -> Boolean.TRUE.equals(v.getSymptoms().getSymptomatic()))) {
					continue;
				}

				Case contactCase = contact.getCaze();
				try {
					String messageContent;
					if (contactCase != null) {
						messageContent = String.format(
							I18nProperties.getString(MessageContents.CONTENT_CONTACT_SYMPTOMATIC),
							DataHelper.getShortUuid(contact.getUuid()),
							DataHelper.getShortUuid(contactCase.getUuid()));
					} else {
						messageContent = String.format(
							I18nProperties.getString(MessageContents.CONTENT_CONTACT_WITHOUT_CASE_SYMPTOMATIC),
							DataHelper.getShortUuid(contact.getUuid()));
					}

					notificationService.sendNotifications(
						NotificationType.CONTACT_SYMPTOMATIC,
						JurisdictionHelper.getContactRegions(contact),
						null,
						MessageSubject.CONTACT_SYMPTOMATIC,
						messageContent);
				} catch (NotificationDeliveryFailedException e) {
					logger.error("EmailDeliveryFailedException when trying to notify supervisors about a contact that has become symptomatic.");
				}
			}
		}

		if (newVisit.getContacts() != null) {
			for (Contact contact : newVisit.getContacts()) {
				contactService.updateFollowUpDetails(contact, false);
			}
		}

		if (newVisit.getCaze() != null) {
			// Update case symptoms
			caseFacade.updateSymptomsByVisit(newVisit);
		}
	}

	private void updateContactVisitAssociations(VisitDto existingVisit, Visit visit) {

		if (existingVisit != null && Objects.equals(existingVisit.getVisitDateTime(), visit.getVisitDateTime())) {
			// No need to update the associations
			return;
		}

		Set<Contact> contacts = contactService.getAllRelevantContacts(visit.getPerson(), visit.getDisease(), visit.getVisitDateTime());
		visit.setContacts(contacts);
		for (Contact contact : contacts) {
			contact.getVisits().add(visit);
		}
	}

	private void updateCaseVisitAssociations(VisitDto existingVisit, Visit visit) {

		if (existingVisit != null
			&& Objects.equals(existingVisit.getVisitDateTime(), visit.getVisitDateTime())
			&& existingVisit.getPerson().equals(visit.getPerson())) {
			// No need to update the associations
			return;
		}

		Case caze = caseService.getRelevantCaseForFollowUp(visit.getPerson(), visit.getDisease(), visit.getVisitDateTime());
		visit.setCaze(caze);
		if (caze != null) {
			caze.getVisits().add(visit);
		}
	}

	@LocalBean
	@Stateless
	public static class VisitFacadeEjbLocal extends VisitFacadeEjb {

		public VisitFacadeEjbLocal() {
		}

		@Inject
		protected VisitFacadeEjbLocal(VisitService service) {
			super(service);
		}

	}
}
