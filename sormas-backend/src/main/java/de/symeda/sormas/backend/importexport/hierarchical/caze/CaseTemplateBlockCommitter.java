package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.PERSON;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.SAMPLE;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.TEST;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.BlockCommitter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedBlock;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;

@Stateless
@LocalBean
public class CaseTemplateBlockCommitter implements BlockCommitter {

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@Override
	public void commit(PlannedBlock block) {
		RequestContextHolder.runInImportContext(() -> save(block.rootRecord()));
	}

	private void save(PlannedRecord caseRecord) {
		if (caseRecord.operation() != OperationType.UNCHANGED) {
			PersonHelper.sanitizeNonHomeAddress(caseRecord.target(PERSON));
			PersonDto person = personFacade.save(caseRecord.<PersonDto> target(PERSON));
			CaseDataDto caze = caseRecord.target(CASE);
			caze.setPerson(person.toReference());
			// saving the person re-saves its cases, the fingerprint already guards drift
			caze.setChangeDate(new Date());
			if (caze.getFollowUpStatus() == null && featureConfigurationFacade.isFeatureEnabled(FeatureType.CASE_FOLLOWUP)) {
				caze.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
			}
			caseFacade.save(caze);
		}
		for (PlannedRecord sampleRecord : caseRecord.children()) {
			if (sampleRecord.operation() != OperationType.UNCHANGED) {
				sampleFacade.saveSample(sampleRecord.target(SAMPLE));
			}
			for (PlannedRecord testRecord : sampleRecord.children()) {
				if (testRecord.operation() != OperationType.UNCHANGED) {
					pathogenTestFacade.savePathogenTest(testRecord.target(TEST));
				}
			}
		}
	}
}
