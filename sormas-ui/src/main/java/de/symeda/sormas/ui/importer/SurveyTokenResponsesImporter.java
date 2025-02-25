package de.symeda.sormas.ui.importer;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.survey.SurveyTokenIndexDto;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Data importer that is used to import survey token responses.
 */
public class SurveyTokenResponsesImporter extends DataImporter {

	private final SurveyDto survey;

	public SurveyTokenResponsesImporter(File inputFile, UserDto currentUser, SurveyDto survey, ValueSeparator csvSeparator)
		throws IOException {
		super(inputFile, false, currentUser, csvSeparator);
		this.survey = survey;
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

		EntityDto newEntityDto = SurveyTokenDto.build(survey);

		SurveyTokenCriteria criteria = new SurveyTokenCriteria();
		criteria.setSurvey(survey.toReference());
		criteria.setToken(values[0]);
		List<SurveyTokenIndexDto> list = FacadeProvider.getSurveyTokenFacade().getIndexList(criteria, 1, 1, null);
		if(list.size() == 1){
			newEntityDto.setUuid(list.get(0).getUuid());
			newEntityDto.setChangeDate(new Date());
		} else {
			writeImportError(values, String.format("UUID %s was not found  for current survey: %s", values[0], survey.getName()));
			return ImportLineResult.SKIPPED;
		}
		boolean hasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			try {
				// If the cell entry is not empty, try to insert it into the current object
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newEntityDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}
			return null;
		});

		if (!hasImportError) {
			ImportLineResultDto<EntityDto> constraintErrors = validateConstraints(newEntityDto);
			if (constraintErrors.isError()) {
				writeImportError(values, constraintErrors.getMessage());
				hasImportError = true;
			}
		}

		// Save the survey token response object into the database if the import has no errors or log a skipped record
		// if there is already an survey token response object with this name in the database
		if (!hasImportError) {
			try {
				FacadeProvider.getSurveyTokenFacade().save((SurveyTokenDto) newEntityDto);
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
	 * Inserts the entry of a single cell into the survey token response object.
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
					// according to the types of the survey token response object's fields; additionally, throw an error if survey token response data that
					// is referenced in the imported object does not exist in the database
					if (!executeDefaultInvoke(pd, currentElement, value, entityPropertyPath)) {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
						}
					}
				} catch (Exception e) {
					logger.error("Unexpected error when trying to import survey token data: " + e.getMessage());
					throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
				}
		}
	}

}
