/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.events.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.eventimport.EventImportEntities;
import de.symeda.sormas.api.event.eventimport.EventImportFacade;
import de.symeda.sormas.api.event.eventimport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.EventParticipantImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;

/**
 * Data importer that is used to import cases and associated samples.
 * This importer adds the following logic:
 * 
 * - Check the database for similar cases and, if at least one is found, execute the
 * similarityCallback received by the calling class.
 * - The import will wait for the similarityCallback to be resolved before it is continued
 * - Based on the results of the similarityCallback, an existing case might be overridden by
 * the data in the CSV file
 * - Save the person and case to the database (unless the case was skipped or the import
 * was canceled)
 */
public class EventImporter extends DataImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventImporter.class);

	private UI currentUI;
	private final EventImportFacade eventImportFacade;
	private final EventFacade eventFacade;
	private final EventParticipantFacade eventParticipantFacade;
	private final PersonFacade personFacade;

	public EventImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser) {
		super(inputFile, hasEntityClassRow, currentUser);

		eventImportFacade = FacadeProvider.getEventImportFacade();
		eventFacade = FacadeProvider.getEventFacade();
		eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
		personFacade = FacadeProvider.getPersonFacade();
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI, boolean duplicatesPossible)
		throws IOException, CsvValidationException {

		this.currentUI = currentUI;
		super.startImport(addErrorReportToLayoutCallback, currentUI, duplicatesPossible);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		ImportLineResultDto<EventImportEntities> importResult =
			eventImportFacade.importEventData(values, entityClasses, entityProperties, entityPropertyPaths, !firstLine);

		if (importResult.isError()) {
			writeImportError(values, importResult.getMessage());
			return ImportLineResult.ERROR;
		} else if (importResult.isDuplicate()) {
			EventImportEntities entities = importResult.getImportEntities();
			List<EventParticipantDto> eventParticipants = entities.getEventParticipants();

			ImportSimilarityResultOption resultOption = null;

			for (EventParticipantDto eventParticipant : eventParticipants) {
				EventImportConsumer consumer = new EventImportConsumer();
				EventImportLock personSelectLock = new EventImportLock();
				PersonDto importPerson = eventParticipant.getPerson();
				// We need to pause the current thread to prevent the import from continuing until the user has acted
				synchronized (personSelectLock) {
					// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
					// to allow the importer to resume
					handlePersonSimilarity(importPerson, result -> consumer.onImportResult(result, personSelectLock));

					try {
						if (!personSelectLock.wasNotified) {
							personSelectLock.wait();
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException when trying to perform LOCK.wait() in case import: " + e.getMessage());
						throw e;
					}

					if (consumer.result != null) {
						resultOption = consumer.result.getResultOption();
					}

					// If the user picked an existing person, override the eventparticipant person with it
					if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
						eventParticipant.getPerson().setUuid(consumer.result.getMatchingPerson().getUuid());
						// Reset the importResult option for case selection
						resultOption = null;
					}
				}

				if (resultOption == ImportSimilarityResultOption.SKIP) {
					consumer.result = null;
					return ImportLineResult.DUPLICATE;
				} else if (resultOption == ImportSimilarityResultOption.CANCEL) {
					cancelImport();
					return ImportLineResult.SKIPPED;
				}
			}

			ImportLineResultDto<EventImportEntities> saveResult = eventImportFacade.saveImportedEntities(entities);

			if (saveResult.isError()) {
				writeImportError(values, importResult.getMessage());
				return ImportLineResult.ERROR;
			}
		}

		return ImportLineResult.SUCCESS;
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate persons.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handlePersonSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {
		// TODO
	}

	private class EventImportConsumer {

		protected EventParticipantImportSimilarityResult result;

		private void onImportResult(EventParticipantImportSimilarityResult result, EventImportLock LOCK) {
			this.result = result;
			synchronized (LOCK) {
				LOCK.notify();
				LOCK.wasNotified = true;
			}
		}
	}

	private class EventImportLock {

		protected boolean wasNotified = false;
	}
}
