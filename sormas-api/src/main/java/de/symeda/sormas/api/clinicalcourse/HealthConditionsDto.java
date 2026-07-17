package de.symeda.sormas.api.clinicalcourse;

import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING,
	FeatureType.IMMUNIZATION_MANAGEMENT })
public class HealthConditionsDto extends PseudonymizableDto {

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
	public static final String IMMUNODEFICIENCY_OTHER_THAN_HIV = "immunodeficiencyOtherThanHiv";
	public static final String CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION = "cardiovascularDiseaseIncludingHypertension";
	public static final String OBESITY = "obesity";
	public static final String CURRENT_SMOKER = "currentSmoker";
	public static final String FORMER_SMOKER = "formerSmoker";
	public static final String ASTHMA = "asthma";
	public static final String SICKLE_CELL_DISEASE = "sickleCellDisease";
	public static final String IMMUNODEFICIENCY_INCLUDING_HIV = "immunodeficiencyIncludingHiv";
	public static final String TUBERCULOSIS_INFECTION_YEAR = "tuberculosisInfectionYear";
	public static final String PREVIOUS_TUBERCULOSIS_TREATMENT = "previousTuberculosisTreatment";
	public static final String COMPLIANCE_WITH_TREATMENT = "complianceWithTreatment";
	public static final String RECURRENT_BRONCHIOLITIS = "recurrentBronchiolitis";
	public static final String IMMUNODEFICIENCY_OTHER_THAN_HIV_TEXT = "immunodeficiencyOtherThanHivText";
	public static final String EXPOSED_TO_MOSQUITO_BORNE_VIRUSES = "exposedToMosquitoBorneViruses";
	public static final String EXPOSED_TO_MOSQUITO_BORNE_VIRUSES_TEXT = "exposedToMosquitoBorneVirusesText";
	public static final String VACCINATED_AGAINST_MOSQUITO_BORNE_VIRUSES = "vaccinatedAgainstMosquitoBorneViruses";
	public static final String MALARIA = "malaria";
	public static final String MALARIA_INFECTED_YEAR = "malariaInfectedYear";
	public static final String ON_MEDICATION = "onMedication";
	public static final String MEDICATION_DETAILS = "medicationDetails";
	public static final String CHRONIC_DISEASE = "chronicDisease";
	public static final String CHRONIC_DISEASE_DETAILS = "chronicDiseaseDetails";
	public static final String HIV_STATUS = "hivStatus";
	public static final String MENTAL_HEALTH_DISORDER = "mentalHealthDisorder";
	public static final String SUBSTANCE_USE_DISORDER = "substanceUseDisorder";
	public static final String SUBSTANCE_USE_DISORDER_DETAILS = "substanceUseDisorderDetails";
	public static final String STI_PROPHYLAXIS = "stiProphylaxis";
	public static final String HIV_PREP = "hivPrep";
	public static final String TREATED_FOR_SYPHILIS = "treatedForSyphilis";
	public static final String SYPHILIS_DRUG_USED = "syphilisDrugUsed";
	public static final String SYPHILIS_NUMBER_OF_DOSES = "syphilisNumberOfDoses";
	public static final String SYPHILIS_DATE_OF_FIRST_DOSE = "syphilisDateOfFirstDose";

	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown tuberculosis;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.SYPHILIS }, hide = true)
	private YesNoUnknown asplenia;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown hepatitis;
	private YesNoUnknown diabetes;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@Diseases(value = {
		Disease.SYPHILIS }, hide = true)
	private YesNoUnknown hiv;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@Diseases(value = {
		Disease.SYPHILIS }, hide = true)
	private YesNoUnknown hivArt;
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	private YesNoUnknown chronicLiverDisease;
	private YesNoUnknown malignancyChemotherapy;

	//TODO: rename ? general heart issue
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	private YesNoUnknown chronicHeartFailure;
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	private YesNoUnknown chronicPulmonaryDisease;
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	private YesNoUnknown chronicKidneyDisease;
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	private YesNoUnknown chronicNeurologicCondition;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.SYPHILIS }, hide = true)
	private YesNoUnknown downSyndrome;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown congenitalSyphilis;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown immunodeficiencyOtherThanHiv;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.DENGUE })
	private String immunodeficiencyOtherThanHivText;
	private YesNoUnknown cardiovascularDiseaseIncludingHypertension;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown obesity;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown currentSmoker;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown formerSmoker;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown asthma;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown sickleCellDisease;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	private YesNoUnknown immunodeficiencyIncludingHiv;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String otherConditions;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	private YesNoUnknown previousTuberculosisTreatment;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	private Integer tuberculosisInfectionYear;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	private ComplianceWithTreatment complianceWithTreatment;

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	private YesNoUnknown recurrentBronchiolitis;

	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.DENGUE })
	private YesNoUnknown exposedToMosquitoBorneViruses;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.DENGUE })
	private String exposedToMosquitoBorneVirusesText;
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@Diseases(value = {
		Disease.DENGUE })
	private YesNoUnknown vaccinatedAgainstMosquitoBorneViruses;

	@Diseases(value = {
		Disease.MALARIA })
	private YesNoUnknown malaria;

	@Diseases(value = {
		Disease.MALARIA })
	private Integer malariaInfectedYear;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	private YesNoUnknown onMedication;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	private String medicationDetails;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	private YesNoUnknown chronicDisease;
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	private String chronicDiseaseDetails;

	@Diseases(value = {
		Disease.SYPHILIS })
	private HivStatus hivStatus;
	@Diseases(value = {
		Disease.SYPHILIS })
	private YesNoUnknown mentalHealthDisorder;
	@Diseases(value = {
		Disease.SYPHILIS })
	private YesNoUnknown substanceUseDisorder;
	@Diseases(value = {
		Disease.SYPHILIS })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String substanceUseDisorderDetails;
	@Diseases(value = {
		Disease.SYPHILIS })
	private YesNoUnknown stiProphylaxis;
	@Diseases(value = {
		Disease.SYPHILIS })
	private YesNoUnknown hivPrep;
	@Diseases(value = {
		Disease.SYPHILIS })
	private YesNoUnknown treatedForSyphilis;
	@Diseases(value = {
		Disease.SYPHILIS })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String syphilisDrugUsed;
	@Diseases(value = {
		Disease.SYPHILIS })
	private Integer syphilisNumberOfDoses;
	@Diseases(value = {
		Disease.SYPHILIS })
	private Date syphilisDateOfFirstDose;

	public static HealthConditionsDto build() {
		HealthConditionsDto healthConditions = new HealthConditionsDto();
		healthConditions.setUuid(DataHelper.createUuid());
		return healthConditions;
	}

	@Order(1)
	public YesNoUnknown getTuberculosis() {
		return tuberculosis;
	}

	public void setTuberculosis(YesNoUnknown tuberculosis) {
		this.tuberculosis = tuberculosis;
	}

	@Order(2)
	public YesNoUnknown getAsplenia() {
		return asplenia;
	}

	public void setAsplenia(YesNoUnknown asplenia) {
		this.asplenia = asplenia;
	}

	@Order(3)
	public YesNoUnknown getHepatitis() {
		return hepatitis;
	}

	public void setHepatitis(YesNoUnknown hepatitis) {
		this.hepatitis = hepatitis;
	}

	@Order(4)
	public YesNoUnknown getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(YesNoUnknown diabetes) {
		this.diabetes = diabetes;
	}

	@Order(5)
	public YesNoUnknown getImmunodeficiencyOtherThanHiv() {
		return immunodeficiencyOtherThanHiv;
	}

	public void setImmunodeficiencyOtherThanHiv(YesNoUnknown immunodeficiencyOtherThanHiv) {
		this.immunodeficiencyOtherThanHiv = immunodeficiencyOtherThanHiv;
	}

	@Order(6)
	public YesNoUnknown getImmunodeficiencyIncludingHiv() {
		return immunodeficiencyIncludingHiv;
	}

	public void setImmunodeficiencyIncludingHiv(YesNoUnknown immunodeficiencyIncludingHiv) {
		this.immunodeficiencyIncludingHiv = immunodeficiencyIncludingHiv;
	}

	@Order(32)
	public YesNoUnknown getHiv() {
		return hiv;
	}

	public void setHiv(YesNoUnknown hiv) {
		this.hiv = hiv;
	}

	@Order(11)
	public YesNoUnknown getHivArt() {
		return hivArt;
	}

	public void setHivArt(YesNoUnknown hivArt) {
		this.hivArt = hivArt;
	}

	@Order(12)
	public YesNoUnknown getChronicLiverDisease() {
		return chronicLiverDisease;
	}

	public void setChronicLiverDisease(YesNoUnknown chronicLiverDisease) {
		this.chronicLiverDisease = chronicLiverDisease;
	}

	@Order(13)
	public YesNoUnknown getMalignancyChemotherapy() {
		return malignancyChemotherapy;
	}

	public void setMalignancyChemotherapy(YesNoUnknown malignancyChemotherapy) {
		this.malignancyChemotherapy = malignancyChemotherapy;
	}

	@Order(14)
	public YesNoUnknown getChronicHeartFailure() {
		return chronicHeartFailure;
	}

	public void setChronicHeartFailure(YesNoUnknown chronicHeartFailure) {
		this.chronicHeartFailure = chronicHeartFailure;
	}

	@Order(20)
	public YesNoUnknown getChronicPulmonaryDisease() {
		return chronicPulmonaryDisease;
	}

	public void setChronicPulmonaryDisease(YesNoUnknown chronicPulmonaryDisease) {
		this.chronicPulmonaryDisease = chronicPulmonaryDisease;
	}

	@Order(21)
	public YesNoUnknown getChronicKidneyDisease() {
		return chronicKidneyDisease;
	}

	public void setChronicKidneyDisease(YesNoUnknown chronicKidneyDisease) {
		this.chronicKidneyDisease = chronicKidneyDisease;
	}

	@Order(22)
	public YesNoUnknown getChronicNeurologicCondition() {
		return chronicNeurologicCondition;
	}

	public void setChronicNeurologicCondition(YesNoUnknown chronicNeurologicCondition) {
		this.chronicNeurologicCondition = chronicNeurologicCondition;
	}

	@Order(23)
	public YesNoUnknown getCardiovascularDiseaseIncludingHypertension() {
		return cardiovascularDiseaseIncludingHypertension;
	}

	public void setCardiovascularDiseaseIncludingHypertension(YesNoUnknown cardiovascularDiseaseIncludingHypertension) {
		this.cardiovascularDiseaseIncludingHypertension = cardiovascularDiseaseIncludingHypertension;
	}

	@Order(24)
	public YesNoUnknown getDownSyndrome() {
		return downSyndrome;
	}

	public void setDownSyndrome(YesNoUnknown downSyndrome) {
		this.downSyndrome = downSyndrome;
	}

	@Order(25)
	public YesNoUnknown getCongenitalSyphilis() {
		return congenitalSyphilis;
	}

	public void setCongenitalSyphilis(YesNoUnknown congenitalSyphilis) {
		this.congenitalSyphilis = congenitalSyphilis;
	}

	@Order(26)
	public YesNoUnknown getObesity() {
		return obesity;
	}

	public void setObesity(YesNoUnknown obesity) {
		this.obesity = obesity;
	}

	@Order(27)
	public YesNoUnknown getCurrentSmoker() {
		return currentSmoker;
	}

	public void setCurrentSmoker(YesNoUnknown currentSmoker) {
		this.currentSmoker = currentSmoker;
	}

	@Order(28)
	public YesNoUnknown getFormerSmoker() {
		return formerSmoker;
	}

	public void setFormerSmoker(YesNoUnknown formerSmoker) {
		this.formerSmoker = formerSmoker;
	}

	@Order(29)
	public YesNoUnknown getAsthma() {
		return asthma;
	}

	public void setAsthma(YesNoUnknown asthma) {
		this.asthma = asthma;
	}

	@Order(30)
	public YesNoUnknown getSickleCellDisease() {
		return sickleCellDisease;
	}

	public void setSickleCellDisease(YesNoUnknown sickleCellDisease) {
		this.sickleCellDisease = sickleCellDisease;
	}

	@Order(31)
	public String getOtherConditions() {
		return otherConditions;
	}

	public void setOtherConditions(String otherConditions) {
		this.otherConditions = otherConditions;
	}

	@Order(10)
	public YesNoUnknown getPreviousTuberculosisTreatment() {
		return previousTuberculosisTreatment;
	}

	public void setPreviousTuberculosisTreatment(YesNoUnknown previousTuberculosisTreatment) {
		this.previousTuberculosisTreatment = previousTuberculosisTreatment;
	}

	public Integer getTuberculosisInfectionYear() {
		return tuberculosisInfectionYear;
	}

	public void setTuberculosisInfectionYear(Integer tuberculosisInfectionYear) {
		this.tuberculosisInfectionYear = tuberculosisInfectionYear;
	}

	public ComplianceWithTreatment getComplianceWithTreatment() {
		return complianceWithTreatment;
	}

	public void setComplianceWithTreatment(ComplianceWithTreatment complianceWithTreatment) {
		this.complianceWithTreatment = complianceWithTreatment;
	}

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

	public YesNoUnknown getVaccinatedAgainstMosquitoBorneViruses() {
		return vaccinatedAgainstMosquitoBorneViruses;
	}

	public void setVaccinatedAgainstMosquitoBorneViruses(YesNoUnknown vaccinatedAgainstMosquitoBorneViruses) {
		this.vaccinatedAgainstMosquitoBorneViruses = vaccinatedAgainstMosquitoBorneViruses;
	}

	public YesNoUnknown getMalaria() {
		return malaria;
	}

	public void setMalaria(YesNoUnknown malaria) {
		this.malaria = malaria;
	}

	public Integer getMalariaInfectedYear() {
		return malariaInfectedYear;
	}

	public void setMalariaInfectedYear(Integer malariaInfectedYear) {
		this.malariaInfectedYear = malariaInfectedYear;
	}

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

	public HivStatus getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(HivStatus hivStatus) {
		this.hivStatus = hivStatus;
	}

	public YesNoUnknown getMentalHealthDisorder() {
		return mentalHealthDisorder;
	}

	public void setMentalHealthDisorder(YesNoUnknown mentalHealthDisorder) {
		this.mentalHealthDisorder = mentalHealthDisorder;
	}

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

	public YesNoUnknown getStiProphylaxis() {
		return stiProphylaxis;
	}

	public void setStiProphylaxis(YesNoUnknown stiProphylaxis) {
		this.stiProphylaxis = stiProphylaxis;
	}

	public YesNoUnknown getHivPrep() {
		return hivPrep;
	}

	public void setHivPrep(YesNoUnknown hivPrep) {
		this.hivPrep = hivPrep;
	}

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

}
