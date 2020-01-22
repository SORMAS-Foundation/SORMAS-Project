package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class InfrastructureImporter extends DataImporter {

	private InfrastructureType type;

	public InfrastructureImporter(File inputFile, UserReferenceDto currentUser, InfrastructureType type) {
		super(inputFile, false, currentUser);
		this.type = type;
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(String[] values, String[] entityClasses, String[] entityProperties,
			String[][] entityPropertyPaths, boolean firstLine)
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
		default:
			throw new IllegalArgumentException(type.toString());
		}

		boolean iHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false,
				new Function<ImportCellData, Exception>() {
					@Override
					public Exception apply(ImportCellData importColumnInformation) {
						try {
							insertColumnEntryIntoData(newEntityDto, importColumnInformation.getValue(),
									importColumnInformation.getEntityPropertyPath());
						} catch (ImportErrorException | InvalidColumnException e) {
							return e;
						}

						return null;
					}
				});

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

	private void insertColumnEntryIntoData(EntityDto newEntityDto, String value, String[] entityPropertyPath)
			throws InvalidColumnException, ImportErrorException {
		Object currentElement = newEntityDto;
		for (int i = 0; i < entityPropertyPath.length; i++) {
			String headerPathElementName = entityPropertyPath[i];

			try {
				if (i != entityPropertyPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass())
							.getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, value, entityPropertyPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district;
						switch (type) {
						case COMMUNITY:
							district = FacadeProvider.getDistrictFacade().getByName(value,
									((CommunityDto) newEntityDto).getRegion());
							break;
						case FACILITY:
							district = FacadeProvider.getDistrictFacade().getByName(value,
									((FacilityDto) newEntityDto).getRegion());
							break;
						case POINT_OF_ENTRY:
							district = FacadeProvider.getDistrictFacade().getByName(value,
									((PointOfEntryDto) newEntityDto).getRegion());
							break;
						default:
							throw new UnsupportedOperationException(I18nProperties.getValidationError(
									Validations.importPropertyTypeNotAllowed, propertyType.getName()));
						}
						if (district.isEmpty()) {
							throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion,
											value, buildEntityProperty(entityPropertyPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importDistrictNotUnique, value,
											buildEntityProperty(entityPropertyPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community;
						switch (type) {
						case FACILITY:
							community = FacadeProvider.getCommunityFacade().getByName(value,
									((FacilityDto) newEntityDto).getDistrict());
							break;
						default:
							throw new UnsupportedOperationException(I18nProperties.getValidationError(
									Validations.importPropertyTypeNotAllowed, propertyType.getName()));
						}
						if (community.isEmpty()) {
							throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion,
											value, buildEntityProperty(entityPropertyPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importDistrictNotUnique, value,
											buildEntityProperty(entityPropertyPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else {
						throw new UnsupportedOperationException(I18nProperties
								.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn,
						buildEntityProperty(entityPropertyPath)));
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
