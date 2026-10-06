package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ImportReportDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<BlockResultDto> results;

	public ImportReportDto(List<BlockResultDto> results) {
		this.results = Collections.unmodifiableList(new ArrayList<>(results));
	}

	public static ImportReportDto combine(List<ImportReportDto> reports) {
		return new ImportReportDto(reports.stream().flatMap(report -> report.getResults().stream()).collect(Collectors.toList()));
	}

	public List<BlockResultDto> getResults() {
		return results;
	}

	public long count(BlockOutcome outcome) {
		return results.stream().filter(result -> result.getOutcome() == outcome).count();
	}
}
