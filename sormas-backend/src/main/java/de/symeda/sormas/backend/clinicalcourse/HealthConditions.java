package de.symeda.sormas.backend.clinicalcourse;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class HealthConditions extends AbstractDomainObject {

	private static final long serialVersionUID = -6688718889862479948L;

	public static final String TABLE_NAME = "healthconditions";

	private YesNoUnknown tuberculosis;
	private YesNoUnknown asplenia;
	private YesNoUnknown hepatitis;
	private YesNoUnknown diabetes;
	private YesNoUnknown hiv;
	private YesNoUnknown hivArt;
	private YesNoUnknown chronicLiverDisease;
	private YesNoUnknown malignancyChemotherapy;
	private YesNoUnknown chronicHeartFailure;
	private YesNoUnknown chronicPulmonaryDisease;
	private YesNoUnknown chronicKidneyDisease;
	private YesNoUnknown chronicNeurologicCondition;
	private YesNoUnknown downSyndrome;
	private YesNoUnknown congenitalSyphilis;
	private YesNoUnknown immunodeficiencyOtherThanHiv;
	private YesNoUnknown cardiovascularDiseaseIncludingHypertension;
	private YesNoUnknown obesity;
	private YesNoUnknown currentSmoker;
	private YesNoUnknown formerSmoker;
	private YesNoUnknown asthma;
	private YesNoUnknown sickleCellDisease;
	private YesNoUnknown immunodeficiencyIncludingHiv;
	private String otherConditions;

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTuberculosis() {
		return tuberculosis;
	}

	public void setTuberculosis(YesNoUnknown tuberculosis) {
		this.tuberculosis = tuberculosis;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAsplenia() {
		return asplenia;
	}

	public void setAsplenia(YesNoUnknown asplenia) {
		this.asplenia = asplenia;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHepatitis() {
		return hepatitis;
	}

	public void setHepatitis(YesNoUnknown hepatitis) {
		this.hepatitis = hepatitis;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(YesNoUnknown diabetes) {
		this.diabetes = diabetes;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHiv() {
		return hiv;
	}

	public void setHiv(YesNoUnknown hiv) {
		this.hiv = hiv;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHivArt() {
		return hivArt;
	}

	public void setHivArt(YesNoUnknown hivArt) {
		this.hivArt = hivArt;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicLiverDisease() {
		return chronicLiverDisease;
	}

	public void setChronicLiverDisease(YesNoUnknown chronicLiverDisease) {
		this.chronicLiverDisease = chronicLiverDisease;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getMalignancyChemotherapy() {
		return malignancyChemotherapy;
	}

	public void setMalignancyChemotherapy(YesNoUnknown malignancyChemotherapy) {
		this.malignancyChemotherapy = malignancyChemotherapy;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicHeartFailure() {
		return chronicHeartFailure;
	}

	public void setChronicHeartFailure(YesNoUnknown chronicHeartFailure) {
		this.chronicHeartFailure = chronicHeartFailure;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicPulmonaryDisease() {
		return chronicPulmonaryDisease;
	}

	public void setChronicPulmonaryDisease(YesNoUnknown chronicPulmonaryDisease) {
		this.chronicPulmonaryDisease = chronicPulmonaryDisease;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicKidneyDisease() {
		return chronicKidneyDisease;
	}

	public void setChronicKidneyDisease(YesNoUnknown chronicKidneyDisease) {
		this.chronicKidneyDisease = chronicKidneyDisease;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicNeurologicCondition() {
		return chronicNeurologicCondition;
	}

	public void setChronicNeurologicCondition(YesNoUnknown chronicNeurologicCondition) {
		this.chronicNeurologicCondition = chronicNeurologicCondition;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getDownSyndrome() {
		return downSyndrome;
	}

	public void setDownSyndrome(YesNoUnknown downSyndrome) {
		this.downSyndrome = downSyndrome;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCongenitalSyphilis() {
		return congenitalSyphilis;
	}

	public void setCongenitalSyphilis(YesNoUnknown congenitalSyphilis) {
		this.congenitalSyphilis = congenitalSyphilis;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getOtherConditions() {
		return otherConditions;
	}

	public void setOtherConditions(String otherConditions) {
		this.otherConditions = otherConditions;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getImmunodeficiencyOtherThanHiv() {
		return immunodeficiencyOtherThanHiv;
	}

	public void setImmunodeficiencyOtherThanHiv(YesNoUnknown immunodeficiencyOtherThanHiv) {
		this.immunodeficiencyOtherThanHiv = immunodeficiencyOtherThanHiv;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCardiovascularDiseaseIncludingHypertension() {
		return cardiovascularDiseaseIncludingHypertension;
	}

	public void setCardiovascularDiseaseIncludingHypertension(YesNoUnknown cardiovascularDiseaseIncludingHypertension) {
		this.cardiovascularDiseaseIncludingHypertension = cardiovascularDiseaseIncludingHypertension;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getObesity() {
		return obesity;
	}

	public void setObesity(YesNoUnknown obesity) {
		this.obesity = obesity;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCurrentSmoker() {
		return currentSmoker;
	}

	public void setCurrentSmoker(YesNoUnknown currentSmoker) {
		this.currentSmoker = currentSmoker;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getFormerSmoker() {
		return formerSmoker;
	}

	public void setFormerSmoker(YesNoUnknown formerSmoker) {
		this.formerSmoker = formerSmoker;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAsthma() {
		return asthma;
	}

	public void setAsthma(YesNoUnknown asthma) {
		this.asthma = asthma;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSickleCellDisease() {
		return sickleCellDisease;
	}

	public void setSickleCellDisease(YesNoUnknown sickleCellDisease) {
		this.sickleCellDisease = sickleCellDisease;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getImmunodeficiencyIncludingHiv() {
		return immunodeficiencyIncludingHiv;
	}

	public void setImmunodeficiencyIncludingHiv(YesNoUnknown immunodeficiencyIncludingHiv) {
		this.immunodeficiencyIncludingHiv = immunodeficiencyIncludingHiv;
	}

}
