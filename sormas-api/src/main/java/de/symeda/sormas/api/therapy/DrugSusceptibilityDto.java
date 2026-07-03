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
	public static final String BEDAQUILINE_METHOD = "bedaquilineMethod";
	public static final String CAPREOMYCIN_METHOD = "capreomycinMethod";
	public static final String CIPROFLOXACIN_METHOD = "ciprofloxacinMethod";
	public static final String DELAMANID_METHOD = "delamanidMethod";
	public static final String ETHAMBUTOL_METHOD = "ethambutolMethod";
	public static final String GATIFLOXACIN_METHOD = "gatifloxacinMethod";
	public static final String ISONIAZID_METHOD = "isoniazidMethod";
	public static final String KANAMYCIN_METHOD = "kanamycinMethod";
	public static final String LEVOFLOXACIN_METHOD = "levofloxacinMethod";
	public static final String MOXIFLOXACIN_METHOD = "moxifloxacinMethod";
	public static final String OFLOXACIN_METHOD = "ofloxacinMethod";
	public static final String RIFAMPICIN_METHOD = "rifampicinMethod";
	public static final String STREPTOMYCIN_METHOD = "streptomycinMethod";
	public static final String CEFTRIAXONE_METHOD = "ceftriaxoneMethod";
	public static final String PENICILLIN_METHOD = "penicillinMethod";
	public static final String ERYTHROMYCIN_METHOD = "erythromycinMethod";

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
	private String amikacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType amikacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String bedaquilineMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType bedaquilineSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String capreomycinMic;
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
	private String ciprofloxacinMic;
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
	private String delamanidMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType delamanidSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String ethambutolMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType ethambutolSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String gatifloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType gatifloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String isoniazidMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType isoniazidSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String kanamycinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType kanamycinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String levofloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType levofloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String moxifloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private DrugSusceptibilityType moxifloxacinSusceptibility;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String ofloxacinMic;
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
	private String rifampicinMic;
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
	private String streptomycinMic;
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
	private String ceftriaxoneMic;
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
	private String penicillinMic;
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
	private String erythromycinMic;
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
	private SusceptibilityMethod bedaquilineMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod capreomycinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ciprofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod delamanidMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ethambutolMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod gatifloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod isoniazidMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod kanamycinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod levofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod moxifloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod ofloxacinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod rifampicinMethod;
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod streptomycinMethod;
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
	private SusceptibilityMethod penicillinMethod;
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private SusceptibilityMethod erythromycinMethod;

	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private String azithromycinMic;
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
	private String ceftazidimeMic;
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
	private String cefotaximeMic;
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
	private String ampicillinMic;
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
	private String trimethoprimSulfamethoxazoleMic;
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

	public String getAmikacinMic() {
		return amikacinMic;
	}

	public void setAmikacinMic(String amikacinMic) {
		this.amikacinMic = amikacinMic;
	}

	public DrugSusceptibilityType getAmikacinSusceptibility() {
		return amikacinSusceptibility;
	}

	public void setAmikacinSusceptibility(DrugSusceptibilityType amikacinSusceptibility) {
		this.amikacinSusceptibility = amikacinSusceptibility;
	}

	public String getBedaquilineMic() {
		return bedaquilineMic;
	}

	public void setBedaquilineMic(String bedaquilineMic) {
		this.bedaquilineMic = bedaquilineMic;
	}

	public DrugSusceptibilityType getBedaquilineSusceptibility() {
		return bedaquilineSusceptibility;
	}

	public void setBedaquilineSusceptibility(DrugSusceptibilityType bedaquilineSusceptibility) {
		this.bedaquilineSusceptibility = bedaquilineSusceptibility;
	}

	public String getCapreomycinMic() {
		return capreomycinMic;
	}

	public void setCapreomycinMic(String capreomycinMic) {
		this.capreomycinMic = capreomycinMic;
	}

	public DrugSusceptibilityType getCapreomycinSusceptibility() {
		return capreomycinSusceptibility;
	}

	public void setCapreomycinSusceptibility(DrugSusceptibilityType capreomycinSusceptibility) {
		this.capreomycinSusceptibility = capreomycinSusceptibility;
	}

	public String getCiprofloxacinMic() {
		return ciprofloxacinMic;
	}

	public void setCiprofloxacinMic(String ciprofloxacinMic) {
		this.ciprofloxacinMic = ciprofloxacinMic;
	}

	public DrugSusceptibilityType getCiprofloxacinSusceptibility() {
		return ciprofloxacinSusceptibility;
	}

	public void setCiprofloxacinSusceptibility(DrugSusceptibilityType ciprofloxacinSusceptibility) {
		this.ciprofloxacinSusceptibility = ciprofloxacinSusceptibility;
	}

	public String getDelamanidMic() {
		return delamanidMic;
	}

	public void setDelamanidMic(String delamanidMic) {
		this.delamanidMic = delamanidMic;
	}

	public DrugSusceptibilityType getDelamanidSusceptibility() {
		return delamanidSusceptibility;
	}

	public void setDelamanidSusceptibility(DrugSusceptibilityType delamanidSusceptibility) {
		this.delamanidSusceptibility = delamanidSusceptibility;
	}

	public String getEthambutolMic() {
		return ethambutolMic;
	}

	public void setEthambutolMic(String ethambutolMic) {
		this.ethambutolMic = ethambutolMic;
	}

	public DrugSusceptibilityType getEthambutolSusceptibility() {
		return ethambutolSusceptibility;
	}

	public void setEthambutolSusceptibility(DrugSusceptibilityType ethambutolSusceptibility) {
		this.ethambutolSusceptibility = ethambutolSusceptibility;
	}

	public String getGatifloxacinMic() {
		return gatifloxacinMic;
	}

	public void setGatifloxacinMic(String gatifloxacinMic) {
		this.gatifloxacinMic = gatifloxacinMic;
	}

	public DrugSusceptibilityType getGatifloxacinSusceptibility() {
		return gatifloxacinSusceptibility;
	}

	public void setGatifloxacinSusceptibility(DrugSusceptibilityType gatifloxacinSusceptibility) {
		this.gatifloxacinSusceptibility = gatifloxacinSusceptibility;
	}

	public String getIsoniazidMic() {
		return isoniazidMic;
	}

	public void setIsoniazidMic(String isoniazidMic) {
		this.isoniazidMic = isoniazidMic;
	}

	public DrugSusceptibilityType getIsoniazidSusceptibility() {
		return isoniazidSusceptibility;
	}

	public void setIsoniazidSusceptibility(DrugSusceptibilityType isoniazidSusceptibility) {
		this.isoniazidSusceptibility = isoniazidSusceptibility;
	}

	public String getKanamycinMic() {
		return kanamycinMic;
	}

	public void setKanamycinMic(String kanamycinMic) {
		this.kanamycinMic = kanamycinMic;
	}

	public DrugSusceptibilityType getKanamycinSusceptibility() {
		return kanamycinSusceptibility;
	}

	public void setKanamycinSusceptibility(DrugSusceptibilityType kanamycinSusceptibility) {
		this.kanamycinSusceptibility = kanamycinSusceptibility;
	}

	public String getLevofloxacinMic() {
		return levofloxacinMic;
	}

	public void setLevofloxacinMic(String levofloxacinMic) {
		this.levofloxacinMic = levofloxacinMic;
	}

	public DrugSusceptibilityType getLevofloxacinSusceptibility() {
		return levofloxacinSusceptibility;
	}

	public void setLevofloxacinSusceptibility(DrugSusceptibilityType levofloxacinSusceptibility) {
		this.levofloxacinSusceptibility = levofloxacinSusceptibility;
	}

	public String getMoxifloxacinMic() {
		return moxifloxacinMic;
	}

	public void setMoxifloxacinMic(String moxifloxacinMic) {
		this.moxifloxacinMic = moxifloxacinMic;
	}

	public DrugSusceptibilityType getMoxifloxacinSusceptibility() {
		return moxifloxacinSusceptibility;
	}

	public void setMoxifloxacinSusceptibility(DrugSusceptibilityType moxifloxacinSusceptibility) {
		this.moxifloxacinSusceptibility = moxifloxacinSusceptibility;
	}

	public String getOfloxacinMic() {
		return ofloxacinMic;
	}

	public void setOfloxacinMic(String ofloxacinMic) {
		this.ofloxacinMic = ofloxacinMic;
	}

	public DrugSusceptibilityType getOfloxacinSusceptibility() {
		return ofloxacinSusceptibility;
	}

	public void setOfloxacinSusceptibility(DrugSusceptibilityType ofloxacinSusceptibility) {
		this.ofloxacinSusceptibility = ofloxacinSusceptibility;
	}

	public String getRifampicinMic() {
		return rifampicinMic;
	}

	public void setRifampicinMic(String rifampicinMic) {
		this.rifampicinMic = rifampicinMic;
	}

	public DrugSusceptibilityType getRifampicinSusceptibility() {
		return rifampicinSusceptibility;
	}

	public void setRifampicinSusceptibility(DrugSusceptibilityType rifampicinSusceptibility) {
		this.rifampicinSusceptibility = rifampicinSusceptibility;
	}

	public String getStreptomycinMic() {
		return streptomycinMic;
	}

	public void setStreptomycinMic(String streptomycinMic) {
		this.streptomycinMic = streptomycinMic;
	}

	public DrugSusceptibilityType getStreptomycinSusceptibility() {
		return streptomycinSusceptibility;
	}

	public void setStreptomycinSusceptibility(DrugSusceptibilityType streptomycinSusceptibility) {
		this.streptomycinSusceptibility = streptomycinSusceptibility;
	}

	public String getCeftriaxoneMic() {
		return ceftriaxoneMic;
	}

	public void setCeftriaxoneMic(String ceftriaxoneMic) {
		this.ceftriaxoneMic = ceftriaxoneMic;
	}

	public DrugSusceptibilityType getCeftriaxoneSusceptibility() {
		return ceftriaxoneSusceptibility;
	}

	public void setCeftriaxoneSusceptibility(DrugSusceptibilityType ceftriaxoneSusceptibility) {
		this.ceftriaxoneSusceptibility = ceftriaxoneSusceptibility;
	}

	public String getPenicillinMic() {
		return penicillinMic;
	}

	public void setPenicillinMic(String penicillinMic) {
		this.penicillinMic = penicillinMic;
	}

	public DrugSusceptibilityType getPenicillinSusceptibility() {
		return penicillinSusceptibility;
	}

	public void setPenicillinSusceptibility(DrugSusceptibilityType penicillinSusceptibility) {
		this.penicillinSusceptibility = penicillinSusceptibility;
	}

	public String getErythromycinMic() {
		return erythromycinMic;
	}

	public void setErythromycinMic(String erythromycinMic) {
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

	public SusceptibilityMethod getBedaquilineMethod() {
		return bedaquilineMethod;
	}

	public void setBedaquilineMethod(SusceptibilityMethod bedaquilineMethod) {
		this.bedaquilineMethod = bedaquilineMethod;
	}

	public SusceptibilityMethod getCapreomycinMethod() {
		return capreomycinMethod;
	}

	public void setCapreomycinMethod(SusceptibilityMethod capreomycinMethod) {
		this.capreomycinMethod = capreomycinMethod;
	}

	public SusceptibilityMethod getCiprofloxacinMethod() {
		return ciprofloxacinMethod;
	}

	public void setCiprofloxacinMethod(SusceptibilityMethod ciprofloxacinMethod) {
		this.ciprofloxacinMethod = ciprofloxacinMethod;
	}

	public SusceptibilityMethod getDelamanidMethod() {
		return delamanidMethod;
	}

	public void setDelamanidMethod(SusceptibilityMethod delamanidMethod) {
		this.delamanidMethod = delamanidMethod;
	}

	public SusceptibilityMethod getEthambutolMethod() {
		return ethambutolMethod;
	}

	public void setEthambutolMethod(SusceptibilityMethod ethambutolMethod) {
		this.ethambutolMethod = ethambutolMethod;
	}

	public SusceptibilityMethod getGatifloxacinMethod() {
		return gatifloxacinMethod;
	}

	public void setGatifloxacinMethod(SusceptibilityMethod gatifloxacinMethod) {
		this.gatifloxacinMethod = gatifloxacinMethod;
	}

	public SusceptibilityMethod getIsoniazidMethod() {
		return isoniazidMethod;
	}

	public void setIsoniazidMethod(SusceptibilityMethod isoniazidMethod) {
		this.isoniazidMethod = isoniazidMethod;
	}

	public SusceptibilityMethod getKanamycinMethod() {
		return kanamycinMethod;
	}

	public void setKanamycinMethod(SusceptibilityMethod kanamycinMethod) {
		this.kanamycinMethod = kanamycinMethod;
	}

	public SusceptibilityMethod getLevofloxacinMethod() {
		return levofloxacinMethod;
	}

	public void setLevofloxacinMethod(SusceptibilityMethod levofloxacinMethod) {
		this.levofloxacinMethod = levofloxacinMethod;
	}

	public SusceptibilityMethod getMoxifloxacinMethod() {
		return moxifloxacinMethod;
	}

	public void setMoxifloxacinMethod(SusceptibilityMethod moxifloxacinMethod) {
		this.moxifloxacinMethod = moxifloxacinMethod;
	}

	public SusceptibilityMethod getOfloxacinMethod() {
		return ofloxacinMethod;
	}

	public void setOfloxacinMethod(SusceptibilityMethod ofloxacinMethod) {
		this.ofloxacinMethod = ofloxacinMethod;
	}

	public SusceptibilityMethod getRifampicinMethod() {
		return rifampicinMethod;
	}

	public void setRifampicinMethod(SusceptibilityMethod rifampicinMethod) {
		this.rifampicinMethod = rifampicinMethod;
	}

	public SusceptibilityMethod getStreptomycinMethod() {
		return streptomycinMethod;
	}

	public void setStreptomycinMethod(SusceptibilityMethod streptomycinMethod) {
		this.streptomycinMethod = streptomycinMethod;
	}

	public SusceptibilityMethod getCeftriaxoneMethod() {
		return ceftriaxoneMethod;
	}

	public void setCeftriaxoneMethod(SusceptibilityMethod ceftriaxoneMethod) {
		this.ceftriaxoneMethod = ceftriaxoneMethod;
	}

	public SusceptibilityMethod getPenicillinMethod() {
		return penicillinMethod;
	}

	public void setPenicillinMethod(SusceptibilityMethod penicillinMethod) {
		this.penicillinMethod = penicillinMethod;
	}

	public SusceptibilityMethod getErythromycinMethod() {
		return erythromycinMethod;
	}

	public void setErythromycinMethod(SusceptibilityMethod erythromycinMethod) {
		this.erythromycinMethod = erythromycinMethod;
	}

	public String getAzithromycinMic() {
		return azithromycinMic;
	}

	public void setAzithromycinMic(String azithromycinMic) {
		this.azithromycinMic = azithromycinMic;
	}

	public DrugSusceptibilityType getAzithromycinSusceptibility() {
		return azithromycinSusceptibility;
	}

	public void setAzithromycinSusceptibility(DrugSusceptibilityType azithromycinSusceptibility) {
		this.azithromycinSusceptibility = azithromycinSusceptibility;
	}

	public String getCeftazidimeMic() {
		return ceftazidimeMic;
	}

	public void setCeftazidimeMic(String ceftazidimeMic) {
		this.ceftazidimeMic = ceftazidimeMic;
	}

	public DrugSusceptibilityType getCeftazidimeSusceptibility() {
		return ceftazidimeSusceptibility;
	}

	public void setCeftazidimeSusceptibility(DrugSusceptibilityType ceftazidimeSusceptibility) {
		this.ceftazidimeSusceptibility = ceftazidimeSusceptibility;
	}

	public String getCefotaximeMic() {
		return cefotaximeMic;
	}

	public void setCefotaximeMic(String cefotaximeMic) {
		this.cefotaximeMic = cefotaximeMic;
	}

	public DrugSusceptibilityType getCefotaximeSusceptibility() {
		return cefotaximeSusceptibility;
	}

	public void setCefotaximeSusceptibility(DrugSusceptibilityType cefotaximeSusceptibility) {
		this.cefotaximeSusceptibility = cefotaximeSusceptibility;
	}

	public String getAmpicillinMic() {
		return ampicillinMic;
	}

	public void setAmpicillinMic(String ampicillinMic) {
		this.ampicillinMic = ampicillinMic;
	}

	public DrugSusceptibilityType getAmpicillinSusceptibility() {
		return ampicillinSusceptibility;
	}

	public void setAmpicillinSusceptibility(DrugSusceptibilityType ampicillinSusceptibility) {
		this.ampicillinSusceptibility = ampicillinSusceptibility;
	}

	public String getTrimethoprimSulfamethoxazoleMic() {
		return trimethoprimSulfamethoxazoleMic;
	}

	public void setTrimethoprimSulfamethoxazoleMic(String trimethoprimSulfamethoxazoleMic) {
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
