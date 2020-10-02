/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.importexport;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Provider;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.CSVCommentLineValidator;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.internal.org.apache.commons.lang3.text.WordUtils;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;

import static de.symeda.sormas.api.caze.CaseDataDto.*;

@Stateless(name = "ImportFacade")
public class ImportFacadeEjb implements ImportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_case_template.csv";
	private static final String CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_case_contact_template.csv";
	private static final String CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_line_listing_template.csv";
	private static final String POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_point_of_entry_template.csv";
	private static final String POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_population_data_template.csv";
	private static final String AREA_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_area_template.csv";
	private static final String REGION_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_region_template.csv";
	private static final String DISTRICT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_district_template.csv";
	private static final String COMMUNITY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_community_template.csv";
	private static final String FACILITY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_facility_template.csv";
	private static final String CONTACT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_contact_template.csv";

	@Override
	public void generateCaseImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, CaseDataDto.class, "", separator);
		appendListOfFields(importColumns, SampleDto.class, "", separator);
		appendListOfFields(importColumns, PathogenTestDto.class, "", separator);

		writeTemplate(Paths.get(getCaseImportTemplateFilePath()), importColumns, true);
	}

	@Override
	public void generateCaseContactImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "", separator);

		List<String> columnsToRemove = Arrays.asList(ContactDto.CAZE,
			ContactDto.DISEASE,
			ContactDto.DISEASE_DETAILS,
			ContactDto.RESULTING_CASE,
			ContactDto.CASE_ID_EXTERNAL_SYSTEM,
			ContactDto.CASE_OR_EVENT_INFORMATION);
		importColumns = importColumns.stream().filter(column -> !columnsToRemove.contains(column.getColumnName())).collect(Collectors.toList());

		writeTemplate(Paths.get(getCaseContactImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generateContactImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "", separator);
		List<String> columnsToRemove = Arrays.asList(ContactDto.CAZE, ContactDto.RESULTING_CASE);
		importColumns = importColumns.stream().filter(column -> !columnsToRemove.contains(column.getColumnName())).collect(Collectors.toList());

		writeTemplate(Paths.get(getContactImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generateCaseLineListingImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(CaseDataDto.class, DISEASE, Disease.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, DISEASE_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, PLAGUE_TYPE, PlagueType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, DENGUE_FEVER_TYPE, DengueFeverType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, RABIES_TYPE, RabiesType.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.FIRST_NAME, String.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.LAST_NAME, String.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.SEX, Sex.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_DD, Integer.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_MM, Integer.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_YYYY, Integer.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, EPID_NUMBER, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, REPORT_DATE, Date.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CASE_ORIGIN, CaseOrigin.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, REGION, RegionReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, DISTRICT, DistrictReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, COMMUNITY, CommunityReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, FACILITY_TYPE, FacilityType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, HEALTH_FACILITY, FacilityReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, HEALTH_FACILITY_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, POINT_OF_ENTRY, PointOfEntryReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, POINT_OF_ENTRY_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, SYMPTOMS + "." + SymptomsDto.ONSET_DATE, Date.class, separator));

		writeTemplate(Paths.get(getCaseLineListingImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generatePointOfEntryImportTemplateFile() throws IOException {
		generateImportTemplateFile(PointOfEntryDto.class, Paths.get(getPointOfEntryImportTemplateFilePath()));
	}

	@Override
	public void generatePopulationDataImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.REGION, RegionReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.DISTRICT, DistrictReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(RegionDto.class, RegionDto.GROWTH_RATE, Float.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_TOTAL", Integer.class, separator));
		for (AgeGroup ageGroup : AgeGroup.values()) {
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_" + ageGroup.name(), Integer.class, separator));
		}

		writeTemplate(Paths.get(getPopulationDataImportTemplateFilePath()), importColumns, false);
	}

	private void createExportDirectoryIfNecessary() throws IOException {

		try {
			Files.createDirectories(Paths.get(configFacade.getGeneratedFilesPath()));
		} catch (IOException e) {
			logger.error("Generated files directory doesn't exist and creation failed.");
			throw e;
		}
	}

	@Override
	public void generateAreaImportTemplateFile() throws IOException {
		generateImportTemplateFile(AreaDto.class, Paths.get(getAreaImportTemplateFilePath()));
	}

	@Override
	public void generateRegionImportTemplateFile() throws IOException {
		generateImportTemplateFile(RegionDto.class, Paths.get(getRegionImportTemplateFilePath()));
	}

	@Override
	public void generateDistrictImportTemplateFile() throws IOException {
		generateImportTemplateFile(DistrictDto.class, Paths.get(getDistrictImportTemplateFilePath()));
	}

	@Override
	public void generateCommunityImportTemplateFile() throws IOException {
		generateImportTemplateFile(CommunityDto.class, Paths.get(getCommunityImportTemplateFilePath()));
	}

	@Override
	public void generateFacilityImportTemplateFile() throws IOException {
		generateImportTemplateFile(FacilityDto.class, Paths.get(getFacilityImportTemplateFilePath()));
	}

	private <T extends EntityDto> void generateImportTemplateFile(Class<T> clazz, Path filePath) throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, clazz, "", separator);

		writeTemplate(filePath, importColumns, false);
	}

	@Override
	public String getCaseImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CASE_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getCaseContactImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getCaseLineListingImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getPointOfEntryImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getPopulationDataImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getAreaImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(AREA_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getRegionImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(REGION_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getDistrictImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(DISTRICT_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getCommunityImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(COMMUNITY_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getFacilityImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(FACILITY_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String getContactImportTemplateFilePath() {

		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CONTACT_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	/**
	 * Builds a list of all fields in the case and its relevant sub entities. IMPORTANT: The order
	 * is not guaranteed; at the time of writing, clazz.getDeclaredFields() seems to return the
	 * fields in the order of declaration (which is what we need here), but that could change
	 * in the future.
	 */
	private void appendListOfFields(List<ImportColumn> importColumns, Class<?> clazz, String prefix, char separator) {

		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Method readMethod = null;
			try {
				readMethod = clazz.getDeclaredMethod("get" + WordUtils.capitalize(field.getName()));
			} catch (NoSuchMethodException e) {
				try {
					readMethod = clazz.getDeclaredMethod("is" + WordUtils.capitalize(field.getName()));
				} catch (NoSuchMethodException f) {
					continue;
				}
			}

			// Fields without a getter or whose getters are declared in a superclass are ignored
			if (readMethod == null || readMethod.getDeclaringClass() != clazz) {
				continue;
			}
			// Fields with the @ImportIgnore annotation are ignored
			if (readMethod.isAnnotationPresent(ImportIgnore.class)) {
				continue;
			}
			// Fields that are depending on a certain feature type to be active may be ignored
			if (readMethod.isAnnotationPresent(DependingOnFeatureType.class)) {
				List<FeatureType> activeServerFeatures = featureConfigurationFacade.getActiveServerFeatureTypes();
				if (!activeServerFeatures.isEmpty() && !activeServerFeatures.contains(readMethod.getAnnotation(DependingOnFeatureType.class)
					.featureType())) {
					continue;
				}
			}
			// List types are ignored
			if (Collection.class.isAssignableFrom(field.getType())) {
				continue;
			}
			// Certain field types are ignored
			if (field.getType() == UserReferenceDto.class) {
				continue;
			}
			// Other non-infrastructure EntityDto/ReferenceDto classes, recursively call this method to include fields of the sub-entity
			if (EntityDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(importColumns,
					field.getType(),
					prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".",
					separator);
			} else if (PersonReferenceDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(importColumns,
					PersonDto.class,
					prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".",
					separator);
			} else {
				importColumns.add(ImportColumn.from(clazz, prefix + field.getName(), field.getType(), separator));
			}
		}
	}

	private boolean isInfrastructureClass(Class<?> clazz) {

		return clazz == RegionReferenceDto.class || clazz == DistrictReferenceDto.class || clazz == CommunityReferenceDto.class || clazz == FacilityReferenceDto.class || clazz == PointOfEntryReferenceDto.class;
	}

	/**
	 * Writes the given line as a comment line
	 * @param csvWriter file writer
	 * @param line line to write
	 */
	private void writeCommentLine(CSVWriter csvWriter, String[] line) {
		String[] commentedLine = Arrays.copyOf(line, line.length);
		commentedLine[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + commentedLine[0];
		csvWriter.writeNext(commentedLine, false);
	}

	/**
	 * Writes template files with the following lines:
	 * <ul>
	 *     <li><code>entityNames</code> - only if <code>includeEntityNames</code> is <code>true</code></li>
	 *     <li><code>columnNames</code> - represent the DTO properties that can be filled</li>
	 *     <li><code>captions</code> - (commented) internationalized caption for each field</li>
	 *     <li><code>dataDescription</code> - (commented) data examples or description for each field</li>
	 * </ul>
	 * @param templatePath path to write the template to
	 * @param importColumns details about each CSV column
	 * @param includeEntityNames weather to include the <code>entityNames</code> or not
	 * @throws IOException
	 */
	private void writeTemplate(Path templatePath, List<ImportColumn> importColumns, boolean includeEntityNames) throws IOException {
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(templatePath.toString()), configFacade.getCsvSeparator())) {
			if (includeEntityNames) {
				writer.writeNext(importColumns.stream().map(ImportColumn::getEntityName).toArray(String[]::new));
			}
			writer.writeNext(importColumns.stream().map(ImportColumn::getColumnName).toArray(String[]::new));
			writeCommentLine(writer, importColumns.stream().map(ImportColumn::getCaption).toArray(String[]::new));
			writeCommentLine(writer, importColumns.stream().map(ImportColumn::getDataDescription).toArray(String[]::new));
			writer.flush();
		}
	}

	@Override
	public String getImportTemplateContent(String templateFilePath) throws IOException {
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(Paths.get(templateFilePath)), charset);
		return resolvePlaceholders(content);
	}

	/**
	 * Replaces placeholders in the given file content.
	 * The placeholders are resolved using dynamic data. For any static data extend {@link ImportColumn}.
	 * @param content file content.
	 * @return
	 * @see ImportFacade#ACTIVE_DISEASES_PLACEHOLDER
	 */
	private String resolvePlaceholders(String content) {
		Map<String, Provider<String>> placeholderResolvers = new HashMap<>();
		placeholderResolvers.put(ImportFacade.ACTIVE_DISEASES_PLACEHOLDER,
			() -> StringUtils.join(diseaseConfigurationFacade.getAllActiveDiseases().stream().map(Disease::getName).collect(Collectors.toList()),
				ImportExportUtils.getCSVSeparatorDifferentFromCurrent(configFacade.getCsvSeparator())));

		for (Map.Entry<String, Provider<String>> placeholderResolver : placeholderResolvers.entrySet()) {
			content = content.replace(placeholderResolver.getKey(), placeholderResolver.getValue().get());
		}

		return content;
	}

	@LocalBean
	@Stateless
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {

	}
}
