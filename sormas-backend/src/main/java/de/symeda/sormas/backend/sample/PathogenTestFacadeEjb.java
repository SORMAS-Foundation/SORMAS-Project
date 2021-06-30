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
package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "PathogenTestFacade")
public class PathogenTestFacadeEjb implements PathogenTestFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private SampleService sampleService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private MessagingService messagingService;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

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
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getAllActivePathogenTestsAfter(date, user)
			.stream()
			.map(p -> convertToDto(p, pseudonymizer))
			.collect(Collectors.toList());
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
		return savePathogenTest(dto, true);
	}

	public PathogenTestDto savePathogenTest(PathogenTestDto dto, boolean checkChangeDate) {
		PathogenTest existingSampleTest = pathogenTestService.getByUuid(dto.getUuid());
		PathogenTestDto existingSampleTestDto = toDto(existingSampleTest);

		restorePseudonymizedDto(dto, existingSampleTest, existingSampleTestDto);

		PathogenTest pathogenTest = fromDto(dto, checkChangeDate);
		pathogenTestService.ensurePersisted(pathogenTest);

		onPathogenTestChanged(existingSampleTestDto, pathogenTest);

		// Update case classification if necessary
		final Case associatedCase = pathogenTest.getSample().getAssociatedCase();
		if (associatedCase != null) {
			caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(associatedCase), associatedCase);
		}

		return convertToDto(pathogenTest, Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public void deletePathogenTest(String pathogenTestUuid) {
		User user = userService.getCurrentUser();
		if (!userRoleConfigFacade.getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[user.getUserRoles().size()]))
			.contains(UserRight.PATHOGEN_TEST_DELETE)) {
			throw new UnsupportedOperationException("User " + user.getUuid() + " is not allowed to delete pathogen " + "tests.");
		}

		PathogenTest pathogenTest = pathogenTestService.getByUuid(pathogenTestUuid);
		pathogenTestService.delete(pathogenTest);

		final Case associatedCase = pathogenTest.getSample().getAssociatedCase();
		if (associatedCase != null) {
			caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(associatedCase), associatedCase);
		}
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
		if (pathogenTest.getTestDateTime() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME)));
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

		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<PathogenTestDto> getPositiveOrLatest(List<String> sampleUuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return pathogenTestService.getBySampleUuids(sampleUuids, true)
			.stream()
			.collect(
				Collectors.toMap(
					s -> s.getSample().getUuid(),
					(s) -> s,
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

		return target;
	}

	public PathogenTestDto convertToDto(PathogenTest source, Pseudonymizer pseudonymizer) {
		PathogenTestDto target = toDto(source);

		pseudonymizeDto(source, target, pseudonymizer);

		return target;
	}

	private void pseudonymizeDto(PathogenTest source, PathogenTestDto target, Pseudonymizer pseudonymizer) {
		if (source != null && target != null) {
			pseudonymizer.pseudonymizeDto(PathogenTestDto.class, target, sampleService.inJurisdictionOrOwned(source.getSample()).getInJurisdiction(), null);
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
		final List<User> messageRecipients = new ArrayList<>();
		Disease disease = null;

		if (caze != null) {
			disease = caze.getDisease();
			messageRecipients.addAll(
				userService.getAllByRegionsAndUserRoles(
					JurisdictionHelper.getCaseRegions(caze),
					UserRole.SURVEILLANCE_SUPERVISOR,
					UserRole.CASE_SUPERVISOR));

		}

		if (contact != null) {
			disease = contact.getDisease() != null ? contact.getDisease() : contact.getCaze().getDisease();
			messageRecipients.addAll(
				userService
					.getAllByRegionsAndUserRoles(
						JurisdictionHelper.getContactRegions(contact),
						UserRole.SURVEILLANCE_SUPERVISOR,
						UserRole.CONTACT_SUPERVISOR)
					.stream()
					.filter(user -> !messageRecipients.contains(user))
					.collect(Collectors.toList()));
		}

		if (eventParticipant != null) {
			final Region region = eventParticipant.getEvent().getEventLocation().getRegion();
			disease = eventParticipant.getEvent().getDisease();
			messageRecipients.addAll(
				userService.getAllByRegionAndUserRoles(region, UserRole.EVENT_OFFICER)
					.stream()
					.filter(user -> !messageRecipients.contains(user))
					.collect(Collectors.toList()));
			if (disease == null) {
				sendMessageOnPathogenTestChanged(
					existingPathogenTest,
					newPathogenTest,
					null,
					messageRecipients,
					MessagingService.CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT_NO_DISEASE,
					MessagingService.CONTENT_LAB_RESULT_SPECIFIED_EVENT_PARTICIPANT_NO_DISEASE,
					DataHelper.getShortUuid(eventParticipant.getUuid()));
			}
		}

		if (disease != null) {
			final String contentLabResultArrived = caze != null
				? MessagingService.CONTENT_LAB_RESULT_ARRIVED
				: contact != null
					? MessagingService.CONTENT_LAB_RESULT_ARRIVED_CONTACT
					: MessagingService.CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT;

			final String contentLabResultSpecified = caze != null
				? MessagingService.CONTENT_LAB_RESULT_SPECIFIED
				: contact != null
					? MessagingService.CONTENT_LAB_RESULT_SPECIFIED_CONTACT
					: MessagingService.CONTENT_LAB_RESULT_SPECIFIED_EVENT_PARTICIPANT;
			final String shortUuid =
				DataHelper.getShortUuid(caze != null ? caze.getUuid() : contact != null ? contact.getUuid() : eventParticipant.getUuid());
			sendMessageOnPathogenTestChanged(
				existingPathogenTest,
				newPathogenTest,
				disease,
				messageRecipients,
				contentLabResultArrived,
				contentLabResultSpecified,
				shortUuid);
		}
	}

	private void sendMessageOnPathogenTestChanged(
		PathogenTestDto existingPathogenTest,
		PathogenTest newPathogenTest,
		Disease disease,
		List<User> messageRecipients,
		String contentLabResultArrived,
		String contentLabResultSpecified,
		String shortUuid) {
		if (existingPathogenTest == null && newPathogenTest.getTestResult() != PathogenTestResultType.PENDING) {
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(
						recipient,
						MessageSubject.LAB_RESULT_ARRIVED,
						String.format(
							I18nProperties.getString(contentLabResultArrived),
							newPathogenTest.getTestResult().toString(),
							disease,
							shortUuid,
							newPathogenTest.getTestType(),
							newPathogenTest.getTestedDisease()),
						MessageType.EMAIL,
						MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(
						String.format(
							"EmailDeliveryFailedException when trying to notify supervisors " + "about the arrival of a lab result. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
							recipient.getUuid()));
				}
			}
		} else if (existingPathogenTest != null
			&& existingPathogenTest.getTestResult() == PathogenTestResultType.PENDING
			&& newPathogenTest.getTestResult() != PathogenTestResultType.PENDING) {
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(
						recipient,
						MessageSubject.LAB_RESULT_SPECIFIED,
						String.format(
							I18nProperties.getString(contentLabResultSpecified),
							disease,
							shortUuid,
							newPathogenTest.getTestResult().toString(),
							newPathogenTest.getTestType(),
							newPathogenTest.getTestedDisease()),
						MessageType.EMAIL,
						MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(
						String.format(
							"EmailDeliveryFailedException when trying to notify supervisors " + "about the specification of a lab result. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
							recipient.getUuid()));
				}
			}
		}
	}

	@LocalBean
	@Stateless
	public static class PathogenTestFacadeEjbLocal extends PathogenTestFacadeEjb {
	}
}
