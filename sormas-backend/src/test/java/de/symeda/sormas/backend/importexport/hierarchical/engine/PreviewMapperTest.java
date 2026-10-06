package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;

class PreviewMapperTest {

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
}
