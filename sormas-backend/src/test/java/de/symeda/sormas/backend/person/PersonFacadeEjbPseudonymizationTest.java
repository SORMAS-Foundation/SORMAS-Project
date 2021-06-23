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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class PersonFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto districtUser1;
	private UserDto districtUser2;
	private UserDto regionUser2;
	private UserDto communityUser2;
	private UserDto facilityUser2;
	private PersonDto person;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1");
		districtUser1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		districtUser2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		regionUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Sup2",
			UserRole.SURVEILLANCE_SUPERVISOR);
		communityUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Comm",
			"Off2",
			UserRole.COMMUNITY_OFFICER);
		facilityUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Hosp",
			"Inf2",
			UserRole.HOSPITAL_INFORMANT);
	}

	@Test
	public void testGetCasePersonInSameJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();

		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1);

		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSameResponsibleJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setResponsibleRegion(rdcf2.region);
			c.setResponsibleDistrict(rdcf2.district);
			c.setResponsibleCommunity(rdcf2.community);
		});

		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonOutsideResponsibleJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setResponsibleRegion(rdcf1.region);
			c.setResponsibleDistrict(rdcf1.district);
			c.setResponsibleCommunity(rdcf1.community);
		});

		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSameResponsibleJurisdictionOnRegionLevel() {
		loginWith(regionUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setResponsibleRegion(rdcf2.region);
			c.setResponsibleDistrict(rdcf2.district);
			c.setResponsibleCommunity(rdcf2.community);
		});

		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSameResponsibleJurisdictionOnCommunityLevel() {
		loginWith(communityUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setResponsibleRegion(rdcf2.region);
			c.setResponsibleDistrict(rdcf2.district);
			c.setResponsibleCommunity(rdcf2.community);
		});

		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonResponsibleJurisdictionOnFacilityLevel() {
		loginWith(facilityUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setResponsibleRegion(rdcf2.region);
			c.setResponsibleDistrict(rdcf2.district);
			c.setResponsibleCommunity(rdcf2.community);
		});

		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateCasePersonInJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateCasePersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1);
		updatePerson(true);
		assertPersonNotUpdated();
	}

	@Test
	public void testUpdateCasePersonInJurisdictionWithPseudonymizedDto() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		updatePersonPseudonymizedDto();
		assertPersonNotUpdated();
	}

	@Test
	public void testGetContactPersonInSameJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createContact(districtUser1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf2);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetContactPersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		ContactDto contact =
			creator.createContact(districtUser1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);
		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));

		CaseDataDto caze = creator.createCase(districtUser1.toReference(), creator.createPerson().toReference(), rdcf2);
		contact.setCaze(caze.toReference());
		contact = getContactFacade().saveContact(contact);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));

		contact.setRegion(rdcf2.region);
		contact.setDistrict(rdcf2.district);
		contact = getContactFacade().saveContact(contact);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));

		contact.setRegion(null);
		contact.setDistrict(null);
		contact = getContactFacade().saveContact(contact);
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateContactPersonInJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createContact(districtUser1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf2);
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateContactPersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createContact(districtUser1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);
		updatePerson(true);
		assertPersonNotUpdated();
	}

	@Test
	public void testGetEventParticipantPersonInSameJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		EventDto event = creator.createEvent(districtUser2.toReference());
		creator.createEventParticipant(event.toReference(), person, districtUser2.toReference());
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testGetEventParticipantPersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		EventDto event = creator.createEvent(districtUser1.toReference());
		creator.createEventParticipant(event.toReference(), person, districtUser1.toReference());
//		assertPseudonymised(getPersonFacade().getPersonByUuid(person.getUuid()));
		assertNotPseudonymized(getPersonFacade().getPersonByUuid(person.getUuid()));
	}

	@Test
	public void testUpdateEventParticipantPersonInJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		EventDto event = creator.createEvent(districtUser2.toReference());
		creator.createEventParticipant(event.toReference(), person, districtUser2.toReference());
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testUpdateEventParticipantPersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		EventDto event = creator.createEvent(districtUser1.toReference());
		creator.createEventParticipant(event.toReference(), person, districtUser1.toReference());
//		updatePerson(true);
//		assertPersonNotUpdated();
		// pseudonymization disabled for now
		updatePerson(false);
		assertPersonUpdated();
	}

	@Test
	public void testPseudonymizeGetByUuids() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		PersonDto person2 = createPerson();
		List<PersonDto> persons = getPersonFacade().getByUuids(Arrays.asList(person.getUuid(), person2.getUuid()));
		assertNotPseudonymized(persons.stream().filter(p -> p.getUuid().equals(person.getUuid())).findFirst().get());
		assertPseudonymised(persons.stream().filter(p -> p.getUuid().equals(person2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllAfter() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);

		PersonDto person2 = createPerson();
		//create redonly case with person2 --> person2 should be pseudonymized
		CaseDataDto caze = creator.createCase(districtUser1.toReference(), person2.toReference(), rdcf1);
		creator
			.createContact(districtUser2.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

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
		address.setStreet("Test street");
		address.setHouseNumber("Test number");
		address.setAdditionalInformation("Test information");
		address.setPostalCode("12345");
		address.setAreaType(AreaType.URBAN);
		address.setDetails("Test address details");
		address.setLongitude(46.432);
		address.setLatitude(23.234);
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
		newAddress.setStreet("New street");
		newAddress.setHouseNumber("New number");
		newAddress.setAdditionalInformation("New information");
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
		newAddress.setStreet("");
		newAddress.setHouseNumber("");
		newAddress.setAdditionalInformation("");
		newAddress.setPostalCode("");
		newAddress.setAreaType(null);
		newAddress.setDetails("");
		newAddress.setLongitude(null);
		newAddress.setLatitude(null);
		newAddress.setLatLonAccuracy(8F);

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
		assertThat(person.getAddress().getStreet(), is("Test street"));
		assertThat(person.getAddress().getHouseNumber(), is("Test number"));
		assertThat(person.getAddress().getAdditionalInformation(), is("Test information"));
		assertThat(person.getAddress().getPostalCode(), is("12345"));
		assertThat(person.getAddress().getAreaType(), is(AreaType.URBAN));
		assertThat(person.getAddress().getDetails(), is("Test address details"));
		assertThat(person.getAddress().getLongitude(), is(46.432));
		assertThat(person.getAddress().getLatitude(), is(23.234));
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
		assertThat(person.getAddress().getStreet(), isEmptyString());
		assertThat(person.getAddress().getHouseNumber(), isEmptyString());
		assertThat(person.getAddress().getAdditionalInformation(), isEmptyString());
		assertThat(person.getAddress().getPostalCode(), is("123"));
		assertThat(person.getAddress().getAreaType(), is(nullValue()));
		assertThat(person.getAddress().getDetails(), isEmptyString());

		assertThat(person.getAddress().getLatitude(), is(not(23.234)));
		assertThat(person.getAddress().getLatitude().toString(), startsWith("23."));
		assertThat(person.getAddress().getLongitude(), is(not(46.432)));
		assertThat(person.getAddress().getLongitude().toString(), startsWith("46."));

		assertThat(person.getAddress().getLatLonAccuracy(), is(10F));

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
		assertThat(savedPerson.getAddress().getStreet(), is("New street"));
		assertThat(savedPerson.getAddress().getHouseNumber(), is("New number"));
		assertThat(savedPerson.getAddress().getAdditionalInformation(), is("New information"));
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
		assertThat(savedPerson.getAddress().getStreet(), is("Test street"));
		assertThat(savedPerson.getAddress().getHouseNumber(), is("Test number"));
		assertThat(savedPerson.getAddress().getAdditionalInformation(), is("Test information"));
		assertThat(savedPerson.getAddress().getPostalCode(), is("12345"));
		assertThat(savedPerson.getAddress().getAreaType(), is(AreaType.URBAN));
		assertThat(savedPerson.getAddress().getDetails(), is("Test address details"));
		assertThat(savedPerson.getAddress().getLongitude(), is(46.432));
		assertThat(savedPerson.getAddress().getLatitude(), is(23.234));
		assertThat(savedPerson.getAddress().getLatLonAccuracy(), is(8F));
	}
}
