/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.epipulse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseOutcomeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueClinicalCriteriaRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDiseaseRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseGenderRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePneumococcalSerotypingMethodRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseVaccinationStatusRef;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.UtilDate;
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
			entry.getAddressCountryLocationCode(),
			entry.getServerCountryLocationCode());
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
			entry.getServerCountryLocationCode());
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

	/**
	 * Resolves {@code ClinicalCriteria} for DENGUE to one of {@code ASY}, {@code NSEV} or {@code SEV}.
	 * Evaluate in this order:
	 *
	 * <ol>
	 * <li>If {@code symptoms.asymptomatic} is {@code YES}, return {@code ASY}. This ASY is ruled out
	 * first because setting it also blanks {@code DateOfOnset}.
	 * <li>Otherwise map {@code symptoms.clinicalManifestation} through
	 * {@link EpipulseDengueClinicalCriteriaRef}. {@code UNKNOWN}, {@code OTHER}, or no answer
	 * resolves to blank.</li>
	 * </ol>
	 *
	 */
	public static String dengueClinicalCriteria(EpipulseDiseaseExportEntryDto entry) {

		if (entry.getAsymptomatic() == SymptomState.YES) {
			return EpipulseDengueClinicalCriteriaRef.ASY.name();
		}

		return EpipulseDengueClinicalCriteriaRef.codeFor(entry.getClinicalManifestation());
	}

	// ==================== Syphilis ====================

	/** {@code CountryOfBirth} as EpiPulse location code of the country. */
	public static String countryOfBirth(EpipulseDiseaseExportEntryDto entry) {
		return entry.getBirthCountryLocationCode();
	}

	/** {@code CountryOfBirthOfMother} as EpiPulse location code of the country. */
	public static String countryOfBirthOfMother(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMotherBirthCountryLocationCode();
	}

	/** {@code CountryOfNationality} as EpiPulse location code of the country. */
	public static String countryOfNationality(EpipulseDiseaseExportEntryDto entry) {
		return entry.getCitizenshipCountryLocationCode();
	}

	/**
	 * {@code CountryOfNationalityOfMother} from epidemiological mother-citizenship data (CONSYPH).
	 */
	public static String countryOfNationalityOfMother(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMotherCitizenshipCountryLocationCode();
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
	 * The set of {@link MeansOfImmunization} values whose doses count toward the patient's own
	 * dose total.
	 *
	 * <p>
	 * This is deliberately not {@link MeansOfImmunization#isVaccination}, which would also admit:
	 * <ul>
	 * <li>{@code MATERNAL_VACCINATION} - these doses were given to the mother and are described by
	 * {@code VaccinationStatusMaternal}; they never count toward the patient's own total</li>
	 * <li>{@code MONOCLONAL_ANTIBODY} - not a vaccine</li>
	 * </ul>
	 * 
	 */
	public static final Set<MeansOfImmunization> VACCINATION_DOSE_MEANS =
		Collections.unmodifiableSet(EnumSet.of(MeansOfImmunization.VACCINATION, MeansOfImmunization.VACCINATION_RECOVERY));

	/**
	 * {@code DateOfLastVaccination}: the latest of the doses {@link #vaccinationStatus} counts.
	 *
	 * <p>
	 * Read from exactly the same doses, so the two columns can never describe different
	 * vaccinations: blank whenever the status is not a dose count. A dose on the onset day counts,
	 * as it does for the status, although EpiPulse words this column "before disease onset".
	 */
	public static String dateOfLastVaccination(EpipulseDiseaseExportEntryDto entry) {

		Date onset = EpipulseCaseDates.dateOfOnset(entry);
		if (onset == null) {
			return "";
		}

		SortedSet<Date> doseDays = doseDaysUpTo(entry, DateHelper.getStartOfDay(onset));

		return doseDays.isEmpty() ? "" : formatDate(doseDays.last());
	}

	/**
	 * {@code VaccinationStatus}: the number of vaccine doses the patient received up to and
	 * including the onset day.
	 *
	 * <p>
	 * Counts doses, not courses: every dose recorded under a same-disease immunization whose means
	 * is in {@link #VACCINATION_DOSE_MEANS}, whatever the immunization's status - a pending, failed
	 * or expired course still delivered its doses. Doses on the same day count once, so a dose
	 * recorded under two immunizations is not counted twice. The first rule that applies wins:
	 *
	 * <ol>
	 * <li>no onset date - blank: there is no date to have been vaccinated by</li>
	 * <li>at least one dose up to the onset day - that count, capped at {@code maxDoses}</li>
	 * <li>doses on record, all after onset - {@code NOTVACC}: the dates show none had been given</li>
	 * <li>no doses on record at all, but a vaccination immunization recorded up to the onset day
	 * ({@link EpipulseCaseDates#immunizationDate}) - {@code UNKDOSE}: vaccinated, the doses not
	 * entered. Its planned number of doses is not a count of doses given, so it is not used</li>
	 * <li>an explicit {@code NOT_IMMUNIZED} immunization - {@code NOTVACC}</li>
	 * <li>nothing recorded - blank: the absence of a record is not evidence of non-vaccination</li>
	 * </ol>
	 */
	public static String vaccinationStatus(EpipulseDiseaseExportEntryDto entry, int maxDoses) {

		Date onset = EpipulseCaseDates.dateOfOnset(entry);
		if (onset == null) {
			return "";
		}

		Date onsetDay = DateHelper.getStartOfDay(onset);

		SortedSet<Date> doseDays = doseDaysUpTo(entry, onsetDay);
		if (!doseDays.isEmpty()) {
			return doseCountCode(Math.min(doseDays.size(), maxDoses));
		}

		if (hasDoses(entry)) {
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		if (hasVaccinationImmunizationUpTo(entry, onsetDay)) {
			return EpipulseVaccinationStatusRef.UNKDOSE.getCode();
		}

		if (hasNotImmunizedRecord(entry)) {
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		return "";
	}

	/**
	 * The distinct days a dose was given on, up to and including the onset day. A dose with no date
	 * at all cannot be placed before or after onset and is left out.
	 */
	private static SortedSet<Date> doseDaysUpTo(EpipulseDiseaseExportEntryDto entry, Date onsetDay) {

		SortedSet<Date> doseDays = new TreeSet<>();
		if (entry.getVaccinations() == null) {
			return doseDays;
		}

		for (EpipulseVaccinationCheckDto vaccination : entry.getVaccinations()) {

			Date administered = EpipulseCaseDates.vaccinationDate(vaccination);
			if (administered == null) {
				continue;
			}

			Date day = DateHelper.getStartOfDay(administered);
			if (!day.after(onsetDay)) {
				doseDays.add(day);
			}
		}

		return doseDays;
	}

	private static boolean hasDoses(EpipulseDiseaseExportEntryDto entry) {
		return entry.getVaccinations() != null && !entry.getVaccinations().isEmpty();
	}

	private static boolean hasVaccinationImmunizationUpTo(EpipulseDiseaseExportEntryDto entry, Date onsetDay) {

		if (entry.getImmunizations() == null) {
			return false;
		}

		for (EpipulseImmunizationCheckDto immunization : entry.getImmunizations()) {

			if (!VACCINATION_DOSE_MEANS.contains(immunization.getMeansOfImmunization())) {
				continue;
			}

			Date recorded = EpipulseCaseDates.immunizationDate(immunization);
			if (recorded != null && !DateHelper.getStartOfDay(recorded).after(onsetDay)) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasNotImmunizedRecord(EpipulseDiseaseExportEntryDto entry) {

		return entry.getImmunizations() != null
			&& entry.getImmunizations().stream().anyMatch(immunization -> immunization.getMeansOfImmunization() == MeansOfImmunization.NOT_IMMUNIZED);
	}

	/**
	 * {@code VaccinationStatusMaternal} BOOL: whether the mother was vaccinated during pregnancy,
	 * for patients under 2 at onset.
	 *
	 * <p>
	 * SORMAS records this on the patient, not on the mother: a same-disease immunization whose means
	 * is {@code MATERNAL_VACCINATION} - the protection the child received through the mother. Such a
	 * record makes the value {@code true}, whatever its status or dates: a maternal vaccination
	 * precedes birth, so it always precedes onset. The value is never {@code false}: the absence of
	 * the record is not evidence that the mother was not vaccinated, and {@code NOT_IMMUNIZED}
	 * describes the patient, not the mother.
	 *
	 * <p>
	 * "Under 2" is the {@code Age} column's own reading - completed years at
	 * {@link EpipulseCaseDates#ageReferenceDate} - so the two columns cannot disagree. Blank when the
	 * age is unknown or 2 or more.
	 */
	public static String vaccinationStatusMaternal(EpipulseDiseaseExportEntryDto entry) {

		Integer years = EpipulseCaseDates.ageYears(entry);
		if (years == null || years >= 2) {
			return "";
		}

		boolean maternalVaccination = entry.getImmunizations() != null
			&& entry.getImmunizations()
				.stream()
				.anyMatch(immunization -> immunization.getMeansOfImmunization() == MeansOfImmunization.MATERNAL_VACCINATION);

		return maternalVaccination ? String.valueOf(true) : "";
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

	// --- TUBE (Tuberculosis) ---

	/**
	 * {@code BornReportingCountry} BOOL: whether the patient originates from the reporting country.
	 *
	 * <p>
	 * Place of birth decides; citizenship is fallback when birth country is missing. Blank when
	 * neither is known.
	 */
	public static String bornReportingCountry(EpipulseDiseaseExportEntryDto entry) {
		return isReportingCountry(entry, firstPopulated(entry.getBirthCountryLocationCode(), entry.getCitizenshipCountryLocationCode()));
	}

	/** {@code NationalityReportingCountry} BOOL, from citizenship alone. Unknown stays blank. */
	public static String nationalityReportingCountry(EpipulseDiseaseExportEntryDto entry) {
		return isReportingCountry(entry, entry.getCitizenshipCountryLocationCode());
	}

	/**
	 * Compares a country EpiPulse location code against the exporting server's own, as the two
	 * {@code *ReportingCountry} booleans do.
	 */
	private static String isReportingCountry(EpipulseDiseaseExportEntryDto entry, String countryLocationCode) {

		String reportingCountryLocationCode = entry.getServerCountryLocationCode();

		if (countryLocationCode == null
			|| countryLocationCode.isEmpty()
			|| reportingCountryLocationCode == null
			|| reportingCountryLocationCode.isEmpty()) {
			return "";
		}

		return String.valueOf(countryLocationCode.equals(reportingCountryLocationCode));
	}

	/** {@code DateOfEntry}: when a patient of foreign origin entered the reporting country. */
	public static String dateOfEntry(EpipulseDiseaseExportEntryDto entry) {
		return formatDate(entry.getDateOfEntry());
	}

	/** {@code MajorSiteOfTB} code; see {@code EpipulseTubeSiteOfTbRef}. */
	public static String majorSiteOfTB(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMajorSiteOfTB();
	}

	/** {@code MinorSiteOfTB} code; unlike major site, never {@code PULMONARY}. */
	public static String minorSiteOfTB(EpipulseDiseaseExportEntryDto entry) {
		return entry.getMinorSiteOfTB();
	}

	/**
	 * {@code DiagnosedAnteMortem} BOOL, the negation of {@code CaseDataDto.postMortem}.
	 *
	 * <p>
	 * Source is primitive {@code boolean}, so the derived value is never null.
	 */
	public static String diagnosedAnteMortem(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getDiagnosedAnteMortem());
	}

	/**
	 * {@code PrevDiagnosis} BOOL: tuberculosis diagnosed in an earlier calendar year.
	 *
	 * <p>
	 * Derived from infection year only; value is {@code true} or blank (never {@code false}).
	 */
	public static String prevDiagnosis(EpipulseDiseaseExportEntryDto entry) {

		Integer year = previousDiagnosisYear(entry);

		return year == null ? "" : String.valueOf(true);
	}

	/** {@code PrevDiagnosisYear}, blank unless it is earlier than {@code DateUsedForStatistics}. */
	public static String prevDiagnosisYear(EpipulseDiseaseExportEntryDto entry) {
		return text(previousDiagnosisYear(entry));
	}

	/**
	 * The recorded year of an earlier episode, or {@code null} where there is none EpiPulse would
	 * accept.
	 *
	 * <p>
	 * EpiPulse requires the year to precede {@code DateUsedForStatistics}; a year equal to or later
	 * than the reporting year describes this episode, not a previous one.
	 *
	 * <p>
	 * Non-positive year means unanswered ({@code int8 default 0} source), not a valid episode year.
	 */
	private static Integer previousDiagnosisYear(EpipulseDiseaseExportEntryDto entry) {

		Integer year = entry.getTuberculosisInfectionYear();
		Date dateUsedForStatistics = EpipulseCaseDates.dateUsedForStatistics(entry);

		if (year == null || year <= 0 || dateUsedForStatistics == null) {
			return null;
		}

		return year < UtilDate.toLocalDate(dateUsedForStatistics).getYear() ? year : null;
	}

	/** {@code PrevTreatment} BOOL. Unknown stays blank. */
	public static String prevTreatment(EpipulseDiseaseExportEntryDto entry) {
		return text(entry.getPrevTreatment());
	}

	/** {@code PrevTreatmentCompletion} code; see {@code EpipulseTubePrevTreatmentCompletionRef}. */
	public static String prevTreatmentCompletion(EpipulseDiseaseExportEntryDto entry) {
		return entry.getPrevTreatmentCompletion();
	}

	/** {@code ResultCulture}: {@code POS}, {@code NEG} or {@code NOTEST}. */
	public static String resultCulture(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultCulture();
	}

	/** {@code ResultMicroscopyAFB}: {@code POS}, {@code NEG} or {@code NOTEST}. */
	public static String resultMicroscopyAFB(EpipulseDiseaseExportEntryDto entry) {
		return entry.getResultMicroscopyAFB();
	}

	/** {@code ResultOtherTest}: nucleic acid and histopathology findings, or {@code NOTEST}. */
	public static String resultOtherTest(EpipulseDiseaseExportEntryDto entry) {
		return EpipulseLaboratoryMapper.mapOtherTestResult(entry.getHistopathologyResult(), entry.getNucleicAcidResult());
	}

	/** {@code SIR_AMK}: susceptibility to Amikacin. */
	public static String sir_AMK(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_AMK();
	}

	/** {@code SIR_BDQ}: susceptibility to Bedaquiline. */
	public static String sir_BDQ(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_BDQ();
	}

	/** {@code SIR_CLF}: susceptibility to Clofazimine. */
	public static String sir_CLF(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_CLF();
	}

	/** {@code SIR_DLM}: susceptibility to Delamanid. */
	public static String sir_DLM(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_DLM();
	}

	/** {@code SIR_ETH}: susceptibility to Ethambutol. */
	public static String sir_ETH(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_ETH();
	}

	/** {@code SIR_ETI}: susceptibility to Ethionamide. */
	public static String sir_ETI(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_ETI();
	}

	/** {@code SIR_INH}: susceptibility to Isoniazid. */
	public static String sir_INH(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_INH();
	}

	/** {@code SIR_LNZ}: susceptibility to Linezolid. */
	public static String sir_LNZ(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_LNZ();
	}

	/** {@code SIR_LVX}: susceptibility to Levofloxacin. */
	public static String sir_LVX(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_LVX();
	}

	/** {@code SIR_MFX}: susceptibility to Moxifloxacin. */
	public static String sir_MFX(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_MFX();
	}

	/** {@code SIR_PTM}: susceptibility to Pretomanid. */
	public static String sir_PTM(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_PTM();
	}

	/** {@code SIR_PZA}: susceptibility to Pyrazinamide. */
	public static String sir_PZA(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_PZA();
	}

	/** {@code SIR_STR}: susceptibility to Streptomycin. */
	public static String sir_STR(EpipulseDiseaseExportEntryDto entry) {
		return entry.getSir_STR();
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
