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

package de.symeda.sormas.backend.caze.caseimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.caseimport.CaseImportEntities;
import de.symeda.sormas.api.caze.caseimport.CaseImportFacade;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportCellData;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.ImportRelatedObjectsMapper;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb.VaccinationFacadeEjbLocal;

@Stateless(name = "CaseImportFacade")
@RightsAllowed(UserRight._CASE_IMPORT)
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
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private CaseTemplateCellWriter cellWriter;
	@EJB
	private VaccinationFacadeEjbLocal vaccinationFacade;

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

		if (personFacade.isPersonSimilarToExisting(person)) {
			return ImportLineResultDto.duplicateResult(entities);
		}

		ImportLineResultDto<CaseImportEntities> result = saveImportedEntities(entities, false);

		return result;
	}

	@Override
	public ImportLineResultDto<CaseImportEntities> updateCaseWithImportData(
		String personUuid,
		String caseUuid,
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean skipPersonValidation)
		throws InvalidColumnException {

		final PersonDto person;
		if (personUuid != null) {
			person = personFacade.getByUuid(personUuid);
		} else {
			person = PersonDto.buildImportEntity();
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

		return saveImportedEntities(entities, skipPersonValidation);
	}

	@Override
	public ImportLineResultDto<CaseImportEntities> saveImportedEntities(@Valid CaseImportEntities entities, boolean skipPersonValidation) {

		CaseDataDto caze = entities.getCaze();
		PersonDto person = entities.getPerson();
		List<SampleDto> samples = entities.getSamples();
		List<PathogenTestDto> pathogenTests = entities.getPathogenTests();
		List<VaccinationDto> vaccinations = entities.getVaccinations();

		try {
			// Necessary to make sure that the follow-up information is retained
			if (featureConfigurationFacade.isFeatureEnabled(FeatureType.CASE_FOLLOWUP) && caze.getFollowUpStatus() == null) {
				caze.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
			}

			if (caze.getEpidNumber() != null && caseFacade.doesEpidNumberExist(caze.getEpidNumber(), caze.getUuid(), caze.getDisease())) {
				return ImportLineResultDto.errorResult(I18nProperties.getString(Strings.messageEpidNumberWarning));
			}

//			PersonDto savedPerson = personFacade.savePerson(person);
			final PersonDto savedPerson = personFacade.save(person, skipPersonValidation);
			caze.setPerson(savedPerson.toReference());
			// Workaround: Reset the change date to avoid OutdatedEntityExceptions
			// Should be changed when doing #2265
			caze.setChangeDate(new Date());
			caseFacade.save(caze);
			for (SampleDto sample : samples) {
				sampleFacade.saveSample(sample);
			}
			for (PathogenTestDto pathogenTest : pathogenTests) {
				pathogenTestFacade.savePathogenTest(pathogenTest);
			}

			for (VaccinationDto vaccination : vaccinations) {
				vaccinationFacade.createWithImmunization(
					vaccination,
					caze.getResponsibleRegion(),
					caze.getResponsibleDistrict(),
					caze.getPerson(),
					caze.getDisease());
			}

			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	private ImportLineResultDto<CaseImportEntities> validateEntities(CaseImportEntities entities) {

		ImportLineResultDto<CaseImportEntities> validationResult = importFacade.validateConstraints(entities);
		if (validationResult.isError()) {
			return validationResult;
		}

		try {
			personFacade.validate(entities.getPerson());
			caseFacade.validate(entities.getCaze());
			for (SampleDto sample : entities.getSamples()) {
				sampleFacade.validate(sample, false);
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
		final List<VaccinationDto> vaccinations = entities.getVaccinations();

		ImportRelatedObjectsMapper.Builder relatedObjectsMapperBuilder =
			new ImportRelatedObjectsMapper.Builder()
				.addMapper(
					SampleDto.class,
					samples,
					() -> SampleDto.build(currentUserRef, new CaseReferenceDto(entities.getCaze().getUuid())),
					cellWriter::writeRelatedCell)
				.addMapper(PathogenTestDto.class, pathogenTests, () -> {
					if (samples.isEmpty()) {
						return null;
					}

					SampleDto referenceSample = samples.get(samples.size() - 1);
					return PathogenTestDto.build(new SampleReferenceDto(referenceSample.getUuid()), currentUserRef);
				}, cellWriter::writeRelatedCell);

		if (featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			relatedObjectsMapperBuilder
				.addMapper(VaccinationDto.class, vaccinations, () -> VaccinationDto.build(currentUserRef), cellWriter::writeRelatedCell);
		}

		ImportRelatedObjectsMapper relatedMapper = relatedObjectsMapperBuilder.build();

		final ImportLineResultDto<CaseImportEntities> result =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, (cellData) -> {
				try {

					CaseDataDto caze = entities.getCaze();

					if (!relatedMapper.map(cellData)) {
						if (StringUtils.isNotEmpty(cellData.getValue())) {
							// If the cell entry is not empty, try to insert it into the current case or its person
							cellWriter.writeCaseCell(caze, entities.getPerson(), cellData.getValue(), cellData.getEntityPropertyPath());
						}
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			});

		// Sanitize non-HOME address
		PersonHelper.sanitizeNonHomeAddress(entities.getPerson());

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
						StringBuilder additionalInfo = new StringBuilder();
						for (int j = 0; j < entityPropertyPath.length; j++) {
							additionalInfo.append(" ").append(entityPropertyPath[j]);
						}
						importError += additionalInfo;
						importError += "value:" + value;
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

	@LocalBean
	@Stateless
	public static class CaseImportFacadeEjbLocal extends CaseImportFacadeEjb {

	}
}
