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
package de.symeda.sormas.ui.events.eventparticipantimporter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.EventParticipantImportSimilarityResult;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.ImporterPersonHelper;
import de.symeda.sormas.ui.person.PersonSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Data importer that is used to import event participant.
 * This importer adds the following logic:
 * 
 * - Check the database for similar person and, if at least one is found, execute the
 * similarityCallback received by the calling class.
 * - The import will wait for the similarityCallback to be resolved before it is continued
 * - An existing event participant (event + person) might be overridden by
 * the data in the CSV file
 * - Save the person and event participant to the database.
 */
public class EventParticipantImporter extends DataImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventParticipantImporter.class);
	private final PersonFacade personFacade;
	private final EventParticipantFacade eventParticipantFacade;
	private final EventReferenceDto event;
	private UI currentUI;

	public EventParticipantImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser, EventReferenceDto event) {
		super(inputFile, hasEntityClassRow, currentUser);
		this.event = event;

		personFacade = FacadeProvider.getPersonFacade();
		eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
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

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		final PersonDto newPersonTemp = PersonDto.build();
		final EventParticipantDto newEventParticipantTemp = EventParticipantDto.build(event, currentUser.toReference());
		newEventParticipantTemp.setPerson(newPersonTemp);

		boolean eventParticipantHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, true, importColumnInformation -> {
			// If the cell entry is not empty, try to insert it into the current contact or person object
			if (!StringUtils.isEmpty(importColumnInformation.getValue())) {
				try {
					insertColumnEntryIntoData(
						newEventParticipantTemp,
						newPersonTemp,
						importColumnInformation.getValue(),
						importColumnInformation.getEntityPropertyPath());
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}
			}
			return null;
		});

		// If the row does not have any import errors, call the backend validation of all associated entities
		if (!eventParticipantHasImportError) {
			try {
				personFacade.validate(newPersonTemp);
				eventParticipantFacade.validate(newEventParticipantTemp);
			} catch (ValidationRuntimeException e) {
				eventParticipantHasImportError = true;
				writeImportError(values, e.getMessage());
			}
		}

		PersonDto newPerson = newPersonTemp;

		// If the eventparticipant still does not have any import errors, search for persons similar to the eventparticipant person in the database and,
		// if there are any, display a window to resolve the conflict to the user
		if (!eventParticipantHasImportError) {
			EventParticipantDto newEventParticipant = newEventParticipantTemp;
			try {
				EventParticipantImportConsumer consumer = new EventParticipantImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				EventParticipantImportLock personSelectLock = new EventParticipantImportLock();
				// We need to pause the current thread to prevent the import from continuing until the user has acted
				synchronized (personSelectLock) {
					// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
					// to allow the importer to resume
					handleSimilarity(newPerson, result -> consumer.onImportResult(result, personSelectLock));

					try {
						if (!personSelectLock.wasNotified) {
							personSelectLock.wait();
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException when trying to perform LOCK.wait() in eventparticipant import: " + e.getMessage());
						throw e;
					}

					if (consumer.result != null) {
						resultOption = consumer.result.getResultOption();
					}

					// If the user picked an existing person, override the eventparticipant person with it
					if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
						newPerson = personFacade.getPersonByUuid(consumer.result.getMatchingPerson().getUuid());

						// get first eventparticipant for event and person
						EventParticipantCriteria eventParticipantCriteria =
							new EventParticipantCriteria().person(newPerson.toReference()).event(event);
						EventParticipantDto pickedEventParticipant = eventParticipantFacade.getFirst(eventParticipantCriteria);

						if (pickedEventParticipant != null) {
							// re-apply import on pickedEventParticipant
							insertRowIntoData(values, entityClasses, entityPropertyPaths, true, importColumnInformation -> {
								// If the cell entry is not empty, try to insert it into the current contact or person object
								if (!StringUtils.isEmpty(importColumnInformation.getValue())) {
									try {
										insertColumnEntryIntoData(
											pickedEventParticipant,
											newPersonTemp,
											importColumnInformation.getValue(),
											importColumnInformation.getEntityPropertyPath());
									} catch (ImportErrorException | InvalidColumnException e) {
										return e;
									}
								}
								return null;
							});
							newEventParticipant = pickedEventParticipant;
						}
					}
				}

				// Determine the import result and, if there was no duplicate, the user did not skip over the eventparticipant 
				// or an existing person was picked, save the eventparticipant and person to the database
				if (eventParticipantHasImportError) {
					return ImportLineResult.ERROR;
				} else if (ImportSimilarityResultOption.SKIP.equals(resultOption)) {
					return ImportLineResult.SKIPPED;
				} else {
					PersonDto savedPerson = personFacade.savePerson(newPerson);
					newEventParticipant.setPerson(savedPerson);

					eventParticipantFacade.saveEventParticipant(newEventParticipant);

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
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate persons.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handleSimilarity(PersonDto newPerson, Consumer<EventParticipantImportSimilarityResult> resultConsumer) {

		currentUI.accessSynchronously(() -> {
			PersonSelectionField personSelect =
				new PersonSelectionField(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForEventParticipantImport));
			personSelect.setWidth(1024, Unit.PIXELS);

			if (personSelect.hasMatches()) {
				final CommitDiscardWrapperComponent<PersonSelectionField> component = new CommitDiscardWrapperComponent<>(personSelect);
				component.addCommitListener(() -> {
					SimilarPersonDto person = personSelect.getValue();
					if (person == null) {
						resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
					} else {
						resultConsumer.accept(new EventParticipantImportSimilarityResult(person, ImportSimilarityResultOption.PICK));
					}
				});

				component.addDiscardListener(
					() -> resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.SKIP)));

				personSelect.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));

				personSelect.selectBestMatch();
			} else {
				resultConsumer.accept(new EventParticipantImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
			}
		});
	}

	/**
	 * Inserts the entry of a single cell into the eventparticipant or its person.
	 */
	private void insertColumnEntryIntoData(EventParticipantDto eventParticipant, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = eventParticipant;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Set the current element to the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else if (EventParticipantExportDto.BIRTH_DATE.equals(headerPathElementName)) {
					BirthDateDto birthDateDto = PersonHelper.parseBirthdate(entry, currentUser.getLanguage());
					if (birthDateDto != null) {
						person.setBirthdateDD(birthDateDto.getBirthdateDD());
						person.setBirthdateMM(birthDateDto.getBirthdateMM());
						person.setBirthdateYYYY(birthDateDto.getBirthdateYYYY());
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the eventparticipant or person fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade()
							.getByName(entry, ImporterPersonHelper.getRegionBasedOnDistrict(pd.getName(), null, null, person, currentElement), false);
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
						List<CommunityReferenceDto> community =
							FacadeProvider.getCommunityFacade().getByName(entry, ImporterPersonHelper.getPersonDistrict(pd.getName(), person), false);
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
						DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData =
							ImporterPersonHelper.getPersonDistrictAndCommunity(pd.getName(), person);
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade()
							.getByNameAndType(
								entry,
								infrastructureData.getElement0(),
								infrastructureData.getElement1(),
								getTypeOfFacility(pd.getName(), currentElement),
								false);
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
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when trying to import an eventparticipant: " + e.getMessage(), e);
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	private class EventParticipantImportConsumer {

		protected EventParticipantImportSimilarityResult result;

		private void onImportResult(EventParticipantImportSimilarityResult result, EventParticipantImportLock LOCK) {
			this.result = result;
			synchronized (LOCK) {
				LOCK.notify();
				LOCK.wasNotified = true;
			}
		}
	}

	private class EventParticipantImportLock {

		protected boolean wasNotified = false;
	}
}
