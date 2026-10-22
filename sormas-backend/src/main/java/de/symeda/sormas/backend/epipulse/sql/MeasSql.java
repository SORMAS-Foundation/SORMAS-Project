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

package de.symeda.sormas.backend.epipulse.sql;

import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_COLLECTION_SEPARATOR;
import static de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql.exportDiseaseTest;

/**
 * Disease-specific SQL for MEAS.
 *
 * <p>
 * Defines all measles CTEs and aliases used by {@link MeasFieldModel}. Alias mismatches are not
 * silent: duplicate aliases are rejected and unknown aliases fail at SQL execution.
 */
public final class MeasSql {

	// REVIEW: These lists intentionally differ.
	// DateOfLabResult includes DIRECT/INDIRECT_FLUORESCENT_ANTIBODY and excludes
	// SANGER_SEQUENCING; ResultVirDetect does the opposite. This can produce a date without a
	// result (or vice versa). Both lists are taken from the source analysis.

	/**
	 * Test types used for {@code DateOfLabResult}; intentionally differs from
	 * {@link #VIRUS_DETECTION_RESULT_TEST_TYPES}.
	 */
	private static final String VIRUS_DETECTION_DATE_TEST_TYPES =
		"('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY', 'SEQUENCING', 'GENOTYPING')";

	/**
	 * Test types used for {@code ResultVirDetect}; intentionally differs from
	 * {@link #VIRUS_DETECTION_DATE_TEST_TYPES}.
	 */
	private static final String VIRUS_DETECTION_RESULT_TEST_TYPES =
		"('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'SEQUENCING', 'GENOTYPING', 'SANGER_SEQUENCING')";

	private MeasSql() {
	}

	/**
	 * Declares MEAS CTEs and joins.
	 *
	 * <p>
	 * {@code PlaceOfInfection} is shared SQL from {@link EpipulsePlaceOfInfectionSql}, merged in the
	 * field model rather than defined here.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(
				buildSampleDataCte(),
				buildVirusDetectionDataCte(),
				buildIggSerologyDataCte(),
				buildIgmSerologyDataCte(),
				buildEpidataClusterCte(),
				buildComplicationsDataCte())
			.joins(
				"LEFT JOIN sample_data sd ON sd.case_id = c.id",
				"LEFT JOIN virus_detection_data vd ON vd.case_id = c.id",
				"LEFT JOIN igg_serology_data igg ON igg.case_id = c.id",
				"LEFT JOIN igm_serology_data igm ON igm.case_id = c.id",
				"LEFT JOIN epidata_cluster ec ON ec.case_id = c.id",
				"LEFT JOIN complications_data comp ON comp.case_id = c.id")
			.build();
	}

	/**
	 * Builds {@code DateOfSpecimen}, {@code SpecimenVirDetect}, and {@code SpecimenSero}.
	 *
	 * <p>
	 * {@code DateOfSpecimen} and {@code SpecimenVirDetect} use all undeleted samples.
	 * {@code SpecimenSero} is narrower: only sample materials from samples that carry IgG/IgM tests.
	 * Untested samples are therefore dated but not counted as serology specimens.
	 */
	private static String buildSampleDataCte() {
		//@formatter:off
		return "sample_data AS (SELECT c.id as case_id," +
			   "                       MIN(s.sampledatetime) as first_specimen_date," +
			   "                       STRING_AGG(DISTINCT CAST(s2.samplematerial AS text), " + SQL_COLLECTION_SEPARATOR + " ORDER BY CAST(s2.samplematerial AS text)) as specimen_types_virus," +
			   "                       STRING_AGG(DISTINCT CAST(s3.samplematerial AS text), " + SQL_COLLECTION_SEPARATOR + " ORDER BY CAST(s3.samplematerial AS text)) as specimen_types_serology " +
			   "                FROM filtered_cases c " +
			   "                LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                LEFT JOIN (SELECT DISTINCT s_vir.associatedcase_id, s_vir.samplematerial " +
			   "                           FROM samples s_vir " +
			   "                           WHERE s_vir.deleted = false " +
			   "                             AND s_vir.samplematerial IS NOT NULL " +
			   "                             AND s_vir.associatedcase_id IN (SELECT id FROM filtered_cases)) s2 " +
			   "                          ON s2.associatedcase_id = c.id " +
			   "                LEFT JOIN (SELECT DISTINCT s_sero.associatedcase_id, s_sero.samplematerial " +
			   "                           FROM samples s_sero " +
			   "                           JOIN pathogentest pt_sero ON pt_sero.sample_id = s_sero.id AND " + exportDiseaseTest("pt_sero") + " " +
			   "                               AND pt_sero.testtype IN ('IGG_SERUM_ANTIBODY', 'IGM_SERUM_ANTIBODY') " +
			   "                           WHERE s_sero.deleted = false " +
			   "                             AND s_sero.samplematerial IS NOT NULL " +
			   "                             AND s_sero.associatedcase_id IN (SELECT id FROM filtered_cases)) s3 " +
			   "                          ON s3.associatedcase_id = c.id " +
			   "                GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * Builds {@code DateOfLabResult}, {@code ResultVirDetect}, and {@code Genotype}.
	 *
	 * <p>
	 * These values are selected independently: earliest virus-detection date across the date test
	 * list, first verified result across the result test list, and first verified non-null
	 * genotype. Tests are ordered by {@code COALESCE(testdatetime, reportdate, creationdate)}.
	 */
	private static String buildVirusDetectionDataCte() {
		//@formatter:off
		return "virus_detection_data AS (SELECT c.id as case_id," +
			   "                                 MIN(COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate)) as lab_result_date," +
			   "                                 (SELECT pt2.testresult " +
			   "                                  FROM samples s2 " +
			   "                                  JOIN pathogentest pt2 ON pt2.sample_id = s2.id AND " + exportDiseaseTest("pt2") + " " +
			   "                                  WHERE s2.associatedcase_id = c.id " +
			   "                                    AND s2.deleted = false " +
			   "                                    AND pt2.testresultverified = true " +
			   "                                    AND pt2.testtype IN " + VIRUS_DETECTION_RESULT_TEST_TYPES + " " +
			   "                                  ORDER BY COALESCE(pt2.testdatetime, pt2.reportdate, pt2.creationdate) ASC " +
			   "                                  LIMIT 1) as virus_detection_result," +
			   "                                 (SELECT pt3.genotype " +
			   "                                  FROM samples s3 " +
			   "                                  JOIN pathogentest pt3 ON pt3.sample_id = s3.id AND " + exportDiseaseTest("pt3") + " " +
			   "                                  WHERE s3.associatedcase_id = c.id " +
			   "                                    AND s3.deleted = false " +
			   "                                    AND pt3.testresultverified = true " +
			   "                                    AND pt3.genotype IS NOT NULL " +
			   "                                  ORDER BY COALESCE(pt3.testdatetime, pt3.reportdate, pt3.creationdate) ASC " +
			   "                                  LIMIT 1) as genotype_raw " +
			   "                          FROM filtered_cases c " +
			   "                          LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                          LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                              AND pt.testtype IN " + VIRUS_DETECTION_DATE_TEST_TYPES + " " +
			   "                          GROUP BY c.id)";
		//@formatter:on
	}

	private static String buildIggSerologyDataCte() {
		//@formatter:off
		return "igg_serology_data AS (SELECT c.id as case_id," +
			   "                              (SELECT CASE " +
			   // Boolean flag: true maps to POSITIVE before falling back to testresult.
			   "                                          WHEN pt_igg.fourfoldincreaseantibodytiter = true THEN 'POSITIVE' " +
			   "                                          ELSE pt_igg.testresult " +
			   "                                      END " +
			   "                               FROM samples s_igg " +
			   "                               JOIN pathogentest pt_igg ON pt_igg.sample_id = s_igg.id AND " + exportDiseaseTest("pt_igg") + " " +
			   "                               WHERE s_igg.associatedcase_id = c.id " +
			   "                                 AND s_igg.deleted = false " +
			   "                                 AND pt_igg.testresultverified = true " +
			   "                                 AND pt_igg.testtype = 'IGG_SERUM_ANTIBODY' " +
			   "                               ORDER BY COALESCE(pt_igg.testdatetime, pt_igg.reportdate, pt_igg.creationdate) ASC " +
			   "                               LIMIT 1) as igg_result " +
			   "                      FROM filtered_cases c)";
		//@formatter:on
	}

	private static String buildIgmSerologyDataCte() {
		//@formatter:off
		return "igm_serology_data AS (SELECT c.id as case_id," +
			   "                              (SELECT pt_igm.testresult " +
			   "                               FROM samples s_igm " +
			   "                               JOIN pathogentest pt_igm ON pt_igm.sample_id = s_igm.id AND " + exportDiseaseTest("pt_igm") + " " +
			   "                               WHERE s_igm.associatedcase_id = c.id " +
			   "                                 AND s_igm.deleted = false " +
			   "                                 AND pt_igm.testresultverified = true " +
			   "                                 AND pt_igm.testtype = 'IGM_SERUM_ANTIBODY' " +
			   "                               ORDER BY COALESCE(pt_igm.testdatetime, pt_igm.reportdate, pt_igm.creationdate) ASC " +
			   "                               LIMIT 1) as igm_result " +
			   "                      FROM filtered_cases c)";
		//@formatter:on
	}

	private static String buildEpidataClusterCte() {
		//@formatter:off
		return "epidata_cluster AS (SELECT c.id as case_id," +
			   "                            epi.clusterrelated," +
			   "                            epi.clustertypetext," +
			   "                            epi.clustertype," +
			   "                            epi.caseimportedstatus " +
			   "                     FROM filtered_cases c " +
			   "                     LEFT JOIN epidata epi ON c.epidata_id = epi.id)";
		//@formatter:on
	}

	private static String buildComplicationsDataCte() {
		//@formatter:off
		return "complications_data AS (SELECT c.id as case_id," +
			   "                              s.acuteencephalitis," +
			   "                              s.diarrhea," +
			   "                              s.otitismedia," +
			   "                              s.pneumoniaclinicalorradiologic," +
			   "                              s.othercomplications " +
			   "                       FROM filtered_cases c " +
			   "                       LEFT JOIN symptoms s ON c.symptoms_id = s.id)";
		//@formatter:on
	}
}
