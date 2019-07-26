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
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
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

	public CaseImporter(File inputFile, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this(inputFile, null, currentUser, currentUI);
	}

	public CaseImporter(File inputFile, OutputStreamWriter errorReportWriter, UserReferenceDto currentUser, UI currentUI) throws IOException {
		super(inputFile, errorReportWriter, currentUser, currentUI);

	}

	@Override
	protected void importDataFromCsvLine(String[] nextLine, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (nextLine.length > headersLine.length) {
			hasImportError = true;
			writeImportError(nextLine, I18nProperties.getValidationError(Validations.importLineTooLong));
			readNextLineFromCsv(headersLine, headers);
		}

		final PersonDto newPersonTmp = PersonDto.build();
		final CaseDataDto newCaseTmp = CaseDataDto.build(newPersonTmp.toReference(), null);
		newCaseTmp.setReportingUser(currentUser);

		boolean caseHasImportError = insertRowIntoData(nextLine, headers, false, new BiFunction<String, String[], Exception>() {
			@Override
			public Exception apply(String entry, String[] entryHeaderPath) {
				try {
					insertColumnEntryIntoData(newCaseTmp, newPersonTmp, entry, entryHeaderPath);
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
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				caseHasImportError = true;
				writeImportError(nextLine, e.getMessage());
			}
		}

		if (!caseHasImportError) {
			try {
				CaseImportConsumer consumer = new CaseImportConsumer();
				ImportSimilarityResultOption resultOption = null;
				
				CaseImportLock LOCK = new CaseImportLock();
				synchronized (LOCK) {
					CaseCriteria criteria = new CaseCriteria()
							.firstName(newPerson.getFirstName())
							.lastName(newPerson.getLastName())
							.disease(newCase.getDisease())
							.region(newCase.getRegion())
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
								caseHasImportError = insertRowIntoData(nextLine, headers, true, new BiFunction<String, String[], Exception>() {
									@Override
									public Exception apply(String entry, String[] entryHeaderPath) {
										try {
											insertColumnEntryIntoData(matchingCaseTmp, matchingCasePersonTmp, entry, entryHeaderPath);
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
					readNextLineFromCsv(headersLine, headers);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.SKIP) {
					// Reset the import result
					consumer.result = null;
					importedCallback.accept(ImportResult.SKIPPED);
					readNextLineFromCsv(headersLine, headers);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.PICK) {
					consumer.result = null;
					importedCallback.accept(ImportResult.DUPLICATE);
					readNextLineFromCsv(headersLine, headers);
				} else if (resultOption != null && resultOption == ImportSimilarityResultOption.CANCEL) {
					cancelAfterCurrent = true;
					return;
				} else {
					PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(newPerson);
					newCase.setPerson(savedPerson.toReference());
					FacadeProvider.getCaseFacade().saveCase(newCase);
					// Reset the import result
					consumer.result = null;
					importedCallback.accept(ImportResult.SUCCESS);
					readNextLineFromCsv(headersLine, headers);
				}
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(nextLine, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
				readNextLineFromCsv(headersLine, headers);
			}
		} else {
			importedCallback.accept(ImportResult.ERROR);
			readNextLineFromCsv(headersLine, headers);
		}	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

					if (propertyType.isEnum()) {
						pd.getWriteMethod().invoke(currentElement, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
					} else if (propertyType.isAssignableFrom(Date.class)) {
						pd.getWriteMethod().invoke(currentElement, DateHelper.parseDateWithException(entry));
					} else if (propertyType.isAssignableFrom(Integer.class)) {
						pd.getWriteMethod().invoke(currentElement, Integer.parseInt(entry));
					} else if (propertyType.isAssignableFrom(Double.class)) {
						pd.getWriteMethod().invoke(currentElement, Double.parseDouble(entry));
					} else if (propertyType.isAssignableFrom(Float.class)) {
						pd.getWriteMethod().invoke(currentElement, Float.parseFloat(entry));
					} else if (propertyType.isAssignableFrom(Boolean.class)) {
						pd.getWriteMethod().invoke(currentElement, Boolean.parseBoolean(entry));
					} else if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
						List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (region.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade().getByName(entry, caze.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = FacadeProvider.getCommunityFacade().getByName(entry, caze.getDistrict());
						if (community.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade().getByName(entry, caze.getDistrict(), caze.getCommunity());
						if (facility.isEmpty()) {
							if (caze.getCommunity() != null) {
								throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrCommunity, entry, buildHeaderPathString(entryHeaderPath)));
							} else {
								throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildHeaderPathString(entryHeaderPath)));
							}
						} else if (facility.size() > 1 && caze.getCommunity() == null) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (facility.size() > 1 && caze.getCommunity() != null) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
						}
					} else if (propertyType.isAssignableFrom(UserReferenceDto.class)) {
						UserDto user = FacadeProvider.getUserFacade().getByUserName(entry);
						if (user != null) {
							pd.getWriteMethod().invoke(currentElement, user.toReference());
						} else {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
						}
					} else if (propertyType.isAssignableFrom(String.class)) {
						pd.getWriteMethod().invoke(currentElement, entry);
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildHeaderPathString(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildHeaderPathString(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importInvalidDate, buildHeaderPathString(entryHeaderPath)));
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
