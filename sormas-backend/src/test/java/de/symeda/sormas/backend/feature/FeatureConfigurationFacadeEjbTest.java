package de.symeda.sormas.backend.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class FeatureConfigurationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testIsTaskGenerationFeatureEnabled() {

		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.TASK_GENERATION_CASE_SURVEILLANCE);

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR)).toReference();
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
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.CASE, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);
		assertEquals(90, (int) defaultDaysForCaseArchiving);
	}

	@Test
	public void testGetPropertyWithWrongPropertyType() {
		assertThrows(
			IllegalArgumentException.class,
			() -> getFeatureConfigurationFacade()
				.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.CASE, FeatureTypeProperty.THRESHOLD_IN_DAYS, Boolean.class));
	}

	@Test
	public void testGetPropertyWhenFeatureTypeDoesNotContainIt() {
		assertThrows(
			IllegalArgumentException.class,
			() -> getFeatureConfigurationFacade()
				.getProperty(FeatureType.CASE_SURVEILANCE, DeletableEntityType.CASE, FeatureTypeProperty.THRESHOLD_IN_DAYS, Boolean.class));
	}
}
