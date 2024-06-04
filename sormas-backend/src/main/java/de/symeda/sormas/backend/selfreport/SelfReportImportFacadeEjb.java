package de.symeda.sormas.backend.selfreport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportCellData;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportImportFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb;
import de.symeda.sormas.backend.importexport.ImportHelper;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SelfReportImportFacade")
@RightsAllowed(UserRight._SELF_REPORT_IMPORT)
public class SelfReportImportFacadeEjb implements SelfReportImportFacade {

	private static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@EJB
	private SelfReportFacadeEjb.SelfReportFacadeEjbLocal selfReportFacade;
	@EJB
	private ImportFacadeEjb.ImportFacadeEjbLocal importFacade;
	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;

	@Override
	public ImportLineResultDto<SelfReportDto> importSelfReportData(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries) {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			return ImportLineResultDto.errorResult(I18nProperties.getValidationError(Validations.importLineTooLong));
		}

		SelfReportDto selfReport = SelfReportDto.build(null);
		ImportLineResultDto<SelfReportDto> importResult = buildSelfReport(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, selfReport);

		if (importResult.isError()) {
			return importResult;
		}

		ImportLineResultDto<SelfReportDto> validationResult = validateSelfReport(selfReport);
		if (validationResult.isError()) {
			return validationResult;
		}

		return saveSelfReport(selfReport);
	}

	private ImportLineResultDto<SelfReportDto> buildSelfReport(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		SelfReportDto selfReport) {

		return insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, (cellData) -> {
			try {
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(selfReport, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});
	}

	protected ImportLineResultDto<SelfReportDto> insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback) {

		String importError = null;
		List<String> invalidColumns = new ArrayList<>();

		for (int i = 0; i < values.length; i++) {
			String value = StringUtils.trimToNull(values[i]);
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

		if (!invalidColumns.isEmpty()) {
			LOGGER.warn("Unhandled columns [{}]", String.join(", ", invalidColumns));
		}

		return importError != null ? ImportLineResultDto.errorResult(importError) : ImportLineResultDto.successResult();
	}

	private void insertColumnEntryIntoData(SelfReportDto selfReport, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Language language = I18nProperties.getUserLanguage();

		Object currentElement = selfReport;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					if (importFacade.executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath, true)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = districtFacade
							.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), selfReport.getAddress(), currentElement), false);
						if (district.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrRegion,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importDistrictNotUnique,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = communityFacade
							.getByName(entry, ImportHelper.getDistrictBasedOnCommunity(pd.getName(), selfReport.getAddress(), currentElement), false);
						if (community.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrDistrict,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importCommunityNotUnique,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData =
							ImportHelper.getDistrictAndCommunityBasedOnFacility(pd.getName(), selfReport.getAddress(), currentElement);

						List<FacilityReferenceDto> facilities = facilityFacade.getByNameAndType(
							entry,
							infrastructureData.getElement0(),
							infrastructureData.getElement1(),
							selfReport.getAddress().getFacilityType(),
							false);
						if (facilities.isEmpty()) {
							if (infrastructureData.getElement1() != null) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrCommunity,
										entry,
										importFacade.buildEntityProperty(entryHeaderPath)));
							} else {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										importFacade.buildEntityProperty(entryHeaderPath)));
							}
						} else if (facilities.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importFacilityNotUniqueInDistrict,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (facilities.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importFacilityNotUniqueInCommunity,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facilities.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importSelfReportPropertyTypeNotAllowed, propertyType.getName()));
					}

				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(importFacade.buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, importFacade.buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, importFacade.buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						importFacade.buildEntityProperty(entryHeaderPath),
						DateHelper.getAllowedDateFormats(language.getDateFormat())));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when trying to import a self report: " + e.getMessage(), e);
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importSelfReportUnexpectedError));
			}
		}
	}

	private ImportLineResultDto<SelfReportDto> validateSelfReport(SelfReportDto selfReport) {
		ImportLineResultDto<SelfReportDto> validationResult = importFacade.validateConstraints(selfReport);
		if (validationResult.isError()) {
			return validationResult;
		}

		try {
			selfReportFacade.validate(selfReport);
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}

		return ImportLineResultDto.successResult();
	}

	private ImportLineResultDto<SelfReportDto> saveSelfReport(SelfReportDto selfReport) {
		try {
			selfReportFacade.save(selfReport);

			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	@Stateless
	@LocalBean
	public static class SelfReportImportFacadeEjbLocal extends SelfReportImportFacadeEjb {
	}
}
