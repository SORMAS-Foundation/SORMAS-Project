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
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
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
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb.SampleTestFacadeEjbLocal;
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
	private SampleTestService sampleTestService;
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
	private SampleTestFacadeEjbLocal sampleTestFacade;

	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return sampleService.getAllUuids(user);
	}	
	
	@Override
	public List<SampleDto> getAllAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		return sampleService.getAllAfter(date, user).stream()
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
		Sample sample = fromDto(dto);
		sampleService.ensurePersisted(sample);
		
		return toDto(sample);
	}
	
	@Override
	public SampleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(sampleService.getByUuid(uuid));
	}
	
	@Override
	public List<SampleIndexDto> getIndexList(String userUuid, SampleCriteria sampleCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleIndexDto> cq = cb.createQuery(SampleIndexDto.class);
		Root<Sample> sample = cq.from(Sample.class);

		Join<Sample, Sample> referredSample = sample.join(Sample.REFERRED_TO, JoinType.LEFT);
		Join<Sample, SampleTest> mainTest = sample.join(Sample.MAIN_SAMPLE_TEST, JoinType.LEFT);
		Join<SampleTest, User> mainTestLabUser = mainTest.join(SampleTest.LAB_USER, JoinType.LEFT);
		Join<Sample, Facility> lab = sample.join(Sample.LAB, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		Join<Case, Person> cazePerson = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> caseRegion = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> caseDistrict = caze.join(Case.DISTRICT, JoinType.LEFT);
		
		cq.multiselect(sample.get(Sample.UUID), 
				sample.get(Sample.SAMPLE_CODE), sample.get(Sample.LAB_SAMPLE_ID),
				sample.get(Sample.SHIPPED), sample.get(Sample.SHIPMENT_DATE), sample.get(Sample.RECEIVED), sample.get(Sample.RECEIVED_DATE), 
				sample.get(Sample.SAMPLE_MATERIAL), sample.get(Sample.SPECIMEN_CONDITION), 
				lab.get(Facility.UUID), lab.get(Facility.NAME), referredSample.get(Sample.UUID), 
				caze.get(Case.UUID), cazePerson.get(Person.FIRST_NAME), cazePerson.get(Person.LAST_NAME),
				caze.get(Case.DISEASE), caze.get(Case.DISEASE_DETAILS), 
				caseRegion.get(Region.UUID), caseDistrict.get(District.UUID), caseDistrict.get(District.NAME), 
				mainTest.get(SampleTest.TEST_RESULT), mainTestLabUser.get(User.FIRST_NAME), mainTestLabUser.get(User.LAST_NAME));
		
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
		
		List<SampleIndexDto> resultList = em.createQuery(cq).getResultList();
		
		return resultList;	
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
		target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
		target.setReferredTo(sampleService.getByReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());

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
		target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
		target.setReferredTo(SampleFacadeEjb.toReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());

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
	
	@LocalBean
	@Stateless
	public static class SampleFacadeEjbLocal extends SampleFacadeEjb {
	}
}
