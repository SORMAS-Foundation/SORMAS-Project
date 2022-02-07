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
package de.symeda.sormas.ui.events.eventparticipantimporter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.ImportRelatedObjectsMapper;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.ImporterPersonHelper;
import de.symeda.sormas.ui.importer.PersonImportSimilarityResult;

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
	private final EventDto event;
	private UI currentUI;

	public EventParticipantImporter(File inputFile, UserDto currentUser, EventDto event, ValueSeparator csvSeparator) throws IOException {
		super(inputFile, true, currentUser, csvSeparator);
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
		throws IOException, InterruptedException {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		// regenerate the UUID to prevent overwrite in case of export and import of the same entities
		int uuidIndex = ArrayUtils.indexOf(entityProperties, EventParticipantDto.UUID);
		if (uuidIndex >= 0) {
			values[uuidIndex] = DataHelper.createUuid();
		}
		int personUuidIndex = ArrayUtils.indexOf(entityProperties, String.join(".", EventParticipantDto.PERSON, PersonDto.UUID));
		if (personUuidIndex >= 0) {
			values[personUuidIndex] = DataHelper.createUuid();
		}

		final PersonDto newPersonTemp = PersonDto.buildImportEntity();
		final EventParticipantDto newEventParticipantTemp = EventParticipantDto.build(event.toReference(), currentUser.toReference());
		newEventParticipantTemp.setPerson(newPersonTemp);
		final List<VaccinationDto> vaccinations = new ArrayList<>();

		ImportRelatedObjectsMapper.Builder relatedObjectsMapperBuilder = new ImportRelatedObjectsMapper.Builder();

		if (FacadeProvider.getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED) && event.getDisease() != null) {
			relatedObjectsMapperBuilder.addMapper(
				VaccinationDto.class,
				vaccinations,
				() -> VaccinationDto.build(currentUser.toReference()),
				this::insertColumnEntryIntoRelatedObject);
		}

		ImportRelatedObjectsMapper relatedMapper = relatedObjectsMapperBuilder.build();

		boolean eventParticipantHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, true, importColumnInformation -> {
			try {
				if (!relatedMapper.map(importColumnInformation)) {
					// If the cell entry is not empty, try to insert it into the current contact or person object
					if (!StringUtils.isEmpty(importColumnInformation.getValue())) {

						insertColumnEntryIntoData(
							newEventParticipantTemp,
							newPersonTemp,
							importColumnInformation.getValue(),
							importColumnInformation.getEntityPropertyPath());

					}
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
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

		// Sanitize non-HOME address
		PersonHelper.sanitizeNonHomeAddress(newPersonTemp);

		// If the eventparticipant still does not have any import errors, search for persons similar to the eventparticipant person in the database and,
		// if there are any, display a window to resolve the conflict to the user
		if (!eventParticipantHasImportError) {
			EventParticipantDto newEventParticipant = newEventParticipantTemp;
			try {
				EventParticipantImportConsumer consumer = new EventParticipantImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				EventParticipantImportLock personSelectLock = new EventParticipantImportLock();

				String selectedPersonUuid = null;

				// We need to pause the current thread to prevent the import from continuing until the user has acted
				synchronized (personSelectLock) {
					// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
					// to allow the importer to resume
					handlePersonSimilarity(
						newPersonTemp,
						result -> consumer.onImportResult(result, personSelectLock),
						PersonImportSimilarityResult::new,
						Strings.infoSelectOrCreatePersonForImport,
						currentUI);

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
						selectedPersonUuid = consumer.result.getMatchingPerson().getUuid();
					}
				}

				// Determine the import result and, if there was no duplicate, the user did not skip over the eventparticipant
				// or an existing person was picked, save the eventparticipant and person to the database
				if (ImportSimilarityResultOption.SKIP.equals(resultOption)) {
					return ImportLineResult.SKIPPED;
				} else {

					boolean skipPersonValidation = ImportSimilarityResultOption.PICK.equals(resultOption);


					PersonDto importPerson = newPersonTemp;

					if (selectedPersonUuid != null) {
						importPerson = FacadeProvider.getPersonFacade().getPersonByUuid(selectedPersonUuid);
					}

					// get first eventparticipant for event and person
					EventParticipantCriteria eventParticipantCriteria =
							new EventParticipantCriteria().withPerson(importPerson.toReference()).withEvent(event.toReference());
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



					PersonDto savedPersonDto;
					if (selectedPersonUuid != null) {
						// Workaround: Reset the change date to avoid OutdatedEntityExceptions
						importPerson.setChangeDate(new Date());
						FacadeProvider.getPersonFacade().mergePerson(importPerson, newPersonTemp, true, skipPersonValidation);
						savedPersonDto = importPerson;
					} else {
						savedPersonDto = FacadeProvider.getPersonFacade().savePerson(newPersonTemp, skipPersonValidation);
					}

					newEventParticipant.setPerson(savedPersonDto);
					newEventParticipant.setChangeDate(new Date());
					eventParticipantFacade.saveEventParticipant(newEventParticipant);

					for (VaccinationDto vaccination : vaccinations) {
						FacadeProvider.getVaccinationFacade()
							.createWithImmunization(
								vaccination,
								newEventParticipant.getRegion(),
								newEventParticipant.getDistrict(),
								newEventParticipant.getPerson().toReference(),
								event.getDisease());
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
						person.setBirthdateDD(birthDateDto.getDateOfBirthDD());
						person.setBirthdateMM(birthDateDto.getDateOfBirthMM());
						person.setBirthdateYYYY(birthDateDto.getDateOfBirthYYYY());
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the eventparticipant or person fields
					if (executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade()
							.getByName(entry, ImporterPersonHelper.getRegionBasedOnDistrict(pd.getName(), null, person, currentElement), false);
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

		ImportLineResultDto<EventParticipantDto> constraintErrors = validateConstraints(eventParticipant);
		if (constraintErrors.isError()) {
			throw new ImportErrorException(constraintErrors.getMessage());
		}
	}

	private class EventParticipantImportConsumer {

		protected PersonImportSimilarityResult result;

		private void onImportResult(PersonImportSimilarityResult result, EventParticipantImportLock LOCK) {
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
