package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

class PreviewMapperTest {

	private static final Date V1 = new Date(1_000L);

	@Test
	void mapsBlocksWithLabelsLineRangesAndOperations() {
		ToyDefinition toy = new ToyDefinition();
		ImportPreviewDto preview = PreviewMapper.toPreview(
			toy,
			new Planner().plan(toy, SheetFixtures.table(ToyDefinition.HEADER, "| P1 | Alpha | C1 | 1 | |", "| | | C2 | 2 | |", "| | | | | x |"), null));

		BlockPreviewDto block = preview.getBlocks().get(0);
		assertEquals("Alpha", block.getLabel());
		assertEquals(2, block.getFirstLine());
		assertEquals(4, block.getLastLine());
		assertEquals(BlockStatus.READY, block.getStatus());
		assertEquals(2, preview.countOperations("child", OperationType.CREATE));
		assertEquals("C2", block.getOperations().get(2).getLabel());
	}

	@Test
	void carriesTheRootsMatchReasonOnly() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1);
		ImportPlan plan = new Planner().plan(toy, SheetFixtures.table("| parent.key | child.code | child.amount |", "| P1 | C9 | 2 |"), null);

		List<RecordOperationDto> operations = PreviewMapper.toPreview(toy, plan).getBlocks().get(0).getOperations();

		assertEquals(MatchReason.BY_ID, operations.get(0).getMatchReason());
		assertNull(operations.get(1).getMatchReason());
	}

	@Test
	void aBlockRangeIncludesATrailingOrphanRow() {
		ToyDefinition toy = new ToyDefinition();
		ImportPreviewDto preview = PreviewMapper
			.toPreview(toy, new Planner().plan(toy, SheetFixtures.table(ToyDefinition.HEADER, "| P1 | Alpha | | | |", "| | | | | stray |"), null));

		assertEquals(3, preview.getBlocks().get(0).getLastLine());
	}
}
