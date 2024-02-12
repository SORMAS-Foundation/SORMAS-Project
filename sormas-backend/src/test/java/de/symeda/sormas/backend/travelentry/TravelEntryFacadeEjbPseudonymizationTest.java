package de.symeda.sormas.backend.travelentry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Query;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.feature.FeatureConfiguration;

public class TravelEntryFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto nationalUser;

	private UserDto districtUser1;

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

		loginWith(nationalUser);

		// travel entry within limited user's jurisdiction
		PersonDto person1 = creator.createPerson("John", "Doe");
		TravelEntryDto travelEntry1 = creator.createTravelEntry(person1.toReference(), nationalUser.toReference(), rdcf1, v -> {
			v.setDisease(Disease.CORONAVIRUS);
			v.setQuarantineHomePossibleComment("pacient can stay home");
		});

		// travel entry outside limited user's jurisdiction
		PersonDto person2 = creator.createPerson("John", "Doe");
		TravelEntryDto travelEntry2 = creator.createTravelEntry(person2.toReference(), nationalUser.toReference(), rdcf2, v -> {
			v.setDisease(Disease.CORONAVIRUS);
			v.setQuarantineHomePossibleComment("pacient can stay home second");
		});

		loginWith(nationalAdmin);
		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		// travel entry created by limited user within limited user's jurisdiction
		PersonDto person3 = creator.createPerson("John", "Doe");
		TravelEntryDto travelEntry3 = creator.createTravelEntry(person3.toReference(), nationalUser.toReference(), rdcf1, v -> {
			v.setDisease(Disease.CORONAVIRUS);
			v.setQuarantineHomePossibleComment("pacient can stay home");
		});

		// travel entry created by limited user outside limited user's jurisdiction
		PersonDto person4 = creator.createPerson("John", "Doe");
		TravelEntryDto travelEntry4 = creator.createTravelEntry(person4.toReference(), nationalUser.toReference(), rdcf2, v -> {
			v.setDisease(Disease.CORONAVIRUS);
			v.setQuarantineHomePossibleComment("pacient can stay home second");
		});

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		TravelEntryDto testTravelEntry = getTravelEntryFacade().getByUuid(travelEntry1.getUuid());
		assertThat(testTravelEntry.isPseudonymized(), is(true));
		assertThat(testTravelEntry.getQuarantineHomePossibleComment(), is(emptyString()));
		TravelEntryDto testTravelEntrySecond = getTravelEntryFacade().getByUuid(travelEntry2.getUuid());
		assertThat(testTravelEntrySecond.isPseudonymized(), is(true));
		assertThat(testTravelEntrySecond.getQuarantineHomePossibleComment(), is(emptyString()));

		loginWith(nationalAdmin);
		final CaseDataDto caseDataDto1 = creator.createCase(nationalUser.toReference(), person1.toReference(), rdcf1);
		final CaseDataDto caseDataDto2 = creator.createCase(nationalUser.toReference(), person2.toReference(), rdcf2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		TravelEntryDto testTravelEntry2 = getTravelEntryFacade().getByUuid(travelEntry1.getUuid());
		assertThat(testTravelEntry2.isPseudonymized(), is(true));
		assertThat(testTravelEntry2.getQuarantineHomePossibleComment(), is(emptyString()));
		TravelEntryDto testTravelEntry2Second = getTravelEntryFacade().getByUuid(travelEntry2.getUuid());
		assertThat(testTravelEntry2Second.isPseudonymized(), is(true));
		assertThat(testTravelEntry2Second.getQuarantineHomePossibleComment(), is(emptyString()));

		loginWith(nationalAdmin);
		caseDataDto1.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caseDataDto1);
		caseDataDto2.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caseDataDto2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		TravelEntryDto testTravelEntry3 = getTravelEntryFacade().getByUuid(travelEntry1.getUuid());
		assertThat(testTravelEntry3.isPseudonymized(), is(false));
		assertThat(testTravelEntry3.getQuarantineHomePossibleComment(), is("pacient can stay home"));

		TravelEntryDto testTravelEntry3Second = getTravelEntryFacade().getByUuid(travelEntry2.getUuid());
		assertThat(testTravelEntry3Second.isPseudonymized(), is(true));
		assertThat(testTravelEntry3Second.getQuarantineHomePossibleComment(), is(emptyString()));

		TravelEntryDto testTravelEntry3Third = getTravelEntryFacade().getByUuid(travelEntry3.getUuid());
		assertThat(testTravelEntry3Third.isPseudonymized(), is(true));
		assertThat(testTravelEntry3Third.getQuarantineHomePossibleComment(), is(emptyString()));

		TravelEntryDto testTravelEntry3Fourth = getTravelEntryFacade().getByUuid(travelEntry4.getUuid());
		assertThat(testTravelEntry3Fourth.isPseudonymized(), is(true));
		assertThat(testTravelEntry3Fourth.getQuarantineHomePossibleComment(), is(emptyString()));
	}

	@Test
	public void testGetTravelEntryOutsideJurisdiction() {
		loginWith(nationalAdmin);

		TravelEntryDto travelEntry = createTravelEntry(nationalUser, rdcf2, null);

		loginWith(districtUser1);

		assertPseudonymized(getTravelEntryFacade().getByUuid(travelEntry.getUuid()), rdcf2);
		assertPseudonymized(getTravelEntryFacade().getByUuids(Collections.singletonList(travelEntry.getUuid())).get(0), rdcf2);
		assertThat(getTravelEntryFacade().getAllAfter(new Date(0)), hasSize(0));
		assertThat(getTravelEntryFacade().getIndexList(new TravelEntryCriteria(), null, null, null), hasSize(0));
	}

	@Test
	public void testGetReportOfCaseWithSpecialAccess() {
		loginWith(nationalAdmin);

		CaseDataDto caze = creator.createCase(nationalUser.toReference(), creator.createPerson().toReference(), rdcf2);
		TravelEntryDto travelEntry = createTravelEntry(nationalUser, rdcf2, caze);
		creator
			.createSpecialCaseAccess(caze.toReference(), nationalUser.toReference(), districtUser1.toReference(), DateHelper.addDays(new Date(), 1));

		loginWith(districtUser1);

		assertNotPseudonymized(getTravelEntryFacade().getByUuid(travelEntry.getUuid()), nationalUser, rdcf2);
		assertNotPseudonymized(getTravelEntryFacade().getByUuids(Collections.singletonList(travelEntry.getUuid())).get(0), nationalUser, rdcf2);
		assertNotPseudonymized(getTravelEntryFacade().getAllAfter(new Date(0)).get(0), nationalUser, rdcf2);
		assertThat(getTravelEntryFacade().getIndexList(new TravelEntryCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
	}

	private void assertPseudonymized(TravelEntryDto travelEntry, TestDataCreator.RDCF rdcf) {
		assertThat(travelEntry.isPseudonymized(), is(true));
		assertThat(travelEntry.getReportingUser(), is(nationalUser));
		assertThat(travelEntry.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(travelEntry.getResponsibleRegion(), is(rdcf.region));
		assertThat(travelEntry.getResponsibleDistrict(), is(rdcf.district));
		assertThat(travelEntry.getResponsibleCommunity(), is(nullValue()));
		assertThat(travelEntry.getPointOfEntry(), is(nullValue()));
		assertThat(travelEntry.getPointOfEntryDetails(), is(""));
		assertThat(travelEntry.getQuarantine(), is(QuarantineType.OTHER));
		assertThat(travelEntry.getQuarantineTypeDetails(), is(""));
		assertThat(travelEntry.getQuarantineHelpNeeded(), is(""));
	}

	private void assertNotPseudonymized(TravelEntryDto travelEntry, UserDto user, TestDataCreator.RDCF rdcf) {
		assertThat(travelEntry.isPseudonymized(), is(false));
		assertThat(travelEntry.getReportingUser(), is(user.toReference()));
		assertThat(travelEntry.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(travelEntry.getResponsibleRegion(), is(rdcf.region));
		assertThat(travelEntry.getResponsibleDistrict(), is(rdcf.district));
		assertThat(travelEntry.getResponsibleCommunity(), is(rdcf.community));
		assertThat(travelEntry.getPointOfEntry(), is(rdcf.pointOfEntry));
		assertThat(travelEntry.getPointOfEntryDetails(), is("Test point of entry details"));
		assertThat(travelEntry.getQuarantine(), is(QuarantineType.OTHER));
		assertThat(travelEntry.getQuarantineTypeDetails(), is("Test quarantine type details"));
		assertThat(travelEntry.getQuarantineHelpNeeded(), is("Test quarantine help needed"));
	}

	private TravelEntryDto createTravelEntry(UserDto user, TestDataCreator.RDCF rdcf, CaseDataDto caze) {
		TravelEntryDto travelEntry = TravelEntryDto.build(caze == null ? creator.createPerson().toReference() : caze.getPerson());
		travelEntry.setReportDate(new Date());
		travelEntry.setDateOfArrival(new Date());
		travelEntry.setReportingUser(user.toReference());
		travelEntry.setDisease(Disease.CORONAVIRUS);
		travelEntry.setResponsibleRegion(rdcf.region);
		travelEntry.setResponsibleDistrict(rdcf.district);
		travelEntry.setResponsibleCommunity(rdcf.community);
		travelEntry.setPointOfEntry(rdcf.pointOfEntry);
		travelEntry.setPointOfEntryDetails("Test point of entry details");
		travelEntry.setQuarantine(QuarantineType.OTHER);
		travelEntry.setQuarantineTypeDetails("Test quarantine type details");
		travelEntry.setQuarantineHelpNeeded("Test quarantine help needed");

		return getTravelEntryFacade().save(travelEntry);
	}
}
