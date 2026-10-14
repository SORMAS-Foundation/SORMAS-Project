package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

public final class PreviewMapper {

	private PreviewMapper() {
	}

	public static ImportPreviewDto toPreview(ImportDefinition definition, ImportPlan plan) {
		List<BlockPreviewDto> blocks = plan.blocks().stream().map(block -> toBlock(definition, block)).collect(Collectors.toList());
		return new ImportPreviewDto(plan.fileIssues(), plan.rowIssues(), blocks, plan.ignoredColumns());
	}

	private static BlockPreviewDto toBlock(ImportDefinition definition, PlannedBlock block) {
		List<RecordOperationDto> operations = block.records()
			.map(
				record -> new RecordOperationDto(
					record.node().level().name(),
					record.operation(),
					definition.handler(record.node().level()).label(record),
					record.node().lineNumber(),
					record.changes(),
					record.parent() == null ? record.reason() : null))
			.collect(Collectors.toList());
		String label =
			block.rootRecord() != null ? definition.handler(block.root().level()).label(block.rootRecord()) : block.root().label();
		return new BlockPreviewDto(
			block.index(),
			block.root().lineNumber(),
			block.root().lastLineNumber(),
			label,
			block.status(),
			block.fingerprint(),
			operations,
			block.issues());
	}
}
