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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.CaseImportSimilarityInput;
import de.symeda.sormas.ui.importer.CaseImportSimilarityResult;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportCellData;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.ImporterPersonHelper;
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

	/**
	 * The name of the first column that belongs to a sample. This is used to determine whether the property
	 * belongs to a new or the currently processed sample.
	 */
	private String firstSampleColumnName;
	/**
	 * The name of the first column that belongs to a pathogen test. This is used to determine whether the property
	 * belongs to a new or the currently processed pathogen test.
	 */
	private String firstPathogenTestColumnName;
	/**
	 * When a new sample or pathogen test is reached and the sample that was processed last does not have any entries,
	 * it is discarded.
	 */
	private boolean currentSampleHasEntries = false;
	/**
	 * When a new sample or pathogen test is reached and the pathogen test that was processed last does not have any entries,
	 * it is discarded.
	 */
	private boolean currentPathogenTestHasEntries = false;

	private UI currentUI;

	public CaseImporter(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser) {
		super(inputFile, hasEntityClassRow, currentUser);
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI, boolean duplicatesPossible) throws IOException {

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

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		// Create new temporary objects for the case and its person
		final PersonDto newPersonTmp = PersonDto.build();
		final CaseDataDto newCaseTmp = CaseDataDto.build(newPersonTmp.toReference(), null);
		newCaseTmp.setReportingUser(currentUser);

		// Create lists for all samples and pathogen tests that might get associated with the case
		final List<SampleDto> samples = new ArrayList<>();
		final List<PathogenTestDto> pathogenTests = new ArrayList<>();

		boolean caseHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, !firstLine, (cellData) -> {
			try {
				// If the first column of a new sample or pathogen test has been reached, remove the last sample and 
				// pathogen test if they don't have any entries
				if (String.join(".", cellData.getEntityPropertyPath()).equals(firstSampleColumnName)
					|| String.join(".", cellData.getEntityPropertyPath()).equals(firstPathogenTestColumnName)) {
					if (samples.size() > 0 && !currentSampleHasEntries) {
						samples.remove(samples.size() - 1);
						currentSampleHasEntries = true;
					}
					if (pathogenTests.size() > 0 && !currentPathogenTestHasEntries) {
						pathogenTests.remove(pathogenTests.size() - 1);
						currentPathogenTestHasEntries = true;
					}
				}

				if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(SampleDto.class))) {
					// If the current column belongs to a sample, set firstSampleColumnName if it's empty, add a new sample
					// to the list if the first column of a new sample has been reached and insert the entry of the cell into the sample
					if (firstSampleColumnName == null) {
						firstSampleColumnName = String.join(".", cellData.getEntityPropertyPath());
					}
					if (String.join(".", cellData.getEntityPropertyPath()).equals(firstSampleColumnName)) {
						currentSampleHasEntries = false;
						samples.add(SampleDto.build(currentUser, new CaseReferenceDto(newCaseTmp.getUuid())));
					}
					if (!StringUtils.isEmpty(cellData.getValue())) {
						currentSampleHasEntries = true;
						insertColumnEntryIntoSampleData(samples.get(samples.size() - 1), null, cellData.getValue(), cellData.getEntityPropertyPath());
					}

				} else if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PathogenTestDto.class))) {
					// If the current column belongs to a pathogen test, set firstPathogenTestColumnName if it's empty, add a new test
					// to the list if the first column of a new test has been reached and insert the entry of the cell into the test
					if (firstPathogenTestColumnName == null) {
						firstPathogenTestColumnName = String.join(".", cellData.getEntityPropertyPath());
					}
					if (!samples.isEmpty()) {
						SampleDto referenceSample = samples.get(samples.size() - 1);
						if (String.join(".", cellData.getEntityPropertyPath()).equals(firstPathogenTestColumnName)) {
							currentPathogenTestHasEntries = false;
							pathogenTests.add(PathogenTestDto.build(new SampleReferenceDto(referenceSample.getUuid()), currentUser));
						}
						if (!StringUtils.isEmpty(cellData.getValue())) {
							currentPathogenTestHasEntries = true;
							insertColumnEntryIntoSampleData(
								null,
								pathogenTests.get(pathogenTests.size() - 1),
								cellData.getValue(),
								cellData.getEntityPropertyPath());
						}
					}
				} else if (!StringUtils.isEmpty(cellData.getValue())) {
					// If the cell entry is not empty, try to insert it into the current case or its person
					insertColumnEntryIntoData(newCaseTmp, newPersonTmp, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		// Remove the last sample and pathogen test if empty
		if (samples.size() > 0 && !currentSampleHasEntries) {
			samples.remove(samples.size() - 1);
		}
		if (pathogenTests.size() > 0 && !currentPathogenTestHasEntries) {
			pathogenTests.remove(pathogenTests.size() - 1);
		}

		CaseDataDto newCase = newCaseTmp;
		PersonDto newPerson = newPersonTmp;

		// If the row does not have any import errors, call the backend validation of all associated entities
		if (!caseHasImportError) {
			try {
				FacadeProvider.getPersonFacade().validate(newPerson);
				FacadeProvider.getCaseFacade().validate(newCase);
				for (SampleDto sample : samples) {
					FacadeProvider.getSampleFacade().validate(sample);
				}
				for (PathogenTestDto pathogenTest : pathogenTests) {
					FacadeProvider.getPathogenTestFacade().validate(pathogenTest);
				}
			} catch (ValidationRuntimeException e) {
				caseHasImportError = true;
				writeImportError(values, e.getMessage());
			}
		}

		// If the case still does not have any import errors, search for similar cases in the database and, if there are any,
		// display a window to resolve the conflict to the user
		if (!caseHasImportError) {
			try {
				CaseImportConsumer consumer = new CaseImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				CaseImportLock LOCK = new CaseImportLock();
				// We need to pause the current thread to prevent the import from continuing until the user has acted
				synchronized (LOCK) {
					// Retrieve all similar cases from the database
					CaseCriteria caseCriteria = new CaseCriteria().disease(newCase.getDisease()).region(newCase.getRegion());
					CaseSimilarityCriteria criteria = new CaseSimilarityCriteria().firstName(newPerson.getFirstName())
						.lastName(newPerson.getLastName())
						.caseCriteria(caseCriteria)
						.reportDate(newCase.getReportDate());
					List<CaseIndexDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(criteria);

					if (similarCases.size() > 0) {
						// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
						// to allow the importer to resume
						handleSimilarity(new CaseImportSimilarityInput(newCase, newPerson, similarCases), new Consumer<CaseImportSimilarityResult>() {

							@Override
							public void accept(CaseImportSimilarityResult result) {
								consumer.onImportResult(result, LOCK);
							}
						});

						try {
							if (!LOCK.wasNotified) {
								LOCK.wait();
							}
						} catch (InterruptedException e) {
							logger.error("InterruptedException when trying to perform LOCK.wait() in case import: " + e.getMessage());
							throw e;
						}

						if (consumer.result != null) {
							resultOption = consumer.result.getResultOption();
						}

						// If the user chose to override an existing case with the imported case, insert the new data into the existing case and associate the imported samples with it
						if (resultOption != null
							&& resultOption != ImportSimilarityResultOption.SKIP
							&& resultOption != ImportSimilarityResultOption.CANCEL
							&& resultOption != ImportSimilarityResultOption.PICK) {
							if (resultOption == ImportSimilarityResultOption.OVERRIDE && consumer.result.getMatchingCase() != null) {
								final CaseDataDto matchingCaseTmp =
									FacadeProvider.getCaseFacade().getCaseDataByUuid(consumer.result.getMatchingCase().getUuid());
								final PersonDto matchingCasePersonTmp =
									FacadeProvider.getPersonFacade().getPersonByUuid(matchingCaseTmp.getPerson().getUuid());
								caseHasImportError =
									insertRowIntoData(values, entityClasses, entityPropertyPaths, true, new Function<ImportCellData, Exception>() {

										@Override
										public Exception apply(ImportCellData cellData) {
											try {
												if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(SampleDto.class))
													|| DataHelper
														.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PathogenTestDto.class))) {
													return null;
												}

												insertColumnEntryIntoData(
													matchingCaseTmp,
													matchingCasePersonTmp,
													cellData.getValue(),
													cellData.getEntityPropertyPath());

												for (SampleDto sample : samples) {
													sample.setAssociatedCase(new CaseReferenceDto(matchingCaseTmp.getUuid()));
												}
											} catch (ImportErrorException | InvalidColumnException e) {
												return e;
											}

											return null;
										}
									});

								newCase = matchingCaseTmp;
								newPerson = matchingCasePersonTmp;
							}
						}
					}
				}

				// Prevent the case from being imported if it has an epid number that already exists in the system
				if (!caseHasImportError
					&& !ImportSimilarityResultOption.OVERRIDE.equals(resultOption)
					&& newCase.getEpidNumber() != null
					&& FacadeProvider.getCaseFacade().doesEpidNumberExist(newCase.getEpidNumber(), "", newCase.getDisease())) {
					caseHasImportError = true;
					writeImportError(values, I18nProperties.getString(Strings.messageEpidNumberWarning));
				}

				// Determine the import result and, if there was no duplicate, the user did not skip over the case 
				// or an existing case was overridden, save the case, person, samples and pathogen tests to the database
				if (caseHasImportError) {
					return ImportLineResult.ERROR;
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.SKIP) {
					consumer.result = null;
					return ImportLineResult.SKIPPED;
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.PICK) {
					consumer.result = null;
					return ImportLineResult.DUPLICATE;
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.CANCEL) {
					cancelImport();
					return ImportLineResult.SKIPPED;
				} else {
					PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(newPerson);
					newCase.setPerson(savedPerson.toReference());
					// Workarround: Reset the change date to avoid OutdatedEntityExceptions
					// Should be changed when doing #2265
					newCase.setChangeDate(new Date());
					FacadeProvider.getCaseFacade().saveCase(newCase);
					for (SampleDto sample : samples) {
						FacadeProvider.getSampleFacade().saveSample(sample);
					}
					for (PathogenTestDto pathogenTest : pathogenTests) {
						FacadeProvider.getPathogenTestFacade().savePathogenTest(pathogenTest);
					}
					consumer.result = null;
					return ImportLineResult.SUCCESS;
				}
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	/**
	 * Inserts the entry of a single cell into the case or its person.
	 */
	private void insertColumnEntryIntoData(CaseDataDto caze, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Set the current element to the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the case or person fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade()
							.getByName(entry, ImporterPersonHelper.getRegionBasedOnDistrict(pd.getName(), caze, null, person, currentElement), false);
						if (district.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildEntityProperty(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = FacadeProvider.getCommunityFacade()
							.getByName(entry, ImporterPersonHelper.getDistrictBasedOnCommunity(pd.getName(), caze, person, currentElement), false);
						if (community.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrDistrict,
									entry,
									buildEntityProperty(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData =
							ImporterPersonHelper.getDistrictAndCommunityBasedOnFacility(pd.getName(), caze, person, currentElement);
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade()
							.getByName(entry, infrastructureData.getElement0(), infrastructureData.getElement1(), false);
						if (facility.isEmpty()) {
							if (infrastructureData.getElement1() != null) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrCommunity,
										entry,
										buildEntityProperty(entryHeaderPath)));
							} else {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
						} else if (facility.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (facility.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
						}
					} else if (propertyType.isAssignableFrom(PointOfEntryReferenceDto.class)) {
						List<PointOfEntryReferenceDto> pointOfEntry =
							FacadeProvider.getPointOfEntryFacade().getByName(entry, caze.getDistrict(), false);
						if (pointOfEntry.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrDistrict,
									entry,
									buildEntityProperty(entryHeaderPath)));
						} else if (pointOfEntry.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importPointOfEntryNotUniqueInDistrict,
									entry,
									buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, pointOfEntry.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	/**
	 * Inserts the entry of a single cell into the sample or pathogen test.
	 */
	private void insertColumnEntryIntoSampleData(SampleDto sample, PathogenTestDto test, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {
		Object currentElement = sample != null ? sample : test;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the sample or pathogen test fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> lab = FacadeProvider.getFacilityFacade().getLaboratoriesByName(entry, false);
						if (lab.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
						} else if (lab.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(Validations.importLabNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, lab.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate cases.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handleSimilarity(CaseImportSimilarityInput input, Consumer<CaseImportSimilarityResult> resultConsumer) {
		currentUI.accessSynchronously(new Runnable() {

			@Override
			public void run() {
				CasePickOrImportField pickOrImportField = new CasePickOrImportField(input.getCaze(), input.getPerson(), input.getSimilarCases());
				pickOrImportField.setWidth(1024, Unit.PIXELS);

				final CommitDiscardWrapperComponent<CasePickOrImportField> component = new CommitDiscardWrapperComponent<>(pickOrImportField);

				component.addCommitListener(new CommitListener() {

					@Override
					public void onCommit() {
						CaseIndexDto pickedCase = pickOrImportField.getValue();
						if (pickedCase != null) {
							if (pickOrImportField.isOverrideCase()) {
								resultConsumer.accept(new CaseImportSimilarityResult(pickedCase, ImportSimilarityResultOption.OVERRIDE));
							} else {
								resultConsumer.accept(new CaseImportSimilarityResult(pickedCase, ImportSimilarityResultOption.PICK));
							}
						} else {
							// TODO May be wrong here!
							resultConsumer.accept(new CaseImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
						}
					}
				});

				DiscardListener discardListener = new DiscardListener() {

					@Override
					public void onDiscard() {
						resultConsumer.accept(new CaseImportSimilarityResult(null, ImportSimilarityResultOption.CANCEL));
					}
				};
				component.addDiscardListener(discardListener);
				component.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
				component.getCommitButton().setEnabled(false);

				Button skipButton = ButtonHelper.createButton(Captions.actionSkip, e -> {
					component.removeDiscardListener(discardListener);
					component.discard();
					resultConsumer.accept(new CaseImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
				});
				component.getButtonsPanel().addComponentAsFirst(skipButton);

				pickOrImportField.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateCase));
			}
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
