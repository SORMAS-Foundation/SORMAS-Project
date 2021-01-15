package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import regions, districts, communities, facilities and points of entry.
 */
public class InfrastructureImporter extends DataImporter {

	private InfrastructureType type;

	public InfrastructureImporter(File inputFile, UserReferenceDto currentUser, InfrastructureType type) {
		super(inputFile, false, currentUser);
		this.type = type;
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

		EntityDto newEntityDto;

		switch (type) {
		case COMMUNITY:
			newEntityDto = CommunityDto.build();
			break;
		case DISTRICT:
			newEntityDto = DistrictDto.build();
			break;
		case FACILITY:
			newEntityDto = FacilityDto.build();
			break;
		case POINT_OF_ENTRY:
			newEntityDto = PointOfEntryDto.build();
			break;
		case REGION:
			newEntityDto = RegionDto.build();
			break;
		case AREA:
			newEntityDto = AreaDto.build();
			break;
		default:
			throw new IllegalArgumentException(type.toString());
		}

		boolean iHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			try {
				// If the cell entry is not empty, try to insert it into the current infrastructure object
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newEntityDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		// Save the infrastructure object into the database if the import has no errors or throw an error
		// if there is already an infrastructure object with this name in the database
		if (!iHasImportError) {
			try {
				switch (type) {
				case COMMUNITY:
					FacadeProvider.getCommunityFacade().saveCommunity((CommunityDto) newEntityDto);
					break;
				case DISTRICT:
					FacadeProvider.getDistrictFacade().saveDistrict((DistrictDto) newEntityDto);
					break;
				case FACILITY:
					FacadeProvider.getFacilityFacade().saveFacility((FacilityDto) newEntityDto);
					break;
				case POINT_OF_ENTRY:
					FacadeProvider.getPointOfEntryFacade().save((PointOfEntryDto) newEntityDto);
					break;
				case REGION:
					FacadeProvider.getRegionFacade().saveRegion((RegionDto) newEntityDto);
					break;
				case AREA:
					FacadeProvider.getAreaFacade().saveArea((AreaDto) newEntityDto);
					break;
				default:
					throw new IllegalArgumentException(type.toString());
				}
				return ImportLineResult.SUCCESS;
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	/**
	 * Inserts the entry of a single cell into the infrastructure object.
	 */
	private void insertColumnEntryIntoData(EntityDto newEntityDto, String value, String[] entityPropertyPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = newEntityDto;
		for (int i = 0; i < entityPropertyPath.length; i++) {
			String headerPathElementName = entityPropertyPath[i];

			try {
				if (i != entityPropertyPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the infrastructure object's fields; additionally, throw an error if infrastructure data that
					// is referenced in the imported object does not exist in the database
					if (executeDefaultInvokings(pd, currentElement, value, entityPropertyPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district;
						switch (type) {
						case COMMUNITY:
							district = FacadeProvider.getDistrictFacade().getByName(value, ((CommunityDto) newEntityDto).getRegion(), false);
							break;
						case FACILITY:
							district = FacadeProvider.getDistrictFacade().getByName(value, ((FacilityDto) newEntityDto).getRegion(), false);
							break;
						case POINT_OF_ENTRY:
							district = FacadeProvider.getDistrictFacade().getByName(value, ((PointOfEntryDto) newEntityDto).getRegion(), false);
							break;
						default:
							throw new UnsupportedOperationException(
								I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
						}
						if (district.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrRegion,
									value,
									buildEntityProperty(entityPropertyPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importDistrictNotUnique, value, buildEntityProperty(entityPropertyPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community;
						switch (type) {
						case FACILITY:
							community = FacadeProvider.getCommunityFacade().getByName(value, ((FacilityDto) newEntityDto).getDistrict(), false);
							break;
						default:
							throw new UnsupportedOperationException(
								I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
						}
						if (community.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrRegion,
									value,
									buildEntityProperty(entityPropertyPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importDistrictNotUnique, value, buildEntityProperty(entityPropertyPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entityPropertyPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(value, buildEntityProperty(entityPropertyPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import infrastructure data: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
		}
	}
}
