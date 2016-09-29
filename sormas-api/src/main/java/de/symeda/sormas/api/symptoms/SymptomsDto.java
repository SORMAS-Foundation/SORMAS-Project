package de.symeda.sormas.api.symptoms;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;

public class SymptomsDto extends DataTransferObject {

	private static final long serialVersionUID = 1467852910743226822L;
	
	private Date initOnset;
	
	private SymptomState fever;
	private int feverTemp;
	private FeverMeasured feverMeasured;
	
	private SymptomState vomiting;
	private SymptomState diarrha;
	private SymptomState intenseFatigue;
	private SymptomState anorexia;
	private SymptomState abdominalPain;
	private SymptomState chestPain;
	private SymptomState musclePain;
	private SymptomState jointPain;
	private SymptomState headache;
	private SymptomState cough;
	private SymptomState difficultyBreathing;
	private SymptomState difficultySwallowing;
	private SymptomState soreThroat;
	private SymptomState jaundice;
	private SymptomState conjunctivitis;
	private SymptomState skinRash;
	private SymptomState hiccups;
	private SymptomState painBehindEyes;
	private SymptomState coma;
	private SymptomState confusedOrDisoriented;
	private SymptomState unexplainedBleeding;
	private SymptomState bleedingGums;
	private SymptomState bleedingInjectionSite;
	private SymptomState epistaxis;
	private SymptomState melena;
	private SymptomState hematemesis;
	private SymptomState digestedBloodInVomit;
	private SymptomState hemoptysis;
	private SymptomState bleedingFromVagina;
	private SymptomState petechiae;
	private SymptomState hematuria;
	
	private SymptomState other;
	private String otherSymptoms;
	
	private SymptomState otherNonHemorrhagic;
	private String otherNonHemorrhagicSymptoms;
	
	public Date getInitOnset() {
		return initOnset;
	}
	public void setInitOnset(Date initOnset) {
		this.initOnset = initOnset;
	}
	
	public SymptomState getFever() {
		return fever;
	}
	public void setFever(SymptomState fever) {
		this.fever = fever;
	}
	
	public int getFeverTemp() {
		return feverTemp;
	}
	public void setFeverTemp(int feverTemp) {
		this.feverTemp = feverTemp;
	}

	public FeverMeasured getFeverMeasured() {
		return feverMeasured;
	}
	public void setFeverMeasured(FeverMeasured feverMeasured) {
		this.feverMeasured = feverMeasured;
	}
	
	public SymptomState getVomiting() {
		return vomiting;
	}
	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
	}
	
	public SymptomState getDiarrha() {
		return diarrha;
	}
	public void setDiarrha(SymptomState diarrha) {
		this.diarrha = diarrha;
	}
	
	public SymptomState getIntenseFatigue() {
		return intenseFatigue;
	}
	public void setIntenseFatigue(SymptomState intenseFatigue) {
		this.intenseFatigue = intenseFatigue;
	}
	
	public SymptomState getAnorexia() {
		return anorexia;
	}
	public void setAnorexia(SymptomState anorexia) {
		this.anorexia = anorexia;
	}
	
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}
	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}
	
	public SymptomState getChestPain() {
		return chestPain;
	}
	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}
	
	public SymptomState getMusclePain() {
		return musclePain;
	}
	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}
	
	public SymptomState getJointPain() {
		return jointPain;
	}
	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}
	
	public SymptomState getHeadache() {
		return headache;
	}
	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}
	
	public SymptomState getCough() {
		return cough;
	}
	public void setCough(SymptomState cough) {
		this.cough = cough;
	}
	
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}
	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}
	
	public SymptomState getDifficultySwallowing() {
		return difficultySwallowing;
	}
	public void setDifficultySwallowing(SymptomState difficultySwallowing) {
		this.difficultySwallowing = difficultySwallowing;
	}
	
	public SymptomState getSoreThroat() {
		return soreThroat;
	}
	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}
	
	public SymptomState getJaundice() {
		return jaundice;
	}
	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}
	
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}
	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}
	
	public SymptomState getSkinRash() {
		return skinRash;
	}
	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}
	
	public SymptomState getHiccups() {
		return hiccups;
	}
	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}

	public SymptomState getPainBehindEyes() {
		return painBehindEyes;
	}
	public void setPainBehindEyes(SymptomState painBehindEyes) {
		this.painBehindEyes = painBehindEyes;
	}
	
	public SymptomState getComa() {
		return coma;
	}
	public void setComa(SymptomState coma) {
		this.coma = coma;
	}
	
	public SymptomState getConfusedOrDisoriented() {
		return confusedOrDisoriented;
	}
	public void setConfusedOrDisoriented(SymptomState confusedOrDisoriented) {
		this.confusedOrDisoriented = confusedOrDisoriented;
	}
	
	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}
	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}
	
	public SymptomState getBleedingGums() {
		return bleedingGums;
	}
	public void setBleedingGums(SymptomState bleedingGums) {
		this.bleedingGums = bleedingGums;
	}
	
	public SymptomState getBleedingInjectionSite() {
		return bleedingInjectionSite;
	}
	public void setBleedingInjectionSite(SymptomState bleedingInjectionSite) {
		this.bleedingInjectionSite = bleedingInjectionSite;
	}
	
	public SymptomState getEpistaxis() {
		return epistaxis;
	}
	public void setEpistaxis(SymptomState epistaxis) {
		this.epistaxis = epistaxis;
	}
	
	public SymptomState getMelena() {
		return melena;
	}
	public void setMelena(SymptomState melena) {
		this.melena = melena;
	}
	
	public SymptomState getHematemesis() {
		return hematemesis;
	}
	public void setHematemesis(SymptomState hematemesis) {
		this.hematemesis = hematemesis;
	}
	
	public SymptomState getDigestedBloodInVomit() {
		return digestedBloodInVomit;
	}
	public void setDigestedBloodInVomit(SymptomState digestedBloodInVomit) {
		this.digestedBloodInVomit = digestedBloodInVomit;
	}
	
	public SymptomState getHemoptysis() {
		return hemoptysis;
	}
	public void setHemoptysis(SymptomState hemoptysis) {
		this.hemoptysis = hemoptysis;
	}
	
	public SymptomState getBleedingFromVagina() {
		return bleedingFromVagina;
	}
	public void setBleedingFromVagina(SymptomState bleedingFromVagina) {
		this.bleedingFromVagina = bleedingFromVagina;
	}
	
	public SymptomState getPetechiae() {
		return petechiae;
	}
	public void setPetechiae(SymptomState petechiae) {
		this.petechiae = petechiae;
	}
	
	public SymptomState getHematuria() {
		return hematuria;
	}
	public void setHematuria(SymptomState hematuria) {
		this.hematuria = hematuria;
	}
	
	public SymptomState getOther() {
		return other;
	}
	public void setOther(SymptomState other) {
		this.other = other;
	}
	
	public String getOtherSymptoms() {
		return otherSymptoms;
	}
	public void setOtherSymptoms(String otherSymptoms) {
		this.otherSymptoms = otherSymptoms;
	}

	public SymptomState getOtherNonHemorrhagic() {
		return otherNonHemorrhagic;
	}
	public void setOtherNonHemorrhagic(SymptomState otherNonHemorrhagic) {
		this.otherNonHemorrhagic = otherNonHemorrhagic;
	}
	
	public String getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}
	public void setOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}
}
