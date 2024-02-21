package de.symeda.sormas.backend.sample;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.sample.AdditionalTestCriteria;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "AdditionalTestFacade")
public class AdditionalTestFacadeEjb implements AdditionalTestFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AdditionalTestService service;
	@EJB
	private SampleService sampleService;
	@EJB
	private UserService userService;

	@Override
	public AdditionalTestDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<AdditionalTestDto> getAllBySample(String sampleUuid) {

		if (sampleUuid == null) {
			return Collections.emptyList();
		}

		Sample sample = sampleService.getByUuid(sampleUuid);
		return service.getAllBySample(sample).stream().map(s -> toDto(s)).collect(Collectors.toList());
	}

	public List<AdditionalTestDto> getIndexList(
		AdditionalTestCriteria additionalTestCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return toPseudonymizedDtos(service.getIndexList(additionalTestCriteria, first, max, sortProperties));
	}

	public Page<AdditionalTestDto> getIndexPage(
		AdditionalTestCriteria additionalTestCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {

		List<AdditionalTestDto> additionalTestList = getIndexList(additionalTestCriteria, offset, size, sortProperties);
		long totalElementCount = service.count(additionalTestCriteria);
		return new Page<>(additionalTestList, offset, size, totalElementCount);

	}

	@Override
	public AdditionalTestDto saveAdditionalTest(@Valid AdditionalTestDto additionalTest) {
		return saveAdditionalTest(additionalTest, true);
	}

	public AdditionalTestDto saveAdditionalTest(@Valid AdditionalTestDto additionalTest, boolean checkChangeDate) {
		AdditionalTest existingAdditionalTest = service.getByUuid(additionalTest.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingAdditionalTest, userService, UserRight.ADDITIONAL_TEST_CREATE, UserRight.ADDITIONAL_TEST_EDIT);
		AdditionalTest entity = fillOrBuildEntity(additionalTest, existingAdditionalTest, checkChangeDate);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void deleteAdditionalTest(String additionalTestUuid) {

		if (!userService.hasRight(UserRight.ADDITIONAL_TEST_DELETE)) {
			throw new UnsupportedOperationException("Your user is not allowed to delete additional tests");
		}

		AdditionalTest additionalTest = service.getByUuid(additionalTestUuid);
		service.deletePermanent(additionalTest);
	}

	@Override
	public List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date) {
		return getAllActiveAdditionalTestsAfter(date, null, null);
	}

	@Override
	public List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllAfter(date, batchSize, lastSynchronizedUuid).stream().map(e -> toDto(e)).collect(Collectors.toList());
	}

	private List<AdditionalTestDto> toPseudonymizedDtos(List<AdditionalTest> entities) {

		List<Long> inJurisdictionIds = service.getInJurisdictionIds(entities);
		Pseudonymizer<AdditionalTestDto> pseudonymizer = Pseudonymizer.getDefault(userService);

		return entities.stream().map(p -> convertToDto(p, pseudonymizer, inJurisdictionIds.contains(p.getId()))).collect(Collectors.toList());
	}

	@Override
	public List<AdditionalTestDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	public AdditionalTestDto convertToDto(AdditionalTest source, Pseudonymizer<AdditionalTestDto> pseudonymizer) {

		if (source == null) {
			return null;
		}

		return convertToDto(source, pseudonymizer, service.inJurisdictionOrOwned(source));
	}

	private static AdditionalTestDto convertToDto(AdditionalTest source, Pseudonymizer<AdditionalTestDto> pseudonymizer, boolean inJurisdiction) {

		AdditionalTestDto dto = toDto(source);
		pseudonymizer.pseudonymizeDto(AdditionalTestDto.class, dto, inJurisdiction, null);
		return dto;
	}

	public AdditionalTest fillOrBuildEntity(@NotNull AdditionalTestDto source, AdditionalTest target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, AdditionalTest::new, checkChangeDate);

		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPH(source.getArterialVenousGasPH());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getProthrombinTime());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	public static AdditionalTestDto toDto(AdditionalTest source) {

		if (source == null) {
			return null;
		}

		AdditionalTestDto target = new AdditionalTestDto();
		DtoHelper.fillDto(target, source);

		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPH(source.getArterialVenousGasPH());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getProthrombinTime());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	@LocalBean
	@Stateless
	public static class AdditionalTestFacadeEjbLocal extends AdditionalTestFacadeEjb {

	}

}
