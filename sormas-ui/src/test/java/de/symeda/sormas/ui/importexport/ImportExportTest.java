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

package de.symeda.sormas.ui.importexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvException;
import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.caze.importer.CaseImporterTest.CaseImporterExtension;
import de.symeda.sormas.ui.contact.importer.ContactImporterTest.ContactImporterExtension;
import de.symeda.sormas.ui.utils.CaseDownloadUtil;
import de.symeda.sormas.ui.utils.ContactDownloadUtil;

public class ImportExportTest extends AbstractUiBeanTest {

	@Test
	public void testImportExportedCase() throws IOException, CsvException, InvalidColumnException, InterruptedException {
		var rdcf = creator.createRDCF("Region", "District", "Community", "Health facility");
		UserDto user = creator.createUser(null, null, null, "james", "Smith", creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson("John", "Doe");

		Date dateNow = new Date();
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			dateNow,
			rdcf);
		caze.setExternalID("text-ext-id");
		caze.setExternalToken("text-ext-token");
		caze.setDiseaseDetails("Corona");
		caze.setHealthFacilityDetails("test HF details");
		caze.setQuarantine(QuarantineType.INSTITUTIONELL);
		caze.setQuarantineFrom(dateNow);
		caze.setQuarantineTo(dateNow);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getHospitalization().setAdmissionDate(dateNow);
		caze.getHospitalization().setDischargeDate(dateNow);
		caze.getHospitalization().setLeftAgainstAdvice(YesNoUnknown.YES);
		caze.getSymptoms().setAbdominalPain(SymptomState.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		caze.getSymptoms().setBedridden(SymptomState.YES);
		caze.getSymptoms().setEyesBleeding(SymptomState.YES);
		caze.getSymptoms().setKopliksSpots(SymptomState.YES);
		caze.getSymptoms().setPigmentaryRetinopathy(SymptomState.YES);
		caze.getSymptoms().setTremor(SymptomState.YES);
		caze.getSymptoms().setVomiting(SymptomState.YES);

		getCaseFacade().save(caze);

		person.setSex(Sex.MALE);
		person.setBirthdateDD(11);
		person.setBirthdateMM(12);
		person.setBirthdateYYYY(1962);
		person.setPresentCondition(PresentCondition.ALIVE);
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setCommunity(rdcf.community);
		person.getAddress().setCity("test city");
		person.getAddress().setStreet("test street");
		person.getAddress().setHouseNumber("test house number");
		person.getAddress().setAdditionalInformation("test additional information");
		person.getAddress().setPostalCode("test postal code");
		person.getAddress().setPostalCode("test postal code");

		getPersonFacade().save(person);

		StreamResource exportStreamResource =
			CaseDownloadUtil.createCaseExportResource(new CaseCriteria(), Collections::emptySet, CaseExportType.CASE_SURVEILLANCE, null);

		List<String[]> rows = CSVUtils.createBomCsvReader(exportStreamResource.getStreamSource().getStream()).readAll();

		assertThat(rows, hasSize(4));

		String[] columns = rows.get(1);
		String[] values = rows.get(3);
		String importUuid = DataHelper.createUuid();

		for (int i = 0, getLength = columns.length; i < getLength; i++) {
			String column = columns[i];

			if (CaseDataDto.UUID.equals(column)) {
				values[i] = importUuid;
			} else if (CaseDataDto.EPID_NUMBER.equals(column)) {
				values[i] = "";
			}
			// update name avoid duplicate checking
			else if (String.join(".", CaseDataDto.PERSON, PersonDto.FIRST_NAME).equals(column)) {
				values[i] = "Import John";
			} else if (String.join(".", CaseDataDto.PERSON, PersonDto.LAST_NAME).equals(column)) {
				values[i] = "Import Doe";
			} else if (String.join(".", CaseDataDto.PERSON, PersonDto.UUID).equals(column)) {
				// Workaround: Reset the change date to avoid OutdatedEntityExceptions
				// Applying a setChangeDate(new Date()) to the person before saving it will result in creating 2 cases and will fail the test
				// So let's just ignore this column for now...
				values[i] = "";
			}
		}

		File tempFile = File.createTempFile("export", "csv");
		tempFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tempFile);
		IOUtils.copy(
			new ByteArrayInputStream(
				rows.stream().map(r -> String.join(",", Arrays.asList(r))).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8)),
			out);
		CaseImporterExtension caseImporter = new CaseImporterExtension(tempFile, true, user);
		caseImporter.runImport();

		InputStream errorStream = new ByteArrayInputStream(caseImporter.stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
		List<String[]> errorRows = CSVUtils.createBomCsvReader(errorStream).readAll();
		if (errorRows.size() > 2) {
			assertThat("Error during import: " + StringUtils.join(errorRows.get(2), ", "), errorRows, hasSize(0));
		}

		PersonCriteria importPersonCriteria = new PersonCriteria();
		importPersonCriteria.setNameAddressPhoneEmailLike("Import John");

		List<PersonIndexDto> importedPersons = getPersonFacade().getIndexList(importPersonCriteria, null, null, null);
		assertThat(importedPersons.size(), is(1));

		List<CaseDataDto> importedCases = getCaseFacade().getByPersonUuids(Collections.singletonList(importedPersons.get(0).getUuid()));
		assertThat(importedCases.size(), is(1));

		CaseDataDto importedCase = importedCases.get(0);

		assertThat(importedCase.getExternalID(), is("text-ext-id"));
		assertThat(importedCase.getExternalToken(), is("text-ext-token"));
		assertThat(importedCase.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(importedCase.getDiseaseDetails(), is("Corona"));
		assertNull(importedCase.getPregnant());
		assertNull(importedCase.getTrimester());
		assertNull(importedCase.getPostpartum());
		assertThat(importedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(importedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(importedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(importedCase.getHealthFacility(), is(rdcf.facility));
		assertThat(importedCase.getHealthFacilityDetails(), is("test HF details"));
		assertThat(importedCase.getQuarantine(), is(QuarantineType.INSTITUTIONELL));
		assertThat(importedCase.getQuarantineFrom().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedCase.getQuarantineTo().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedCase.getHospitalization().getAdmittedToHealthFacility(), is(YesNoUnknown.YES));
		assertThat(importedCase.getHospitalization().getAdmissionDate().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedCase.getHospitalization().getDischargeDate().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedCase.getHospitalization().getLeftAgainstAdvice(), is(YesNoUnknown.YES));
		assertThat(importedCase.getSymptoms().getAbdominalPain(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getBedridden(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getEyesBleeding(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getKopliksSpots(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getPigmentaryRetinopathy(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getTremor(), is(SymptomState.YES));
		assertThat(importedCase.getSymptoms().getVomiting(), is(SymptomState.YES));

		PersonDto importedPerson = getPersonFacade().getByUuid(importedCase.getPerson().getUuid());

		assertThat(importedPerson.getFirstName(), is("Import John"));
		assertThat(importedPerson.getLastName(), is("Import Doe"));
		assertThat(importedPerson.getSex(), is(Sex.MALE));
		assertThat(importedPerson.getBirthdateDD(), is(11));
		assertThat(importedPerson.getBirthdateMM(), is(12));
		assertThat(importedPerson.getBirthdateYYYY(), is(1962));
		assertThat(importedPerson.getPresentCondition(), is(PresentCondition.ALIVE));
		assertThat(importedPerson.getAddress().getRegion(), is(rdcf.region));
		assertThat(importedPerson.getAddress().getDistrict(), is(rdcf.district));
		assertThat(importedPerson.getAddress().getCommunity(), is(rdcf.community));
		assertThat(importedPerson.getAddress().getCity(), is("test city"));
		assertThat(importedPerson.getAddress().getStreet(), is("test street"));
		assertThat(importedPerson.getAddress().getHouseNumber(), is("test house number"));
		assertThat(importedPerson.getAddress().getAdditionalInformation(), is("test additional information"));
	}

	@Test
	public void testImportExportedContact() throws IOException, CsvException, InvalidColumnException, InterruptedException {
		var rdcf = creator.createRDCF("Region", "District", "Community", "Health facility");
		UserDto user = creator.createUser(null, null, null, "james", "Smith", creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson("John", "Doe");

		Date dateNow = new Date();
		ContactDto contact = creator.createContact(
			user.toReference(),
			user.toReference(),
			person.toReference(),
			creator.createCase(
				user.toReference(),
				person.toReference(),
				Disease.CORONAVIRUS,
				CaseClassification.PROBABLE,
				InvestigationStatus.PENDING,
				dateNow,
				rdcf),
			dateNow,
			dateNow,
			Disease.CORONAVIRUS);
		contact.setExternalID("text-ext-id");
		contact.setExternalToken("text-ext-token");
		contact.setDiseaseDetails("Corona");
		contact.setRegion(rdcf.region);
		contact.setDistrict(rdcf.district);
		contact.setCommunity(rdcf.community);
		contact.setQuarantine(QuarantineType.INSTITUTIONELL);
		contact.setQuarantineFrom(dateNow);
		contact.setQuarantineTo(dateNow);
		contact.setQuarantineExtended(true);
		contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
		contact.setFollowUpUntil(dateNow);

		getContactFacade().save(contact);

		person.setSex(Sex.MALE);
		person.setBirthdateDD(11);
		person.setBirthdateMM(12);
		person.setBirthdateYYYY(1962);
		person.setPresentCondition(PresentCondition.ALIVE);
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setCommunity(rdcf.community);
		person.getAddress().setCity("test city");
		person.getAddress().setStreet("test street");
		person.getAddress().setHouseNumber("test house number");
		person.getAddress().setAdditionalInformation("test additional information");
		person.getAddress().setPostalCode("test postal code");
		person.getAddress().setPostalCode("test postal code");

		getPersonFacade().save(person);

		StreamResource exportStreamResource = ContactDownloadUtil.createContactExportResource(new ContactCriteria(), Collections::emptySet, null);

		List<String[]> rows = CSVUtils.createBomCsvReader(exportStreamResource.getStreamSource().getStream()).readAll();

		assertThat(rows, hasSize(4));

		String[] columns = rows.get(1);
		String[] values = rows.get(3);
		String importUuid = DataHelper.createUuid();

		for (int i = 0, getLength = columns.length; i < getLength; i++) {
			String column = columns[i];

			if (ContactDto.UUID.equals(column)) {
				values[i] = importUuid;
			}
			// update name avoid duplicate checking
			else if (String.join(".", ContactDto.PERSON, PersonDto.FIRST_NAME).equals(column)) {
				values[i] = "Import John";
			} else if (String.join(".", ContactDto.PERSON, PersonDto.LAST_NAME).equals(column)) {
				values[i] = "Import Doe";
			} else if (String.join(".", ContactDto.PERSON, PersonDto.UUID).equals(column)) {
				// Workaround: Reset the change date to avoid OutdatedEntityExceptions
				// Applying a setChangeDate(new Date()) to the person before saving it will result in creating 2 cases and will fail the test
				// So let's just ignore this column for now...
				values[i] = "";
			}
		}

		File tempFile = File.createTempFile("export", "csv");
		tempFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tempFile);
		IOUtils.copy(
			new ByteArrayInputStream(
				rows.stream().map(r -> String.join(",", Arrays.asList(r))).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8)),
			out);
		ContactImporterExtension contactImporter = new ContactImporterExtension(tempFile, user);
		contactImporter.runImport();

		InputStream errorStream = new ByteArrayInputStream(contactImporter.stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
		List<String[]> errorRows = CSVUtils.createBomCsvReader(errorStream).readAll();
		if (errorRows.size() > 2) {
			assertThat("Error during import: " + StringUtils.join(errorRows.get(2), ", "), errorRows, hasSize(0));
		}

		PersonCriteria importPersonCriteria = new PersonCriteria();
		importPersonCriteria.setNameAddressPhoneEmailLike("Import John");

		List<PersonIndexDto> importedPersons = getPersonFacade().getIndexList(importPersonCriteria, null, null, null);
		assertThat(importedPersons.size(), is(1));

		List<ContactDto> importedContacts = getContactFacade().getByPersonUuids(Collections.singletonList(importedPersons.get(0).getUuid()));
		assertThat(importedContacts.size(), is(1));

		ContactDto importedContact = importedContacts.get(0);

		assertThat(importedContact.getUuid(), not(importUuid));
		assertThat(importedContact.getExternalID(), is("text-ext-id"));
		assertThat(importedContact.getExternalToken(), is("text-ext-token"));
		assertThat(importedContact.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(importedContact.getDiseaseDetails(), is("Corona"));
		assertThat(importedContact.getRegion(), is(rdcf.region));
		assertThat(importedContact.getDistrict(), is(rdcf.district));
		assertThat(importedContact.getCommunity(), is(rdcf.community));
		assertThat(importedContact.getQuarantine(), is(QuarantineType.INSTITUTIONELL));
		assertThat(importedContact.getQuarantineFrom().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedContact.getQuarantineTo().getTime(), is(DateHelper.getStartOfDay(dateNow).getTime()));
		assertThat(importedContact.isQuarantineExtended(), is(true));
		assertThat(importedContact.getFollowUpStatus(), is(FollowUpStatus.FOLLOW_UP));

		PersonDto importedPerson = getPersonFacade().getByUuid(importedContact.getPerson().getUuid());

		assertThat(importedPerson.getFirstName(), is("Import John"));
		assertThat(importedPerson.getLastName(), is("Import Doe"));
		assertThat(importedPerson.getSex(), is(Sex.MALE));
		assertThat(importedPerson.getBirthdateDD(), is(11));
		assertThat(importedPerson.getBirthdateMM(), is(12));
		assertThat(importedPerson.getBirthdateYYYY(), is(1962));
		assertThat(importedPerson.getPresentCondition(), is(PresentCondition.ALIVE));
		assertThat(importedPerson.getAddress().getRegion(), is(rdcf.region));
		assertThat(importedPerson.getAddress().getDistrict(), is(rdcf.district));
		assertThat(importedPerson.getAddress().getCommunity(), is(rdcf.community));
		assertThat(importedPerson.getAddress().getCity(), is("test city"));
		assertThat(importedPerson.getAddress().getStreet(), is("test street"));
		assertThat(importedPerson.getAddress().getHouseNumber(), is("test house number"));
		assertThat(importedPerson.getAddress().getAdditionalInformation(), is("test additional information"));
	}
}
