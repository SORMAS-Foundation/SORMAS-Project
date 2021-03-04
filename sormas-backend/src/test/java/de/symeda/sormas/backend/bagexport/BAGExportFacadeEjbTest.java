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

package de.symeda.sormas.backend.bagexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.bagexport.BAGExportCaseDto;
import de.symeda.sormas.api.bagexport.BAGExportContactDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.caze.SamplingReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.EndOfQuarantineReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.facility.Facility;

public class BAGExportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testCaseExport() {
		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto personDto = creator.createPerson("James", "Smith", p -> {
			LocationDto homeAddress = p.getAddress();
			homeAddress.setStreet("Home street");
			homeAddress.setHouseNumber("11A");
			homeAddress.setCity("Home city");
			homeAddress.setPostalCode("12345");

			p.setPhone("12345678");
			p.setEmailAddress("test@email.com");
			p.setSex(Sex.MALE);

			p.setBirthdateYYYY(1978);
			p.setBirthdateMM(10);
			p.setBirthdateDD(22);

			p.setOccupationType(OccupationType.ACCOMMODATION_AND_FOOD_SERVICES);

			LocationDto workPlaceAddress = LocationDto.build();
			workPlaceAddress.setAddressType(PersonAddressType.PLACE_OF_WORK);
			workPlaceAddress.setStreet("Work street");
			workPlaceAddress.setHouseNumber("12W");
			workPlaceAddress.setCity("Work city");
			workPlaceAddress.setPostalCode("54321");

			p.getAddresses().add(workPlaceAddress);

			LocationDto exposureAddress = LocationDto.build();
			exposureAddress.setAddressType(PersonAddressType.PLACE_OF_EXPOSURE);
			exposureAddress.setStreet("Exposure street");
			exposureAddress.setHouseNumber("13E");
			exposureAddress.setCity("Exposure city");
			exposureAddress.setPostalCode("098765");

			p.getAddresses().add(exposureAddress);

			LocationDto isolationAddress = LocationDto.build();
			isolationAddress.setAddressType(PersonAddressType.PLACE_OF_ISOLATION);
			isolationAddress.setStreet("Isolation street");
			isolationAddress.setHouseNumber("14I");
			isolationAddress.setCity("Isolation city");
			isolationAddress.setPostalCode("76543");

			p.getAddresses().add(isolationAddress);
		});

		Date symptomDate = new Date();
		Date contactTracingDate = DateHelper.subtractDays(new Date(), 10);
		Date quarantineFromDate = DateHelper.subtractDays(new Date(), 11);
		Date quarantineToDate = DateHelper.subtractDays(new Date(), 1);
		Date followupDate = DateHelper.addDays(new Date(), 10);

		CaseDataDto cazeDto = creator.createCase(
			user.toReference(),
			personDto.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			c -> {
				c.setCaseIdIsm(123456);
				c.getSymptoms().setAgitation(SymptomState.YES);
				c.getSymptoms().setOnsetDate(symptomDate);
				c.setContactTracingFirstContactDate(contactTracingDate);
				c.setWasInQuarantineBeforeIsolation(YesNoUnknown.NO);
				c.setQuarantineFrom(quarantineFromDate);

				c.setQuarantineReasonBeforeIsolation(QuarantineReason.OTHER_REASON);
				c.setQuarantineReasonBeforeIsolationDetails("Test quarantine details");

				c.setQuarantine(QuarantineType.OTHER);
				c.setQuarantineTypeDetails("Test quarantine");

				c.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				c.setOverwriteFollowUpUntil(true);
				c.setFollowUpUntil(followupDate);
				c.setQuarantineTo(quarantineToDate);
				c.setEndOfIsolationReason(EndOfIsolationReason.OTHER);
				c.setEndOfIsolationReasonDetails("Test end of iso");
			});

		Date sampleDate = DateHelper.subtractDays(new Date(), 5);

		SampleDto sample = creator.createSample(cazeDto.toReference(), user.toReference(), new Facility(), s -> {
			s.setSampleDateTime(sampleDate);
			s.setSamplingReason(SamplingReason.OTHER_REASON);
			s.setSamplingReasonDetails("Test reason");
		});

		Date testDate = DateHelper.subtractDays(new Date(), 4);
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.RAPID_TEST,
			Disease.CORONAVIRUS,
			testDate,
			new Facility(),
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"",
			true);

		Case caze = getCaseService().getByUuid(cazeDto.getUuid());

		List<BAGExportCaseDto> caseList = getBAGExportFacade().getCaseExportList(0, 100);

		BAGExportCaseDto firstCase = caseList.get(0);

		assertThat(firstCase.getCaseIdIsm(), is(123456));
		assertThat(firstCase.getCaseId(), is(caze.getId()));
		assertThat(firstCase.getLastName(), is("Smith"));
		assertThat(firstCase.getFirstName(), is("James"));
		assertThat(firstCase.getHomeAddressStreet(), is("Home street"));
		assertThat(firstCase.getHomeAddressHouseNumber(), is("11A"));
		assertThat(firstCase.getHomeAddressCity(), is("Home city"));
		assertThat(firstCase.getHomeAddressPostalCode(), is("12345"));
		assertThat(firstCase.getHomeAddressCountry(), isEmptyOrNullString());
		assertThat(firstCase.getPhoneNumber(), is("12345678"));
		assertThat(firstCase.getMobileNumber(), isEmptyOrNullString());
		assertThat(firstCase.getEmailAddress(), is("test@email.com"));
		assertThat(firstCase.getSex(), is(Sex.MALE));

		assertThat(firstCase.getBirthDate().getBirthdateYYYY(), is(1978));
		assertThat(firstCase.getBirthDate().getBirthdateMM(), is(10));
		assertThat(firstCase.getBirthDate().getBirthdateDD(), is(22));

		assertThat(firstCase.getOccupationType(), is(OccupationType.ACCOMMODATION_AND_FOOD_SERVICES));

		assertThat(firstCase.getWorkPlaceName(), isEmptyOrNullString());
		assertThat(firstCase.getWorkPlaceStreet(), is("Work street"));
		assertThat(firstCase.getWorkPlaceStreetNumber(), is("12W"));
		assertThat(firstCase.getWorkPlaceCity(), is("Work city"));
		assertThat(firstCase.getWorkPlacePostalCode(), is("54321"));
		assertThat(firstCase.getWorkPlaceCountry(), isEmptyOrNullString());

		assertThat(firstCase.getSymptomatic(), is(YesNoUnknown.YES));

		assertThat(firstCase.getPcrReason(), is(SamplingReason.OTHER_REASON));
		assertThat(firstCase.getOtherPcrReason(), is("Test reason"));

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		assertThat(dateFormat.format(firstCase.getSymptomOnsetDate()), is(dateFormat.format(symptomDate)));

		assertThat(dateFormat.format(firstCase.getSampleDate()), is(dateFormat.format(sampleDate)));
		assertThat(dateFormat.format(firstCase.getLabReportDate()), is(dateFormat.format(testDate)));
		assertThat(firstCase.getTestType(), is(PathogenTestType.RAPID_TEST));
		assertThat(firstCase.getTestResult(), is(PathogenTestResultType.POSITIVE));

		assertThat(firstCase.getContactCaseLinkCaseYn(), is(nullValue()));
		assertThat(firstCase.getContactCaseLinkCaseId(), is(nullValue()));
		assertThat(firstCase.getContactCaseLinkCaseIdIsm(), is(nullValue()));
		assertThat(firstCase.getContactCaseLinkContactDate(), is(nullValue()));

		assertThat(firstCase.getExposureLocationYn(), is(YesNoUnknown.YES));
		assertThat(firstCase.getActivityMappingYn(), isEmptyOrNullString());
		assertThat(firstCase.getExposureCountry(), isEmptyOrNullString());
		assertThat(firstCase.getExposureLocationCity(), is("Exposure city"));
		assertThat(firstCase.getExposureLocationTypeDetails(), isEmptyOrNullString());
		assertThat(firstCase.getExposureLocationName(), isEmptyOrNullString());
		assertThat(firstCase.getExposureLocationStreet(), is("Exposure street"));
		assertThat(firstCase.getExposureLocationStreetNumber(), is("13E"));
		assertThat(firstCase.getExposureLocationCity(), is("Exposure city"));
		assertThat(firstCase.getExposureLocationPostalCode(), is("098765"));
		assertThat(firstCase.getExposureLocationCountry(), isEmptyOrNullString());

		assertThat(dateFormat.format(firstCase.getContactTracingContactDate()), is(dateFormat.format(contactTracingDate)));

		assertThat(firstCase.getIsolationType(), is(QuarantineType.OTHER));
		assertThat(firstCase.getIsolationTypeDetails(), is("Test quarantine"));

		assertThat(firstCase.getIsolationLocationStreet(), is("Isolation street"));
		assertThat(firstCase.getIsolationLocationStreetNumber(), is("14I"));
		assertThat(firstCase.getIsolationLocationCity(), is("Isolation city"));
		assertThat(firstCase.getIsolationLocationPostalCode(), is("76543"));
		assertThat(firstCase.getIsolationLocationCountry(), isEmptyOrNullString());

		assertThat(dateFormat.format(firstCase.getFollowUpStartDate()), is(dateFormat.format(quarantineFromDate)));
		assertThat(dateFormat.format(firstCase.getEndOfIsolationDate()), is(dateFormat.format(quarantineToDate)));
		assertThat(firstCase.getEndOfIsolationReason(), is(EndOfIsolationReason.OTHER));
		assertThat(firstCase.getEndOfIsolationReasonDetails(), is("Test end of iso"));
	}

	@Test
	public void testContactExport() {
		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto personDto = creator.createPerson("James", "Smith", p -> {
			LocationDto homeAddress = p.getAddress();
			homeAddress.setAddressType(PersonAddressType.HOME);
			homeAddress.setStreet("Home street");
			homeAddress.setHouseNumber("11A");
			homeAddress.setCity("Home city");
			homeAddress.setPostalCode("12345");

			p.setPhone("12345678");
			p.setEmailAddress("test@email.com");
			p.setSex(Sex.MALE);

			p.setBirthdateYYYY(1978);
			p.setBirthdateMM(10);
			p.setBirthdateDD(22);

			p.setOccupationType(OccupationType.ACCOMMODATION_AND_FOOD_SERVICES);

			LocationDto workPlaceAddress = LocationDto.build();
			workPlaceAddress.setAddressType(PersonAddressType.PLACE_OF_WORK);
			workPlaceAddress.setStreet("Work street");
			workPlaceAddress.setHouseNumber("12W");
			workPlaceAddress.setCity("Work city");
			workPlaceAddress.setPostalCode("54321");

			p.getAddresses().add(workPlaceAddress);

			LocationDto exposureAddress = LocationDto.build();
			exposureAddress.setAddressType(PersonAddressType.PLACE_OF_EXPOSURE);
			exposureAddress.setStreet("Exposure street");
			exposureAddress.setHouseNumber("13E");
			exposureAddress.setCity("Exposure city");
			exposureAddress.setPostalCode("098765");

			p.getAddresses().add(exposureAddress);

			LocationDto isolationAddress = LocationDto.build();
			isolationAddress.setAddressType(PersonAddressType.PLACE_OF_ISOLATION);
			isolationAddress.setStreet("Isolation street");
			isolationAddress.setHouseNumber("14I");
			isolationAddress.setCity("Isolation city");
			isolationAddress.setPostalCode("76543");

			p.getAddresses().add(isolationAddress);
		});

		Date quarantineFromDate = DateHelper.subtractDays(new Date(), 11);
		Date quarantineToDate = DateHelper.subtractDays(new Date(), 1);

		ContactDto contactDto = creator.createContact(
			user.toReference(),
			user.toReference(),
			personDto.toReference(),
			creator.createCase(user.toReference(), personDto.toReference(), rdcf),
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			c -> {
				c.setQuarantineFrom(quarantineFromDate);

				c.setQuarantine(QuarantineType.OTHER);
				c.setQuarantineTypeDetails("Test quarantine");

				c.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				c.setOverwriteFollowUpUntil(true);
				c.setFollowUpUntil(quarantineFromDate);
				c.setQuarantineTo(quarantineToDate);
				c.setEndOfQuarantineReason(EndOfQuarantineReason.OTHER);
				c.setEndOfQuarantineReasonDetails("Test end of iso");
			});

		Date sampleDate = DateHelper.subtractDays(new Date(), 5);

		SampleDto sample =
			creator.createSample(contactDto.toReference(), sampleDate, new Date(), user.toReference(), SampleMaterial.BLOOD, new Facility());

		Date testDate = DateHelper.subtractDays(new Date(), 4);
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.RAPID_TEST,
			Disease.CORONAVIRUS,
			testDate,
			new Facility(),
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"",
			true);

		Contact contact = getContactService().getByUuid(contactDto.getUuid());

		List<BAGExportContactDto> contactList = getBAGExportFacade().getContactExportList(0, 100);

		BAGExportContactDto firstContact = contactList.get(0);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		assertThat(firstContact.getContactId(), is(contact.getId()));
		assertThat(firstContact.getLastName(), is("Smith"));
		assertThat(firstContact.getFirstName(), is("James"));
		assertThat(firstContact.getHomeAddressStreet(), is("Home street"));
		assertThat(firstContact.getHomeAddressHouseNumber(), is("11A"));
		assertThat(firstContact.getHomeAddressCity(), is("Home city"));
		assertThat(firstContact.getHomeAddressPostalCode(), is("12345"));
		assertThat(firstContact.getHomeAddressCountry(), isEmptyOrNullString());
		assertThat(firstContact.getPhoneNumber(), is("12345678"));
		assertThat(firstContact.getMobileNumber(), isEmptyOrNullString());
		assertThat(firstContact.getSex(), is(Sex.MALE));

		assertThat(firstContact.getBirthDate().getBirthdateYYYY(), is(1978));
		assertThat(firstContact.getBirthDate().getBirthdateMM(), is(10));
		assertThat(firstContact.getBirthDate().getBirthdateDD(), is(22));

		assertThat(firstContact.getOccupationType(), is(OccupationType.ACCOMMODATION_AND_FOOD_SERVICES));

		assertThat(firstContact.getWorkPlaceName(), isEmptyOrNullString());
		assertThat(firstContact.getWorkPlacePostalCode(), is("54321"));
		assertThat(firstContact.getWorkPlaceCountry(), isEmptyOrNullString());
		assertThat(firstContact.getWorkPlaceName(), isEmptyOrNullString());

		assertThat(dateFormat.format(firstContact.getSampleDate()), is(dateFormat.format(sampleDate)));
		assertThat(firstContact.getTestType(), is(PathogenTestType.RAPID_TEST));
		assertThat(firstContact.getTestResult(), is(PathogenTestResultType.POSITIVE));

		assertThat(firstContact.getOtherExposureLocation(), isEmptyOrNullString());
		assertThat(firstContact.getExposureLocationName(), isEmptyOrNullString());
		assertThat(firstContact.getExposureLocationStreet(), is("Exposure street"));
		assertThat(firstContact.getExposureLocationStreetNumber(), is("13E"));
		assertThat(firstContact.getExposureLocationCity(), is("Exposure city"));
		assertThat(firstContact.getExposureLocationPostalCode(), is("098765"));
		assertThat(firstContact.getExposureLocationCountry(), isEmptyOrNullString());


		assertThat(dateFormat.format(firstContact.getStartOfQuarantineDate()), is(dateFormat.format(quarantineFromDate)));
		assertThat(dateFormat.format(firstContact.getEndOfQuarantineDate()), is(dateFormat.format(quarantineToDate)));
		assertThat(firstContact.getEndOfQuarantineReason(), is(EndOfQuarantineReason.OTHER));
		assertThat(firstContact.getEndOfQuarantineReasonDetails(), is("Test end of iso"));
	}
}
