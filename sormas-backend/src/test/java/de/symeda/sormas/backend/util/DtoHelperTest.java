package de.symeda.sormas.backend.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class DtoHelperTest extends AbstractBeanTest {

	@Test
	public void testMergeDto() throws Exception {

		// Test simple values
		{
			HealthConditionsDto leadDto = new HealthConditionsDto();
			HealthConditionsDto otherDto = new HealthConditionsDto();

			// lead and other have different values
			leadDto.setTuberculosis(YesNoUnknown.YES);
			otherDto.setTuberculosis(YesNoUnknown.NO);

			// lead has value, other has not
			leadDto.setAsplenia(YesNoUnknown.YES);

			// lead has no value, other has
			otherDto.setDiabetes(YesNoUnknown.YES);

			HealthConditionsDto merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);

			// Check no values
			assertNull(merged.getHiv());

			// Check 'lead and other have different values'
			assertNotEquals(otherDto.getTuberculosis(), merged.getTuberculosis());

			// Check 'lead has value, other has not'
			assertEquals(YesNoUnknown.YES, merged.getAsplenia());
			assertNull(otherDto.getAsplenia());

			// Check 'lead has no value, other has'
			assertEquals(otherDto.getDiabetes(), merged.getDiabetes());
		}

		// Test complex subDto
		{
			RDCFEntities rdcf = creator.createRDCFEntities();
			UserDto user = creator.createUser(rdcf, UserRole.ADMIN);
			PersonDto person = creator.createPerson("First", "Last");
			CaseDataDto leadCaseDto = creator.createCase(user.toReference(), person.toReference(), rdcf);
			CaseDataDto otherCaseDto = creator.createCase(user.toReference(), person.toReference(), rdcf);

			SymptomsDto leadSymptomsDto = new SymptomsDto();
			SymptomsDto otherSymptomsDto = new SymptomsDto();

			// lead and other have different values
			SymptomState abdominalPain = SymptomState.NO;
			leadSymptomsDto.setAbdominalPain(abdominalPain);
			otherSymptomsDto.setAbdominalPain(SymptomState.UNKNOWN);

			// lead has value, other has not
			SymptomState alteredConsciousness = SymptomState.YES;
			leadSymptomsDto.setAlteredConsciousness(alteredConsciousness);

			// lead has no value, other has
			SymptomState anorexiaAppetiteLoss = SymptomState.UNKNOWN;
			otherSymptomsDto.setAnorexiaAppetiteLoss(anorexiaAppetiteLoss);

			leadCaseDto.setSymptoms(leadSymptomsDto);
			otherCaseDto.setSymptoms(otherSymptomsDto);

			CaseDataDto merged = DtoHelper.mergeDto(leadCaseDto, otherCaseDto, false, false);

			// Check no values
			assertNull(merged.getSymptoms().getBackache());

			// Check 'lead and other have different values'
			assertEquals(abdominalPain, merged.getSymptoms().getAbdominalPain());

			// Check 'lead has value, other has not'
			assertEquals(alteredConsciousness, merged.getSymptoms().getAlteredConsciousness());

			// Check 'lead has no value, other has'
			assertEquals(anorexiaAppetiteLoss, merged.getSymptoms().getAnorexiaAppetiteLoss());
		}

		// Test List
		{
			HospitalizationDto leadDto = new HospitalizationDto();
			HospitalizationDto otherDto = new HospitalizationDto();

			PreviousHospitalizationDto subDto1 = new PreviousHospitalizationDto();
			PreviousHospitalizationDto subDto2 = new PreviousHospitalizationDto();

			// lead and other have different values
			ArrayList<PreviousHospitalizationDto> leadList1 = new ArrayList<PreviousHospitalizationDto>();
			leadList1.add(subDto1);

			ArrayList<PreviousHospitalizationDto> otherList1 = new ArrayList<PreviousHospitalizationDto>();
			otherList1.add(subDto2);

			// lead has values, other has not
			ArrayList<PreviousHospitalizationDto> leadList2 = new ArrayList<PreviousHospitalizationDto>();
			leadList2.add(subDto1);
			leadList2.add(subDto2);

			// lead has no values, other has
			ArrayList<PreviousHospitalizationDto> otherList2 = new ArrayList<PreviousHospitalizationDto>();
			otherList2.add(subDto1);
			otherList2.add(subDto2);

			// Check no values
			HospitalizationDto merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);
			assertTrue(merged.getPreviousHospitalizations().isEmpty());

			// Check 'lead and other have different values'
			leadDto.setPreviousHospitalizations(leadList1);
			otherDto.setPreviousHospitalizations(otherList1);
			merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);
			assertEquals(leadList1.size(), merged.getPreviousHospitalizations().size());
			assertEquals(leadList1.get(0).getUuid(), merged.getPreviousHospitalizations().get(0).getUuid());

			// Check 'lead has value, other has not'
			leadDto.setPreviousHospitalizations(leadList2);
			otherDto.setPreviousHospitalizations(null);
			merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);
			assertEquals(leadList2.size(), merged.getPreviousHospitalizations().size());
			assertEquals(leadList2.get(0).getUuid(), merged.getPreviousHospitalizations().get(0).getUuid());
			assertEquals(leadList2.get(1).getUuid(), merged.getPreviousHospitalizations().get(1).getUuid());

			// Check 'lead has no value, other has'
			leadDto.setPreviousHospitalizations(null);
			otherDto.setPreviousHospitalizations(otherList2);
			merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);
			assertEquals(otherList2.size(), merged.getPreviousHospitalizations().size());
			assertEquals(otherList2.get(0).getUuid(), merged.getPreviousHospitalizations().get(0).getUuid());
			assertEquals(otherList2.get(1).getUuid(), merged.getPreviousHospitalizations().get(1).getUuid());
		}
	}
}
