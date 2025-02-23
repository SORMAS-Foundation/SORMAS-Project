package de.symeda.sormas.ui.event.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.events.importer.EventImporter;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

public class EventImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportAllEvents() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		var rdcf = creator.createRDCF("Bourgogne-Franche-Comté", "Côte d'Or", "Dijon", "CHU Dijon Bourgogne");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		// Successful import of 5 cases
		File csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_success.csv").toURI());
		EventImporterExtension eventImporter = new EventImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult, eventImporter.errors.toString());
		assertEquals(4, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		List<EventDto> events = getEventFacade().getAllAfter(null);
		Optional<EventDto> optionalEventWith2Participants =
			events.stream().filter(event -> "Event title with 2 participants".equals(event.getEventTitle())).findFirst();
		assertTrue(optionalEventWith2Participants.isPresent());
		optionalEventWith2Participants.ifPresent(event -> {
			List<EventParticipantDto> participants = getEventParticipantFacade().getAllActiveEventParticipantsByEvent(event.getUuid());
			assertEquals(2, participants.size());
		});

		// Similarity: skip
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<PersonImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new PersonImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
			}
		};
		importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(4, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: pick
		List<SimilarPersonDto> persons = FacadeProvider.getPersonFacade().getSimilarPersonDtos(new PersonSimilarityCriteria());
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<PersonImportSimilarityResult> resultConsumer) {
				List<SimilarPersonDto> entries = new ArrayList<>();
				for (SimilarPersonDto person : persons) {
					if (PersonHelper
						.areNamesSimilar(newPerson.getFirstName(), newPerson.getLastName(), person.getFirstName(), person.getLastName(), null)) {
						entries.add(person);
					}
				}
				resultConsumer.accept(new PersonImportSimilarityResult(entries.get(0), ImportSimilarityResultOption.PICK));
			}
		};
		importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(6, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: cancel
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<PersonImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new PersonImportSimilarityResult(null, ImportSimilarityResultOption.CANCEL));
			}
		};
		importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.CANCELED, importResult);
		assertEquals(6, getEventFacade().count(null));
		assertEquals(3, getPersonFacade().count(null));

		// Similarity: create
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_similarities.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user) {

			@Override
			protected void handlePersonSimilarity(PersonDto newPerson, Consumer<PersonImportSimilarityResult> resultConsumer) {
				resultConsumer.accept(new PersonImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
			}
		};
		importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(8, getEventFacade().count(null));
		assertEquals(6, getPersonFacade().count(null));

		// Successful import of 5 cases from a commented CSV file
		csvFile = new File(getClass().getClassLoader().getResource("sormas_event_import_test_comment_success.csv").toURI());
		eventImporter = new EventImporterExtension(csvFile, true, user);
		importResult = eventImporter.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult, eventImporter.errors.toString());
		assertEquals(10, getEventFacade().count(null));
	}

	private static class EventImporterExtension extends EventImporter {

		private StringBuilder errors = new StringBuilder("");
		private StringBuilderWriter writer = new StringBuilderWriter(errors);

		private EventImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) throws IOException {
			super(inputFile, hasEntityClassRow, currentUser, ValueSeparator.DEFAULT);
		}

		protected void handlePersonSimilarity(PersonDto newPerson, Consumer<PersonImportSimilarityResult> resultConsumer) {
			resultConsumer.accept(new PersonImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
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
