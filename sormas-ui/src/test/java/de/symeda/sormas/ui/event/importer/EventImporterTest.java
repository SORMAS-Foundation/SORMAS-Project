package de.symeda.sormas.ui.event.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.events.importer.EventImporter;
import de.symeda.sormas.ui.importer.EventParticipantImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;

@RunWith(MockitoJUnitRunner.class)
public class EventImporterTest extends AbstractBeanTest {

	@Test
	public void testImportAllEvents() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		TestDataCreator creator = new TestDataCreator();

		TestDataCreator.RDCF rdcf = creator.createRDCF("Bourgogne-Franche-Comté", "Côte d'Or", "Dijon", "CHU Dijon Bourgogne");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_success.csv").toURI());
		EventImporterExtension eventImporter = new EventImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = eventImporter.runImport();

		assertEquals(eventImporter.errors.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(4, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		List<EventDto> events = getEventFacade().getAllActiveEventsAfter(null);
		Optional<EventDto> optionalEventWith2Participants = events.stream()
			.filter(event -> "Event title with 2 participants".equals(event.getEventTitle()))
			.findFirst();
		assertTrue(optionalEventWith2Participants.isPresent());
		optionalEventWith2Participants.ifPresent(event -> {
			List<EventParticipantDto> participants = getEventParticipantFacade().getAllActiveEventParticipantsByEvent(event.getUuid());
			assertEquals(2, participants.size());
		});

		// Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = eventImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(4, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: pick
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
				Optional<SimilarPersonDto> optionalPerson = getPersonFacade().getSimilarPersonsByUuids(getPersonFacade().getAllUuids())
					.stream()
					.filter(person -> Objects.equals(person.getFirstName(), newPerson.getFirstName()) && Objects.equals(person.getLastName(), newPerson.getLastName()))
					.findFirst();
				assertTrue(optionalPerson.isPresent());
				resultConsumer.accept(new EventParticipantImportSimilarityResult(optionalPerson.get(), ImportSimilarityResultOption.PICK));
			}
		};
		importResult = eventImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: cancel
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CANCEL));
			}
		};
		importResult = eventImporter.runImport();

		assertEquals(ImportResultStatus.CANCELED, importResult);
		assertEquals(6, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: create
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = eventImporter.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(8, getEventFacade().count(null));
		assertEquals(6, getPersonFacade().count(null));

		// Successful import of 5 cases from a commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_comment_success.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user);
		importResult = eventImporter.runImport();

		assertEquals(eventImporter.errors.toString(), ImportResultStatus.COMPLETED, importResult);
		assertEquals(10, getEventFacade().count(null));
	}

	private static class EventImporterExtension extends EventImporter {

		private StringBuilder errors = new StringBuilder("");
		private StringBuilderWriter writer = new StringBuilderWriter(errors);

		private EventImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) {
			super(inputFile, hasEntityClassRow, currentUser);
		}

		protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
		}

		protected Writer createErrorReportWriter() {
			return writer;
		}
	}
}
