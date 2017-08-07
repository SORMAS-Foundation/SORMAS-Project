package de.symeda.sormas.backend.sample;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
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
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
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
	public List<SampleDto> getAllSamplesAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		return sampleService.getAllAfter(date, user).stream()
				.map(e -> toSampleDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<SampleDto> getAllByCase(CaseReferenceDto caseRef) {
		if(caseRef == null) {
			return Collections.emptyList();
		}
		
		Case caze = caseService.getByUuid(caseRef.getUuid());
		
		return sampleService.getAllByCase(caze).stream()
				.map(s -> toSampleDto(s))
				.collect(Collectors.toList());
	}

	@Override
	public SampleDto getSampleByUuid(String uuid) {
		return toSampleDto(sampleService.getByUuid(uuid));
	}

	@Override
	public SampleDto saveSample(SampleDto dto) {
		Sample sample = fromSampleDto(dto);
		sampleService.ensurePersisted(sample);
		
		return toSampleDto(sample);
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
	public SampleReferenceDto getReferredFrom(String sampleUuid) {
		return toReferenceDto(sampleService.getReferredFrom(sampleUuid));
	}
	
	public Sample fromSampleDto(@NotNull SampleDto source) {
		
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
		target.setOtherLab(facilityService.getByReferenceDto(source.getOtherLab()));
		target.setShipmentStatus(source.getShipmentStatus());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
		target.setReferredTo(sampleService.getByReferenceDto(source.getReferredTo()));
		
		return target;
	}
	
	public static SampleDto toSampleDto(Sample source) {
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
		target.setOtherLab(FacilityFacadeEjb.toReferenceDto(source.getOtherLab()));
		target.setShipmentStatus(source.getShipmentStatus());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
		target.setReferredTo(SampleFacadeEjb.toReferenceDto(source.getReferredTo()));
		
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
		target.setSampleCode(source.getSampleCode());
		target.setLabSampleID(source.getLabSampleID());
		target.setShipmentDate(source.getShipmentDate());
		target.setReceivedDate(source.getReceivedDate());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setShipmentStatus(source.getShipmentStatus());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setLga(DistrictFacadeEjb.toReferenceDto(source.getAssociatedCase().getDistrict()));
		
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
