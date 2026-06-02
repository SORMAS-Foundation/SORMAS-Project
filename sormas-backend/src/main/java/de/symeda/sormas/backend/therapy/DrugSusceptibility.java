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

package de.symeda.sormas.backend.therapy;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.therapy.SusceptibilityMethod;
import de.symeda.sormas.api.therapy.SusceptibilitySurveillanceType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Table(name = "drugsusceptibility")
public class DrugSusceptibility extends AbstractDomainObject {

	private static final long serialVersionUID = 2250769025956939876L;

	public static final String TABLE_NAME = "drugsusceptibility";

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
	public static final String isoniazid_Susceptibility = "isoniazidSusceptibility";
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

	private Float amikacinMic;
	private DrugSusceptibilityType amikacinSusceptibility;
	private Float bedaquilineMic;
	private DrugSusceptibilityType bedaquilineSusceptibility;
	private Float capreomycinMic;
	private DrugSusceptibilityType capreomycinSusceptibility;
	private Float ciprofloxacinMic;
	private DrugSusceptibilityType ciprofloxacinSusceptibility;
	private Float delamanidMic;
	private DrugSusceptibilityType delamanidSusceptibility;
	private Float ethambutolMic;
	private DrugSusceptibilityType ethambutolSusceptibility;
	private Float gatifloxacinMic;
	private DrugSusceptibilityType gatifloxacinSusceptibility;
	private Float isoniazidMic;
	private DrugSusceptibilityType isoniazidSusceptibility;
	private Float kanamycinMic;
	private DrugSusceptibilityType kanamycinSusceptibility;
	private Float levofloxacinMic;
	private DrugSusceptibilityType levofloxacinSusceptibility;
	private Float moxifloxacinMic;
	private DrugSusceptibilityType moxifloxacinSusceptibility;
	private Float ofloxacinMic;
	private DrugSusceptibilityType ofloxacinSusceptibility;
	private Float rifampicinMic;
	private DrugSusceptibilityType rifampicinSusceptibility;
	private Float streptomycinMic;
	private DrugSusceptibilityType streptomycinSusceptibility;
	private Float ceftriaxoneMic;
	private DrugSusceptibilityType ceftriaxoneSusceptibility;
	private Float penicillinMic;
	private DrugSusceptibilityType penicillinSusceptibility;
	private Float erythromycinMic;
	private DrugSusceptibilityType erythromycinSusceptibility;

	private SusceptibilityMethod amikacinMethod;
	private Float amikacinZoneDiameter;
	private SusceptibilitySurveillanceType amikacinSurveillance;
	private SusceptibilityMethod bedaquilineMethod;
	private Float bedaquilineZoneDiameter;
	private SusceptibilitySurveillanceType bedaquilineSurveillance;
	private SusceptibilityMethod capreomycinMethod;
	private Float capreomycinZoneDiameter;
	private SusceptibilitySurveillanceType capreomycinSurveillance;
	private SusceptibilityMethod ciprofloxacinMethod;
	private Float ciprofloxacinZoneDiameter;
	private SusceptibilitySurveillanceType ciprofloxacinSurveillance;
	private SusceptibilityMethod delamanidMethod;
	private Float delamanidZoneDiameter;
	private SusceptibilitySurveillanceType delamanidSurveillance;
	private SusceptibilityMethod ethambutolMethod;
	private Float ethambutolZoneDiameter;
	private SusceptibilitySurveillanceType ethambutolSurveillance;
	private SusceptibilityMethod gatifloxacinMethod;
	private Float gatifloxacinZoneDiameter;
	private SusceptibilitySurveillanceType gatifloxacinSurveillance;
	private SusceptibilityMethod isoniazidMethod;
	private Float isoniazidZoneDiameter;
	private SusceptibilitySurveillanceType isoniazidSurveillance;
	private SusceptibilityMethod kanamycinMethod;
	private Float kanamycinZoneDiameter;
	private SusceptibilitySurveillanceType kanamycinSurveillance;
	private SusceptibilityMethod levofloxacinMethod;
	private Float levofloxacinZoneDiameter;
	private SusceptibilitySurveillanceType levofloxacinSurveillance;
	private SusceptibilityMethod moxifloxacinMethod;
	private Float moxifloxacinZoneDiameter;
	private SusceptibilitySurveillanceType moxifloxacinSurveillance;
	private SusceptibilityMethod ofloxacinMethod;
	private Float ofloxacinZoneDiameter;
	private SusceptibilitySurveillanceType ofloxacinSurveillance;
	private SusceptibilityMethod rifampicinMethod;
	private Float rifampicinZoneDiameter;
	private SusceptibilitySurveillanceType rifampicinSurveillance;
	private SusceptibilityMethod streptomycinMethod;
	private Float streptomycinZoneDiameter;
	private SusceptibilitySurveillanceType streptomycinSurveillance;
	private SusceptibilityMethod ceftriaxoneMethod;
	private Float ceftriaxoneZoneDiameter;
	private SusceptibilitySurveillanceType ceftriaxoneSurveillance;
	private SusceptibilityMethod penicillinMethod;
	private Float penicillinZoneDiameter;
	private SusceptibilitySurveillanceType penicillinSurveillance;
	private SusceptibilityMethod erythromycinMethod;
	private Float erythromycinZoneDiameter;
	private SusceptibilitySurveillanceType erythromycinSurveillance;

	private Float azithromycinMic;
	private DrugSusceptibilityType azithromycinSusceptibility;
	private Float ceftazidimeMic;
	private DrugSusceptibilityType ceftazidimeSusceptibility;
	private Float cefotaximeMic;
	private DrugSusceptibilityType cefotaximeSusceptibility;
	private Float ampicillinMic;
	private DrugSusceptibilityType ampicillinSusceptibility;
	private Float trimethoprimSulfamethoxazoleMic;
	private DrugSusceptibilityType trimethoprimSulfamethoxazoleSusceptibility;

	public Float getAmikacinMic() {
		return amikacinMic;
	}

	public void setAmikacinMic(Float amikacinMic) {
		this.amikacinMic = amikacinMic;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getErythromycinSusceptibility() {
		return erythromycinSusceptibility;
	}

	public void setErythromycinSusceptibility(DrugSusceptibilityType erythromycinSusceptibility) {
		this.erythromycinSusceptibility = erythromycinSusceptibility;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getAmikacinSurveillance() {
		return amikacinSurveillance;
	}

	public void setAmikacinSurveillance(SusceptibilitySurveillanceType amikacinSurveillance) {
		this.amikacinSurveillance = amikacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getBedaquilineSurveillance() {
		return bedaquilineSurveillance;
	}

	public void setBedaquilineSurveillance(SusceptibilitySurveillanceType bedaquilineSurveillance) {
		this.bedaquilineSurveillance = bedaquilineSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getCapreomycinSurveillance() {
		return capreomycinSurveillance;
	}

	public void setCapreomycinSurveillance(SusceptibilitySurveillanceType capreomycinSurveillance) {
		this.capreomycinSurveillance = capreomycinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getCiprofloxacinSurveillance() {
		return ciprofloxacinSurveillance;
	}

	public void setCiprofloxacinSurveillance(SusceptibilitySurveillanceType ciprofloxacinSurveillance) {
		this.ciprofloxacinSurveillance = ciprofloxacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getDelamanidSurveillance() {
		return delamanidSurveillance;
	}

	public void setDelamanidSurveillance(SusceptibilitySurveillanceType delamanidSurveillance) {
		this.delamanidSurveillance = delamanidSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getEthambutolSurveillance() {
		return ethambutolSurveillance;
	}

	public void setEthambutolSurveillance(SusceptibilitySurveillanceType ethambutolSurveillance) {
		this.ethambutolSurveillance = ethambutolSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getGatifloxacinSurveillance() {
		return gatifloxacinSurveillance;
	}

	public void setGatifloxacinSurveillance(SusceptibilitySurveillanceType gatifloxacinSurveillance) {
		this.gatifloxacinSurveillance = gatifloxacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getIsoniazidSurveillance() {
		return isoniazidSurveillance;
	}

	public void setIsoniazidSurveillance(SusceptibilitySurveillanceType isoniazidSurveillance) {
		this.isoniazidSurveillance = isoniazidSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getKanamycinSurveillance() {
		return kanamycinSurveillance;
	}

	public void setKanamycinSurveillance(SusceptibilitySurveillanceType kanamycinSurveillance) {
		this.kanamycinSurveillance = kanamycinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getLevofloxacinSurveillance() {
		return levofloxacinSurveillance;
	}

	public void setLevofloxacinSurveillance(SusceptibilitySurveillanceType levofloxacinSurveillance) {
		this.levofloxacinSurveillance = levofloxacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getMoxifloxacinSurveillance() {
		return moxifloxacinSurveillance;
	}

	public void setMoxifloxacinSurveillance(SusceptibilitySurveillanceType moxifloxacinSurveillance) {
		this.moxifloxacinSurveillance = moxifloxacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getOfloxacinSurveillance() {
		return ofloxacinSurveillance;
	}

	public void setOfloxacinSurveillance(SusceptibilitySurveillanceType ofloxacinSurveillance) {
		this.ofloxacinSurveillance = ofloxacinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getRifampicinSurveillance() {
		return rifampicinSurveillance;
	}

	public void setRifampicinSurveillance(SusceptibilitySurveillanceType rifampicinSurveillance) {
		this.rifampicinSurveillance = rifampicinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getStreptomycinSurveillance() {
		return streptomycinSurveillance;
	}

	public void setStreptomycinSurveillance(SusceptibilitySurveillanceType streptomycinSurveillance) {
		this.streptomycinSurveillance = streptomycinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getCeftriaxoneSurveillance() {
		return ceftriaxoneSurveillance;
	}

	public void setCeftriaxoneSurveillance(SusceptibilitySurveillanceType ceftriaxoneSurveillance) {
		this.ceftriaxoneSurveillance = ceftriaxoneSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public SusceptibilitySurveillanceType getPenicillinSurveillance() {
		return penicillinSurveillance;
	}

	public void setPenicillinSurveillance(SusceptibilitySurveillanceType penicillinSurveillance) {
		this.penicillinSurveillance = penicillinSurveillance;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getTrimethoprimSulfamethoxazoleSusceptibility() {
		return trimethoprimSulfamethoxazoleSusceptibility;
	}

	public void setTrimethoprimSulfamethoxazoleSusceptibility(DrugSusceptibilityType trimethoprimSulfamethoxazoleSusceptibility) {
		this.trimethoprimSulfamethoxazoleSusceptibility = trimethoprimSulfamethoxazoleSusceptibility;
	}

}
