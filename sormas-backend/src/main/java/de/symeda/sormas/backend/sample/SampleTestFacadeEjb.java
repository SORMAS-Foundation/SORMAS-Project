package de.symeda.sormas.backend.sample;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "SampleTestFacade")
public class SampleTestFacadeEjb implements SampleTestFacade {

	@EJB
	private SampleTestService sampleTestService;
	@EJB
	private SampleService sampleService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	
	@Override
	public List<SampleTestDto> getAllSampleTestsAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		return sampleTestService.getAllAfter(date, user).stream()
				.map(e -> toSampleTestDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<SampleTestDto> getAllBySample(SampleReferenceDto sampleRef) {
		if(sampleRef == null) {
			return Collections.emptyList();
		}
		
		Sample sample = sampleService.getByUuid(sampleRef.getUuid());
		
		return sampleTestService.getAllBySample(sample).stream()
				.map(s -> toSampleTestDto(s))
				.collect(Collectors.toList());
	}
	
	@Override
	public SampleTestDto getByUuid(String uuid) {
		return toSampleTestDto(sampleTestService.getByUuid(uuid));
	}
	
	@Override
	public SampleTestDto saveSampleTest(SampleTestDto dto) {
		SampleTest sampleTest = fromSampleTestDto(dto);
		sampleTestService.ensurePersisted(sampleTest);
		
		return toSampleTestDto(sampleTest);
	}
	
	public SampleTest fromSampleTestDto(@NotNull SampleTestDto source) {
		SampleTest target = sampleTestService.getByUuid(source.getUuid());
		if(target == null) {
			target = new SampleTest();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setTestType(source.getTestType());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestDateTime(source.getTestDateTime());
		target.setLab(facilityService.getByReferenceDto(source.getLab()));
		target.setLabUser(userService.getByReferenceDto(source.getLabUser()));
		target.setTestResult(source.getTestResult());
		target.setTestResultText(source.getTestResultText());
		target.setTestResultVerified(source.isTestResultVerified());
		
		return target;
	}
	
	public SampleTestDto toSampleTestDto(SampleTest source) {
		if(source == null) {
			return null;
		}
		SampleTestDto target = new SampleTestDto();
		DtoHelper.fillReferenceDto(target, source);
		
		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setTestType(source.getTestType());
		target.setTestTypeText(source.getTestTypeText());
		target.setTestDateTime(source.getTestDateTime());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setLabUser(UserFacadeEjb.toReferenceDto(source.getLabUser()));
		target.setTestResult(source.getTestResult());
		target.setTestResultText(source.getTestResultText());
		target.setTestResultVerified(source.isTestResultVerified());
		
		return target;
	}
}
