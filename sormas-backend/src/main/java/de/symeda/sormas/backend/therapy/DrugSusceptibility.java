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
}
