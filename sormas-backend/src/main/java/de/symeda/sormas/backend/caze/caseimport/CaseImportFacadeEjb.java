/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze.caseimport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.caseimport.CaseImportEntities;
import de.symeda.sormas.api.caze.caseimport.CaseImportFacade;
import de.symeda.sormas.api.caze.caseimport.ImportLineResultDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.AreaFacadeEjb.AreaFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "CaseImportFacade")
public class CaseImportFacadeEjb implements CaseImportFacade {

	private final Logger LOGGER = LoggerFactory.getLogger(CaseImportFacadeEjb.class);

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);

	@EJB
	private UserService userService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private AreaFacadeEjbLocal areaFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private EnumService enumService;

	@Override
	@Transactional
	public ImportLineResultDto<CaseImportEntities> importCaseData(
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

		final CaseImportEntities entities = new CaseImportEntities(userService.getCurrentUser().toReference());
		ImportLineResultDto<CaseImportEntities> importResult =
			buildEntities(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, entities);
		if (importResult.isError()) {
			return importResult;
		}

		ImportLineResultDto<CaseImportEntities> validationResult = validateEntities(entities);
		if (validationResult.isError()) {
			return validationResult;
		}

		PersonDto person = entities.getPerson();

		if (isPersonSimilarToExisting(person)) {
			return ImportLineResultDto.duplicateResult(entities);
		}

		ImportLineResultDto<CaseImportEntities> result = saveImportedEntities(entities);

		return result;
	}

	@Override
	public ImportLineResultDto<CaseImportEntities> updateCaseWithImportData(
		String personUuid,
		String caseUuid,
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths)
		throws InvalidColumnException {

		final PersonDto person;
		if (personUuid != null) {
			person = personFacade.getPersonByUuid(personUuid);
		} else {
			person = PersonDto.build();
		}

		final CaseDataDto caze;
		if (caseUuid != null) {
			caze = caseFacade.getCaseDataByUuid(caseUuid);
		} else {
			caze = CaseImportEntities.createCase(person, userService.getCurrentUser().toReference());
		}

		CaseImportEntities entities = new CaseImportEntities(person, caze);
		ImportLineResultDto<CaseImportEntities> importResult = buildEntities(values, entityClasses, entityPropertyPaths, true, entities);

		if (importResult.isError()) {
			return importResult;
		}

		return saveImportedEntities(entities);
	}

	@Override
	public ImportLineResultDto<CaseImportEntities> saveImportedEntities(CaseImportEntities entities) {

		CaseDataDto caze = entities.getCaze();
		PersonDto person = entities.getPerson();
		List<SampleDto> samples = entities.getSamples();
		List<PathogenTestDto> pathogenTests = entities.getPathogenTests();

		try {
			// Necessary to make sure that the follow-up information is retained
			if (featureConfigurationFacade.isFeatureEnabled(FeatureType.CASE_FOLLOWUP) && caze.getFollowUpStatus() == null) {
				caze.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
			}

			if (caze.getEpidNumber() != null && caseFacade.doesEpidNumberExist(caze.getEpidNumber(), caze.getUuid(), caze.getDisease())) {
				return ImportLineResultDto.errorResult(I18nProperties.getString(Strings.messageEpidNumberWarning));
			}

			PersonDto savedPerson = personFacade.savePerson(person);
			caze.setPerson(savedPerson.toReference());
			// Workaround: Reset the change date to avoid OutdatedEntityExceptions
			// Should be changed when doing #2265
			caze.setChangeDate(new Date());
			caseFacade.saveCase(caze);
			for (SampleDto sample : samples) {
				sampleFacade.saveSample(sample);
			}
			for (PathogenTestDto pathogenTest : pathogenTests) {
				pathogenTestFacade.savePathogenTest(pathogenTest);
			}

			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	private ImportLineResultDto<CaseImportEntities> validateEntities(CaseImportEntities entities) {
		try {
			personFacade.validate(entities.getPerson());
			caseFacade.validate(entities.getCaze());
			for (SampleDto sample : entities.getSamples()) {
				sampleFacade.validate(sample);
			}
			for (PathogenTestDto pathogenTest : entities.getPathogenTests()) {
				pathogenTestFacade.validate(pathogenTest);
			}
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}

		return ImportLineResultDto.successResult();
	}

	private ImportLineResultDto<CaseImportEntities> buildEntities(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		CaseImportEntities entities) {

		final UserReferenceDto currentUserRef = userService.getCurrentUser().toReference();

		final List<SampleDto> samples = entities.getSamples();
		final List<PathogenTestDto> pathogenTests = entities.getPathogenTests();

		final MutableBoolean currentSampleHasEntries = new MutableBoolean(false);
		final MutableBoolean currentPathogenTestHasEntries = new MutableBoolean(false);
		final Mutable<String> firstSampleColumnName = new MutableObject<>(null);
		final Mutable<String> firstPathogenTestColumnName = new MutableObject<>(null);

		final ImportLineResultDto<CaseImportEntities> result =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, (cellData) -> {
				try {
					// If the first column of a new sample or pathogen test has been reached, remove the last sample and
					// pathogen test if they don't have any entries
					if (String.join(".", cellData.getEntityPropertyPath()).equals(firstSampleColumnName.getValue())
						|| String.join(".", cellData.getEntityPropertyPath()).equals(firstPathogenTestColumnName.getValue())) {
						if (samples.size() > 0 && currentSampleHasEntries.isFalse()) {
							samples.remove(samples.size() - 1);
							currentSampleHasEntries.setTrue();
						}

						if (pathogenTests.size() > 0 && currentPathogenTestHasEntries.isFalse()) {
							pathogenTests.remove(pathogenTests.size() - 1);
							currentPathogenTestHasEntries.setTrue();
						}
					}

					CaseDataDto caze = entities.getCaze();
					if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(SampleDto.class))) {
						// If the current column belongs to a sample, set firstSampleColumnName if it's empty, add a new sample
						// to the list if the first column of a new sample has been reached and insert the entry of the cell into the sample
						if (firstSampleColumnName.getValue() == null) {
							firstSampleColumnName.setValue(String.join(".", cellData.getEntityPropertyPath()));
						}
						if (String.join(".", cellData.getEntityPropertyPath()).equals(firstSampleColumnName.getValue())) {
							currentSampleHasEntries.setFalse();
							samples.add(SampleDto.build(currentUserRef, new CaseReferenceDto(caze.getUuid())));
						}
						if (!StringUtils.isEmpty(cellData.getValue())) {
							currentSampleHasEntries.setTrue();
							insertColumnEntryIntoSampleData(
								samples.get(samples.size() - 1),
								null,
								cellData.getValue(),
								cellData.getEntityPropertyPath());
						}

					} else if (DataHelper.equal(cellData.getEntityClass(), DataHelper.getHumanClassName(PathogenTestDto.class))) {
						// If the current column belongs to a pathogen test, set firstPathogenTestColumnName if it's empty, add a new test
						// to the list if the first column of a new test has been reached and insert the entry of the cell into the test
						if (firstPathogenTestColumnName.getValue() == null) {
							firstPathogenTestColumnName.setValue(String.join(".", cellData.getEntityPropertyPath()));
						}
						if (!samples.isEmpty()) {
							SampleDto referenceSample = samples.get(samples.size() - 1);
							if (String.join(".", cellData.getEntityPropertyPath()).equals(firstPathogenTestColumnName.getValue())) {
								currentPathogenTestHasEntries.setFalse();
								pathogenTests.add(PathogenTestDto.build(new SampleReferenceDto(referenceSample.getUuid()), currentUserRef));
							}
							if (!StringUtils.isEmpty(cellData.getValue())) {
								currentPathogenTestHasEntries.setTrue();
								insertColumnEntryIntoSampleData(
									null,
									pathogenTests.get(pathogenTests.size() - 1),
									cellData.getValue(),
									cellData.getEntityPropertyPath());
							}
						}
					} else if (!StringUtils.isEmpty(cellData.getValue())) {
						// If the cell entry is not empty, try to insert it into the current case or its person
						insertColumnEntryIntoData(caze, entities.getPerson(), cellData.getValue(), cellData.getEntityPropertyPath());
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			});

		// Remove the last sample and pathogen test if empty
		if (samples.size() > 0 && currentSampleHasEntries.isFalse()) {
			samples.remove(samples.size() - 1);
		}
		if (pathogenTests.size() > 0 && currentPathogenTestHasEntries.isFalse()) {
			pathogenTests.remove(pathogenTests.size() - 1);
		}

		return result;
	}

	protected ImportLineResultDto<CaseImportEntities> insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback) {

		String importError = null;
		List<String> invalidColumns = new ArrayList<>();

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
						invalidColumns.add(((InvalidColumnException) exception).getColumnName());
					}
				}
			}
		}

		if (invalidColumns.size() > 0) {
			LOGGER.warn("Unhandled columns [{}]", String.join(", ", invalidColumns));
		}

		return importError != null ? ImportLineResultDto.errorResult(importError) : ImportLineResultDto.successResult();
	}

	/**
	 * Inserts the entry of a single cell into the case or its person.
	 */
	private void insertColumnEntryIntoData(CaseDataDto caze, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			Language language = userService.getCurrentUser().getLanguage();
			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Set the current element to the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else if (CaseExportDto.BIRTH_DATE.equals(headerPathElementName)) {
					BirthDateDto birthDateDto = PersonHelper.parseBirthdate(entry, language);
					if (birthDateDto != null) {
						person.setBirthdateDD(birthDateDto.getBirthdateDD());
						person.setBirthdateMM(birthDateDto.getBirthdateMM());
						person.setBirthdateYYYY(birthDateDto.getBirthdateYYYY());
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the case or person fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = districtFacade
							.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), caze, null, person, currentElement), false);
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
						List<CommunityReferenceDto> community = communityFacade
							.getByName(entry, ImportHelper.getDistrictBasedOnCommunity(pd.getName(), caze, person, currentElement), false);
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
							ImportHelper.getDistrictAndCommunityBasedOnFacility(pd.getName(), caze, person, currentElement);

						if (I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_FACILITY).equals(entry)) {
							entry = FacilityDto.OTHER_FACILITY;
						}

						if (I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.NO_FACILITY).equals(entry)) {
							entry = FacilityDto.NO_FACILITY;
						}

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
					} else if (propertyType.isAssignableFrom(PointOfEntryReferenceDto.class)) {
						List<PointOfEntryReferenceDto> pointOfEntry = pointOfEntryFacade.getByName(entry, caze.getDistrict(), false);
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
			} catch (IllegalArgumentException | EnumService.InvalidEnumCaptionException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						buildEntityProperty(entryHeaderPath),
						DateHelper.getAllowedDateFormats(language.getDateFormat())));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when trying to import a case: " + e.getMessage(), e);
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
						List<FacilityReferenceDto> lab = facilityFacade.getLaboratoriesByName(entry, false);
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
				LOGGER.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	protected FacilityType getTypeOfFacility(String propertyName, Object currentElement)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		String typeProperty;
		if (CaseDataDto.class.equals(currentElement.getClass()) && CaseDataDto.HEALTH_FACILITY.equals(propertyName)) {
			typeProperty = CaseDataDto.FACILITY_TYPE;
		} else {
			typeProperty = propertyName + "Type";
		}
		PropertyDescriptor pd = new PropertyDescriptor(typeProperty, currentElement.getClass());
		return (FacilityType) pd.getReadMethod().invoke(currentElement);
	}

	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException, EnumService.InvalidEnumCaptionException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {

			Enum enumValue = null;
			Class<Enum> enumType = (Class<Enum>) propertyType;
			try {
				enumValue = Enum.valueOf(enumType, entry.toUpperCase());
			} catch (IllegalArgumentException e) {
				// ignore
			}

			if (enumValue == null) {
				enumValue = enumService.getEnumByCaption(enumType, entry);
			}

			pd.getWriteMethod().invoke(element, enumValue);
			return true;
		}
		if (propertyType.isAssignableFrom(Date.class)) {
			pd.getWriteMethod().invoke(element, DateHelper.parseDateWithException(entry, userService.getCurrentUser().getLanguage().getDateFormat()));
			return true;
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
			pd.getWriteMethod().invoke(element, DataHelper.parseBoolean(entry));
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
	public static class CaseImportFacadeEjbLocal extends CaseImportFacadeEjb {

	}
}
