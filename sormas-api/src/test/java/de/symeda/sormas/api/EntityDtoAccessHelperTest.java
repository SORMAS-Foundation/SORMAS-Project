/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.location.LocationDto;
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

		LocationDto address = new LocationDto();
		address.setStreet("Elm Street");
		personDto.setAddress(address);

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
			EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "blubber.blubber");
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("In CaseData: No property blubber in class CaseData", e.getMessage());
		}

		try {
			EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "disease.blubber");
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("In CaseData.disease: Disease.blubber cannot be resolved.", e.getMessage());
		}

		IReferenceDtoResolver mockResolver = mock(IReferenceDtoResolver.class);
		CachedReferenceDtoResolver cachedReferenceDtoResolver = new CachedReferenceDtoResolver(mockResolver);
		when(mockResolver.resolve(personReferenceDto)).thenReturn(personDto);
		caseDataDto.setPerson(personReferenceDto);

		try {
			EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.blubber", cachedReferenceDtoResolver);
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("In CaseData.person: No property blubber in class Person", e.getMessage());
		}

		try {
			EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName.blubber", cachedReferenceDtoResolver);
			fail("expected: IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("In CaseData.person.firstName: String.blubber cannot be resolved.", e.getMessage());
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
		when(mockResolver.resolve(personReferenceDto)).thenReturn(personDto);
		caseDataDto.setPerson(personReferenceDto);
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", cachedReferenceDtoResolver));
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValue(caseDataDto, "person.firstName", cachedReferenceDtoResolver));
		verify(mockResolver, times(1)).resolve(personReferenceDto);
	}

	@Test
	public void readPropertyValuesString() {
		caseDataDto.setPerson(personReferenceDto);
		caseDataDto.setHospitalization(hospitalizationDto);
		assertEquals("Tenzing", EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "person.firstName", referenceDtoResolver));
		assertEquals(
			YesNoUnknown.NO,
			EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "hospitalization.isolated", referenceDtoResolver));
		assertEquals(
			"9/18/2020",
			EntityDtoAccessHelper.getPropertyPathValueString(caseDataDto, "hospitalization.dischargeDate", referenceDtoResolver));
	}
}
