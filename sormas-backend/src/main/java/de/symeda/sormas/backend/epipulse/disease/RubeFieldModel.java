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

package de.symeda.sormas.backend.epipulse.disease;

import static de.symeda.sormas.backend.epipulse.disease.CommonFields.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.backend.epipulse.disease.CommonFields.DATE_OF_ONSET;
import static de.symeda.sormas.backend.epipulse.disease.CommonFields.HOSPITALISATION;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseRubellaGenotypeRef;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;
import de.symeda.sormas.backend.epipulse.sql.RubeSql;

/**
 * Field model for RUBE.
 *
 * <p>
 * Reuses most measles-like blocks and adds rubella-specific pregnancy and serology logic.
 */
final class RubeFieldModel {

	private RubeFieldModel() {
	}

	/**
	 * Builds RUBE fields with hospitalization/vaccination/place-of-infection SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			RubeSql.spec()
				.plus(EpipulseHospitalisationSql.spec())
				.plus(EpipulseVaccinationSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.IMPORTED_COUNTRY)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = CommonFields.defs();
		HospitalisationFields.addHospitalisationFields(fields);
		VaccinationFields.addVaccinationFields(fields);

		fields.add(DATE_OF_ONSET);

		fields.add(
			ValueDef.direct(
				"investigated_date",
				"cast(c.investigateddate as date)",
				(dto, row) -> dto.setDateOfInvestigation((Date) row.get("investigated_date")),
				EpipulseVariable.DATE_OF_INVESTIGATION,
				EpipulseMapping::dateOfInvestigation));

		fields.add(HOSPITALISATION);

		addPregnancy(fields);

		fields.add(
			ValueDef.direct(
				"clinical_confirmation",
				"c.clinicalconfirmation",
				FieldReaders.clinicalCriteriaStatus("clinical_confirmation"),
				EpipulseVariable.CLINICAL_CRITERIA_STATUS,
				EpipulseMapping::clinicalCriteriaStatus));

		addComplications(fields);
		addCluster(fields);

		fields.add(DATE_OF_LAST_VACCINATION);
		fields.add(CommonFields.vaccinationStatus(CommonFields.STANDARD_MAX_DOSES));

		fields.add(
			ValueDef.direct(
				"caseimportedstatus",
				"rube_epidata.caseimportedstatus",
				FieldReaders.importedStatus("caseimportedstatus"),
				EpipulseVariable.IMPORTED_STATUS,
				EpipulseMapping::importedStatus));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));

		addLaboratory(fields);

		return fields;
	}

	/**
	 * Adds pregnancy-related columns.
	 */
	private static void addPregnancy(List<ValueDef> fields) {

		fields.add(ValueDef.direct("pregnant", "c.pregnant", RubeFieldModel::readPregnancy, EpipulseVariable.PREGNANCY, EpipulseMapping::pregnancy));

		addWeekOfGestation(fields);
	}

	/**
	 * WeekOfGestation: gestational age at infection (declared, left blank). SORMAS has no matching field
	 * (PersonDto.gestationAgeAtBirth is congenital-only, different measurement). Blank not absent: EpiPulse
	 * validates exact column set. TODO: SORMAS field needed (bands W1-12, W13-20, W20+).
	 */
	private static void addWeekOfGestation(List<ValueDef> fields) {
		fields.add(ValueDef.blank(EpipulseVariable.WEEK_OF_GESTATION));
	}

	/**
	 * Adds complication inputs and the repeatable complication group.
	 */
	private static void addComplications(List<ValueDef> fields) {

		fields.add(FieldReaders.addressable("arthritis", "symptom.arthritis"));
		fields.add(FieldReaders.addressable("encephalitis", "symptom.encephalitis"));
		fields.add(ValueDef.sqlOnly("other_neurological", "symptom.otherneurologicalsymptoms", RubeFieldModel::readComplications));

		// Not floored, and does not need to be: EpipulseMapping.complicationDiagnosis substitutes a
		// reported NONE for a case with no complications, so the group is never empty to begin
		// with. Wrapping it in atLeastOneColumn would be dead code suggesting otherwise.
		fields.add(ValueDef.repeated(EpipulseVariable.COMPLICATION_DIAGNOSIS, EpipulseMapping::complicationDiagnosis));
	}

	/**
	 * Adds cluster-related columns.
	 */
	private static void addCluster(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"clusterrelated",
				"rube_epidata.clusterrelated",
				(dto, row) -> dto.setClusterRelated((Boolean) row.get("clusterrelated")),
				EpipulseVariable.CLUSTER_RELATED,
				EpipulseMapping::clusterRelated));

		fields.add(
			ValueDef.direct(
				"clusteridentifier",
				"rube_epidata.clusteridentifier",
				(dto, row) -> dto.setClusterIdentification((String) row.get("clusteridentifier")),
				EpipulseVariable.CLUSTER_ID,
				EpipulseMapping::clusterIdentification));

		fields.add(ValueDef.sqlOnly("clustertype", "rube_epidata.clustertype", FieldReaders.clusterSetting("clustertype")));
		fields.add(ValueDef.repeated(EpipulseVariable.CLUSTER_SETTING, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.clusterSetting(dto))));
	}

	/**
	 * Adds rubella laboratory columns.
	 */
	private static void addLaboratory(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"first_specimen_date",
				"rube_specimen.first_specimen_date",
				(dto, row) -> dto.setDateOfSpecimen((Date) row.get("first_specimen_date")),
				EpipulseVariable.DATE_OF_SPECIMEN,
				EpipulseMapping::dateOfSpecimen));

		fields.add(
			ValueDef.direct(
				"lab_result_date",
				"rube_lab_result.lab_result_date",
				(dto, row) -> dto.setDateOfLaboratoryResult((Date) row.get("lab_result_date")),
				EpipulseVariable.DATE_OF_LAB_RESULT,
				EpipulseMapping::dateOfLaboratoryResult));

		fields.add(
			ValueDef.sqlOnly("virus_detection_specimen", "rube_virus_detection.virus_detection_specimen", RubeFieldModel::readSpecimenVirDetect));
		fields.add(ValueDef.repeated(EpipulseVariable.SPECIMEN_VIR_DETECT, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.specimenVirDetect(dto))));

		fields.add(
			ValueDef.direct(
				"virus_detection_result",
				"rube_virus_detection.virus_detection_result",
				RubeFieldModel::readVirusDetectionResult,
				EpipulseVariable.RESULT_VIR_DETECT,
				EpipulseMapping::resultOfVirusDetection));

		fields.add(
			ValueDef.direct(
				"genotype_raw",
				"rube_genotype.genotype_raw",
				RubeFieldModel::readGenotype,
				EpipulseVariable.GENOTYPE,
				EpipulseMapping::genotype));

		fields.add(ValueDef.sqlOnly("specimen_types_serology", "rube_specimen.specimen_types_serology", RubeFieldModel::readSpecimenTypesSerology));
		fields.add(ValueDef.repeated(EpipulseVariable.SPECIMEN_SERO, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.specimenSero(dto))));

		// IgGAvidityTest: declared by RUBE and by no other subject code, and SORMAS records no
		// avidity test to answer it with - PathogenTestType has no constant for one. Written blank
		// rather than omitted because EpiPulse validates against the declared columns.
		// TODO: IgGAvidityTest: "IgG avidity test method performed for confirmation of the case
		// according to EU case definition." A SORMAS field would be a new PathogenTestType constant
		// or a flag on the IgG test, and is a decision about the case form rather than about this
		// export.
		fields.add(ValueDef.blank(EpipulseVariable.IG_G_AVIDITY_TEST));

		addSerologyResults(fields);
	}

	/**
	 * Adds serology result columns using precedence-based mapping.
	 */
	private static void addSerologyResults(List<ValueDef> fields) {

		fields.add(FieldReaders.addressable("igg_test_count", "rube_serology.igg_test_count"));
		fields.add(FieldReaders.addressable("igg_paired_count", "rube_serology.igg_paired_count"));
		fields.add(FieldReaders.addressable("igg_paired_positive", "rube_serology.igg_paired_positive"));
		fields.add(
			ValueDef.direct(
				"igg_paired_negative",
				"rube_serology.igg_paired_negative",
				RubeFieldModel::readResultIgG,
				EpipulseVariable.RESULT_IG_G,
				EpipulseMapping::resultIgG));

		fields.add(FieldReaders.addressable("igm_test_count", "rube_serology.igm_test_count"));
		fields.add(FieldReaders.addressable("igm_positive", "rube_serology.igm_positive"));
		fields.add(
			ValueDef.direct(
				"igm_negative",
				"rube_serology.igm_negative",
				RubeFieldModel::readResultIgM,
				EpipulseVariable.RESULT_IG_M,
				EpipulseMapping::resultIgM));
	}

	/**
	 * Pregnancy: SORMAS's pregnant field. UNKNOWN reports nothing (not false). EpiPulse BOOL has no
	 * third state; "could not establish" not the same as "not pregnant". Form asks every case (no disease dependency).
	 */
	private static void readPregnancy(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		YesNoUnknown pregnant = FieldReaders.parseEnum(YesNoUnknown.class, (String) row.get("pregnant"));

		if (pregnant != null && pregnant != YesNoUnknown.UNKNOWN) {
			dto.setPregnancy(pregnant == YesNoUnknown.YES);
		}
	}

	/**
	 * ComplicationDiagnosis: ARTH=arthritis, NEURO=encephalitis or otherneurologicalsymptoms (deduped),
	 * NONE substituted by EpipulseMapping (not derived here). OTH not mapped (would inspect all symptoms).
	 * Only SymptomState.YES counts present. Case with only-other complication reports NONE not OTH.
	 */
	private static void readComplications(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		List<String> complications = new ArrayList<>();

		if (recorded(row, "arthritis")) {
			complications.add("ARTH");
		}
		if (recorded(row, "encephalitis") || recorded(row, "other_neurological")) {
			complications.add("NEURO");
		}

		dto.setComplicationDiagnosis(complications);
	}

	/** Whether the column is recorded as {@code YES}. Nothing else counts. */
	private static boolean recorded(EpipulseRow row, String alias) {
		return FieldReaders.parseSymptomState((String) row.get(alias)) == SymptomState.YES;
	}

	/**
	 * SpecimenVirDetect: single-valued here (repeatable in metadata). Unmapped material (not in EpiPulse)
	 * reports nothing not OTH (EpipulseLaboratoryMapper already places unmapped under OTH).
	 */
	private static void readSpecimenVirDetect(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		SampleMaterial material = FieldReaders.parseEnum(SampleMaterial.class, (String) row.get("virus_detection_specimen"));
		String mapped = EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(material);

		if (mapped != null) {
			List<String> materials = new ArrayList<>();
			materials.add(mapped);
			dto.setTypeOfSpecimenCollected(materials);
		}
	}

	private static void readSpecimenTypesSerology(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {
		dto.setTypeOfSpecimenSerology(FieldReaders.specimenCodes((String) row.get("specimen_types_serology")));
	}

	private static void readVirusDetectionResult(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		PathogenTestResultType result = FieldReaders.parseEnum(PathogenTestResultType.class, (String) row.get("virus_detection_result"));
		if (result != null) {
			dto.setResultOfVirusDetection(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(result));
		}
	}

	/**
	 * ResultIgG: precedence (POS > NEG > EQUI-never > NOTEST). "Qualifying" = paired serum with
	 * fourfold-rise-or-seroconversion flags only. IgG tests without flags fall through: tested (not NOTEST)
	 * but no answer to variable. Precedence steps listed in readResultIgG javadoc.
	 */
	private static void readResultIgG(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {
		dto.setResultIgG(
			serologyResult(
				count(row, "igg_test_count"),
				count(row, "igg_paired_count"),
				count(row, "igg_paired_positive"),
				count(row, "igg_paired_negative")));
	}

	/**
	 * ResultIgM: same precedence as ResultIgG over all IgM tests (no flags narrow it).
	 * Set counted towards NOTEST = set counted towards result (unlike IgG's paired-with-flags). Case
	 * falling through all branches cannot arise here.
	 */
	private static void readResultIgM(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {
		long tested = count(row, "igm_test_count");
		dto.setResultIgM(serologyResult(tested, tested, count(row, "igm_positive"), count(row, "igm_negative")));
	}

	/**
	 * Serology precedence: tested=0 => NOTEST; positive>0 => POS; qualifying>0 ^ negative=qualifying => NEG; else => null.
	 * See readResultIgG for step rationale.
	 */
	private static String serologyResult(long tested, long qualifying, long positive, long negative) {

		if (tested == 0) {
			return "NOTEST";
		}
		if (positive > 0) {
			return "POS";
		}
		if (qualifying > 0 && negative == qualifying) {
			return "NEG";
		}

		return null;
	}

	/**
	 * A count from the serology CTE. {@code COUNT} is never null in PostgreSQL, but the column
	 * reaches here as an {@code Object} from a left join that may have produced no row at all -- a
	 * case outside {@code rube_serology} would otherwise be a null unboxing rather than a case with
	 * no tests.
	 */
	private static long count(EpipulseRow row, String alias) {

		Number count = (Number) row.get(alias);
		return count == null ? 0 : count.longValue();
	}

	/**
	 * Genotype: EpiPulse code matching GenoType (only rubella families + NA/UNK). No value if unrecorded
	 * or unmapped. valueOf guarded (dropped GenoType constants won't fail export). Independent of
	 * ResultVirDetect: potentially different tests, case can carry genotype with no verified detection.
	 */
	private static void readGenotype(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		String raw = (String) row.get("genotype_raw");
		if (StringUtils.isBlank(raw)) {
			return;
		}

		GenoType genoType;
		try {
			genoType = GenoType.valueOf(raw.trim());
		} catch (IllegalArgumentException e) {
			return;
		}

		dto.setGenotype(EpipulseRubellaGenotypeRef.codeFor(genoType));
	}
}
