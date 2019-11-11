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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
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
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * These are the steps performed by the case importer:
 * 
 * 1) Read the CSV file from the passed file path and open an error report file
 * 2) Read the header row from the CSV and build a list of properties based on its columns
 * 3) Read the next line from the CSV and fill a case with its contents by using reflection;
 *    Validate the case afterwards
 * 	  - If an error is thrown doing this, the import of this case is canceled and the case gets
 *   	added to the error report file
 * 4) Check the database for similar cases and, if at least one is found, execute the 
 *    similarityCallback received by the calling class.
 *    - The import will wait for the similarityCallback to be resolved before it is continued
 * 5) Based on the results of the similarityCallback, an existing case might be overridden by 
 * 	  the data in the CSV file
 * 6) Save the person and case to the database (unless the case was skipped or the import
 *    was canceled)
 * 7) Repeat from step 3 until all cases have been handled
 */
public class CaseImporter extends DataImporter {

	private String firstSampleColumnName;
	private String firstPathogenTestColumnName;
	private boolean currentEntityHasEntries;

	public CaseImporter(File inputFile, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this(inputFile, null, currentUser, currentUI);
	}

	public CaseImporter(File inputFile, OutputStreamWriter errorReportWriter, UserReferenceDto currentUser, UI currentUI) throws IOException {
		super(inputFile, true, errorReportWriter, currentUser, currentUI);

	}

	@Override
	protected void importDataFromCsvLine(String[] values, String[] entityClasses, String[] entityProperties, String[][] entityPropertyPaths) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			hasImportError = true;
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			importedCallback.accept(ImportResult.ERROR);
			return;
		}

		final PersonDto newPersonTmp = PersonDto.build();
		final CaseDataDto newCaseTmp = CaseDataDto.build(newPersonTmp.toReference(), null);
		newCaseTmp.setReportingUser(currentUser);

		final List<SampleDto> samples = new ArrayList<>();
		final List<PathogenTestDto> pathogenTests = new ArrayList<>();

		boolean caseHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, new Function<ImportCellData, Exception>() {
			@Override
			public Exception apply(ImportCellData cellData) {
				try {
					if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(SampleDto.class))) {
						if (firstSampleColumnName == null) {
							firstSampleColumnName = String.join(".", cellData.getEntityPropertyPath());
						}
						if (String.join(".", cellData.getEntityPropertyPath()).equals(firstSampleColumnName)) {
							if (samples.size() > 0 && !currentEntityHasEntries) {
								samples.remove(samples.size() - 1);
							}
							currentEntityHasEntries = false;
							samples.add(SampleDto.buildSample(currentUser, new CaseReferenceDto(newCaseTmp.getUuid())));
						}
						if (!StringUtils.isEmpty(cellData.getValue())) {
							currentEntityHasEntries = true;
							insertColumnEntryIntoSampleData(samples.get(samples.size() - 1), null, cellData.getValue(), cellData.getEntityPropertyPath());
						}
					} else if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PathogenTestDto.class))) {
						if (firstPathogenTestColumnName == null) {
							firstPathogenTestColumnName = String.join(".", cellData.getEntityPropertyPath());
						}
						SampleDto referenceSample = samples.get(samples.size() - 1);
						if (String.join(".", cellData.getEntityPropertyPath()).equals(firstPathogenTestColumnName)) {
							if (pathogenTests.size() > 0 && !currentEntityHasEntries) {
								pathogenTests.remove(pathogenTests.size() - 1);
							}
							currentEntityHasEntries = false;
							pathogenTests.add(PathogenTestDto.build(new SampleReferenceDto(referenceSample.getUuid()), currentUser));
						}
						if (!StringUtils.isEmpty(cellData.getValue())) {
							currentEntityHasEntries = true;					
							insertColumnEntryIntoSampleData(null, pathogenTests.get(pathogenTests.size() - 1), cellData.getValue(), cellData.getEntityPropertyPath());
						}
					} else {
						insertColumnEntryIntoData(newCaseTmp, newPersonTmp, cellData.getValue(), cellData.getEntityPropertyPath());
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			}
		});

		CaseDataDto newCase = newCaseTmp;
		PersonDto newPerson = newPersonTmp;

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
				hasImportError = true;
				caseHasImportError = true;
				writeImportError(values, e.getMessage());
			}
		}

		if (!caseHasImportError) {
			try {
				CaseImportConsumer consumer = new CaseImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				CaseImportLock LOCK = new CaseImportLock();
				synchronized (LOCK) {
					CaseCriteria caseCriteria = new CaseCriteria()
							.disease(newCase.getDisease())
							.region(newCase.getRegion());
					CaseSimilarityCriteria criteria = new CaseSimilarityCriteria()
							.firstName(newPerson.getFirstName())
							.lastName(newPerson.getLastName())
							.caseCriteria(caseCriteria)
							.reportDate(newCase.getReportDate());
					List<CaseIndexDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(criteria, currentUser.getUuid());

					if (similarCases.size() > 0) {
						handleSimilarity(new ImportSimilarityInput(newCase, newPerson, similarCases), new Consumer<ImportSimilarityResult>() {
							@Override
							public void accept(ImportSimilarityResult result) {
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

						if (resultOption != null && resultOption != ImportSimilarityResultOption.SKIP && resultOption != ImportSimilarityResultOption.CANCEL && resultOption != ImportSimilarityResultOption.PICK) {
							if (resultOption == ImportSimilarityResultOption.OVERRIDE && consumer.result.getMatchingCase() != null) {
								final CaseDataDto matchingCaseTmp = FacadeProvider.getCaseFacade().getCaseDataByUuid(consumer.result.getMatchingCase().getUuid());
								final PersonDto matchingCasePersonTmp = FacadeProvider.getPersonFacade().getPersonByUuid(matchingCaseTmp.getPerson().getUuid());
								caseHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, true, new Function<ImportCellData, Exception>() {
									@Override
									public Exception apply(ImportCellData cellData) {
										try {
											if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(SampleDto.class))
													|| DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PathogenTestDto.class))) {
												return null;
											}
											
											insertColumnEntryIntoData(matchingCaseTmp, matchingCasePersonTmp, cellData.getValue(), cellData.getEntityPropertyPath());
											
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

				if (caseHasImportError) {
					// In case insertRowIntoCase when matching person/case has thrown an unexpected error
					importedCallback.accept(ImportResult.ERROR);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.SKIP) {
					// Reset the import result
					consumer.result = null;
					importedCallback.accept(ImportResult.SKIPPED);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.PICK) {
					consumer.result = null;
					importedCallback.accept(ImportResult.DUPLICATE);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.CANCEL) {
					cancelAfterCurrent = true;
				} else {
					PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(newPerson);
					newCase.setPerson(savedPerson.toReference());
					FacadeProvider.getCaseFacade().saveCase(newCase);
					for (SampleDto sample : samples) {
						FacadeProvider.getSampleFacade().saveSample(sample);
					}
					for (PathogenTestDto pathogenTest : pathogenTests) {
						FacadeProvider.getPathogenTestFacade().savePathogenTest(pathogenTest);
					}
					// Reset the import result
					consumer.result = null;
					importedCallback.accept(ImportResult.SUCCESS);
				}
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(values, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
			}
		} else {
			importedCallback.accept(ImportResult.ERROR);
		}	
	}

	private void insertColumnEntryIntoData(CaseDataDto caze, PersonDto person, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Replace PersonReferenceDto with the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade().getByName(entry, caze.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildEntityProperty(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = FacadeProvider.getCommunityFacade().getByName(entry, caze.getDistrict());
						if (community.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade().getByName(entry, caze.getDistrict(), caze.getCommunity());
						if (facility.isEmpty()) {
							if (caze.getCommunity() != null) {
								throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrCommunity, entry, buildEntityProperty(entryHeaderPath)));
							} else {
								throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildEntityProperty(entryHeaderPath)));
							}
						} else if (facility.size() > 1 && caze.getCommunity() == null) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (facility.size() > 1 && caze.getCommunity() != null) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
						}
					} else if (propertyType.isAssignableFrom(PointOfEntryReferenceDto.class)) {
						List<PointOfEntryReferenceDto> pointOfEntry = FacadeProvider.getPointOfEntryFacade().getByName(entry, caze.getDistrict());
						if (pointOfEntry.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (pointOfEntry.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importPointOfEntryNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, pointOfEntry.get(0));
						}
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	private void insertColumnEntryIntoSampleData(SampleDto sample, PathogenTestDto test, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = sample != null ? sample : test;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> lab = FacadeProvider.getFacilityFacade().getLaboratoriesByName(entry);
						if (lab.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
						} else if (lab.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importLabNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, lab.get(0));
						}
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	private void handleSimilarity(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
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
								resultConsumer.accept(new ImportSimilarityResult(pickedCase, ImportSimilarityResultOption.OVERRIDE));
							} else {
								resultConsumer.accept(new ImportSimilarityResult(pickedCase, ImportSimilarityResultOption.PICK));
							}
						} else {
							// TODO May be wrong here!
							resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
						}
					}});

				DiscardListener discardListener = new DiscardListener() {
					@Override
					public void onDiscard() {
						resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.CANCEL));
					}
				};
				component.addDiscardListener(discardListener);
				component.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
				component.getCommitButton().setEnabled(false);

				Button skipButton = new Button(I18nProperties.getCaption(Captions.actionSkip));
				skipButton.addClickListener(e -> {
					currentUI.accessSynchronously(new Runnable() {
						@Override
						public void run() {
							component.removeDiscardListener(discardListener);
							component.discard();
							resultConsumer.accept(new ImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
						}
					});
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
		protected ImportSimilarityResult result;

		private void onImportResult(ImportSimilarityResult result, CaseImportLock LOCK) {
			this.result = result;
			synchronized(LOCK) {
				LOCK.notify();
				LOCK.wasNotified = true;
			}
		}
	}

	private class CaseImportLock {
		protected boolean wasNotified = false;
	}

}
