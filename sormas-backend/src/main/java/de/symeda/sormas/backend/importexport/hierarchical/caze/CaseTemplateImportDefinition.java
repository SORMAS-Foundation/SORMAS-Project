package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.BlockCommitter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportDefinition;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportLevel;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportSchema;
import de.symeda.sormas.backend.importexport.hierarchical.engine.LevelHandler;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.CurrentUserService;

@Stateless
@LocalBean
public class CaseTemplateImportDefinition implements ImportDefinition {

	@EJB
	private CaseTemplateCellAdapter cellWriter;
	@EJB
	private CaseTemplateBlockCommitter committer;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private CurrentUserService currentUserService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public String id() {
		return HierarchicalImportFacade.CASE_TEMPLATE;
	}

	@Override
	public ImportSchema schema() {
		return CaseTemplateSchema.SCHEMA;
	}

	@Override
	public List<ColumnSpec> columns() {
		return CaseTemplateSchema.columns(importFacade.caseImportColumns(featureConfigurationFacade.getActiveServerFeatureConfigurations()));
	}

	@Override
	public Optional<ColumnSpec> resolveColumn(String header) {
		return CaseTemplateSchema.resolve(header);
	}

	@Override
	public boolean ignoresUnknownColumns() {
		return true;
	}

	@Override
	public List<ImportIssueDto> fileIssues(ImportSheet sheet) {
		List<String> headers = sheet.getHeaders();
		for (int i = 0; i < headers.size(); i++) {
			if (!CaseTemplateLayout.VACCINATION_ENTITY.equals(CaseTemplateLayout.entityOf(headers.get(i)))) {
				continue;
			}
			int index = i;
			if (sheet.getRows().stream().anyMatch(row -> !row.cell(index).isEmpty())) {
				return List.of(
					new ImportIssueDto(
						ImportIssueKind.FILE,
						null,
						headers.get(i),
						I18nProperties.getValidationError(Validations.importHierarchicalVaccinationNotNested)));
			}
		}
		return List.of();
	}

	@Override
	public CellWriter cellWriter() {
		return cellWriter;
	}

	@Override
	public LevelHandler handler(ImportLevel level) {
		if (level == CaseTemplateSchema.CASE_LEVEL) {
			return new CaseHandler(caseFacade, personFacade, caseService, currentUserService, configFacade.getNameSimilarityThreshold());
		}
		if (level == CaseTemplateSchema.SAMPLE_LEVEL) {
			return new SampleHandler(sampleFacade, currentUserService);
		}
		if (level == CaseTemplateSchema.TEST_LEVEL) {
			return new TestHandler(pathogenTestFacade, currentUserService);
		}
		throw new IllegalArgumentException(level.name() + " has no handler");
	}

	@Override
	public BlockCommitter committer() {
		return committer;
	}
}
