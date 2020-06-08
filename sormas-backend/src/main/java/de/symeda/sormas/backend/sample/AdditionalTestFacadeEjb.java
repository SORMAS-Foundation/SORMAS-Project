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
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

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

	@Override
	public AdditionalTestDto saveAdditionalTest(AdditionalTestDto additionalTest) {

		AdditionalTest entity = fromDto(additionalTest);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void deleteAdditionalTest(String additionalTestUuid) {

		User user = userService.getCurrentUser();
		// TODO replace this with a proper user right call #944
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities");
		}

		AdditionalTest additionalTest = service.getByUuid(additionalTestUuid);
		service.delete(additionalTest);
	}

	@Override
	public List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveAdditionalTestsAfter(date, user).stream().map(e -> toDto(e)).collect(Collectors.toList());
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

	public AdditionalTest fromDto(@NotNull AdditionalTestDto source) {

		AdditionalTest target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new AdditionalTest();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

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
		target.setProthrombinTime(source.getPlatelets());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	public AdditionalTestDto toDto(AdditionalTest source) {

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
		target.setProthrombinTime(source.getPlatelets());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	@LocalBean
	@Stateless
	public static class AdditionalTestFacadeEjbLocal extends AdditionalTestFacadeEjb {

	}

}
