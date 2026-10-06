package de.symeda.sormas.api.importexport.hierarchical;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface HierarchicalImportFacade {

	String LAB_CASES_SAMPLES_TESTS = "lab-cases-samples-tests";

	ImportPreviewDto dryRun(String definitionId, ImportSheet sheet);

	ImportReportDto commit(String definitionId, ImportSheet sheet, Map<Integer, String> blockFingerprints);

	ImportTemplateDto getTemplate(String definitionId);
}
