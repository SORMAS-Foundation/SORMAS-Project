/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalmessage.labmessage;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "TestReportFacade")
@RightsAllowed({
	UserRight._EXTERNAL_MESSAGE_LABORATORY_PROCESS,
	UserRight._EXTERNAL_MESSAGE_DOCTOR_DECLARATION_PROCESS })
public class TestReportFacadeEjb implements TestReportFacade {

	@EJB
	private SampleReportService sampleReportService;
	@EJB
	private TestReportService testReportService;
	@EJB
	private CountryService countryService;

	@Override
	public TestReportDto getByUuid(String uuid) {
		return toDto(testReportService.getByUuid(uuid));
	}

	@Override
	public TestReportDto saveTestReport(@Valid TestReportDto dto) {

		return saveTestReport(dto, true);
	}

	public TestReportDto saveTestReport(@Valid TestReportDto dto, boolean checkChangeDate) {

		TestReport testReport = fromDto(dto, checkChangeDate);

		testReportService.ensurePersisted(testReport);

		return toDto(testReport);
	}

	public static TestReportDto toDto(TestReport source) {
		if (source == null) {
			return null;
		}

		TestReportDto target = new TestReportDto();
		DtoHelper.fillDto(target, source);

		target.setSampleReport(SampleReportFacadeEjb.toReferenceDto(source.getSampleReport()));
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalIds(source.getTestLabExternalIds());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestType(source.getTestType());
		target.setTestTypeDetails(source.getTestTypeDetails());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setDateOfResult(source.getDateOfResult());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setTypingId(source.getTypingId());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setPreliminary(source.getPreliminary());
		target.setTestPcrTestSpecification(source.getTestPcrTestSpecification());
		target.setCqValue(source.getCqValue());
		target.setCtValueE(source.getCtValueE());
		target.setCtValueN(source.getCtValueN());
		target.setCtValueRdrp(source.getCtValueRdrp());
		target.setCtValueS(source.getCtValueS());
		target.setCtValueOrf1(source.getCtValueOrf1());
		target.setCtValueRdrpS(source.getCtValueRdrpS());
		target.setPrescriberPhysicianCode(source.getPrescriberPhysicianCode());
		target.setPrescriberFirstName(source.getPrescriberFirstName());
		target.setPrescriberLastName(source.getPrescriberLastName());
		target.setPrescriberPhoneNumber(source.getPrescriberPhoneNumber());
		target.setPrescriberAddress(source.getPrescriberAddress());
		target.setPrescriberPostalCode(source.getPrescriberPostalCode());
		target.setPrescriberCity(source.getPrescriberCity());
		target.setPrescriberCountry(CountryFacadeEjb.toReferenceDto(source.getPrescriberCountry()));
		target.setGenoTypeResult(source.getGenoTypeResult());
		target.setRsvSubtype(source.getRsvSubtype());
		target.setSpecie(source.getSpecie());
		target.setTubeNil(source.getTubeNil());
		target.setTubeNilGT10(source.getTubeNilGT10());
		target.setTubeAgTb1(source.getTubeAgTb1());
		target.setTubeAgTb1GT10(source.getTubeAgTb1GT10());
		target.setTubeAgTb2(source.getTubeAgTb2());
		target.setTubeAgTb2GT10(source.getTubeAgTb2GT10());
		target.setTubeMitogene(source.getTubeMitogene());
		target.setTubeMitogeneGT10(source.getTubeMitogeneGT10());
		target.setStrainCallStatus(source.getStrainCallStatus());

		// Drug susceptibility mappings
		target.setAmikacinMic(source.getAmikacinMic());
		target.setAmikacinSusceptibility(source.getAmikacinSusceptibility());
		target.setBedaquilineMic(source.getBedaquilineMic());
		target.setBedaquilineSusceptibility(source.getBedaquilineSusceptibility());
		target.setCapreomycinMic(source.getCapreomycinMic());
		target.setCapreomycinSusceptibility(source.getCapreomycinSusceptibility());
		target.setCiprofloxacinMic(source.getCiprofloxacinMic());
		target.setCiprofloxacinSusceptibility(source.getCiprofloxacinSusceptibility());
		target.setDelamanidMic(source.getDelamanidMic());
		target.setDelamanidSusceptibility(source.getDelamanidSusceptibility());
		target.setEthambutolMic(source.getEthambutolMic());
		target.setEthambutolSusceptibility(source.getEthambutolSusceptibility());
		target.setGatifloxacinMic(source.getGatifloxacinMic());
		target.setGatifloxacinSusceptibility(source.getGatifloxacinSusceptibility());
		target.setIsoniazidMic(source.getIsoniazidMic());
		target.setIsoniazidSusceptibility(source.getIsoniazidSusceptibility());
		target.setKanamycinMic(source.getKanamycinMic());
		target.setKanamycinSusceptibility(source.getKanamycinSusceptibility());
		target.setLevofloxacinMic(source.getLevofloxacinMic());
		target.setLevofloxacinSusceptibility(source.getLevofloxacinSusceptibility());
		target.setMoxifloxacinMic(source.getMoxifloxacinMic());
		target.setMoxifloxacinSusceptibility(source.getMoxifloxacinSusceptibility());
		target.setOfloxacinMic(source.getOfloxacinMic());
		target.setOfloxacinSusceptibility(source.getOfloxacinSusceptibility());
		target.setRifampicinMic(source.getRifampicinMic());
		target.setRifampicinSusceptibility(source.getRifampicinSusceptibility());
		target.setStreptomycinMic(source.getStreptomycinMic());
		target.setStreptomycinSusceptibility(source.getStreptomycinSusceptibility());
		target.setCeftriaxoneMic(source.getCeftriaxoneMic());
		target.setCeftriaxoneSusceptibility(source.getCeftriaxoneSusceptibility());
		target.setPenicillinMic(source.getPenicillinMic());
		target.setPenicillinSusceptibility(source.getPenicillinSusceptibility());
		target.setErythromycinMic(source.getErythromycinMic());
		target.setErythromycinSusceptibility(source.getErythromycinSusceptibility());

		target.setSeroGroupSpecification(source.getSeroGroupSpecification());
		target.setSeroGroupSpecificationText(source.getSeroGroupSpecificationText());
		target.setSeroTypingMethod(source.getSeroTypingMethod());
		target.setSeroTypingMethodText(source.getSeroTypingMethodText());
		target.setSerotype(source.getSerotype());

		return target;
	}

	public TestReport fillOrBuildEntity(@NotNull TestReportDto source, @NotNull SampleReport sampleReport, boolean checkChangeDate) {
		TestReport target = DtoHelper.fillOrBuildEntity(source, testReportService.getByUuid(source.getUuid()), TestReport::new, checkChangeDate);

		target.setSampleReport(sampleReport);
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalIds(source.getTestLabExternalIds());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestType(source.getTestType());
		target.setTestTypeDetails(source.getTestTypeDetails());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setDateOfResult(source.getDateOfResult());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setTypingId(source.getTypingId());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setPreliminary(source.getPreliminary());
		target.setTestPcrTestSpecification(source.getTestPcrTestSpecification());
		target.setCqValue(source.getCqValue());
		target.setCtValueE(source.getCtValueE());
		target.setCtValueN(source.getCtValueN());
		target.setCtValueRdrp(source.getCtValueRdrp());
		target.setCtValueS(source.getCtValueS());
		target.setCtValueOrf1(source.getCtValueOrf1());
		target.setCtValueRdrpS(source.getCtValueRdrpS());
		target.setPrescriberPhysicianCode(source.getPrescriberPhysicianCode());
		target.setPrescriberFirstName(source.getPrescriberFirstName());
		target.setPrescriberLastName(source.getPrescriberLastName());
		target.setPrescriberPhoneNumber(source.getPrescriberPhoneNumber());
		target.setPrescriberAddress(source.getPrescriberAddress());
		target.setPrescriberPostalCode(source.getPrescriberPostalCode());
		target.setPrescriberCity(source.getPrescriberCity());
		target.setPrescriberCountry(countryService.getByReferenceDto(source.getPrescriberCountry()));
		target.setGenoTypeResult(source.getGenoTypeResult());
		target.setRsvSubtype(source.getRsvSubtype());
		target.setSpecie(source.getSpecie());
		target.setTubeNil(source.getTubeNil());
		target.setTubeNilGT10(source.getTubeNilGT10());
		target.setTubeAgTb1(source.getTubeAgTb1());
		target.setTubeAgTb1GT10(source.getTubeAgTb1GT10());
		target.setTubeAgTb2(source.getTubeAgTb2());
		target.setTubeAgTb2GT10(source.getTubeAgTb2GT10());
		target.setTubeMitogene(source.getTubeMitogene());
		target.setTubeMitogeneGT10(source.getTubeMitogeneGT10());
		target.setStrainCallStatus(source.getStrainCallStatus());

		// Drug susceptibility mappings
		target.setAmikacinMic(source.getAmikacinMic());
		target.setAmikacinSusceptibility(source.getAmikacinSusceptibility());
		target.setBedaquilineMic(source.getBedaquilineMic());
		target.setBedaquilineSusceptibility(source.getBedaquilineSusceptibility());
		target.setCapreomycinMic(source.getCapreomycinMic());
		target.setCapreomycinSusceptibility(source.getCapreomycinSusceptibility());
		target.setCiprofloxacinMic(source.getCiprofloxacinMic());
		target.setCiprofloxacinSusceptibility(source.getCiprofloxacinSusceptibility());
		target.setDelamanidMic(source.getDelamanidMic());
		target.setDelamanidSusceptibility(source.getDelamanidSusceptibility());
		target.setEthambutolMic(source.getEthambutolMic());
		target.setEthambutolSusceptibility(source.getEthambutolSusceptibility());
		target.setGatifloxacinMic(source.getGatifloxacinMic());
		target.setGatifloxacinSusceptibility(source.getGatifloxacinSusceptibility());
		target.setIsoniazidMic(source.getIsoniazidMic());
		target.setIsoniazidSusceptibility(source.getIsoniazidSusceptibility());
		target.setKanamycinMic(source.getKanamycinMic());
		target.setKanamycinSusceptibility(source.getKanamycinSusceptibility());
		target.setLevofloxacinMic(source.getLevofloxacinMic());
		target.setLevofloxacinSusceptibility(source.getLevofloxacinSusceptibility());
		target.setMoxifloxacinMic(source.getMoxifloxacinMic());
		target.setMoxifloxacinSusceptibility(source.getMoxifloxacinSusceptibility());
		target.setOfloxacinMic(source.getOfloxacinMic());
		target.setOfloxacinSusceptibility(source.getOfloxacinSusceptibility());
		target.setRifampicinMic(source.getRifampicinMic());
		target.setRifampicinSusceptibility(source.getRifampicinSusceptibility());
		target.setStreptomycinMic(source.getStreptomycinMic());
		target.setStreptomycinSusceptibility(source.getStreptomycinSusceptibility());
		target.setCeftriaxoneMic(source.getCeftriaxoneMic());
		target.setCeftriaxoneSusceptibility(source.getCeftriaxoneSusceptibility());
		target.setPenicillinMic(source.getPenicillinMic());
		target.setPenicillinSusceptibility(source.getPenicillinSusceptibility());
		target.setErythromycinMic(source.getErythromycinMic());
		target.setErythromycinSusceptibility(source.getErythromycinSusceptibility());

		target.setSeroGroupSpecification(source.getSeroGroupSpecification());
		target.setSeroGroupSpecificationText(source.getSeroGroupSpecificationText());
		target.setSeroTypingMethod(source.getSeroTypingMethod());
		target.setSeroTypingMethodText(source.getSeroTypingMethodText());
		target.setSerotype(source.getSerotype());

		return target;
	}

	public TestReport fromDto(@NotNull TestReportDto source, boolean checkChangeDate) {
		SampleReport sampleReport = sampleReportService.getByReferenceDto(source.getSampleReport());

		return fillOrBuildEntity(source, sampleReport, checkChangeDate);
	}

	@LocalBean
	@Stateless
	public static class TestReportFacadeEjbLocal extends TestReportFacadeEjb {

	}
}
