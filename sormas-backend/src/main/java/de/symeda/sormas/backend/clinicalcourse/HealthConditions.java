package de.symeda.sormas.backend.clinicalcourse;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
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
	private String immunodeficiencyOtherThanHivText;
	private YesNoUnknown cardiovascularDiseaseIncludingHypertension;
	private YesNoUnknown obesity;
	private YesNoUnknown currentSmoker;
	private YesNoUnknown formerSmoker;
	private YesNoUnknown asthma;
	private YesNoUnknown sickleCellDisease;
	private YesNoUnknown immunodeficiencyIncludingHiv;
	private String otherConditions;
	private YesNoUnknown exposedToMosquitoBorneViruses;
	private String exposedToMosquitoBorneVirusesText;
	private YesNoUnknown vaccinatedAgainstMosquitoBorneViruses;
	private YesNoUnknown malaria;
	private Integer malariaInfectedYear;

	private Integer tuberculosisInfectionYear;
	private YesNoUnknown previousTuberculosisTreatment;
	private ComplianceWithTreatment complianceWithTreatment;

	private YesNoUnknown recurrentBronchiolitis;

	private YesNoUnknown onMedication;
	private String medicationDetails;
	private YesNoUnknown chronicDisease;
	private String chronicDiseaseDetails;

	private HivStatus hivStatus;
	private YesNoUnknown mentalHealthDisorder;
	private YesNoUnknown substanceUseDisorder;
	private String substanceUseDisorderDetails;
	private YesNoUnknown stiProphylaxis;
	private YesNoUnknown hivPrep;
	private YesNoUnknown treatedForSyphilis;
	private String syphilisDrugUsed;
	private Integer syphilisNumberOfDoses;
	private Date syphilisDateOfFirstDose;
	private YesNoUnknown syphilisOrOtherStis;

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

	@Column(length = CHARACTER_LIMIT_BIG)
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

	public Integer getTuberculosisInfectionYear() {
		return tuberculosisInfectionYear;
	}

	public void setTuberculosisInfectionYear(Integer tuberculosisInfectionYear) {
		this.tuberculosisInfectionYear = tuberculosisInfectionYear;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPreviousTuberculosisTreatment() {
		return previousTuberculosisTreatment;
	}

	public void setPreviousTuberculosisTreatment(YesNoUnknown previousTuberculosisTreatment) {
		this.previousTuberculosisTreatment = previousTuberculosisTreatment;
	}

	@Enumerated(EnumType.STRING)
	public ComplianceWithTreatment getComplianceWithTreatment() {
		return complianceWithTreatment;
	}

	public void setComplianceWithTreatment(ComplianceWithTreatment complianceWithTreatment) {
		this.complianceWithTreatment = complianceWithTreatment;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRecurrentBronchiolitis() {
		return recurrentBronchiolitis;
	}

	public void setRecurrentBronchiolitis(YesNoUnknown recurrentBronchiolitis) {
		this.recurrentBronchiolitis = recurrentBronchiolitis;
	}

	public String getImmunodeficiencyOtherThanHivText() {
		return immunodeficiencyOtherThanHivText;
	}

	public void setImmunodeficiencyOtherThanHivText(String immunodeficiencyOtherThanHivText) {
		this.immunodeficiencyOtherThanHivText = immunodeficiencyOtherThanHivText;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getExposedToMosquitoBorneViruses() {
		return exposedToMosquitoBorneViruses;
	}

	public void setExposedToMosquitoBorneViruses(YesNoUnknown exposedToMosquitoBorneViruses) {
		this.exposedToMosquitoBorneViruses = exposedToMosquitoBorneViruses;
	}

	public String getExposedToMosquitoBorneVirusesText() {
		return exposedToMosquitoBorneVirusesText;
	}

	public void setExposedToMosquitoBorneVirusesText(String exposedToMosquitoBorneVirusesText) {
		this.exposedToMosquitoBorneVirusesText = exposedToMosquitoBorneVirusesText;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccinatedAgainstMosquitoBorneViruses() {
		return vaccinatedAgainstMosquitoBorneViruses;
	}

	public void setVaccinatedAgainstMosquitoBorneViruses(YesNoUnknown vaccinatedAgainstMosquitoBorneViruses) {
		this.vaccinatedAgainstMosquitoBorneViruses = vaccinatedAgainstMosquitoBorneViruses;
	}

	public Integer getMalariaInfectedYear() {
		return malariaInfectedYear;
	}

	public void setMalariaInfectedYear(Integer malariaInfectedYear) {
		this.malariaInfectedYear = malariaInfectedYear;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getMalaria() {
		return malaria;
	}

	public void setMalaria(YesNoUnknown malaria) {
		this.malaria = malaria;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getOnMedication() {
		return onMedication;
	}

	public void setOnMedication(YesNoUnknown onMedication) {
		this.onMedication = onMedication;
	}

	public String getMedicationDetails() {
		return medicationDetails;
	}

	public void setMedicationDetails(String medicationDetails) {
		this.medicationDetails = medicationDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getChronicDisease() {
		return chronicDisease;
	}

	public void setChronicDisease(YesNoUnknown chronicDisease) {
		this.chronicDisease = chronicDisease;
	}

	public String getChronicDiseaseDetails() {
		return chronicDiseaseDetails;
	}

	public void setChronicDiseaseDetails(String chronicDiseaseDetails) {
		this.chronicDiseaseDetails = chronicDiseaseDetails;
	}

	@Enumerated(EnumType.STRING)
	public HivStatus getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(HivStatus hivStatus) {
		this.hivStatus = hivStatus;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getMentalHealthDisorder() {
		return mentalHealthDisorder;
	}

	public void setMentalHealthDisorder(YesNoUnknown mentalHealthDisorder) {
		this.mentalHealthDisorder = mentalHealthDisorder;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSubstanceUseDisorder() {
		return substanceUseDisorder;
	}

	public void setSubstanceUseDisorder(YesNoUnknown substanceUseDisorder) {
		this.substanceUseDisorder = substanceUseDisorder;
	}

	public String getSubstanceUseDisorderDetails() {
		return substanceUseDisorderDetails;
	}

	public void setSubstanceUseDisorderDetails(String substanceUseDisorderDetails) {
		this.substanceUseDisorderDetails = substanceUseDisorderDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getStiProphylaxis() {
		return stiProphylaxis;
	}

	public void setStiProphylaxis(YesNoUnknown stiProphylaxis) {
		this.stiProphylaxis = stiProphylaxis;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHivPrep() {
		return hivPrep;
	}

	public void setHivPrep(YesNoUnknown hivPrep) {
		this.hivPrep = hivPrep;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTreatedForSyphilis() {
		return treatedForSyphilis;
	}

	public void setTreatedForSyphilis(YesNoUnknown treatedForSyphilis) {
		this.treatedForSyphilis = treatedForSyphilis;
	}

	public String getSyphilisDrugUsed() {
		return syphilisDrugUsed;
	}

	public void setSyphilisDrugUsed(String syphilisDrugUsed) {
		this.syphilisDrugUsed = syphilisDrugUsed;
	}

	public Integer getSyphilisNumberOfDoses() {
		return syphilisNumberOfDoses;
	}

	public void setSyphilisNumberOfDoses(Integer syphilisNumberOfDoses) {
		this.syphilisNumberOfDoses = syphilisNumberOfDoses;
	}

	public Date getSyphilisDateOfFirstDose() {
		return syphilisDateOfFirstDose;
	}

	public void setSyphilisDateOfFirstDose(Date syphilisDateOfFirstDose) {
		this.syphilisDateOfFirstDose = syphilisDateOfFirstDose;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSyphilisOrOtherStis() {
		return syphilisOrOtherStis;
	}

	public void setSyphilisOrOtherStis(YesNoUnknown syphilisOrOtherStis) {
		this.syphilisOrOtherStis = syphilisOrOtherStis;
	}

}
