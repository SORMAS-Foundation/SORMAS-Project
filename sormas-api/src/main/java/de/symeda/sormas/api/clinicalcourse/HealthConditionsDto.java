package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class HealthConditionsDto extends EntityDto {
	
	private static final long serialVersionUID = -6688718889862479948L;

	public static final String I18N_PREFIX = "HealthConditions";
	
	public static final String TUBERCULOSIS = "tuberculosis";
	public static final String ASPLENIA = "asplenia";
	public static final String HEPATITIS = "hepatitis";
	public static final String DIABETES = "diabetes";
	public static final String HIV = "hiv";
	public static final String HIV_ART = "hivArt";
	public static final String CHRONIC_LIVER_DISEASE = "chronicLiverDisease";
	public static final String MALIGNANCY_CHEMOTHERAPY = "malignancyChemotherapy";
	public static final String CHRONIC_HEART_FAILURE = "chronicHeartFailure";
	public static final String CHRONIC_PULMONARY_DISEASE = "chronicPulmonaryDisease";
	public static final String CHRONIC_KIDNEY_DISEASE = "chronicKidneyDisease";
	public static final String CHRONIC_NEUROLOGIC_CONDITION = "chronicNeurologicCondition";
	public static final String DOWN_SYNDROME = "downSyndrome";
	public static final String CONGENITAL_SYPHILIS = "congenitalSyphilis";
	public static final String OTHER_CONDITIONS = "otherConditions";
	
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
	private String otherConditions;
	
	public static HealthConditionsDto build() {
		HealthConditionsDto healthConditions = new HealthConditionsDto();
		healthConditions.setUuid(DataHelper.createUuid());
		return healthConditions;
	}
	
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
	
}
