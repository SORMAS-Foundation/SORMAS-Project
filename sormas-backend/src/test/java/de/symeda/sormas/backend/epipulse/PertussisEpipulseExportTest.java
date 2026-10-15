/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse;

import static de.symeda.sormas.api.epipulse.EpipulseVariable.AGE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.AGE_MONTH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GESTATIONAL_AGE_AT_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_METHOD;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS_MATERNAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * Tests the Pertussis export against a real database.
 *
 * <p>
 * {@code PertussisFieldModel} is the thinnest of the four dedicated models: it uses the shared
 * definitions and three shared groups, and adds only one definition of its own,
 * {@code PathogenDetectionMethod}. This suite therefore is not a second pass over the shared block.
 * Instead, it covers what only an export with real data can cover - that the SQL fills each column
 * from the case - and it covers the one column Pertussis owns in the detail that column deserves.
 *
 * <p>
 * The dates are fixed rather than relative to today, so an expected age or vaccination date is a
 * literal that can be checked by reading rather than recomputed by the test.
 */
public class PertussisEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/** Three doses of a primary course, all well before onset. The last is what gets reported. */
	private static final Date FIRST_DOSE = DateHelper.getDateZero(2023, 0, 15);
	private static final Date SECOND_DOSE = DateHelper.getDateZero(2023, 5, 15);
	private static final Date LAST_DOSE = DateHelper.getDateZero(2023, 11, 15);

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;

	@BeforeEach
	public void setUpPertussisExport() {

		configureLuxembourgFor(EpipulseSubjectCode.PERT);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		fixture = fixtureFor(Disease.PERTUSSIS, rdcf, creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR));
	}

	@Test
	@DisplayName("a fully populated case fills every column Pertussis emits")
	public void aFullyPopulatedCaseFillsEveryColumn() {

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15).toReference();
		CaseDataDto caze = hospitalisedCase(person);
		courseCoveringOnset(person, 3, FIRST_DOSE, SECOND_DOSE, LAST_DOSE);
		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		var entry = export.only();

		// from the configuration rather than the case
		assertThat(export.value(entry, DISEASE), is("PERT"));
		assertThat(export.value(entry, SUBJECT_CODE), is("PERT"));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, DATA_SOURCE), is(SERVER_DATA_SOURCE));

		// from the case record itself
		assertThat(export.value(entry, STATUS), is("NEW/UPDATE"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(not(blankOrNullString())));
		assertThat(export.value(entry, CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(entry, OUTCOME), is("A"));
		assertThat(export.value(entry, DATE_OF_ONSET), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));
		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-10"));

		// from the person
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, AGE), is("33"));
		// AgeMonth is reported only below two years - see ageMonthIsReportedOnlyBelowTwoYears
		assertThat(export.value(entry, AGE_MONTH), is(blankOrNullString()));

		// the region and district carry no NUTS code of their own, so both fall back to the
		// server country's
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is(SERVER_COUNTRY_NUTS_CODE));

		// from the hospitalisation, the immunization course and the pathogen test
		assertThat(export.value(entry, HOSPITALISATION), is("true"));
		assertThat(export.value(entry, VACCINATION_STATUS), is("3DOSE"));
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is("2023-12-15"));
		assertThat(export.values(entry, PATHOGEN_DETECTION_METHOD), contains("CULT"));

		// The maternal pair: declared by EpiPulse and unanswerable from SORMAS, so columns that are
		// present and empty rather than absent. The fully populated case is where that is worth
		// asserting, because nothing on it could have filled either column by accident.
		for (EpipulseVariable declaredAndBlank : new EpipulseVariable[] {
			VACCINATION_STATUS_MATERNAL,
			GESTATIONAL_AGE_AT_VACCINATION }) {

			assertThat(export.emits(declaredAndBlank), is(true));
			assertThat(export.value(entry, declaredAndBlank), is(blankOrNullString()));
			assertThat(export.header(), hasItem(declaredAndBlank.getVariableName()));
		}
	}

	@Test
	@DisplayName("PathogenDetectionMethod reports each mapped test type once, and only tests that matter")
	public void pathogenDetectionMethodReportsDistinctMappedTypes() {

		PersonReferenceDto person = creator.createPerson("Ben", "Clark", Sex.MALE, 1985, 3, 2).toReference();
		CaseDataDto caze = hospitalisedCase(person);

		// two culture techniques both map to CULT, and must be reported once between them
		fixture.pathogenTest(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, ONSET);
		fixture.pathogenTest(caze, PathogenTestType.BACTERIAL_CULTURE, PathogenTestResultType.POSITIVE, true, ONSET);
		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, ONSET);

		// none of these may reach the column: not verified, not positive, and not a technique
		fixture.pathogenTest(caze, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, false, ONSET);
		fixture.pathogenTest(caze, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, ONSET);
		fixture.pathogenTest(caze, PathogenTestType.MICROSCOPY, PathogenTestResultType.POSITIVE, true, ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.only(), PATHOGEN_DETECTION_METHOD), containsInAnyOrder("CULT", "PCR"));
	}

	@Test
	@DisplayName("VaccinationStatus is NOTVACC when the cours does not cover the onset date")
	public void vaccinationStatusIsNotVaccWhenTheCourseDoesNotCoverOnset() {

		PersonReferenceDto person = creator.createPerson("Carla", "Dupont", Sex.FEMALE, 1990, 6, 15).toReference();
		hospitalisedCase(person);

		// a complete course by every other measure, but protection had lapsed by the onset date
		fixture
			.completedCourse(person, 3, DateHelper.getDateZero(2023, 0, 10), DateHelper.getDateZero(2024, 0, 10), FIRST_DOSE, SECOND_DOSE, LAST_DOSE);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		var entry = export.only();
		assertThat(export.value(entry, VACCINATION_STATUS), is("NOTVACC"));
		// no course was selected, so there is no course to take a last dose from
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is(blankOrNullString()));
	}

	@Test
	@DisplayName("AgeMonth counts completed months, and is reported only below two years")
	public void ageMonthCountsCompletedMonths() {

		// born 2023-01-15, onset 2024-06-10: one year old, and 16 completed months - the 17th falls
		// due on 2024-06-15 and has not been reached. Completed months, not months the period
		// touches, which is the distinction this date deliberately sits on.
		PersonReferenceDto infant = creator.createPerson("Dora", "Egli", Sex.FEMALE, 2023, 1, 15).toReference();
		hospitalisedCase(infant);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		var entry = export.only();
		assertThat(export.value(entry, AGE), is("1"));
		assertThat(export.value(entry, AGE_MONTH), is("16"));
	}

	@Test
	@DisplayName("a birth date recorded to the year only still yields an age")
	public void aBirthDateRecordedToTheYearOnlyStillYieldsAnAge() {

		// The spec defaults a missing month to January and a missing day to the 1st, and blanks the
		// column only when the birth year or the onset date is absent - so a person known only by
		// birth year still gets an age. See EpipulseCaseDates.birthDate.
		PersonReferenceDto person = creator.createPerson("Elias", "Frank", Sex.MALE, 1990, null, null).toReference();
		hospitalisedCase(person);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		// treated as born 1990-01-01, so 34 completed years by the onset date
		assertThat(export.value(export.only(), AGE), is("34"));
	}

	@Test
	@DisplayName("only cases reported within the period are selected")
	public void onlyCasesWithinThePeriodAreSelected() {

		hospitalisedCase(creator.createPerson("Eva", "Fischer", Sex.FEMALE, 1990, 6, 15).toReference());
		hospitalisedCase(creator.createPerson("Finn", "Gruber", Sex.MALE, 1988, 2, 3).toReference());
		fixture.caze(creator.createPerson("Gil", "Hansen", Sex.MALE, 1979, 9, 21).toReference(), DateHelper.subtractDays(PERIOD_START, 30));

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		assertThat(export.all(), hasSize(2));
	}

	@Test
	@DisplayName("the PathogenDetectionMethod group is floored at one column, but only once there is a case to measure")
	public void thePathogenDetectionMethodGroupIsFlooredAtOneColumn() {

		// 1. No cases at all. The width is measured over the entries, so there is nothing for the
		// floor to apply to and the group is zero columns wide. The definition is still on the
		// model - the column is not.
		ExportedEntries empty = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		assertThat(empty.all(), is(empty()));
		assertThat(empty.header(), is(not(empty())));
		assertThat(empty.emits(PATHOGEN_DETECTION_METHOD), is(true));
		assertThat(empty.header(), not(hasItem("PathogenDetectionMethod")));

		// 2. One case, carrying no test that qualifies. This is what the floor is for: the column
		// stays in the file and is written blank, rather than the file changing shape because
		// nobody happened to have a positive result.
		CaseDataDto caze = hospitalisedCase(creator.createPerson("Nora", "Olsen", Sex.FEMALE, 1991, 2, 9).toReference());
		fixture.pathogenTest(caze, PathogenTestType.MICROSCOPY, PathogenTestResultType.POSITIVE, true, ONSET);

		ExportedEntries exported = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		assertThat(exported.header(), hasItem("PathogenDetectionMethod"));
		assertThat(exported.values(exported.only(), PATHOGEN_DETECTION_METHOD), contains(""));
	}

	@Test
	@DisplayName("Hospitalisation is false when the admission was not for the reported disease")
	public void hospitalisationIsFalseWhenTheAdmissionWasNotForTheReportedDisease() {

		PersonReferenceDto person = creator.createPerson("Hana", "Iversen", Sex.FEMALE, 1975, 4, 8).toReference();

		// admitted, and admitted as an inpatient - but for isolation rather than for the disease
		// being reported, which is the half of the rule a fixture is most likely to get wrong
		fixture.caze(person, ONSET, caze -> {
			caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
			caze.getHospitalization().setHospitalizationReason(HospitalizationReasonType.ISOLATION);
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), HOSPITALISATION), is("false"));
	}

	@Test
	@DisplayName("DateOfOnset is blank for a case recorded as asymptomatic, even with an onset date on it")
	public void dateOfOnsetIsBlankForAnAsymptomaticCase() {

		PersonReferenceDto person = creator.createPerson("Ivo", "Jansen", Sex.MALE, 1992, 8, 30).toReference();

		// the onset date is still set - createCase writes it from the report date - so this asserts
		// the override rather than the absence of a value to report
		fixture.caze(person, ONSET, caze -> caze.getSymptoms().setAsymptomatic(SymptomState.YES));

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		var entry = export.only();
		assertThat(export.value(entry, DATE_OF_ONSET), is(blankOrNullString()));
		// the case is still exported, and the dates that do not depend on symptoms are unaffected
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));
	}

	@Test
	@DisplayName("the place columns report the most specific NUTS code each jurisdiction carries")
	public void thePlaceColumnsReportTheMostSpecificNutsCode() {

		// two jurisdictions, coded at different levels: where the patient lives has a code only at
		// region level, where the case is notified has one all the way down to community. One
		// export then shows both that the walk stops at the first code it finds and that the two
		// columns read different jurisdictions.
		TestDataCreator.RDCF homeRdcf = creator.createRDCF("HomeRegion", "HomeDistrict", "HomeCommunity", "HomeFacility");
		assignNutsCodes(homeRdcf, "LU00", null, null);
		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Jonas", "Klein", Sex.MALE, 1983, 11, 4, p -> {
			p.getAddress().setRegion(homeRdcf.region);
			p.getAddress().setDistrict(homeRdcf.district);
			p.getAddress().setCommunity(homeRdcf.community);
		}).toReference();

		fixture.caze(person, ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PERT, PERIOD_START, PERIOD_END);

		var entry = export.only();
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is("LU00"));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is("LU1010"));
	}

	// ---------------------------------------------------------------------------------------
	// fixture - everything shared lives on CaseFixture; what is left is Pertussis shorthand
	// ---------------------------------------------------------------------------------------

	private CaseDataDto hospitalisedCase(PersonReferenceDto person) {
		return fixture.caze(person, ONSET, hospitalisedForTheReportedDisease().andThen(caze -> caze.setOutcome(CaseOutcome.RECOVERED)));
	}

	/** A course whose protection window contains {@link #ONSET}, so VaccinationStatus reports it. */
	private void courseCoveringOnset(PersonReferenceDto person, int numberOfDoses, Date... doseDates) {
		fixture.completedCourse(person, numberOfDoses, DateHelper.getDateZero(2023, 0, 10), DateHelper.getDateZero(2030, 0, 10), doseDates);
	}
}
