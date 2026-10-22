package de.symeda.sormas.ui.importer.hierarchical;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;

public final class ChunkedCommit {

	public interface ChunkCommitter {

		ImportReportDto commit(Map<Integer, String> chunk);
	}

	private ChunkedCommit() {
	}

	public static Optional<RuntimeException> run(
		List<Map<Integer, String>> chunks,
		ChunkCommitter committer,
		BiConsumer<ImportReportDto, Float> onChunk,
		BooleanSupplier cancelled) {
		for (int i = 0; i < chunks.size() && !cancelled.getAsBoolean(); i++) {
			ImportReportDto report;
			try {
				report = committer.commit(chunks.get(i));
			} catch (RuntimeException e) {
				return Optional.of(e);
			}
			onChunk.accept(report, (float) (i + 1) / chunks.size());
		}
		return Optional.empty();
	}
}
