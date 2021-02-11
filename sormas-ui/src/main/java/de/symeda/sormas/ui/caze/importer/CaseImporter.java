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
package de.symeda.sormas.ui.caze.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.caseimport.CaseImportEntities;
import de.symeda.sormas.api.caze.caseimport.CaseImportFacade;
import de.symeda.sormas.api.caze.caseimport.ImportLineResultDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.importer.CaseImportSimilarityInput;
import de.symeda.sormas.ui.importer.CaseImportSimilarityResult;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.person.PersonSelectionField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

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
public class CaseImporter extends DataImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaseImporter.class);

	private UI currentUI;
	private final CaseImportFacade caseImportFacade;

	private final PersonFacade personFacade;
	private final CaseFacade caseFacade;

	public CaseImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser) {
		super(inputFile, hasEntityClassRow, currentUser);

		caseImportFacade = FacadeProvider.getCaseImportFacade();

		personFacade = FacadeProvider.getPersonFacade();
		caseFacade = FacadeProvider.getCaseFacade();
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

		ImportLineResultDto<CaseImportEntities> importResult =
			caseImportFacade.importCaseData(values, entityClasses, entityProperties, entityPropertyPaths, !firstLine);

		if (importResult.isError()) {
			writeImportError(values, importResult.getMessage());
			return ImportLineResult.ERROR;
		} else if (importResult.isDuplicate()) {
			CaseImportEntities entities = importResult.getImportEntities();
			CaseDataDto importCase = entities.getCaze();
			PersonDto importPerson = entities.getPerson();

			String selectedPersonUuid = null;
			String selectedCaseUuid = null;

			CaseImportConsumer consumer = new CaseImportConsumer();
			ImportSimilarityResultOption resultOption = null;

			CaseImportLock personSelectLock = new CaseImportLock();
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

				// If the user picked an existing person, override the case person with it
				if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
					selectedPersonUuid = consumer.result.getMatchingPerson().getUuid();
					// Reset the importResult option for case selection
					resultOption = null;
				}
			}

			if (ImportSimilarityResultOption.SKIP.equals(resultOption)) {
				return ImportLineResult.SKIPPED;
			} else {
				final CaseImportLock caseSelectLock = new CaseImportLock();
				synchronized (caseSelectLock) {
					// Retrieve all similar cases from the database
					CaseCriteria caseCriteria = new CaseCriteria().disease(importCase.getDisease()).region(importCase.getRegion());
					CaseSimilarityCriteria criteria =
						new CaseSimilarityCriteria().personUuid(selectedPersonUuid != null ? selectedPersonUuid : importPerson.getUuid())
							.caseCriteria(caseCriteria)
							.reportDate(importCase.getReportDate());

					List<CaseIndexDto> similarCases = caseFacade.getSimilarCases(criteria);

					if (similarCases.size() > 0) {
						// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
						// to allow the importer to resume
						if (selectedPersonUuid != null) {
							importPerson = personFacade.getPersonByUuid(selectedPersonUuid);
						}

						handleCaseSimilarity(
							new CaseImportSimilarityInput(importCase, importPerson, similarCases),
							result -> consumer.onImportResult(result, caseSelectLock));

						try {
							if (!caseSelectLock.wasNotified) {
								caseSelectLock.wait();
							}
						} catch (InterruptedException e) {
							logger.error("InterruptedException when trying to perform LOCK.wait() in case import: " + e.getMessage());
							throw e;
						}

						if (consumer.result != null) {
							resultOption = consumer.result.getResultOption();
						}

						// If the user chose to override an existing case with the imported case, insert the new data into the existing case and associate the imported samples with it
						if (resultOption == ImportSimilarityResultOption.OVERRIDE && consumer.result.getMatchingCase() != null) {
							selectedCaseUuid = consumer.result.getMatchingCase().getUuid();
						}
					}
				}
			}

			if (resultOption == ImportSimilarityResultOption.SKIP) {
				consumer.result = null;
				return ImportLineResult.SKIPPED;
			} else if (resultOption == ImportSimilarityResultOption.PICK) {
				consumer.result = null;
				return ImportLineResult.DUPLICATE;
			} else if (resultOption == ImportSimilarityResultOption.CANCEL) {
				cancelImport();
				return ImportLineResult.SKIPPED;
			} else {
				ImportLineResultDto<CaseImportEntities> saveResult;
				if (selectedPersonUuid != null || selectedCaseUuid != null) {
					saveResult =
						caseImportFacade.updateCaseWithImportData(selectedPersonUuid, selectedCaseUuid, values, entityClasses, entityPropertyPaths);
				} else {
					saveResult = caseImportFacade.saveImportedEntities(entities);
				}

				if (saveResult.isError()) {
					writeImportError(values, importResult.getMessage());
					return ImportLineResult.ERROR;
				}
			}
		}

		return ImportLineResult.SUCCESS;
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate persons.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handlePersonSimilarity(PersonDto newPerson, Consumer<CaseImportSimilarityResult> resultConsumer) {
		currentUI.accessSynchronously(() -> {
			PersonSelectionField personSelect =
				new PersonSelectionField(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCaseImport));
			personSelect.setWidth(1024, Unit.PIXELS);

			if (personSelect.hasMatches()) {
				final CommitDiscardWrapperComponent<PersonSelectionField> component = new CommitDiscardWrapperComponent<>(personSelect);
				component.addCommitListener(() -> {
					SimilarPersonDto person = personSelect.getValue();
					if (person == null) {
						resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
					} else {
						resultConsumer.accept(new CaseImportSimilarityResult(person, null, ImportSimilarityResultOption.PICK));
					}
				});

				component
					.addDiscardListener(() -> resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP)));

				personSelect.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));

				personSelect.selectBestMatch();
			} else {
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}
		});
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate cases.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handleCaseSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
		currentUI.accessSynchronously(() -> {
			CasePickOrImportField pickOrImportField = new CasePickOrImportField(input.getCaze(), input.getPerson(), input.getSimilarCases());
			pickOrImportField.setWidth(1024, Unit.PIXELS);

			final CommitDiscardWrapperComponent<CasePickOrImportField> component = new CommitDiscardWrapperComponent<>(pickOrImportField);

			component.addCommitListener(new CommitListener() {

				@Override
				public void onCommit() {
					CaseIndexDto pickedCase = pickOrImportField.getValue();
					if (pickedCase != null) {
						if (pickOrImportField.isOverrideCase()) {
							resultConsumer.accept(new CaseImportSimilarityResult(null, pickedCase, ImportSimilarityResultOption.OVERRIDE));
						} else {
							resultConsumer.accept(new CaseImportSimilarityResult(null, pickedCase, ImportSimilarityResultOption.PICK));
						}
					} else {
						resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
					}
				}
			});

			DiscardListener discardListener =
				() -> resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.CANCEL));
			component.addDiscardListener(discardListener);
			component.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
			component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			component.getCommitButton().setEnabled(false);

			Button skipButton = ButtonHelper.createButton(Captions.actionSkip, e -> {
				component.removeDiscardListener(discardListener);
				component.discard();
				resultConsumer.accept(new CaseImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP));
			});
			component.getButtonsPanel().addComponentAsFirst(skipButton);

			pickOrImportField.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateCase));
		});
	}

	private class CaseImportConsumer {

		protected CaseImportSimilarityResult result;

		private void onImportResult(CaseImportSimilarityResult result, CaseImportLock LOCK) {
			this.result = result;
			synchronized (LOCK) {
				LOCK.notify();
				LOCK.wasNotified = true;
			}
		}
	}

	private class CaseImportLock {

		protected boolean wasNotified = false;
	}
}
