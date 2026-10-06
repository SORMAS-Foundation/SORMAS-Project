package de.symeda.sormas.backend.importexport.hierarchical.lab;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
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
public class LabImportDefinition implements ImportDefinition {

	@EJB
	private LabCellWriter cellWriter;
	@EJB
	private LabBlockCommitter committer;
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

	@Override
	public String id() {
		return HierarchicalImportFacade.LAB_CASES_SAMPLES_TESTS;
	}

	@Override
	public ImportSchema schema() {
		return LabImportSchema.SCHEMA;
	}

	@Override
	public List<ColumnSpec> columns() {
		return LabImportSchema.columns();
	}

	@Override
	public CellWriter cellWriter() {
		return cellWriter;
	}

	@Override
	public LevelHandler handler(ImportLevel level) {
		if (level == LabImportSchema.CASE_LEVEL) {
			return new CaseHandler(caseFacade, personFacade, caseService, currentUserService);
		}
		if (level == LabImportSchema.SAMPLE_LEVEL) {
			return new SampleHandler(sampleFacade, currentUserService);
		}
		if (level == LabImportSchema.TEST_LEVEL) {
			return new TestHandler(pathogenTestFacade, currentUserService, new AstFolder(cellWriter));
		}
		throw new IllegalArgumentException(level.name() + " has no handler");
	}

	@Override
	public BlockCommitter committer() {
		return committer;
	}
}
