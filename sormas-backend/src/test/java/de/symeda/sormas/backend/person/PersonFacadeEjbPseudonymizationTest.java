/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.person;

import de.symeda.sormas.api.Disease;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

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
		user1 = creator.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(),
				"Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		user2 = creator.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(),
				"Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Before()
	public void beforeEach() {
		person = createPerson();
	}

	@Test
	public void testGetCasePersonInSameJurisdiction() {
		creator.createCase(user1.toReference(), person.toReference(), rdcf2);

		assertNotPseudonomyzed(person);
	}

	@Test
	public void testGetCasePersonInOtherJurisdiction() {
		creator.createCase(user1.toReference(), person.toReference(), rdcf1);

		assertPsoudonimysed(person);
	}

	@Test
	public void testUpdateCasePersonInJurisdiction() {
		creator.createCase(user1.toReference(), person.toReference(), rdcf2);

		updatePerson();

		assertPersonUpdated();
	}

	@Test
	public void testUpdateCasePersonOutsideJurisdiction() {
		creator.createCase(user1.toReference(), person.toReference(), rdcf2);

		updatePerson();

		assertPersonUpdated();
	}


	@Test
	public void testGetContactPersonInSameJurisdiction() {
		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf2);

		assertNotPseudonomyzed(person);
	}

	@Test
	public void testGetContactPersonInOtherJurisdiction() {
		creator.createContact(user1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);

		assertPsoudonimysed(person);
	}

	@Test
	public void testGetEventParticipantPersonInSameJurisdiction() {
		EventDto event = creator.createEvent(user2.toReference());
		creator.createEventParticipant(event.toReference(), person);

		assertNotPseudonomyzed(person);
	}

	@Test
	public void testGetEventParticipantPersonInOtherJurisdiction() {
		EventDto event = creator.createEvent(user1.toReference());
		creator.createEventParticipant(event.toReference(), person);

		assertPsoudonimysed(person);
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

		return creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, address);
	}

	private void updatePerson() {
		person.setFirstName("Newfirstname");
		person.setLastName("Newlastname");
		person.setBirthdateDD(23);

		LocationDto newAddress = new LocationDto();
		newAddress.setRegion(rdcf2.region);
		newAddress.setDistrict(rdcf2.district);
		newAddress.setCommunity(rdcf2.community);
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

	private void assertNotPseudonomyzed(PersonDto person) {
		PersonDto personForUser2 = getPersonFacade().getPersonByUuid(person.getUuid());

		assertThat(personForUser2.isPseudonymized(), is(false));
		assertThat(personForUser2.getFirstName(), is("James"));
		assertThat(personForUser2.getLastName(), is("Smith"));
		assertThat(personForUser2.getBirthdateDD(), is(1));

		assertThat(personForUser2.getAddress().getRegion().getCaption(), is("Region 1"));
		assertThat(personForUser2.getAddress().getDistrict().getCaption(), is("District 1"));
		assertThat(personForUser2.getAddress().getCommunity(), is(rdcf1.community));
		assertThat(personForUser2.getAddress().getCity(), is("Test City"));
		assertThat(personForUser2.getAddress().getAddress(), is("Test address"));
		assertThat(personForUser2.getAddress().getPostalCode(), is("12345"));
		assertThat(personForUser2.getAddress().getAreaType(), is(AreaType.URBAN));
		assertThat(personForUser2.getAddress().getDetails(), is("Test address details"));
		assertThat(personForUser2.getAddress().getLongitude(), is(46.233));
		assertThat(personForUser2.getAddress().getLatitude(), is(26.533));
		assertThat(personForUser2.getAddress().getLatLonAccuracy(), is(10F));
	}

	private void assertPsoudonimysed(PersonDto person) {
		PersonDto personForUser2 = getPersonFacade().getPersonByUuid(person.getUuid());

		assertThat(personForUser2.isPseudonymized(), is(true));
		assertThat(personForUser2.getFirstName(), isEmptyString());
		assertThat(personForUser2.getLastName(), isEmptyString());
		assertThat(personForUser2.getBirthdateDD(), is(nullValue()));

		assertThat(personForUser2.getAddress().getRegion().getCaption(), is("Region 1"));
		assertThat(personForUser2.getAddress().getDistrict().getCaption(), is("District 1"));
		assertThat(personForUser2.getAddress().getCommunity(), is(nullValue()));
		assertThat(personForUser2.getAddress().getCity(), isEmptyString());
		assertThat(personForUser2.getAddress().getAddress(), isEmptyString());
		assertThat(personForUser2.getAddress().getPostalCode(), isEmptyString());
		assertThat(personForUser2.getAddress().getAreaType(), is(nullValue()));
		assertThat(personForUser2.getAddress().getDetails(), isEmptyString());
		assertThat(personForUser2.getAddress().getLongitude(), is(nullValue()));
		assertThat(personForUser2.getAddress().getLatitude(), is(nullValue()));
		assertThat(personForUser2.getAddress().getLatLonAccuracy(), is(nullValue()));
	}

	private void assertPersonUpdated() {
		Person savedPerson = getPersonService().getByUuid(person.getUuid());

		assertThat(savedPerson.getFirstName(), is("Newfirstname"));
		assertThat(savedPerson.getLastName(), is("Newlastname"));
		assertThat(savedPerson.getBirthdateDD(), is(23));
		assertThat(savedPerson.getAddress().getRegion().getName(), is(rdcf2.region.getCaption()));
		assertThat(savedPerson.getAddress().getDistrict().getName(), is(rdcf2.district.getCaption()));
		assertThat(savedPerson.getAddress().getCommunity().getName(), is(rdcf2.community.getCaption()));
		assertThat(savedPerson.getAddress().getCity(), is("New City"));
		assertThat(savedPerson.getAddress().getAddress(), is("New address"));
		assertThat(savedPerson.getAddress().getPostalCode(), is("938"));
		assertThat(savedPerson.getAddress().getAreaType(), is(AreaType.RURAL));
		assertThat(savedPerson.getAddress().getDetails(), is("New address details"));
		assertThat(savedPerson.getAddress().getLongitude(), is(36.233));
		assertThat(savedPerson.getAddress().getLatitude(), is(36.533));
		assertThat(savedPerson.getAddress().getLatLonAccuracy(), is(8F));
	}
}
