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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import java.sql.Timestamp;
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
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
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
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

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
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
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

		if(user == null) {
			return Collections.emptyList();
		}

		return pathogenTestService.getAllActivePathogenTestsAfter(date, user).stream()
				.map(e -> toDto(e))
				.collect(Collectors.toList());
	}

	@Override
	public List<PathogenTestDto> getByUuids(List<String> uuids) {
		return pathogenTestService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PathogenTestDto> getBySampleUuids(List<String> sampleUuids) {
		return pathogenTestService.getBySampleUuids(sampleUuids)
				.stream()
				.map(p -> toDto(p))
				.collect(Collectors.toList());
	}

	@Override
	public List<PathogenTestDto> getAllBySample(SampleReferenceDto sampleRef) {
		if(sampleRef == null) {
			return Collections.emptyList();
		}

		Sample sample = sampleService.getByUuid(sampleRef.getUuid());

		return pathogenTestService.getAllBySample(sample).stream()
				.map(s -> toDto(s))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return pathogenTestService.getDeletedUuidsSince(user, since);
	}

	@Override
	public PathogenTestDto getByUuid(String uuid) {
		return toDto(pathogenTestService.getByUuid(uuid));
	}

	@Override
	public PathogenTestDto savePathogenTest(PathogenTestDto dto) {
		PathogenTestDto existingSampleTest = toDto(pathogenTestService.getByUuid(dto.getUuid()));		
		PathogenTest pathogenTest = fromDto(dto);
		pathogenTestService.ensurePersisted(pathogenTest);

		onPathogenTestChanged(existingSampleTest, pathogenTest);

		// Update case classification if necessary
		caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(pathogenTest.getSample().getAssociatedCase()), pathogenTest.getSample().getAssociatedCase());

		return toDto(pathogenTest);
	}

	@Override
	public void deletePathogenTest(String pathogenTestUuid) {
		User user = userService.getCurrentUser();
		if (!userRoleConfigFacade.getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[user.getUserRoles().size()])).contains(UserRight.PATHOGEN_TEST_DELETE)) {
			throw new UnsupportedOperationException("User " + user.getUuid() + " is not allowed to delete pathogen tests.");
		}

		PathogenTest pathogenTest = pathogenTestService.getByUuid(pathogenTestUuid);
		pathogenTestService.delete(pathogenTest);

		caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(pathogenTest.getSample().getAssociatedCase()), pathogenTest.getSample().getAssociatedCase());
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
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE)));
		}
		if (pathogenTest.getTestedDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE)));
		}
		if (pathogenTest.getTestDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME)));
		}
		if (pathogenTest.getLab() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.LAB)));
		}
		if (pathogenTest.getTestResult() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT)));
		}
		if (pathogenTest.getTestResultVerified() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED)));
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

	public PathogenTest fromDto(@NotNull PathogenTestDto source) {
		PathogenTest target = pathogenTestService.getByUuid(source.getUuid());
		if(target == null) {
			target = new PathogenTest();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestType(source.getTestType());
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

		return target;
	}

	public PathogenTestDto toDto(PathogenTest source) {
		if(source == null) {
			return null;
		}
		PathogenTestDto target = new PathogenTestDto();
		DtoHelper.fillDto(target, source);

		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setTestedDisease(source.getTestedDisease());
		target.setTestedDiseaseDetails(source.getTestedDiseaseDetails());
		target.setTestType(source.getTestType());
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

		return target;
	}

	private void onPathogenTestChanged(PathogenTestDto existingPathogenTest, PathogenTest newPathogenTest) {
		// Send an email to all responsible supervisors when a new non-pending sample test is created or the status of a formerly pending test result has changed
		if (existingPathogenTest == null && newPathogenTest.getTestResult() != PathogenTestResultType.PENDING) {
			Case existingSampleCase = sampleService.getByUuid(newPathogenTest.getSample().getUuid()).getAssociatedCase();
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(existingSampleCase.getRegion(), 
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);

			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_LAB_RESULT_ARRIVED), 
							String.format(I18nProperties.getString(MessagingService.CONTENT_LAB_RESULT_ARRIVED), 
									newPathogenTest.getTestResult().toString(), existingSampleCase.getDisease(), DataHelper.getShortUuid(newPathogenTest.getUuid()),
									newPathogenTest.getTestType(), newPathogenTest.getTestedDisease()), 
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format("EmailDeliveryFailedException when trying to notify supervisors about the arrival of a lab result. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
				}
			}
		} else if (existingPathogenTest != null && existingPathogenTest.getTestResult() == PathogenTestResultType.PENDING && 
				newPathogenTest.getTestResult() != PathogenTestResultType.PENDING) {
			Case existingSampleCase = sampleService.getByUuid(newPathogenTest.getSample().getUuid()).getAssociatedCase();
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(existingSampleCase.getRegion(), 
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);

			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_LAB_RESULT_SPECIFIED), 
							String.format(I18nProperties.getString(MessagingService.CONTENT_LAB_RESULT_SPECIFIED), 
									existingSampleCase.getDisease(), DataHelper.getShortUuid(newPathogenTest.getUuid()), newPathogenTest.getTestResult().toString(),
									newPathogenTest.getTestType(), newPathogenTest.getTestedDisease()), 
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format("EmailDeliveryFailedException when trying to notify supervisors about the specification of a lab result. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
				}
			}
		}
	}

	@LocalBean
	@Stateless
	public static class PathogenTestFacadeEjbLocal extends PathogenTestFacadeEjb {
	}
}
