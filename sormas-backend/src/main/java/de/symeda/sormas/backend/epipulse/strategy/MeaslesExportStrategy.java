/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.epipulse.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Export strategy for Measles (MEAS) disease exports.
 * Measles extends the common fields with laboratory data and clinical/epidemiology data.
 * This includes 7 additional CTEs, 20 additional DTO fields, and 5 additional max count trackers.
 */
@Stateless
@LocalBean
public class MeaslesExportStrategy extends AbstractEpipulseDiseaseExportStrategy {

	@Override
	protected String buildDiseaseExportQuery() {
		StringBuilder query = new StringBuilder();

		// Common CTEs
		query.append(sqlCteBuilder.buildVariablesCte());
		query.append(sqlCteBuilder.buildConfigDataCte());
		query.append(sqlCteBuilder.buildFilteredCasesCte(true)); // Include Measles-specific fields (epidata_id, investigateddate, clinicalconfirmation)
		query.append(sqlCteBuilder.buildPreviousHospitalizationsCte());
		query.append(sqlCteBuilder.buildSamplesCte());
		query.append(sqlCteBuilder.buildPathogenTestsCte());
		query.append(sqlCteBuilder.buildImmunizationsCte());
		query.append(sqlCteBuilder.buildVaccinationsCte());

		// Measles-specific CTEs
		query.append(buildSampleDataCte());
		query.append(buildVirusDetectionDataCte());
		query.append(buildIggSerologyDataCte());
		query.append(buildIgmSerologyDataCte());
		query.append(buildEpidataClusterCte());
		query.append(buildExposureLocationsCte());
		query.append(buildComplicationsDataCte());

		// Main SELECT clause with Measles-specific fields
		query.append(buildMeaslesSelectClause());

		return query.toString();
	}

	@Override
	protected void mapDiseaseSpecificFields(EpipulseDiseaseExportEntryDto dto, Object[] row, int startIndex) {
		int index = startIndex;

		// Laboratory data (indices 28-35)
		dto.setDateOfSpecimen((Date) row[++index]);
		dto.setDateOfLaboratoryResult((Date) row[++index]);

		String specimenTypesVirusRaw = (String) row[++index];
		dto.setTypeOfSpecimenCollected(parseSpecimenTypes(specimenTypesVirusRaw));

		String virusDetectionResultRaw = (String) row[++index];
		if (!StringUtils.isBlank(virusDetectionResultRaw)) {
			PathogenTestResultType virusDetectionResult = parsePathogenTestResultType(virusDetectionResultRaw);
			if (virusDetectionResult != null) {
				dto.setResultOfVirusDetection(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(virusDetectionResult));
			}
		}

		String genotypeRaw = (String) row[++index];
		if (!StringUtils.isBlank(genotypeRaw)) {
			dto.setGenotype(EpipulseLaboratoryMapper.normalizeGenotypeForEpipulse(genotypeRaw));
		}

		String specimenTypesSerologyRaw = (String) row[++index];
		dto.setTypeOfSpecimenSerology(parseSpecimenTypes(specimenTypesSerologyRaw));

		String iggResultRaw = (String) row[++index];
		if (!StringUtils.isBlank(iggResultRaw)) {
			PathogenTestResultType iggResult = parsePathogenTestResultType(iggResultRaw);
			if (iggResult != null) {
				dto.setResultIgG(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(iggResult));
			}
		}

		String igmResultRaw = (String) row[++index];
		if (!StringUtils.isBlank(igmResultRaw)) {
			PathogenTestResultType igmResult = parsePathogenTestResultType(igmResultRaw);
			if (igmResult != null) {
				dto.setResultIgM(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(igmResult));
			}
		}

		// Clinical and epidemiology data (indices 36-47)
		dto.setDateOfInvestigation((Date) row[++index]);

		Boolean clusterRelated = (Boolean) row[++index];
		dto.setClusterRelated(clusterRelated);

		dto.setClusterIdentification((String) row[++index]);

		String clusterTypeRaw = (String) row[++index];
		if (!StringUtils.isBlank(clusterTypeRaw)) {
			ClusterType clusterType = parseClusterType(clusterTypeRaw);
			if (clusterType != null) {
				List<String> clusterSettings = new ArrayList<>();
				clusterSettings.add(EpipulseLaboratoryMapper.mapClusterTypeToEpipulseCode(clusterType));
				dto.setClusterSetting(clusterSettings);
			}
		}

		String caseImportedStatusRaw = (String) row[++index];
		if (!StringUtils.isBlank(caseImportedStatusRaw)) {
			CaseImportedStatus caseImportedStatus = parseCaseImportedStatus(caseImportedStatusRaw);
			if (caseImportedStatus != null) {
				dto.setImportedStatus(EpipulseLaboratoryMapper.mapCaseImportedStatusToEpipulseCode(caseImportedStatus));
			}
		}

		// Complications mapping (4 fields)
		String acuteEncephalitisRaw = (String) row[++index];
		String diarrheaRaw = (String) row[++index];
		String otitisMediaRaw = (String) row[++index];
		String otherComplicationsRaw = (String) row[++index];

		SymptomState acuteEncephalitis = parseSymptomState(acuteEncephalitisRaw);
		SymptomState diarrhea = parseSymptomState(diarrheaRaw);
		SymptomState otitisMedia = parseSymptomState(otitisMediaRaw);
		SymptomState otherComplications = parseSymptomState(otherComplicationsRaw);

		dto.setComplicationDiagnosis(
			EpipulseLaboratoryMapper.mapSymptomsToComplicationCodes(acuteEncephalitis, diarrhea, otitisMedia, otherComplications));

		// Clinical criteria status
		String clinicalConfirmationRaw = (String) row[++index];
		if (!StringUtils.isBlank(clinicalConfirmationRaw)) {
			YesNoUnknown clinicalConfirmation = parseYesNoUnknown(clinicalConfirmationRaw);
			if (clinicalConfirmation != null) {
				dto.setClinicalCriteriaStatus(EpipulseLaboratoryMapper.deriveClinicalCriteriaStatus(clinicalConfirmation));
			}
		}

		// Place of infection (exposure locations)
		String placeOfInfectionRaw = (String) row[++index];
		if (!StringUtils.isBlank(placeOfInfectionRaw)) {
			List<String> placesOfInfection = new ArrayList<>();
			for (String place : placeOfInfectionRaw.split(";")) {
				if (!place.trim().isEmpty()) {
					placesOfInfection.add(place.trim());
				}
			}
			dto.setPlaceOfInfection(placesOfInfection);
		}

		// Cause of death
		dto.setCauseOfDeath((String) row[++index]);
	}

	@Override
	protected void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {
		int maxComplicationDiagnosis = 0;
		int maxClusterSettings = 0;
		int maxPlaceOfInfection = 0;
		int maxSpecimenVirDetect = 0;
		int maxSpecimenSero = 0;

		for (EpipulseDiseaseExportEntryDto entry : entries) {
			if (entry.getComplicationDiagnosis() != null && entry.getComplicationDiagnosis().size() > maxComplicationDiagnosis) {
				maxComplicationDiagnosis = entry.getComplicationDiagnosis().size();
			}
			if (entry.getClusterSetting() != null && entry.getClusterSetting().size() > maxClusterSettings) {
				maxClusterSettings = entry.getClusterSetting().size();
			}
			if (entry.getPlaceOfInfection() != null && entry.getPlaceOfInfection().size() > maxPlaceOfInfection) {
				maxPlaceOfInfection = entry.getPlaceOfInfection().size();
			}
			if (entry.getTypeOfSpecimenCollected() != null && entry.getTypeOfSpecimenCollected().size() > maxSpecimenVirDetect) {
				maxSpecimenVirDetect = entry.getTypeOfSpecimenCollected().size();
			}
			if (entry.getTypeOfSpecimenSerology() != null && entry.getTypeOfSpecimenSerology().size() > maxSpecimenSero) {
				maxSpecimenSero = entry.getTypeOfSpecimenSerology().size();
			}
		}

		result.setMaxComplicationDiagnosis(maxComplicationDiagnosis);
		result.setMaxClusterSettings(maxClusterSettings);
		result.setMaxPlaceOfInfection(maxPlaceOfInfection);
		result.setMaxSpecimenVirDetect(maxSpecimenVirDetect);
		result.setMaxSpecimenSero(maxSpecimenSero);
	}

	// Helper methods for Measles-specific CTEs

	private String buildSampleDataCte() {
		//@formatter:off
		return ", sample_data AS (SELECT c.id as case_id," +
			   "                       MIN(s.sampledatetime) as first_specimen_date," +
			   "                       STRING_AGG(DISTINCT CAST(s2.samplematerial AS text), ',' ORDER BY CAST(s2.samplematerial AS text)) as specimen_types_virus," +
			   "                       STRING_AGG(DISTINCT CAST(s3.samplematerial AS text), ',' ORDER BY CAST(s3.samplematerial AS text)) as specimen_types_serology " +
			   "                FROM filtered_cases c " +
			   "                LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                LEFT JOIN (SELECT DISTINCT s_vir.id, s_vir.associatedcase_id, s_vir.samplematerial " +
			   "                           FROM samples s_vir " +
			   "                           JOIN pathogentest pt_vir ON pt_vir.sample_id = s_vir.id " +
			   "                           WHERE s_vir.deleted = false " +
			   "                             AND s_vir.samplematerial IS NOT NULL " +
			   "                             AND pt_vir.testtype IN ('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY')) s2 " +
			   "                          ON s2.associatedcase_id = c.id " +
			   "                LEFT JOIN (SELECT DISTINCT s_sero.id, s_sero.associatedcase_id, s_sero.samplematerial " +
			   "                           FROM samples s_sero " +
			   "                           JOIN pathogentest pt_sero ON pt_sero.sample_id = s_sero.id " +
			   "                           WHERE s_sero.deleted = false " +
			   "                             AND pt_sero.testtype IN ('IGG_SERUM_ANTIBODY', 'IGM_SERUM_ANTIBODY', 'SEROLOGY')) s3 " +
			   "                          ON s3.associatedcase_id = c.id " +
			   "                GROUP BY c.id), ";
		//@formatter:on
	}

	private String buildVirusDetectionDataCte() {
		//@formatter:off
		return "virus_detection_data AS (SELECT c.id as case_id," +
			   "                                 MIN(pt.testdatetime) as lab_result_date," +
			   "                                 (SELECT pt2.testresult " +
			   "                                  FROM samples s2 " +
			   "                                  JOIN pathogentest pt2 ON pt2.sample_id = s2.id " +
			   "                                  WHERE s2.associatedcase_id = c.id " +
			   "                                    AND s2.deleted = false " +
			   "                                    AND pt2.testtype IN ('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY') " +
			   "                                    AND pt2.testresultverified = true " +
			   "                                  ORDER BY pt2.testdatetime ASC " +
			   "                                  LIMIT 1) as virus_detection_result," +
			   "                                 (SELECT COALESCE(pt3.typingid, pt3.genotyperesult) " +
			   "                                  FROM samples s3 " +
			   "                                  JOIN pathogentest pt3 ON pt3.sample_id = s3.id " +
			   "                                  WHERE s3.associatedcase_id = c.id " +
			   "                                    AND s3.deleted = false " +
			   "                                    AND (pt3.typingid IS NOT NULL OR pt3.genotyperesult IS NOT NULL) " +
			   "                                  ORDER BY pt3.testdatetime ASC " +
			   "                                  LIMIT 1) as genotype_raw " +
			   "                          FROM filtered_cases c " +
			   "                          LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                          LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "                              AND pt.testtype IN ('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY') " +
			   "                              AND pt.testresultverified = true " +
			   "                          GROUP BY c.id), ";
		//@formatter:on
	}

	private String buildIggSerologyDataCte() {
		//@formatter:off
		return "igg_serology_data AS (SELECT c.id as case_id," +
			   "                              (SELECT CASE " +
			   "                                          WHEN pt_igg.fourfoldincreaseantibodytiter = 'YES' THEN 'POSITIVE' " +
			   "                                          ELSE pt_igg.testresult " +
			   "                                      END " +
			   "                               FROM samples s_igg " +
			   "                               JOIN pathogentest pt_igg ON pt_igg.sample_id = s_igg.id " +
			   "                               WHERE s_igg.associatedcase_id = c.id " +
			   "                                 AND s_igg.deleted = false " +
			   "                                 AND pt_igg.testtype = 'IGG_SERUM_ANTIBODY' " +
			   "                               ORDER BY pt_igg.testdatetime ASC " +
			   "                               LIMIT 1) as igg_result " +
			   "                      FROM filtered_cases c), ";
		//@formatter:on
	}

	private String buildIgmSerologyDataCte() {
		//@formatter:off
		return "igm_serology_data AS (SELECT c.id as case_id," +
			   "                              (SELECT pt_igm.testresult " +
			   "                               FROM samples s_igm " +
			   "                               JOIN pathogentest pt_igm ON pt_igm.sample_id = s_igm.id " +
			   "                               WHERE s_igm.associatedcase_id = c.id " +
			   "                                 AND s_igm.deleted = false " +
			   "                                 AND pt_igm.testtype = 'IGM_SERUM_ANTIBODY' " +
			   "                               ORDER BY pt_igm.testdatetime ASC " +
			   "                               LIMIT 1) as igm_result " +
			   "                      FROM filtered_cases c), ";
		//@formatter:on
	}

	private String buildEpidataClusterCte() {
		//@formatter:off
		return "epidata_cluster AS (SELECT c.id as case_id," +
			   "                            epi.clusterrelated," +
			   "                            epi.clustertypetext," +
			   "                            epi.clustertype," +
			   "                            epi.caseimportedstatus " +
			   "                     FROM filtered_cases c " +
			   "                     LEFT JOIN epidata epi ON c.epidata_id = epi.id), ";
		//@formatter:on
	}

	private String buildExposureLocationsCte() {
		//@formatter:off
		return "exposure_locations AS (SELECT e.epidata_id," +
			   "                              STRING_AGG(" +
			   "                                  CASE " +
			   "                                      WHEN co.defaultname IS NOT NULL THEN co.defaultname " +
			   "                                      WHEN l.city IS NOT NULL THEN l.city " +
			   "                                      WHEN l.details IS NOT NULL THEN l.details " +
			   "                                      ELSE 'Unknown' " +
			   "                                  END, " +
			   "                                  '; ' " +
			   "                                  ORDER BY e.startdate DESC" +
			   "                              ) as infection_locations " +
			   "                       FROM exposures e " +
			   "                       LEFT JOIN location l ON e.location_id = l.id " +
			   "                       LEFT JOIN country co ON l.country_id = co.id " +
			   "                       WHERE e.epidata_id IN (SELECT epidata_id FROM filtered_cases WHERE epidata_id IS NOT NULL) " +
			   "                       GROUP BY e.epidata_id), ";
		//@formatter:on
	}

	private String buildComplicationsDataCte() {
		//@formatter:off
		return "complications_data AS (SELECT c.id as case_id," +
			   "                              s.acuteencephalitis," +
			   "                              s.diarrhea," +
			   "                              s.otitismedia," +
			   "                              s.othercomplications " +
			   "                       FROM filtered_cases c " +
			   "                       LEFT JOIN symptoms s ON c.symptoms_id = s.id) ";
		//@formatter:on
	}

	private String buildMeaslesSelectClause() {
		StringBuilder select = new StringBuilder();
		//@formatter:off
		// Use common SELECT fields and append Measles-specific fields
		select.append("SELECT ")
		      .append(sqlCteBuilder.buildCommonSelectFields())
		      .append(",")
		      .append("       sd.first_specimen_date,")
		      .append("       vd.lab_result_date,")
		      .append("       sd.specimen_types_virus,")
		      .append("       vd.virus_detection_result,")
		      .append("       vd.genotype_raw,")
		      .append("       sd.specimen_types_serology,")
		      .append("       igg.igg_result,")
		      .append("       igm.igm_result,")
		      .append("       cast(c.investigateddate as date) as investigated_date,")
		      .append("       ec.clusterrelated,")
		      .append("       ec.clustertypetext,")
		      .append("       ec.clustertype,")
		      .append("       ec.caseimportedstatus,")
		      .append("       comp.acuteencephalitis,")
		      .append("       comp.diarrhea,")
		      .append("       comp.otitismedia,")
		      .append("       comp.othercomplications,")
		      .append("       c.clinicalconfirmation,")
		      .append("       el.infection_locations,")
		      .append("       person.causeofdeathdetails ");

		// Use common FROM and JOINs, then append Measles-specific joins
		select.append(sqlCteBuilder.buildCommonFromAndJoins())
		      .append("         LEFT JOIN sample_data sd ON sd.case_id = c.id")
		      .append("         LEFT JOIN virus_detection_data vd ON vd.case_id = c.id")
		      .append("         LEFT JOIN igg_serology_data igg ON igg.case_id = c.id")
		      .append("         LEFT JOIN igm_serology_data igm ON igm.case_id = c.id")
		      .append("         LEFT JOIN epidata_cluster ec ON ec.case_id = c.id")
		      .append("         LEFT JOIN exposure_locations el ON el.epidata_id = c.epidata_id")
		      .append("         LEFT JOIN complications_data comp ON comp.case_id = c.id ");

		select.append("ORDER BY c.reportdate");
		//@formatter:on

		return select.toString();
	}

	private List<String> parseSpecimenTypes(String specimenTypesRaw) {
		if (StringUtils.isBlank(specimenTypesRaw)) {
			return null;
		}
		List<String> specimenTypes = new ArrayList<>();
		for (String specimenType : specimenTypesRaw.split(",")) {
			SampleMaterial material = parseSampleMaterial(specimenType.trim());
			if (material != null) {
				specimenTypes.add(EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(material));
			}
		}
		return specimenTypes;
	}

	private SymptomState parseSymptomState(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return SymptomState.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid SymptomState value '{}', treating as null", value);
			return null;
		}
	}

	private SampleMaterial parseSampleMaterial(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return SampleMaterial.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid SampleMaterial value '{}', treating as null", value);
			return null;
		}
	}

	private PathogenTestResultType parsePathogenTestResultType(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return PathogenTestResultType.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid PathogenTestResultType value '{}', treating as null", value);
			return null;
		}
	}

	private ClusterType parseClusterType(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return ClusterType.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid ClusterType value '{}', treating as null", value);
			return null;
		}
	}

	private CaseImportedStatus parseCaseImportedStatus(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return CaseImportedStatus.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid CaseImportedStatus value '{}', treating as null", value);
			return null;
		}
	}

	private YesNoUnknown parseYesNoUnknown(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return YesNoUnknown.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid YesNoUnknown value '{}', treating as null", value);
			return null;
		}
	}
}
