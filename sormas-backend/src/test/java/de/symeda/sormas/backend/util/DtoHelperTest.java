package de.symeda.sormas.backend.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class DtoHelperTest extends AbstractBeanTest {

	@Test
	public void testMergeDto() throws Exception {

		// Test simple values
		{
			VisitDto leadDto = new VisitDto();
			VisitDto otherDto = new VisitDto();

			// lead and other have different values
			Double reportLat = 1.234;
			leadDto.setReportLat(reportLat);
			otherDto.setReportLat(2.345);

			// lead has value, other has not
			Double reportLon = 3.456;
			leadDto.setReportLon(reportLon);

			// lead has no value, other has
			Float reportLatLonAccuracy = (float) 4.567;
			otherDto.setReportLatLonAccuracy(reportLatLonAccuracy);

			VisitDto merged = DtoHelper.mergeDto(leadDto, otherDto, false, false);

			// Check no values
			assertNull(merged.getDisease());

			// Check 'lead and other have different values'
			assertEquals(reportLat, merged.getReportLat());

			// Check 'lead has value, other has not'
			assertEquals(reportLon, merged.getReportLon());

			// Check 'lead has no value, other has'
			assertEquals(reportLatLonAccuracy, merged.getReportLatLonAccuracy());
		}

		// Test complex subDto
		{
			CaseDataDto leadCaseDto = new CaseDataDto();
			CaseDataDto otherCaseDto = new CaseDataDto();

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
