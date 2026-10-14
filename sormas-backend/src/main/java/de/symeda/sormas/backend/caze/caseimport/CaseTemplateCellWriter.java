package de.symeda.sormas.backend.caze.caseimport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportHelper;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;

@Stateless
@LocalBean
public class CaseTemplateCellWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaseTemplateCellWriter.class);

	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;

	public void writeCaseCell(CaseDataDto caze, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Language language = I18nProperties.getUserLanguage();

		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else if (CaseExportDto.BIRTH_DATE.equals(headerPathElementName)) {
					BirthDateDto birthDateDto = PersonHelper.parseBirthdate(entry, language);
					if (birthDateDto != null) {
						person.setBirthdateDD(birthDateDto.getDateOfBirthDD());
						person.setBirthdateMM(birthDateDto.getDateOfBirthMM());
						person.setBirthdateYYYY(birthDateDto.getDateOfBirthYYYY());
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (importFacade.executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath, false)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = districtFacade
							.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), caze, null, null, person, currentElement), false);
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
						PointOfEntryReferenceDto pointOfEntryReference;
						DistrictReferenceDto pointOfEntryDistrict = CaseLogic.getDistrictWithFallback(caze);
						List<PointOfEntryReferenceDto> customPointsOfEntry = pointOfEntryFacade.getByName(entry, pointOfEntryDistrict, false);
						if (customPointsOfEntry.isEmpty()) {
							final String poeName = entry;
							List<PointOfEntryDto> defaultPointOfEntries = pointOfEntryFacade.getByUuids(PointOfEntryDto.CONSTANT_POE_UUIDS);
							Optional<PointOfEntryDto> defaultPointOfEntry = defaultPointOfEntries.stream()
								.filter(
									defaultPoe -> InfrastructureHelper.buildPointOfEntryString(defaultPoe.getUuid(), defaultPoe.getName())
										.equals(poeName))
								.findFirst();
							if (!defaultPointOfEntry.isPresent()) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
							pointOfEntryReference = defaultPointOfEntry.get().toReference();
						} else if (customPointsOfEntry.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importPointOfEntryNotUniqueInDistrict,
									entry,
									buildEntityProperty(entryHeaderPath)));

						} else {
							pointOfEntryReference = customPointsOfEntry.get(0);
						}

						pd.getWriteMethod().invoke(currentElement, pointOfEntryReference);
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

	public void writeRelatedCell(Object currentElement, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (importFacade.executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath, false)) {
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
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						buildEntityProperty(entryHeaderPath),
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}

	private FacilityType getTypeOfFacility(String propertyName, Object currentElement)
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

	private String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}
}
