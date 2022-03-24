/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.importexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.ParseException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.user.DefaultUserRole;

public class ImportParserServiceTest extends AbstractBeanTest {

	private static TestDataCreator.RDCF rdcf;

	@Before
	public void setup() {
		rdcf = creator.createRDCF();
	}

	@Test
	public void testParseEnumFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.class),
			"CONFIRMED_NO_SYMPTOMS",
			new String[] {
				CaseDataDto.CASE_CLASSIFICATION });

		assertThat(parsed, is(CaseClassification.CONFIRMED_NO_SYMPTOMS));
	}

	@Test
	public void testParseCustomizableEnumFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(CaseDataDto.DISEASE_VARIANT, CaseDataDto.class),
			"Test Variant",
			new String[] {
				CaseDataDto.DISEASE_VARIANT });

		assertThat(parsed.getClass(), Matchers.typeCompatibleWith(DiseaseVariant.class));
		assertThat(((DiseaseVariant) parsed).getValue(), is("Test Variant"));
	}

	@Test
	public void testParseDateFieldValue() throws IntrospectionException, ImportErrorException, ParseException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(CaseDataDto.DISTRICT_LEVEL_DATE, CaseDataDto.class),
			"07/30/2021",
			new String[] {
				CaseDataDto.DISTRICT_LEVEL_DATE });

		assertThat(parsed, is(DateHelper.parseDateWithException("30/07/2021", "dd/MM/yyyy")));
	}

	@Test
	public void testParseDateTimeFieldValue() throws IntrospectionException, ImportErrorException, ParseException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(EventDto.START_DATE, EventDto.class),
			"07/30/2021 15:30",
			new String[] {
				EventDto.START_DATE });

		assertThat(parsed, is(DateHelper.parseDateTimeWithException("30.07.2021 15:30", "dd.MM.yyyy H:mm")));
	}

	@Test
	public void testParseIntegerFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(PersonDto.BIRTH_DATE_DD, PersonDto.class),
			"15",
			new String[] {
				CaseDataDto.PERSON,
				PersonDto.BIRTH_DATE_DD });

		assertThat(parsed, is(15));
	}

	@Test
	public void testParseDoubleFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(ContactDto.REPORT_LAT, ContactDto.class),
			"23.123456",
			new String[] {
				ContactDto.REPORT_LAT });

		assertThat(parsed, is(23.123456D));
	}

	@Test
	public void testParseFloatFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(CaseDataDto.REPORT_LAT_LON_ACCURACY, CaseDataDto.class),
			"10.5",
			new String[] {
				CaseDataDto.REPORT_LAT_LON_ACCURACY });

		assertThat(parsed, is(10.5F));
	}

	@Test
	public void testParseBooleanFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(ContactDto.HIGH_PRIORITY, ContactDto.class),
			"true",
			new String[] {
				ContactDto.HIGH_PRIORITY });

		assertThat(parsed, is(Boolean.TRUE));

		parsed = getImportParserService().parseValue(
			new PropertyDescriptor(ContactDto.HIGH_PRIORITY, ContactDto.class),
			"yes",
			new String[] {
				ContactDto.HIGH_PRIORITY });

		assertThat(parsed, is(Boolean.TRUE));
	}

	@Test
	public void testParseCountryFieldValue() throws IntrospectionException, ImportErrorException {
		Country country = creator.createCountry("Test Country", "test iso", "test uno");

		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(PersonDto.CITIZENSHIP, PersonDto.class),
			"Test Country",
			new String[] {
				ContactDto.PERSON,
				PersonDto.CITIZENSHIP });

		assertThat(parsed, is(country));
	}

	@Test
	public void testParseAreaFieldValue() throws IntrospectionException, ImportErrorException {
		AreaDto area = AreaDto.build();
		area.setName("Test Area");
		getAreaFacade().save(area);

		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(RegionDto.AREA, RegionDto.class),
			"Test Area",
			new String[] {
				RegionDto.AREA });

		assertThat(parsed, is(area));
	}

	@Test
	public void testParseRegionFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(CaseDataDto.RESPONSIBLE_REGION, CaseDataDto.class),
			rdcf.region.getCaption(),
			new String[] {
				CaseDataDto.RESPONSIBLE_REGION });

		assertThat(parsed, is(rdcf.region));
	}

	@Test
	public void testParseUserFieldValue() throws IntrospectionException, ImportErrorException {
		UserDto user = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));

		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(EventDto.REPORTING_USER, EventDto.class),
			user.getUserName(),
			new String[] {
				EventDto.REPORTING_USER });

		assertThat(parsed, is(user));
	}

	@Test
	public void testParseStringFieldValue() throws IntrospectionException, ImportErrorException {
		Object parsed = getImportParserService().parseValue(
			new PropertyDescriptor(EventDto.EVENT_DESC, EventDto.class),
			"Test Description",
			new String[] {
				EventDto.EVENT_DESC });

		assertThat(parsed, is("Test Description"));
	}
}
