package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImportPreviewDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<ImportIssueDto> fileIssues;
	private final List<ImportIssueDto> rowIssues;
	private final List<BlockPreviewDto> blocks;
	private final List<String> ignoredColumns;

	public ImportPreviewDto(List<ImportIssueDto> fileIssues, List<ImportIssueDto> rowIssues, List<BlockPreviewDto> blocks) {
		this(fileIssues, rowIssues, blocks, List.of());
	}

	public ImportPreviewDto(List<ImportIssueDto> fileIssues, List<ImportIssueDto> rowIssues, List<BlockPreviewDto> blocks, List<String> ignoredColumns) {
		this.ignoredColumns = Collections.unmodifiableList(new ArrayList<>(ignoredColumns));
		this.fileIssues = Collections.unmodifiableList(new ArrayList<>(fileIssues));
		this.rowIssues = Collections.unmodifiableList(new ArrayList<>(rowIssues));
		this.blocks = Collections.unmodifiableList(new ArrayList<>(blocks));
	}

	public List<ImportIssueDto> getFileIssues() {
		return fileIssues;
	}

	public List<ImportIssueDto> getRowIssues() {
		return rowIssues;
	}

	public List<BlockPreviewDto> getBlocks() {
		return blocks;
	}

	public List<String> getIgnoredColumns() {
		return ignoredColumns;
	}

	public boolean canImport() {
		return fileIssues.isEmpty() && countBlocks(BlockStatus.READY) > 0;
	}

	public long countBlocks(BlockStatus status) {
		return blocks.stream().filter(block -> block.getStatus() == status).count();
	}

	public long countOperations(String level, OperationType type) {
		return blocks.stream()
			.filter(block -> block.getStatus() != BlockStatus.ERROR)
			.flatMap(block -> block.getOperations().stream())
			.filter(operation -> operation.getLevel().equals(level) && operation.getType() == type)
			.count();
	}

	public Map<Integer, String> readyFingerprints() {
		Map<Integer, String> fingerprints = new HashMap<>();
		blocks.stream()
			.filter(block -> block.getStatus() == BlockStatus.READY)
			.forEach(block -> fingerprints.put(block.getBlockIndex(), block.getFingerprint()));
		return fingerprints;
	}

	public List<ImportIssueDto> allIssues() {
		List<ImportIssueDto> issues = new ArrayList<>(fileIssues);
		issues.addAll(rowIssues);
		blocks.forEach(block -> issues.addAll(block.getIssues()));
		return issues;
	}
}
