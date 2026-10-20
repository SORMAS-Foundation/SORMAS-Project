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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_RELATED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLUSTER_SETTING;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COMPLICATION_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_INVESTIGATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAB_RESULT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_SPECIMEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENOTYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IG_G_AVIDITY_TEST;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.IMPORTED_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PREGNANCY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_IG_G;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_IG_M;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.RESULT_VIR_DETECT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SPECIMEN_SERO;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SPECIMEN_VIR_DETECT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.WEEK_OF_GESTATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.Date;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * /**
 * Tests the Rubella export against a real database.
 *
 * <p>
 * EpiPulse declares 38 variables for RUBE, and the model derives 37 of them.
 * {@code IgGAvidityTest} is the one it cannot derive, because SORMAS records no avidity test.
 * {@code RubeFieldModelTest} states the column set and the reference-value mappings without a
 * database. What does need a database is everything {@code RubeSql} decides. Rubella's four
 * laboratory derivations all differ from the measles ones they resemble: which test the lab-result
 * date comes from, which test the detection result and its specimen come from together, and how a
 * precedence over many serology tests settles into one code.
 *
 * <p>
 * <strong>Four of the columns asserted here are filled in this suite but empty in production
 * today.</strong> {@code ClusterRelated}, {@code ClusterId}, {@code ClusterSetting} and
 * {@code ImportedStatus} read {@code epidata} fields that are not annotated {@code @Diseases} for
 * {@code Disease.RUBELLA}, so no rubella case form offers them. The facades persist them anyway --
 * the annotation decides what the form shows, not what is stored -- which is exactly what allows
 * these tests to set them, and why the derivations are proved now rather than left until the
 * annotations change. See {@code RubeFieldModel}.
 *
 * <p>
 * The dates are fixed rather than relative to today, so an expected value is a literal that can be
 * checked by reading. SORMAS gives rubella an incubation period of 14 to 23 days, so an exposure
 * counts for {@code PlaceOfInfection} when it overlaps {@code [2024-05-18, 2024-05-27]}.
 */
public class RubellaEpipulseExportTest extends AbstractEpipulseExportTest {

	/**
	 * The four groups that would narrow to nothing without {@code ValueDef.atLeastOneColumn}.
	 * {@code ComplicationDiagnosis} is not among them: it substitutes a reported {@code NONE} for a
	 * case with no complications, so it is never empty and needs no floor.
	 */
	private static final EpipulseVariable[] FLOORED_GROUPS = {
		CLUSTER_SETTING,
		PLACE_OF_INFECTION,
		SPECIMEN_VIR_DETECT,
		SPECIMEN_SERO };

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	private static final Date INVESTIGATED = DateHelper.getDateZero(2024, 5, 11);

	/** Inside the incubation window {@code [2024-05-18, 2024-05-27]}. */
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 4, 20);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 4, 25);

	private static final Date FIRST_SPECIMEN = DateHelper.getDateZero(2024, 5, 11);
	private static final Date SECOND_SPECIMEN = DateHelper.getDateZero(2024, 5, 12);

	private static final Date EARLIER_TEST = DateHelper.getDateZero(2024, 5, 12);
	private static final Date LATER_TEST = DateHelper.getDateZero(2024, 5, 13);
	private static final Date LATEST_TEST = DateHelper.getDateZero(2024, 5, 14);

	private static final Date FIRST_DOSE = DateHelper.getDateZero(2023, 0, 15);
	private static final Date LAST_DOSE = DateHelper.getDateZero(2023, 5, 15);

	/** Wide enough to contain {@link #ONSET}, which is what makes a course count for a case. */
	private static final Date COURSE_VALID_FROM = DateHelper.getDateZero(2023, 0, 10);
	private static final Date COURSE_VALID_UNTIL = DateHelper.getDateZero(2030, 0, 10);

	private CaseFixture fixture;
	private CountryReferenceDto france;

	@BeforeEach
	public void setUpRubellaExport() {

		configureLuxembourgFor(EpipulseSubjectCode.RUBE);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		fixture = fixtureFor(Disease.RUBELLA, rdcf, creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		france = countryWithNutsCode("France", "FRA", "FR");
	}

	@Test
	@DisplayName("a fully populated case fills every column RUBE derives, and only IgGAvidityTest is blank")
	public void aFullyPopulatedCaseFillsEveryColumn() {

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> p.setGestationAgeAtBirth(16)).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.setInvestigatedDate(INVESTIGATED);
			c.setClinicalConfirmation(YesNoUnknown.YES);
			c.setPregnant(YesNoUnknown.YES);
			c.getSymptoms().setArthritis(SymptomState.YES);
			c.getSymptoms().setEncephalitis(SymptomState.YES);
			c.getEpiData().setClusterRelated(true);
			c.getEpiData().setClusterIdentifier("LU-RUBE-2024-03");
			c.getEpiData().setClusterType(ClusterType.SCHOOL);
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			c.getEpiData().getExposures().add(travelTo(france));
			hospitalisedForTheReportedDisease().accept(c);
		});

		fixture.completedCourse(person, 2, COURSE_VALID_FROM, COURSE_VALID_UNTIL, FIRST_DOSE, LAST_DOSE);

		// Two specimens, each with the tests its columns are about: the serum carries both
		// serologies, the swab the virus detection and the genotype.
		SampleDto serum = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, EARLIER_TEST);
		fixture.testOn(
			serum,
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST,
			test -> test.setFourFoldIncreaseAntibodyTiter(true));

		SampleDto swab = fixture.specimen(caze, SampleMaterial.NASAL_SWAB, SECOND_SPECIMEN);
		fixture.testOn(
			swab,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_1E));

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.only();

		// from the configuration rather than the case
		assertThat(export.value(entry, DISEASE), is("RUBE"));
		assertThat(export.value(entry, SUBJECT_CODE), is("RUBE"));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, DATA_SOURCE), is(SERVER_DATA_SOURCE));

		// from the case record itself
		assertThat(export.value(entry, STATUS), is("NEW/UPDATE"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(caze.getUuid()));
		assertThat(export.value(entry, CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(entry, OUTCOME), is("A"));
		assertThat(export.value(entry, DATE_OF_ONSET), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));
		assertThat(export.value(entry, DATE_OF_INVESTIGATION), is("2024-06-11"));
		assertThat(export.value(entry, HOSPITALISATION), is("true"));
		assertThat(export.value(entry, CLINICAL_CRITERIA_STATUS), is("true"));
		assertThat(export.values(entry, COMPLICATION_DIAGNOSIS), contains("ARTH", "NEURO"));

		// worked out from the tests: the earliest positive verified one is on the 12th
		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-12"));

		// the pregnancy block: one column derived, one blank for want of a field that measures it
		assertThat(export.value(entry, PREGNANCY), is("true"));
		assertThat(export.value(entry, WEEK_OF_GESTATION), is(blankOrNullString()));

		// from the person and their address
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, AGE), is("33"));
		assertThat(export.value(entry, AGE_MONTH), is(blankOrNullString()));
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is(SERVER_COUNTRY_NUTS_CODE));

		// from the epidemiological data, none of which the rubella form offers today
		assertThat(export.value(entry, CLUSTER_RELATED), is("true"));
		assertThat(export.value(entry, CLUSTER_ID), is("LU-RUBE-2024-03"));
		assertThat(export.values(entry, CLUSTER_SETTING), contains("SCH"));
		assertThat(export.value(entry, IMPORTED_STATUS), is("IMP"));
		assertThat(export.values(entry, PLACE_OF_INFECTION), contains("FR"));

		// the vaccination summary pair
		assertThat(export.value(entry, VACCINATION_STATUS), is("2DOSE"));
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is("2023-06-15"));

		// the laboratory block
		assertThat(export.value(entry, DATE_OF_SPECIMEN), is("2024-06-11"));
		assertThat(export.value(entry, DATE_OF_LAB_RESULT), is("2024-06-12"));
		assertThat(export.values(entry, SPECIMEN_VIR_DETECT), contains("NASALSWAB"));
		assertThat(export.values(entry, SPECIMEN_SERO), contains("SER"));
		assertThat(export.value(entry, RESULT_VIR_DETECT), is("POS"));
		assertThat(export.value(entry, GENOTYPE), is("RUBEV_1E"));
		assertThat(export.value(entry, RESULT_IG_G), is("POS"));
		assertThat(export.value(entry, RESULT_IG_M), is("POS"));

		// the other column SORMAS cannot answer: no avidity test exists to record
		assertThat(export.value(entry, IG_G_AVIDITY_TEST), is(blankOrNullString()));
	}

	@Test
	@DisplayName("Pregnancy reports nothing for UNKNOWN, and WeekOfGestation nothing at all")
	public void pregnancyIsReportedAndWeekOfGestationIsNot() {

		// Four cases in one export rather than four tests: the database is truncated after every
		// method, so another case inside one costs almost nothing. Each person carries a different
		// gestational age at birth, which is what makes the second half of this test say something.
		CaseDataDto pregnant = pregnancyCase(YesNoUnknown.YES, 8);
		CaseDataDto notPregnant = pregnancyCase(YesNoUnknown.NO, null);
		CaseDataDto unknown = pregnancyCase(YesNoUnknown.UNKNOWN, 25);
		CaseDataDto unanswered = pregnancyCase(null, 20);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(pregnant.getUuid()), PREGNANCY), is("true"));
		assertThat(export.value(export.entryFor(notPregnant.getUuid()), PREGNANCY), is("false"));

		// EpiPulse types the variable as a BOOL with no third state, so "we do not know" is not
		// reported as "she was not" - it is not reported at all, as an unanswered question is.
		assertThat(export.value(export.entryFor(unknown.getUuid()), PREGNANCY), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(unanswered.getUuid()), PREGNANCY), is(blankOrNullString()));

		// WeekOfGestation is blank for all four, including the three whose person records a
		// gestational age at birth in each of the three bands EpiPulse offers. That field measures
		// the gestational age of the person at their own birth, not of the patient's pregnancy when
		// she was infected, so a value here would be the wrong measurement rather than a missing
		// one. This is the assertion that fails if the column is ever wired back to it.
		for (CaseDataDto caze : new CaseDataDto[] {
			pregnant,
			notPregnant,
			unknown,
			unanswered }) {

			assertThat(
				"WeekOfGestation for " + caze.getUuid(),
				export.value(export.entryFor(caze.getUuid()), WEEK_OF_GESTATION),
				is(blankOrNullString()));
		}
	}

	@Test
	@DisplayName("the serology precedence: one positive wins, all negative is NEG, no test is NOTEST")
	public void theSerologyPrecedenceIsAppliedOverEveryTest() {

		// A case with a negative and a positive IgM. Positive wins whatever else was found: one
		// positive serology is a positive case.
		CaseDataDto mixed = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto mixedSerum = fixture.specimen(mixed, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(mixedSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);
		fixture.testOn(mixedSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, LATER_TEST);

		// Every IgM negative, so the case is reported negative.
		CaseDataDto negative = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto negativeSerum = fixture.specimen(negative, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(negativeSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);
		fixture.testOn(negativeSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, LATER_TEST);

		// One indeterminate among the negatives. EQUI is deliberately unmapped, and an
		// indeterminate result also stops the case being reported as negative - "all of them were
		// negative" is false once one of them was not.
		CaseDataDto indeterminate = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto indeterminateSerum = fixture.specimen(indeterminate, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(indeterminateSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);
		fixture.testOn(indeterminateSerum, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.INDETERMINATE, true, LATER_TEST);

		// An unverified positive. The variable asks for a validated result, so a result the
		// laboratory has not stood behind is not counted at all - which here means the case has no
		// verified serology and reports NOTEST rather than POS.
		CaseDataDto unverified = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(unverified, SampleMaterial.SERA, FIRST_SPECIMEN),
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			false,
			EARLIER_TEST);

		// No serology at all.
		CaseDataDto untested = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(mixed.getUuid()), RESULT_IG_M), is("POS"));
		assertThat(export.value(export.entryFor(negative.getUuid()), RESULT_IG_M), is("NEG"));
		assertThat(export.value(export.entryFor(indeterminate.getUuid()), RESULT_IG_M), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(unverified.getUuid()), RESULT_IG_M), is("NOTEST"));

		// NOTEST is a reported value meaning the test was not done, not an empty column.
		assertThat(export.value(export.entryFor(untested.getUuid()), RESULT_IG_M), is("NOTEST"));
		assertThat(export.value(export.entryFor(untested.getUuid()), RESULT_IG_G), is("NOTEST"));
	}

	@Test
	@DisplayName("ResultIgG counts only paired samples, and a case tested without one reports nothing")
	public void resultIgGCountsOnlyPairedSamples() {

		// The specification defines the variable as "at least a fourfold rise in specific
		// antibodies titre or seroconversion in paired serum samples", so a plain IgG test is not
		// an answer to it. Both flags qualify, and either alone is enough.
		CaseDataDto titreRise = iggCase(PathogenTestResultType.POSITIVE, test -> test.setFourFoldIncreaseAntibodyTiter(true));
		CaseDataDto seroconversion = iggCase(PathogenTestResultType.POSITIVE, test -> test.setSeroConversion(true));
		CaseDataDto negativePair = iggCase(PathogenTestResultType.NEGATIVE, test -> test.setSeroConversion(true));

		// Tested for IgG, and not for what this variable asks. Neither NOTEST - the test was done -
		// nor POS, nor NEG: the case has no answer to the question, and reports none.
		CaseDataDto unpaired = iggCase(PathogenTestResultType.POSITIVE, test -> {
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(titreRise.getUuid()), RESULT_IG_G), is("POS"));
		assertThat(export.value(export.entryFor(seroconversion.getUuid()), RESULT_IG_G), is("POS"));
		assertThat(export.value(export.entryFor(negativePair.getUuid()), RESULT_IG_G), is("NEG"));
		assertThat(export.value(export.entryFor(unpaired.getUuid()), RESULT_IG_G), is(blankOrNullString()));

		// and the IgM column is decided from IgM tests alone, so all four are NOTEST there
		assertThat(export.value(export.entryFor(titreRise.getUuid()), RESULT_IG_M), is("NOTEST"));
	}

	@Test
	@DisplayName("the detection result and its specimen come from one test, the earliest verified one")
	public void theDetectionResultAndItsSpecimenComeFromOneTest() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);

		// Three detection tests on three specimens. The earliest verified one is the swab, so its
		// result and its material are what both columns report - this is what distinguishes rubella
		// from measles, which reports every specimen the patient gave.
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST);
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.URINE, SECOND_SPECIMEN),
			PathogenTestType.CULTURE,
			PathogenTestResultType.NEGATIVE,
			true,
			LATEST_TEST);

		// Earlier than both and unverified, so it settles nothing.
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.SALIVA, FIRST_SPECIMEN),
			PathogenTestType.PCR_RT_PCR,
			PathogenTestResultType.POSITIVE,
			false,
			EARLIER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, RESULT_VIR_DETECT), is("POS"));
		assertThat(export.values(entry, SPECIMEN_VIR_DETECT), contains("NASALSWAB"));
	}

	@Test
	@DisplayName("DateOfLabResult is the oldest positive verified test, of any type")
	public void theLabResultDateIsTheOldestPositiveVerifiedTest() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto specimen = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);

		// Earliest of the three and negative, so it does not confirm the case and is not the date.
		fixture.testOn(specimen, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.NEGATIVE, true, EARLIER_TEST);

		// An antibody test, which is not in the virus-detection list measles dates itself from -
		// rubella's rule is about the result rather than about the kind of test, this counts.
		fixture.testOn(specimen, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE, true, LATER_TEST);
		fixture.testOn(specimen, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, LATEST_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.only(), DATE_OF_LAB_RESULT), is("2024-06-13"));
	}

	@Test
	@DisplayName("the two specimen columns ask different questions of the same samples")
	public void theTwoSpecimenColumnsAskDifferentQuestions() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);

		// A specimen with nothing done to it. It is the earliest, so DateOfSpecimen reports it -
		// the variable is specified as the first specimen collected "regardless of test results".
		fixture.specimen(caze, SampleMaterial.URINE, FIRST_SPECIMEN);

		// and it is not reported as collected for serology, because nothing in the record says it
		// was. SpecimenSero is narrowed to the samples carrying an IgG or IgM test.
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.SERA, SECOND_SPECIMEN),
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestResultType.POSITIVE,
			true,
			LATER_TEST);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		assertThat(export.value(entry, DATE_OF_SPECIMEN), is("2024-06-11"));
		assertThat(export.values(entry, SPECIMEN_SERO), contains("SER"));
	}

	@Test
	@DisplayName("complications accumulate as two codes, and a case with none reports NONE")
	public void complicationsAccumulateAndACaseWithNoneReportsNone() {

		CaseDataDto arthritis = complicationCase(s -> s.setArthritis(SymptomState.YES));
		CaseDataDto encephalitis = complicationCase(s -> s.setEncephalitis(SymptomState.YES));
		CaseDataDto otherNeurological = complicationCase(s -> s.setOtherNeurologicalSymptoms(YesNoUnknown.YES));

		// Both neurological columns at once. EpiPulse offers one code for all of them, so the list
		// is de-duplicated and the case reports NEURO once rather than twice.
		CaseDataDto bothNeurological = complicationCase(s -> {
			s.setEncephalitis(SymptomState.YES);
			s.setOtherNeurologicalSymptoms(YesNoUnknown.YES);
		});

		// Repeatable, so ARTH and NEURO do not compete.
		CaseDataDto both = complicationCase(s -> {
			s.setEncephalitis(SymptomState.YES);
			s.setArthritis(SymptomState.YES);
		});

		// Joint pain without arthritis is not "rubella arthritis", and an "other" complication is
		// deliberately not mapped - see RubeFieldModel.readComplications. Both of these report NONE.
		CaseDataDto arthralgiaOnly = complicationCase(s -> s.setArthralgia(SymptomState.YES));
		CaseDataDto otherOnly = complicationCase(s -> s.setOtherComplications(SymptomState.YES));
		CaseDataDto none = complicationCase(s -> {
		});

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(arthritis.getUuid()), COMPLICATION_DIAGNOSIS), contains("ARTH"));
		assertThat(export.values(export.entryFor(encephalitis.getUuid()), COMPLICATION_DIAGNOSIS), contains("NEURO"));
		assertThat(export.values(export.entryFor(otherNeurological.getUuid()), COMPLICATION_DIAGNOSIS), contains("NEURO"));
		assertThat(export.values(export.entryFor(bothNeurological.getUuid()), COMPLICATION_DIAGNOSIS), contains("NEURO"));
		assertThat(export.values(export.entryFor(both.getUuid()), COMPLICATION_DIAGNOSIS), contains("ARTH", "NEURO"));

		assertThat(export.values(export.entryFor(arthralgiaOnly.getUuid()), COMPLICATION_DIAGNOSIS), contains("NONE"));
		assertThat(export.values(export.entryFor(otherOnly.getUuid()), COMPLICATION_DIAGNOSIS), contains("NONE"));
		assertThat(export.values(export.entryFor(none.getUuid()), COMPLICATION_DIAGNOSIS), contains("NONE"));
	}

	@Test
	@DisplayName("a genotype from another disease's family reports nothing rather than a rubella code")
	public void aGenotypeFromAnotherFamilyReportsNothing() {

		CaseDataDto rubella = genotypedCase(GenoType.GENOTYPE_2B);
		CaseDataDto measles = genotypedCase(GenoType.GENOTYPE_D8);
		CaseDataDto notDetermined = genotypedCase(GenoType.GENOTYPE_UNK);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(rubella.getUuid()), GENOTYPE), is("RUBEV_2B"));

		// The two enums draw from one GenoType, so nothing but this lookup stops a measles genotype
		// reaching a rubella file - where EpiPulse would reject it.
		assertThat(export.value(export.entryFor(measles.getUuid()), GENOTYPE), is(blankOrNullString()));
		assertThat(export.value(export.entryFor(notDetermined.getUuid()), GENOTYPE), is(blankOrNullString()));
	}

	@Test
	@DisplayName("a genotype is reported even when nothing else about the test is")
	public void aGenotypeIsReportedIndependentlyOfTheDetectionResult() {

		// The genotype and the detection result are two findings, and a case can have the second
		// without the first. This case has a sequencing test that recorded a genotype and was
		// never verified: ResultVirDetect asks for a validated result and has none, while Genotype
		// asks only what the laboratory typed and has one.
		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			false,
			EARLIER_TEST,
			test -> test.setGenoType(GenoType.GENOTYPE_1C));

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		// This is the case that fails if the genotype is ever folded back into the CTE that picks
		// the detection test: that CTE only has a row for a case with a verified detection test,
		// and this case has none.
		assertThat(export.value(entry, GENOTYPE), is("RUBEV_1C"));
		assertThat(export.value(entry, RESULT_VIR_DETECT), is(blankOrNullString()));
		// one empty column rather than none: the group is floored, so it keeps its place in the header
		assertThat(export.values(entry, SPECIMEN_VIR_DETECT), contains(""));
	}

	@Test
	@DisplayName("the four floored groups keep their column for a case that records nothing")
	public void theFlooredGroupsKeepTheirColumn() {

		// A bare case: no cluster, no travel, no specimen. Without atLeastOneColumn each group
		// would measure zero wide and leave the header.
		fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.RUBE, PERIOD_START, PERIOD_END);

		for (EpipulseVariable group : FLOORED_GROUPS) {
			assertThat(group.getVariableName() + " left the header", export.header().contains(group.getVariableName()), is(true));
		}

		// and ComplicationDiagnosis is there without a floor, because it is never empty
		assertThat(export.header().contains(COMPLICATION_DIAGNOSIS.getVariableName()), is(true));
		assertThat(export.values(export.only(), COMPLICATION_DIAGNOSIS), contains("NONE"));
	}

	/**
	 * @param gestationWeeks
	 *            written to {@code PersonDto.gestationAgeAtBirth}, the field {@code WeekOfGestation}
	 *            deliberately does not read, so the cases that carry one are what proves it
	 */
	private CaseDataDto pregnancyCase(YesNoUnknown pregnant, Integer gestationWeeks) {

		PersonReferenceDto person =
			creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> p.setGestationAgeAtBirth(gestationWeeks)).toReference();

		return fixture.caze(person, ONSET, caze -> caze.setPregnant(pregnant));
	}

	private CaseDataDto iggCase(PathogenTestResultType result, Consumer<PathogenTestDto> flags) {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		SampleDto serum = fixture.specimen(caze, SampleMaterial.SERA, FIRST_SPECIMEN);
		fixture.testOn(serum, PathogenTestType.IGG_SERUM_ANTIBODY, result, true, EARLIER_TEST, flags);

		return caze;
	}

	private CaseDataDto complicationCase(Consumer<SymptomsDto> symptoms) {
		return fixture.caze(creator.createPerson().toReference(), ONSET, caze -> symptoms.accept(caze.getSymptoms()));
	}

	private CaseDataDto genotypedCase(GenoType genoType) {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.testOn(
			fixture.specimen(caze, SampleMaterial.NASAL_SWAB, FIRST_SPECIMEN),
			PathogenTestType.SEQUENCING,
			PathogenTestResultType.POSITIVE,
			true,
			EARLIER_TEST,
			test -> test.setGenoType(genoType));

		return caze;
	}

	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(EXPOSURE_START);
		exposure.setEndDate(EXPOSURE_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	private CountryReferenceDto countryWithNutsCode(String name, String isoCode, String nutsCode) {

		Country country = creator.createCountry(name, isoCode, null);

		executeInTransaction(em -> {
			em.createNativeQuery("UPDATE country SET nutscode = :nutsCode WHERE uuid = :uuid")
				.setParameter("nutsCode", nutsCode)
				.setParameter("uuid", country.getUuid())
				.executeUpdate();
		});

		return new CountryReferenceDto(country.getUuid(), name, isoCode);
	}
}
