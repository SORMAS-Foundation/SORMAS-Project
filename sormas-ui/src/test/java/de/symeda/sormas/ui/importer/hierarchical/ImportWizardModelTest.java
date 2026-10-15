package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockResultDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

class ImportWizardModelTest {

	private static final ImportSheet SHEET = new ImportSheet(List.of("case.uuid"), List.of());

	@Test
	void startsWaitingAndRefusesToCommitBeforeAPreview() {
		ImportWizardModel model = new ImportWizardModel();

		assertEquals(ImportWizardModel.Step.WAITING_FOR_FILE, model.step());
		assertFalse(model.canCommit());
		assertThrows(IllegalStateException.class, () -> model.commitChunks(10));
	}

	@Test
	void isBusyOnlyWhileAPreviewOrCommitRuns() {
		ImportWizardModel model = new ImportWizardModel();
		assertFalse(model.isBusy());

		model.previewing();
		assertTrue(model.isBusy());
		model.previewed(SHEET, preview(2));
		assertFalse(model.isBusy());

		model.commitChunks(1);
		assertTrue(model.isBusy());
		model.finished();
		assertFalse(model.isBusy());
	}

	@Test
	void aFailedPreviewReturnsToWaitingForAFile() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewing();

		model.previewFailed();

		assertEquals(ImportWizardModel.Step.WAITING_FOR_FILE, model.step());
		assertFalse(model.isBusy());
	}

	@Test
	void splitsReadyBlocksIntoOrderedChunks() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewed(SHEET, preview(5));

		List<Map<Integer, String>> chunks = model.commitChunks(2);

		assertEquals(ImportWizardModel.Step.COMMITTING, model.step());
		assertEquals(3, chunks.size());
		assertEquals(List.of(0, 1), new ArrayList<>(chunks.get(0).keySet()));
		assertEquals(List.of(4), new ArrayList<>(chunks.get(2).keySet()));
	}

	@Test
	void combinesChunkReportsAndFinishes() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewed(SHEET, preview(2));
		model.commitChunks(1);
		model.chunkCommitted(new ImportReportDto(List.of(new BlockResultDto(0, BlockOutcome.COMMITTED, List.of()))));
		model.chunkCommitted(new ImportReportDto(List.of(new BlockResultDto(1, BlockOutcome.FAILED, List.of()))));
		model.finished();

		assertEquals(ImportWizardModel.Step.DONE, model.step());
		assertEquals(1, model.report().count(BlockOutcome.COMMITTED));
		assertEquals(1, model.failedBlocks().size());
	}

	@Test
	void remembersACancelRequestUntilTheNextPreview() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewed(SHEET, preview(1));
		model.commitChunks(1);

		model.requestCancel();
		assertTrue(model.isCancelRequested());
		model.finished();
		model.previewed(SHEET, preview(1));

		assertFalse(model.isCancelRequested());
	}

	@Test
	void aNewPreviewForgetsThePreviousFile() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewed(SHEET, preview(1));

		model.previewing();

		assertNull(model.preview());
		assertNull(model.sheet());
	}

	@Test
	void countsTheReadyBlocksACancelLeftOut() {
		ImportWizardModel model = new ImportWizardModel();
		model.previewed(SHEET, preview(5));
		model.commitChunks(2);
		model.chunkCommitted(
			new ImportReportDto(
				List.of(new BlockResultDto(0, BlockOutcome.COMMITTED, List.of()), new BlockResultDto(1, BlockOutcome.COMMITTED, List.of()))));
		model.requestCancel();
		model.finished();

		assertEquals(3, model.notImportedCount());
	}

	@Test
	void listsErrorBlocksFromThePreviewAsFailed() {
		BlockPreviewDto broken = new BlockPreviewDto(0, 2, 4, "A", BlockStatus.ERROR, "", List.of(), List.of());
		ImportWizardModel model = new ImportWizardModel();

		model.previewed(SHEET, new ImportPreviewDto(List.of(), List.of(), List.of(broken)));

		assertEquals(1, model.failedBlocks().size());
	}

	private static ImportPreviewDto preview(int readyBlocks) {
		List<BlockPreviewDto> blocks = new ArrayList<>();
		for (int i = 0; i < readyBlocks; i++) {
			blocks.add(new BlockPreviewDto(i, i + 2, i + 2, "b" + i, BlockStatus.READY, "fp" + i, List.of(), List.of()));
		}
		return new ImportPreviewDto(List.of(), List.of(), blocks);
	}
}
