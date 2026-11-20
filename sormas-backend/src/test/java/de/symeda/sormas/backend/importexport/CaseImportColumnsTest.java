package de.symeda.sormas.backend.importexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	@Test
	public void theTemplateNamesTheGroupOfRepeatedCaptionsAndMarksRequiredColumns() throws IOException, CsvException {
		MockProducer.mockProperty(ConfigFacadeEjb.GENERATED_FILES_PATH, generatedFiles.toString());
		ImportFacadeEjbLocal importFacade = getBean(ImportFacadeEjbLocal.class);
		List<ImportColumn> columns = importFacade.caseImportColumns(getFeatureConfigurationFacade().getActiveServerFeatureConfigurations());

		importFacade.generateCaseImportTemplateFile(getFeatureConfigurationFacade().getActiveServerFeatureConfigurations());

		try (CSVReader reader = CSVUtils.createCSVReader(
			Files.newBufferedReader(Paths.get(importFacade.getCaseImportTemplateFilePath()), StandardCharsets.UTF_8),
			getConfigFacade().getCsvSeparator())) {
			List<String[]> rows = reader.readAll();
			List<String> names = Arrays.asList(rows.get(1));
			String[] captions = rows.get(2);
			String[] descriptions = rows.get(3);
			assertEquals("##", captions[0].substring(0, 2));

			assertTrue(captionOf(names, captions, "healthFacility").startsWith("Place of stay - "));
			assertTrue(captionOf(names, captions, "person.address.region").startsWith("Home address - "));
			assertNotEquals(captions[indexOf(columns, "Sample", "lab")], captions[indexOf(columns, "PathogenTest", "lab")]);
			assertTrue(captionOf(names, captions, "disease").endsWith(" *"));
			assertTrue(captionOf(names, captions, "person.firstName").endsWith(" *"));
			assertTrue(captionOf(names, captions, "person.lastName").endsWith(" *"));
			assertTrue(captionOf(names, captions, "responsibleRegion").endsWith(" *"));
			assertTrue(captionOf(names, captions, "responsibleDistrict").endsWith(" *"));
			assertFalse(captionOf(names, captions, "person.sex").endsWith(" *"));
			assertTrue(descriptions[names.indexOf("healthFacility")].endsWith("Required unless caseOrigin is POINT_OF_ENTRY."));
			assertTrue(descriptions[names.indexOf("pointOfEntry")].endsWith("Required when caseOrigin is POINT_OF_ENTRY."));

			List<String> stripped = Arrays.stream(captions).map(caption -> caption.replaceFirst("^##", "")).collect(Collectors.toList());
			assertEquals(stripped.size(), new HashSet<>(stripped).size(), "duplicate captions: " + duplicates(stripped, names));
		}
	}

	private static int indexOf(List<ImportColumn> columns, String entityName, String columnName) {
		return IntStream.range(0, columns.size())
			.filter(index -> entityName.equals(columns.get(index).getEntityName()) && columnName.equals(columns.get(index).getColumnName()))
			.findFirst()
			.orElseThrow(AssertionError::new);
	}

	private static String captionOf(List<String> names, String[] captions, String columnName) {
		return captions[names.indexOf(columnName)].replaceFirst("^##", "");
	}

	private static List<String> duplicates(List<String> values, List<String> names) {
		return IntStream.range(0, values.size())
			.filter(index -> Collections.frequency(values, values.get(index)) > 1)
			.mapToObj(index -> values.get(index) + " <- " + names.get(index))
			.collect(Collectors.toList());
	}
}
