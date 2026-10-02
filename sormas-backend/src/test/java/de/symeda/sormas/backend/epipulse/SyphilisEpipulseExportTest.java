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
import static de.symeda.sormas.api.epipulse.EpipulseVariable.ANTIBIOTIC_PROPHYLAXIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CASE_CLASSIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_CRITERIA_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CLINICAL_SERVICE_TYPE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.CONTACT_SW;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_BIRTH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_BIRTH_OF_MOTHER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_NATIONALITY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.COUNTRY_OF_NATIONALITY_OF_MOTHER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATA_SOURCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_DIAGNOSIS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_OF_ONSET;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DATE_USED_FOR_STATISTICS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.EPI_LINKED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HIV_PR_EP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HIV_STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.MODE_OF_TRANSMISSION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.OUTCOME;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_RESULT;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_NOTIFICATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PLACE_OF_RESIDENCE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEX_WORKER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SITE_OF_INFECTION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STAGE_SYPH;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STAGE_SYP_HDETAILED;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.STATUS;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.SyphilisPresentation;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.epidata.TypeOfClinicalService;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Tests acquired and congenital syphilis against a real database, column by column.
 *
 * <p>
 * SYPH and CONSYPH are the only two subject codes that share a SORMAS disease. Both select
 * {@code Disease.SYPHILIS} and are separated by {@code cases.syphilispresentation} through
 * {@code EpipulseCaseSubset}, applied by the export strategy from the subject code. This suite
 * exists to prove two things, and the separation is the first: it is invisible in either field
 * model, so nothing else would catch its loss, and losing it would send every congenital case to
 * EpiPulse twice (once correctly, once as an acquired infection).
 *
 * <p>
 * The second is that every column each code declares carries the value the case was built with.
 * {@link #assertEveryDeclaredColumn} takes the expectation for a whole file and first checks that it
 * names <em>every</em> variable the metadata declares for that code and no other, so a variable
 * added in a later metadata revision fails here with a list of what is unaccounted for rather than
 * being exported untested. That makes these two full-coverage tests, not a hand-picked column
 * selection.
 *
 * <p>
 * Dates are fixed rather than relative to today, so every expected value is a literal that can be
 * verified by reading.
 */
public class SyphilisEpipulseExportTest extends AbstractEpipulseExportTest {

	private static final Date ONSET = DateHelper.getDateZero(2024, 5, 10);
	private static final Date PERIOD_START = DateHelper.getDateZero(2024, 5, 1);
	private static final Date PERIOD_END = DateHelper.getDateZero(2024, 5, 20);

	/** A doctor's notification diagnosed the case before the laboratory confirmed it. */
	private static final Date DOCTOR_DIAGNOSIS = DateHelper.getDateZero(2024, 5, 5);
	private static final Date POSITIVE_TEST = DateHelper.getDateZero(2024, 5, 12);

	/**
	 * Inside the incubation window: SORMAS gives syphilis 10 to 90 days, so an exposure counts when
	 * it overlaps {@code [2024-03-12, 2024-05-31]}.
	 */
	private static final Date EXPOSURE_START = DateHelper.getDateZero(2024, 3, 1);
	private static final Date EXPOSURE_END = DateHelper.getDateZero(2024, 3, 15);

	/**
	 * Every {@link SyphilisInfectionSite} and the EpiPulse code it reports, {@code null} where
	 * EpiPulse has no value for it. Spelled out rather than derived from
	 * {@code EpipulseSiteOfInfectionRef}, which would assert only that the class agrees with
	 * itself.
	 */
	private static final Map<SyphilisInfectionSite, String> SITE_CODES = new LinkedHashMap<>();
	private static final Map<SyphilisInfectiousness, String> INFECTIOUSNESS_CODES = new LinkedHashMap<>();
	private static final Map<SyphilisStage, String> STAGE_CODES = new LinkedHashMap<>();

	static {
		SITE_CODES.put(SyphilisInfectionSite.GENITAL, "GEN");
		SITE_CODES.put(SyphilisInfectionSite.ANORECTAL, "AR");
		SITE_CODES.put(SyphilisInfectionSite.PHARYNGEAL, "PH");
		SITE_CODES.put(SyphilisInfectionSite.OTHER, "OTH");
		// OTH means "a site EpiPulse names but does not list", not "not established"
		SITE_CODES.put(SyphilisInfectionSite.UNKNOWN, null);

		INFECTIOUSNESS_CODES.put(SyphilisInfectiousness.INFECTIOUS, "I");
		INFECTIOUSNESS_CODES.put(SyphilisInfectiousness.NOT_INFECTIOUS, "NI");
		INFECTIOUSNESS_CODES.put(SyphilisInfectiousness.UNKNOWN, null);

		STAGE_CODES.put(SyphilisStage.PRIMARY_SYPHILIS, "P");
		STAGE_CODES.put(SyphilisStage.SECONDARY_SYPHILIS, "S");
		STAGE_CODES.put(SyphilisStage.EARLY_LATENT_SYPHILIS, "EL");
		STAGE_CODES.put(SyphilisStage.LATE_LATENT_SYPHILIS, "LL");
		// EpiPulse's StageSYPHdetailed stops at late latent; these two have no code to report
		STAGE_CODES.put(SyphilisStage.TERTIARY_SYPHILIS, null);
		STAGE_CODES.put(SyphilisStage.NEUROLOGICAL_SYPHILIS, null);
		STAGE_CODES.put(SyphilisStage.UNKNOWN, null);
	}

	private TestDataCreator.RDCF rdcf;
	private CaseFixture fixture;
	private UserReferenceDto reportingUser;
	private CountryReferenceDto france;
	private CountryReferenceDto italy;
	private CountryReferenceDto spain;

	@BeforeEach
	public void setUpSyphilisExport() {

		configureLuxembourgFor(EpipulseSubjectCode.SYPH, EpipulseSubjectCode.CONSYPH);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);
		reportingUser = surveillanceSupervisor.toReference();
		fixture = fixtureFor(Disease.SYPHILIS, rdcf, surveillanceSupervisor);

		france = countryWithNutsCode("France", "FRA", "FR");
		italy = countryWithNutsCode("Italy", "ITA", "IT");
		spain = countryWithNutsCode("Spain", "ESP", "ES");
	}

	// ---------------------------------------------------------------------------------------
	// the two codes are one disease
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("each subject code exports only its own presentation of the same disease")
	public void eachSubjectCodeExportsOnlyItsOwnPresentation() {

		CaseDataDto acquired = syphilisCase(SyphilisPresentation.ACQUIRED);
		CaseDataDto congenital = syphilisCase(SyphilisPresentation.CONGENITAL);

		// a case of each, reported before the period: neither export may reach them
		fixture.caze(newPerson(), DateHelper.subtractDays(PERIOD_START, 30), caze -> caze.setSyphilisPresentation(SyphilisPresentation.ACQUIRED));
		fixture.caze(newPerson(), DateHelper.subtractDays(PERIOD_START, 30), caze -> caze.setSyphilisPresentation(SyphilisPresentation.CONGENITAL));

		ExportedEntries acquiredExport = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);
		assertThat(acquiredExport.all(), hasSize(1));
		assertThat(acquiredExport.value(acquiredExport.only(), NATIONAL_RECORD_ID), is(acquired.getUuid()));

		ExportedEntries congenitalExport = runExport(EpipulseSubjectCode.CONSYPH, PERIOD_START, PERIOD_END);
		assertThat(congenitalExport.all(), hasSize(1));
		assertThat(congenitalExport.value(congenitalExport.only(), NATIONAL_RECORD_ID), is(congenital.getUuid()));
	}

	// ---------------------------------------------------------------------------------------
	// every declared column, filled
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("a fully populated acquired case fills every one of the 32 columns SYPH declares")
	public void aFullyPopulatedAcquiredCaseFillsEveryColumnSyphDeclares() {

		TestDataCreator.RDCF homeRdcf = creator.createRDCF("HomeRegion", "HomeDistrict", "HomeCommunity", "HomeFacility");
		assignNutsCodes(homeRdcf, "LU00", null, null);
		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		PersonReferenceDto person = creator.createPerson("Anna", "Bauer", Sex.FEMALE, 1990, 6, 15, p -> {
			p.setBirthCountry(france);
			// born in one country and a citizen of another, which is the whole point of the two
			// being separate columns
			p.setCitizenship(spain);
			p.getAddress().setRegion(homeRdcf.region);
			p.getAddress().setDistrict(homeRdcf.district);
			p.getAddress().setCommunity(homeRdcf.community);
		}).toReference();

		CaseDataDto caze = fixture.caze(person, ONSET, c -> {
			c.setSyphilisPresentation(SyphilisPresentation.ACQUIRED);
			c.setOutcome(CaseOutcome.RECOVERED);
			c.getSymptoms().setSyphilisInfectionSite(SyphilisInfectionSite.PHARYNGEAL);
			c.getSymptoms().setSyphilisInfectiousness(SyphilisInfectiousness.INFECTIOUS);
			c.getSymptoms().setSyphilisStage(SyphilisStage.EARLY_LATENT_SYPHILIS);
			c.getEpiData().setSexWorker(YesNoUnknown.YES);
			c.getEpiData().getExposures().add(travelTo(france));
			// the STI risk and service block, shared with gonorrhoea
			c.getEpiData().setTypeOfClinicalService(TypeOfClinicalService.DERMATOLOGY_VENEREOLOGY_CLINIC);
			c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.HETEROSEXUAL_CONTACT);
			c.getEpiData().setContactWithSexWorker(YesNoUnknown.NO);
			c.getHealthConditions().setHivStatus(HivStatus.NEW_DIAGNOSIS);
			c.getHealthConditions().setHivPrep(YesNoUnknown.NO);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.YES);
		});

		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, POSITIVE_TEST);
		doctorNotification(caze);

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		// from the configuration rather than from the case
		expected.put(DISEASE, value("SYPH"));
		expected.put(SUBJECT_CODE, value("SYPH"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		// from the case record
		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(OUTCOME, value("A"));
		expected.put(DATE_OF_ONSET, value("2024-06-10"));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		// earliest of the doctor's diagnosis and the first positive test, not a precedence between them
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-05"));
		// the first positive test wins over both report dates
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-12"));

		// from the person
		expected.put(AGE, value("33"));
		expected.put(GENDER, value("F"));
		expected.put(COUNTRY_OF_BIRTH, value("FR"));
		expected.put(COUNTRY_OF_NATIONALITY, value("ES"));
		expected.put(PLACE_OF_RESIDENCE, value("LU00"));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		// the columns SYPH owns
		expected.put(SITE_OF_INFECTION, value("PH"));
		expected.put(STAGE_SYPH, value("I"));
		expected.put(STAGE_SYP_HDETAILED, value("EL"));
		expected.put(SEX_WORKER, value("true"));
		expected.put(PLACE_OF_INFECTION, value("FR"));
		// the case is CONFIRMED, and PathogenDetectionResult answers from the same classification
		expected.put(PATHOGEN_DETECTION_RESULT, value("CONF"));

		// the STI risk and service block, derived by StiRiskFields and shared with gonorrhoea
		expected.put(CLINICAL_SERVICE_TYPE, value("DV"));
		expected.put(MODE_OF_TRANSMISSION, value("HETERO"));
		expected.put(HIV_STATUS, value("POSNEW"));
		expected.put(HIV_PR_EP, value("false"));
		expected.put(ANTIBIOTIC_PROPHYLAXIS, value("true"));
		expected.put(CONTACT_SW, value("false"));

		// EpiPulse does not expect these two: under the EU case definition only laboratory
		// confirmed cases are reported, so neither variable has anything to say here
		expected.put(CLINICAL_CRITERIA_STATUS, blank());
		expected.put(EPI_LINKED, blank());

		assertEveryDeclaredColumn(export, entry, EpipulseSubjectCode.SYPH, expected);
	}

	@Test
	@DisplayName("a fully populated congenital case fills every one of the 24 columns CONSYPH declares")
	public void aFullyPopulatedCongenitalCaseFillsEveryColumnConSyphDeclares() {

		assignNutsCodes(rdcf, "LU10", "LU101", "LU1010");

		// born 2024-03-15, onset 2024-06-10: not yet one year old, and two completed months - the
		// third falls due on 2024-06-15. CONSYPH is one of the three codes that declare AgeMonth,
		// which is why the infant matters.
		PersonReferenceDto infant = creator.createPerson("Luca", "Bauer", Sex.MALE, 2024, 3, 15, p -> {
			p.setBirthCountry(france);
			p.setCitizenship(spain);
		}).toReference();

		CaseDataDto caze = fixture.caze(infant, ONSET, c -> {
			c.setSyphilisPresentation(SyphilisPresentation.CONGENITAL);
			c.setOutcome(CaseOutcome.RECOVERED);
			// the mother is not a person on the case - what CONSYPH knows about her is a field on
			// the epidemiological data.
			c.getEpiData().setMotherCountryOfBirth(italy);
			c.getEpiData().setMotherCitizenship(france);
		});

		fixture.pathogenTest(caze, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE, true, POSITIVE_TEST);
		doctorNotification(caze);

		ExportedEntries export = runExport(EpipulseSubjectCode.CONSYPH, PERIOD_START, PERIOD_END);
		EpipulseDiseaseExportEntryDto entry = export.only();

		Map<EpipulseVariable, List<String>> expected = new LinkedHashMap<>();

		expected.put(SUBJECT_CODE, value("CONSYPH"));
		// EpiPulse reports congenital syphilis under the same disease code as acquired - the
		// subject code is what distinguishes the two, not this column
		expected.put(DISEASE, value("SYPH"));
		expected.put(REPORTING_COUNTRY, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(DATA_SOURCE, value(SERVER_DATA_SOURCE));
		expected.put(STATUS, value("NEW/UPDATE"));

		expected.put(NATIONAL_RECORD_ID, value(caze.getUuid()));
		expected.put(CASE_CLASSIFICATION, value("CONF"));
		expected.put(OUTCOME, value("A"));
		expected.put(DATE_OF_ONSET, value("2024-06-10"));
		expected.put(DATE_OF_NOTIFICATION, value("2024-06-10"));
		expected.put(DATE_OF_DIAGNOSIS, value("2024-06-05"));
		expected.put(DATE_USED_FOR_STATISTICS, value("2024-06-12"));

		expected.put(AGE, value("0"));
		expected.put(AGE_MONTH, value("2"));
		expected.put(GENDER, value("M"));
		// four countries, four distinct codes: the infant's birth country and citizenship, and the
		// mother's. Any join reading the wrong one shows up as another column's value rather than
		// as a blank.
		expected.put(COUNTRY_OF_BIRTH, value("FR"));
		expected.put(COUNTRY_OF_BIRTH_OF_MOTHER, value("IT"));
		expected.put(COUNTRY_OF_NATIONALITY, value("ES"));
		expected.put(COUNTRY_OF_NATIONALITY_OF_MOTHER, value("FR"));
		// no address on the person, so residence falls through to the server country
		expected.put(PLACE_OF_RESIDENCE, value(SERVER_COUNTRY_NUTS_CODE));
		expected.put(PLACE_OF_NOTIFICATION, value("LU1010"));

		expected.put(PATHOGEN_DETECTION_RESULT, value("CONF"));

		// the two SORMAS has no answer for
		expected.put(CLINICAL_CRITERIA_STATUS, blank());
		expected.put(EPI_LINKED, blank());

		assertEveryDeclaredColumn(export, entry, EpipulseSubjectCode.CONSYPH, expected);
	}

	@Test
	@DisplayName("a case that answers nothing still writes every declared column, for both codes")
	public void aCaseThatAnswersNothingStillWritesEveryDeclaredColumn() {

		// Nothing recorded beyond what creating a case requires. Both codes in one method: the
		// database is truncated per test method, so a second export costs a statement and a second
		// case almost nothing.
		syphilisCase(SyphilisPresentation.ACQUIRED);
		syphilisCase(SyphilisPresentation.CONGENITAL);

		assertHeaderCarriesEveryDeclaredColumn(EpipulseSubjectCode.SYPH);
		assertHeaderCarriesEveryDeclaredColumn(EpipulseSubjectCode.CONSYPH);
	}

	// ---------------------------------------------------------------------------------------
	// the columns SYPH owns, across their whole value set
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("every recorded site, infectiousness and stage reaches its EpiPulse code")
	public void everyRecordedSiteInfectiousnessAndStageReachesItsCode() {

		// One case per SORMAS constant, all in one export and told apart by NationalRecordId. This
		// is the end-to-end half of the reference-value tripwires in sormas-api: those check the
		// mapping, this checks that the column is selected, parsed and reported at all.
		Map<String, String> sites = new LinkedHashMap<>();
		for (Map.Entry<SyphilisInfectionSite, String> site : SITE_CODES.entrySet()) {
			sites.put(acquiredCaseWith(c -> c.getSymptoms().setSyphilisInfectionSite(site.getKey())).getUuid(), site.getValue());
		}

		Map<String, String> infectiousness = new LinkedHashMap<>();
		for (Map.Entry<SyphilisInfectiousness, String> stage : INFECTIOUSNESS_CODES.entrySet()) {
			infectiousness.put(acquiredCaseWith(c -> c.getSymptoms().setSyphilisInfectiousness(stage.getKey())).getUuid(), stage.getValue());
		}

		Map<String, String> stages = new LinkedHashMap<>();
		for (Map.Entry<SyphilisStage, String> stage : STAGE_CODES.entrySet()) {
			stages.put(acquiredCaseWith(c -> c.getSymptoms().setSyphilisStage(stage.getKey())).getUuid(), stage.getValue());
		}

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		for (Map.Entry<String, String> expected : sites.entrySet()) {
			// SiteOfInfection is a floored repeatable group, so a case with no code still holds one
			// blank column rather than none
			assertThat(
				"SiteOfInfection for " + expected.getKey(),
				export.values(export.entryFor(expected.getKey()), SITE_OF_INFECTION),
				is(expected.getValue() == null ? blank() : value(expected.getValue())));
		}

		for (Map.Entry<String, String> expected : infectiousness.entrySet()) {
			assertThat(
				"StageSYPH for " + expected.getKey(),
				export.value(export.entryFor(expected.getKey()), STAGE_SYPH),
				is(expected.getValue() == null ? "" : expected.getValue()));
		}

		for (Map.Entry<String, String> expected : stages.entrySet()) {
			assertThat(
				"StageSYPHdetailed for " + expected.getKey(),
				export.value(export.entryFor(expected.getKey()), STAGE_SYP_HDETAILED),
				is(expected.getValue() == null ? "" : expected.getValue()));
		}
	}

	@Test
	@DisplayName("SexWorker reports nothing when the answer was not established")
	public void sexWorkerReportsNothingWhenNotEstablished() {

		String yes = acquiredCaseWith(c -> c.getEpiData().setSexWorker(YesNoUnknown.YES)).getUuid();
		String no = acquiredCaseWith(c -> c.getEpiData().setSexWorker(YesNoUnknown.NO)).getUuid();
		String unknown = acquiredCaseWith(c -> c.getEpiData().setSexWorker(YesNoUnknown.UNKNOWN)).getUuid();
		String unanswered = syphilisCase(SyphilisPresentation.ACQUIRED).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(yes), SEX_WORKER), is("true"));
		assertThat(export.value(export.entryFor(no), SEX_WORKER), is("false"));
		// an EpiPulse BOOL has two states, and "not established" is neither of them - so UNKNOWN
		// reports blank rather than false, as it does everywhere else in the export
		assertThat(export.value(export.entryFor(unknown), SEX_WORKER), is(""));
		assertThat(export.value(export.entryFor(unanswered), SEX_WORKER), is(""));
	}

	@Test
	@DisplayName("the STI risk block reports the same codes here as it does for gonorrhoea")
	public void theStiRiskBlockReportsTheSameCodes() {

		// Six columns syphilis used to report blank. They are derived by StiRiskFields, which GONO
		// shares, so this asserts the block from the syphilis side rather than re-deriving it: what
		// could break here and not there is the pair of table aliases each model passes in, and a
		// wrong one is a column reading another row's value or no value at all.
		String recorded = acquiredCaseWith(c -> {
			c.getEpiData().setTypeOfClinicalService(TypeOfClinicalService.YOUTH_CLINIC);
			c.getEpiData().setProbableRouteOfTransmission(ProbableRouteOfTransmission.MSM_HOMO_OR_BISEXUAL_MALE);
			c.getEpiData().setContactWithSexWorker(YesNoUnknown.YES);
			c.getHealthConditions().setHivStatus(HivStatus.KNOWN_POSITIVE);
			c.getHealthConditions().setHivPrep(YesNoUnknown.YES);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.NO);
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(recorded), CLINICAL_SERVICE_TYPE), is("YTH"));
		assertThat(export.value(export.entryFor(recorded), MODE_OF_TRANSMISSION), is("MSM"));
		assertThat(export.value(export.entryFor(recorded), HIV_STATUS), is("POSKNOWN"));
		assertThat(export.value(export.entryFor(recorded), HIV_PR_EP), is("true"));
		assertThat(export.value(export.entryFor(recorded), ANTIBIOTIC_PROPHYLAXIS), is("false"));
		assertThat(export.value(export.entryFor(recorded), CONTACT_SW), is("true"));
	}

	@Test
	@DisplayName("the HIV and prophylaxis columns come from the case's own health conditions")
	public void theHealthConditionColumnsComeFromTheRightCase() {

		// The three columns read through the healthconditions join.
		// Two cases with different answers is the assertion that matters: a join
		// on the wrong key, or one aliased to another model's, would give both cases the same row -
		// and a single-case test would pass on either.
		String positive = acquiredCaseWith(c -> {
			c.getHealthConditions().setHivStatus(HivStatus.POSITIVE);
			c.getHealthConditions().setHivPrep(YesNoUnknown.YES);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.YES);
		}).getUuid();

		String negative = acquiredCaseWith(c -> {
			c.getHealthConditions().setHivStatus(HivStatus.NEGATIVE);
			c.getHealthConditions().setHivPrep(YesNoUnknown.NO);
			c.getHealthConditions().setStiProphylaxis(YesNoUnknown.UNKNOWN);
		}).getUuid();

		String nothingRecorded = syphilisCase(SyphilisPresentation.ACQUIRED).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(positive), HIV_STATUS), is("POS"));
		assertThat(export.value(export.entryFor(positive), HIV_PR_EP), is("true"));
		assertThat(export.value(export.entryFor(positive), ANTIBIOTIC_PROPHYLAXIS), is("true"));

		assertThat(export.value(export.entryFor(negative), HIV_STATUS), is("NEG"));
		assertThat(export.value(export.entryFor(negative), HIV_PR_EP), is("false"));
		// an unknown answer is not a false one, here as everywhere else
		assertThat(export.value(export.entryFor(negative), ANTIBIOTIC_PROPHYLAXIS), is(""));

		assertThat(export.value(export.entryFor(nothingRecorded), HIV_STATUS), is(""));
		assertThat(export.value(export.entryFor(nothingRecorded), HIV_PR_EP), is(""));
		assertThat(export.value(export.entryFor(nothingRecorded), ANTIBIOTIC_PROPHYLAXIS), is(""));
	}

	@Test
	@DisplayName("PathogenDetectionResult reports only the two classifications EpiPulse allows it")
	public void pathogenDetectionResultReportsOnlyConfirmedAndProbable() {

		String confirmed = acquiredCaseClassified(CaseClassification.CONFIRMED).getUuid();
		String probable = acquiredCaseClassified(CaseClassification.PROBABLE).getUuid();
		String suspect = acquiredCaseClassified(CaseClassification.SUSPECT).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		assertThat(export.value(export.entryFor(confirmed), PATHOGEN_DETECTION_RESULT), is("CONF"));
		assertThat(export.value(export.entryFor(probable), PATHOGEN_DETECTION_RESULT), is("PROB"));

		// CaseClassification offers CONF, POSS and PROB; PathogenDetectionResult only CONF and
		// PROB. A suspect case reports its classification and nothing here.
		assertThat(export.value(export.entryFor(suspect), CASE_CLASSIFICATION), is("POSS"));
		assertThat(export.value(export.entryFor(suspect), PATHOGEN_DETECTION_RESULT), is(""));
	}

	// ---------------------------------------------------------------------------------------
	// PlaceOfInfection - SYPH is the first production code on the LOWEST_LEVEL rule
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("PlaceOfInfection prefers a stated country of contamination over travel, and ignores exposures outside the incubation window")
	public void placeOfInfectionPrefersTheStatedCountryOfContamination() {

		String travelled = acquiredCaseWith(c -> c.getEpiData().getExposures().add(travelTo(france))).getUuid();

		// a country of contamination is an assertion about the infection where the exposures are a
		// record of travel, so it wins rather than joining them
		String stated = acquiredCaseWith(c -> {
			c.getEpiData().setCountry(italy);
			c.getEpiData().getExposures().add(travelTo(france));
		}).getUuid();

		// ...but only for a case that is not imported: an imported case already carries the
		// exposures that say where the patient was, and those are the richer answer
		String imported = acquiredCaseWith(c -> {
			c.getEpiData().setCaseImportedStatus(CaseImportedStatus.IMPORTED_CASE);
			c.getEpiData().setCountry(italy);
			c.getEpiData().getExposures().add(travelTo(france));
		}).getUuid();

		// syphilis incubates 10 to 90 days, so travel five days before onset cannot be where the
		// infection was acquired
		String tooLate = acquiredCaseWith(c -> {
			ExposureDto exposure = travelTo(france);
			exposure.setStartDate(DateHelper.subtractDays(ONSET, 5));
			exposure.setEndDate(DateHelper.subtractDays(ONSET, 3));
			c.getEpiData().getExposures().add(exposure);
		}).getUuid();

		ExportedEntries export = runExport(EpipulseSubjectCode.SYPH, PERIOD_START, PERIOD_END);

		assertThat(export.values(export.entryFor(travelled), PLACE_OF_INFECTION), contains("FR"));
		assertThat(export.values(export.entryFor(stated), PLACE_OF_INFECTION), contains("IT"));
		assertThat(export.values(export.entryFor(imported), PLACE_OF_INFECTION), contains("FR"));
		// the group is floored, so a case with no place keeps one blank column rather than none
		assertThat(export.values(export.entryFor(tooLate), PLACE_OF_INFECTION), is(blank()));
	}

	// ---------------------------------------------------------------------------------------
	// assertions
	// ---------------------------------------------------------------------------------------

	/**
	 * Asserts one exported case column by column, having first established that the expectation
	 * covers the subject code's whole declared column set.
	 *
	 * <p>
	 * The set check is what makes the caller's map a contract rather than a sample. A variable
	 * added to the metadata for this code, or one dropped from it, fails here naming the
	 * difference - and it fails in the test whose job is to say what every column reports, rather
	 * than being exported with nothing asserting its value.
	 */
	private void assertEveryDeclaredColumn(
		ExportedEntries export,
		EpipulseDiseaseExportEntryDto entry,
		EpipulseSubjectCode subjectCode,
		Map<EpipulseVariable, List<String>> expected) {

		assertThat(
			"the expectations must name every variable EpiPulse declares for " + subjectCode + ", and no other",
			expected.keySet(),
			is(EpipulseSubjectCodeVariables.of(subjectCode)));

		for (Map.Entry<EpipulseVariable, List<String>> column : expected.entrySet()) {
			assertThat(column.getKey().getVariableName(), export.values(entry, column.getKey()), is(column.getValue()));
		}
	}

	/**
	 * Asserts that the file this export would write carries a column for every variable the subject
	 * code declares.
	 */
	private void assertHeaderCarriesEveryDeclaredColumn(EpipulseSubjectCode subjectCode) {

		ExportedEntries export = runExport(subjectCode, PERIOD_START, PERIOD_END);
		assertThat(subjectCode + " exported no case to measure the header against", export.all(), hasSize(1));

		for (EpipulseVariable variable : EpipulseSubjectCodeVariables.of(subjectCode)) {
			assertThat(subjectCode + " dropped " + variable.getVariableName(), export.header(), hasItem(variable.getVariableName()));
		}
	}

	private static List<String> value(String value) {
		return Collections.singletonList(value);
	}

	/** One column, present and empty - which is what a declared variable with no answer writes. */
	private static List<String> blank() {
		return Collections.singletonList("");
	}

	// ---------------------------------------------------------------------------------------
	// fixture
	// ---------------------------------------------------------------------------------------

	private CaseDataDto syphilisCase(SyphilisPresentation presentation) {
		return fixture.caze(newPerson(), ONSET, caze -> caze.setSyphilisPresentation(presentation));
	}

	private CaseDataDto acquiredCaseWith(java.util.function.Consumer<CaseDataDto> extraConfig) {

		return fixture.caze(newPerson(), ONSET, caze -> {
			caze.setSyphilisPresentation(SyphilisPresentation.ACQUIRED);
			extraConfig.accept(caze);
		});
	}

	private CaseDataDto acquiredCaseClassified(CaseClassification classification) {
		return acquiredCaseWith(caze -> caze.setCaseClassification(classification));
	}

	private PersonReferenceDto newPerson() {
		return creator.createPerson().toReference();
	}

	/** A dated stay in one country, inside the incubation window unless a caller moves it. */
	private ExposureDto travelTo(CountryReferenceDto country) {

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setStartDate(EXPOSURE_START);
		exposure.setEndDate(EXPOSURE_END);
		exposure.getLocation().setCountry(country);

		return exposure;
	}

	/**
	 * A doctor's notification carrying a date of diagnosis, which is one of the two dates
	 * {@code DateOfDiagnosis} takes the earlier of.
	 */
	private void doctorNotification(CaseDataDto caze) {

		creator.createSurveillanceReport(reportingUser, ReportingType.DOCTOR, caze.toReference(), report -> {
			report.setDateOfDiagnosis(DOCTOR_DIAGNOSIS);
			report.setReportDate(DOCTOR_DIAGNOSIS);
		});
	}

	/**
	 * A country the export can report, which means one with a NUTS code - {@code TestDataCreator}
	 * does not set that column, and without it the country is invisible to every place column.
	 */
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
