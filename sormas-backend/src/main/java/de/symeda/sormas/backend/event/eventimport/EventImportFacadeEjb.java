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

package de.symeda.sormas.backend.event.eventimport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.eventimport.EventImportEntities;
import de.symeda.sormas.api.event.eventimport.EventImportFacade;
import de.symeda.sormas.api.event.eventimport.ImportLineResultDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.AreaFacadeEjb.AreaFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "EventImportFacade")
public class EventImportFacadeEjb implements EventImportFacade {

	private final Logger LOGGER = LoggerFactory.getLogger(EventImportFacadeEjb.class);

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);

	@EJB
	private UserService userService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private AreaFacadeEjbLocal areaFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;

	@Override
	@Transactional
	public ImportLineResultDto<EventImportEntities> importEventData(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries)
		throws InvalidColumnException {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			return ImportLineResultDto.errorResult(I18nProperties.getValidationError(Validations.importLineTooLong));
		}

		final EventImportEntities entities = new EventImportEntities(userService.getCurrentUser().toReference());
		ImportLineResultDto<EventImportEntities> importResult =
			buildEntities(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, entities);
		if (importResult.isError()) {
			return importResult;
		}

		ImportLineResultDto<EventImportEntities> validationResult = validateEntities(entities);
		if (validationResult.isError()) {
			return validationResult;
		}

		for (EventParticipantDto eventParticipant : entities.getEventParticipants()) {
			PersonDto person = eventParticipant.getPerson();

			if (isPersonSimilarToExisting(person)) {
				return ImportLineResultDto.duplicateResult(entities);
			}
		}

		return saveImportedEntities(entities);
	}

	@Override
	public ImportLineResultDto<EventImportEntities> saveImportedEntities(EventImportEntities entities) {

		EventDto event = entities.getEvent();
		List<EventParticipantDto> eventParticipants = entities.getEventParticipants();

		try {
			eventFacade.saveEvent(event);

			for (EventParticipantDto eventParticipant : eventParticipants) {
				PersonDto existingPerson = personFacade.getPersonByUuid(eventParticipant.getPerson().getUuid());
				// Check if the person already exists
				// In that case it would means that the person related to the event participant were deduped by the user
				// So no need to persist an already existing person
				if (existingPerson == null) {
					personFacade.savePerson(eventParticipant.getPerson());
				}
				eventParticipantFacade.saveEventParticipant(eventParticipant);
			}

			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	private ImportLineResultDto<EventImportEntities> validateEntities(EventImportEntities entities) {
		try {
			eventFacade.validate(entities.getEvent());
			for (EventParticipantDto eventParticipant : entities.getEventParticipants()) {
				eventParticipantFacade.validate(eventParticipant);
			}
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}

		return ImportLineResultDto.successResult();
	}

	private ImportLineResultDto<EventImportEntities> buildEntities(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		EventImportEntities entities)
		throws InvalidColumnException {

		final UserReferenceDto currentUserRef = userService.getCurrentUser().toReference();

		final List<EventParticipantDto> eventParticipants = entities.getEventParticipants();

		final MutableBoolean currentEventParticipantHasEntries = new MutableBoolean(false);
		final Mutable<String> firstEventParticipantColumnName = new MutableObject<>(null);

		final ImportLineResultDto<EventImportEntities> result =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, (cellData) -> {
				try {
					// If the first column of a new event participant has been reached, remove the last event
					// participant if they don't have any entries
					if (String.join(".", cellData.getEntityPropertyPath()).equals(firstEventParticipantColumnName.getValue())) {
						if (eventParticipants.size() > 0 && currentEventParticipantHasEntries.isFalse()) {
							eventParticipants.remove(eventParticipants.size() - 1);
							currentEventParticipantHasEntries.setTrue();
						}
					}

					EventDto event = entities.getEvent();
					if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(EventParticipantDto.class))
						|| DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PersonDto.class))
						|| (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(LocationDto.class)) && eventParticipants.size() > 0)) {
						// If the current column belongs to an EventParticipant, set firstEventParticipantColumnName if it's empty, add a new participant
						// to the list if the first column of a new participant has been reached and insert the entry of the cell into the participant
						if (firstEventParticipantColumnName.getValue() == null) {
							firstEventParticipantColumnName.setValue(String.join(".", cellData.getEntityPropertyPath()));
						}
						if (String.join(".", cellData.getEntityPropertyPath()).equals(firstEventParticipantColumnName.getValue())) {
							currentEventParticipantHasEntries.setFalse();
							EventParticipantDto eventParticipantDto = EventParticipantDto.build(new EventReferenceDto(event.getUuid()), currentUserRef);
							eventParticipantDto.setPerson(PersonDto.build());
							eventParticipants.add(eventParticipantDto);
						}
						if (!StringUtils.isEmpty(cellData.getValue())) {
							currentEventParticipantHasEntries.setTrue();
							insertColumnEntryIntoEventParticipantData(
								eventParticipants.get(eventParticipants.size() - 1),
								cellData.getValue(),
								cellData.getEntityPropertyPath());
						}

					} else if (!StringUtils.isEmpty(cellData.getValue())) {
						// If the cell entry is not empty, try to insert it into the current event
						insertColumnEntryIntoData(event, cellData.getValue(), cellData.getEntityPropertyPath());
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			});

		// Remove the eventParticipant if empty
		if (eventParticipants.size() > 0 && currentEventParticipantHasEntries.isFalse()) {
			eventParticipants.remove(eventParticipants.size() - 1);
		}

		return result;
	}

	protected ImportLineResultDto<EventImportEntities> insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback)
		throws InvalidColumnException {

		String importError = null;

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			if (ignoreEmptyEntries && (value == null || value.isEmpty())) {
				continue;
			}

			String[] entityPropertyPath = entityPropertyPaths[i];
			// Error description column is ignored
			if (entityPropertyPath[0].equals(ERROR_COLUMN_NAME)) {
				continue;
			}

			if (!(ignoreEmptyEntries && StringUtils.isEmpty(value))) {
				Exception exception =
					insertCallback.apply(new ImportCellData(value, entityClasses != null ? entityClasses[i] : null, entityPropertyPath));
				if (exception != null) {
					if (exception instanceof ImportErrorException) {
						importError = exception.getMessage();
						break;
					} else if (exception instanceof InvalidColumnException) {
						throw (InvalidColumnException) exception;
					}
				}
			}
		}

		return importError != null ? ImportLineResultDto.errorResult(importError) : ImportLineResultDto.successResult();
	}

	/**
	 * Inserts the entry of a single cell into the event or its participant.
	 */
	private void insertColumnEntryIntoData(EventDto event, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = event;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the case or person fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(EventReferenceDto.class)) {
						EventDto referencedDto = eventFacade.getEventByUuid(entry);
						if (referencedDto == null) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExist,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, referencedDto.toReference());
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district =
							districtFacade.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), event, currentElement), false);
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
							communityFacade.getByName(entry, ImportHelper.getDistrictBasedOnCommunity(pd.getName(), event, currentElement), false);
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
							ImportHelper.getDistrictAndCommunityBasedOnFacility(pd.getName(), event, currentElement);
						List<FacilityReferenceDto> facilities = facilityFacade.getByNameAndType(
							entry,
							infrastructureData.getElement0(),
							infrastructureData.getElement1(),
							getTypeOfFacility(pd.getName(), currentElement),
							false);

						if (facilities.isEmpty()) {
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
						} else if (facilities.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (facilities.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facilities.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importEventsPropertyTypeNotAllowed, propertyType.getName()));
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
				LOGGER.error("Unexpected error when trying to import an event: " + e.getMessage(), e);
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	/**
	 * Inserts the entry of a single cell into the event participant
	 */
	private void insertColumnEntryIntoEventParticipantData(EventParticipantDto eventParticipant, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {
		Object currentElement = eventParticipant;
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
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district =
							districtFacade.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), eventParticipant, eventParticipant.getPerson(), currentElement), false);
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
							communityFacade.getByName(entry, ImportHelper.getDistrictBasedOnCommunity(pd.getName(), eventParticipant, eventParticipant.getPerson(), currentElement), false);
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
						DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData = ImportHelper
							.getDistrictAndCommunityBasedOnFacility(pd.getName(), eventParticipant, eventParticipant.getPerson(), currentElement);
						List<FacilityReferenceDto> facilities = facilityFacade.getByNameAndType(
							entry,
							infrastructureData.getElement0(),
							infrastructureData.getElement1(),
							getTypeOfFacility(pd.getName(), currentElement),
							false);

						if (facilities.isEmpty()) {
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
						} else if (facilities.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (facilities.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facilities.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importEventsPropertyTypeNotAllowed, propertyType.getName()));
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
				LOGGER.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	protected FacilityType getTypeOfFacility(String propertyName, Object currentElement)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		String typeProperty;
		if (LocationDto.class.equals(currentElement.getClass()) && LocationDto.FACILITY.equals(propertyName)) {
			typeProperty = LocationDto.FACILITY_TYPE;
		} else {
			typeProperty = propertyName + "Type";
		}
		PropertyDescriptor pd = new PropertyDescriptor(typeProperty, currentElement.getClass());
		return (FacilityType) pd.getReadMethod().invoke(currentElement);
	}

	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {
			pd.getWriteMethod().invoke(element, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
			return true;
		}
		if (propertyType.isAssignableFrom(Date.class)) {
			// If the string is smaller than the length of the expected date format, throw an exception
			if (entry.length() < 10) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod()
					.invoke(element, DateHelper.parseDateWithException(entry, userService.getCurrentUser().getLanguage().getDateFormat()));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(Integer.class)) {
			pd.getWriteMethod().invoke(element, Integer.parseInt(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Double.class)) {
			pd.getWriteMethod().invoke(element, Double.parseDouble(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Float.class)) {
			pd.getWriteMethod().invoke(element, Float.parseFloat(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(boolean.class)) {
			pd.getWriteMethod().invoke(element, Boolean.parseBoolean(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(AreaReferenceDto.class)) {
			List<AreaReferenceDto> areas = areaFacade.getByName(entry, false);
			if (areas.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (areas.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importAreaNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, areas.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			List<RegionReferenceDto> region = regionFacade.getByName(entry, false);
			if (region.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (region.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, region.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(UserReferenceDto.class)) {
			UserDto user = userFacade.getByUserName(entry);
			if (user != null) {
				pd.getWriteMethod().invoke(element, user.toReference());
				return true;
			} else {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		if (propertyType.isAssignableFrom(String.class)) {
			pd.getWriteMethod().invoke(element, entry);
			return true;
		}

		return false;
	}

	private boolean isPersonSimilarToExisting(PersonDto referencePerson) {

		PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().firstName(referencePerson.getFirstName())
			.lastName(referencePerson.getLastName())
			.sex(referencePerson.getSex())
			.birthdateDD(referencePerson.getBirthdateDD())
			.birthdateMM(referencePerson.getBirthdateMM())
			.birthdateYYYY(referencePerson.getBirthdateYYYY())
			.passportNumber(referencePerson.getPassportNumber())
			.nationalHealthId(referencePerson.getNationalHealthId());

		return personFacade.checkMatchingNameInDatabase(userFacade.getCurrentUser().toReference(), criteria);
	}

	protected String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}

	@LocalBean
	@Stateless
	public static class EventImportFacadeEjbLocal extends EventImportFacadeEjb {

	}
}
