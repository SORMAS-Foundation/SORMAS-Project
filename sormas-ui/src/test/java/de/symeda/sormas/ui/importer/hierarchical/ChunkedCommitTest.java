package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;

class ChunkedCommitTest {

	private static final List<Map<Integer, String>> CHUNKS = List.of(Map.of(0, "a"), Map.of(1, "b"), Map.of(2, "c"));

	@Test
	void reportsEachChunkWithItsProgress() {
		List<Float> progress = new ArrayList<>();

		Optional<RuntimeException> failure =
			ChunkedCommit.run(CHUNKS, chunk -> new ImportReportDto(List.of()), (report, done) -> progress.add(done), () -> false);

		assertTrue(failure.isEmpty());
		assertEquals(List.of(1f / 3, 2f / 3, 1f), progress);
	}

	@Test
	void stopsAndReturnsTheFailureWhenACommitThrows() {
		List<Float> progress = new ArrayList<>();

		Optional<RuntimeException> failure = ChunkedCommit.run(CHUNKS, chunk -> {
			if (chunk.containsKey(1)) {
				throw new IllegalStateException("server down");
			}
			return new ImportReportDto(List.of());
		}, (report, done) -> progress.add(done), () -> false);

		assertEquals("server down", failure.get().getMessage());
		assertEquals(1, progress.size());
	}

	@Test
	void stopsBetweenChunksWhenCancelled() {
		List<Float> progress = new ArrayList<>();

		ChunkedCommit.run(CHUNKS, chunk -> new ImportReportDto(List.of()), (report, done) -> progress.add(done), () -> !progress.isEmpty());

		assertEquals(1, progress.size());
	}
}
