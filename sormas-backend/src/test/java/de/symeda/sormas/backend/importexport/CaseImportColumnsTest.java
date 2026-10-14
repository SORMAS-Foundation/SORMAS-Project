package de.symeda.sormas.backend.importexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;

public class CaseImportColumnsTest extends AbstractBeanTest {

	@TempDir
	Path generatedFiles;

	@Test
	public void theTemplateIsWrittenFromTheSameColumnsTheImportReads() throws IOException, CsvException {
		MockProducer.mockProperty(ConfigFacadeEjb.GENERATED_FILES_PATH, generatedFiles.toString());
		ImportFacadeEjbLocal importFacade = getBean(ImportFacadeEjbLocal.class);
		List<ImportColumn> columns = importFacade.caseImportColumns(getFeatureConfigurationFacade().getActiveServerFeatureConfigurations());

		importFacade.generateCaseImportTemplateFile(getFeatureConfigurationFacade().getActiveServerFeatureConfigurations());

		try (CSVReader reader = CSVUtils.createCSVReader(
			Files.newBufferedReader(Paths.get(importFacade.getCaseImportTemplateFilePath()), StandardCharsets.UTF_8),
			getConfigFacade().getCsvSeparator())) {
			List<String[]> rows = reader.readAll();
			assertEquals(columns.stream().map(ImportColumn::getEntityName).collect(Collectors.toList()), Arrays.asList(rows.get(0)));
			assertEquals(columns.stream().map(ImportColumn::getColumnName).collect(Collectors.toList()), Arrays.asList(rows.get(1)));
		}
		assertTrue(columns.stream().anyMatch(column -> "Sample".equals(column.getEntityName()) && "fieldSampleID".equals(column.getColumnName())));
	}
}
