package de.symeda.sormas.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;

public class EntityDtoAccessHelperTest {

	private CaseDataDto caseDataDto;
	private HospitalizationDto hospitalizationDto;

	@Before
	public void setup() {
		caseDataDto = new CaseDataDto();
		caseDataDto.setDisease(Disease.DENGUE);
		caseDataDto.setUuid("ABCDEF");

		hospitalizationDto = new HospitalizationDto();
		hospitalizationDto.setDischargeDate(new Date(1600387200000L));

	}

	@Test
	public void readEntityDtoProperties() throws InvocationTargetException, IllegalAccessException {
		assertEquals(Disease.DENGUE, EntityDtoAccessHelper.getPropertyValue(caseDataDto, "dIsEaSe"));
		assertEquals("ABCDEF", EntityDtoAccessHelper.getPropertyValue(caseDataDto, "uuID"));
		assertNull(EntityDtoAccessHelper.getPropertyValue(caseDataDto, "Person"));
	}

	@Test
	public void readEntityDtoPropertyPath() {
		assertNull(EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "hospitalization.dischargeDate"));
		caseDataDto.setHospitalization(hospitalizationDto);
		assertEquals(new Date(1600387200000L), EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "hospitalization.dischargeDate"));
	}
}
