package de.symeda.sormas.backend.sample;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.DashboardSample;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "SampleFacade")
public class SampleFacadeEjb implements SampleFacade {

	@EJB
	private SampleService sampleService;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;

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
	public List<SampleIndexDto> getIndexList(String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		return sampleService.getAllAfter(null, user).stream()
				.map(s -> toIndexDto(s))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<SampleIndexDto> getIndexListByCase(CaseReferenceDto caseRef) {
		Case caze = caseService.getByReferenceDto(caseRef);
		
		return sampleService.getAllByCase(caze).stream()
				.map(s -> toIndexDto(s))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<DashboardSample> getNewSamplesForDashboard(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		User user = userService.getByUuid(userUuid);
		District district = districtService.getByReferenceDto(districtRef);
		
		return sampleService.getNewSamplesForDashboard(district, disease, from, to, user);
	}
	
	@Override
	public SampleReferenceDto getReferredFrom(String sampleUuid) {
		return toReferenceDto(sampleService.getReferredFrom(sampleUuid));
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
		DtoHelper.fillReferenceDto(target, source);
		
		target.setAssociatedCase(CaseFacadeEjb.toReferenceDto(source.getAssociatedCase()));
		target.setSampleCode(source.getSampleCode());
		target.setLabSampleID(source.getLabSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
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
		SampleReferenceDto dto = new SampleReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	public SampleIndexDto toIndexDto(Sample source) {
		if(source == null) {
			return null;
		}
		SampleIndexDto target = new SampleIndexDto();
		DtoHelper.fillReferenceDto(target, source);
		
		target.setAssociatedCase(CaseFacadeEjb.toReferenceDto(source.getAssociatedCase()));
		target.setDisease(source.getAssociatedCase().getDisease());
		target.setDiseaseDetails(source.getAssociatedCase().getDiseaseDetails());
		target.setSampleCode(source.getSampleCode());
		target.setLabSampleID(source.getLabSampleID());
		target.setShipmentDate(source.getShipmentDate());
		target.setReceivedDate(source.getReceivedDate());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setCaseRegion(RegionFacadeEjb.toReferenceDto(source.getAssociatedCase().getRegion()));
		target.setCaseDistrict(DistrictFacadeEjb.toReferenceDto(source.getAssociatedCase().getDistrict()));
		target.setReferredTo(SampleFacadeEjb.toReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());
		
		SampleTest latestSampleTest = null;
		for(SampleTest sampleTest : source.getSampleTests()) {
			if(latestSampleTest == null) {
				latestSampleTest = sampleTest;
			} else {
				if(sampleTest.getTestDateTime().after(latestSampleTest.getTestDateTime())) {
					latestSampleTest = sampleTest;
				}
			}
		}
		
		if(latestSampleTest != null) {
			SampleTestDto latestSampleTestDto = FacadeProvider.getSampleTestFacade().getByUuid(latestSampleTest.getUuid());
			target.setLabUser(latestSampleTestDto.getLabUser());
			target.setTestType(latestSampleTestDto.getTestType());
			target.setTestResult(latestSampleTestDto.getTestResult());
		}
		
		return target;
	}
	
}
