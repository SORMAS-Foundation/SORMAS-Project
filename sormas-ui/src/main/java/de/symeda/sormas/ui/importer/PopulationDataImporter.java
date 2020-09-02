package de.symeda.sormas.ui.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import population data.
 */
public class PopulationDataImporter extends DataImporter {

	/**
	 * The pattern that entries in the header row must match in order for the importer to determine how to fill its entries.
	 */
	private static final String HEADER_PATTERN = "[A-Z]+_[A-Z]{3}_\\d+_(\\d+|PLUS)";

	/**
	 * The pattern that entries in the header row representing total counts must match in order for the importer to determine how to fill
	 * its entries.
	 */
	private static final String TOTAL_HEADER_PATTERN = "[A-Z]+_TOTAL";

	private final Date collectionDate;

	public PopulationDataImporter(File inputFile, UserReferenceDto currentUser, Date collectionDate) {
		super(inputFile, false, currentUser);
		this.collectionDate = collectionDate;
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

		// Reference population data that contains the region and district for this line
		RegionReferenceDto region = null;
		DistrictReferenceDto district = null;

		// Retrieve the region and district from the database or throw an error if more or less than one entry have been retrieved
		for (int i = 0; i < entityProperties.length; i++) {
			if (PopulationDataDto.REGION.equalsIgnoreCase(entityProperties[i])) {
				List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getByName(values[i], false);
				if (regions.size() != 1) {
					writeImportError(values, new ImportErrorException(values[i], entityProperties[i]).getMessage());
					return ImportLineResult.ERROR;
				}
				region = regions.get(0);
			}
			if (PopulationDataDto.DISTRICT.equalsIgnoreCase(entityProperties[i])) {
				if (DataHelper.isNullOrEmpty(values[i])) {
					district = null;
				} else {
					List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade().getByName(values[i], region, false);
					if (districts.size() != 1) {
						writeImportError(values, new ImportErrorException(values[i], entityProperties[i]).getMessage());
						return ImportLineResult.ERROR;
					}
					district = districts.get(0);
				}
			}
		}

		// The region and district that will be used to save the population data to the database
		final RegionReferenceDto finalRegion = region;
		final DistrictReferenceDto finalDistrict = district;

		// Retrieve the existing population data for the region and district
		PopulationDataCriteria criteria = new PopulationDataCriteria().region(finalRegion);
		if (district == null) {
			criteria.districtIsNull(true);
		} else {
			criteria.district(finalDistrict);
		}
		List<PopulationDataDto> existingPopulationDataList = FacadeProvider.getPopulationDataFacade().getPopulationData(criteria);
		List<PopulationDataDto> modifiedPopulationDataList = new ArrayList<PopulationDataDto>();

		boolean populationDataHasImportError =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, false, new Function<ImportCellData, Exception>() {

				@Override
				public Exception apply(ImportCellData cellData) {
					try {
						if (PopulationDataDto.REGION.equalsIgnoreCase(cellData.getEntityPropertyPath()[0])
							|| PopulationDataDto.DISTRICT.equalsIgnoreCase(cellData.getEntityPropertyPath()[0])) {
							// Ignore the region and district columns
						} else if (RegionDto.GROWTH_RATE.equalsIgnoreCase(cellData.getEntityPropertyPath()[0])) {
							// Update the growth rate of the region or district
							if (!DataHelper.isNullOrEmpty(cellData.value)) {
								Float growthRate = Float.parseFloat(cellData.value);
								if (finalDistrict != null) {
									DistrictDto districtDto = FacadeProvider.getDistrictFacade().getDistrictByUuid(finalDistrict.getUuid());
									districtDto.setGrowthRate(growthRate);
									FacadeProvider.getDistrictFacade().saveDistrict(districtDto);
								} else {
									RegionDto regionDto = FacadeProvider.getRegionFacade().getRegionByUuid(finalRegion.getUuid());
									regionDto.setGrowthRate(growthRate);
									FacadeProvider.getRegionFacade().saveRegion(regionDto);
								}
							}
						} else {
							// Add the data from the currently processed cell to a new population data object
							PopulationDataDto newPopulationData = PopulationDataDto.build(collectionDate);
							insertCellValueIntoData(newPopulationData, cellData.getValue(), cellData.getEntityPropertyPath());

							Optional<PopulationDataDto> existingPopulationData = existingPopulationDataList.stream()
								.filter(
									populationData -> populationData.getAgeGroup() == newPopulationData.getAgeGroup()
										&& populationData.getSex() == newPopulationData.getSex())
								.findFirst();

							// Check whether this population data set already exists in the database; if yes, override it
							if (existingPopulationData.isPresent()) {
								existingPopulationData.get().setPopulation(newPopulationData.getPopulation());
								existingPopulationData.get().setCollectionDate(collectionDate);
								modifiedPopulationDataList.add(existingPopulationData.get());
							} else {
								newPopulationData.setRegion(finalRegion);
								newPopulationData.setDistrict(finalDistrict);
								modifiedPopulationDataList.add(newPopulationData);
							}
						}
					} catch (ImportErrorException | InvalidColumnException | NumberFormatException e) {
						return e;
					}

					return null;
				}
			});

		// Validate and save the population data object into the database if the import has no errors
		if (!populationDataHasImportError) {
			try {
				FacadeProvider.getPopulationDataFacade().savePopulationData(modifiedPopulationDataList);
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
	 * Inserts the entry of a single cell into the population data object. Checks whether the entity property accords to one of the patterns
	 * defined in this class
	 * and sets the according sex and age group to the population data object.
	 */
	private void insertCellValueIntoData(PopulationDataDto populationData, String value, String[] entityPropertyPaths)
		throws InvalidColumnException, ImportErrorException {
		String entityProperty = buildEntityProperty(entityPropertyPaths);

		if (entityPropertyPaths.length != 1) {
			throw new UnsupportedOperationException(
				I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, buildEntityProperty(entityPropertyPaths)));
		}

		String entityPropertyPath = entityPropertyPaths[0];

		try {
			if (entityPropertyPath.equalsIgnoreCase("TOTAL")) {
				insertPopulationIntoPopulationData(populationData, value);
			} else if (entityPropertyPath.matches(TOTAL_HEADER_PATTERN)) {
				try {
					populationData.setSex(Sex.valueOf(entityPropertyPaths[0].substring(0, entityPropertyPaths[0].indexOf("_"))));
				} catch (IllegalArgumentException e) {
					throw new InvalidColumnException(entityProperty);
				}
				insertPopulationIntoPopulationData(populationData, value);
			} else if (entityPropertyPath.matches(HEADER_PATTERN)) {
				// Sex
				String sexString = entityPropertyPath.substring(0, entityPropertyPaths[0].indexOf("_"));
				if (!sexString.equals("TOTAL")) {
					try {
						populationData.setSex(Sex.valueOf(sexString));
					} catch (IllegalArgumentException e) {
						throw new InvalidColumnException(entityProperty);
					}
				}

				// Age group
				String ageGroupString = entityPropertyPath.substring(entityPropertyPath.indexOf("_") + 1, entityPropertyPaths[0].length());
				try {
					populationData.setAgeGroup(AgeGroup.valueOf(ageGroupString));
				} catch (IllegalArgumentException e) {
					throw new InvalidColumnException(entityProperty);
				}

				insertPopulationIntoPopulationData(populationData, value);
			} else {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, entityPropertyPath));
			}
		} catch (IllegalArgumentException e) {
			throw new ImportErrorException(value, entityProperty);
		} catch (ImportErrorException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error when trying to import population data: " + e.getMessage());
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
		}
	}

	private void insertPopulationIntoPopulationData(PopulationDataDto populationData, String entry) throws ImportErrorException {
		try {
			populationData.setPopulation(Integer.parseInt(entry));
		} catch (NumberFormatException e) {
			throw new ImportErrorException(e.getMessage());
		}
	}
}
