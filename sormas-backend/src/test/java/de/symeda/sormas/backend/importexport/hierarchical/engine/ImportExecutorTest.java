package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

class ImportExecutorTest {

	private final AtomicInteger runs = new AtomicInteger();
	private final BlockRunner countingRunner = work -> {
		runs.incrementAndGet();
		work.run();
	};

	@Test
	void commitsEveryPreviewedReadyBlock() {
		ToyDefinition toy = new ToyDefinition();
		ImportSheet sheet = sheet("| P1 | Alpha | C1 | 1 | |", "| P2 | Beta | C1 | 1 | |");

		ImportReportDto report = new ImportExecutor().commit(toy, sheet, preview(toy, sheet).readyFingerprints(), countingRunner);

		assertEquals(2, report.count(BlockOutcome.COMMITTED));
		assertEquals(List.of(0, 1), toy.committed);
		assertEquals(2, runs.get());
	}

	@Test
	void skipsABlockThatChangedSinceThePreview() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", new Date(1_000L));
		ImportSheet sheet = sheet("| P1 | Beta | | | |", "| P2 | Gamma | | | |");
		Map<Integer, String> previewed = preview(toy, sheet).readyFingerprints();
		toy.parentVersions.put("P1", new Date(2_000L));

		ImportReportDto report = new ImportExecutor().commit(toy, sheet, previewed, countingRunner);

		assertEquals(BlockOutcome.CHANGED_SINCE_PREVIEW, report.getResults().get(0).getOutcome());
		assertEquals(BlockOutcome.COMMITTED, report.getResults().get(1).getOutcome());
		assertEquals(List.of(1), toy.committed);
	}

	@Test
	void aFailingBlockDoesNotStopTheNextOne() {
		ToyDefinition toy = new ToyDefinition();
		ImportSheet sheet = sheet("| P1 | explode | | | |", "| P2 | Beta | | | |");

		ImportReportDto report = new ImportExecutor().commit(toy, sheet, preview(toy, sheet).readyFingerprints(), countingRunner);

		assertEquals(BlockOutcome.FAILED, report.getResults().get(0).getOutcome());
		assertEquals("exploded", report.getResults().get(0).getIssues().get(0).getMessage());
		assertEquals(BlockOutcome.COMMITTED, report.getResults().get(1).getOutcome());
	}

	@Test
	void anUnchangedBlockIsNeverCommitted() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", new Date(1_000L));
		ImportSheet sheet = sheet("| P1 | Alpha | | | |");
		String fingerprint = new Planner().plan(toy, sheet, null).blocks().get(0).fingerprint();

		ImportReportDto report = new ImportExecutor().commit(toy, sheet, Map.of(0, fingerprint), countingRunner);

		assertEquals(BlockOutcome.UNCHANGED, report.getResults().get(0).getOutcome());
		assertEquals(0, runs.get());
	}

	private static ImportPreviewDto preview(ToyDefinition toy, ImportSheet sheet) {
		return PreviewMapper.toPreview(toy, new Planner().plan(toy, sheet, null));
	}

	private static ImportSheet sheet(String... rows) {
		String[] lines = new String[rows.length + 1];
		lines[0] = ToyDefinition.HEADER;
		System.arraycopy(rows, 0, lines, 1, rows.length);
		return SheetFixtures.table(lines);
	}
}
