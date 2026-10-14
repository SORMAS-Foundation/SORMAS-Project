package de.symeda.sormas.api.importexport.hierarchical;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface HierarchicalImportFacade {

	String CASE_TEMPLATE = "caseTemplate";

	ImportPreviewDto dryRun(String definitionId, ImportSheet sheet);

	ImportReportDto commit(String definitionId, ImportSheet sheet, Map<Integer, String> blockFingerprints);
}
