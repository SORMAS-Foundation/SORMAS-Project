package de.symeda.sormas.backend.feature;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class FeatureConfigurationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testIsTaskGenerationFeatureEnabled() throws Exception {
		FeatureConfigurationIndexDto featureConfiguration = new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.TASK_GENERATION_CASE_SURVEILLANCE);
		
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR).toReference();
		PersonReferenceDto person = creator.createPerson("Case", "Person").toReference();
		
		CaseDataDto caze = creator.createCase(user, person, rdcf);
		
		List<TaskDto> caseTasks = getTaskFacade().getAllPendingByCase(caze.toReference());
		assertEquals(0, caseTasks.size());
	}

}
