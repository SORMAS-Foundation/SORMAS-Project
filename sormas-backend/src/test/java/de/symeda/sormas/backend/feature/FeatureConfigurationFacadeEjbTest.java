package de.symeda.sormas.backend.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class FeatureConfigurationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testIsTaskGenerationFeatureEnabled() {

		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.TASK_GENERATION_CASE_SURVEILLANCE);

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR).toReference();
		PersonReferenceDto person = creator.createPerson("Case", "Person").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);

		List<TaskDto> caseTasks = getTaskFacade().getAllPendingByCase(caze.toReference());
		assertEquals(0, caseTasks.size());
	}

	@Test
	public void testIsPropertyValue() {

		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.IMMUNIZATION_MANAGEMENT);

		// Check against the default value when the property column is empty
		assertFalse(getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED));

		// TODO: Test for an explicitely added property; currently problematic because H2 has issues with the JSON converting
	}

	@Test
	public void testGetProperty() {
		FeatureConfigurationService featureConfigurationService = getBean(FeatureConfigurationService.class);
		featureConfigurationService.createMissingFeatureConfigurations();

		Integer defaultDaysForCaseArchiving = getFeatureConfigurationFacade()
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, CoreEntityType.CASE, FeatureTypeProperty.DAYS_FOR_AUTOMATIC_ARCHIVING, Integer.class);
		Assert.assertEquals(90, (int) defaultDaysForCaseArchiving);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPropertyWithWrongPropertyType() {
		getFeatureConfigurationFacade()
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, CoreEntityType.CASE, FeatureTypeProperty.DAYS_FOR_AUTOMATIC_ARCHIVING, Boolean.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPropertyWhenFeatureTypeDoesNotContainIt() {
		getFeatureConfigurationFacade()
			.getProperty(FeatureType.CASE_SURVEILANCE, CoreEntityType.CASE, FeatureTypeProperty.DAYS_FOR_AUTOMATIC_ARCHIVING, Boolean.class);
	}
}
