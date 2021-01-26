package de.symeda.sormas.ui.event.eventparticipantimporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator.RDCF;
import de.symeda.sormas.ui.events.eventparticipantimporter.EventParticipantImporter;
import de.symeda.sormas.ui.importer.EventParticipantImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;

public class EventParticipantImporterTest extends AbstractBeanTest {

	@Test
	public void testImportEventParticipant() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
		EventReferenceDto eventRef = event.toReference();

		// Successful import of 5 event participant
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_success.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef);
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
	}

	@Test
	public void testImportEventParticipantSimilarityPick() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
		EventReferenceDto eventRef = event.toReference();
		PersonDto person = creator.createPerson("Günther", "Heinz");
		creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// Person Similarity: pick
		List<PersonNameDto> persons = FacadeProvider.getPersonFacade().getMatchingNameDtos(user.toReference(), new PersonSimilarityCriteria());
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef) {

				@Override
				protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {

					List<SimilarPersonDto> entries = new ArrayList<>();
					for (PersonNameDto person : persons) {
						if (PersonHelper
							.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
							entries.addAll(FacadeProvider.getPersonFacade().getSimilarPersonsByUuids(Collections.singletonList(person.getUuid())));
						}
					}
					resultConsumer.accept(new EventParticipantImportSimilarityResult(entries.get(0), ImportSimilarityResultOption.PICK));
				}
			};
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().event(eventRef), null, null, null).get(0);
		PersonDto importedPerson = getPersonFacade().getPersonByUuid(importedEventParticipant.getPersonUuid());

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
		assertEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(person.getFirstName(), importedPerson.getFirstName());
		assertEquals(person.getLastName(), importedPerson.getLastName());

		assertEquals(1, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilarityPickEventParticipant()
			throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
		EventReferenceDto eventRef = event.toReference();
		PersonDto person = creator.createPerson("Günther", "Heinz");
		EventParticipantDto eventParticipant = creator.createEventParticipant(eventRef, person, "old desc", user.toReference());

		// Person Similarity: pick event participant
		List<PersonNameDto> persons = FacadeProvider.getPersonFacade().getMatchingNameDtos(user.toReference(), new PersonSimilarityCriteria());
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef) {

				@Override
				protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {

					List<SimilarPersonDto> entries = new ArrayList<>();
					for (PersonNameDto person : persons) {
						if (PersonHelper
							.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
							entries.addAll(FacadeProvider.getPersonFacade().getSimilarPersonsByUuids(Collections.singletonList(person.getUuid())));
						}
					}
					resultConsumer.accept(new EventParticipantImportSimilarityResult(entries.get(0), ImportSimilarityResultOption.PICK));
				}
			};
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().event(eventRef), null, null, null).get(0);

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
		assertEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(eventParticipant.getUuid(), importedEventParticipant.getUuid());
		assertEquals("description 1", importedEventParticipant.getInvolvementDescription());
		assertEquals(1, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilarityCreate()
			throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
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

		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef) {

				@Override
				protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
					resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
				}
			};
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		EventParticipantIndexDto importedEventParticipant =
			eventParticipantFacade.getIndexList(new EventParticipantCriteria().event(eventRef), null, null, null).get(0);

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
		assertNotEquals(person.getUuid(), importedEventParticipant.getPersonUuid());
		assertEquals(2, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantSimilaritySkip() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
		EventReferenceDto eventRef = event.toReference();

		// Person Similarity: create
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_similarities.csv").toURI());

		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef) {

				@Override
				protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
					resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
				}
			};
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(0, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
		assertEquals(0, getPersonFacade().getAllUuids().size());
	}

	@Test
	public void testImportEventParticipantComment() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		EventParticipantFacadeEjbLocal eventParticipantFacade = getBean(EventParticipantFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
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
			Disease.EVD);
		EventReferenceDto eventRef = event.toReference();

		// Successful import of 5 event participant
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_eventparticipant_import_test_comment_success.csv").toURI());
		EventParticipantImporterExtension eventParticipantImporter =
			new EventParticipantImporterExtension(csvFile, false, user, eventRef);
		ImportResultStatus importResult = eventParticipantImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(5, eventParticipantFacade.count(new EventParticipantCriteria().event(eventRef)));
	}

	private static class EventParticipantImporterExtension extends EventParticipantImporter {

		private EventParticipantImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser, EventReferenceDto event) {
			super(inputFile, hasEntityClassRow, currentUser, event);
		}

		protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter(new OutputStream() {

				@Override
				public void write(int b) {
					// Do nothing
				}
			});
		}
	}
}
