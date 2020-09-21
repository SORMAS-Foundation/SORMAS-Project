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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ImportFacade")
public class ImportFacadeEjb implements ImportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictService districtService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityService communityService;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityService facilityService;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private PersonService personService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

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

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, CaseDataDto.class, "");
		appendListOfFields(importColumns, SampleDto.class, "");
		appendListOfFields(importColumns, PathogenTestDto.class, "");

		writeTemplate(Paths.get(getCaseImportTemplateFilePath()), importColumns, true);
	}

	@Override
	public void generateCaseContactImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "");

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

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "");
		List<String> columnsToRemove = Arrays.asList(ContactDto.CAZE, ContactDto.RESULTING_CASE);
		importColumns = importColumns.stream().filter(column -> !columnsToRemove.contains(column.getColumnName())).collect(Collectors.toList());

		writeTemplate(Paths.get(getContactImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generateCaseLineListingImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.DISEASE, Disease.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.DISEASE_DETAILS, String.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.PLAGUE_TYPE, PlagueType.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.DENGUE_FEVER_TYPE, DengueFeverType.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.RABIES_TYPE, RabiesType.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.FIRST_NAME, String.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.LAST_NAME, String.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.SEX, Sex.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_DD, Integer.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_MM, Integer.class));
		importColumns.add(ImportColumn.from(PersonDto.class, CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_YYYY, Integer.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.EPID_NUMBER, String.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.REPORT_DATE, Date.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.CASE_ORIGIN, CaseOrigin.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.REGION, RegionReferenceDto.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.DISTRICT, DistrictReferenceDto.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.COMMUNITY, CommunityReferenceDto.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.FACILITY_TYPE, FacilityType.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.HEALTH_FACILITY, FacilityReferenceDto.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.HEALTH_FACILITY_DETAILS, String.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.POINT_OF_ENTRY, PointOfEntryReferenceDto.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.POINT_OF_ENTRY_DETAILS, String.class));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CaseDataDto.SYMPTOMS + "." + SymptomsDto.ONSET_DATE, Date.class));

		writeTemplate(Paths.get(getCaseLineListingImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generatePointOfEntryImportTemplateFile() throws IOException {
		generateImportTemplateFile(PointOfEntryDto.class, Paths.get(getPointOfEntryImportTemplateFilePath()), PointOfEntryDto.I18N_PREFIX);
	}

	@Override
	public void generatePopulationDataImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.REGION, RegionReferenceDto.class));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.DISTRICT, DistrictReferenceDto.class));
		importColumns.add(ImportColumn.from(RegionDto.class, RegionDto.GROWTH_RATE, Float.class));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL", Integer.class));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_TOTAL", Integer.class));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_TOTAL", Integer.class));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_TOTAL", Integer.class));
		for (AgeGroup ageGroup : AgeGroup.values()) {
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL_" + ageGroup.name(), Integer.class));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_" + ageGroup.name(), Integer.class));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_" + ageGroup.name(), Integer.class));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_" + ageGroup.name(), Integer.class));
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
		generateImportTemplateFile(AreaDto.class, Paths.get(getAreaImportTemplateFilePath()), AreaDto.I18N_PREFIX);
	}

	@Override
	public void generateRegionImportTemplateFile() throws IOException {
		generateImportTemplateFile(RegionDto.class, Paths.get(getRegionImportTemplateFilePath()), RegionDto.I18N_PREFIX);
	}

	@Override
	public void generateDistrictImportTemplateFile() throws IOException {
		generateImportTemplateFile(DistrictDto.class, Paths.get(getDistrictImportTemplateFilePath()), DistrictDto.I18N_PREFIX);
	}

	@Override
	public void generateCommunityImportTemplateFile() throws IOException {
		generateImportTemplateFile(CommunityDto.class, Paths.get(getCommunityImportTemplateFilePath()), CommunityDto.I18N_PREFIX);
	}

	@Override
	public void generateFacilityImportTemplateFile() throws IOException {
		generateImportTemplateFile(FacilityDto.class, Paths.get(getFacilityImportTemplateFilePath()), FacilityDto.I18N_PREFIX);
	}

	private <T extends EntityDto> void generateImportTemplateFile(Class<T> clazz, Path filePath, String i18nPrefix) throws IOException {

		createExportDirectoryIfNecessary();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, clazz, "");

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
	private void appendListOfFields(List<ImportColumn> importColumns, Class<?> clazz, String prefix) {

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
				if (!activeServerFeatures.isEmpty()
					&& !activeServerFeatures.contains(readMethod.getAnnotation(DependingOnFeatureType.class).featureType())) {
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
				appendListOfFields(
					importColumns,
					field.getType(),
					prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".");
			} else if (PersonReferenceDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(
					importColumns,
					PersonDto.class,
					prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".");
			} else {
				importColumns.add(ImportColumn.from(clazz, prefix + field.getName(), field.getType()));
			}
		}
	}

	private boolean isInfrastructureClass(Class<?> clazz) {

		return clazz == RegionReferenceDto.class
			|| clazz == DistrictReferenceDto.class
			|| clazz == CommunityReferenceDto.class
			|| clazz == FacilityReferenceDto.class
			|| clazz == PointOfEntryReferenceDto.class;
	}

	@LocalBean
	@Stateless
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {

	}

	private void writeCommentLine(CSVWriter csvWriter, String[] line) {
		String[] commentedLine = Arrays.copyOf(line, line.length);
		commentedLine[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + commentedLine[0];
		csvWriter.writeNext(commentedLine);
	}

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
}
