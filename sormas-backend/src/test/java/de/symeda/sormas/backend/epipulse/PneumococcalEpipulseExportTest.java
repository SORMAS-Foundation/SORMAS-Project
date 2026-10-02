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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.AST_METHOD;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_ERY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_SIGN_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_ERY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MIC_VALUE_AST_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NRL_DATA;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_METHOD;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_CTX_CFX;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_ERY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SIR_PEN;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.therapy.SusceptibilityMethod;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * Tests the Invasive Pneumococcal Infection export against a real database.
 *
 * <p>
 * PNEU carries more of its own SQL than any other dedicated model - {@code PneuSql} adds the
 * reference-laboratory flag, the serotyping methods and the susceptibility panel - and each of
 * those can only be checked by an export over real rows: which tests a CTE reads, which it filters
 * out, and which one it settles on when there are several. The mapping behind each column is
 * unit-tested in {@code sormas-api}; this suite pins the SQL that feeds it.
 *
 * <p>
 * For {@code ClinicalCriteria}, this suite covers only what PNEU does differently from MENI, which
 * is a single value: PNEU's value set has no plain {@code PNEU} code and calls a pneumonic
 * presentation {@code BACTERPNEUMO}. The rest of the ladder is shared code, exercised end to end in
 * {@link MeningococcalEpipulseExportTest} and rung by rung in {@code EpipulseLaboratoryMapperTest};
 * repeating it here would cost a second database cycle and add no new assertion.
 *
 * <p>
 * The dates are fixed rather than relative to today, so an expected value is a literal that can be
 * checked by reading.
 */
public class PneumococcalEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/** Serotyping, two days after onset: the oldest positive test, so the diagnosis date. */
	private static final Date SEROTYPED = DateHelper.getDateZero(2024, 5, 12);
	/** An earlier susceptibility panel, superseded by {@link #LATEST_AST}. */
	private static final Date EARLIER_AST = DateHelper.getDateZero(2024, 5, 13);
	private static final Date LATEST_AST = DateHelper.getDateZero(2024, 5, 14);

	private static final Date FIRST_DOSE = DateHelper.getDateZero(2023, 0, 15);
	private static final Date LAST_DOSE = DateHelper.getDateZero(2023, 5, 15);

	/**
	 * The seventeen per-dose vaccination columns and {@code Serotype}: declared by EpiPulse and not
	 * derived by SORMAS, so present and empty - see {@code PneuFieldModel}.
	 */
	private static final List<EpipulseVariable> DECLARED_AND_BLANK = Arrays.asList(
		EpipulseVariable.SEROTYPE,
		EpipulseVariable.VACCINE,
		EpipulseVariable.DOSE_PCV1,
		EpipulseVariable.DOSE_PCV2,
		EpipulseVariable.DOSE_PCV3,
		EpipulseVariable.DOSE_PCV4,
		EpipulseVariable.DATE_PCV1,
		EpipulseVariable.DATE_PCV2,
		EpipulseVariable.DATE_PCV3,
		EpipulseVariable.DATE_PCV4,
		EpipulseVariable.BRAND_PCV1,
		EpipulseVariable.BRAND_PCV2,
		EpipulseVariable.BRAND_PCV3,
		EpipulseVariable.BRAND_PCV4,
		EpipulseVariable.PCV_DOSES,
		EpipulseVariable.DOSE_PPV,
		EpipulseVariable.DATE_PPV,
		EpipulseVariable.PPV_DOSES);

	private CaseFixture fixture;

	@BeforeEach
	public void setUpPneumococcalExport() {

		configureLuxembourgFor(EpipulseSubjectCode.PNEU);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		fixture = fixtureFor(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, rdcf, creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR));
	}

	@Test
	@DisplayName("a fully populated case fills every column PNEU derives, and leaves the declared-blank ones empty")
	public void aFullyPopulatedCaseFillsEveryColumn() {

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15).toReference();
		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setOutcome(CaseOutcome.RECOVERED);
			c.getSymptoms().setMeningitis(SymptomState.YES);
		});

		fixture.completedCourse(person, 2, DateHelper.getDateZero(2023, 0, 10), DateHelper.getDateZero(2030, 0, 10), FIRST_DOSE, LAST_DOSE);

		fixture.pathogenTest(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, SEROTYPED, test -> {
			test.setSeroTypingMethod(SerotypingMethod.QUELLUNG_REACTION);
			test.setPerformedByReferenceLaboratory(true);
		});
		fixture.pathogenTest(
			caze,
			PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
			PathogenTestResultType.POSITIVE,
			true,
			LATEST_AST,
			panel(SusceptibilityMethod.ETEST, SusceptibilityMethod.ETEST, SusceptibilityMethod.ETEST));

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto entry = export.only();

		// from the configuration rather than the case
		assertThat(export.value(entry, DISEASE), is("PNEU"));
		assertThat(export.value(entry, SUBJECT_CODE), is("PNEU"));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, DATA_SOURCE), is(SERVER_DATA_SOURCE));

		// from the case record itself
		assertThat(export.value(entry, STATUS), is("NEW/UPDATE"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(caze.getUuid()));
		assertThat(export.value(entry, CASE_CLASSIFICATION), is("CONF"));
		assertThat(export.value(entry, OUTCOME), is("A"));
		assertThat(export.value(entry, CLINICAL_CRITERIA), is("MENI"));
		assertThat(export.value(entry, DATE_OF_NOTIFICATION), is("2024-06-10"));

		// both worked out from the tests: the serotyping on the 12th is the oldest positive one
		assertThat(export.value(entry, DATE_OF_DIAGNOSIS), is("2024-06-12"));
		assertThat(export.value(entry, DATE_USED_FOR_STATISTICS), is("2024-06-12"));

		// from the person and their address
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, AGE), is("33"));
		assertThat(export.value(entry, AGE_MONTH), is(blankOrNullString()));
		assertThat(export.value(entry, PLACE_OF_RESIDENCE), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, PLACE_OF_NOTIFICATION), is(SERVER_COUNTRY_NUTS_CODE));

		// the vaccination summary pair
		assertThat(export.value(entry, VACCINATION_STATUS), is("2DOSE"));
		assertThat(export.value(entry, DATE_OF_LAST_VACCINATION), is("2023-06-15"));

		// PNEU's own laboratory columns
		assertThat(export.value(entry, NRL_DATA), is("true"));
		assertThat(export.values(entry, PATHOGEN_DETECTION_METHOD), contains("QUE"));
		assertThat(export.value(entry, AST_METHOD), is("GRAD"));
		assertThat(export.value(entry, SIR_CTX_CFX), is("S"));

		// Declared and blank. The fully populated case is where that is worth asserting: it has a
		// vaccination course and a serotyping test, so a column filled by accident would show here.
		for (EpipulseVariable declaredAndBlank : DECLARED_AND_BLANK) {
			assertThat(declaredAndBlank.getVariableName(), export.value(entry, declaredAndBlank), is(blankOrNullString()));
			assertThat(export.header(), hasItem(declaredAndBlank.getVariableName()));
		}
	}

	@Test
	@DisplayName("a pneumonic presentation reports Pneumococcal's own code, and the shared layers are untouched")
	public void aPneumonicPresentationReportsPneumococcalsOwnCode() {

		CaseDataDto pneumonia = caseWithMeningitisAndPneumonia(SymptomState.NO, SymptomState.YES);
		CaseDataDto meningitis = caseWithMeningitisAndPneumonia(SymptomState.YES, SymptomState.NO);

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(pneumonia.getUuid()), CLINICAL_CRITERIA), is("BACTERPNEUMO"));

		// the code passed in must reach the pneumonia and no other
		assertThat(export.value(export.entryFor(meningitis.getUuid()), CLINICAL_CRITERIA), is("MENI"));
	}

	@Test
	@DisplayName("PathogenDetectionMethod reports each serotyping technique once, and only from tests that matter")
	public void pathogenDetectionMethodReportsDistinctSerotypingTechniques() {

		CaseDataDto caze = fixture.caze(creator.createPerson().toReference(), ONSET);

		// Both test types are read: MULTIPLEX_PCR belongs on a serotyping test, the rest on
		// serogrouping. Two Quellung tests are one technique.
		serotyped(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, SerotypingMethod.QUELLUNG_REACTION);
		serotyped(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, SerotypingMethod.QUELLUNG_REACTION);
		serotyped(caze, PathogenTestType.SEROTYPING, PathogenTestResultType.POSITIVE, true, SerotypingMethod.MULTIPLEX_PCR);
		serotyped(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, true, SerotypingMethod.OTHER);

		// None of these should reach the column: negative, unverified, and a method recorded on a test
		// that is not a serotyping one.
		serotyped(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.NEGATIVE, true, SerotypingMethod.PNEUMOTEST);
		serotyped(caze, PathogenTestType.SEROGROUPING, PathogenTestResultType.POSITIVE, false, SerotypingMethod.GEL_DIFFUSION);
		serotyped(caze, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, SerotypingMethod.SLIDE_AGGLUTINATION);

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.only(), PATHOGEN_DETECTION_METHOD), containsInAnyOrder("QUE", "MPCR", "OTH"));
	}

	@Test
	@DisplayName("PathogenDetectionMethod keeps its column for a case with no recorded serotyping method")
	public void pathogenDetectionMethodKeepsItsColumnWithoutASerotypingMethod() {

		fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		assertThat(export.header(), hasItem(PATHOGEN_DETECTION_METHOD.getVariableName()));
		assertThat(export.values(export.only(), PATHOGEN_DETECTION_METHOD), contains(""));
	}

	/**
	 * The susceptibility block in one export - one case per situation, told apart by
	 * {@code NationalRecordId}.
	 */
	@Test
	@DisplayName("the susceptibility columns come from the newest panel, and ASTMethod collapses that panel's methods")
	public void theSusceptibilityColumnsComeFromTheNewestPanel() {

		// Two panels. The earlier one disagrees with the later on every column, so any value that
		// leaks from it shows. The later one is also created first, so the choice cannot be riding
		// on insertion order.
		CaseDataDto testedTwice = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			testedTwice,
			PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
			PathogenTestResultType.POSITIVE,
			true,
			LATEST_AST,
			panel(SusceptibilityMethod.ETEST, SusceptibilityMethod.ETEST, SusceptibilityMethod.ETEST));
		fixture.pathogenTest(testedTwice, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, PathogenTestResultType.POSITIVE, true, EARLIER_AST, test -> {
			DrugSusceptibilityDto earlier = DrugSusceptibilityDto.build();
			earlier.setCeftriaxoneMic("16");
			earlier.setCeftriaxoneSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setErythromycinMic("0.25");
			earlier.setErythromycinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			earlier.setPenicillinMic("4");
			earlier.setPenicillinSusceptibility(DrugSusceptibilityType.RESISTANT);
			earlier.setCeftriaxoneMethod(SusceptibilityMethod.AGAR_DILUTION);
			earlier.setErythromycinMethod(SusceptibilityMethod.AGAR_DILUTION);
			earlier.setPenicillinMethod(SusceptibilityMethod.AGAR_DILUTION);
			test.setDrugSusceptibility(earlier);
		});

		// the three drugs determined three different ways: EpiPulse has one answer per case
		CaseDataDto methodsDiffer = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			methodsDiffer,
			PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
			PathogenTestResultType.POSITIVE,
			true,
			LATEST_AST,
			panel(SusceptibilityMethod.ETEST, SusceptibilityMethod.AUTOMATED_MIC, SusceptibilityMethod.BROTH_MICRODILUTION));

		// only one drug has a method: an absent method is not a disagreement
		CaseDataDto oneMethod = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			oneMethod,
			PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
			PathogenTestResultType.POSITIVE,
			true,
			LATEST_AST,
			panel(null, null, SusceptibilityMethod.AUTOMATED_MIC));

		// never tested for susceptibility
		CaseDataDto untested = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		EpipulseDiseaseExportEntryDto newest = export.entryFor(testedTwice.getUuid());
		// "<=0.125 mg/L": a typed operator and a unit around the number
		assertThat(export.value(newest, MIC_SIGN_CTX_CFX), is("<="));
		assertThat(export.value(newest, MIC_VALUE_AST_CTX_CFX), is("0.125"));
		assertThat(export.value(newest, SIR_CTX_CFX), is("S"));
		// "> 32": a space between the operator and the number
		assertThat(export.value(newest, MIC_SIGN_ERY), is(">"));
		assertThat(export.value(newest, MIC_VALUE_AST_ERY), is("32"));
		assertThat(export.value(newest, SIR_ERY), is("R"));
		// "0,06": a decimal comma, and no operator
		assertThat(export.value(newest, MIC_SIGN_PEN), is(blankOrNullString()));
		assertThat(export.value(newest, MIC_VALUE_AST_PEN), is("0.06"));
		assertThat(export.value(newest, SIR_PEN), is("I"));
		assertThat(export.value(newest, AST_METHOD), is("GRAD"));

		assertThat(export.value(export.entryFor(methodsDiffer.getUuid()), AST_METHOD), is("OTH"));
		assertThat(export.value(export.entryFor(oneMethod.getUuid()), AST_METHOD), is("AUTOM"));

		EpipulseDiseaseExportEntryDto none = export.entryFor(untested.getUuid());
		for (EpipulseVariable astColumn : new EpipulseVariable[] {
			AST_METHOD,
			MIC_SIGN_CTX_CFX,
			MIC_VALUE_AST_CTX_CFX,
			SIR_CTX_CFX,
			MIC_SIGN_ERY,
			MIC_VALUE_AST_ERY,
			SIR_ERY,
			MIC_SIGN_PEN,
			MIC_VALUE_AST_PEN,
			SIR_PEN }) {
			assertThat(astColumn.getVariableName() + " for a case never tested", export.value(none, astColumn), is(blankOrNullString()));
		}
	}

	@Test
	@DisplayName("NRLData is true only for a case with a non-deleted test by the reference laboratory")
	public void nrlDataIsTrueOnlyForAReferenceLaboratoryTest() {

		CaseDataDto byReferenceLab = fixture.caze(creator.createPerson().toReference(), ONSET);
		// any kind of test counts, positive or not
		fixture.pathogenTest(
			byReferenceLab,
			PathogenTestType.CULTURE,
			PathogenTestResultType.NEGATIVE,
			false,
			SEROTYPED,
			test -> test.setPerformedByReferenceLaboratory(true));

		CaseDataDto byOtherLab = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(
			byOtherLab,
			PathogenTestType.CULTURE,
			PathogenTestResultType.POSITIVE,
			true,
			SEROTYPED,
			test -> test.setPerformedByReferenceLaboratory(false));

		// the flag was never set, as on tests recorded before it existed
		CaseDataDto flagUnset = fixture.caze(creator.createPerson().toReference(), ONSET);
		fixture.pathogenTest(flagUnset, PathogenTestType.CULTURE, PathogenTestResultType.POSITIVE, true, SEROTYPED);

		CaseDataDto noTests = fixture.caze(creator.createPerson().toReference(), ONSET);

		ExportedEntries export = runExport(EpipulseSubjectCode.PNEU, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(byReferenceLab.getUuid()), NRL_DATA), is("true"));
		// never blank: not known to be reference-laboratory data is non-reference-laboratory data
		assertThat(export.value(export.entryFor(byOtherLab.getUuid()), NRL_DATA), is("false"));
		assertThat(export.value(export.entryFor(flagUnset.getUuid()), NRL_DATA), is("false"));
		assertThat(export.value(export.entryFor(noTests.getUuid()), NRL_DATA), is("false"));
	}

	// ---------------------------------------------------------------------------------------
	// fixture - everything shared lives on CaseFixture; what is left is PNEU shorthand
	// ---------------------------------------------------------------------------------------

	private CaseDataDto caseWithMeningitisAndPneumonia(SymptomState meningitis, SymptomState pneumonia) {

		return fixture.caze(creator.createPerson().toReference(), ONSET, caze -> {
			caze.getSymptoms().setMeningitis(meningitis);
			caze.getSymptoms().setPneumoniaClinicalOrRadiologic(pneumonia);
		});
	}

	private void serotyped(CaseDataDto caze, PathogenTestType testType, PathogenTestResultType result, boolean verified, SerotypingMethod method) {
		fixture.pathogenTest(caze, testType, result, verified, SEROTYPED, test -> test.setSeroTypingMethod(method));
	}

	/**
	 * A panel whose MICs are typed three different ways a laboratory writes them, with the given
	 * method per drug - ceftriaxone, erythromycin, penicillin.
	 */
	private static Consumer<PathogenTestDto> panel(
		SusceptibilityMethod ceftriaxone,
		SusceptibilityMethod erythromycin,
		SusceptibilityMethod penicillin) {

		return test -> {
			DrugSusceptibilityDto panel = DrugSusceptibilityDto.build();
			panel.setCeftriaxoneMic("\u22640.125 mg/L");
			panel.setCeftriaxoneSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
			panel.setErythromycinMic("> 32");
			panel.setErythromycinSusceptibility(DrugSusceptibilityType.RESISTANT);
			panel.setPenicillinMic("0,06");
			panel.setPenicillinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
			panel.setCeftriaxoneMethod(ceftriaxone);
			panel.setErythromycinMethod(erythromycin);
			panel.setPenicillinMethod(penicillin);
			test.setDrugSusceptibility(panel);
		};
	}
}
