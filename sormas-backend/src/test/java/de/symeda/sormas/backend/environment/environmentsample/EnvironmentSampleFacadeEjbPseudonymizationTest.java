package de.symeda.sormas.backend.environment.environmentsample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EnvironmentSampleFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private FacilityDto lab1;
	private FacilityDto lab2;
	private UserDto reportingUser;
	UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities;
	private EnvironmentDto environment1;
	private EnvironmentDto environment2;
	private EnvironmentDto environment3;
	private EnvironmentDto environment4;

	private TestDataCreator.RDCF rdcf2;
	private UserDto userInDifferentJurisdiction;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF();
		rdcf2 = creator.createRDCF();
		lab1 = creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community, FacilityType.LABORATORY);
		lab2 = creator.createFacility("Lab2", rdcf2.region, rdcf2.district, rdcf2.community, FacilityType.LABORATORY);

		reportingUser = creator.createUser(rdcf1, creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));
		userInDifferentJurisdiction =
			creator.createUser(rdcf2, "Env", "Surv2", creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));
		surveillanceOfficerWithRestrictedAccessToAssignedEntities = creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		environment1 = creator.createEnvironment("Test env", EnvironmentMedia.WATER, reportingUser.toReference(), rdcf1);
		environment2 = creator.createEnvironment("Test environment 2", EnvironmentMedia.AIR, userInDifferentJurisdiction.toReference(), rdcf2);
		environment3 = creator.createEnvironment(
			"Test environment 3",
			EnvironmentMedia.AIR,
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf2);
		environment4 = creator.createEnvironment(
			"Test environment 4",
			EnvironmentMedia.AIR,
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf2);
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() {

		// environment within limited user's jurisdiction
		EnvironmentSampleDto sample1 =
			creator.createEnvironmentSample(environment1.toReference(), reportingUser.toReference(), rdcf1, lab1.toReference(), s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material");
				s.setFieldSampleId("Test id");
				s.setLaboratoryDetails("Test lab details");
				s.setOtherRequestedPathogenTests("Test pathogen tests");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details");
				s.setLabSampleId("Test lab sample id");
				s.setGeneralComment("Test comment");
				s.getLocation().setCity("Test city");
			});

		// environment outside limited user's jurisdiction
		EnvironmentSampleDto sample2 =
			creator.createEnvironmentSample(environment2.toReference(), userInDifferentJurisdiction.toReference(), rdcf2, lab2.toReference(), s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material second");
				s.setFieldSampleId("Test id second");
				s.setLaboratoryDetails("Test lab details second");
				s.setOtherRequestedPathogenTests("Test pathogen tests second");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details second");
				s.setLabSampleId("Test lab sample id second");
				s.setGeneralComment("Test comment second");
				s.getLocation().setCity("Test city second");
			});

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		EnvironmentSampleDto testSample1 = getEnvironmentSampleFacade().getByUuid(sample1.getUuid());
		assertThat(testSample1.isPseudonymized(), is(true));
		assertThat(testSample1.getOtherSampleMaterial(), is(emptyString()));
		EnvironmentSampleDto testSample2 = getEnvironmentSampleFacade().getByUuid(sample2.getUuid());
		assertThat(testSample2.isPseudonymized(), is(true));
		assertThat(testSample2.getOtherSampleMaterial(), is(emptyString()));

		// environment within limited user's jurisdiction
		loginWith(nationalAdmin);
		EnvironmentSampleDto sample3 = creator.createEnvironmentSample(
			environment3.toReference(),
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf1,
			lab1.toReference(),
			s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material third");
				s.setFieldSampleId("Test id third");
				s.setLaboratoryDetails("Test lab details third");
				s.setOtherRequestedPathogenTests("Test pathogen tests third");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details third");
				s.setLabSampleId("Test lab sample id third");
				s.setGeneralComment("Test comment third");
				s.getLocation().setCity("Test city third");
			});

		// environment outside limited user's jurisdiction
		EnvironmentSampleDto sample4 = creator.createEnvironmentSample(
			environment4.toReference(),
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf2,
			lab2.toReference(),
			s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material fourth");
				s.setFieldSampleId("Test id fourth");
				s.setLaboratoryDetails("Test lab details fourth");
				s.setOtherRequestedPathogenTests("Test pathogen tests fourth");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details fourth");
				s.setLabSampleId("Test lab sample id fourth");
				s.setGeneralComment("Test comment fourth");
				s.getLocation().setCity("Test city fourth");
			});

//		loginWith(nationalAdmin);
		environment1.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment1);
		environment2.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		EnvironmentSampleDto returnTestSample1 = getEnvironmentSampleFacade().getByUuid(sample1.getUuid());
		assertThat(returnTestSample1.isPseudonymized(), is(false));
		assertThat(returnTestSample1.getOtherSampleMaterial(), is("Test material"));

		EnvironmentSampleDto returnTestSample2 = getEnvironmentSampleFacade().getByUuid(sample2.getUuid());
		assertThat(returnTestSample2.isPseudonymized(), is(false));
		assertThat(returnTestSample2.getOtherSampleMaterial(), is("Test material second"));

		EnvironmentSampleDto returnTestSample3 = getEnvironmentSampleFacade().getByUuid(sample3.getUuid());
		assertThat(returnTestSample3.isPseudonymized(), is(false));
		assertThat(returnTestSample3.getOtherSampleMaterial(), is("Test material third"));

		EnvironmentSampleDto returnTestSample4 = getEnvironmentSampleFacade().getByUuid(sample4.getUuid());
		assertThat(returnTestSample4.isPseudonymized(), is(false));
		assertThat(returnTestSample4.getOtherSampleMaterial(), is("Test material fourth"));
	}
}
