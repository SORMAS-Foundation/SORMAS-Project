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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonExportDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

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

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "PointOfEntry1");
		districtUser1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "PointOfEntry2");
		districtUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		regionUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Sup2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		communityUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Comm",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.COMMUNITY_OFFICER));
		facilityUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Hosp",
			"Inf2",
			creator.getUserRoleReference(DefaultUserRole.HOSPITAL_INFORMANT));
	}

	@Test
	public void testGetCasePersonInSameJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();

		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1);

		assertPseudonymised(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSamePlaceOfStayJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setRegion(rdcf2.region);
			c.setDistrict(rdcf2.district);
			c.setCommunity(rdcf2.community);
			c.setHealthFacility(rdcf2.facility);
		});

		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
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

		assertPseudonymised(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSamePlaceOfStayJurisdictionOnRegionLevel() {
		loginWith(regionUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setRegion(rdcf2.region);
			c.setDistrict(rdcf2.district);
			c.setCommunity(rdcf2.community);
			c.setHealthFacility(rdcf2.facility);
		});

		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInSamePlaceOfStayJurisdictionOnCommunityLevel() {
		loginWith(communityUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setRegion(rdcf2.region);
			c.setDistrict(rdcf2.district);
			c.setCommunity(rdcf2.community);
			c.setHealthFacility(rdcf2.facility);
		});

		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetCasePersonInPlaceOfStayJurisdictionOnFacilityLevel() {
		loginWith(facilityUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1, c -> {
			c.setRegion(rdcf2.region);
			c.setDistrict(rdcf2.district);
			c.setCommunity(rdcf2.community);
			c.setHealthFacility(rdcf2.facility);
		});

		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
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
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetContactPersonOutsideJurisdiction() {
		loginWith(districtUser1);
		person = createPerson();
		ContactDto contact =
			creator.createContact(districtUser1.toReference(), null, person.toReference(), null, new Date(), null, Disease.CORONAVIRUS, rdcf1);

		loginWith(districtUser2);
		assertPseudonymised(getPersonFacade().getByUuid(person.getUuid()));

		loginWith(districtUser1);
		CaseDataDto caze = creator.createCase(districtUser1.toReference(), creator.createPerson().toReference(), rdcf2);
		contact.setCaze(caze.toReference());
		contact = getContactFacade().save(contact);

		loginWith(districtUser2);
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));

		loginWith(districtUser1);
		contact.setRegion(rdcf2.region);
		contact.setDistrict(rdcf2.district);
		contact = getContactFacade().save(contact);

		loginWith(districtUser2);
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));

		loginWith(districtUser1);
		contact.setRegion(null);
		contact.setDistrict(null);
		contact = getContactFacade().save(contact);

		loginWith(districtUser2);
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetTravelEntryPersonOutsideJurisdiction() {
		loginWith(districtUser1);
		person = createPerson();
		TravelEntryDto travelEntry = creator.createTravelEntry(person.toReference(), districtUser1.toReference(), rdcf1, (t) -> {
			t.setDisease(Disease.EVD);
		});

		loginWith(districtUser2);
		assertPseudonymised(getPersonFacade().getByUuid(person.getUuid()));

		loginWith(districtUser1);
		CaseDataDto caze = creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);
		travelEntry.setResultingCase(caze.toReference());
		getTravelEntryFacade().save(travelEntry);

		loginWith(districtUser2);
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
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
		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
	}

	@Test
	public void testGetEventParticipantPersonOutsideJurisdiction() {
		loginWith(districtUser2);

		person = createPerson();
		EventDto event = creator.createEvent(districtUser1.toReference());
		creator.createEventParticipant(event.toReference(), person, districtUser1.toReference());
		assertPseudonymised(getPersonFacade().getByUuid(person.getUuid()));
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
		updatePerson(true);
		assertPersonNotUpdated();
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
		List<PersonDto> persons = getPersonFacade().getAllAfter(calendar.getTime());

		assertNotPseudonymized(persons.stream().filter(p -> p.getUuid().equals(person.getUuid())).findFirst().get());
		assertPseudonymised(persons.stream().filter(p -> p.getUuid().equals(person2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeExportList() {
		loginWith(districtUser2);

		person = createPerson();
		creator.createCase(districtUser1.toReference(), person.toReference(), rdcf2);

		PersonDto person2 = createPerson();
		//create readonly case with person2 --> person2 should be pseudonymized
		CaseDataDto caze = creator.createCase(districtUser1.toReference(), person2.toReference(), rdcf1);
		creator
			.createContact(districtUser2.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		List<PersonExportDto> exportList = getPersonFacade().getExportList(new PersonCriteria(), 0, 100);

		PersonExportDto exportedPerson = exportList.stream().filter(p -> p.getUuid().equals(person.getUuid())).findFirst().get();
		assertThat(exportedPerson.getFirstName(), is("James"));
		assertThat(exportedPerson.getLastName(), is("Smith"));
		assertThat(exportedPerson.getBirthdate().getDateOfBirthDD(), is(1));

		assertEquals(Optional.empty(), exportList.stream().filter(p -> p.getUuid().equals(person2.getUuid())).findFirst());
	}

	@Test
	public void tesGetPersonOfCaseWithSpecialAccess() {
		person = createPerson();
		CaseDataDto caze = creator.createCase(districtUser1.toReference(), person.toReference(), rdcf1);
		creator
			.createSpecialCaseAccess(caze.toReference(), nationalAdmin.toReference(), districtUser2.toReference(), DateHelper.addDays(new Date(), 1));
		loginWith(districtUser2);

		assertNotPseudonymized(getPersonFacade().getByUuid(person.getUuid()));
		assertNotPseudonymized(getPersonFacade().getByUuids(Collections.singletonList(person.getUuid())).get(0));
		assertNotPseudonymized(getPersonFacade().getAllAfter(new Date(0)).get(0));
		assertThat(
			getPersonFacade().getIndexList(new PersonCriteria().personAssociation(PersonAssociation.CASE), null, null, null).get(0).isPseudonymized(),
			is(false));
		assertThat(
			getPersonFacade().getExportList(new PersonCriteria().personAssociation(PersonAssociation.CASE), 0, Integer.MAX_VALUE)
				.get(0)
				.getFirstName(),
			is(person.getFirstName()));
	}

	private PersonDto createPerson() {

		LocationDto address = LocationDto.build();
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

		LocationDto newAddress = LocationDto.build();
		newAddress.setUuid(person.getAddress().getUuid());
		newAddress.setChangeDate(person.getAddress().getChangeDate());

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

		person.setAddress(newAddress);

		getPersonFacade().save(person);
	}

	private void updatePersonPseudonymizedDto() {

		person.setPseudonymized(true);
		person.setFirstName("");
		person.setLastName("");
		person.setBirthdateDD(null);

		LocationDto newAddress = LocationDto.build();
		newAddress.setUuid(person.getAddress().getUuid());
		newAddress.setChangeDate(person.getAddress().getChangeDate());

		newAddress.setPseudonymized(true);
		newAddress.setRegion(rdcf1.region);
		newAddress.setDistrict(rdcf1.district);
		newAddress.setCommunity(null);
		newAddress.setCity("");
		newAddress.setStreet("");
		newAddress.setHouseNumber("");
		newAddress.setAdditionalInformation("");
		newAddress.setPostalCode("123");
		newAddress.setAreaType(null);
		newAddress.setDetails("");

		person.setAddress(newAddress);

		getPersonFacade().save(person);
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

		// sensitive data
		assertThat(person.getPhone(), is("1234567"));
		assertThat(person.getCauseOfDeathDetails(), is("Test cause of death details"));
		assertThat(person.getPassportNumber(), is("Test passport num"));
	}

	private void assertPseudonymised(PersonDto person) {

		assertThat(person.isPseudonymized(), is(true));
		assertThat(person.getFirstName(), is(I18nProperties.getCaption(Captions.inaccessibleValue)));
		assertThat(person.getLastName(), is(I18nProperties.getCaption(Captions.inaccessibleValue)));
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
	}
}
