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
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ciprofloxacinMic;
	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
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
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	private Float ceftriaxoneMic;
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
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
}
