package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BlockResultDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int blockIndex;
	private final BlockOutcome outcome;
	private final List<ImportIssueDto> issues;

	public BlockResultDto(int blockIndex, BlockOutcome outcome, List<ImportIssueDto> issues) {
		this.blockIndex = blockIndex;
		this.outcome = outcome;
		this.issues = Collections.unmodifiableList(new ArrayList<>(issues));
	}

	public int getBlockIndex() {
		return blockIndex;
	}

	public BlockOutcome getOutcome() {
		return outcome;
	}

	public List<ImportIssueDto> getIssues() {
		return issues;
	}
}
