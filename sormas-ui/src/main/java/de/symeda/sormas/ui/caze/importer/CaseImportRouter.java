package de.symeda.sormas.ui.caze.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.Consumer;

import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.ui.importer.hierarchical.SheetReader;

public final class CaseImportRouter {

	private CaseImportRouter() {
	}

	public static void route(File file, char separator, Runnable classic, Consumer<ImportSheet> nested) throws IOException {
		ImportSheet sheet;
		try (InputStream input = Files.newInputStream(file.toPath())) {
			sheet = SheetReader.read(input, separator);
		}
		if (ImportFormatDetector.detect(sheet) == ImportFormatDetector.ImportFormat.NESTED) {
			nested.accept(sheet);
		} else {
			classic.run();
		}
	}
}
