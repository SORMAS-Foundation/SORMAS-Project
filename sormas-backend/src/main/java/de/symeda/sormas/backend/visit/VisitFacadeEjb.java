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
package de.symeda.sormas.backend.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.api.visit.VisitCaseJurisdictionDto;
import de.symeda.sormas.api.visit.VisitContactJurisdictionDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJurisdictionChecker;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "VisitFacade")
public class VisitFacadeEjb implements VisitFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

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
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@EJB
	private ContactJurisdictionChecker contactJurisdictionChecker;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return visitService.getAllActiveUuids(user);
	}

	/**
	 * Attention: For now this only returns the visits of contacts, since case visits are not yet implemented in the mobile app
	 */
	@Override
	public List<VisitDto> getAllActiveVisitsAfter(Date date) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return visitService.getAllActiveVisitsAfter(date).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<VisitDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return visitService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public VisitDto getLastVisitByContact(ContactReferenceDto contactRef) {
		Contact contact = contactService.getByReferenceDto(contactRef);
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

		return convertToDto(contact.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null), pseudonymizer);
	}

	@Override
	public List<VisitDto> getVisitsByContactAndPeriod(ContactReferenceDto contactRef, Date begin, Date end) {
		Contact contact = contactService.getByReferenceDto(contactRef);
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

		return contact.getVisits()
			.stream()
			.filter(visit -> visit.getVisitDateTime().after(begin) && visit.getVisitDateTime().before(end))
			.map(visit -> convertToDto(visit, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public VisitDto getLastVisitByCase(CaseReferenceDto caseRef) {
		Case caze = caseService.getByReferenceDto(caseRef);
		return toDto(caze.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null));
	}

	@Override
	public VisitDto getVisitByUuid(String uuid) {
		return convertToDto(visitService.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public VisitDto saveVisit(@Valid VisitDto dto) {
		final String visitUuid = dto.getUuid();
		final Visit existingVisit = visitUuid != null ? visitService.getByUuid(visitUuid) : null;
		final VisitDto existingDto = toDto(existingVisit);

		restorePseudonymizedDto(dto, existingVisit, existingDto);

		this.validate(dto);

		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());
		Visit entity = fromDto(dto, true);

		visitService.ensurePersisted(entity);

		onVisitChanged(existingDto, entity);

		return convertToDto(entity, Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public ExternalVisitDto saveExternalVisit(final ExternalVisitDto dto) {

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

		saveVisit(visitDto);

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

	@Override
	public void validate(VisitDto visit) {

		if (visit.getVisitStatus() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitStatus));
		}
		if (visit.getSymptoms() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitSymptoms));
		}
		if (visit.getVisitDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.visitDate));
		}
		if (visit.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		if (visit.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
	}

	@Override
	public void deleteVisit(String visitUuid) {

		if (!userService.hasRight(UserRight.VISIT_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete visits.");
		}

		Visit visit = visitService.getByUuid(visitUuid);
		visitService.delete(visit);
	}

	@Override
	public List<VisitIndexDto> getIndexList(VisitCriteria visitCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		if (visitCriteria == null || visitCriteria.isEmpty()) {
			return new ArrayList<>(); // Retrieving an index list independent of a contact/case is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<VisitIndexDto> cq = cb.createQuery(VisitIndexDto.class);
		Root<Visit> visit = cq.from(Visit.class);
		Join<Visit, Symptoms> symptoms = visit.join(Visit.SYMPTOMS, JoinType.LEFT);

		cq.multiselect(
			visit.get(Visit.ID),
			visit.get(Visit.UUID),
			visit.get(Visit.VISIT_DATE_TIME),
			visit.get(Visit.VISIT_STATUS),
			visit.get(Visit.VISIT_REMARKS),
			visit.get(Visit.DISEASE),
			symptoms.get(Symptoms.SYMPTOMATIC),
			symptoms.get(Symptoms.TEMPERATURE),
			symptoms.get(Symptoms.TEMPERATURE_SOURCE),
			visit.get(Visit.ORIGIN));

		cq.where(visitService.buildCriteriaFilter(visitCriteria, cb, visit));

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case VisitIndexDto.VISIT_DATE_TIME:
				case VisitIndexDto.VISIT_STATUS:
				case VisitIndexDto.VISIT_REMARKS:
				case VisitIndexDto.DISEASE:
				case VisitIndexDto.ORIGIN:
					expression = visit.get(sortProperty.propertyName);
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
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(visit.get(Visit.VISIT_DATE_TIME)));
		}

		final List<VisitIndexDto> indexList;
		if (first != null && max != null) {
			indexList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			indexList = em.createQuery(cq).getResultList();
		}

		if (indexList.size() > 0) {

			List<Long> visitIds = indexList.stream().map(VisitIndexDto::getId).collect(Collectors.toList());
			Map<Long, List<VisitContactJurisdictionDto>> contactJurisdictions = getVisitContactJurisdictions(visitIds);
			Map<Long, VisitCaseJurisdictionDto> caseJurisdictions = getVisitCaseJurisdictions(visitIds);

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));

			indexList.forEach(visitIndex -> {

				VisitContactJurisdictionDto anyContactJurisdiction = null;
				VisitContactJurisdictionDto matchingContactJurisdiction = null;

				// check contact jurisdictions
				if (contactJurisdictions.containsKey(visitIndex.getId())) {
					List<VisitContactJurisdictionDto> visitContactJurisdictions = contactJurisdictions.get(visitIndex.getId());
					matchingContactJurisdiction = visitContactJurisdictions.stream()
						.filter(c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c))
						.findFirst()
						.orElse(null);
					if (matchingContactJurisdiction == null) {
						anyContactJurisdiction = visitContactJurisdictions.get(0); // keep for later
					}
				}

				// no allow jurisdiction found? -> check case jurisdictions
				if (matchingContactJurisdiction == null && caseJurisdictions.containsKey(visitIndex.getId())) {
					VisitCaseJurisdictionDto visitCaseJurisdiction = caseJurisdictions.get(visitIndex.getId());
					if (caseJurisdictionChecker.isInJurisdictionOrOwned(visitCaseJurisdiction)) {
						// wrap the VisitCaseJurisdictionDto in a VisitContactJurisdictionDto to make the UI part easier
						matchingContactJurisdiction =
							new VisitContactJurisdictionDto(visitCaseJurisdiction.getVisitId(), null, null, null, null, visitCaseJurisdiction);
					} else if (anyContactJurisdiction == null) {
						anyContactJurisdiction =
							new VisitContactJurisdictionDto(visitCaseJurisdiction.getVisitId(), null, null, null, null, visitCaseJurisdiction);
					}
				}

				boolean inJurisdiction = matchingContactJurisdiction != null;
				visitIndex.setJurisdiction(matchingContactJurisdiction != null ? matchingContactJurisdiction : anyContactJurisdiction);

				pseudonymizer.pseudonymizeDto(VisitIndexDto.class, visitIndex, inJurisdiction, null);
			});
		}

		return indexList;
	}

	@Override
	public long count(VisitCriteria visitCriteria) {

		if (visitCriteria == null || visitCriteria.isEmpty()) {
			return 0L; // Retrieving a list count independent of a contact/case is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> root = cq.from(Visit.class);
		cq.where(visitService.buildCriteriaFilter(visitCriteria, cb, root));
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<VisitExportDto> getVisitsExportList(
		VisitCriteria visitCriteria,
		VisitExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitExportDto> cq = cb.createQuery(VisitExportDto.class);
		final Root<Visit> visitRoot = cq.from(Visit.class);
		final Join<Visit, Symptoms> symptomsJoin = visitRoot.join(Visit.SYMPTOMS, JoinType.LEFT);
		final Join<Visit, Person> personJoin = visitRoot.join(Visit.PERSON, JoinType.LEFT);
		final Join<Visit, User> userJoin = visitRoot.join(Visit.VISIT_USER, JoinType.LEFT);

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
			personJoin.get(Person.UUID));

		Predicate filter = visitService.buildCriteriaFilter(visitCriteria, cb, visitRoot);
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(visitRoot.get(Visit.VISIT_DATE_TIME)), cb.desc(visitRoot.get(Case.ID)));

		List<VisitExportDto> resultList =
			em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).setFirstResult(first).setMaxResults(max).getResultList();

		if (!resultList.isEmpty()) {

			Map<Long, Symptoms> symptoms = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseDataDto.SYMPTOMS)) {
				List<Symptoms> symptomsList = null;
				CriteriaQuery<Symptoms> symptomsCq = cb.createQuery(Symptoms.class);
				Root<Symptoms> symptomsRoot = symptomsCq.from(Symptoms.class);
				Expression<String> symptomsIdsExpr = symptomsRoot.get(Symptoms.ID);
				symptomsCq.where(symptomsIdsExpr.in(resultList.stream().map(VisitExportDto::getSymptomsId).collect(Collectors.toList())));
				symptomsList = em.createQuery(symptomsCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				symptoms = symptomsList.stream().collect(Collectors.toMap(Symptoms::getId, Function.identity()));
			}

			if (resultList.size() > 0) {
				List<Long> visitIds = resultList.stream().map(VisitExportDto::getId).collect(Collectors.toList());
				Map<Long, List<VisitContactJurisdictionDto>> jurisdictions = getVisitContactJurisdictions(visitIds);
				Map<Long, VisitCaseJurisdictionDto> caseJurisdictions = getVisitCaseJurisdictions(visitIds);

				Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
				for (VisitExportDto exportDto : resultList) {
					List<VisitContactJurisdictionDto> visitContactJurisdictions = jurisdictions.get(exportDto.getId());
					boolean inJurisdiction = (visitContactJurisdictions != null
						&& visitContactJurisdictions.stream().anyMatch(c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c)))
						|| (caseJurisdictions.containsKey(exportDto.getId())
							&& caseJurisdictionChecker.isInJurisdictionOrOwned(caseJurisdictions.get(exportDto.getId())));

					pseudonymizer.pseudonymizeDto(VisitExportDto.class, exportDto, inJurisdiction, v -> {
						if (v.getSymptoms() != null) {
							pseudonymizer.pseudonymizeDto(SymptomsDto.class, v.getSymptoms(), inJurisdiction, null);
						}
					});

					if (symptoms != null) {
						Optional.ofNullable(symptoms.get(exportDto.getSymptomsId()))
							.ifPresent(symptom -> exportDto.setSymptoms(SymptomsFacadeEjb.toDto(symptom)));
					}
				}
			}
		}

		return resultList;
	}

	public Visit fromDto(@NotNull VisitDto source, boolean checkChangeDate) {

		final String visitUuid = source.getUuid();
		Visit target = DtoHelper.fillOrBuildEntity(source, visitService.getByUuid(visitUuid), Visit::new, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms(), checkChangeDate));
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

	public VisitDto convertToDto(Visit source, Pseudonymizer pseudonymizer) {
		VisitDto visitDto = toDto(source);

		pseudonymizeDto(source, visitDto, pseudonymizer);

		return visitDto;
	}

	private void pseudonymizeDto(Visit source, VisitDto visitDto, Pseudonymizer pseudonymizer) {
		if (visitDto != null) {
			boolean isInJurisdiction = source.getContacts().stream().anyMatch(c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c))
				|| (source.getCaze() != null && caseJurisdictionChecker.isInJurisdictionOrOwned(source.getCaze()));

			pseudonymizer.pseudonymizeDto(VisitDto.class, visitDto, isInJurisdiction, (v) -> {
				pseudonymizer.pseudonymizeDto(PersonReferenceDto.class, visitDto.getPerson(), isInJurisdiction, null);
				pseudonymizer.pseudonymizeDto(SymptomsDto.class, visitDto.getSymptoms(), isInJurisdiction, null);
			});
		}
	}

	private void restorePseudonymizedDto(VisitDto dto, Visit existingVisit, VisitDto existingDto) {
		if (existingDto != null) {
			boolean isInJurisdiction = existingVisit.getContacts().stream().anyMatch(c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c))
				|| (existingVisit.getCaze() != null && caseJurisdictionChecker.isInJurisdictionOrOwned(existingVisit.getCaze()));

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.restorePseudonymizedValues(VisitDto.class, dto, existingDto, isInJurisdiction);
			pseudonymizer.restorePseudonymizedValues(SymptomsDto.class, dto.getSymptoms(), existingDto.getSymptoms(), isInJurisdiction);
		}
	}

	public static VisitReferenceDto toReferenceDto(Visit source) {

		if (source == null) {
			return null;
		}

		VisitReferenceDto target = new VisitReferenceDto(source.getUuid(), source.toString());
		return target;
	}

	public static VisitDto toDto(Visit source) {

		if (source == null) {
			return null;
		}

		VisitDto target = new VisitDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
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

		// Send an email to all responsible supervisors when the contact has become
		// symptomatic
		boolean previousSymptomaticStatus = existingVisit != null && Boolean.TRUE.equals(existingVisit.getSymptoms().getSymptomatic());
		if (newVisit.getContacts() != null && previousSymptomaticStatus == false && Boolean.TRUE.equals(newVisit.getSymptoms().getSymptomatic())) {
			for (Contact contact : newVisit.getContacts()) {
				// Skip if there is already a symptomatic visit for this contact
				if (contact.getVisits()
					.stream()
					.filter(v -> !v.equals(newVisit))
					.filter(v -> Boolean.TRUE.equals(v.getSymptoms().getSymptomatic()))
					.count()
					> 0) {
					continue;
				}

				Case contactCase = contact.getCaze();
				List<User> messageRecipients = userService.getAllByRegionAndUserRoles(
					contact.getRegion() != null ? contact.getRegion() : contactCase.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR,
					UserRole.CONTACT_SUPERVISOR);
				for (User recipient : messageRecipients) {
					try {
						String messageContent;
						if (contactCase != null) {
							messageContent = String.format(
								I18nProperties.getString(MessagingService.CONTENT_CONTACT_SYMPTOMATIC),
								DataHelper.getShortUuid(contact.getUuid()),
								DataHelper.getShortUuid(contactCase.getUuid()));
						} else {
							messageContent = String.format(
								I18nProperties.getString(MessagingService.CONTENT_CONTACT_WITHOUT_CASE_SYMPTOMATIC),
								DataHelper.getShortUuid(contact.getUuid()));
						}

						messagingService
							.sendMessage(recipient, MessageSubject.CONTACT_SYMPTOMATIC, messageContent, MessageType.EMAIL, MessageType.SMS);
					} catch (NotificationDeliveryFailedException e) {
						logger.error(
							String.format(
								"EmailDeliveryFailedException when trying to notify supervisors about a contact that has become symptomatic. "
									+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
								recipient.getUuid()));
					}
				}
			}
		}

		if (newVisit.getContacts() != null) {
			for (Contact contact : newVisit.getContacts()) {
				contactService.updateFollowUpUntilAndStatus(contact);
			}
		}

		if (newVisit.getCaze() != null) {
			// Update case symptoms
			CaseDataDto caze = CaseFacadeEjb.toDto(newVisit.getCaze());
			SymptomsDto caseSymptoms = caze.getSymptoms();
			SymptomsHelper.updateSymptoms(toDto(newVisit).getSymptoms(), caseSymptoms);
			caseFacade.saveCase(caze);
		}
	}

	private void updateContactVisitAssociations(VisitDto existingVisit, Visit visit) {

		if (existingVisit != null && existingVisit.getVisitDateTime() == visit.getVisitDateTime()) {
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
			&& existingVisit.getVisitDateTime() == visit.getVisitDateTime()
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

	private Map<Long, List<VisitContactJurisdictionDto>> getVisitContactJurisdictions(List<Long> visitIds) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitContactJurisdictionDto> cq = cb.createQuery(VisitContactJurisdictionDto.class);
		final Root<Visit> visitRoot = cq.from(Visit.class);
		VisitJoins joins = new VisitJoins(visitRoot, JoinType.INNER);

		cq.multiselect(
			visitRoot.get(Visit.ID),
			joins.getContactReportingUser().get(User.UUID),
			joins.getContactRegion().get(Region.UUID),
			joins.getContactDistrict().get(District.UUID),
			joins.getContactCommunity().get(Community.UUID),
			joins.getContactCaseReportingUser().get(User.UUID),
			joins.getContactCaseRegion().get(Region.UUID),
			joins.getContactCaseDistrict().get(District.UUID),
			joins.getContactCaseCommunity().get(Community.UUID),
			joins.getContactCaseHealthFacility().get(Facility.UUID),
			joins.getContactCasePointOfEntry().get(PointOfEntry.UUID));

		cq.where(visitRoot.get(Visit.ID).in(visitIds));
		cq.orderBy(cb.desc(visitRoot.get(Visit.VISIT_DATE_TIME)), cb.desc(visitRoot.get(Case.ID)));

		List<VisitContactJurisdictionDto> jurisdictions = em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();

		return jurisdictions.stream().collect(Collectors.groupingBy(VisitContactJurisdictionDto::getVisitId));
	}

	private Map<Long, VisitCaseJurisdictionDto> getVisitCaseJurisdictions(List<Long> visitIds) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitCaseJurisdictionDto> cq = cb.createQuery(VisitCaseJurisdictionDto.class);
		final Root<Visit> visitRoot = cq.from(Visit.class);
		VisitJoins joins = new VisitJoins(visitRoot, JoinType.INNER);

		cq.multiselect(
			visitRoot.get(Visit.ID),
			joins.getCaseReportingUser().get(User.UUID),
			joins.getCaseRegion().get(Region.UUID),
			joins.getCaseDistrict().get(District.UUID),
			joins.getCaseCommunity().get(Community.UUID),
			joins.getCaseHealthFacility().get(Facility.UUID),
			joins.getCasePointOfEntry().get(PointOfEntry.UUID));

		cq.where(visitRoot.get(Visit.ID).in(visitIds));
		cq.orderBy(cb.desc(visitRoot.get(Visit.VISIT_DATE_TIME)), cb.desc(visitRoot.get(Case.ID)));

		List<VisitCaseJurisdictionDto> jurisdictions = em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();

		return jurisdictions.stream().collect(Collectors.toMap(j -> j.getVisitId(), j -> j));
	}

	@LocalBean
	@Stateless
	public static class VisitFacadeEjbLocal extends VisitFacadeEjb {

	}
}
