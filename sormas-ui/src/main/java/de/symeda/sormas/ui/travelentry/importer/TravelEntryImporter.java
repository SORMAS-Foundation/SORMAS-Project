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

package de.symeda.sormas.ui.travelentry.importer;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.travelentryimport.TravelEntryImportEntities;
import de.symeda.sormas.api.travelentry.travelentryimport.TravelEntryImportFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

public class TravelEntryImporter extends DataImporter {

	protected final TravelEntryImporter.TravelEntryImportLock PERSON_SELECT_LOCK = new TravelEntryImportLock();
	private UI currentUI;
	private final TravelEntryImportFacade importFacade;

	public TravelEntryImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator csvSeparator) throws IOException {

		super(inputFile, hasEntityClassRow, currentUser, csvSeparator);
		importFacade = FacadeProvider.getTravelEntryImportFacade();
	}

	@Override
	public void startImport(Consumer<StreamResource> errorReportConsumer, UI currentUI, boolean duplicatesPossible)
		throws IOException, CsvValidationException {

		this.currentUI = currentUI;
		super.startImport(errorReportConsumer, currentUI, duplicatesPossible);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		ImportLineResultDto<TravelEntryImportEntities> importResult =
			importFacade.importData(values, entityClasses, entityProperties, entityPropertyPaths, !firstLine);

		if (importResult.isError()) {
			writeImportError(values, importResult.getMessage());
			return ImportLineResult.ERROR;
		} else if (importResult.isDuplicate()) {
			TravelEntryImportEntities entities = importResult.getImportEntities();
			PersonDto importPerson = entities.getPerson();

			TravelEntryImportConsumer consumer = new TravelEntryImportConsumer();
			ImportSimilarityResultOption resultOption = null;
			String pickedPersonUuid = null;

			synchronized (PERSON_SELECT_LOCK) {
				handlePersonSimilarity(
					importPerson,
					consumer::onImportResult,
					PersonImportSimilarityResult::new,
					Strings.infoSelectOrCreatePersonForImport,
					currentUI);

				try {
					if (!PERSON_SELECT_LOCK.wasNotified) {
						PERSON_SELECT_LOCK.wait();
					}
				} catch (InterruptedException e) {
					logger.error("InterruptedException when trying to perform LOCK.wait() in travel entry import: " + e.getMessage());
					throw e;
				}

				PERSON_SELECT_LOCK.wasNotified = false;
				if (consumer.result != null) {
					resultOption = consumer.result.getResultOption();
					pickedPersonUuid = consumer.result.getMatchingPerson() != null ? consumer.result.getMatchingPerson().getUuid() : null;
				}
			}

			if (resultOption == ImportSimilarityResultOption.SKIP) {
				consumer.result = null;
				return ImportLineResult.SKIPPED;
			} else if (resultOption == ImportSimilarityResultOption.CANCEL) {
				cancelImport();
				return ImportLineResult.SKIPPED;
			} else {
				ImportLineResultDto<TravelEntryImportEntities> saveResult;
				if (resultOption == ImportSimilarityResultOption.PICK && pickedPersonUuid != null) {
					saveResult = importFacade.importDataWithExistingPerson(pickedPersonUuid, values, entityClasses, entityPropertyPaths);
				} else {
					saveResult = importFacade.saveImportedEntities(entities);
				}

				if (saveResult.isError()) {
					writeImportError(values, saveResult.getMessage());
					return ImportLineResult.ERROR;
				}
			}
		} else if (importResult.getResult() == de.symeda.sormas.api.importexport.ImportLineResult.SKIPPED) {
			return ImportLineResult.SKIPPED;
		}

		return ImportLineResult.SUCCESS;
	}

	private class TravelEntryImportConsumer {

		protected PersonImportSimilarityResult result;

		private void onImportResult(PersonImportSimilarityResult result) {
			this.result = result;
			synchronized (PERSON_SELECT_LOCK) {
				PERSON_SELECT_LOCK.notify();
				PERSON_SELECT_LOCK.wasNotified = true;
			}
		}
	}

	private static class TravelEntryImportLock {

		protected boolean wasNotified = false;
	}

}
