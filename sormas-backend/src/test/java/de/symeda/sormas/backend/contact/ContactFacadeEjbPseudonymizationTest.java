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
package de.symeda.sormas.backend.contact;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFollowUpDto;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ContactFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;
	private UserDto nationalContactUser;

	@Override
	public void init() {

		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		nationalContactUser = creator.createUser(
			null,
			null,
			null,
			"National",
			"Contact User",
			"National Contact User",
			JurisdictionLevel.NATION,
			UserRight.CONTACT_VIEW,
			UserRight.CONTACT_EDIT,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT);
		loginWith(user2);
	}

	@Test
	public void testGetContactInJurisdiction() {

		CaseDataDto caze = createCase(user2, rdcf2);
		ContactDto contact = createContact(user2, caze, rdcf2);
		assertNotPseudonymized(getContactFacade().getByUuid(contact.getUuid()), true);
	}

	@Test
	public void testGetContactOutsideJurisdiction() {

		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact = createContact(user1, caze, rdcf1);
		// contact of case on other jurisdiction --> should be pseudonymized
		creator.createContact(
			user1.toReference(),
			null,
			createPerson().toReference(),
			getCaseFacade().getCaseDataByUuid(contact.getCaze().getUuid()),
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		assertPseudonymized(getContactFacade().getByUuid(contact.getUuid()));
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() {

		// contact & case within limited user's jurisdiction
		CaseDataDto caze1 = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user1, caze1, rdcf1);

		// contact & case outside limited user's jurisdiction
		CaseDataDto caze2 = createCase(user2, rdcf2);
		ContactDto contact2 = createContact(user2, caze2, rdcf2);

		loginWith(nationalAdmin);
		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		ContactDto testContact1 = getContactFacade().getByUuid(contact1.getUuid());
		assertThat(testContact1.isPseudonymized(), is(true));
		assertThat(testContact1.getPerson().getFirstName(), is(emptyString()));

		ContactDto testContact2 = getContactFacade().getByUuid(contact2.getUuid());
		assertThat(testContact2.isPseudonymized(), is(true));
		assertThat(testContact2.getPerson().getFirstName(), is(emptyString()));

		//contact created by limited user in the same jurisdiction
		ContactDto contact3 = createContact(surveillanceOfficerWithRestrictedAccessToAssignedEntities, null, rdcf1);
		//contact created by limited user outside limited user's jurisdiction
		ContactDto contact4 = createContact(surveillanceOfficerWithRestrictedAccessToAssignedEntities, null, rdcf2);

		loginWith(nationalAdmin);
		contact1.setContactOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getContactFacade().save(contact1);
		contact2.setContactOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getContactFacade().save(contact2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		ContactDto testForContact1 = getContactFacade().getByUuid(contact1.getUuid());
		assertThat(testForContact1.isPseudonymized(), is(false));
		assertThat(testForContact1.getPerson().getFirstName(), is("James"));
		CaseDataDto testForCase1 = getCaseFacade().getCaseDataByUuid(caze1.getUuid());
		assertThat(testForCase1.isPseudonymized(), is(true));
		assertThat(testForCase1.getPerson().getFirstName(), is(emptyString()));

		ContactDto testForContact2 = getContactFacade().getByUuid(contact2.getUuid());
		assertThat(testForContact2.isPseudonymized(), is(false));
		assertThat(testForContact2.getPerson().getFirstName(), is("James"));
		CaseDataDto testForCase2 = getCaseFacade().getCaseDataByUuid(caze2.getUuid());
		assertThat(testForCase2.isPseudonymized(), is(true));
		assertThat(testForCase2.getPerson().getFirstName(), is(emptyString()));

		ContactDto testForContact3 = getContactFacade().getByUuid(contact3.getUuid());
		assertThat(testForContact3.isPseudonymized(), is(false));
		assertThat(testForContact3.getPerson().getFirstName(), is("James"));

		ContactDto testForContact4 = getContactFacade().getByUuid(contact4.getUuid());
		assertThat(testForContact4.isPseudonymized(), is(false));
		assertThat(testForContact4.getPerson().getFirstName(), is("James"));
	}

	@Test
	public void testPseudonymizeGetByUuids() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 = createContact(user1, caze, rdcf2);

		List<ContactDto> contacts = getContactFacade().getByUuids(Arrays.asList(contact1.getUuid(), contact2.getUuid()));
		assertNotPseudonymized(contacts.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get(), false);
		assertPseudonymized(contacts.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllAfter() {

		CaseDataDto caze1 = createCase(user2, rdcf2);
		ContactDto contact1 = createContact(user2, caze1, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		CaseDataDto caze2 = createCase(user1, rdcf1);
		ContactDto contact3 = createContact(user2, caze2, rdcf2);
		ContactDto contact2 = createContact(user1, caze2, rdcf2);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<ContactDto> contacts = getContactFacade().getAllAfter(calendar.getTime());

		assertNotPseudonymized(contacts.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get(), true);
		assertPseudonymized(contacts.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get());
		assertNotPseudonymized(contacts.stream().filter(c -> c.getUuid().equals(contact3.getUuid())).findFirst().get(), false);
	}

	@Test
	public void testPseudonymizeIndexData() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 =
			creator.createContact(user1.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		List<ContactIndexDto> indexList = getContactFacade().getIndexList(contactCriteria, null, null, Collections.emptyList());

		ContactIndexDto index1 = indexList.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get();
		assertThat(index1.getFirstName(), is("James"));
		assertThat(index1.getLastName(), is("Smith"));
		assertThat(index1.getCaze().getFirstName(), is("Confidential"));
		assertThat(index1.getCaze().getLastName(), is("Confidential"));

		ContactIndexDto index2 = indexList.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get();
		assertThat(index2.getFirstName(), is("Confidential"));
		assertThat(index2.getLastName(), is("Confidential"));
		assertThat(index2.getCaze().getFirstName(), is("Confidential"));
		assertThat(index2.getCaze().getLastName(), is("Confidential"));
	}

	@Test
	public void testPseudonymizeIndexDetailedData() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 =
			creator.createContact(user1.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		List<ContactIndexDetailedDto> indexList = getContactFacade().getIndexDetailedList(contactCriteria, null, null, Collections.emptyList());

		ContactIndexDetailedDto index1 = indexList.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get();
		assertThat(index1.getFirstName(), is("James"));
		assertThat(index1.getLastName(), is("Smith"));
		assertThat(index1.getCaze().getFirstName(), is(is("Confidential")));
		assertThat(index1.getCaze().getLastName(), is(is("Confidential")));
		assertThat(index1.getReportingUser().getUuid(), is(user2.getUuid()));

		ContactIndexDetailedDto index2 = indexList.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get();
		assertThat(index2.getFirstName(), is("Confidential"));
		assertThat(index2.getLastName(), is("Confidential"));
		assertThat(index2.getCaze().getFirstName(), is("Confidential"));
		assertThat(index2.getCaze().getLastName(), is("Confidential"));
		assertThat(index2.getReportingUser(), is(nullValue()));
	}

	@Test
	public void testPseudonymizeExportData() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 =
			creator.createContact(user1.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		List<ContactExportDto> exportList = getContactFacade().getExportList(contactCriteria, Collections.emptySet(), 0, 100, null, Language.EN);

		ContactExportDto index1 = exportList.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get();
		assertThat(index1.getFirstName(), is("James"));
		assertThat(index1.getLastName(), is("Smith"));
		assertThat(index1.getCity(), is("Test City"));
		assertThat(index1.getStreet(), is("Test street"));
		assertThat(index1.getHouseNumber(), is("Test number"));
		assertThat(index1.getAdditionalInformation(), is("Test information"));
		assertThat(index1.getPostalCode(), is("12345"));

		ContactExportDto index2 = exportList.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get();
		assertThat(index2.getFirstName(), is("Confidential"));
		assertThat(index2.getLastName(), is("Confidential"));
		assertThat(index2.getCity(), is("Confidential"));
		assertThat(index2.getStreet(), is("Confidential"));
		assertThat(index2.getHouseNumber(), is("Confidential"));
		assertThat(index2.getAdditionalInformation(), is("Confidential"));
		assertThat(index2.getPostalCode(), is("123"));
	}

	@Test
	public void testPseudonymizeGetMatchingContacts() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 =
			creator.createContact(user1.toReference(), null, createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf2);

		ContactSimilarityCriteria criteria = new ContactSimilarityCriteria();
		criteria.setReportDate(new Date());
		criteria.setLastContactDate(new Date());

		List<SimilarContactDto> matchingContacts = getContactFacade().getMatchingContacts(criteria);

		SimilarContactDto matching1 = matchingContacts.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get();
		assertThat(matching1.getFirstName(), is("James"));
		assertThat(matching1.getLastName(), is("Smith"));

		SimilarContactDto matching2 = matchingContacts.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get();
		assertThat(matching2.getFirstName(), isEmptyString());
		assertThat(matching2.getLastName(), isEmptyString());

	}

	@Test
	public void testPseudonymizeGetFollowupList() {
		CaseDataDto caze = createCase(user1, rdcf1);
		ContactDto contact1 = createContact(user2, caze, rdcf2);
		// contact of case on other jurisdiction --> should be pseudonymized
		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			user1.toReference(),
			createPerson().toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		List<ContactFollowUpDto> matchingContacts =
			getContactFacade().getContactFollowUpList(contactCriteria, new Date(), 10, 0, 100, Collections.emptyList());

		ContactFollowUpDto followup1 = matchingContacts.stream().filter(c -> c.getUuid().equals(contact1.getUuid())).findFirst().get();
		assertThat(followup1.getFirstName(), is("James"));
		assertThat(followup1.getLastName(), is("Smith"));

		//sensitive data
		assertThat(followup1.getContactOfficer(), is(user2));

		ContactFollowUpDto followup2 = matchingContacts.stream().filter(c -> c.getUuid().equals(contact2.getUuid())).findFirst().get();
		assertThat(followup2.getFirstName(), is("Confidential"));
		assertThat(followup2.getLastName(), is("Confidential"));

		//sensitive data
		assertThat(followup2.getContactOfficer(), is(nullValue()));
	}

	@Test
	public void testUpdatePseudonymizedContact() {
		CaseDataDto caze = createCase(user2, rdcf2);
		ContactDto contact = createContact(user2, caze, rdcf2);

		loginWith(nationalContactUser);

		contact.setReportingUser(null);
		contact.setContactOfficer(null);
		contact.setResultingCaseUser(null);
		contact.setReportLat(null);
		contact.setReportLon(null);
		contact.setReportLatLonAccuracy(20F);

		getContactFacade().save(contact);

		Contact updatedContact = getContactService().getByUuid(contact.getUuid());

		assertThat(updatedContact.getReportingUser().getUuid(), is(user2.getUuid()));
		assertThat(updatedContact.getContactOfficer().getUuid(), is(user2.getUuid()));
		assertThat(updatedContact.getResultingCaseUser(), is(nullValue()));

		assertThat(updatedContact.getReportLat(), is(46.432));
		assertThat(updatedContact.getReportLon(), is(23.234));

		assertThat(updatedContact.getReportLatLonAccuracy(), is(20F));
	}

	@Test
	public void testUpdateContactInJurisdictionWithPseudonymizedDto() {
		CaseDataDto caze = createCase(user2, rdcf2);
		ContactDto contact = createContact(user2, caze, rdcf2);

		contact.setPseudonymized(true);
		contact.setReportingUser(null);
		contact.setContactOfficer(null);
		contact.setResultingCaseUser(null);
		contact.setReportLatLonAccuracy(20F);

		getContactFacade().save(contact);

		Contact updatedContact = getContactService().getByUuid(contact.getUuid());

		assertThat(updatedContact.getReportingUser().getUuid(), is(user2.getUuid()));
		assertThat(updatedContact.getContactOfficer().getUuid(), is(user2.getUuid()));
		assertThat(updatedContact.getResultingCaseUser(), is(nullValue()));

		assertThat(updatedContact.getReportLat(), is(46.432));
		assertThat(updatedContact.getReportLon(), is(23.234));

		assertThat(updatedContact.getReportLatLonAccuracy(), is(20F));
	}

	private void assertNotPseudonymized(ContactDto contact, boolean caseInJurisdiction) {
		assertThat(contact.getPerson().getFirstName(), is("James"));
		assertThat(contact.getPerson().getLastName(), is("Smith"));

		assertThat(contact.getCaze().getFirstName(), caseInJurisdiction ? is("James") : isEmptyString());
		assertThat(contact.getCaze().getLastName(), caseInJurisdiction ? is("Smith") : isEmptyString());

		// sensitive data
		assertThat(contact.getReportingUser().getUuid(), is(user2.getUuid()));
		assertThat(contact.getContactOfficer().getUuid(), is(user2.getUuid()));
		assertThat(contact.getResultingCaseUser(), is(nullValue()));

		assertThat(contact.getReportLat(), is(46.432));
		assertThat(contact.getReportLon(), is(23.234));
		assertThat(contact.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(ContactDto contact) {

		assertThat(contact.getPerson().getFirstName(), isEmptyString());
		assertThat(contact.getPerson().getLastName(), isEmptyString());

		assertThat(contact.getCaze().getFirstName(), isEmptyString());
		assertThat(contact.getCaze().getLastName(), isEmptyString());

		// sensitive data
		assertThat(contact.getReportingUser(), is(nullValue()));
		assertThat(contact.getContactOfficer(), is(nullValue()));
		assertThat(contact.getResultingCaseUser(), is(nullValue()));

		assertThat(contact.getReportLat(), is(not(46.432)));
		assertThat(contact.getReportLat().toString(), startsWith("46."));
		assertThat(contact.getReportLon(), is(not(23.234)));
		assertThat(contact.getReportLon().toString(), startsWith("23."));

		assertThat(contact.getReportLatLonAccuracy(), is(10F));
	}

	private ContactDto createContact(UserDto reportingUser, CaseDataDto caze, TestDataCreator.RDCF rdcf) {
		return creator.createContact(
			reportingUser.toReference(),
			reportingUser.toReference(),
			createPerson().toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			c -> {
				c.setResultingCaseUser(reportingUser.toReference());

				c.setReportLat(46.432);
				c.setReportLon(23.234);
				c.setReportLatLonAccuracy(10F);
			});
	}

	private CaseDataDto createCase(UserDto reportingUser, TestDataCreator.RDCF rdcf) {
		return creator.createCase(reportingUser.toReference(), createPerson().toReference(), rdcf);
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
		address.setLongitude(46.233);
		address.setLatitude(26.533);
		address.setLatLonAccuracy(10F);

		return creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, p -> {
			p.setAddress(address);
		});
	}
}
