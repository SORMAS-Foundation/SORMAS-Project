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
import java.util.Collection;
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
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
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
import de.symeda.sormas.api.utils.fieldaccess.checkers.AnnotationBasedFieldAccessChecker.SpecialAccessCheck;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleFacadeEjb;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleService;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

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
	private EnvironmentSampleService environmentSampleService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private NotificationService notificationService;
	@EJB
	private CountryService countryService;
	@EJB
	private SpecialCaseAccessService specialCaseAccessService;

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

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(pathogenTestService.getAllAfter(date, batchSize, lastSynchronizedUuid));
	}

	private List<PathogenTestDto> toPseudonymizedDtos(List<PathogenTest> entities) {

		List<Long> inJurisdictionIds = pathogenTestService.getInJurisdictionIds(entities);
		Pseudonymizer<PathogenTestDto> pseudonymizer = createPseudonymizer(entities);

		return entities.stream().map(p -> convertToDto(p, pseudonymizer, inJurisdictionIds.contains(p.getId()))).collect(Collectors.toList());
	}

	public List<PathogenTestDto> getIndexList(
		PathogenTestCriteria pathogenTestCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return toPseudonymizedDtos(pathogenTestService.getIndexList(pathogenTestCriteria, first, max, sortProperties));
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

		return toPseudonymizedDtos(pathogenTestService.getByUuids(uuids));
	}

	@Override
	public List<PathogenTestDto> getBySampleUuids(List<String> sampleUuids) {

		return toPseudonymizedDtos(pathogenTestService.getBySampleUuids(sampleUuids, false));
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

		return QueryHelper.getFirstResult(em, cq, this::convertToDto);
	}

	@Override
	public List<PathogenTestDto> getAllBySample(SampleReferenceDto sampleRef) {

		if (sampleRef == null) {
			return Collections.emptyList();
		}

		List<PathogenTest> entities =
			sampleService.getByUuid(sampleRef.getUuid()).getPathogenTests().stream().filter(p -> !p.isDeleted()).collect(Collectors.toList());
		return toPseudonymizedDtos(entities);
	}

	@Override
	public List<PathogenTestDto> getAllByEnvironmentSample(EnvironmentSampleReferenceDto environmentSampleRef) {
		if (environmentSampleRef == null) {
			return Collections.emptyList();
		}

		List<PathogenTest> entities = environmentSampleService.getByUuid(environmentSampleRef.getUuid())
			.getPathogenTests()
			.stream()
			.filter(p -> !p.isDeleted())
			.collect(Collectors.toList());
		return toPseudonymizedDtos(entities);
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
		return convertToDto(pathogenTestService.getByUuid(uuid));
	}

	@Override
	public PathogenTestDto savePathogenTest(@Valid PathogenTestDto dto) {
		return savePathogenTest(dto, true, true);
	}

	public static PathogenTestDto toDto(PathogenTest source) {
		if (source == null) {
			return null;
		}

		PathogenTestDto target = new PathogenTestDto();
		DtoHelper.fillDto(target, source);

		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setEnvironmentSample(EnvironmentSampleFacadeEjb.toReferenceDto(source.getEnvironmentSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setTestedPathogen(source.getTestedPathogen());
		target.setTestedPathogenDetails(source.getTestedPathogenDetails());
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
		target.setCtValueE(source.getCtValueE());
		target.setCtValueN(source.getCtValueN());
		target.setCtValueRdrp(source.getCtValueRdrp());
		target.setCtValueS(source.getCtValueS());
		target.setCtValueOrf1(source.getCtValueOrf1());
		target.setCtValueRdrpS(source.getCtValueRdrpS());
		target.setReportDate(source.getReportDate());
		target.setViaLims(source.isViaLims());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setPreliminary(source.getPreliminary());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		target.setPrescriberPhysicianCode(source.getPrescriberPhysicianCode());
		target.setPrescriberFirstName(source.getPrescriberFirstName());
		target.setPrescriberLastName(source.getPrescriberLastName());
		target.setPrescriberPhoneNumber(source.getPrescriberPhoneNumber());
		target.setPrescriberAddress(source.getPrescriberAddress());
		target.setPrescriberPostalCode(source.getPrescriberPostalCode());
		target.setPrescriberCity(source.getPrescriberCity());
		target.setPrescriberCountry(CountryFacadeEjb.toReferenceDto(source.getPrescriberCountry()));

		return target;
	}

	private void handleAssociatedEntityChanges(PathogenTest pathogenTest, boolean syncShares) {

		if (pathogenTest.getSample() == null) {
			return;
		}

		// Update case classification if necessary
		final Case associatedCase = pathogenTest.getSample().getAssociatedCase();
		if (associatedCase != null && userService.hasRight(UserRight.CASE_EDIT)) {
			caseFacade.onCaseChanged(caseFacade.toDto(associatedCase), associatedCase, syncShares);
		}

		// update contact if necessary
		Contact associatedContact = pathogenTest.getSample().getAssociatedContact();
		if (associatedContact != null && userService.hasRight(UserRight.CONTACT_EDIT)) {
			contactFacade.onContactChanged(contactFacade.toDto(associatedContact), syncShares);
		}

		// update event participant if necessary
		EventParticipant associatedEventParticipant = pathogenTest.getSample().getAssociatedEventParticipant();
		if (associatedEventParticipant != null && userService.hasRight(UserRight.EVENTPARTICIPANT_EDIT)) {
			eventParticipantFacade.onEventParticipantChanged(
				eventFacade.toDto(associatedEventParticipant.getEvent()),
				eventParticipantFacade.toDto(associatedEventParticipant),
				associatedEventParticipant,
				syncShares);
		}
	}

	@Override
	@RightsAllowed({
		UserRight._PATHOGEN_TEST_DELETE,
		UserRight._ENVIRONMENT_PATHOGEN_TEST_DELETE })
	public void deletePathogenTest(String pathogenTestUuid, DeletionDetails deletionDetails) {

		PathogenTest pathogenTest = pathogenTestService.getByUuid(pathogenTestUuid);
		pathogenTestService.delete(pathogenTest, deletionDetails);

		handleAssociatedEntityChanges(pathogenTest, true);
	}

	@Override
	public boolean hasPathogenTest(SampleReferenceDto sample) {
		Sample sampleEntity = sampleService.getByReferenceDto(sample);
		return pathogenTestService.hasPathogenTest(sampleEntity);
	}

	public PathogenTestDto savePathogenTest(@Valid PathogenTestDto dto, boolean checkChangeDate, boolean syncShares) {
		PathogenTest existingSampleTest = pathogenTestService.getByUuid(dto.getUuid());
		FacadeHelper.checkCreateAndEditRights(
			existingSampleTest,
			userService,
			dto.getSample() != null ? UserRight.PATHOGEN_TEST_CREATE : UserRight.ENVIRONMENT_PATHOGEN_TEST_CREATE,
			dto.getSample() != null ? UserRight.PATHOGEN_TEST_EDIT : UserRight.ENVIRONMENT_PATHOGEN_TEST_EDIT);

		PathogenTestDto existingSampleTestDto = toDto(existingSampleTest);

		restorePseudonymizedDto(dto, existingSampleTest, existingSampleTestDto);

		validate(dto);

		PathogenTest pathogenTest = fillOrBuildEntity(dto, existingSampleTest, checkChangeDate);
		pathogenTestService.ensurePersisted(pathogenTest);

		onPathogenTestChanged(existingSampleTestDto, pathogenTest);
		handleAssociatedEntityChanges(pathogenTest, syncShares);

		return convertToDto(pathogenTest);
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

		Collection<PathogenTestDto> dtos = toPseudonymizedDtos(pathogenTestService.getBySampleUuids(sampleUuids, true)).stream()
			.collect(Collectors.toMap(s -> s.getSample().getUuid(), s -> s, (s1, s2) -> {

				// keep the positive one
				if (s1.getTestResult() == PathogenTestResultType.POSITIVE) {
					return s1;
				} else if (s2.getTestResult() == PathogenTestResultType.POSITIVE) {
					return s2;
				}

				// ordered by creation date by default, so always keep the first one
				return s1;
			}))
			.values();
		return new ArrayList<>(dtos);
	}

	@Override
	public void validate(PathogenTestDto pathogenTest) throws ValidationRuntimeException {
		if (pathogenTest.getSample() == null && pathogenTest.getEnvironmentSample() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.pathogenTestValidSampleOrEnvironment));
		}

		if (pathogenTest.getTestType() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE)));
		}
		if (pathogenTest.getSample() != null && pathogenTest.getTestedDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.pathogenTestValidDisease));
		}

		if (pathogenTest.getEnvironmentSample() != null && pathogenTest.getTestedPathogen() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.pathogenTestValidPathogen));
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

	private PathogenTestDto convertToDto(PathogenTest source) {
		return convertToDto(source, createPseudonymizer(Collections.singletonList(source)));
	}

	public PathogenTestDto convertToDto(PathogenTest source, Pseudonymizer<PathogenTestDto> pseudonymizer) {

		if (source == null) {
			return null;
		}

		boolean inJurisdiction = pathogenTestService.inJurisdictionOrOwned(source);
		return convertToDto(source, pseudonymizer, inJurisdiction);
	}

	private PathogenTestDto convertToDto(PathogenTest source, Pseudonymizer<PathogenTestDto> pseudonymizer, boolean inJurisdiction) {

		PathogenTestDto target = toDto(source);
		pseudonymizeDto(source, target, pseudonymizer, inJurisdiction);
		return target;
	}

	private static void pseudonymizeDto(
		PathogenTest source,
		PathogenTestDto target,
		Pseudonymizer<PathogenTestDto> pseudonymizer,
		boolean inJurisdiction) {

		if (source != null && target != null) {
			pseudonymizer.pseudonymizeDto(PathogenTestDto.class, target, inJurisdiction, null);
		}
	}

	private Pseudonymizer<PathogenTestDto> createPseudonymizer(Collection<PathogenTest> tests) {
		return Pseudonymizer.getDefault(userService, createSpecialAccessChecker(tests));
	}

	private <T extends PathogenTestDto> SpecialAccessCheck<T> createSpecialAccessChecker(Collection<PathogenTest> tests) {
		List<String> withSpecialAccess = specialCaseAccessService.getPathogenTestUuidsWithSpecialAccess(tests);

		return test -> withSpecialAccess.contains(test.getUuid());
	}

	private void restorePseudonymizedDto(PathogenTestDto dto, PathogenTest existingSampleTest, PathogenTestDto existingSampleTestDto) {

		if (existingSampleTestDto != null) {
			boolean isInJurisdiction = pathogenTestService.inJurisdictionOrOwned(existingSampleTest);
			Pseudonymizer<PathogenTestDto> pseudonymizer = createPseudonymizer(Collections.singletonList(existingSampleTest));

			pseudonymizer.restorePseudonymizedValues(PathogenTestDto.class, dto, existingSampleTestDto, isInJurisdiction);
		}
	}

	public PathogenTest fillOrBuildEntity(@NotNull PathogenTestDto source, PathogenTest target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, PathogenTest::new, checkChangeDate);

		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setEnvironmentSample(environmentSampleService.getByReferenceDto(source.getEnvironmentSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setTestedPathogen(source.getTestedPathogen());
		target.setTestedPathogenDetails(source.getTestedPathogenDetails());
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
		target.setCtValueE(source.getCtValueE());
		target.setCtValueN(source.getCtValueN());
		target.setCtValueRdrp(source.getCtValueRdrp());
		target.setCtValueS(source.getCtValueS());
		target.setCtValueOrf1(source.getCtValueOrf1());
		target.setCtValueRdrpS(source.getCtValueRdrpS());
		target.setReportDate(source.getReportDate());
		target.setViaLims(source.isViaLims());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setPreliminary(source.getPreliminary());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		target.setPrescriberPhysicianCode(source.getPrescriberPhysicianCode());
		target.setPrescriberFirstName(source.getPrescriberFirstName());
		target.setPrescriberLastName(source.getPrescriberLastName());
		target.setPrescriberPhoneNumber(source.getPrescriberPhoneNumber());
		target.setPrescriberAddress(source.getPrescriberAddress());
		target.setPrescriberPostalCode(source.getPrescriberPostalCode());
		target.setPrescriberCity(source.getPrescriberCity());
		target.setPrescriberCountry(countryService.getByReferenceDto(source.getPrescriberCountry()));

		return target;
	}

	private void onPathogenTestChanged(PathogenTestDto existingPathogenTest, PathogenTest newPathogenTest) {

		if (newPathogenTest.getSample() == null) {
			return;
		}

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
