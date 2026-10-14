package de.symeda.sormas.backend.importexport.hierarchical;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateImportDefinition;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportDefinition;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportExecutor;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Planner;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PreviewMapper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "HierarchicalImportFacade")
@RightsAllowed(UserRight._CASE_IMPORT)
public class HierarchicalImportFacadeEjb implements HierarchicalImportFacade {

	@EJB
	private CaseTemplateImportDefinition caseTemplateImportDefinition;
	@EJB
	private RequiresNewBlockRunner blockRunner;

	@Override
	public ImportPreviewDto checkImport(String definitionId, ImportSheet sheet) {
		ImportDefinition definition = definition(definitionId);
		return PreviewMapper.toPreview(definition, new Planner().plan(definition, sheet, null));
	}

	@Override
	public ImportReportDto saveImport(String definitionId, ImportSheet sheet, Map<Integer, String> blockFingerprints) {
		return new ImportExecutor().commit(definition(definitionId), sheet, blockFingerprints, blockRunner);
	}

	private ImportDefinition definition(String definitionId) {
		if (caseTemplateImportDefinition.id().equals(definitionId)) {
			return caseTemplateImportDefinition;
		}
		throw new IllegalArgumentException("Unknown import definition " + definitionId);
	}

	@Stateless
	public static class HierarchicalImportFacadeEjbLocal extends HierarchicalImportFacadeEjb {
	}
}
