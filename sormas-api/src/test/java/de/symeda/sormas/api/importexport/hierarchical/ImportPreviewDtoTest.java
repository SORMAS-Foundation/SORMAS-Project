package de.symeda.sormas.api.importexport.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ImportPreviewDtoTest {

	private static final ImportIssueDto PROBLEM = new ImportIssueDto(ImportIssueKind.BLOCK, 5, "test.testDateTime", "bad date");

	@Test
	void countsBlocksAndOperationsOutsideErrorBlocks() {
		ImportPreviewDto preview = new ImportPreviewDto(
			List.of(),
			List.of(),
			List.of(
				block(0, BlockStatus.READY, "fp0", List.of(op("case", OperationType.CREATE), op("sample", OperationType.CREATE))),
				block(1, BlockStatus.UNCHANGED, "fp1", List.of(op("case", OperationType.UNCHANGED))),
				block(2, BlockStatus.ERROR, "", List.of(op("case", OperationType.CREATE)))));

		assertEquals(1, preview.countBlocks(BlockStatus.READY));
		assertEquals(1, preview.countBlocks(BlockStatus.ERROR));
		assertEquals(1, preview.countOperations("case", OperationType.CREATE));
		assertEquals(Map.of(0, "fp0"), preview.readyFingerprints());
		assertTrue(preview.canImport());
	}

	@Test
	void cannotImportWithFileIssues() {
		ImportIssueDto fileIssue = new ImportIssueDto(ImportIssueKind.FILE, null, "case.colour", "Unknown column case.colour");
		ImportPreviewDto preview =
			new ImportPreviewDto(List.of(fileIssue), List.of(), List.of(block(0, BlockStatus.READY, "fp0", List.of())));

		assertFalse(preview.canImport());
	}

	@Test
	void collectsAllIssues() {
		ImportIssueDto orphan = new ImportIssueDto(ImportIssueKind.ROW, 2, null, "orphan");
		BlockPreviewDto failed = new BlockPreviewDto(0, 4, 6, "A", BlockStatus.ERROR, "", List.of(), List.of(PROBLEM));

		ImportPreviewDto preview = new ImportPreviewDto(List.of(), List.of(orphan), List.of(failed));

		assertEquals(List.of(orphan, PROBLEM), preview.allIssues());
	}

	private static BlockPreviewDto block(int index, BlockStatus status, String fingerprint, List<RecordOperationDto> operations) {
		return new BlockPreviewDto(index, index + 2, index + 2, "block " + index, status, fingerprint, operations, List.of());
	}

	private static RecordOperationDto op(String level, OperationType type) {
		return new RecordOperationDto(level, type, level, 2, List.of());
	}
}
