package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;

public final class SheetFixtures {

	private SheetFixtures() {
	}

	public static ImportSheet table(String... lines) {
		List<String> headers = cells(lines[0]).stream().map(String::strip).map(h -> h.contains(".") ? h : h + ".key").collect(Collectors.toList());
		List<ImportSheetRow> rows = new ArrayList<>();
		for (int i = 1; i < lines.length; i++) {
			rows.add(new ImportSheetRow(i + 1, cells(lines[i])));
		}
		return new ImportSheet(headers, rows);
	}

	private static List<String> cells(String line) {
		String inner = line.strip();
		inner = inner.substring(1, inner.length() - 1);
		return Arrays.stream(inner.split("\\|", -1)).map(cell -> cell.replace("\\u00A0", "\u00A0")).collect(Collectors.toList());
	}
}
