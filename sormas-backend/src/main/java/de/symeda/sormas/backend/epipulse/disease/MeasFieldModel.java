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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMeaslesGenotypeRef;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;
import de.symeda.sormas.backend.epipulse.sql.MeasSql;

/**
 * MEAS (Measles) - five repeatable groups interleaved, all floored except ComplicationDiagnosis.
 * 
 * Complications, ClusterSetting, PlaceOfInfection, SpecimenVirDetect, SpecimenSero each measured
 * across the export. ComplicationDiagnosis never empty (reports NONE for no complications), so not floored.
 * Parsing shared via {@link FieldReaders}.
 */
final class MeasFieldModel {

	private MeasFieldModel() {
	}

	/**
	 * The field list below is the CSV side. The SQL side - the CTEs that produce the columns it
	 * selects, and the joins and aliases they are read through - is {@link MeasSql}. A select
	 * expression here such as an alias-qualified column is declared there.
	 */
	static FieldModel create() {
		return new FieldModel(
			fields(),
			MeasSql.spec()
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

		// --- cause of death: the description, but only when the person died of Measles ---
		fields.add(FieldReaders.addressable("cause_of_death", "person.causeofdeath"));
		fields.add(FieldReaders.addressable("cause_of_death_disease", "person.causeofdeathdisease"));
		fields.add(
			ValueDef.direct(
				"cause_of_death_details",
				"person.causeofdeathdetails",
				MeasFieldModel::readCauseOfDeath,
				EpipulseVariable.CAUSE_OF_DEATH,
				EpipulseMapping::causeOfDeath));

		fields.add(
			ValueDef.direct(
				"clinical_confirmation",
				"c.clinicalconfirmation",
				FieldReaders.clinicalCriteriaStatus("clinical_confirmation"),
				EpipulseVariable.CLINICAL_CRITERIA_STATUS,
				EpipulseMapping::clinicalCriteriaStatus));

		// --- complications: five raw symptom columns collapse into one repeated CSV group ---
		fields.add(ValueDef.sqlOnly("acuteencephalitis", "comp.acuteencephalitis", FieldReaders.noop()));
		fields.add(ValueDef.sqlOnly("diarrhea", "comp.diarrhea", FieldReaders.noop()));
		fields.add(ValueDef.sqlOnly("otitismedia", "comp.otitismedia", FieldReaders.noop()));
		fields.add(ValueDef.sqlOnly("pneumoniaclinicalorradiologic", "comp.pneumoniaclinicalorradiologic", FieldReaders.noop()));
		fields.add(ValueDef.sqlOnly("othercomplications", "comp.othercomplications", MeasFieldModel::readComplications));
		// Not floored, and does not need to be: EpipulseMapping.complicationDiagnosis substitutes a
		// reported NONE for a case with no complications, so the group is never empty to begin
		// with. Wrapping it in atLeastOneColumn would be dead code suggesting otherwise.
		fields.add(ValueDef.repeated(EpipulseVariable.COMPLICATION_DIAGNOSIS, EpipulseMapping::complicationDiagnosis));

		fields.add(
			ValueDef.direct(
				"clusterrelated",
				"ec.clusterrelated",
				(dto, row) -> dto.setClusterRelated((Boolean) row.get("clusterrelated")),
				EpipulseVariable.CLUSTER_RELATED,
				EpipulseMapping::clusterRelated));

		fields.add(
			ValueDef.direct(
				"clustertypetext",
				"ec.clustertypetext",
				(dto, row) -> dto.setClusterIdentification((String) row.get("clustertypetext")),
				EpipulseVariable.CLUSTER_ID,
				EpipulseMapping::clusterIdentification));

		fields.add(ValueDef.sqlOnly("clustertype", "ec.clustertype", FieldReaders.clusterSetting("clustertype")));
		fields.add(ValueDef.repeated(EpipulseVariable.CLUSTER_SETTING, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.clusterSetting(dto))));

		fields.add(DATE_OF_LAST_VACCINATION);
		fields.add(CommonFields.vaccinationStatus(CommonFields.STANDARD_MAX_DOSES));

		fields.add(
			ValueDef.direct(
				"caseimportedstatus",
				"ec.caseimportedstatus",
				FieldReaders.importedStatus("caseimportedstatus"),
				EpipulseVariable.IMPORTED_STATUS,
				EpipulseMapping::importedStatus));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));

		fields.add(
			ValueDef.direct(
				"first_specimen_date",
				"sd.first_specimen_date",
				(dto, row) -> dto.setDateOfSpecimen((Date) row.get("first_specimen_date")),
				EpipulseVariable.DATE_OF_SPECIMEN,
				EpipulseMapping::dateOfSpecimen));

		fields.add(
			ValueDef.direct(
				"lab_result_date",
				"vd.lab_result_date",
				(dto, row) -> dto.setDateOfLaboratoryResult((Date) row.get("lab_result_date")),
				EpipulseVariable.DATE_OF_LAB_RESULT,
				EpipulseMapping::dateOfLaboratoryResult));

		fields.add(ValueDef.sqlOnly("specimen_types_virus", "sd.specimen_types_virus", MeasFieldModel::readSpecimenTypesVirus));
		fields.add(ValueDef.repeated(EpipulseVariable.SPECIMEN_VIR_DETECT, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.specimenVirDetect(dto))));

		fields.add(
			ValueDef.direct(
				"virus_detection_result",
				"vd.virus_detection_result",
				MeasFieldModel::readVirusDetectionResult,
				EpipulseVariable.RESULT_VIR_DETECT,
				EpipulseMapping::resultOfVirusDetection));

		fields.add(
			ValueDef.direct("genotype_raw", "vd.genotype_raw", MeasFieldModel::readGenotype, EpipulseVariable.GENOTYPE, EpipulseMapping::genotype));

		fields.add(ValueDef.sqlOnly("specimen_types_serology", "sd.specimen_types_serology", MeasFieldModel::readSpecimenTypesSerology));
		fields.add(ValueDef.repeated(EpipulseVariable.SPECIMEN_SERO, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.specimenSero(dto))));

		fields.add(
			ValueDef.direct("igg_result", "igg.igg_result", MeasFieldModel::readIggResult, EpipulseVariable.RESULT_IG_G, EpipulseMapping::resultIgG));

		fields.add(
			ValueDef.direct("igm_result", "igm.igm_result", MeasFieldModel::readIgmResult, EpipulseVariable.RESULT_IG_M, EpipulseMapping::resultIgM));

		return fields;
	}

	/**
	 * CauseOfDeath text only when cause=EPIDEMIC_DISEASE AND disease=MEASLES.
	 * 
	 * Text can linger from prior cause; checking both fields prevents stale data being read
	 * as measles death (e.g., "road accident" from EPIDEMIC_DISEASE changed to OTHER_CAUSE).
	 */
	private static void readCauseOfDeath(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		CauseOfDeath causeOfDeath = FieldReaders.parseEnum(CauseOfDeath.class, (String) row.get("cause_of_death"));
		Disease causeOfDeathDisease = FieldReaders.parseEnum(Disease.class, (String) row.get("cause_of_death_disease"));

		if (causeOfDeath == CauseOfDeath.EPIDEMIC_DISEASE && causeOfDeathDisease == Disease.MEASLES) {
			dto.setCauseOfDeath(StringUtils.trimToNull((String) row.get("cause_of_death_details")));
		}
	}

	/**
	 * ComplicationDiagnosis: one code per symptom column=YES. Measles-specific (ACENCE, DIARR, OME, PNEU, OTH).
	 * Repeatable (codes accumulate), not floored: {@link EpipulseMapping#complicationDiagnosis} substitutes NONE
	 * for empty list (reported value meaning "no complications").
	 */
	private static void readComplications(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		List<String> complications = new ArrayList<>();

		addComplication(complications, row, "acuteencephalitis", "ACENCE");
		addComplication(complications, row, "diarrhea", "DIARR");
		addComplication(complications, row, "otitismedia", "OME");
		addComplication(complications, row, "pneumoniaclinicalorradiologic", "PNEU");
		addComplication(complications, row, "othercomplications", "OTH");

		dto.setComplicationDiagnosis(complications);
	}

	/** Adds {@code code} when the column is recorded as {@code YES}. Nothing else counts. */
	private static void addComplication(List<String> complications, EpipulseRow row, String alias, String code) {

		if (FieldReaders.parseSymptomState((String) row.get(alias)) == SymptomState.YES) {
			complications.add(code);
		}
	}

	private static void readSpecimenTypesVirus(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {
		dto.setTypeOfSpecimenCollected(FieldReaders.specimenCodes((String) row.get("specimen_types_virus")));
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

	private static void readIggResult(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		PathogenTestResultType result = FieldReaders.parseEnum(PathogenTestResultType.class, (String) row.get("igg_result"));
		if (result != null) {
			dto.setResultIgG(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(result));
		}
	}

	private static void readIgmResult(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		PathogenTestResultType result = FieldReaders.parseEnum(PathogenTestResultType.class, (String) row.get("igm_result"));
		if (result != null) {
			dto.setResultIgM(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(result));
		}
	}

	/**
	 * Genotype: EpiPulse code matching {@link GenoType}. No value if unrecorded or no equivalent code.
	 * {@code valueOf} guarded: dropped GenoType constants would fail whole export, not just one case.
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

		EpipulseMeaslesGenotypeRef ref = EpipulseMeaslesGenotypeRef.getByGenoType(genoType);
		if (ref != null) {
			dto.setGenotype(ref.name());
		}
	}
}
