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

package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "PathogenTestFacade")
public class PathogenTestFacadeEjb implements PathogenTestFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private SampleService sampleService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private NotificationService notificationService;

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return pathogenTestService.getAllActiveUuids(user);
	}

	@Override
	public List<PathogenTestDto> getAllActivePathogenTestsAfter(Date date) {
		return getAllActivePathogenTestsAfter(date, null, null);
	}

	@Override
	public List<PathogenTestDto> getAllActivePathogenTestsAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getAllActivePathogenTestsAfter(date, user, batchSize, lastSynchronizedUuid)
			.stream()
			.map(p -> convertToDto(p, pseudonymizer))
			.collect(Collectors.toList());
	}

	public List<PathogenTestDto> getIndexList(
		PathogenTestCriteria pathogenTestCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getIndexList(pathogenTestCriteria, first, max, sortProperties)
			.stream()
			.map(p -> convertToDto(p, pseudonymizer))
			.collect(Collectors.toList());

	}

	public Page<PathogenTestDto> getIndexPage(
		PathogenTestCriteria pathogenTestCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {

		List<PathogenTestDto> pathogenTestList = getIndexList(pathogenTestCriteria, offset, size, sortProperties);
		long totalElementCount = pathogenTestService.count(pathogenTestCriteria);
		return new Page<>(pathogenTestList, offset, size, totalElementCount);

	}

	@Override
	public List<PathogenTestDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<PathogenTestDto> getBySampleUuids(List<String> sampleUuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getBySampleUuids(sampleUuids, false)
			.stream()
			.map(p -> convertToDto(p, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public PathogenTestDto getLatestPathogenTest(String sampleUuid) {
		if (sampleUuid == null) {
			return null;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(PathogenTest.class);
		Root<PathogenTest> pathogenTestRoot = cq.from(PathogenTest.class);
		Join<PathogenTest, Sample> sampleJoin = pathogenTestRoot.join(PathogenTest.SAMPLE);

		Predicate filter = cb.and(cb.equal(sampleJoin.get(Sample.UUID), sampleUuid), cb.isFalse(pathogenTestRoot.get(CoreAdo.DELETED)));
		cq.where(filter);
		cq.orderBy(cb.desc(pathogenTestRoot.get(PathogenTest.CREATION_DATE)));

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return QueryHelper.getFirstResult(em, cq, t -> convertToDto(t, pseudonymizer));
	}

	@Override
	public List<PathogenTestDto> getAllBySample(SampleReferenceDto sampleRef) {
		if (sampleRef == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return sampleService.getByUuid(sampleRef.getUuid())
			.getPathogenTests()
			.stream()
			.filter(p -> !p.isDeleted())
			.map(p -> convertToDto(p, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return pathogenTestService.getDeletedUuidsSince(since);
	}

	@Override
	public PathogenTestDto getByUuid(String uuid) {
		return convertToDto(pathogenTestService.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public PathogenTestDto savePathogenTest(@Valid PathogenTestDto dto) {
		return savePathogenTest(dto, true, true);
	}

	public PathogenTestDto savePathogenTest(@Valid PathogenTestDto dto, boolean checkChangeDate, boolean syncShares) {
		PathogenTest existingSampleTest = pathogenTestService.getByUuid(dto.getUuid());
		PathogenTestDto existingSampleTestDto = toDto(existingSampleTest);

		restorePseudonymizedDto(dto, existingSampleTest, existingSampleTestDto);

		PathogenTest pathogenTest = fromDto(dto, checkChangeDate);
		pathogenTestService.ensurePersisted(pathogenTest);

		onPathogenTestChanged(existingSampleTestDto, pathogenTest);

		handleAssotiatedObjectChanges(pathogenTest, syncShares);

		return convertToDto(pathogenTest, Pseudonymizer.getDefault(userService::hasRight));
	}

	private void handleAssotiatedObjectChanges(PathogenTest pathogenTest, boolean syncShares) {
		// Update case classification if necessary
		final Case associatedCase = pathogenTest.getSample().getAssociatedCase();
		if (associatedCase != null) {
			caseFacade.onCaseChanged(caseFacade.toDto(associatedCase), associatedCase, syncShares);
		}

		// update contact if necessary
		Contact associatedContact = pathogenTest.getSample().getAssociatedContact();
		if (associatedContact != null) {
			contactFacade.onContactChanged(contactFacade.toDto(associatedContact), syncShares);
		}

		// update event participant if necessary
		EventParticipant associatedEventParticipant = pathogenTest.getSample().getAssociatedEventParticipant();
		if (associatedEventParticipant != null) {
			eventParticipantFacade.onEventParticipantChanged(
				eventFacade.toDto(associatedEventParticipant.getEvent()),
				eventParticipantFacade.toDto(associatedEventParticipant),
				associatedEventParticipant,
				syncShares);
		}
	}

	@Override
	public void deletePathogenTest(String pathogenTestUuid) {
		User user = userService.getCurrentUser();
		if (!UserRole.getUserRights(user.getUserRoles()).contains(UserRight.PATHOGEN_TEST_DELETE)) {
			throw new UnsupportedOperationException("User " + user.getUuid() + " is not allowed to delete pathogen " + "tests.");
		}

		PathogenTest pathogenTest = pathogenTestService.getByUuid(pathogenTestUuid);
		pathogenTestService.delete(pathogenTest);

		handleAssotiatedObjectChanges(pathogenTest, true);
	}

	@Override
	public boolean hasPathogenTest(SampleReferenceDto sample) {
		Sample sampleEntity = sampleService.getByReferenceDto(sample);
		return pathogenTestService.hasPathogenTest(sampleEntity);
	}

	@Override
	public void validate(PathogenTestDto pathogenTest) throws ValidationRuntimeException {
		if (pathogenTest.getSample() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validSample));
		}
		if (pathogenTest.getTestType() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE)));
		}
		if (pathogenTest.getTestedDisease() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE)));
		}
		if (pathogenTest.getLab() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.LAB)));
		}
		if (pathogenTest.getTestResult() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT)));
		}
		if (pathogenTest.getTestResultVerified() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED)));
		}
	}

	@Override
	public Date getLatestPathogenTestDate(String sampleUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<PathogenTest> pathogenTestRoot = cq.from(PathogenTest.class);
		Join<PathogenTest, Sample> sampleJoin = pathogenTestRoot.join(PathogenTest.SAMPLE);

		Predicate filter = cb.equal(sampleJoin.get(Sample.UUID), sampleUuid);
		cq.where(filter);
		cq.orderBy(cb.desc(pathogenTestRoot.get(PathogenTest.TEST_DATE_TIME)));
		cq.select(pathogenTestRoot.get(PathogenTest.TEST_DATE_TIME));

		return QueryHelper.getFirstResult(em, cq);
	}

	public List<PathogenTestDto> getPositiveOrLatest(List<String> sampleUuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getBySampleUuids(sampleUuids, true)
			.stream()
			.collect(
				Collectors.toMap(
					s -> s.getSample().getUuid(),
					s -> s,
					(s1, s2) -> {

						// keep the positive one
						if (s1.getTestResult() == PathogenTestResultType.POSITIVE) {
							return s1;
						} else if (s2.getTestResult() == PathogenTestResultType.POSITIVE) {
							return s2;
						}

						// ordered by creation date by default, so always keep the first one
						return s1;
					}))
			.values()
			.stream()
			.map(s -> convertToDto(s, pseudonymizer))
			.collect(Collectors.toList());
	}

	public static PathogenTestDto toDto(PathogenTest source) {
		if (source == null) {
			return null;
		}

		PathogenTestDto target = new PathogenTestDto();
		DtoHelper.fillDto(target, source);

		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setTypingId(source.getTypingId());
		target.setTestType(source.getTestType());
		target.setPcrTestSpecification(source.getPcrTestSpecification());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestDateTime(source.getTestDateTime());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setLabUser(UserFacadeEjb.toReferenceDto(source.getLabUser()));
		target.setTestResult(source.getTestResult());
		target.setTestResultText(source.getTestResultText());
		target.setTestResultVerified(source.getTestResultVerified());
		target.setFourFoldIncreaseAntibodyTiter(source.isFourFoldIncreaseAntibodyTiter());
		target.setSerotype(source.getSerotype());
		target.setCqValue(source.getCqValue());
		target.setReportDate(source.getReportDate());
		target.setViaLims(source.isViaLims());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setPreliminary(source.getPreliminary());

		return target;
	}

	public PathogenTestDto convertToDto(PathogenTest source, Pseudonymizer pseudonymizer) {
		PathogenTestDto target = toDto(source);

		pseudonymizeDto(source, target, pseudonymizer);

		return target;
	}

	private void pseudonymizeDto(PathogenTest source, PathogenTestDto target, Pseudonymizer pseudonymizer) {
		if (source != null && target != null) {
			pseudonymizer
				.pseudonymizeDto(PathogenTestDto.class, target, sampleService.inJurisdictionOrOwned(source.getSample()).getInJurisdiction(), null);
		}
	}

	private void restorePseudonymizedDto(PathogenTestDto dto, PathogenTest existingSampleTest, PathogenTestDto existingSampleTestDto) {
		if (existingSampleTestDto != null) {
			boolean isInJurisdiction = sampleService.inJurisdictionOrOwned(existingSampleTest.getSample()).getInJurisdiction();
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.restorePseudonymizedValues(PathogenTestDto.class, dto, existingSampleTestDto, isInJurisdiction);
		}
	}

	public PathogenTest fromDto(@NotNull PathogenTestDto source, boolean checkChangeDate) {
		PathogenTest target =
			DtoHelper.fillOrBuildEntity(source, pathogenTestService.getByUuid(source.getUuid()), PathogenTest::new, checkChangeDate);

		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setTypingId(source.getTypingId());
		target.setTestType(source.getTestType());
		target.setPcrTestSpecification(source.getPcrTestSpecification());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestDateTime(source.getTestDateTime());
		target.setLab(facilityService.getByReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setLabUser(userService.getByReferenceDto(source.getLabUser()));
		target.setTestResult(source.getTestResult());
		target.setTestResultText(source.getTestResultText());
		target.setTestResultVerified(source.getTestResultVerified());
		target.setFourFoldIncreaseAntibodyTiter(source.isFourFoldIncreaseAntibodyTiter());
		target.setSerotype(source.getSerotype());
		target.setCqValue(source.getCqValue());
		target.setReportDate(source.getReportDate());
		target.setViaLims(source.isViaLims());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setPreliminary(source.getPreliminary());

		return target;
	}

	private void onPathogenTestChanged(PathogenTestDto existingPathogenTest, PathogenTest newPathogenTest) {
		// Send an email to all responsible supervisors when a new non-pending sample test is created or the status of
		// a formerly pending test result has changed
		final String sampleUuid = newPathogenTest.getSample().getUuid();
		final Sample sample = sampleService.getByUuid(sampleUuid);
		final Case caze = sample.getAssociatedCase();
		final Contact contact = sample.getAssociatedContact();
		final EventParticipant eventParticipant = sample.getAssociatedEventParticipant();

		Disease disease = null;
		Set<NotificationType> notificationTypes = new HashSet<>();
		List<Region> regions = new ArrayList<>();

		if (caze != null) {
			disease = caze.getDisease();
			notificationTypes.add(NotificationType.CASE_LAB_RESULT_ARRIVED);
			regions.addAll(JurisdictionHelper.getCaseRegions(caze));
		}

		if (contact != null) {
			disease = contact.getDisease() != null ? contact.getDisease() : contact.getCaze().getDisease();
			notificationTypes.add(NotificationType.CONTACT_LAB_RESULT_ARRIVED);
			regions.addAll(JurisdictionHelper.getContactRegions(contact));
		}

		if (eventParticipant != null) {
			disease = eventParticipant.getEvent().getDisease();
			notificationTypes.add(NotificationType.EVENT_PARTICIPANT_LAB_RESULT_ARRIVED);
			regions.add(eventParticipant.getRegion());

			if (disease == null) {
				sendMessageOnPathogenTestChanged(
					existingPathogenTest,
					newPathogenTest,
					null,
					notificationTypes,
					regions,
					MessageContents.CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT_NO_DISEASE,
					DataHelper.getShortUuid(eventParticipant.getUuid()));
			}
		}

		if (disease != null) {
			final String contentLabResultArrived = caze != null
				? MessageContents.CONTENT_LAB_RESULT_ARRIVED
				: contact != null ? MessageContents.CONTENT_LAB_RESULT_ARRIVED_CONTACT : MessageContents.CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT;

			final String shortUuid =
				DataHelper.getShortUuid(caze != null ? caze.getUuid() : contact != null ? contact.getUuid() : eventParticipant.getUuid());
			sendMessageOnPathogenTestChanged(
				existingPathogenTest,
				newPathogenTest,
				disease,
				notificationTypes,
				regions,
				contentLabResultArrived,
				shortUuid);
		}
	}

	private void sendMessageOnPathogenTestChanged(
		PathogenTestDto existingPathogenTest,
		PathogenTest newPathogenTest,
		Disease disease,
		Set<NotificationType> notificationTypes,
		List<Region> regions,
		String contentLabResultArrived,
		String shortUuid) {
		boolean isNewTestWithResult = existingPathogenTest == null && newPathogenTest.getTestResult() != PathogenTestResultType.PENDING;
		boolean testResultChanged = existingPathogenTest != null
			&& existingPathogenTest.getTestResult() == PathogenTestResultType.PENDING
			&& newPathogenTest.getTestResult() != PathogenTestResultType.PENDING;
		if (newPathogenTest.getTestResult() != null && isNewTestWithResult || testResultChanged) {
			try {
				String message = String.format(
					I18nProperties.getString(contentLabResultArrived),
					newPathogenTest.getTestResult().toString(),
					disease,
					shortUuid,
					newPathogenTest.getTestType(),
					newPathogenTest.getTestedDisease());

				notificationService.sendNotifications(notificationTypes, regions, null, MessageSubject.LAB_RESULT_ARRIVED, message);
			} catch (NotificationDeliveryFailedException e) {
				logger.error("EmailDeliveryFailedException when trying to notify supervisors " + "about the arrival of a lab result.");
			}
		}
	}

	@LocalBean
	@Stateless
	public static class PathogenTestFacadeEjbLocal extends PathogenTestFacadeEjb {
	}
}
