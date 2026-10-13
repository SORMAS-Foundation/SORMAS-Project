package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockResultDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

class PreviewPresentationTest {

	private static final ImportIssueDto BAD_DATE = new ImportIssueDto(ImportIssueKind.BLOCK, 4, "test.testDateTime", "bad date");

	@Test
	void aCleanNewImportHasNothingToReview() {
		ImportPreviewDto preview = preview(ready(0, create("case", "NI-001", 2), create("sample", "S1", 2)));

		PreviewPresentation presentation = new PreviewPresentation(preview, emptyReport());

		assertTrue(presentation.nothingToReview());
		assertEquals(List.of(), presentation.problems());
		assertEquals(List.of(), presentation.changes());
		assertEquals(2, presentation.newRecords().size());
	}

	@Test
	void listsChangesOnlyFromReadyBlocks() {
		RecordOperationDto update =
			new RecordOperationDto("sample", OperationType.UPDATE, "S-1", 3, List.of(new FieldChangeDto("sample.sampleMaterial", "Serum", "Blood")));
		BlockPreviewDto broken = new BlockPreviewDto(1, 5, 5, "B", BlockStatus.ERROR, "", List.of(update), List.of(BAD_DATE));

		PreviewPresentation presentation = new PreviewPresentation(preview(ready(0, update), broken), emptyReport());

		assertFalse(presentation.nothingToReview());
		assertEquals(1, presentation.changes().size());
		assertEquals("S-1", presentation.changes().get(0).getRecord());
		assertEquals("Serum → Blood", presentation.changes().get(0).getChange());
	}

	@Test
	void newRecordsComeOnlyFromReadyBlocks() {
		BlockPreviewDto broken = new BlockPreviewDto(1, 5, 5, "B", BlockStatus.ERROR, "", List.of(create("case", "NI-002", 5)), List.of(BAD_DATE));

		PreviewPresentation presentation = new PreviewPresentation(preview(ready(0, create("case", "NI-001", 2)), broken), emptyReport());

		assertEquals(1, presentation.newRecords().size());
		assertEquals("NI-001", presentation.newRecords().get(0).getLabel());
	}

	@Test
	void problemsIncludeCommitFailuresFromTheReport() {
		ImportIssueDto commitFailure = new ImportIssueDto(ImportIssueKind.COMMIT, 2, null, "changed since the preview");
		ImportReportDto report = new ImportReportDto(List.of(new BlockResultDto(0, BlockOutcome.CHANGED_SINCE_PREVIEW, List.of(commitFailure))));

		PreviewPresentation presentation = new PreviewPresentation(preview(ready(0, create("case", "NI-001", 2))), report);

		assertEquals(List.of(commitFailure), presentation.problems());
		assertFalse(presentation.nothingToReview());
	}

	@Test
	void sizesGridsToTheirRowsWithinBounds() {
		assertEquals(1, PreviewPresentation.gridRows(0));
		assertEquals(3, PreviewPresentation.gridRows(3));
		assertEquals(8, PreviewPresentation.gridRows(250));
	}

	private static ImportPreviewDto preview(BlockPreviewDto... blocks) {
		return new ImportPreviewDto(List.of(), List.of(), List.of(blocks));
	}

	private static BlockPreviewDto ready(int index, RecordOperationDto... operations) {
		return new BlockPreviewDto(index, 2, 2, "block", BlockStatus.READY, "fp", List.of(operations), List.of());
	}

	private static RecordOperationDto create(String level, String label, int line) {
		return new RecordOperationDto(level, OperationType.CREATE, label, line, List.of());
	}

	private static ImportReportDto emptyReport() {
		return new ImportReportDto(List.of());
	}
}
