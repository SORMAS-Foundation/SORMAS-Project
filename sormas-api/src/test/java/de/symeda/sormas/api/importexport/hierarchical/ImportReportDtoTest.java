package de.symeda.sormas.api.importexport.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class ImportReportDtoTest {

	@Test
	void combinesChunkReportsAndCountsOutcomes() {
		ImportReportDto first = new ImportReportDto(
			List.of(new BlockResultDto(0, BlockOutcome.COMMITTED, List.of()), new BlockResultDto(1, BlockOutcome.FAILED, List.of())));
		ImportReportDto second = new ImportReportDto(List.of(new BlockResultDto(2, BlockOutcome.COMMITTED, List.of())));

		ImportReportDto combined = ImportReportDto.combine(List.of(first, second));

		assertEquals(3, combined.getResults().size());
		assertEquals(2, combined.count(BlockOutcome.COMMITTED));
		assertEquals(1, combined.count(BlockOutcome.FAILED));
	}
}
