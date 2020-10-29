package de.symeda.sormas.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.EntityDtoAccessHelper.CachedReferenceDtoResolver;
import de.symeda.sormas.api.EntityDtoAccessHelper.IReferenceDtoResolver;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EntityDtoAccessHelperTest {

	private CaseDataDto caseDataDto;
	private HospitalizationDto hospitalizationDto;
	private PersonReferenceDto personReferenceDto;
	private PersonDto personDto;
	private IReferenceDtoResolver referenceDtoResolver;

	@Before
	public void setup() {
		caseDataDto = new CaseDataDto();
		caseDataDto.setDisease(Disease.DENGUE);
		caseDataDto.setUuid("ABCDEF");

		hospitalizationDto = new HospitalizationDto();
		hospitalizationDto.setDischargeDate(new Date(1600387200000L));
		hospitalizationDto.setIsolated(YesNoUnknown.NO);

		personReferenceDto = new PersonReferenceDto();
		personReferenceDto.setUuid("GHIJKL");

		personDto = new PersonDto();
		personDto.setUuid("GHIJKL");
		personDto.setFirstName("Tenzing");
		personDto.setLastName("Mike");
		personDto.setBirthdateDD(26);
		personDto.setBirthdateMM(11);
		personDto.setBirthdateYYYY(1973);
		personDto.setPhone("+49 681 1234");

		referenceDtoResolver = new IReferenceDtoResolver() {

			@Override
			public EntityDto resolve(ReferenceDto referenceDto) {
				if (referenceDto != null && "GHIJKL".equals(referenceDto.getUuid())) {
					return personDto;
				}
				return null;
			}
		};
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

	@Test
	public void failOnImplausibleProperty() {
		try {
			assertNull(EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "blubber.blubber"));
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("No property blubber in class CaseDataDto", e.getMessage());
		}

		try {
			assertNull(EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "disease.blubber"));
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("CaseDataDto.disease is not an EntityDto or ReferenceDto", e.getMessage());
		}
	}

	@Test
	public void readReferencedEntityDto() {
		IReferenceDtoResolver referenceDtoResolver = new EntityDtoAccessHelper.IReferenceDtoResolver() {

			@Override
			public EntityDto resolve(ReferenceDto referenceDto) {
				if (referenceDto != null && "GHIJKL".equals(referenceDto.getUuid())) {
					return personDto;
				}
				return null;
			}
		};

		assertNull(EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", null));
		caseDataDto.setPerson(personReferenceDto);
		assertNull(EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", null));
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", referenceDtoResolver));
		assertEquals(26, EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.BirthdateDD", referenceDtoResolver));
		assertEquals(11, EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.BirthdateMM", referenceDtoResolver));
		assertEquals(1973, EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.BirthdateYYYY", referenceDtoResolver));
		assertEquals("+49 681 1234", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.phone", referenceDtoResolver));
	}

	@Test
	public void readCachedReferencedEntityDto() {
		IReferenceDtoResolver mockResolver = mock(IReferenceDtoResolver.class);
		CachedReferenceDtoResolver cachedReferenceDtoResolver = new CachedReferenceDtoResolver(mockResolver);
		caseDataDto.setPerson(personReferenceDto);
		when(mockResolver.resolve(personReferenceDto)).thenReturn(personDto);
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", cachedReferenceDtoResolver));
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", cachedReferenceDtoResolver));
		verify(mockResolver, times(1)).resolve(personReferenceDto);
	}

	@Test
	public void readPropertyValuesString() {
		caseDataDto.setPerson(personReferenceDto);
		caseDataDto.setHospitalization(hospitalizationDto);
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "person.firstName", referenceDtoResolver));
		assertEquals("No", EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "hospitalization.isolated", referenceDtoResolver));
		assertEquals(
			"9/18/2020",
			EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "hospitalization.dischargeDate", referenceDtoResolver));
	}
}
