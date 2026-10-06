package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;

public final class ImportNode {

	private final ImportLevel level;
	private final int lineNumber;
	private final Map<String, String> cells;
	private final List<ImportNode> children = new ArrayList<>();
	private final List<ImportIssueDto> issues = new ArrayList<>();
	private ImportNode parent;

	public ImportNode(ImportLevel level, int lineNumber, Map<String, String> cells) {
		this.level = level;
		this.lineNumber = lineNumber;
		this.cells = Collections.unmodifiableMap(new LinkedHashMap<>(cells));
	}

	public ImportLevel level() {
		return level;
	}

	public int lineNumber() {
		return lineNumber;
	}

	public int lastLineNumber() {
		return subtree().mapToInt(ImportNode::lineNumber).max().orElse(lineNumber);
	}

	public Map<String, String> cells() {
		return cells;
	}

	public String cell(String header) {
		return cells.getOrDefault(header, "");
	}

	public String label() {
		return cells.values().stream().findFirst().orElse("");
	}

	public ImportNode parent() {
		return parent;
	}

	public List<ImportNode> children() {
		return children;
	}

	public List<ImportNode> children(ImportLevel childLevel) {
		return children.stream().filter(child -> child.level == childLevel).collect(Collectors.toList());
	}

	public List<ImportIssueDto> issues() {
		return issues;
	}

	public void reportIssue(String column, String message) {
		issues.add(new ImportIssueDto(ImportIssueKind.BLOCK, lineNumber, column, message));
	}

	public Stream<ImportNode> subtree() {
		return Stream.concat(Stream.of(this), children.stream().flatMap(ImportNode::subtree));
	}

	void addChild(ImportNode child) {
		child.parent = this;
		children.add(child);
	}
}
