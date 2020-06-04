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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
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
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
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

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_case_template.csv";
	private static final String CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_case_contact_template.csv";
	private static final String CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_line_listing_template.csv";
	private static final String POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_point_of_entry_template.csv";
	private static final String POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_population_data_template.csv";
	private static final String REGION_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_region_template.csv";
	private static final String DISTRICT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_district_template.csv";
	private static final String COMMUNITY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_community_template.csv";
	private static final String FACILITY_LABORATORY_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_facility_laboratory_template.csv";
	private static final String CONTACT_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX
			+ "_import_contact_template.csv";

	@Override
	public void generateCaseImportTemplateFile() throws IOException {				
		createExportDirectoryIfNessecary();

		List<String> columnNames = new ArrayList<>();
		List<String> entityNames = new ArrayList<>();
		appendListOfFields(columnNames, entityNames, CaseDataDto.class, "");
		appendListOfFields(columnNames, entityNames, SampleDto.class, "");
		appendListOfFields(columnNames, entityNames, PathogenTestDto.class, "");
		Path filePath = Paths.get(getCaseImportTemplateFilePath());
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()), configFacade.getCsvSeparator())) {
			writer.writeNext(entityNames.toArray(new String[entityNames.size()]));
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
	}
	
	@Override
	public void generateCaseContactImportTemplateFile() throws IOException {
		createExportDirectoryIfNessecary();

		List<String> columnNames = new ArrayList<>();
		List<String> entityNames = new ArrayList<>();
		appendListOfFields(columnNames, entityNames, ContactDto.class, "");
		columnNames.removeAll(Arrays.asList(ContactDto.CAZE, ContactDto.DISEASE, ContactDto.DISEASE_DETAILS,
				ContactDto.RESULTING_CASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION));
		Path filePath = Paths.get(getCaseContactImportTemplateFilePath());
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()), configFacade.getCsvSeparator())) {
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
	}

	@Override
	public void generateContactImportTemplateFile() throws IOException {
		createExportDirectoryIfNessecary();

		List<String> columnNames = new ArrayList<>();
		List<String> entityNames = new ArrayList<>();
		appendListOfFields(columnNames, entityNames, ContactDto.class, "");
		columnNames.removeAll(Arrays.asList(ContactDto.CAZE, ContactDto.RESULTING_CASE));

		Path filePath = Paths.get(getContactImportTemplateFilePath());
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()),
				configFacade.getCsvSeparator())) {
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
	}

	@Override
	public void generateCaseLineListingImportTemplateFile() throws IOException {
		createExportDirectoryIfNessecary();

		List<String> columnNames = new ArrayList<>();
		columnNames.add(CaseDataDto.DISEASE);
		columnNames.add(CaseDataDto.DISEASE_DETAILS);
		columnNames.add(CaseDataDto.PLAGUE_TYPE);
		columnNames.add(CaseDataDto.DENGUE_FEVER_TYPE);
		columnNames.add(CaseDataDto.RABIES_TYPE);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.FIRST_NAME);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.LAST_NAME);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.SEX);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_DD);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_MM);
		columnNames.add(CaseDataDto.PERSON + "." + PersonDto.BIRTH_DATE_YYYY);
		columnNames.add(CaseDataDto.EPID_NUMBER);
		columnNames.add(CaseDataDto.REPORT_DATE);
		columnNames.add(CaseDataDto.CASE_ORIGIN);
		columnNames.add(CaseDataDto.REGION);
		columnNames.add(CaseDataDto.DISTRICT);
		columnNames.add(CaseDataDto.COMMUNITY);
		columnNames.add(CaseDataDto.HEALTH_FACILITY);
		columnNames.add(CaseDataDto.HEALTH_FACILITY_DETAILS);
		columnNames.add(CaseDataDto.POINT_OF_ENTRY);
		columnNames.add(CaseDataDto.POINT_OF_ENTRY_DETAILS);
		columnNames.add(CaseDataDto.SYMPTOMS + "." + SymptomsDto.ONSET_DATE);

		Path filePath = Paths.get(getCaseLineListingImportTemplateFilePath());
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()), configFacade.getCsvSeparator())) {
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
	}

	@Override
	public void generatePointOfEntryImportTemplateFile() throws IOException {
		generateImportTemplateFile(PointOfEntryDto.class, Paths.get(getPointOfEntryImportTemplateFilePath()));
	}
	
	@Override
	public void generatePopulationDataImportTemplateFile() throws IOException {
		
		createExportDirectoryIfNessecary();
		
		List<String> columnNames = new ArrayList<>();
		columnNames.add(PopulationDataDto.REGION);
		columnNames.add(PopulationDataDto.DISTRICT);
		columnNames.add(RegionDto.GROWTH_RATE);
		columnNames.add("TOTAL");
		columnNames.add("MALE_TOTAL");
		columnNames.add("FEMALE_TOTAL");
		for (AgeGroup ageGroup : AgeGroup.values()) {
			columnNames.add("TOTAL_" + ageGroup.name());
			columnNames.add("MALE_" + ageGroup.name());
			columnNames.add("FEMALE_" + ageGroup.name());
		}
		Path filePath = Paths.get(getPopulationDataImportTemplateFilePath());
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()), configFacade.getCsvSeparator())) {
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
	}

	private void createExportDirectoryIfNessecary() throws IOException {
		try {
			Files.createDirectories(Paths.get(configFacade.getGeneratedFilesPath()));
		} catch (IOException e) {
			logger.error("Generated files directory doesn't exist and creation failed.");
			throw e;
		}
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
	public void generateFacilityLaboratoryImportTemplateFile() throws IOException {
		generateImportTemplateFile(FacilityDto.class, Paths.get(getFacilityLaboratoryImportTemplateFilePath()));
	}

	private <T extends EntityDto> void generateImportTemplateFile(Class<T> clazz, Path filePath) throws IOException {
		createExportDirectoryIfNessecary();

		List<String> columnNames = new ArrayList<>();
		List<String> entityNames = new ArrayList<>();
		appendListOfFields(columnNames, entityNames, clazz, "");
		try (CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()),
				configFacade.getCsvSeparator())) {
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
			writer.flush();
		}
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
	public String getFacilityLaboratoryImportTemplateFilePath() {
		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(FACILITY_LABORATORY_IMPORT_TEMPLATE_FILE_NAME);
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
	private void appendListOfFields(List<String> columnNames, List<String> entityNames, Class<?> clazz, String prefix) {
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
				appendListOfFields(columnNames, entityNames, field.getType(), prefix == null || prefix.isEmpty() ? field.getName() + "." :  prefix + field.getName() + ".");
			} else if (PersonReferenceDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(columnNames, entityNames, PersonDto.class, prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".");
			} else {
				entityNames.add(DataHelper.getHumanClassName(clazz));
				columnNames.add(prefix + field.getName());
			}
		}
	}

	private boolean isInfrastructureClass(Class<?> clazz) {
		return clazz == RegionReferenceDto.class || clazz == DistrictReferenceDto.class || clazz == CommunityReferenceDto.class 
				|| clazz == FacilityReferenceDto.class || clazz == PointOfEntryReferenceDto.class;
	}

	@LocalBean
	@Stateless
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {

	}
}
