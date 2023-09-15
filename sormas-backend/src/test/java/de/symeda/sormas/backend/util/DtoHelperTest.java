package de.symeda.sormas.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.ReinfectionDetail;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class DtoHelperTest extends AbstractBeanTest {

	@Test
	public void testFillDto() {

		RDCF rdcf = creator.createRDCF();
		RDCF rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Lab", "Point of Entry 2");

		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Test simple values
		{
			HealthConditionsDto targetDto = new HealthConditionsDto();
			HealthConditionsDto sourceDto = new HealthConditionsDto();

			// lead and other have different values
			targetDto.setTuberculosis(YesNoUnknown.YES);
			sourceDto.setTuberculosis(YesNoUnknown.NO);

			// lead has value, other has not
			targetDto.setAsplenia(YesNoUnknown.YES);

			// lead has no value, other has
			sourceDto.setDiabetes(YesNoUnknown.YES);

			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);

			// Check no values
			assertNull(targetDto.getHiv());

			// Check 'lead and other have different values'
			assertNotEquals(sourceDto.getTuberculosis(), targetDto.getTuberculosis());

			// Check 'lead has value, other has not'
			assertEquals(YesNoUnknown.YES, targetDto.getAsplenia());
			assertNull(sourceDto.getAsplenia());

			// Check 'lead has no value, other has'
			assertEquals(sourceDto.getDiabetes(), targetDto.getDiabetes());
		}

		// Test complex subDto
		{
			PersonDto person = creator.createPerson("First", "Last");
			CaseDataDto targetDto = creator.createCase(user.toReference(), person.toReference(), rdcf);
			CaseDataDto sourceDto = creator.createCase(user.toReference(), person.toReference(), rdcf);

			SymptomsDto targetSymptomsDto = targetDto.getSymptoms();
			SymptomsDto sourceSymptomsDto = sourceDto.getSymptoms();

			// lead and other have different values
			SymptomState abdominalPain = SymptomState.NO;
			targetSymptomsDto.setAbdominalPain(abdominalPain);
			sourceSymptomsDto.setAbdominalPain(SymptomState.UNKNOWN);

			// lead has value, other has not
			SymptomState alteredConsciousness = SymptomState.YES;
			targetSymptomsDto.setAlteredConsciousness(alteredConsciousness);

			// lead has no value, other has
			SymptomState anorexiaAppetiteLoss = SymptomState.UNKNOWN;
			sourceSymptomsDto.setAnorexiaAppetiteLoss(anorexiaAppetiteLoss);

			targetDto.setSymptoms(targetSymptomsDto);
			sourceDto.setSymptoms(sourceSymptomsDto);

			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);

			// Check no values
			assertNull(targetDto.getSymptoms().getBackache());

			// Check 'lead and other have different values'
			assertEquals(abdominalPain, targetDto.getSymptoms().getAbdominalPain());

			// Check 'lead has value, other has not'
			assertEquals(alteredConsciousness, targetDto.getSymptoms().getAlteredConsciousness());

			// Check 'lead has no value, other has'
			assertEquals(anorexiaAppetiteLoss, targetDto.getSymptoms().getAnorexiaAppetiteLoss());
		}

		// Test List
		{
			PersonDto person = creator.createPerson("First", "Last");
			CaseDataDto targetDto = creator.createCase(user.toReference(), person.toReference(), rdcf);
			CaseDataDto sourceDto = creator.createCase(user.toReference(), person.toReference(), rdcf);

			ExposureDto subDto1 = ExposureDto.build(ExposureType.TRAVEL);
			ExposureDto subDto2 = ExposureDto.build(ExposureType.TRAVEL);

			// lead and other have different values
			ArrayList<ExposureDto> targetList1 = new ArrayList<>();
			targetList1.add(subDto1);

			ArrayList<ExposureDto> sourceList1 = new ArrayList<>();
			sourceList1.add(subDto2);

			// lead has values, other has not
			ArrayList<ExposureDto> targetList2 = new ArrayList<>();
			targetList2.add(subDto1);
			targetList2.add(subDto2);

			// lead has no values, other has
			ArrayList<ExposureDto> sourceList2 = new ArrayList<>();
			sourceList2.add(subDto1);
			sourceList2.add(subDto2);

			// Check no values
			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);
			assertTrue(targetDto.getEpiData().getExposures().isEmpty());

			// Check 'lead has still same entries'
			targetDto.getEpiData().setExposures(targetList1);
			sourceDto.getEpiData().setExposures(sourceList1);
			String existingUuid = targetList1.get(0).getUuid();
			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);

			assertEquals(targetList1.size(), targetDto.getEpiData().getExposures().size());
			assertNotNull(targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(existingUuid, targetDto.getEpiData().getExposures().get(0).getUuid());
			assertNotEquals(existingUuid, sourceDto.getEpiData().getExposures().get(0).getUuid());

			// Check 'lead has value, other has not'
			targetDto.getEpiData().setExposures(targetList2);
			sourceDto.getEpiData().setExposures(null);
			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);

			assertNotNull(targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(targetList2.size(), targetDto.getEpiData().getExposures().size());
			assertEquals(targetList2.get(0).getUuid(), targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(targetList2.get(1).getUuid(), targetDto.getEpiData().getExposures().get(1).getUuid());

			// Check 'lead has no value, other has'
			targetDto.getEpiData().setExposures(null);
			sourceDto.getEpiData().setExposures(sourceList2);
			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);

			assertNotNull(targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(sourceList2.size(), targetDto.getEpiData().getExposures().size());
			assertNotEquals(sourceList2.get(0).getUuid(), targetDto.getEpiData().getExposures().get(0).getUuid());
			assertNotEquals(sourceList2.get(1).getUuid(), targetDto.getEpiData().getExposures().get(1).getUuid());
		}

		// test non-entity list
		{
			PersonDto person = creator.createPerson("First", "Last");
			CaseDataDto targetCaseDto = creator.createCase(user.toReference(), person.toReference(), rdcf);
			CaseDataDto sourceCaseDto = creator.createCase(user.toReference(), person.toReference(), rdcf);

			SampleDto sourceDto = creator.createSample(sourceCaseDto.toReference(), user.toReference(), rdcf2.facility);
			sourceDto.setPathogenTestingRequested(true);
			sourceDto.getRequestedPathogenTests().add(PathogenTestType.ANTIGEN_DETECTION);
			sourceDto.getRequestedPathogenTests().add(PathogenTestType.NEUTRALIZING_ANTIBODIES);

			SampleDto targetDto = SampleDto.build(user.toReference(), targetCaseDto.toReference());
			DtoCopyHelper.copyDtoValues(targetDto, sourceDto, false);
			assertEquals(2, targetDto.getRequestedPathogenTests().size());
		}

		// test map
		{
			RDCF caseRdcf = creator.createRDCF();

			Map<ReinfectionDetail, Boolean> map1 = new EnumMap<>(ReinfectionDetail.class);
			map1.put(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, true);
			map1.put(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, true);
			map1.put(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, false);

			PersonDto person = creator.createPerson();
			CaseDataDto sourceCase = creator.createCase(user.toReference(), person.toReference(), caseRdcf);
			CaseDataDto targetCase = creator.createCase(user.toReference(), person.toReference(), caseRdcf);

			// Map must not be persisted because H2 can't map it to JSON
			sourceCase.setReinfectionDetails(map1);

			DtoCopyHelper.copyDtoValues(targetCase, sourceCase, false);
			assertEquals(3, targetCase.getReinfectionDetails().size());

			sourceCase.getReinfectionDetails().put(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, false);
			sourceCase.getReinfectionDetails().put(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, true);

			DtoCopyHelper.copyDtoValues(targetCase, sourceCase, false);
			assertEquals(4, targetCase.getReinfectionDetails().size());
			assertTrue(targetCase.getReinfectionDetails().get(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION));

			DtoCopyHelper.copyDtoValues(targetCase, sourceCase, true);
			assertEquals(4, targetCase.getReinfectionDetails().size());
			assertFalse(targetCase.getReinfectionDetails().get(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION));

			sourceCase.setReinfectionDetails(null);
			DtoCopyHelper.copyDtoValues(targetCase, sourceCase, false);
			assertEquals(4, targetCase.getReinfectionDetails().size());
			DtoCopyHelper.copyDtoValues(targetCase, sourceCase, true);
			assertEquals(4, targetCase.getReinfectionDetails().size());
		}
	}
}
