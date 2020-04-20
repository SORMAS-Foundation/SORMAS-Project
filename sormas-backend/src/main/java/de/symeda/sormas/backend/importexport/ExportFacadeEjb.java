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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportFacade;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ExportFacade")
public class ExportFacadeEjb implements ExportFacade {

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
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PersonService personService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private ExportConfigurationService exportConfigurationService;
	@EJB
	private DatabaseExportService databaseExportService;

	@Override
	public String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException, IOException {

		Path tempPath = Paths.get(configFacade.getTempFilesPath());
		// Create the folder if it doesn't exist
		try {	
			Files.createDirectories(tempPath);
		} catch (IOException e) {
			logger.error("Temp directory doesn't exist and creation failed.");
			throw e;
		}

		long startTime = System.currentTimeMillis();
		Path csvTempPath = Files.createTempDirectory(tempPath, ImportExportUtils.TEMP_FILE_PREFIX);
		String zipFilePath;
		try {
			// Export all selected tables to .csv files
			List<Path> exportedCsvs = databaseExportService.exportAsCsvFiles(csvTempPath, databaseTables);

			String formattedDate = DateHelper.formatDateForExport(new Date());
			int exportId = new Random().nextInt(Integer.MAX_VALUE);
	
			// Create a zip containing all created .csv files
			zipFilePath =
				createZipFromFiles(csvTempPath, exportedCsvs, formattedDate, exportId);
		
		} finally {
			Files.delete(csvTempPath);
		}
		logger
			.debug(
				"generateDatabaseExportArchive() finished. {} tables, {} s",
				databaseTables.size(),
				(System.currentTimeMillis() - startTime) / 1_000);

		return zipFilePath;
	}

	@Override
	public String generateZipArchive(String date, int randomNumber) {
		Path path = new File(configFacade.getTempFilesPath()).toPath();
		String fileName = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + DateHelper.formatDateForExport(new Date()) + "_" + new Random().nextInt(Integer.MAX_VALUE) + ".zip";
		Path filePath = path.resolve(fileName);
		String zipPath = filePath.toString();
		return zipPath;
	}
	
	@Override
	public List<ExportConfigurationDto> getExportConfigurations() {
		User user = userService.getCurrentUser();
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExportConfiguration> cq = cb.createQuery(ExportConfiguration.class);
		Root<ExportConfiguration> config = cq.from(ExportConfiguration.class);
		
		cq.where(cb.equal(config.get(ExportConfiguration.USER), user));
		cq.orderBy(cb.desc(config.get(ExportConfiguration.CHANGE_DATE)));
		
		return em.createQuery(cq).getResultList().stream()
				.map(c -> toExportConfigurationDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public void saveExportConfiguration(ExportConfigurationDto exportConfiguration) {
		ExportConfiguration entity = fromExportConfigurationDto(exportConfiguration);
		exportConfigurationService.ensurePersisted(entity);
	}
	
	@Override
	public void deleteExportConfiguration(String exportConfigurationUuid) {
		ExportConfiguration exportConfiguration = exportConfigurationService.getByUuid(exportConfigurationUuid);
		exportConfigurationService.delete(exportConfiguration);
	}
	
	/**
	 * Creates a zip by collecting all .csv files that match the file names of the passed databaseTables plus
	 * the date and randomNumber suffixes. The zip is stored in the same export folder that contains the .csv files
	 * and its file path is returned.
	 */
	private String createZipFromFiles(Path basePath, List<Path> files, String date, int randomNumber) throws ExportErrorException {
		String zipPath = generateZipArchive(date, randomNumber);
		try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream( new FileOutputStream(zipPath)))) {
			for (Path file : files) {
				Path filePath = basePath.resolve(file);
				
				if (! filePath.normalize().startsWith(basePath.normalize())) {
					throw new IOException(filePath + " is not child of " + basePath);
				}
				
				Path relativePath = basePath.relativize(file);
				zos.putNextEntry(new ZipEntry(relativePath.toString().replace(File.separatorChar, '/')));
				Files.copy(filePath, zos);
				zos.closeEntry();
			}
			return zipPath;
		} catch (IOException e) {
			logger.error("Failed to generate a zip file for database export.");
			throw new ExportErrorException();
		} finally {
			//get rid of csv files
			files.forEach(f -> {
				try {
					Files.deleteIfExists(f);
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			});
		}
	}

	public ExportConfiguration fromExportConfigurationDto(@NotNull ExportConfigurationDto source) {
		ExportConfiguration target = exportConfigurationService.getByUuid(source.getUuid());
		if (target == null) {
			target = new ExportConfiguration();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);
		
		target.setName(source.getName());
		target.setUser(userService.getByReferenceDto(source.getUser()));
		target.setExportType(source.getExportType());
		target.setProperties(source.getProperties());
		
		return target;
	}
	
	public static ExportConfigurationDto toExportConfigurationDto(ExportConfiguration source) {
		if (source == null) {
			return null;
		}
		
		ExportConfigurationDto target = new ExportConfigurationDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());
		target.setUser(UserFacadeEjb.toReferenceDto(source.getUser()));
		target.setExportType(source.getExportType());
		target.setProperties(source.getProperties());
		
		return target;
	}

	@LocalBean
	@Stateless
	public static class ExportFacadeEjbLocal extends ExportFacadeEjb {
	}

}
