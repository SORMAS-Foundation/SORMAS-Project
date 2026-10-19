package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;

public final class PlannedBlock {

	private final int index;
	private final ImportNode root;
	private final PlannedRecord rootRecord;
	private final List<ImportIssueDto> issues;
	private final String fingerprint;

	PlannedBlock(int index, ImportNode root, PlannedRecord rootRecord) {
		this.index = index;
		this.root = root;
		this.rootRecord = rootRecord;
		this.issues = root.subtree().flatMap(node -> node.issues().stream()).collect(Collectors.toList());
		this.fingerprint = issues.isEmpty() && rootRecord != null ? Fingerprint.of(rootRecord) : "";
	}

	public int index() {
		return index;
	}

	public ImportNode root() {
		return root;
	}

	public PlannedRecord rootRecord() {
		return rootRecord;
	}

	public List<ImportIssueDto> issues() {
		return issues;
	}

	public String fingerprint() {
		return fingerprint;
	}

	public BlockStatus status() {
		if (!issues.isEmpty() || rootRecord == null) {
			return BlockStatus.ERROR;
		}
		return records().allMatch(record -> record.operation() == OperationType.UNCHANGED) ? BlockStatus.UNCHANGED : BlockStatus.READY;
	}

	public Stream<PlannedRecord> records() {
		return rootRecord == null ? Stream.empty() : rootRecord.subtree();
	}
}
