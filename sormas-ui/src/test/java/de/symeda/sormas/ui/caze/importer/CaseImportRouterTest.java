package de.symeda.sormas.ui.caze.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

class CaseImportRouterTest {

	@TempDir
	Path folder;

	@Test
	void aClassicFileGoesToTheClassicImporter() throws IOException {
		File file = write("CaseData,Sample\ndisease,fieldSampleID\nTB,S1\nTB,S2\n");
		AtomicBoolean classic = new AtomicBoolean();
		AtomicReference<ImportSheet> nested = new AtomicReference<>();

		CaseImportRouter.route(file, ',', () -> classic.set(true), nested::set);

		assertTrue(classic.get());
		assertNull(nested.get());
	}

	@Test
	void aNestedFileGoesToThePreviewWithItsSheet() throws IOException {
		File file = write("CaseData,Sample\ndisease,fieldSampleID\nTB,S1\n,S2\n");
		AtomicBoolean classic = new AtomicBoolean();
		AtomicReference<ImportSheet> nested = new AtomicReference<>();

		CaseImportRouter.route(file, ',', () -> classic.set(true), nested::set);

		assertFalse(classic.get());
		assertEquals(2, nested.get().getRows().size());
	}

	@Test
	void aFileWithoutTheTwoHeaderRowsGoesToTheClassicImporter() throws IOException {
		File file = write("disease\n");
		AtomicBoolean classic = new AtomicBoolean();

		CaseImportRouter.route(file, ',', () -> classic.set(true), sheet -> fail());

		assertTrue(classic.get());
	}

	private File write(String csv) throws IOException {
		Path path = folder.resolve("import.csv");
		Files.write(path, csv.getBytes(StandardCharsets.UTF_8));
		return path.toFile();
	}
}
