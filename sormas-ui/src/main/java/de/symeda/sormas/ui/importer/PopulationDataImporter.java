package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class PopulationDataImporter extends DataImporter {

	private static final String HEADER_PATTERN = "[A-Z]+_[A-Z]{3}_\\d+_(\\d+|PLUS)";
	private static final String TOTAL_HEADER_PATTERN = "[A-Z]+_TOTAL";
	private final Date collectionDate;

	public PopulationDataImporter(File inputFile, UserReferenceDto currentUser, UI currentUI, Date collectionDate) throws IOException {
		this(inputFile, null, currentUser, currentUI, collectionDate);
	}

	public PopulationDataImporter(File inputFile, OutputStreamWriter errorReportWriter, UserReferenceDto currentUser, UI currentUI, Date collectionDate) throws IOException {
		super(inputFile, errorReportWriter, currentUser, currentUI);
		this.collectionDate = collectionDate;
	}

	@Override
	protected void importDataFromCsvLine(String[] nextLine, List<String> entityHeaders, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (nextLine.length > headersLine.length) {
			hasImportError = true;
			writeImportError(nextLine, I18nProperties.getValidationError(Validations.importLineTooLong));
			readNextLineFromCsv(entityHeaders, headersLine, headers);
		}

		// Reference population data that contains the region and district for this line
		PopulationDataDto referencePopulationData = PopulationDataDto.build(collectionDate);
		Set<PopulationDataDto> populationDataSet = new HashSet<>();

		boolean populationDataHasImportError = insertRowIntoData(nextLine, entityHeaders, headers, false, new Function<ImportColumnInformation, Exception>() {
			@Override
			public Exception apply(ImportColumnInformation importColumnInformation) {
				try {
					if (importColumnInformation.getEntryHeaderPath()[0].equals("region")
							|| importColumnInformation.getEntryHeaderPath()[0].contentEquals("district")) {
						insertColumnEntryIntoData(referencePopulationData, importColumnInformation.getEntry(), importColumnInformation.getEntryHeaderPath());
					} else {
						PopulationDataDto newPopulationData = PopulationDataDto.build(collectionDate);
						insertColumnEntryIntoData(newPopulationData, importColumnInformation.getEntry(), importColumnInformation.getEntryHeaderPath());

						// Check whether this population data set already exists in the database; if yes, override it
						PopulationDataDto existingPopulationData = FacadeProvider.getPopulationDataFacade().getPopulationData(
								new PopulationDataCriteria().region(referencePopulationData.getRegion()).district(referencePopulationData.getDistrict())
								.ageGroup(newPopulationData.getAgeGroup()).sex(newPopulationData.getSex()));

						if (existingPopulationData != null) {
							existingPopulationData.setPopulation(newPopulationData.getPopulation());
							existingPopulationData.setCollectionDate(collectionDate);
							populationDataSet.add(existingPopulationData);
						} else {
							newPopulationData.setRegion(referencePopulationData.getRegion());
							newPopulationData.setDistrict(referencePopulationData.getDistrict());
							populationDataSet.add(newPopulationData);
						}
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			}
		});

		if (!populationDataHasImportError) {
			try {
				for (PopulationDataDto populationData : populationDataSet) {
					FacadeProvider.getPopulationDataFacade().savePopulationData(populationData);
				}
				importedCallback.accept(ImportResult.SUCCESS);
				readNextLineFromCsv(entityHeaders, headersLine, headers);
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(nextLine, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
				readNextLineFromCsv(entityHeaders, headersLine, headers);
			}
		} else {
			hasImportError = true;
			importedCallback.accept(ImportResult.ERROR);
			readNextLineFromCsv(entityHeaders, headersLine, headers);
		}
	}

	private void insertColumnEntryIntoData(PopulationDataDto populationData, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = populationData;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					if (entryHeaderPath[0].equals("TOTAL")) {
						insertPopulationIntoPopulationData(populationData, entry);						
						continue;
					} else if (entryHeaderPath[0].matches(TOTAL_HEADER_PATTERN)) {
						try {
							populationData.setSex(Sex.valueOf(entryHeaderPath[0].substring(0, entryHeaderPath[0].indexOf("_"))));
						} catch (IllegalArgumentException e) {
							throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
						}
						insertPopulationIntoPopulationData(populationData, entry);
						continue;
					} else if (entryHeaderPath[0].matches(HEADER_PATTERN)) {
						// Sex
						String sexString = entryHeaderPath[0].substring(0, entryHeaderPath[0].indexOf("_"));
						if (!sexString.equals("TOTAL")) {
							try {
								populationData.setSex(Sex.valueOf(sexString));
							} catch (IllegalArgumentException e) {
								throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
							}
						}

						// Age group
						String ageGroupString = entryHeaderPath[0].substring(entryHeaderPath[0].indexOf("_") + 1, entryHeaderPath[0].length());
						try {
							populationData.setAgeGroup(AgeGroup.valueOf(ageGroupString));
						} catch (IllegalArgumentException e) {
							throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
						}

						insertPopulationIntoPopulationData(populationData, entry);
						
						continue;
					}

					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
						List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (region.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade().getByName(entry, populationData.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildHeaderPathString(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildHeaderPathString(entryHeaderPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import population data: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
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
