package de.symeda.sormas.backend.importexport.hierarchical;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportTemplateDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportDefinition;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportExecutor;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Planner;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PreviewMapper;
import de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportDefinition;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "HierarchicalImportFacade")
@RightsAllowed(UserRight._SAMPLE_CREATE)
public class HierarchicalImportFacadeEjb implements HierarchicalImportFacade {

	@EJB
	private LabImportDefinition labImportDefinition;
	@EJB
	private RequiresNewBlockRunner blockRunner;

	@Override
	public ImportPreviewDto dryRun(String definitionId, ImportSheet sheet) {
		ImportDefinition definition = definition(definitionId);
		return PreviewMapper.toPreview(definition, new Planner().plan(definition, sheet, null));
	}

	@Override
	public ImportReportDto commit(String definitionId, ImportSheet sheet, Map<Integer, String> blockFingerprints) {
		return new ImportExecutor().commit(definition(definitionId), sheet, blockFingerprints, blockRunner);
	}

	@Override
	public ImportTemplateDto getTemplate(String definitionId) {
		List<ColumnSpec> columns = definition(definitionId).columns();
		return new ImportTemplateDto(
			columns.stream().map(ColumnSpec::header).collect(Collectors.toList()),
			columns.stream().map(ColumnSpec::caption).collect(Collectors.toList()));
	}

	private ImportDefinition definition(String definitionId) {
		if (labImportDefinition.id().equals(definitionId)) {
			return labImportDefinition;
		}
		throw new IllegalArgumentException("Unknown import definition " + definitionId);
	}

	@Stateless
	public static class HierarchicalImportFacadeEjbLocal extends HierarchicalImportFacadeEjb {
	}
}
