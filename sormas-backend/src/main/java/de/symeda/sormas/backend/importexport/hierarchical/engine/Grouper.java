package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;

public final class Grouper {

	public GroupingResult group(ImportSheet sheet, ImportSchema schema) {
		List<ImportLevel> levels = schema.levels();
		Map<ImportLevel, List<Integer>> columns = columnsByLevel(sheet.getHeaders(), schema);
		ImportNode[] open = new ImportNode[levels.size()];
		List<ImportNode> roots = new ArrayList<>();
		List<ImportIssueDto> unattached = new ArrayList<>();

		for (ImportSheetRow row : sheet.getRows()) {
			for (int depth = 0; depth < levels.size(); depth++) {
				ImportLevel level = levels.get(depth);
				Map<String, String> cells = filledCells(row, sheet.getHeaders(), columns.get(level));
				if (cells.isEmpty()) {
					continue;
				}
				if (depth > 0 && open[depth - 1] == null) {
					ImportIssueDto orphan = orphan(row.getLineNumber(), level, levels.get(depth - 1));
					if (open[0] == null) {
						unattached.add(orphan);
					} else {
						open[0].issues().add(orphan);
					}
					break;
				}
				ImportNode node = new ImportNode(level, row.getLineNumber(), cells);
				if (depth == 0) {
					roots.add(node);
				} else {
					open[depth - 1].addChild(node);
				}
				open[depth] = node;
				Arrays.fill(open, depth + 1, open.length, null);
			}
		}
		return new GroupingResult(roots, unattached);
	}

	private static Map<ImportLevel, List<Integer>> columnsByLevel(List<String> headers, ImportSchema schema) {
		Map<ImportLevel, List<Integer>> columns = new HashMap<>();
		schema.levels().forEach(level -> columns.put(level, new ArrayList<>()));
		for (int i = 0; i < headers.size(); i++) {
			int index = i;
			schema.levelOf(headers.get(i)).ifPresent(level -> columns.get(level).add(index));
		}
		return columns;
	}

	private static Map<String, String> filledCells(ImportSheetRow row, List<String> headers, List<Integer> columnIndexes) {
		Map<String, String> cells = new LinkedHashMap<>();
		for (int index : columnIndexes) {
			String value = row.cell(index);
			if (!value.isEmpty()) {
				cells.put(headers.get(index), value);
			}
		}
		return cells;
	}

	private static ImportIssueDto orphan(int lineNumber, ImportLevel level, ImportLevel parentLevel) {
		return new ImportIssueDto(
			ImportIssueKind.ROW,
			lineNumber,
			null,
			I18nProperties.getValidationError(Validations.importHierarchicalOrphanRow, lineNumber, level.name(), parentLevel.name()));
	}
}
