package de.symeda.sormas.backend.immunization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;

import javax.persistence.Query;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.feature.FeatureConfiguration;

public class ImmunizationFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto nationalUser;

	@Override
	public void init() {
		super.init();
		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		nationalUser = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.community.getUuid(),
			rdcf1.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		districtUser1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
	}

	@Test
	public void testPseudonymized() {
		loginWith(nationalAdmin);

		// immunization within limited user's jurisdiction
		PersonDto person1 = creator.createPerson("John", "Doe");
		final ImmunizationDto immunization1 = creator.createImmunization(
			Disease.CORONAVIRUS,
			person1.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		immunization1.setAdditionalDetails("confidential details");
		getImmunizationFacade().save(immunization1);

		// immunization outside limited user's jurisdiction
		PersonDto person2 = creator.createPerson("Max", "MUstermann");
		final ImmunizationDto immunization2 = creator.createImmunization(
			Disease.CORONAVIRUS,
			person2.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf2);
		immunization2.setAdditionalDetails("confidential details second");
		getImmunizationFacade().save(immunization2);

		loginWith(districtUser1);
		ImmunizationDto testImmunization3 = getImmunizationFacade().getByUuid(immunization1.getUuid());
		assertThat(testImmunization3.isPseudonymized(), is(false));
		assertThat(testImmunization3.getAdditionalDetails(), is("confidential details"));
		ImmunizationDto testImmunization3Second = getImmunizationFacade().getByUuid(immunization2.getUuid());
		assertThat(testImmunization3Second.isPseudonymized(), is(true));
		assertThat(testImmunization3Second.getAdditionalDetails(), is(emptyString()));
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() {

		// deactivate AUTOMATIC_RESPONSIBILITY_ASSIGNMENT in order to assign the limited user to a case from outside jurisdiction
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.CASE_SURVEILANCE);

		executeInTransaction(em -> {
			Query query = em.createQuery("select f from featureconfiguration f");
			FeatureConfiguration singleResult = (FeatureConfiguration) query.getSingleResult();
			HashMap<FeatureTypeProperty, Object> properties = new HashMap<>();
			properties.put(FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, false);
			singleResult.setProperties(properties);
			em.persist(singleResult);
		});

		loginWith(nationalAdmin);

		// immunization within limited user's jurisdiction
		PersonDto person1 = creator.createPerson("John", "Doe");
		final ImmunizationDto immunization1 = creator.createImmunization(
				Disease.CORONAVIRUS,
				person1.toReference(),
				nationalUser.toReference(), rdcf1, v->{
					v.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
					v.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
					v.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
					v.setAdditionalDetails("confidential details");
				}
		);

		// immunization outside limited user's jurisdiction
		PersonDto person2 = creator.createPerson("Max", "MUstermann");
		final ImmunizationDto immunization2 = creator.createImmunization(
				Disease.CORONAVIRUS,
				person2.toReference(),
				nationalUser.toReference(), rdcf2, v-> {
					v.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
					v.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
					v.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
					v.setAdditionalDetails("confidential details second");
				}
		);

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		// immunization created by limited user within limited user's jurisdiction
		PersonDto person3 = creator.createPerson("John", "Doe");
		final ImmunizationDto immunization3 = creator.createImmunization(
				Disease.CORONAVIRUS,
				person3.toReference(),
				nationalUser.toReference(), rdcf1, v-> {
					v.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
					v.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
					v.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
					v.setAdditionalDetails("confidential details");
				}
		);

		// immunization created by limited user outside limited user's jurisdiction
		PersonDto person4 = creator.createPerson("Max", "MUstermann");
		final ImmunizationDto immunization4 = creator.createImmunization(
				Disease.CORONAVIRUS,
				person4.toReference(),
				nationalUser.toReference(), rdcf2, v-> {
					v.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
					v.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
					v.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
					v.setAdditionalDetails("confidential details second");
				}
		);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		ImmunizationDto testImmunization = getImmunizationFacade().getByUuid(immunization1.getUuid());
		assertThat(testImmunization.isPseudonymized(), is(true));
		assertThat(testImmunization.getAdditionalDetails(), is(emptyString()));
		ImmunizationDto testImmunizationSecond = getImmunizationFacade().getByUuid(immunization2.getUuid());
		assertThat(testImmunizationSecond.isPseudonymized(), is(true));
		assertThat(testImmunizationSecond.getAdditionalDetails(), is(emptyString()));

		loginWith(nationalAdmin);
		final CaseDataDto caseDataDto = creator.createCase(nationalUser.toReference(), person1.toReference(), rdcf1);
		final CaseDataDto caseDataDtoSecond = creator.createCase(nationalUser.toReference(), person2.toReference(), rdcf2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		ImmunizationDto testImmunization2 = getImmunizationFacade().getByUuid(immunization1.getUuid());
		assertThat(testImmunization2.isPseudonymized(), is(true));
		assertThat(testImmunization2.getAdditionalDetails(), is(emptyString()));
		ImmunizationDto testImmunization2Second = getImmunizationFacade().getByUuid(immunization2.getUuid());
		assertThat(testImmunization2Second.isPseudonymized(), is(true));
		assertThat(testImmunization2Second.getAdditionalDetails(), is(emptyString()));

		loginWith(nationalAdmin);
		caseDataDto.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caseDataDto);
		caseDataDtoSecond.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caseDataDtoSecond);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		ImmunizationDto testImmunization3 = getImmunizationFacade().getByUuid(immunization1.getUuid());
		assertThat(testImmunization3.isPseudonymized(), is(false));
		assertThat(testImmunization3.getAdditionalDetails(), is("confidential details"));

		ImmunizationDto testImmunization3Second = getImmunizationFacade().getByUuid(immunization2.getUuid());
		assertThat(testImmunization3Second.isPseudonymized(), is(true));
		assertThat(testImmunization3Second.getAdditionalDetails(), is(emptyString()));

		ImmunizationDto testImmunization3Third = getImmunizationFacade().getByUuid(immunization3.getUuid());
		assertThat(testImmunization3Third.isPseudonymized(), is(true));
		assertThat(testImmunization3Third.getAdditionalDetails(), is(emptyString()));

		ImmunizationDto testImmunization3Fourth = getImmunizationFacade().getByUuid(immunization4.getUuid());
		assertThat(testImmunization3Fourth.isPseudonymized(), is(true));
		assertThat(testImmunization3Fourth.getAdditionalDetails(), is(emptyString()));
	}
}
