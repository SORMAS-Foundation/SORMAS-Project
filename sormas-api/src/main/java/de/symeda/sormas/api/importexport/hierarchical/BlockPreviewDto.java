package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BlockPreviewDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int blockIndex;
	private final int firstLine;
	private final int lastLine;
	private final String label;
	private final BlockStatus status;
	private final String fingerprint;
	private final List<RecordOperationDto> operations;
	private final List<ImportIssueDto> issues;

	public BlockPreviewDto(
		int blockIndex,
		int firstLine,
		int lastLine,
		String label,
		BlockStatus status,
		String fingerprint,
		List<RecordOperationDto> operations,
		List<ImportIssueDto> issues) {
		this.blockIndex = blockIndex;
		this.firstLine = firstLine;
		this.lastLine = lastLine;
		this.label = label;
		this.status = status;
		this.fingerprint = fingerprint;
		this.operations = Collections.unmodifiableList(new ArrayList<>(operations));
		this.issues = Collections.unmodifiableList(new ArrayList<>(issues));
	}

	public int getBlockIndex() {
		return blockIndex;
	}

	public int getFirstLine() {
		return firstLine;
	}

	public int getLastLine() {
		return lastLine;
	}

	public String getLabel() {
		return label;
	}

	public BlockStatus getStatus() {
		return status;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public List<RecordOperationDto> getOperations() {
		return operations;
	}

	public List<ImportIssueDto> getIssues() {
		return issues;
	}
}
