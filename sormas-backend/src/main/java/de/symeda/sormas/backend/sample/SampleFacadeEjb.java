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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SampleFacade")
public class SampleFacadeEjb implements SampleFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private SampleService sampleService;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PathogenTestFacadeEjbLocal sampleTestFacade;
	@EJB
	private MessagingService messagingService;

	private static final Logger logger = LoggerFactory.getLogger(PathogenTestFacadeEjb.class);

	@Override
	public List<String> getAllActiveUuids(String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getAllActiveUuids(user);
	}	

	@Override
	public List<SampleDto> getAllActiveSamplesAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);

		if(user == null) {
			return Collections.emptyList();
		}

		return sampleService.getAllActiveSamplesAfter(date, user).stream()
				.map(e -> toDto(e))
				.collect(Collectors.toList());
	}

	@Override
	public List<SampleDto> getByUuids(List<String> uuids) {
		return sampleService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<SampleDto> getAllByCase(CaseReferenceDto caseRef) {
		if(caseRef == null) {
			return Collections.emptyList();
		}

		Case caze = caseService.getByUuid(caseRef.getUuid());

		return sampleService.getAllByCase(caze).stream()
				.map(s -> toDto(s))
				.collect(Collectors.toList());
	}

	@Override
	public int getReceivedSampleCountByCase(CaseReferenceDto caseRef) {
		if (caseRef == null) {
			return 0;
		}

		Case caze = caseService.getByUuid(caseRef.getUuid());

		return sampleService.getReceivedSampleCountByCase(caze);
	}

	@Override
	public SampleDto getSampleByUuid(String uuid) {
		return toDto(sampleService.getByUuid(uuid));
	}

	@Override
	public SampleDto saveSample(SampleDto dto) {
		SampleDto existingSample = toDto(sampleService.getByUuid(dto.getUuid()));
		Sample sample = fromDto(dto);

		// Set defaults for testing requests
		if (sample.getPathogenTestingRequested() == null) {
			sample.setPathogenTestingRequested(false);
		}
		if (sample.getAdditionalTestingRequested() == null) {
			sample.setAdditionalTestingRequested(false);
		}

		sampleService.ensurePersisted(sample);

		onSampleChanged(existingSample, sample);

		return toDto(sample);
	}

	@Override
	public SampleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(sampleService.getByUuid(uuid));
	}

	@Override
	public List<SampleIndexDto> getIndexList(String userUuid, SampleCriteria sampleCriteria, int first, int max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleIndexDto> cq = cb.createQuery(SampleIndexDto.class);
		Root<Sample> sample = cq.from(Sample.class);

		Join<Sample, Sample> referredSample = sample.join(Sample.REFERRED_TO, JoinType.LEFT);
		Join<Sample, Facility> lab = sample.join(Sample.LAB, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		Join<Case, Person> cazePerson = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> caseRegion = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> caseDistrict = caze.join(Case.DISTRICT, JoinType.LEFT);

		cq.multiselect(sample.get(Sample.UUID), 
				sample.get(Sample.SAMPLE_CODE), sample.get(Sample.LAB_SAMPLE_ID), sample.get(Sample.SAMPLE_DATE_TIME), 
				sample.get(Sample.SHIPPED), sample.get(Sample.SHIPMENT_DATE), sample.get(Sample.RECEIVED), sample.get(Sample.RECEIVED_DATE), 
				sample.get(Sample.SAMPLE_MATERIAL), sample.get(Sample.SPECIMEN_CONDITION), 
				lab.get(Facility.UUID), lab.get(Facility.NAME), referredSample.get(Sample.UUID), 
				caze.get(Case.UUID), cazePerson.get(Person.FIRST_NAME), cazePerson.get(Person.LAST_NAME),
				caze.get(Case.DISEASE), caze.get(Case.DISEASE_DETAILS), 
				caseRegion.get(Region.UUID), caseDistrict.get(District.UUID), caseDistrict.get(District.NAME), sample.get(Sample.PATHOGEN_TEST_RESULT), 
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED), cb.isNotEmpty(sample.get(Sample.ADDITIONAL_TESTS)));

		Predicate filter = null;
		if (userUuid != null) {
			User user = userService.getByUuid(userUuid);
			filter = sampleService.createUserFilter(cb, cq, sample, user);
		}

		if (sampleCriteria != null) {
			Predicate criteriaFilter = sampleService.buildCriteriaFilter(sampleCriteria, cb, sample);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}		

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SampleIndexDto.UUID:
				case SampleIndexDto.SAMPLE_CODE:
				case SampleIndexDto.LAB_SAMPLE_ID:
				case SampleIndexDto.SHIPPED:
				case SampleIndexDto.RECEIVED:
				case SampleIndexDto.REFERRED:
				case SampleIndexDto.SAMPLE_DATE_TIME:
				case SampleIndexDto.SHIPMENT_DATE:
				case SampleIndexDto.RECEIVED_DATE:
				case SampleIndexDto.SAMPLE_MATERIAL:
				case SampleIndexDto.PATHOGEN_TEST_RESULT:
				case SampleIndexDto.ADDITIONAL_TESTING_STATUS:
					expression = sample.get(sortProperty.propertyName);
					break;
				case SampleIndexDto.DISEASE:
					expression = caze.get(Case.DISEASE);
					break;
				case SampleIndexDto.ASSOCIATED_CASE:
					expression = cazePerson.get(Person.LAST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = cazePerson.get(Person.FIRST_NAME);
					break;
				case SampleIndexDto.CASE_DISTRICT:
					expression = caseDistrict.get(District.NAME);
					break;
				case SampleIndexDto.LAB:
					expression = lab.get(Facility.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(sample.get(Sample.SAMPLE_DATE_TIME)));
		}

		List<SampleIndexDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		return resultList;	
	}
	
	@Override
	public long count(String userUuid, SampleCriteria sampleCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sample> root = cq.from(Sample.class);
		User user = userService.getByUuid(userUuid);
		Predicate filter = sampleService.createUserFilter(cb, cq, root, user);
		if (sampleCriteria != null) {
			Predicate criteriaFilter = sampleService.buildCriteriaFilter(sampleCriteria, cb, root);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<DashboardSampleDto> getNewSamplesForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		return sampleService.getNewSamplesForDashboard(region, district, disease, from, to, user);
	}

	@Override
	public SampleReferenceDto getReferredFrom(String sampleUuid) {
		return toReferenceDto(sampleService.getReferredFrom(sampleUuid));
	}

	@Override
	public void deleteSample(SampleReferenceDto sampleRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}

		Sample sample = sampleService.getByReferenceDto(sampleRef);
		sampleService.delete(sample);

		caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(sample.getAssociatedCase()), sample.getAssociatedCase());
	}

	public Sample fromDto(@NotNull SampleDto source) {

		Sample target = sampleService.getByUuid(source.getUuid());
		if(target == null) {
			target = new Sample();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setAssociatedCase(caseService.getByReferenceDto(source.getAssociatedCase()));
		target.setSampleCode(source.getSampleCode());
		target.setLabSampleID(source.getLabSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setLab(facilityService.getByReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setReferredTo(sampleService.getByReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setPathogenTestResult(source.getPathogenTestResult());
		target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
		target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static SampleDto toDto(Sample source) {
		if(source == null) {
			return null;
		}
		SampleDto target = new SampleDto();
		DtoHelper.fillDto(target, source);

		target.setAssociatedCase(CaseFacadeEjb.toReferenceDto(source.getAssociatedCase()));
		target.setSampleCode(source.getSampleCode());
		target.setLabSampleID(source.getLabSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setReferredTo(SampleFacadeEjb.toReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setPathogenTestResult(source.getPathogenTestResult());
		target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
		target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static SampleReferenceDto toReferenceDto(Sample entity) {
		if(entity == null) {
			return null;
		}
		SampleReferenceDto dto = new SampleReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	private void onSampleChanged(SampleDto existingSample, Sample newSample) {
		// Send an email to the lab user when a sample has been shipped to his lab
		if (newSample.isShipped() && (existingSample == null || !existingSample.isShipped())) {
			List<User> messageRecipients = userService.getLabUsersOfLab(newSample.getLab());

			for (User recipient : messageRecipients) {
				try {
					if (!StringUtils.isEmpty(newSample.getSampleCode())) {
						messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_LAB_SAMPLE_SHIPPED), 
								String.format(I18nProperties.getString(MessagingService.CONTENT_LAB_SAMPLE_SHIPPED), 
										newSample.getSampleCode(), 
										DataHelper.getShortUuid(newSample.getAssociatedCase().getUuid())), 
								MessageType.EMAIL, MessageType.SMS);
					} else {
						messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_LAB_SAMPLE_SHIPPED), 
								String.format(I18nProperties.getString(MessagingService.CONTENT_LAB_SAMPLE_SHIPPED_SHORT), 
										DataHelper.getShortUuid(newSample.getAssociatedCase().getUuid())), 
								MessageType.EMAIL, MessageType.SMS);
					}
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format("EmailDeliveryFailedException when trying to notify supervisors about the shipment of a lab sample. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
				}
			}
		}
	}

	@LocalBean
	@Stateless
	public static class SampleFacadeEjbLocal extends SampleFacadeEjb {
	}
}
