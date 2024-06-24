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

package de.symeda.sormas.ui.event.eventparticipantimporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.events.eventparticipantimporter.EventParticipantImporter;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

public class EventParticipantImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportEventParticipant()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();

		// Successful import of 5 event participant
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_success.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event);
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
		List<EventParticipantDto> eventParticipants = eventParticipantFacade.getAllActiveEventParticipantsByEvent(eventRef.getUuid());
		for (EventParticipantDto eventParticipant : eventParticipants) {
			assertNotNull(eventParticipant.getRegion());
			assertNotNull(eventParticipant.getDistrict());
			assertEquals(eventParticipant.getRegion().getUuid(), rdcf.region.getUuid());
			assertEquals(eventParticipant.getDistrict().getUuid(), rdcf.district.getUuid());
		}
	}

	@Test
	public void testImportEventParticipantSimilarityPick()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();
		final String EXISTING_PERSON_LAST_NAME = "Heinz";
		PersonDto person = creator.createPerson("Günther", EXISTING_PERSON_LAST_NAME);
		person.setPresentCondition(PresentCondition.UNKNOWN);
		getPersonFacade().save(person);

		creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// Person Similarity: pick
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(new PersonSimilarityCriteria());
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {

				List<SimilarPersonDto> entries = new ArrayList<>();
				for (SimilarPersonDto person : persons) {
					if (PersonHelper
						.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
						entries.add(person);
					}
				}
				resultConsumer.accept((T) new PersonImportSimilarityResult(entries.get(0), ImportSimilarityResultOption.PICK));
			}
		};
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().withEvent(eventRef), null, null, null).get(0);
		PersonDto importedPerson = getPersonFacade().getByUuid(importedEventParticipant.getPersonUuid());

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
		assertEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(person.getFirstName(), importedPerson.getFirstName());
		// person data changed
		assertNotEquals(EXISTING_PERSON_LAST_NAME, importedPerson.getLastName());
		assertNotEquals(PresentCondition.UNKNOWN, importedPerson.getPresentCondition());

		assertEquals(1, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilarityPickEventParticipant()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();
		PersonDto person = creator.createPerson("Günther", "Heinz");
		EventParticipantDto eventParticipant = creator.createEventParticipant(eventRef, person, "old desc", user.toReference());

		// Person Similarity: pick event participant
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(new PersonSimilarityCriteria());
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {

				List<SimilarPersonDto> entries = new ArrayList<>();
				for (SimilarPersonDto person : persons) {
					if (PersonHelper
						.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
						entries.add(person);
					}
				}
				resultConsumer.accept((T) new PersonImportSimilarityResult(entries.get(0), ImportSimilarityResultOption.PICK));
			}
		};
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().withEvent(eventRef), null, null, null).get(0);

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
		assertEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(eventParticipant.getUuid(), importedEventParticipant.getUuid());
		assertEquals("description 1", importedEventParticipant.getInvolvementDescription());
		assertEquals(1, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilarityCreate()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();
		PersonDto person = creator.createPerson("Günther", "Heinze");
		creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// Person Similarity: create
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event);
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().withEvent(eventRef), null, null, null).get(0);

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
		assertNotEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(2, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilaritySkip()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();

		// Person Similarity: create
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event) {

			@Override
			protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
				PersonDto newPerson,
				Consumer<T> resultConsumer,
				BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
				String infoText,
				UI currentUI) {
				resultConsumer.accept((T) new PersonImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
			}
		};
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(0, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
		assertEquals(0, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantComment()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();

		// Successful import of 5 event participant
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_comment_success.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event);
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, eventParticipantFacade.count(new EventParticipantCriteria().withEvent(eventRef)));
	}

	@Test
	public void testImportEventParticipantDifferentAddressTypes()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf);
		EventReferenceDto eventRef = event.toReference();

		// import of 3 event participants with different address types
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_address_types.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event);
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		List<EventParticipantDto> eventParticipants = getEventParticipantFacade().getByEventUuids(Collections.singletonList(eventRef.getUuid()));

		assertEquals(3, eventParticipants.size());

		boolean foundOtto = false;
		boolean foundOskar = false;
		boolean foundOona = false;

		for (EventParticipantDto eventParticipant : eventParticipants) {
			PersonDto person = eventParticipant.getPerson();
			if ("Otto".equals(person.getFirstName())) {
				foundOtto = true;
				assertTrue(CollectionUtils.isEmpty(person.getAddresses()));
				assertEquals("131", person.getAddress().getHouseNumber());
			}
			if ("Oskar".equals(person.getFirstName())) {
				foundOskar = true;
				assertTrue(CollectionUtils.isEmpty(person.getAddresses()));
				assertEquals("132", person.getAddress().getHouseNumber());
			}
			if ("Oona".equals(person.getFirstName())) {
				foundOona = true;
				assertTrue(LocationHelper.checkIsEmptyLocation(person.getAddress()));
				assertEquals(1, person.getAddresses().size());
				assertEquals("133", person.getAddresses().get(0).getHouseNumber());
			}
		}

		assertTrue(foundOtto && foundOskar && foundOona, "Not all eventparticipants found.");
	}

	@Test
	@Disabled("Remove ignore once we have replaced H2, and feature properties can be changed by code")
	public void testImportWithVaccinations()
		throws URISyntaxException, IOException, InterruptedException, CsvValidationException, InvalidColumnException {
		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.CORONAVIRUS,
			null);

		// Successful import of 5 event participant
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_vaccinations.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter = new EventParticipantImporterExtension(csvFile, user, event);
		ImportResultStatus importResult = eventParticipantImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult, eventParticipantImporter.stringBuilder.toString());
		assertEquals(ImportResultStatus.COMPLETED, importResult);

		List<EventParticipantDto> eventParticipants = getEventParticipantFacade().getByEventUuids(Collections.singletonList(event.getUuid()));
		assertEquals(3, eventParticipants.size());

		EventParticipantDto ep1 = eventParticipants.stream().filter(e -> e.getPerson().getFirstName().equals("Günther")).findFirst().get();
		EventParticipantDto ep2 = eventParticipants.stream().filter(e -> e.getPerson().getFirstName().equals("Peter")).findFirst().get();
		EventParticipantDto ep3 = eventParticipants.stream().filter(e -> e.getPerson().getFirstName().equals("Hans")).findFirst().get();

		List<VaccinationDto> case1Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(ep1.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(0, case1Vaccinations.size());

		List<VaccinationDto> case2Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(ep2.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(1, case2Vaccinations.size());
		assertEquals(Vaccine.COMIRNATY, case2Vaccinations.get(0).getVaccineName());
		assertNull(case2Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());

		List<VaccinationDto> case3Vaccinations =
			FacadeProvider.getVaccinationFacade().getAllVaccinations(ep3.getPerson().getUuid(), Disease.CORONAVIRUS);
		assertEquals(2, case3Vaccinations.size());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(0).getVaccineName());
		assertEquals(YesNoUnknown.YES, case3Vaccinations.get(0).getHealthConditions().getChronicPulmonaryDisease());
		assertEquals(Vaccine.MRNA_1273, case3Vaccinations.get(1).getVaccineName());
		assertNull(case3Vaccinations.get(1).getHealthConditions().getChronicPulmonaryDisease());

	}

	private static class EventParticipantImporterExtension extends EventParticipantImporter {

		public StringBuilder stringBuilder = new StringBuilder();
		private StringBuilderWriter writer = new StringBuilderWriter(stringBuilder);

		private EventParticipantImporterExtension(File inputFile, UserDto currentUser, EventDto event) throws IOException {
			super(inputFile, currentUser, event, ValueSeparator.DEFAULT);
		}

		@Override
		protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
			PersonDto newPerson,
			Consumer<T> resultConsumer,
			BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
			String infoText,
			UI currentUI) {
			resultConsumer.accept((T) new PersonImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
		}

		protected Writer createErrorReportWriter() {
			return writer;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
