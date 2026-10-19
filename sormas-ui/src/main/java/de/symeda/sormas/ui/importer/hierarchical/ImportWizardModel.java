package de.symeda.sormas.ui.importer.hierarchical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockResultDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

public final class ImportWizardModel {

	public enum Step {
		WAITING_FOR_FILE,
		PREVIEWING,
		PREVIEWED,
		COMMITTING,
		DONE
	}

	private static final Set<BlockOutcome> FAILED_OUTCOMES = Set.of(BlockOutcome.FAILED, BlockOutcome.CHANGED_SINCE_PREVIEW);

	private Step step = Step.WAITING_FOR_FILE;
	private ImportSheet sheet;
	private ImportPreviewDto preview;
	private final List<ImportReportDto> reports = new ArrayList<>();
	private volatile boolean cancelRequested;

	public Step step() {
		return step;
	}

	public ImportSheet sheet() {
		return sheet;
	}

	public ImportPreviewDto preview() {
		return preview;
	}

	public void previewing() {
		require(!isBusy());
		sheet = null;
		preview = null;
		reports.clear();
		step = Step.PREVIEWING;
	}

	public void previewFailed() {
		require(step == Step.PREVIEWING);
		sheet = null;
		preview = null;
		step = Step.WAITING_FOR_FILE;
	}

	public boolean isBusy() {
		return step == Step.PREVIEWING || step == Step.COMMITTING;
	}

	public void previewed(ImportSheet sheet, ImportPreviewDto preview) {
		require(step != Step.COMMITTING);
		this.sheet = sheet;
		this.preview = preview;
		reports.clear();
		cancelRequested = false;
		step = Step.PREVIEWED;
	}

	public boolean canCommit() {
		return step == Step.PREVIEWED && preview.canImport();
	}

	public List<Map<Integer, String>> commitChunks(int chunkSize) {
		require(canCommit());
		step = Step.COMMITTING;
		Map<Integer, String> ready = preview.readyFingerprints();
		List<Map<Integer, String>> chunks = new ArrayList<>();
		Map<Integer, String> chunk = new LinkedHashMap<>();
		for (List<Integer> group : blocksCommittedTogether(ready.keySet())) {
			group.forEach(index -> chunk.put(index, ready.get(index)));
			if (chunk.size() >= chunkSize) {
				chunks.add(new LinkedHashMap<>(chunk));
				chunk.clear();
			}
		}
		if (!chunk.isEmpty()) {
			chunks.add(chunk);
		}
		return chunks;
	}

	private Collection<List<Integer>> blocksCommittedTogether(Set<Integer> readyBlocks) {
		Map<Integer, Integer> blockByFirstLine =
			preview.getBlocks().stream().collect(Collectors.toMap(BlockPreviewDto::getFirstLine, BlockPreviewDto::getBlockIndex, (first, second) -> first));
		Map<Integer, List<Integer>> groups = new TreeMap<>();
		preview.getBlocks()
			.stream()
			.filter(block -> readyBlocks.contains(block.getBlockIndex()))
			.sorted(Comparator.comparing(BlockPreviewDto::getBlockIndex))
			.forEach(block -> {
				Integer sharedBlock = linkedLine(block).map(blockByFirstLine::get).filter(groups::containsKey).orElse(block.getBlockIndex());
				groups.computeIfAbsent(sharedBlock, key -> new ArrayList<>()).add(block.getBlockIndex());
			});
		return groups.values();
	}

	private static Optional<Integer> linkedLine(BlockPreviewDto block) {
		return block.getOperations().stream().findFirst().map(RecordOperationDto::getLinkedLine);
	}

	public void chunkCommitted(ImportReportDto report) {
		require(step == Step.COMMITTING);
		reports.add(report);
	}

	public void requestCancel() {
		cancelRequested = true;
	}

	public boolean isCancelRequested() {
		return cancelRequested;
	}

	public void finished() {
		require(step == Step.COMMITTING);
		step = Step.DONE;
	}

	public long notImportedCount() {
		return preview.readyFingerprints().size() - report().getResults().size();
	}

	public ImportReportDto report() {
		return ImportReportDto.combine(reports);
	}

	public List<ErrorFile.FailedBlock> failedBlocks() {
		Map<Integer, BlockPreviewDto> blocksByIndex =
			preview.getBlocks().stream().collect(Collectors.toMap(BlockPreviewDto::getBlockIndex, block -> block));
		List<ErrorFile.FailedBlock> failed = new ArrayList<>();
		preview.getBlocks()
			.stream()
			.filter(block -> block.getStatus() == BlockStatus.ERROR)
			.forEach(block -> failed.add(new ErrorFile.FailedBlock(block.getFirstLine(), block.getLastLine(), block.getIssues())));
		for (BlockResultDto result : report().getResults()) {
			BlockPreviewDto block = blocksByIndex.get(result.getBlockIndex());
			if (FAILED_OUTCOMES.contains(result.getOutcome()) && block != null) {
				failed.add(new ErrorFile.FailedBlock(block.getFirstLine(), block.getLastLine(), result.getIssues()));
			}
		}
		return failed;
	}

	public List<ImportIssueDto> looseIssues() {
		List<ImportIssueDto> issues = new ArrayList<>(preview.getFileIssues());
		issues.addAll(preview.getRowIssues());
		return issues.stream().filter(issue -> issue.getLineNumber() != null).collect(Collectors.toList());
	}

	private static void require(boolean condition) {
		if (!condition) {
			throw new IllegalStateException("Import step not allowed now");
		}
	}
}
