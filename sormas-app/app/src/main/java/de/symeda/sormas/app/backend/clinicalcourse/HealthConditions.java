/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.clinicalcourse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = HealthConditions.TABLE_NAME)
@DatabaseTable(tableName = HealthConditions.TABLE_NAME)
@EmbeddedAdo
public class HealthConditions extends PseudonymizableAdo {

	private static final long serialVersionUID = -6688718889862479948L;

	public static final String TABLE_NAME = "healthConditions";
	public static final String I18N_PREFIX = "HealthConditions";

	@Enumerated(EnumType.STRING)
	private YesNoUnknown tuberculosis;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown asplenia;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown hepatitis;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown diabetes;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown hiv;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown hivArt;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown chronicLiverDisease;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown malignancyChemotherapy;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown chronicHeartFailure;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown chronicPulmonaryDisease;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown chronicKidneyDisease;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown chronicNeurologicCondition;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown downSyndrome;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown congenitalSyphilis;
	@Column(length = 512)
	private String otherConditions;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown immunodeficiencyOtherThanHiv;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown cardiovascularDiseaseIncludingHypertension;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown obesity;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown currentSmoker;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown formerSmoker;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown asthma;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown sickleCellDisease;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown immunodeficiencyIncludingHiv;

	public YesNoUnknown getTuberculosis() {
		return tuberculosis;
	}

	public void setTuberculosis(YesNoUnknown tuberculosis) {
		this.tuberculosis = tuberculosis;
	}

	public YesNoUnknown getAsplenia() {
		return asplenia;
	}

	public void setAsplenia(YesNoUnknown asplenia) {
		this.asplenia = asplenia;
	}

	public YesNoUnknown getHepatitis() {
		return hepatitis;
	}

	public void setHepatitis(YesNoUnknown hepatitis) {
		this.hepatitis = hepatitis;
	}

	public YesNoUnknown getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(YesNoUnknown diabetes) {
		this.diabetes = diabetes;
	}

	public YesNoUnknown getHiv() {
		return hiv;
	}

	public void setHiv(YesNoUnknown hiv) {
		this.hiv = hiv;
	}

	public YesNoUnknown getHivArt() {
		return hivArt;
	}

	public void setHivArt(YesNoUnknown hivArt) {
		this.hivArt = hivArt;
	}

	public YesNoUnknown getChronicLiverDisease() {
		return chronicLiverDisease;
	}

	public void setChronicLiverDisease(YesNoUnknown chronicLiverDisease) {
		this.chronicLiverDisease = chronicLiverDisease;
	}

	public YesNoUnknown getMalignancyChemotherapy() {
		return malignancyChemotherapy;
	}

	public void setMalignancyChemotherapy(YesNoUnknown malignancyChemotherapy) {
		this.malignancyChemotherapy = malignancyChemotherapy;
	}

	public YesNoUnknown getChronicHeartFailure() {
		return chronicHeartFailure;
	}

	public void setChronicHeartFailure(YesNoUnknown chronicHeartFailure) {
		this.chronicHeartFailure = chronicHeartFailure;
	}

	public YesNoUnknown getChronicPulmonaryDisease() {
		return chronicPulmonaryDisease;
	}

	public void setChronicPulmonaryDisease(YesNoUnknown chronicPulmonaryDisease) {
		this.chronicPulmonaryDisease = chronicPulmonaryDisease;
	}

	public YesNoUnknown getChronicKidneyDisease() {
		return chronicKidneyDisease;
	}

	public void setChronicKidneyDisease(YesNoUnknown chronicKidneyDisease) {
		this.chronicKidneyDisease = chronicKidneyDisease;
	}

	public YesNoUnknown getChronicNeurologicCondition() {
		return chronicNeurologicCondition;
	}

	public void setChronicNeurologicCondition(YesNoUnknown chronicNeurologicCondition) {
		this.chronicNeurologicCondition = chronicNeurologicCondition;
	}

	public YesNoUnknown getDownSyndrome() {
		return downSyndrome;
	}

	public void setDownSyndrome(YesNoUnknown downSyndrome) {
		this.downSyndrome = downSyndrome;
	}

	public YesNoUnknown getCongenitalSyphilis() {
		return congenitalSyphilis;
	}

	public void setCongenitalSyphilis(YesNoUnknown congenitalSyphilis) {
		this.congenitalSyphilis = congenitalSyphilis;
	}

	public String getOtherConditions() {
		return otherConditions;
	}

	public void setOtherConditions(String otherConditions) {
		this.otherConditions = otherConditions;
	}

	public YesNoUnknown getImmunodeficiencyOtherThanHiv() {
		return immunodeficiencyOtherThanHiv;
	}

	public void setImmunodeficiencyOtherThanHiv(YesNoUnknown immunodeficiencyOtherThanHiv) {
		this.immunodeficiencyOtherThanHiv = immunodeficiencyOtherThanHiv;
	}

	public YesNoUnknown getCardiovascularDiseaseIncludingHypertension() {
		return cardiovascularDiseaseIncludingHypertension;
	}

	public void setCardiovascularDiseaseIncludingHypertension(YesNoUnknown cardiovascularDiseaseIncludingHypertension) {
		this.cardiovascularDiseaseIncludingHypertension = cardiovascularDiseaseIncludingHypertension;
	}

	public YesNoUnknown getObesity() {
		return obesity;
	}

	public void setObesity(YesNoUnknown obesity) {
		this.obesity = obesity;
	}

	public YesNoUnknown getCurrentSmoker() {
		return currentSmoker;
	}

	public void setCurrentSmoker(YesNoUnknown currentSmoker) {
		this.currentSmoker = currentSmoker;
	}

	public YesNoUnknown getFormerSmoker() {
		return formerSmoker;
	}

	public void setFormerSmoker(YesNoUnknown formerSmoker) {
		this.formerSmoker = formerSmoker;
	}

	public YesNoUnknown getAsthma() {
		return asthma;
	}

	public void setAsthma(YesNoUnknown asthma) {
		this.asthma = asthma;
	}

	public YesNoUnknown getSickleCellDisease() {
		return sickleCellDisease;
	}

	public void setSickleCellDisease(YesNoUnknown sickleCellDisease) {
		this.sickleCellDisease = sickleCellDisease;
	}

	public YesNoUnknown getImmunodeficiencyIncludingHiv() {
		return immunodeficiencyIncludingHiv;
	}

	public void setImmunodeficiencyIncludingHiv(YesNoUnknown immunodeficiencyIncludingHiv) {
		this.immunodeficiencyIncludingHiv = immunodeficiencyIncludingHiv;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
