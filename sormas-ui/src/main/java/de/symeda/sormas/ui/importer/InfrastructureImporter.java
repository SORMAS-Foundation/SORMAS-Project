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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import regions, districts, communities, facilities and points of entry.
 */
public class InfrastructureImporter extends DataImporter {

	private final InfrastructureType type;
	protected final boolean allowOverwrite;

	public InfrastructureImporter(File inputFile, UserDto currentUser, InfrastructureType type, ValueSeparator csvSeparator) throws IOException {
		this(inputFile, currentUser, type, false, csvSeparator);
	}

	public InfrastructureImporter(File inputFile, UserDto currentUser, InfrastructureType type, boolean allowOverwrite, ValueSeparator csvSeparator)
		throws IOException {
		super(inputFile, false, currentUser, csvSeparator);
		this.type = type; 
		this.allowOverwrite = allowOverwrite;
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		EntityDto newEntityDto;

		switch (type) {
		case COMMUNITY:
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$55555555555555555555555555555555555555555555%%%%%%%%%%%");		
			newEntityDto = CommunityDto.build();
			break;
		case DISTRICT:
			System.out.println("$$$$$$$$$$$$$$==================55555%%%%%%%%%%%");		
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
		case SUBCONTINENT:
			newEntityDto = SubcontinentDto.build();
			break;
		case CONTINENT:
			newEntityDto = ContinentDto.build();
			break;
		default:
			throw new IllegalArgumentException(type.toString());
		}

		boolean iHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			
					
			try {
				// If the cell entry is not empty, try to insert it into the current infrastructure object
				if (!StringUtils.isEmpty(cellData.getValue())) {
					System.out.println(cellData.getEntityPropertyPath()+ " trying to send data to db importer "+cellData.getValue()+"  == "+newEntityDto);
					insertColumnEntryIntoData(newEntityDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		// Save the infrastructure object into the database if the import has no errors or throw an error
		// if there is already an infrastructure object with this name in the database
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$=======555555555555555555555555%%%%%%%%%%% iHasImportError= "+iHasImportError);
		if (!iHasImportError) {
			try {
				switch (type) {
				case COMMUNITY:
					FacadeProvider.getCommunityFacade().save((CommunityDto) newEntityDto, allowOverwrite);
					break;
				case DISTRICT:
					FacadeProvider.getDistrictFacade().save((DistrictDto) newEntityDto, allowOverwrite);
					break;
				case FACILITY:
					FacadeProvider.getFacilityFacade().save((FacilityDto) newEntityDto, allowOverwrite);
					break;
				case POINT_OF_ENTRY:
					FacadeProvider.getPointOfEntryFacade().save((PointOfEntryDto) newEntityDto, allowOverwrite);
					break;
				case REGION:
					FacadeProvider.getRegionFacade().save((RegionDto) newEntityDto, allowOverwrite);
					break;
				case AREA:
					FacadeProvider.getAreaFacade().save((AreaDto) newEntityDto, allowOverwrite);
					break;
				case SUBCONTINENT:
					FacadeProvider.getSubcontinentFacade().save((SubcontinentDto) newEntityDto, allowOverwrite);
					break;
				case CONTINENT:
					FacadeProvider.getContinentFacade().save((ContinentDto) newEntityDto, allowOverwrite);
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
					System.out.println("come000000000000");
				} else {
					System.out.println("come111111111111111");
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the infrastructure object's fields; additionally, throw an error if infrastructure data that
					// is referenced in the imported object does not exist in the database
					if (!executeDefaultInvoke(pd, currentElement, value, entityPropertyPath)) { 
						if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
							System.out.println("c!!!!!!!!!!!!!!!!!!!!!ome here to fix the for district..........................................");
							
							
							List<DistrictReferenceDto> district;
							switch (type) {
							case COMMUNITY:
								System.out.println("c!!!!!!!!!");
								district = FacadeProvider.getDistrictFacade().getByExternalID(Long.parseLong(value), ((CommunityDto) newEntityDto).getRegion(), false);
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
								System.out.println("SCUCESSSSSSSSSS DISTRTICT RETRIEVED!!! = "+district.get(0).getCaption());
								pd.getWriteMethod().invoke(currentElement, district.get(0));
							}
						} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
							System.out.println("c!!!!!!!!!!!!!!!!!!!!!ome here to fix the uuid for comm..........................................");
							
							List<CommunityReferenceDto> community;
							if (type == InfrastructureType.FACILITY) {
								community = FacadeProvider.getCommunityFacade().getByName(value, ((FacilityDto) newEntityDto).getDistrict(), false);
							} else {
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

		ImportLineResultDto<EntityDto> constraintErrors = validateConstraints(newEntityDto);
		if (constraintErrors.isError()) {
			throw new ImportErrorException(constraintErrors.getMessage());
		}
	}
}
