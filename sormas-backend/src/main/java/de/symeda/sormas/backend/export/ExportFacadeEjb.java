package de.symeda.sormas.backend.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.export.DatabaseTable;
import de.symeda.sormas.api.export.ExportFacade;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ExportFacade")
public class ExportFacadeEjb implements ExportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException {
		// Create the folder if it doesn't exist
		try {
			Files.createDirectories(Paths.get(configFacade.getExportPath()));
		} catch (IOException e) {
			logger.error("Export directory doesn't exist and creation failed.");
			throw new ExportErrorException();
		}

		// Export all selected tables to .csv files
		String date = DateHelper.formatDateForExport(new Date());
		int randomNumber = new Random().nextInt(Integer.MAX_VALUE);
		for (DatabaseTable databaseTable : databaseTables) {
			em.createNativeQuery(databaseTable.getExportQuery(date, randomNumber)).getResultList();
		}

		// Create a zip containing all created .csv files
		return createZipFromCsvFiles(databaseTables, date, randomNumber);
	}

	/**
	 * Creates a zip by collecting all .csv files that match the file names of the passed databaseTables plus
	 * the date and randomNumber suffixes. The zip is stored in the same export folder that contains the .csv files
	 * and its file path is returned.
	 */
	private String createZipFromCsvFiles(List<DatabaseTable> databaseTables, String date, int randomNumber) throws ExportErrorException {
		try {
			String zipPath = configFacade.getExportPath() + "sormas_export_" + 
					date + "_" + randomNumber + ".zip";
			FileOutputStream fos = new FileOutputStream(zipPath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);

			for (DatabaseTable databaseTable : databaseTables) {
				File file = new File(configFacade.getExportPath(), "sormas_export_" + 
						databaseTable.getFileName() + "_" + date + "_" + randomNumber + ".csv");
				Path filePath = file.toPath();
				zos.putNextEntry(new ZipEntry(databaseTable.getFileName() + ".csv"));
				byte[] bytes = Files.readAllBytes(filePath);
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
			}

			zos.close();
			return zipPath;
		} catch (IOException e) {
			logger.error("Failed to generate a zip file for database export.");
			throw new ExportErrorException();
		}
	}

	@LocalBean
	@Stateless
	public static class ExportFacadeEjbLocal extends ExportFacadeEjb {
	}

}
