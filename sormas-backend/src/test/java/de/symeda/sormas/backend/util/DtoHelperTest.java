package de.symeda.sormas.backend.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class DtoHelperTest extends AbstractBeanTest {

	@Test
	public void testFillDto() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		RDCFEntities rdcf2 = creator.createRDCFEntities();
		rdcf2.facility.setType(FacilityType.LABORATORY);

		UserDto user = creator.createUser(rdcf, UserRole.ADMIN);

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

			DtoHelper.copyDtoValues(targetDto, sourceDto, false);

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

			DtoHelper.copyDtoValues(targetDto, sourceDto, false);

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
			DtoHelper.copyDtoValues(targetDto, sourceDto, false);
			assertTrue(targetDto.getEpiData().getExposures().isEmpty());

			// Check 'lead has still same entries'
			targetDto.getEpiData().setExposures(targetList1);
			sourceDto.getEpiData().setExposures(sourceList1);
			String existingUuid = targetList1.get(0).getUuid();
			DtoHelper.copyDtoValues(targetDto, sourceDto, false);

			assertEquals(targetList1.size(), targetDto.getEpiData().getExposures().size());
			assertNotNull(targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(existingUuid, targetDto.getEpiData().getExposures().get(0).getUuid());
			assertNotEquals(existingUuid, sourceDto.getEpiData().getExposures().get(0).getUuid());

			// Check 'lead has value, other has not'
			targetDto.getEpiData().setExposures(targetList2);
			sourceDto.getEpiData().setExposures(null);
			DtoHelper.copyDtoValues(targetDto, sourceDto, false);

			assertNotNull(targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(targetList2.size(), targetDto.getEpiData().getExposures().size());
			assertEquals(targetList2.get(0).getUuid(), targetDto.getEpiData().getExposures().get(0).getUuid());
			assertEquals(targetList2.get(1).getUuid(), targetDto.getEpiData().getExposures().get(1).getUuid());

			// Check 'lead has no value, other has'
			targetDto.getEpiData().setExposures(null);
			sourceDto.getEpiData().setExposures(sourceList2);
			DtoHelper.copyDtoValues(targetDto, sourceDto, false);

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
			DtoHelper.copyDtoValues(targetDto, sourceDto, false);
			assertEquals(2, targetDto.getRequestedPathogenTests().size());
		}
	}
}
