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

package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.SAMPLES_LAB,
	FeatureType.CASE_SURVEILANCE })
public class DrugSusceptibilityDto extends PseudonymizableDto {

	private static final long serialVersionUID = 2688860305284961183L;

	public static final String I18N_PREFIX = "DrugSusceptibility";

	public static final String AMIKACIN_MIC = "amikacinMic";
	public static final String AMIKACIN_SUSCEPTIBILITY = "amikacinSusceptibility";
	public static final String BEDAQUILINE_MIC = "bedaquilineMic";
	public static final String BEDAQUILINE_SUSCEPTIBILITY = "bedaquilineSusceptibility";
	public static final String CAPREOMYCIN_MIC = "capreomycinMic";
	public static final String CAPREOMYCIN_SUSCEPTIBILITY = "capreomycinSusceptibility";
	public static final String CIPROFLOXACIN_MIC = "ciprofloxacinMic";
	public static final String CIPROFLOXACIN_SUSCEPTIBILITY = "ciprofloxacinSusceptibility";
	public static final String DELAMANID_MIC = "delamanidMic";
	public static final String DELAMANID_SUSCEPTIBILITY = "delamanidSusceptibility";
	public static final String ETHAMBUTOL_MIC = "ethambutolMic";
	public static final String ETHAMBUTOL_SUSCEPTIBILITY = "ethambutolSusceptibility";
	public static final String GATIFLOXACIN_MIC = "gatifloxacinMic";
	public static final String GATIFLOXACIN_SUSCEPTIBILITY = "gatifloxacinSusceptibility";
	public static final String ISONIAZID_MIC = "isoniazidMic";
	public static final String ISONIAZID_SUSCEPTIBILITY = "isoniazidSusceptibility";
	public static final String KANAMYCIN_MIC = "kanamycinMic";
	public static final String KANAMYCIN_SUSCEPTIBILITY = "kanamycinSusceptibility";
	public static final String LEVOFLOXACIN_MIC = "levofloxacinMic";
	public static final String LEVOFLOXACIN_SUSCEPTIBILITY = "levofloxacinSusceptibility";
	public static final String MOXIFLOXACIN_MIC = "moxifloxacinMic";
	public static final String MOXIFLOXACIN_SUSCEPTIBILITY = "moxifloxacinSusceptibility";
	public static final String OFLOXACIN_MIC = "ofloxacinMic";
	public static final String OFLOXACIN_SUSCEPTIBILITY = "ofloxacinSusceptibility";
	public static final String RIFAMPICIN_MIC = "rifampicinMic";
	public static final String RIFAMPICIN_SUSCEPTIBILITY = "rifampicinSusceptibility";
	public static final String STREPTOMYCIN_MIC = "streptomycinMic";
	public static final String STREPTOMYCIN_SUSCEPTIBILITY = "streptomycinSusceptibility";
	public static final String CEFTRIAXONE_MIC = "ceftriaxoneMic";
	public static final String CEFTRIAXONE_SUSCEPTIBILITY = "ceftriaxoneSusceptibility";
	public static final String PENICILLIN_MIC = "penicillinMic";
	public static final String PENICILLIN_SUSCEPTIBILITY = "penicillinSusceptibility";
	public static final String ERYTHROMYCIN_MIC = "erythromycinMic";
	public static final String ERYTHROMYCIN_SUSCEPTIBILITY = "erythromycinSusceptibility";

	public static final String AMIKACIN_METHOD = "amikacinMethod";
	public static final String AMIKACIN_ZONE_DIAMETER = "amikacinZoneDiameter";
	public static final String AMIKACIN_SURVEILLANCE = "amikacinSurveillance";
	public static final String BEDAQUILINE_METHOD = "bedaquilineMethod";
	public static final String BEDAQUILINE_ZONE_DIAMETER = "bedaquilineZoneDiameter";
	public static final String BEDAQUILINE_SURVEILLANCE = "bedaquilineSurveillance";
	public static final String CAPREOMYCIN_METHOD = "capreomycinMethod";
	public static final String CAPREOMYCIN_ZONE_DIAMETER = "capreomycinZoneDiameter";
	public static final String CAPREOMYCIN_SURVEILLANCE = "capreomycinSurveillance";
	public static final String CIPROFLOXACIN_METHOD = "ciprofloxacinMethod";
	public static final String CIPROFLOXACIN_ZONE_DIAMETER = "ciprofloxacinZoneDiameter";
	public static final String CIPROFLOXACIN_SURVEILLANCE = "ciprofloxacinSurveillance";
	public static final String DELAMANID_METHOD = "delamanidMethod";
	public static final String DELAMANID_ZONE_DIAMETER = "delamanidZoneDiameter";
	public static final String DELAMANID_SURVEILLANCE = "delamanidSurveillance";
	public static final String ETHAMBUTOL_METHOD = "ethambutolMethod";
	public static final String ETHAMBUTOL_ZONE_DIAMETER = "ethambutolZoneDiameter";
	public static final String ETHAMBUTOL_SURVEILLANCE = "ethambutolSurveillance";
	public static final String GATIFLOXACIN_METHOD = "gatifloxacinMethod";
	public static final String GATIFLOXACIN_ZONE_DIAMETER = "gatifloxacinZoneDiameter";
	public static final String GATIFLOXACIN_SURVEILLANCE = "gatifloxacinSurveillance";
	public static final String ISONIAZID_METHOD = "isoniazidMethod";
	public static final String ISONIAZID_ZONE_DIAMETER = "isoniazidZoneDiameter";
	public static final String ISONIAZID_SURVEILLANCE = "isoniazidSurveillance";
	public static final String KANAMYCIN_METHOD = "kanamycinMethod";
	public static final String KANAMYCIN_ZONE_DIAMETER = "kanamycinZoneDiameter";
	public static final String KANAMYCIN_SURVEILLANCE = "kanamycinSurveillance";
	public static final String LEVOFLOXACIN_METHOD = "levofloxacinMethod";
	public static final String LEVOFLOXACIN_ZONE_DIAMETER = "levofloxacinZoneDiameter";
	public static final String LEVOFLOXACIN_SURVEILLANCE = "levofloxacinSurveillance";
	public static final String MOXIFLOXACIN_METHOD = "moxifloxacinMethod";
	public static final String MOXIFLOXACIN_ZONE_DIAMETER = "moxifloxacinZoneDiameter";
	public static final String MOXIFLOXACIN_SURVEILLANCE = "moxifloxacinSurveillance";
	public static final String OFLOXACIN_METHOD = "ofloxacinMethod";
	public static final String OFLOXACIN_ZONE_DIAMETER = "ofloxacinZoneDiameter";
	public static final String OFLOXACIN_SURVEILLANCE = "ofloxacinSurveillance";
	public static final String RIFAMPICIN_METHOD = "rifampicinMethod";
	public static final String RIFAMPICIN_ZONE_DIAMETER = "rifampicinZoneDiameter";
	public static final String RIFAMPICIN_SURVEILLANCE = "rifampicinSurveillance";
	public static final String STREPTOMYCIN_METHOD = "streptomycinMethod";
	public static final String STREPTOMYCIN_ZONE_DIAMETER = "streptomycinZoneDiameter";
	public static final String STREPTOMYCIN_SURVEILLANCE = "streptomycinSurveillance";
	public static final String CEFTRIAXONE_METHOD = "ceftriaxoneMethod";
	public static final String CEFTRIAXONE_ZONE_DIAMETER = "ceftriaxoneZoneDiameter";
	public static final String CEFTRIAXONE_SURVEILLANCE = "ceftriaxoneSurveillance";
	public static final String PENICILLIN_METHOD = "penicillinMethod";
	public static final String PENICILLIN_ZONE_DIAMETER = "penicillinZoneDiameter";
	public static final String PENICILLIN_SURVEILLANCE = "penicillinSurveillance";
	public static final String ERYTHROMYCIN_METHOD = "erythromycinMethod";
	public static final String ERYTHROMYCIN_ZONE_DIAMETER = "erythromycinZoneDiameter";
	public static final String ERYTHROMYCIN_SURVEILLANCE = "erythromycinSurveillance";

	public static final String AZITHROMYCIN_MIC = "azithromycinMic";
	public static final String AZITHROMYCIN_SUSCEPTIBILITY = "azithromycinSusceptibility";
	public static final String AZITHROMYCIN_METHOD = "azithromycinMethod";
	public static final String CEFTAZIDIME_MIC = "ceftazidimeMic";
	public static final String CEFTAZIDIME_SUSCEPTIBILITY = "ceftazidimeSusceptibility";
	public static final String CEFTAZIDIME_METHOD = "ceftazidimeMethod";
	public static final String CEFOTAXIME_MIC = "cefotaximeMic";
	public static final String CEFOTAXIME_SUSCEPTIBILITY = "cefotaximeSusceptibility";
	public static final String CEFOTAXIME_METHOD = "cefotaximeMethod";
	public static final String AMPICILLIN_MIC = "ampicillinMic";
	public static final String AMPICILLIN_SUSCEPTIBILITY = "ampicillinSusceptibility";
	public static final String AMPICILLIN_METHOD = "ampicillinMethod";
	public static final String TRIMETHOPRIM_SULFAMETHOXAZOLE_MIC = "trimethoprimSulfamethoxazoleMic";
	public static final String TRIMETHOPRIM_SULFAMETHOXAZOLE_SUSCEPTIBILITY = "trimethoprimSulfamethoxazoleSusceptibility";
	public static final String TRIMETHOPRIM_SULFAMETHOXAZOLE_METHOD = "trimethoprimSulfamethoxazoleMethod";

	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float amikacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType amikacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float bedaquilineMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType bedaquilineSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float capreomycinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType capreomycinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ciprofloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ciprofloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float delamanidMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType delamanidSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ethambutolMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ethambutolSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float gatifloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType gatifloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float isoniazidMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType isoniazidSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float kanamycinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType kanamycinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float levofloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType levofloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float moxifloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType moxifloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ofloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ofloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float rifampicinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType rifampicinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float streptomycinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType streptomycinSusceptibility;

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ceftriaxoneMic;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ceftriaxoneSusceptibility;

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float penicillinMic;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType penicillinSusceptibility;

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float erythromycinMic;
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType erythromycinSusceptibility;

	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod amikacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float amikacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType amikacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod bedaquilineMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float bedaquilineZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType bedaquilineSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod capreomycinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float capreomycinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType capreomycinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ciprofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ciprofloxacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType ciprofloxacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod delamanidMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float delamanidZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType delamanidSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ethambutolMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ethambutolZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType ethambutolSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod gatifloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float gatifloxacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType gatifloxacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod isoniazidMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float isoniazidZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType isoniazidSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod kanamycinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float kanamycinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType kanamycinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod levofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float levofloxacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType levofloxacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod moxifloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float moxifloxacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType moxifloxacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ofloxacinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType ofloxacinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod rifampicinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float rifampicinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType rifampicinSurveillance;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod streptomycinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float streptomycinZoneDiameter;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType streptomycinSurveillance;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ceftriaxoneMethod;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ceftriaxoneZoneDiameter;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType ceftriaxoneSurveillance;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod penicillinMethod;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float penicillinZoneDiameter;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType penicillinSurveillance;
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod erythromycinMethod;
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float erythromycinZoneDiameter;
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilitySurveillanceType erythromycinSurveillance;

	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float azithromycinMic;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType azithromycinSusceptibility;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod azithromycinMethod;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ceftazidimeMic;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ceftazidimeSusceptibility;

	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ceftazidimeMethod;

	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float cefotaximeMic;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType cefotaximeSusceptibility;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod cefotaximeMethod;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ampicillinMic;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ampicillinSusceptibility;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ampicillinMethod;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float trimethoprimSulfamethoxazoleMic;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType trimethoprimSulfamethoxazoleSusceptibility;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod trimethoprimSulfamethoxazoleMethod;

	public static DrugSusceptibilityDto build() {
		DrugSusceptibilityDto drugSusceptibility = new DrugSusceptibilityDto();
		drugSusceptibility.setUuid(DataHelper.createUuid());
		return drugSusceptibility;
	}

	public Float getAmikacinMic() {
		return amikacinMic;
	}

	public void setAmikacinMic(Float amikacinMic) {
		this.amikacinMic = amikacinMic;
	}

	public DrugSusceptibilityType getAmikacinSusceptibility() {
		return amikacinSusceptibility;
	}

	public void setAmikacinSusceptibility(DrugSusceptibilityType amikacinSusceptibility) {
		this.amikacinSusceptibility = amikacinSusceptibility;
	}

	public Float getBedaquilineMic() {
		return bedaquilineMic;
	}

	public void setBedaquilineMic(Float bedaquilineMic) {
		this.bedaquilineMic = bedaquilineMic;
	}

	public DrugSusceptibilityType getBedaquilineSusceptibility() {
		return bedaquilineSusceptibility;
	}

	public void setBedaquilineSusceptibility(DrugSusceptibilityType bedaquilineSusceptibility) {
		this.bedaquilineSusceptibility = bedaquilineSusceptibility;
	}

	public Float getCapreomycinMic() {
		return capreomycinMic;
	}

	public void setCapreomycinMic(Float capreomycinMic) {
		this.capreomycinMic = capreomycinMic;
	}

	public DrugSusceptibilityType getCapreomycinSusceptibility() {
		return capreomycinSusceptibility;
	}

	public void setCapreomycinSusceptibility(DrugSusceptibilityType capreomycinSusceptibility) {
		this.capreomycinSusceptibility = capreomycinSusceptibility;
	}

	public Float getCiprofloxacinMic() {
		return ciprofloxacinMic;
	}

	public void setCiprofloxacinMic(Float ciprofloxacinMic) {
		this.ciprofloxacinMic = ciprofloxacinMic;
	}

	public DrugSusceptibilityType getCiprofloxacinSusceptibility() {
		return ciprofloxacinSusceptibility;
	}

	public void setCiprofloxacinSusceptibility(DrugSusceptibilityType ciprofloxacinSusceptibility) {
		this.ciprofloxacinSusceptibility = ciprofloxacinSusceptibility;
	}

	public Float getDelamanidMic() {
		return delamanidMic;
	}

	public void setDelamanidMic(Float delamanidMic) {
		this.delamanidMic = delamanidMic;
	}

	public DrugSusceptibilityType getDelamanidSusceptibility() {
		return delamanidSusceptibility;
	}

	public void setDelamanidSusceptibility(DrugSusceptibilityType delamanidSusceptibility) {
		this.delamanidSusceptibility = delamanidSusceptibility;
	}

	public Float getEthambutolMic() {
		return ethambutolMic;
	}

	public void setEthambutolMic(Float ethambutolMic) {
		this.ethambutolMic = ethambutolMic;
	}

	public DrugSusceptibilityType getEthambutolSusceptibility() {
		return ethambutolSusceptibility;
	}

	public void setEthambutolSusceptibility(DrugSusceptibilityType ethambutolSusceptibility) {
		this.ethambutolSusceptibility = ethambutolSusceptibility;
	}

	public Float getGatifloxacinMic() {
		return gatifloxacinMic;
	}

	public void setGatifloxacinMic(Float gatifloxacinMic) {
		this.gatifloxacinMic = gatifloxacinMic;
	}

	public DrugSusceptibilityType getGatifloxacinSusceptibility() {
		return gatifloxacinSusceptibility;
	}

	public void setGatifloxacinSusceptibility(DrugSusceptibilityType gatifloxacinSusceptibility) {
		this.gatifloxacinSusceptibility = gatifloxacinSusceptibility;
	}

	public Float getIsoniazidMic() {
		return isoniazidMic;
	}

	public void setIsoniazidMic(Float isoniazidMic) {
		this.isoniazidMic = isoniazidMic;
	}

	public DrugSusceptibilityType getIsoniazidSusceptibility() {
		return isoniazidSusceptibility;
	}

	public void setIsoniazidSusceptibility(DrugSusceptibilityType isoniazidSusceptibility) {
		this.isoniazidSusceptibility = isoniazidSusceptibility;
	}

	public Float getKanamycinMic() {
		return kanamycinMic;
	}

	public void setKanamycinMic(Float kanamycinMic) {
		this.kanamycinMic = kanamycinMic;
	}

	public DrugSusceptibilityType getKanamycinSusceptibility() {
		return kanamycinSusceptibility;
	}

	public void setKanamycinSusceptibility(DrugSusceptibilityType kanamycinSusceptibility) {
		this.kanamycinSusceptibility = kanamycinSusceptibility;
	}

	public Float getLevofloxacinMic() {
		return levofloxacinMic;
	}

	public void setLevofloxacinMic(Float levofloxacinMic) {
		this.levofloxacinMic = levofloxacinMic;
	}

	public DrugSusceptibilityType getLevofloxacinSusceptibility() {
		return levofloxacinSusceptibility;
	}

	public void setLevofloxacinSusceptibility(DrugSusceptibilityType levofloxacinSusceptibility) {
		this.levofloxacinSusceptibility = levofloxacinSusceptibility;
	}

	public Float getMoxifloxacinMic() {
		return moxifloxacinMic;
	}

	public void setMoxifloxacinMic(Float moxifloxacinMic) {
		this.moxifloxacinMic = moxifloxacinMic;
	}

	public DrugSusceptibilityType getMoxifloxacinSusceptibility() {
		return moxifloxacinSusceptibility;
	}

	public void setMoxifloxacinSusceptibility(DrugSusceptibilityType moxifloxacinSusceptibility) {
		this.moxifloxacinSusceptibility = moxifloxacinSusceptibility;
	}

	public Float getOfloxacinMic() {
		return ofloxacinMic;
	}

	public void setOfloxacinMic(Float ofloxacinMic) {
		this.ofloxacinMic = ofloxacinMic;
	}

	public DrugSusceptibilityType getOfloxacinSusceptibility() {
		return ofloxacinSusceptibility;
	}

	public void setOfloxacinSusceptibility(DrugSusceptibilityType ofloxacinSusceptibility) {
		this.ofloxacinSusceptibility = ofloxacinSusceptibility;
	}

	public Float getRifampicinMic() {
		return rifampicinMic;
	}

	public void setRifampicinMic(Float rifampicinMic) {
		this.rifampicinMic = rifampicinMic;
	}

	public DrugSusceptibilityType getRifampicinSusceptibility() {
		return rifampicinSusceptibility;
	}

	public void setRifampicinSusceptibility(DrugSusceptibilityType rifampicinSusceptibility) {
		this.rifampicinSusceptibility = rifampicinSusceptibility;
	}

	public Float getStreptomycinMic() {
		return streptomycinMic;
	}

	public void setStreptomycinMic(Float streptomycinMic) {
		this.streptomycinMic = streptomycinMic;
	}

	public DrugSusceptibilityType getStreptomycinSusceptibility() {
		return streptomycinSusceptibility;
	}

	public void setStreptomycinSusceptibility(DrugSusceptibilityType streptomycinSusceptibility) {
		this.streptomycinSusceptibility = streptomycinSusceptibility;
	}

	public Float getCeftriaxoneMic() {
		return ceftriaxoneMic;
	}

	public void setCeftriaxoneMic(Float ceftriaxoneMic) {
		this.ceftriaxoneMic = ceftriaxoneMic;
	}

	public DrugSusceptibilityType getCeftriaxoneSusceptibility() {
		return ceftriaxoneSusceptibility;
	}

	public void setCeftriaxoneSusceptibility(DrugSusceptibilityType ceftriaxoneSusceptibility) {
		this.ceftriaxoneSusceptibility = ceftriaxoneSusceptibility;
	}

	public Float getPenicillinMic() {
		return penicillinMic;
	}

	public void setPenicillinMic(Float penicillinMic) {
		this.penicillinMic = penicillinMic;
	}

	public DrugSusceptibilityType getPenicillinSusceptibility() {
		return penicillinSusceptibility;
	}

	public void setPenicillinSusceptibility(DrugSusceptibilityType penicillinSusceptibility) {
		this.penicillinSusceptibility = penicillinSusceptibility;
	}

	public Float getErythromycinMic() {
		return erythromycinMic;
	}

	public void setErythromycinMic(Float erythromycinMic) {
		this.erythromycinMic = erythromycinMic;
	}

	public DrugSusceptibilityType getErythromycinSusceptibility() {
		return erythromycinSusceptibility;
	}

	public void setErythromycinSusceptibility(DrugSusceptibilityType erythromycinSusceptibility) {
		this.erythromycinSusceptibility = erythromycinSusceptibility;
	}

	public SusceptibilityMethod getAmikacinMethod() {
		return amikacinMethod;
	}

	public void setAmikacinMethod(SusceptibilityMethod amikacinMethod) {
		this.amikacinMethod = amikacinMethod;
	}

	public Float getAmikacinZoneDiameter() {
		return amikacinZoneDiameter;
	}

	public void setAmikacinZoneDiameter(Float amikacinZoneDiameter) {
		this.amikacinZoneDiameter = amikacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getAmikacinSurveillance() {
		return amikacinSurveillance;
	}

	public void setAmikacinSurveillance(SusceptibilitySurveillanceType amikacinSurveillance) {
		this.amikacinSurveillance = amikacinSurveillance;
	}

	public SusceptibilityMethod getBedaquilineMethod() {
		return bedaquilineMethod;
	}

	public void setBedaquilineMethod(SusceptibilityMethod bedaquilineMethod) {
		this.bedaquilineMethod = bedaquilineMethod;
	}

	public Float getBedaquilineZoneDiameter() {
		return bedaquilineZoneDiameter;
	}

	public void setBedaquilineZoneDiameter(Float bedaquilineZoneDiameter) {
		this.bedaquilineZoneDiameter = bedaquilineZoneDiameter;
	}

	public SusceptibilitySurveillanceType getBedaquilineSurveillance() {
		return bedaquilineSurveillance;
	}

	public void setBedaquilineSurveillance(SusceptibilitySurveillanceType bedaquilineSurveillance) {
		this.bedaquilineSurveillance = bedaquilineSurveillance;
	}

	public SusceptibilityMethod getCapreomycinMethod() {
		return capreomycinMethod;
	}

	public void setCapreomycinMethod(SusceptibilityMethod capreomycinMethod) {
		this.capreomycinMethod = capreomycinMethod;
	}

	public Float getCapreomycinZoneDiameter() {
		return capreomycinZoneDiameter;
	}

	public void setCapreomycinZoneDiameter(Float capreomycinZoneDiameter) {
		this.capreomycinZoneDiameter = capreomycinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getCapreomycinSurveillance() {
		return capreomycinSurveillance;
	}

	public void setCapreomycinSurveillance(SusceptibilitySurveillanceType capreomycinSurveillance) {
		this.capreomycinSurveillance = capreomycinSurveillance;
	}

	public SusceptibilityMethod getCiprofloxacinMethod() {
		return ciprofloxacinMethod;
	}

	public void setCiprofloxacinMethod(SusceptibilityMethod ciprofloxacinMethod) {
		this.ciprofloxacinMethod = ciprofloxacinMethod;
	}

	public Float getCiprofloxacinZoneDiameter() {
		return ciprofloxacinZoneDiameter;
	}

	public void setCiprofloxacinZoneDiameter(Float ciprofloxacinZoneDiameter) {
		this.ciprofloxacinZoneDiameter = ciprofloxacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getCiprofloxacinSurveillance() {
		return ciprofloxacinSurveillance;
	}

	public void setCiprofloxacinSurveillance(SusceptibilitySurveillanceType ciprofloxacinSurveillance) {
		this.ciprofloxacinSurveillance = ciprofloxacinSurveillance;
	}

	public SusceptibilityMethod getDelamanidMethod() {
		return delamanidMethod;
	}

	public void setDelamanidMethod(SusceptibilityMethod delamanidMethod) {
		this.delamanidMethod = delamanidMethod;
	}

	public Float getDelamanidZoneDiameter() {
		return delamanidZoneDiameter;
	}

	public void setDelamanidZoneDiameter(Float delamanidZoneDiameter) {
		this.delamanidZoneDiameter = delamanidZoneDiameter;
	}

	public SusceptibilitySurveillanceType getDelamanidSurveillance() {
		return delamanidSurveillance;
	}

	public void setDelamanidSurveillance(SusceptibilitySurveillanceType delamanidSurveillance) {
		this.delamanidSurveillance = delamanidSurveillance;
	}

	public SusceptibilityMethod getEthambutolMethod() {
		return ethambutolMethod;
	}

	public void setEthambutolMethod(SusceptibilityMethod ethambutolMethod) {
		this.ethambutolMethod = ethambutolMethod;
	}

	public Float getEthambutolZoneDiameter() {
		return ethambutolZoneDiameter;
	}

	public void setEthambutolZoneDiameter(Float ethambutolZoneDiameter) {
		this.ethambutolZoneDiameter = ethambutolZoneDiameter;
	}

	public SusceptibilitySurveillanceType getEthambutolSurveillance() {
		return ethambutolSurveillance;
	}

	public void setEthambutolSurveillance(SusceptibilitySurveillanceType ethambutolSurveillance) {
		this.ethambutolSurveillance = ethambutolSurveillance;
	}

	public SusceptibilityMethod getGatifloxacinMethod() {
		return gatifloxacinMethod;
	}

	public void setGatifloxacinMethod(SusceptibilityMethod gatifloxacinMethod) {
		this.gatifloxacinMethod = gatifloxacinMethod;
	}

	public Float getGatifloxacinZoneDiameter() {
		return gatifloxacinZoneDiameter;
	}

	public void setGatifloxacinZoneDiameter(Float gatifloxacinZoneDiameter) {
		this.gatifloxacinZoneDiameter = gatifloxacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getGatifloxacinSurveillance() {
		return gatifloxacinSurveillance;
	}

	public void setGatifloxacinSurveillance(SusceptibilitySurveillanceType gatifloxacinSurveillance) {
		this.gatifloxacinSurveillance = gatifloxacinSurveillance;
	}

	public SusceptibilityMethod getIsoniazidMethod() {
		return isoniazidMethod;
	}

	public void setIsoniazidMethod(SusceptibilityMethod isoniazidMethod) {
		this.isoniazidMethod = isoniazidMethod;
	}

	public Float getIsoniazidZoneDiameter() {
		return isoniazidZoneDiameter;
	}

	public void setIsoniazidZoneDiameter(Float isoniazidZoneDiameter) {
		this.isoniazidZoneDiameter = isoniazidZoneDiameter;
	}

	public SusceptibilitySurveillanceType getIsoniazidSurveillance() {
		return isoniazidSurveillance;
	}

	public void setIsoniazidSurveillance(SusceptibilitySurveillanceType isoniazidSurveillance) {
		this.isoniazidSurveillance = isoniazidSurveillance;
	}

	public SusceptibilityMethod getKanamycinMethod() {
		return kanamycinMethod;
	}

	public void setKanamycinMethod(SusceptibilityMethod kanamycinMethod) {
		this.kanamycinMethod = kanamycinMethod;
	}

	public Float getKanamycinZoneDiameter() {
		return kanamycinZoneDiameter;
	}

	public void setKanamycinZoneDiameter(Float kanamycinZoneDiameter) {
		this.kanamycinZoneDiameter = kanamycinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getKanamycinSurveillance() {
		return kanamycinSurveillance;
	}

	public void setKanamycinSurveillance(SusceptibilitySurveillanceType kanamycinSurveillance) {
		this.kanamycinSurveillance = kanamycinSurveillance;
	}

	public SusceptibilityMethod getLevofloxacinMethod() {
		return levofloxacinMethod;
	}

	public void setLevofloxacinMethod(SusceptibilityMethod levofloxacinMethod) {
		this.levofloxacinMethod = levofloxacinMethod;
	}

	public Float getLevofloxacinZoneDiameter() {
		return levofloxacinZoneDiameter;
	}

	public void setLevofloxacinZoneDiameter(Float levofloxacinZoneDiameter) {
		this.levofloxacinZoneDiameter = levofloxacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getLevofloxacinSurveillance() {
		return levofloxacinSurveillance;
	}

	public void setLevofloxacinSurveillance(SusceptibilitySurveillanceType levofloxacinSurveillance) {
		this.levofloxacinSurveillance = levofloxacinSurveillance;
	}

	public SusceptibilityMethod getMoxifloxacinMethod() {
		return moxifloxacinMethod;
	}

	public void setMoxifloxacinMethod(SusceptibilityMethod moxifloxacinMethod) {
		this.moxifloxacinMethod = moxifloxacinMethod;
	}

	public Float getMoxifloxacinZoneDiameter() {
		return moxifloxacinZoneDiameter;
	}

	public void setMoxifloxacinZoneDiameter(Float moxifloxacinZoneDiameter) {
		this.moxifloxacinZoneDiameter = moxifloxacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getMoxifloxacinSurveillance() {
		return moxifloxacinSurveillance;
	}

	public void setMoxifloxacinSurveillance(SusceptibilitySurveillanceType moxifloxacinSurveillance) {
		this.moxifloxacinSurveillance = moxifloxacinSurveillance;
	}

	public SusceptibilityMethod getOfloxacinMethod() {
		return ofloxacinMethod;
	}

	public void setOfloxacinMethod(SusceptibilityMethod ofloxacinMethod) {
		this.ofloxacinMethod = ofloxacinMethod;
	}

	public Float getOfloxacinZoneDiameter() {
		return ofloxacinZoneDiameter;
	}

	public void setOfloxacinZoneDiameter(Float ofloxacinZoneDiameter) {
		this.ofloxacinZoneDiameter = ofloxacinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getOfloxacinSurveillance() {
		return ofloxacinSurveillance;
	}

	public void setOfloxacinSurveillance(SusceptibilitySurveillanceType ofloxacinSurveillance) {
		this.ofloxacinSurveillance = ofloxacinSurveillance;
	}

	public SusceptibilityMethod getRifampicinMethod() {
		return rifampicinMethod;
	}

	public void setRifampicinMethod(SusceptibilityMethod rifampicinMethod) {
		this.rifampicinMethod = rifampicinMethod;
	}

	public Float getRifampicinZoneDiameter() {
		return rifampicinZoneDiameter;
	}

	public void setRifampicinZoneDiameter(Float rifampicinZoneDiameter) {
		this.rifampicinZoneDiameter = rifampicinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getRifampicinSurveillance() {
		return rifampicinSurveillance;
	}

	public void setRifampicinSurveillance(SusceptibilitySurveillanceType rifampicinSurveillance) {
		this.rifampicinSurveillance = rifampicinSurveillance;
	}

	public SusceptibilityMethod getStreptomycinMethod() {
		return streptomycinMethod;
	}

	public void setStreptomycinMethod(SusceptibilityMethod streptomycinMethod) {
		this.streptomycinMethod = streptomycinMethod;
	}

	public Float getStreptomycinZoneDiameter() {
		return streptomycinZoneDiameter;
	}

	public void setStreptomycinZoneDiameter(Float streptomycinZoneDiameter) {
		this.streptomycinZoneDiameter = streptomycinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getStreptomycinSurveillance() {
		return streptomycinSurveillance;
	}

	public void setStreptomycinSurveillance(SusceptibilitySurveillanceType streptomycinSurveillance) {
		this.streptomycinSurveillance = streptomycinSurveillance;
	}

	public SusceptibilityMethod getCeftriaxoneMethod() {
		return ceftriaxoneMethod;
	}

	public void setCeftriaxoneMethod(SusceptibilityMethod ceftriaxoneMethod) {
		this.ceftriaxoneMethod = ceftriaxoneMethod;
	}

	public Float getCeftriaxoneZoneDiameter() {
		return ceftriaxoneZoneDiameter;
	}

	public void setCeftriaxoneZoneDiameter(Float ceftriaxoneZoneDiameter) {
		this.ceftriaxoneZoneDiameter = ceftriaxoneZoneDiameter;
	}

	public SusceptibilitySurveillanceType getCeftriaxoneSurveillance() {
		return ceftriaxoneSurveillance;
	}

	public void setCeftriaxoneSurveillance(SusceptibilitySurveillanceType ceftriaxoneSurveillance) {
		this.ceftriaxoneSurveillance = ceftriaxoneSurveillance;
	}

	public SusceptibilityMethod getPenicillinMethod() {
		return penicillinMethod;
	}

	public void setPenicillinMethod(SusceptibilityMethod penicillinMethod) {
		this.penicillinMethod = penicillinMethod;
	}

	public Float getPenicillinZoneDiameter() {
		return penicillinZoneDiameter;
	}

	public void setPenicillinZoneDiameter(Float penicillinZoneDiameter) {
		this.penicillinZoneDiameter = penicillinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getPenicillinSurveillance() {
		return penicillinSurveillance;
	}

	public void setPenicillinSurveillance(SusceptibilitySurveillanceType penicillinSurveillance) {
		this.penicillinSurveillance = penicillinSurveillance;
	}

	public SusceptibilityMethod getErythromycinMethod() {
		return erythromycinMethod;
	}

	public void setErythromycinMethod(SusceptibilityMethod erythromycinMethod) {
		this.erythromycinMethod = erythromycinMethod;
	}

	public Float getErythromycinZoneDiameter() {
		return erythromycinZoneDiameter;
	}

	public void setErythromycinZoneDiameter(Float erythromycinZoneDiameter) {
		this.erythromycinZoneDiameter = erythromycinZoneDiameter;
	}

	public SusceptibilitySurveillanceType getErythromycinSurveillance() {
		return erythromycinSurveillance;
	}

	public void setErythromycinSurveillance(SusceptibilitySurveillanceType erythromycinSurveillance) {
		this.erythromycinSurveillance = erythromycinSurveillance;
	}

	public Float getAzithromycinMic() {
		return azithromycinMic;
	}

	public void setAzithromycinMic(Float azithromycinMic) {
		this.azithromycinMic = azithromycinMic;
	}

	public DrugSusceptibilityType getAzithromycinSusceptibility() {
		return azithromycinSusceptibility;
	}

	public void setAzithromycinSusceptibility(DrugSusceptibilityType azithromycinSusceptibility) {
		this.azithromycinSusceptibility = azithromycinSusceptibility;
	}

	public Float getCeftazidimeMic() {
		return ceftazidimeMic;
	}

	public void setCeftazidimeMic(Float ceftazidimeMic) {
		this.ceftazidimeMic = ceftazidimeMic;
	}

	public DrugSusceptibilityType getCeftazidimeSusceptibility() {
		return ceftazidimeSusceptibility;
	}

	public void setCeftazidimeSusceptibility(DrugSusceptibilityType ceftazidimeSusceptibility) {
		this.ceftazidimeSusceptibility = ceftazidimeSusceptibility;
	}

	public Float getCefotaximeMic() {
		return cefotaximeMic;
	}

	public void setCefotaximeMic(Float cefotaximeMic) {
		this.cefotaximeMic = cefotaximeMic;
	}

	public DrugSusceptibilityType getCefotaximeSusceptibility() {
		return cefotaximeSusceptibility;
	}

	public void setCefotaximeSusceptibility(DrugSusceptibilityType cefotaximeSusceptibility) {
		this.cefotaximeSusceptibility = cefotaximeSusceptibility;
	}

	public Float getAmpicillinMic() {
		return ampicillinMic;
	}

	public void setAmpicillinMic(Float ampicillinMic) {
		this.ampicillinMic = ampicillinMic;
	}

	public DrugSusceptibilityType getAmpicillinSusceptibility() {
		return ampicillinSusceptibility;
	}

	public void setAmpicillinSusceptibility(DrugSusceptibilityType ampicillinSusceptibility) {
		this.ampicillinSusceptibility = ampicillinSusceptibility;
	}

	public Float getTrimethoprimSulfamethoxazoleMic() {
		return trimethoprimSulfamethoxazoleMic;
	}

	public void setTrimethoprimSulfamethoxazoleMic(Float trimethoprimSulfamethoxazoleMic) {
		this.trimethoprimSulfamethoxazoleMic = trimethoprimSulfamethoxazoleMic;
	}

	public DrugSusceptibilityType getTrimethoprimSulfamethoxazoleSusceptibility() {
		return trimethoprimSulfamethoxazoleSusceptibility;
	}

	public void setTrimethoprimSulfamethoxazoleSusceptibility(DrugSusceptibilityType trimethoprimSulfamethoxazoleSusceptibility) {
		this.trimethoprimSulfamethoxazoleSusceptibility = trimethoprimSulfamethoxazoleSusceptibility;
	}

	public SusceptibilityMethod getAzithromycinMethod() {
		return azithromycinMethod;
	}

	public void setAzithromycinMethod(SusceptibilityMethod azithromycinMethod) {
		this.azithromycinMethod = azithromycinMethod;
	}

	public SusceptibilityMethod getCeftazidimeMethod() {
		return ceftazidimeMethod;
	}

	public void setCeftazidimeMethod(SusceptibilityMethod ceftazidimeMethod) {
		this.ceftazidimeMethod = ceftazidimeMethod;
	}

	public SusceptibilityMethod getCefotaximeMethod() {
		return cefotaximeMethod;
	}

	public void setCefotaximeMethod(SusceptibilityMethod cefotaximeMethod) {
		this.cefotaximeMethod = cefotaximeMethod;
	}

	public SusceptibilityMethod getAmpicillinMethod() {
		return ampicillinMethod;
	}

	public void setAmpicillinMethod(SusceptibilityMethod ampicillinMethod) {
		this.ampicillinMethod = ampicillinMethod;
	}

	public SusceptibilityMethod getTrimethoprimSulfamethoxazoleMethod() {
		return trimethoprimSulfamethoxazoleMethod;
	}

	public void setTrimethoprimSulfamethoxazoleMethod(SusceptibilityMethod trimethoprimSulfamethoxazoleMethod) {
		this.trimethoprimSulfamethoxazoleMethod = trimethoprimSulfamethoxazoleMethod;
	}
}
