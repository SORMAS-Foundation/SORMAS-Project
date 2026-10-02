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

package de.symeda.sormas.api.epipulse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseOutcomeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDiseaseRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseGenderRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePneumococcalSerotypingMethodRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseVaccinationStatusRef;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Pure mapping functions from export DTO fields to EpiPulse variable values.
 *
 * <p>
 * Field models bind variables to these methods (for example
 * {@code ValueDef.calculated(EpipulseVariable.AGE, EpipulseMapping::ageInYears)}), so derivation
 * logic is explicit at declaration site. Methods are null-tolerant because each disease selects
 * only a subset of columns.
 *
 * <p>
 * Date/age precedence rules are centralized in {@link EpipulseCaseDates}; this class mainly renders
 * those results.
 */
public final class EpipulseMapping {

	/** Explicit "none" code; distinct from missing information. */
	private static final String NO_COMPLICATIONS = "NONE";

	private EpipulseMapping() {
	}

	// --- record envelope ---

	public static String disease(EpipulseDiseaseExportEntryDto entry) {

		if (entry.getSubjectCode() == null) {
			return "";
		}

		EpipulseDiseaseRef diseaseRef = EpipulseDiseaseRef.getBySubjectCode(entry.getSubjectCode());

		return diseaseRef == null ? "" : diseaseRef.name();
	}

	public static String reportingCountry(EpipulseDiseaseExportEntryDto entry) {
		return entry.getReportingCountry();
	}

	public static String status(EpipulseDiseaseExportEntryDto entry) {

		return entry.getDeleted() == Boolean.FALSE ? EpipulseStatusRef.NEW_UPDATE.getCode() : EpipulseStatusRef.DELETE.getCode();
	}

	public static String subjectCode(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSubjectCode() == null ? "" : entry.getSubjectCode().name();
	}

	public static String nationalRecordId(EpipulseDiseaseExportEntryDto entry) {
		return entry.getNationalRecordId();
	}

	public static String dataSource(EpipulseDiseaseExportEntryDto entry) {
		return entry.getDataSource();
	}

	/** {@code DateUsedForStatistics}; see {@link EpipulseCaseDates#dateUsedForStatistics}. */
	public static String dateUsedForStatistics(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(EpipulseCaseDates.dateUsedForStatistics(entry));
	}

	// --- demographics ---

	/** {@code Age} in completed years at {@link EpipulseCaseDates#ageReferenceDate}. */
	public static String ageInYears(EpipulseDiseaseExportEntryDto entry) {

		Integer years = EpipulseCaseDates.ageYears(entry);

		return years == null ? null : years.toString();
	}

	/** {@code AgeMonth} in completed months, reported only when age is below 2 years. */
	public static String ageInMonths(EpipulseDiseaseExportEntryDto entry) {

		Integer years = EpipulseCaseDates.ageYears(entry);
		if (years == null || years >= 2) {
			return null;
		}

		Integer months = EpipulseCaseDates.ageMonths(entry);

		return months == null ? null : months.toString();
	}

	public static String gender(EpipulseDiseaseExportEntryDto entry) {

		EpipulseGenderRef genderRef = EpipulseGenderRef.getByGender(entry.getSex());

		return genderRef == null ? null : genderRef.name();
	}

	/**
	 * {@code PlaceOfResidence} at the finest available NUTS level.
	 * Fallback order: community -> district -> region -> address country -> server country.
	 */
	public static String placeOfResidence(EpipulseDiseaseExportEntryDto entry) {

		return firstPopulated(
			entry.getAddressCommunityNutsCode(),
			entry.getAddressDistrictNutsCode(),
			entry.getAddressRegionNutsCode(),
			entry.getAddressCountryNutsCode(),
			entry.getServerCountryNutsCode());
	}

	/**
	 * {@code PlaceOfNotification} at the finest responsible-jurisdiction NUTS level.
	 * Fallback order: responsible community -> district -> region -> server country.
	 */
	public static String placeOfNotification(EpipulseDiseaseExportEntryDto entry) {

		return firstPopulated(
			entry.getResponsibleCommunityNutsCode(),
			entry.getResponsibleDistrictNutsCode(),
			entry.getResponsibleRegionNutsCode(),
			entry.getServerCountryNutsCode());
	}

	// --- case ---

	/**
	 * {@code CaseClassification} as recorded in SORMAS.
	 *
	 * <p>
	 * No subject-code-specific narrowing is applied here. Any resulting EpiPulse rejection must be
	 * handled by case filtering, not by silently changing this value.
	 */
	public static String caseClassification(EpipulseDiseaseExportEntryDto entry) {

		EpipulseCaseClassificationRef classificationRef = EpipulseCaseClassificationRef.getByCaseClassification(entry.getCaseClassification());

		return classificationRef == null ? null : classificationRef.name();
	}

	/** {@code DateOfOnset}; see {@link EpipulseCaseDates#dateOfOnset}. */
	public static String dateOfOnset(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(EpipulseCaseDates.dateOfOnset(entry));
	}

	public static String dateOfNotification(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getReportDate());
	}

	public static String dateOfInvestigation(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getDateOfInvestigation());
	}

	/** {@code DateOfDiagnosis}; see {@link EpipulseCaseDates#dateOfDiagnosis}. */
	public static String dateOfDiagnosis(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(EpipulseCaseDates.dateOfDiagnosis(entry));
	}

	/**
	 * {@code Hospitalisation}: true if any current/previous stay for reported disease includes at
	 * least one overnight. See {@link #hospitalizedFor}.
	 */
	public static String hospitalisation(EpipulseDiseaseExportEntryDto entry) {

		if (hospitalizedFor(
			entry.getAdmittedToHealthFacility(),
			entry.getHospitalizationReason(),
			entry.getAdmissionDate(),
			entry.getDischargeDate())) {
			return String.valueOf(true);
		}

		if (entry.getPreviousHospitalizations() != null) {
			for (EpipulseHospitalizationCheckDto previous : entry.getPreviousHospitalizations()) {
				if (hospitalizedFor(
					previous.getAdmittedToHealthFacility(),
					previous.getHospitalizationReason(),
					previous.getAdmissionDate(),
					previous.getDischargeDate())) {
					return String.valueOf(true);
				}
			}
		}

		return String.valueOf(false);
	}

	public static String outcome(EpipulseDiseaseExportEntryDto entry) {

		EpipulseCaseOutcomeRef outcomeRef = EpipulseCaseOutcomeRef.getByCaseOutcome(entry.getCaseOutcome());

		return outcomeRef == null ? null : outcomeRef.name();
	}

	public static String causeOfDeath(EpipulseDiseaseExportEntryDto entry) {
		return entry.getCauseOfDeath();
	}

	// --- clinical ---

	public static String clinicalCriteria(EpipulseDiseaseExportEntryDto entry) {
		return entry.getClinicalCriteria();
	}

	public static String clinicalCriteriaStatus(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getClinicalCriteriaStatus());
	}

	// ==================== Syphilis ====================

	/** {@code CountryOfBirth} as NUTS code. */
	public static String countryOfBirth(EpipulseDiseaseExportEntryDto entry) {
		return entry.getBirthCountryNutsCode();
	}

	/** {@code CountryOfBirthOfMother} as NUTS code. */
	public static String countryOfBirthOfMother(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMotherBirthCountryNutsCode();
	}

	/** {@code CountryOfNationality} as NUTS code. */
	public static String countryOfNationality(EpipulseDiseaseExportEntryDto entry) {
		return entry.getCitizenshipCountryNutsCode();
	}

	/**
	 * {@code CountryOfNationalityOfMother} from epidemiological mother-citizenship data (CONSYPH).
	 */
	public static String countryOfNationalityOfMother(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMotherCitizenshipCountryNutsCode();
	}

	/**
	 * {@code SiteOfInfection} values; empty list if absent so no repeatable column is emitted.
	 */
	public static List<String> siteOfInfection(EpipulseDiseaseExportEntryDto entry) {

		List<String> sites = entry.getSiteOfInfection();

		return sites == null ? Collections.emptyList() : sites;
	}

	public static String stageSyph(EpipulseDiseaseExportEntryDto entry) {
		return entry.getStageSyph();
	}

	public static String stageSyphDetailed(EpipulseDiseaseExportEntryDto entry) {
		return entry.getStageSyphDetailed();
	}

	public static String sexWorker(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getSexWorker());
	}

	/**
	 * {@code PathogenDetectionResult} derived from case classification.
	 * Only {@code CONF}/{@code PROB} are valid here; other classifications map to blank.
	 */
	public static String pathogenDetectionResult(EpipulseDiseaseExportEntryDto entry) {

		String classification = caseClassification(entry);

		return EpipulseCaseClassificationRef.CONF.name().equals(classification) || EpipulseCaseClassificationRef.PROB.name().equals(classification)
			? classification
			: null;
	}

	/**
	 * Complication codes, or a single {@code NONE} when explicitly absent.
	 */
	public static List<String> complicationDiagnosis(EpipulseDiseaseExportEntryDto entry) {

		List<String> complications = entry.getComplicationDiagnosis();

		return complications == null || complications.isEmpty() ? Collections.singletonList(NO_COMPLICATIONS) : complications;
	}

	public static List<String> clinicalPresentation(EpipulseDiseaseExportEntryDto entry) {
		return entry.getClinicalPresentation();
	}

	// --- epidemiology ---

	public static String clusterRelated(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getClusterRelated());
	}

	public static String clusterIdentification(EpipulseDiseaseExportEntryDto entry) {
		return entry.getClusterIdentification();
	}

	public static List<String> clusterSetting(EpipulseDiseaseExportEntryDto entry) {
		return entry.getClusterSetting();
	}

	public static String importedStatus(EpipulseDiseaseExportEntryDto entry) {
		return entry.getImportedStatus();
	}

	/**
	 * {@code Imported} BOOL for acquisition outside reporting country.
	 * Unknown stays blank (not {@code false}).
	 */
	public static String imported(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getImported());
	}

	/**
	 * {@code ModeOfTransmission} code from the subject-code-specific reference set.
	 */
	public static String modeOfTransmission(EpipulseDiseaseExportEntryDto entry) {
		return entry.getModeOfTransmission();
	}

	/** {@code SuspectedVehicle}: suspected food/animal source. */
	public static String suspectedVehicle(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSuspectedVehicle();
	}

	public static List<String> placeOfInfection(EpipulseDiseaseExportEntryDto entry) {
		return entry.getPlaceOfInfection();
	}

	// --- laboratory ---

	/**
	 * {@code Specimen} using enteric 6-value mapping; see
	 * {@code EpipulseLaboratoryMapper.mapSampleMaterialToEntericSpecimenCode}.
	 */
	public static String specimen(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSpecimen();
	}

	/** {@code DateOfReceiptReferenceLab}. */
	public static String dateOfReceiptReferenceLab(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getDateOfReceiptReferenceLab());
	}

	public static String dateOfSpecimen(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getDateOfSpecimen());
	}

	public static String dateOfLaboratoryResult(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getDateOfLaboratoryResult());
	}

	public static List<String> specimenVirDetect(EpipulseDiseaseExportEntryDto entry) {
		return entry.getTypeOfSpecimenCollected();
	}

	public static List<String> specimenSero(EpipulseDiseaseExportEntryDto entry) {
		return entry.getTypeOfSpecimenSerology();
	}

	public static String resultOfVirusDetection(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultOfVirusDetection();
	}

	public static String genotype(EpipulseDiseaseExportEntryDto entry) {
		return entry.getGenotype();
	}

	public static String resultIgG(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultIgG();
	}

	public static String resultIgM(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultIgM();
	}

	public static String resultOfCulture(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultOfCulture();
	}

	public static String resultOfPCR(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultOfPCR();
	}

	/**
	 * {@code Serotype} code from subject-code-specific reference mapping.
	 *
	 * <p>
	 * This method returns pre-mapped DTO value; disease-specific field models decide whether and how
	 * a SORMAS serotype can be expressed for their EpiPulse subject code.
	 */
	public static String serotype(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSerotype();
	}

	public static String serogroup(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSerogroup();
	}

	public static String serogroupMethod(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSerogroupMethod();
	}

	public static String penicillinResistance(EpipulseDiseaseExportEntryDto entry) {
		return entry.getPenicillinResistance();
	}

	public static String nrlData(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getNrlData());
	}

	/**
	 * Repeatable {@code PathogenDetectionMethod} values for qualifying positive tests.
	 * Mapping is subject-code specific ({@link EpipulsePathogenTestTypeRef}); duplicate mapped codes
	 * are removed.
	 */
	public static List<String> pathogenDetectionMethods(EpipulseDiseaseExportEntryDto entry) {

		List<String> detectionMethods = new ArrayList<>();
		if (entry.getPathogenTests() == null) {
			return detectionMethods;
		}

		for (EpipulsePathogentTestCheckDto pathogenTest : entry.getPathogenTests()) {

			String code = EpipulsePathogenTestTypeRef.codeFor(entry.getSubjectCode(), pathogenTest.getTestType());
			if (code != null && !detectionMethods.contains(code)) {
				detectionMethods.add(code);
			}
		}

		return detectionMethods;
	}

	/**
	 * PNEU-specific repeatable detection methods used for serotyping.
	 * Mapping happens before de-duplication so many-to-one conversions (for example to {@code OTH})
	 * collapse correctly.
	 */
	public static List<String> serotypingMethods(EpipulseDiseaseExportEntryDto entry) {

		List<String> codes = new ArrayList<>();
		if (entry.getSerotypingMethods() == null) {
			return codes;
		}

		for (SerotypingMethod method : entry.getSerotypingMethods()) {

			String code = EpipulsePneumococcalSerotypingMethodRef.resolve(method);
			if (code != null && !codes.contains(code)) {
				codes.add(code);
			}
		}

		return codes;
	}

	/**
	 * {@code MainPathogenDetectionMethod}/{@code SecondPathogenDetectionMethod} are intentionally
	 * unimplemented: EpiPulse requires specimen ranking (primary/second), and SORMAS does not store
	 * that ranking.
	 *
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public static List<String> mainPathogenDetectionMethods(EpipulseDiseaseExportEntryDto entry) {
		throw new UnsupportedOperationException(
			"MainPathogenDetectionMethod is not implemented: EpiPulse splits it from SecondPathogenDetectionMethod by "
				+ "specimen, and SORMAS records nothing that says which of a case's specimens is the primary one; see the javadoc.");
	}

	/**
	 * @see #mainPathogenDetectionMethods
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public static List<String> secondPathogenDetectionMethods(EpipulseDiseaseExportEntryDto entry) {
		throw new UnsupportedOperationException(
			"SecondPathogenDetectionMethod is not implemented: EpiPulse defines it as the method used on a second type of "
				+ "specimen with a positive result, and SORMAS records nothing that ranks specimens; see the javadoc.");
	}

	public static String resultPorA1(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultPorA1();
	}

	public static String resultPorA2(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultPorA2();
	}

	public static String resultFetVR(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultFetVR();
	}

	/**
	 * {@code ReportedEMERTII} is intentionally unimplemented.
	 * SORMAS has no field proving submission to EMERT II; deriving this from sample IDs is invalid.
	 *
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public static String reportedEmertii(EpipulseDiseaseExportEntryDto entry) {
		throw new UnsupportedOperationException(
			"ReportedEMERTII is not implemented: SORMAS records nothing about an isolate having been submitted to "
				+ "EMERT II. It needs a field recording the submission; see the javadoc.");
	}

	// ==================== Sexually transmitted infections ====================

	/** {@code ClinicalServiceType}; see {@code EpipulseClinicalServiceTypeRef}. */
	public static String clinicalServiceType(EpipulseDiseaseExportEntryDto entry) {
		return entry.getClinicalServiceType();
	}

	/** {@code HIVStatus}; see {@code EpipulseStiHivStatusRef}. */
	public static String hivStatus(EpipulseDiseaseExportEntryDto entry) {
		return entry.getHivStatus();
	}

	/**
	 * {@code HIVPrEP} BOOL (spec-defined 12-month window already encoded in source question).
	 */
	public static String hivPrEP(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getHivPrEP());
	}

	/**
	 * {@code AntibioticProphylaxis} BOOL (prophylaxis use in previous 3 months).
	 */
	public static String antibioticProphylaxis(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getAntibioticProphylaxis());
	}

	/** {@code ContactSW} BOOL for recent contact within infection incubation period. */
	public static String contactSW(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getContactSW());
	}

	// ==================== Malaria ====================

	/**
	 * {@code Occupation} for MALA; see {@code EpipulseOccupationRef}.
	 */
	public static String occupation(EpipulseDiseaseExportEntryDto entry) {
		return entry.getOccupation();
	}

	/**
	 * Repeatable MALA {@code Pathogen} codes (Plasmodium species), pre-distinct upstream.
	 */
	public static List<String> pathogen(EpipulseDiseaseExportEntryDto entry) {
		return entry.getPathogen();
	}

	/**
	 * Single-valued rendering of {@code Pathogen} for non-repeatable subject codes.
	 * If multiple codes are present, first value is used (field-model contract violation otherwise).
	 */
	public static String pathogenCode(EpipulseDiseaseExportEntryDto entry) {

		List<String> codes = entry.getPathogen();

		return codes == null || codes.isEmpty() ? null : codes.get(0);
	}

	/**
	 * {@code Prophylaxis} BOOL for completed travel chemoprophylaxis course.
	 * Unknown adherence stays blank.
	 */
	public static String prophylaxis(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getProphylaxis());
	}

	/** {@code PurposeOfTravel} for MALA; see {@code EpipulsePurposeOfTravelRef}. */
	public static String purposeOfTravel(EpipulseDiseaseExportEntryDto entry) {
		return entry.getPurposeOfTravel();
	}

	// ==================== Dengue ====================

	/**
	 * DENGUE detection methods: base methods plus {@code SCONV} when seroconversion/titre rise is
	 * flagged.
	 */
	public static List<String> dengueDetectionMethods(EpipulseDiseaseExportEntryDto entry) {

		List<String> methods = pathogenDetectionMethods(entry);

		if (Boolean.TRUE.equals(entry.getSeroconversionOrTitreRise())) {
			methods.add("SCONV");
		}

		return methods;
	}

	// ==================== Rubella ====================

	/** {@code Pregnancy} BOOL at infection time. */
	public static String pregnancy(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getPregnancy());
	}

	// --- antimicrobial susceptibility testing ---

	public static String astMethod(EpipulseDiseaseExportEntryDto entry) {
		return entry.getAstMethod();
	}

	public static String micSign_CTX_CFX(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMicSign_CTX_CFX();
	}

	public static String micValueAST_CTX_CFX(EpipulseDiseaseExportEntryDto entry) {
		return micValue(entry.getMicValueAST_CTX_CFX());
	}

	public static String sir_CTX_CFX(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_CTX_CFX();
	}

	public static String micSign_ERY(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMicSign_ERY();
	}

	public static String micValueAST_ERY(EpipulseDiseaseExportEntryDto entry) {
		return micValue(entry.getMicValueAST_ERY());
	}

	public static String sir_ERY(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_ERY();
	}

	public static String micSign_PEN(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMicSign_PEN();
	}

	public static String micValueAST_PEN(EpipulseDiseaseExportEntryDto entry) {
		return micValue(entry.getMicValueAST_PEN());
	}

	public static String sir_PEN(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_PEN();
	}

	public static String micSign_CIP(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMicSign_CIP();
	}

	public static String micValueAST_CIP(EpipulseDiseaseExportEntryDto entry) {
		return micValue(entry.getMicValueAST_CIP());
	}

	public static String sir_CIP(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_CIP();
	}

	public static String micSign_RIF(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMicSign_RIF();
	}

	public static String micValueAST_RIF(EpipulseDiseaseExportEntryDto entry) {
		return micValue(entry.getMicValueAST_RIF());
	}

	public static String sir_RIF(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_RIF();
	}

	// Enteric panel is SIR-only: SALM declares no MICSign/MICValueAST for these drugs.

	public static String sir_AMP(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_AMP();
	}

	public static String sir_CAZ(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_CAZ();
	}

	public static String sir_CTX(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_CTX();
	}

	public static String sir_SXT(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_SXT();
	}

	// --- vaccination ---

	/**
	 * {@code DateOfLastVaccination}: latest dose date before onset from the same selected course used
	 * by {@code VaccinationStatus}.
	 *
	 * <p>
	 * Dose date uses administration date with report-date fallback
	 * ({@link EpipulseCaseDates#vaccinationDate}); same-day-or-later doses are excluded.
	 */
	public static String dateOfLastVaccination(EpipulseDiseaseExportEntryDto entry) {

		Date onset = EpipulseCaseDates.dateOfOnset(entry);
		if (onset == null) {
			return "";
		}

		Date onsetDay = DateHelper.getStartOfDay(onset);
		EpipulseImmunizationCheckDto course = relevantImmunization(entry, onsetDay);
		if (course == null || entry.getVaccinations() == null) {
			return "";
		}

		Date latest = null;
		for (EpipulseVaccinationCheckDto vaccination : entry.getVaccinations()) {

			if (!Objects.equals(vaccination.getImmunizationId(), course.getId())) {
				continue;
			}

			Date administered = EpipulseCaseDates.vaccinationDate(vaccination);
			if (administered == null || !administered.before(onsetDay)) {
				continue;
			}

			if (latest == null || administered.after(latest)) {
				latest = administered;
			}
		}

		return formatDate(latest);
	}

	/**
	 * {@code VaccinationStatus} as EpiPulse dose code from the relevant immunization course.
	 *
	 * <p>
	 * There is no fallback date usage, the vaccination status is derived based of onset date or none at all.
	 * 
	 * <p>
	 * Selection follows onset-based BR0010-BR0012 logic (shared with
	 * {@code ImmunizationService.deriveVaccinationStatus}): course must be valid at onset and latest
	 * by {@code validFrom}. No qualifying course -> {@code NOTVACC}; missing dose count ->
	 * {@code UNKDOSE}. Dose value is capped by {@code maxDoses} per subject code.
	 */
	public static String vaccinationStatus(EpipulseDiseaseExportEntryDto entry, int maxDoses) {

		Date onset = EpipulseCaseDates.dateOfOnset(entry);
		if (onset == null) {
			return "";
		}

		EpipulseImmunizationCheckDto reported = relevantImmunization(entry, DateHelper.getStartOfDay(onset));
		if (reported == null) {
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		Integer doses = reported.getNumberOfDoses();

		return doses == null ? EpipulseVaccinationStatusRef.UNKDOSE.getCode() : doseCountCode(Math.min(doses, maxDoses));
	}

	/**
	 * Selects the single immunization course shared by vaccination status/date-of-last-dose logic.
	 * Requires validity window to include onset day and chooses latest {@code validFrom}.
	 */
	private static EpipulseImmunizationCheckDto relevantImmunization(EpipulseDiseaseExportEntryDto entry, Date onsetDay) {

		if (entry.getImmunizations() == null) {
			return null;
		}

		EpipulseImmunizationCheckDto relevant = null;
		for (EpipulseImmunizationCheckDto immunization : entry.getImmunizations()) {

			if (immunization.getValidFrom() == null || immunization.getValidUntil() == null) {
				continue;
			}

			Date validFrom = DateHelper.getStartOfDay(immunization.getValidFrom());
			if (validFrom.after(onsetDay) || DateHelper.getStartOfDay(immunization.getValidUntil()).before(onsetDay)) {
				continue;
			}

			if (relevant == null || validFrom.after(DateHelper.getStartOfDay(relevant.getValidFrom()))) {
				relevant = immunization;
			}
		}

		return relevant;
	}

	// --- shared helpers ---

	/**
	 * True when stay was for reported disease and is overnight or longer.
	 * Missing admission/discharge dates still count as hospitalized when admission+reason are present.
	 */
	private static boolean hospitalizedFor(YesNoUnknown admitted, HospitalizationReasonType reason, Date admission, Date discharge) {

		if (admitted != YesNoUnknown.YES || reason != HospitalizationReasonType.REPORTED_DISEASE) {
			return false;
		}

		if (admission == null || discharge == null) {
			return true;
		}

		return DateHelper.getDaysBetween(admission, discharge) > 1;
	}

	/** Maps dose count to EpiPulse vaccination-status code (input already capped by caller). */
	private static String doseCountCode(int doses) {

		switch (doses) {
		case 0:
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		case 1:
			return EpipulseVaccinationStatusRef.ONE_DOSE.getCode();
		case 2:
			return EpipulseVaccinationStatusRef.TWO_DOSE.getCode();
		case 3:
			return EpipulseVaccinationStatusRef.THREE_DOSE.getCode();
		case 4:
			return EpipulseVaccinationStatusRef.FOUR_DOSE.getCode();
		case 5:
			return EpipulseVaccinationStatusRef.FIVE_DOSE.getCode();
		case 6:
			return EpipulseVaccinationStatusRef.SIX_DOSE.getCode();
		case 7:
			return EpipulseVaccinationStatusRef.SEVEN_DOSE.getCode();
		case 8:
			return EpipulseVaccinationStatusRef.EIGHT_DOSE.getCode();
		case 9:
			return EpipulseVaccinationStatusRef.NINE_DOSE.getCode();
		default:
			return EpipulseVaccinationStatusRef.TEN_DOSE.getCode();
		}
	}

	/** Formats date as {@code yyyy-MM-dd}; null becomes blank. */
	public static String formatDate(Date date) {
		return date == null ? "" : DateHelper.convertDateToDbFormat(date);
	}

	/**
	 * Formats a {@code MICValueAST_*} as a plain decimal: {@code 32} rather than {@code 32.0}, and
	 * {@code 0.0005} rather than {@code 5.0E-4}. Null becomes blank.
	 */
	private static String micValue(Double value) {
		return value == null ? "" : BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
	}

	/** Converts nullable value to export text. */
	private static String text(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	/** Returns first non-null, non-empty candidate. */
	private static String firstPopulated(String... candidates) {

		for (String candidate : candidates) {
			if (candidate != null && !candidate.isEmpty()) {
				return candidate;
			}
		}

		return null;
	}

}
