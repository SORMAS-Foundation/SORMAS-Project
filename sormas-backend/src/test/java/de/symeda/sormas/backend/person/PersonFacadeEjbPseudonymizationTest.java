/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.PresentCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class PersonFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;
	private PersonDto person;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1");
		user1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		user2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Before()
	public void beforeEach() {
		person = createPerson();
	}

	@Test
	public void testGetCasePersonInSameJurisdiction() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf2);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonOutsideJurisdiction() {
		creator.createCase(user1.toReference(), person.toReference(), rdcf1);

		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateCasePersonInJurisdiction() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf2);
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateCasePersonOutsideJurisdiction() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf1);
		updatePerson(true);
		assertPersonNotUpdated();
	}

	@Test
	public void testUpdateCasePersonInJurisdictionWithPseudonymizedDto() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf2);
		updatePersonPseudonymizedDto();
		assertPersonNotUpdated();
	}

	@Test
	public void testGetContactPersonInSameJurisdiction() {

		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf2);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetContactPersonOutsideJurisdiction() {

		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);
		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateContactPersonInJurisdiction() {

		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf2);
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateContactPersonOutsideJurisdiction() {

		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);
		updatePerson(true);
		assertPersonNotUpdated();
	}

	@Test
	public void testGetEventParticipantPersonInSameJurisdiction() {

		EventDto event = creator.createEvent(user2.toReference());
		creator.createEventParticipant(event.toReference(), person);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetEventParticipantPersonOutsideJurisdiction() {

		EventDto event = creator.createEvent(user1.toReference());
		creator.createEventParticipant(event.toReference(), person);
		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateEventParticipantPersonInJurisdiction() {

		EventDto event = creator.createEvent(user2.toReference());
		creator.createEventParticipant(event.toReference(), person);
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateEventParticipantPersonOutsideJurisdiction() {

		EventDto event = creator.createEvent(user1.toReference());
		creator.createEventParticipant(event.toReference(), person);
		updatePerson(true);
		assertPersonNotUpdated();
	}

	@Test
	public void testPseudonymizeGetByUuids() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf2);
		PersonDto person2 = createPerson();
		List<PersonDto> persons = getPersonFacade().getByUuids(Arrays.asList(person.getUuid(), person2.getUuid()));
		assertNotPseudonymized(persons.stream().filter(p -> p.getUuid().equals(person.getUuid())).findFirst().get());
		assertPseudonymised(persons.stream().filter(p -> p.getUuid().equals(person2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllAfter() {

		creator.createCase(user1.toReference(), person.toReference(), rdcf2);

		PersonDto person2 = createPerson();
		//create redonly case with person2 --> person2 should be pseudonymized
		CaseDataDto caze = creator.createCase(user1.toReference(), person2.toReference(), rdcf1);
		creator.createContact(user2.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<PersonDto> persons = getPersonFacade().getPersonsAfter(calendar.getTime());

		assertNotPseudonymized(persons.stream().filter(p -> p.getUuid().equals(person.getUuid())).findFirst().get());
		assertPseudonymised(persons.stream().filter(p -> p.getUuid().equals(person2.getUuid())).findFirst().get());
	}

	private PersonDto createPerson() {

		LocationDto address = new LocationDto();
		address.setRegion(rdcf1.region);
		address.setDistrict(rdcf1.district);
		address.setCommunity(rdcf1.community);
		address.setCity("Test City");
		address.setAddress("Test address");
		address.setPostalCode("12345");
		address.setAreaType(AreaType.URBAN);
		address.setDetails("Test address details");
		address.setLongitude(46.233);
		address.setLatitude(26.533);
		address.setLatLonAccuracy(10F);

		return creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, p -> {
			p.setAddress(address);

			p.setPhone("1234567");
			p.setPresentCondition(PresentCondition.DEAD);
			p.setCauseOfDeathDetails("Test cause of death details");
			p.setPassportNumber("Test passport num");
		});
	}

	private void updatePerson(boolean pseudonymized) {

		person.setPseudonymized(pseudonymized);
		person.setFirstName("Newfirstname");
		person.setLastName("Newlastname");
		person.setBirthdateDD(23);

		LocationDto newAddress = new LocationDto();
		person.setPseudonymized(pseudonymized);
		newAddress.setRegion(rdcf1.region);
		newAddress.setDistrict(rdcf1.district);
		newAddress.setCommunity(rdcf1.community);
		newAddress.setCity("New City");
		newAddress.setAddress("New address");
		newAddress.setPostalCode("938");
		newAddress.setAreaType(AreaType.RURAL);
		newAddress.setDetails("New address details");
		newAddress.setLongitude(36.233);
		newAddress.setLatitude(36.533);
		newAddress.setLatLonAccuracy(8F);

		person.setAddress(newAddress);

		getPersonFacade().savePerson(person);
	}

	private void updatePersonPseudonymizedDto() {

		person.setPseudonymized(true);
		person.setFirstName("");
		person.setLastName("");
		person.setBirthdateDD(null);

		LocationDto newAddress = new LocationDto();
		newAddress.setPseudonymized(true);
		newAddress.setRegion(rdcf1.region);
		newAddress.setDistrict(rdcf1.district);
		newAddress.setCommunity(null);
		newAddress.setCity("");
		newAddress.setAddress("");
		newAddress.setPostalCode("");
		newAddress.setAreaType(null);
		newAddress.setDetails("");
		newAddress.setLongitude(null);
		newAddress.setLatitude(null);
		newAddress.setLatLonAccuracy(null);

		person.setAddress(newAddress);

		getPersonFacade().savePerson(person);
	}

	private void assertNotPseudonymized(PersonDto person) {

		assertThat(person.isPseudonymized(), is(false));
		assertThat(person.getFirstName(), is("James"));
		assertThat(person.getLastName(), is("Smith"));
		assertThat(person.getBirthdateDD(), is(1));

		assertThat(person.getAddress().getRegion().getCaption(), is("Region 1"));
		assertThat(person.getAddress().getDistrict().getCaption(), is("District 1"));
		assertThat(person.getAddress().getCommunity(), is(rdcf1.community));
		assertThat(person.getAddress().getCity(), is("Test City"));
		assertThat(person.getAddress().getAddress(), is("Test address"));
		assertThat(person.getAddress().getPostalCode(), is("12345"));
		assertThat(person.getAddress().getAreaType(), is(AreaType.URBAN));
		assertThat(person.getAddress().getDetails(), is("Test address details"));
		assertThat(person.getAddress().getLongitude(), is(46.233));
		assertThat(person.getAddress().getLatitude(), is(26.533));
		assertThat(person.getAddress().getLatLonAccuracy(), is(10F));

		// sensitive data
		assertThat(person.getPhone(), is("1234567"));
		assertThat(person.getCauseOfDeathDetails(), is("Test cause of death details"));
		assertThat(person.getPassportNumber(), is("Test passport num"));
	}

	private void assertPseudonymised(PersonDto person) {

		assertThat(person.isPseudonymized(), is(true));
		assertThat(person.getFirstName(), isEmptyString());
		assertThat(person.getLastName(), isEmptyString());
		assertThat(person.getBirthdateDD(), is(nullValue()));

		assertThat(person.getAddress().getRegion().getCaption(), is("Region 1"));
		assertThat(person.getAddress().getDistrict().getCaption(), is("District 1"));
		assertThat(person.getAddress().getCommunity(), is(nullValue()));
		assertThat(person.getAddress().getCity(), isEmptyString());
		assertThat(person.getAddress().getAddress(), isEmptyString());
		assertThat(person.getAddress().getPostalCode(), isEmptyString());
		assertThat(person.getAddress().getAreaType(), is(nullValue()));
		assertThat(person.getAddress().getDetails(), isEmptyString());
		assertThat(person.getAddress().getLongitude(), is(nullValue()));
		assertThat(person.getAddress().getLatitude(), is(nullValue()));
		assertThat(person.getAddress().getLatLonAccuracy(), is(nullValue()));

		// sensitive data
		assertThat(person.getPhone(), isEmptyString());
		assertThat(person.getCauseOfDeathDetails(), isEmptyString());
		assertThat(person.getPassportNumber(), isEmptyString());
	}

	private void assertPersonUpdated() {

		Person savedPerson = getPersonService().getByUuid(person.getUuid());

		assertThat(savedPerson.getFirstName(), is("Newfirstname"));
		assertThat(savedPerson.getLastName(), is("Newlastname"));
		assertThat(savedPerson.getBirthdateDD(), is(23));
		assertThat(savedPerson.getAddress().getRegion().getName(), is(rdcf1.region.getCaption()));
		assertThat(savedPerson.getAddress().getDistrict().getName(), is(rdcf1.district.getCaption()));
		assertThat(savedPerson.getAddress().getCommunity().getName(), is(rdcf1.community.getCaption()));
		assertThat(savedPerson.getAddress().getCity(), is("New City"));
		assertThat(savedPerson.getAddress().getAddress(), is("New address"));
		assertThat(savedPerson.getAddress().getPostalCode(), is("938"));
		assertThat(savedPerson.getAddress().getAreaType(), is(AreaType.RURAL));
		assertThat(savedPerson.getAddress().getDetails(), is("New address details"));
		assertThat(savedPerson.getAddress().getLongitude(), is(36.233));
		assertThat(savedPerson.getAddress().getLatitude(), is(36.533));
		assertThat(savedPerson.getAddress().getLatLonAccuracy(), is(8F));
	}

	private void assertPersonNotUpdated() {

		Person savedPerson = getPersonService().getByUuid(person.getUuid());

		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));
		assertThat(savedPerson.getBirthdateDD(), is(1));

		assertThat(savedPerson.getAddress().getRegion().getName(), is("Region 1"));
		assertThat(savedPerson.getAddress().getDistrict().getName(), is("District 1"));
		assertThat(savedPerson.getAddress().getCommunity().getName(), is("Community 1"));
		assertThat(savedPerson.getAddress().getCity(), is("Test City"));
		assertThat(savedPerson.getAddress().getAddress(), is("Test address"));
		assertThat(savedPerson.getAddress().getPostalCode(), is("12345"));
		assertThat(savedPerson.getAddress().getAreaType(), is(AreaType.URBAN));
		assertThat(savedPerson.getAddress().getDetails(), is("Test address details"));
		assertThat(savedPerson.getAddress().getLongitude(), is(46.233));
		assertThat(savedPerson.getAddress().getLatitude(), is(26.533));
		assertThat(savedPerson.getAddress().getLatLonAccuracy(), is(10F));
	}
}
