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
import java.util.Map;
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

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.util.PseudonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SampleFacade")
public class SampleFacadeEjb implements SampleFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SampleService sampleService;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private AdditionalTestService additionalTestService;
	@EJB
	private AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private LocationService locationService;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private PseudonymizationService pseudonymizationService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getAllActiveUuids(user);
	}

	@Override
	public List<SampleDto> getAllActiveSamplesAfter(Date date) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getAllActiveSamplesAfter(date, user).stream()
				.map(e -> convertToDto(e))
				.collect(Collectors.toList());
	}

	@Override
	public List<SampleDto> getByUuids(List<String> uuids) {
		return sampleService.getByUuids(uuids)
				.stream()
				.map(c -> convertToDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<SampleDto> getByCaseUuids(List<String> caseUuids) {
		return sampleService.getByCaseUuids(caseUuids)
				.stream()
				.map(c -> convertToDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getDeletedUuidsSince(user, since);
	}

	@Override
	public SampleDto getSampleByUuid(String uuid) {
		return convertToDto(sampleService.getByUuid(uuid));
	}

	@Override
	public SampleDto saveSample(SampleDto dto) {
		return saveSample(dto, true);
	}

	public SampleDto saveSample(SampleDto dto, boolean handleChanges) {
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

		if (handleChanges) {
			onSampleChanged(existingSample, sample);
		}

		return toDto(sample);
	}

	@Override
	public SampleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(sampleService.getByUuid(uuid));
	}

	@Override
	public List<SampleIndexDto> getIndexList(SampleCriteria sampleCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleIndexDto> cq = cb.createQuery(SampleIndexDto.class);
		Root<Sample> sample = cq.from(Sample.class);

		SampleJoins joins = new SampleJoins(sample);

		cq.multiselect(sample.get(Sample.UUID),
				joins.getCaze().get(Case.EPID_NUMBER), sample.get(Sample.LAB_SAMPLE_ID), sample.get(Sample.SAMPLE_DATE_TIME),
				sample.get(Sample.SHIPPED), sample.get(Sample.SHIPMENT_DATE), sample.get(Sample.RECEIVED), sample.get(Sample.RECEIVED_DATE),
				sample.get(Sample.SAMPLE_MATERIAL), sample.get(Sample.SAMPLE_PURPOSE), sample.get(Sample.SPECIMEN_CONDITION),
				joins.getLab().get(Facility.UUID), joins.getLab().get(Facility.NAME), joins.getReferredSample().get(Sample.UUID),
				joins.getCaze().get(Case.UUID), joins.getCasePerson().get(Person.FIRST_NAME), joins.getCasePerson().get(Person.LAST_NAME),
				joins.getCaze().get(Case.DISEASE), joins.getCaze().get(Case.DISEASE_DETAILS),
				joins.getCaseRegion().get(Region.UUID), joins.getCaseDistrict().get(District.UUID), joins.getCaseDistrict().get(District.NAME), sample.get(Sample.PATHOGEN_TEST_RESULT),
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED), cb.isNotEmpty(sample.get(Sample.ADDITIONAL_TESTS)),
				joins.getCaseReportingUser().get(User.UUID), joins.getCaseCommunity().get(Community.UUID), joins.getCaseFacility().get(Community.UUID), joins.getCasePointOfEntry().get(Community.UUID));

		Predicate filter = sampleService.createUserFilter(cb, cq, sample);

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
					case SampleIndexDto.LAB_SAMPLE_ID:
					case SampleIndexDto.SHIPPED:
					case SampleIndexDto.RECEIVED:
					case SampleIndexDto.REFERRED:
					case SampleIndexDto.SAMPLE_DATE_TIME:
					case SampleIndexDto.SHIPMENT_DATE:
					case SampleIndexDto.RECEIVED_DATE:
					case SampleIndexDto.SAMPLE_MATERIAL:
					case SampleIndexDto.SAMPLE_PURPOSE:
					case SampleIndexDto.PATHOGEN_TEST_RESULT:
					case SampleIndexDto.ADDITIONAL_TESTING_STATUS:
						expression = sample.get(sortProperty.propertyName);
						break;
					case SampleIndexDto.DISEASE:
						expression = joins.getCaze().get(Case.DISEASE);
						break;
					case SampleIndexDto.EPID_NUMBER:
						expression = joins.getCaze().get(Case.EPID_NUMBER);
						break;
					case SampleIndexDto.ASSOCIATED_CASE:
						expression = joins.getCasePerson().get(Person.LAST_NAME);
						order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
						expression = joins.getCasePerson().get(Person.FIRST_NAME);
						break;
					case SampleIndexDto.CASE_DISTRICT:
						expression = joins.getCaseDistrict().get(District.NAME);
						break;
					case SampleIndexDto.LAB:
						expression = joins.getLab().get(Facility.NAME);
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

		List<SampleIndexDto> samples;
		if (first != null && max != null) {
			samples = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			samples = em.createQuery(cq).getResultList();
		}

		pseudonymizationService.pseudonymizeDtoCollection(SampleIndexDto.class, samples, s -> {
					if (s.getAssociatedCase() == null) {
						return true;
					}

					return caseJurisdictionChecker.isInJurisdiction(s.getAssociatedCase().getJurisdiction());
				},
				(s, isInJurisdiction) -> {
					pseudonymizationService.pseudonymizeDto(CaseReferenceDto.class, s.getAssociatedCase(), isInJurisdiction, null);
				});

		return samples;
	}

	@Override
	public void validate(SampleDto sample) throws ValidationRuntimeException {
		if (sample.getAssociatedCase() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validCase));
		}
		if (sample.getSampleDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)));
		}
		if (sample.getReportDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.REPORT_DATE_TIME)));
		}
		if (sample.getSampleMaterial() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL)));
		}
		if (sample.getSamplePurpose() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_PURPOSE)));
		}
		if (sample.getLab() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.LAB)));
		}
	}

	private List<SampleExportDto> getExportList(SampleCriteria sampleCriteria, CaseCriteria caseCriteria, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleExportDto> cq = cb.createQuery(SampleExportDto.class);
		Root<Sample> sample = cq.from(Sample.class);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Sample, Facility> laboratory = sample.join(Sample.LAB, JoinType.LEFT);
		Join<Sample, Sample> referredTo = sample.join(Sample.REFERRED_TO, JoinType.LEFT);
		Join<Case, Region> caseRegion = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> caseDistrict = caze.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Community> caseCommunity = caze.join(Case.COMMUNITY, JoinType.LEFT);
		Join<Case, Facility> caseFacility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Person, Location> personAddress = person.join(Person.ADDRESS, JoinType.LEFT);

		cq.multiselect(
				sample.get(Sample.ID),
				sample.get(Sample.UUID),
				sample.get(Sample.LAB_SAMPLE_ID),
				caze.get(Case.EPID_NUMBER),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				caze.get(Case.DISEASE),
				caze.get(Case.DISEASE_DETAILS),
				sample.get(Sample.SAMPLE_DATE_TIME),
				sample.get(Sample.SAMPLE_MATERIAL),
				sample.get(Sample.SAMPLE_MATERIAL_TEXT),
				sample.get(Sample.SAMPLE_PURPOSE),
				sample.get(Sample.SAMPLE_SOURCE),
				laboratory.get(Facility.UUID),
				laboratory.get(Facility.NAME),
				sample.get(Sample.LAB_DETAILS),
				sample.get(Sample.PATHOGEN_TEST_RESULT),
				sample.get(Sample.PATHOGEN_TESTING_REQUESTED),
				sample.get(Sample.REQUESTED_PATHOGEN_TESTS_STRING),
				sample.get(Sample.REQUESTED_OTHER_PATHOGEN_TESTS),
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED),
				sample.get(Sample.REQUESTED_ADDITIONAL_TESTS_STRING),
				sample.get(Sample.REQUESTED_OTHER_ADDITIONAL_TESTS),
				sample.get(Sample.SHIPPED),
				sample.get(Sample.SHIPMENT_DATE),
				sample.get(Sample.SHIPMENT_DETAILS),
				sample.get(Sample.RECEIVED),
				sample.get(Sample.RECEIVED_DATE),
				sample.get(Sample.SPECIMEN_CONDITION),
				sample.get(Sample.NO_TEST_POSSIBLE_REASON),
				sample.get(Sample.COMMENT),
				referredTo.get(Sample.UUID),
				caze.get(Case.UUID),
				person.get(Person.APPROXIMATE_AGE),
				person.get(Person.APPROXIMATE_AGE_TYPE),
				person.get(Person.SEX),
				personAddress.get(Location.ID),
				caze.get(Case.REPORT_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.OUTCOME),
				caseRegion.get(Region.NAME),
				caseDistrict.get(District.NAME),
				caseCommunity.get(Community.NAME),
				caseFacility.get(Facility.UUID),
				caseFacility.get(Facility.NAME),
				caze.get(Case.HEALTH_FACILITY_DETAILS)
		);

		Predicate filter = sampleService.createUserFilter(cb, cq, sample);

		if (sampleCriteria != null) {
			Predicate criteriaFilter = sampleService.buildCriteriaFilter(sampleCriteria, cb, sample);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		} else if (caseCriteria != null) {
			Join<Case, Case> casePath = sample.join(Sample.ASSOCIATED_CASE);
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, casePath);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
			filter = AbstractAdoService.and(cb, filter, cb.isFalse(sample.get(Sample.DELETED)));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(sample.get(Sample.REPORT_DATE_TIME)));

		List<SampleExportDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		for (SampleExportDto exportDto : resultList) {
			exportDto.setCaseAddress(locationService.getById(exportDto.getCaseAddressId()).toString());

			List<PathogenTest> pathogenTests = pathogenTestService.getAllBySample(sampleService.getById(exportDto.getId()));
			int count = 0;
			for (PathogenTest pathogenTest : pathogenTests) {
				switch (++count) {
					case 1:
						exportDto.setPathogenTestType1(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()));
						exportDto.setPathogenTestDisease1(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails()));
						exportDto.setPathogenTestDateTime1(pathogenTest.getTestDateTime());
						if (pathogenTest.getLab() != null) {
							exportDto
									.setPathogenTestLab1(FacilityHelper.buildFacilityString(pathogenTest.getLab().getUuid(),
											pathogenTest.getLab().getName(), pathogenTest.getLabDetails()));
						}
						exportDto.setPathogenTestResult1(pathogenTest.getTestResult());
						exportDto.setPathogenTestVerified1(pathogenTest.getTestResultVerified());
						break;
					case 2:
						exportDto.setPathogenTestType2(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()));
						exportDto.setPathogenTestDisease2(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails()));
						exportDto.setPathogenTestDateTime2(pathogenTest.getTestDateTime());
						if (pathogenTest.getLab() != null) {
							exportDto
									.setPathogenTestLab2(FacilityHelper.buildFacilityString(pathogenTest.getLab().getUuid(),
											pathogenTest.getLab().getName(), pathogenTest.getLabDetails()));
						}
						exportDto.setPathogenTestResult2(pathogenTest.getTestResult());
						exportDto.setPathogenTestVerified2(pathogenTest.getTestResultVerified());
						break;
					case 3:
						exportDto.setPathogenTestType3(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()));
						exportDto.setPathogenTestDisease3(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails()));
						exportDto.setPathogenTestDateTime3(pathogenTest.getTestDateTime());
						if (pathogenTest.getLab() != null) {
							exportDto
									.setPathogenTestLab3(FacilityHelper.buildFacilityString(pathogenTest.getLab().getUuid(),
											pathogenTest.getLab().getName(), pathogenTest.getLabDetails()));
						}
						exportDto.setPathogenTestResult3(pathogenTest.getTestResult());
						exportDto.setPathogenTestVerified3(pathogenTest.getTestResultVerified());
						break;
					default:
						StringBuilder sb = new StringBuilder();
						if (!exportDto.getOtherPathogenTestsDetails().isEmpty()) {
							sb.append(", ");
						}
						sb.append(DateHelper.formatDateForExport(pathogenTest.getTestDateTime())).append(" (")
								.append(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()))
								.append(", ").append(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails()))
								.append(", ").append(pathogenTest.getTestResult()).append(")");
						exportDto.setOtherPathogenTestsDetails(exportDto.getOtherPathogenTestsDetails() + sb.toString());
						break;
				}
			}

			List<AdditionalTest> additionalTests = additionalTestService.getAllBySample(sampleService.getById(exportDto.getId()));
			if (additionalTests.size() > 0) {
				exportDto.setAdditionalTest(additionalTestFacade.toDto(additionalTests.get(0)));
			}
			if (additionalTests.size() > 1) {
				exportDto.setOtherAdditionalTestsDetails(I18nProperties.getString(Strings.yes));
			} else {
				exportDto.setOtherAdditionalTestsDetails(I18nProperties.getString(Strings.no));
			}
		}

		return resultList;
	}

	@Override
	public List<SampleExportDto> getExportList(SampleCriteria criteria, int first, int max) {
		return getExportList(criteria, null, first, max);
	}

	@Override
	public List<SampleExportDto> getExportList(CaseCriteria criteria, int first, int max) {
		return getExportList(null, criteria, first, max);
	}

	@Override
	public long count(SampleCriteria sampleCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sample> root = cq.from(Sample.class);
		Predicate filter = sampleService.createUserFilter(cb, cq, root);
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
	public SampleReferenceDto getReferredFrom(String sampleUuid) {
		return toReferenceDto(sampleService.getReferredFrom(sampleUuid));
	}

	@Override
	public void deleteSample(SampleReferenceDto sampleRef) {
		if (!userService.hasRight(UserRight.SAMPLE_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete samples.");
		}

		Sample sample = sampleService.getByReferenceDto(sampleRef);
		sampleService.delete(sample);

		caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(sample.getAssociatedCase()), sample.getAssociatedCase());
	}

	@Override
	public Map<PathogenTestResultType, Long> getNewTestResultCountByResultType(List<Long> caseIds) {
		return sampleService.getNewTestResultCountByResultType(caseIds);
	}

	public Sample fromDto(@NotNull SampleDto source) {

		Sample target = sampleService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Sample();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setAssociatedCase(caseService.getByReferenceDto(source.getAssociatedCase()));
		target.setLabSampleID(source.getLabSampleID());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSamplePurpose(source.getSamplePurpose());
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

	private SampleDto convertToDto(Sample source) {
		SampleDto dto = toDto(source);

		if (dto != null) {
			Boolean isInJurisdiction = caseJurisdictionChecker.isInJurisdiction(source.getAssociatedCase());
			pseudonymizationService.pseudonymizeDto(SampleDto.class, dto, isInJurisdiction, s -> {
				pseudonymizationService.pseudonymizeDto(CaseReferenceDto.class, s.getAssociatedCase(), isInJurisdiction, null);
			});
		}

		return dto;
	}

	public static SampleDto toDto(Sample source) {
		if (source == null) {
			return null;
		}
		SampleDto target = new SampleDto();
		DtoHelper.fillDto(target, source);

		target.setAssociatedCase(CaseFacadeEjb.toReferenceDto(source.getAssociatedCase()));
		target.setLabSampleID(source.getLabSampleID());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSamplePurpose(source.getSamplePurpose());
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
		if (entity == null) {
			return null;
		}
		SampleReferenceDto dto = new SampleReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	private void onSampleChanged(SampleDto existingSample, Sample newSample) {
		// Change pathogenTestResultChangeDate if the pathogen test result has changed
		if (existingSample != null && existingSample.getPathogenTestResult() != null && existingSample.getPathogenTestResult() != newSample.getPathogenTestResult()) {
			Date latestPathogenTestDate = pathogenTestFacade.getLatestPathogenTestDate(newSample.getUuid());
			if (latestPathogenTestDate != null) {
				newSample.setPathogenTestResultChangeDate(latestPathogenTestDate);
			}
		}

		caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(newSample.getAssociatedCase()), newSample.getAssociatedCase());

		// Send an email to the lab user when a sample has been shipped to his lab
		if (newSample.isShipped() && (existingSample == null || !existingSample.isShipped())) {
			List<User> messageRecipients = userService.getLabUsersOfLab(newSample.getLab());

			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_LAB_SAMPLE_SHIPPED),
							String.format(I18nProperties.getString(MessagingService.CONTENT_LAB_SAMPLE_SHIPPED_SHORT),
									DataHelper.getShortUuid(newSample.getAssociatedCase().getUuid())),
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format("EmailDeliveryFailedException when trying to notify supervisors about the shipment of a lab sample. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
				}
			}
		}
	}

	@Override
	public boolean isDeleted(String sampleUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sample> from = cq.from(Sample.class);

		cq.where(cb.and(
				cb.isTrue(from.get(Sample.DELETED)),
				cb.equal(from.get(AbstractDomainObject.UUID), sampleUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@LocalBean
	@Stateless
	public static class SampleFacadeEjbLocal extends SampleFacadeEjb {

	}
}
