package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;

public final class Planner {

	public ImportPlan plan(ImportDefinition definition, ImportSheet sheet, Set<Integer> selectedBlocks) {
		SheetColumns sheetColumns = SheetColumns.of(definition, sheet);
		List<ImportIssueDto> fileIssues = new ArrayList<>(HeaderCheck.check(definition, sheet, sheetColumns));
		fileIssues.addAll(definition.fileIssues(sheet));
		if (!fileIssues.isEmpty()) {
			return new ImportPlan(fileIssues, List.of(), List.of(), sheetColumns.ignored());
		}

		Set<String> importableHeaders = sheetColumns.columns().stream().map(ColumnSpec::header).collect(Collectors.toSet());
		GroupingResult grouping = new Grouper().group(sheet, definition.schema(), importableHeaders);
		Map<Integer, ImportNode> roots = selectRoots(grouping.roots(), selectedBlocks);
		List<ColumnSpec> columns = sheetColumns.columns();
		RecordBinder binder = new RecordBinder(definition.cellWriter());
		Map<ImportNode, PlannedRecord> records = new IdentityHashMap<>();
		Map<String, ImportNode> rootsByIdentity = new HashMap<>();

		for (ImportLevel level : definition.schema().levels()) {
			if (level.isEmbedded()) {
				continue;
			}
			LevelHandler handler = definition.handler(level);
			List<PlannedRecord> drafts = draft(level, roots.values(), records, handler, binder, columns);
			rejectDuplicateKeys(drafts, handler);
			List<PlannedRecord> matchable = drafts.stream().filter(draft -> draft.node().issues().isEmpty()).collect(Collectors.toList());
			Map<ImportNode, Match> matches = matchable.isEmpty() ? Map.of() : handler.match(matchable);
			for (PlannedRecord draft : matchable) {
				Match match = matches.getOrDefault(draft.node(), Match.create());
				if (match.isError()) {
					draft.reportIssue(match.errorColumn(), match.errorMessage());
					continue;
				}
				if (draft.parent() == null && match.isExisting() && isRepeatedRoot(draft, match, rootsByIdentity)) {
					continue;
				}
				PlannedRecord record;
				if (match.isExisting()) {
					record = rebind(draft, match, binder, columns);
				} else if (match.isCreateWith()) {
					PlannedRecord created = PlannedRecord.createdWith(draft.node(), draft.parent(), match);
					binder.bind(created.node(), columns, created.targets());
					List<ColumnSpec> storedColumns =
						columns.stream().filter(column -> created.baseline().containsKey(column.target())).collect(Collectors.toList());
					FieldDiff.diff(created.node(), storedColumns, created.baseline(), created.targets()).forEach(created::addChange);
					record = created;
				} else {
					record = draft.parent() == null ? draft.withReason(MatchReason.NEW) : draft;
				}
				if (record.parent() != null) {
					record.parent().children().add(record);
				}
				records.put(record.node(), record);
			}
		}

		records.values().forEach(record -> definition.handler(record.node().level()).complete(record));

		List<PlannedBlock> blocks = roots.entrySet()
			.stream()
			.map(entry -> new PlannedBlock(entry.getKey(), entry.getValue(), records.get(entry.getValue())))
			.collect(Collectors.toList());
		return new ImportPlan(List.of(), grouping.issues(), blocks, sheetColumns.ignored());
	}

	private static Map<Integer, ImportNode> selectRoots(List<ImportNode> roots, Set<Integer> selectedBlocks) {
		Map<Integer, ImportNode> selected = new LinkedHashMap<>();
		for (int i = 0; i < roots.size(); i++) {
			if (selectedBlocks == null || selectedBlocks.contains(i)) {
				selected.put(i, roots.get(i));
			}
		}
		return selected;
	}

	private static List<PlannedRecord> draft(
		ImportLevel level,
		Collection<ImportNode> roots,
		Map<ImportNode, PlannedRecord> records,
		LevelHandler handler,
		RecordBinder binder,
		List<ColumnSpec> columns) {
		List<PlannedRecord> drafts = new ArrayList<>();
		roots.stream().flatMap(ImportNode::subtree).filter(node -> node.level() == level).forEach(node -> {
			PlannedRecord parent = node.parent() == null ? null : records.get(node.parent());
			if (node.parent() != null && parent == null) {
				return;
			}
			PlannedRecord draft = PlannedRecord.created(node, parent, handler.newTargets(node, parent));
			binder.bind(node, columns, draft.targets());
			node.describeAs(handler.label(draft));
			drafts.add(draft);
		});
		return drafts;
	}

	private static void rejectDuplicateKeys(List<PlannedRecord> drafts, LevelHandler handler) {
		Map<String, ImportNode> firstByKey = new HashMap<>();
		for (PlannedRecord draft : drafts) {
			Optional<String> key = handler.key(draft);
			if (key.isEmpty()) {
				continue;
			}
			String scope = draft.parent() == null ? "" : String.valueOf(draft.parent().node().lineNumber());
			ImportNode first = firstByKey.putIfAbsent(scope + "/" + key.get(), draft.node());
			if (first != null) {
				draft.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalDuplicateKey, key.get(), first.lineNumber()));
			}
		}
	}

	private static boolean isRepeatedRoot(PlannedRecord draft, Match match, Map<String, ImportNode> rootsByIdentity) {
		ImportNode first = rootsByIdentity.putIfAbsent(match.identity(), draft.node());
		if (first == null) {
			return false;
		}
		draft.reportIssue(
			null,
			I18nProperties.getValidationError(Validations.importHierarchicalDuplicateKey, draft.node().label(), first.lineNumber()));
		return true;
	}

	private static PlannedRecord rebind(PlannedRecord draft, Match match, RecordBinder binder, List<ColumnSpec> columns) {
		PlannedRecord record = PlannedRecord.existing(draft.node(), draft.parent(), match);
		binder.bind(record.node(), columns, record.targets());
		FieldDiff.diff(record.node(), columns, record.original(), record.targets()).forEach(record::addChange);
		return record;
	}
}
