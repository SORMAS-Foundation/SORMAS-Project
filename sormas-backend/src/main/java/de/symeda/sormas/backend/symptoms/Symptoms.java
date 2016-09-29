package de.symeda.sormas.backend.symptoms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.symptoms.FeverMeasured;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Symptoms extends AbstractDomainObject {

	private static final long serialVersionUID = 1467852910743225822L;
	
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
	
	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public FeverMeasured getFeverMeasured() {
		return feverMeasured;
	}
	public void setFeverMeasured(FeverMeasured feverMeasured) {
		this.feverMeasured = feverMeasured;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getVomiting() {
		return vomiting;
	}
	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getDiarrha() {
		return diarrha;
	}
	public void setDiarrha(SymptomState diarrha) {
		this.diarrha = diarrha;
	}
	
	@Enumerated(EnumType.STRING)	
	public SymptomState getIntenseFatigue() {
		return intenseFatigue;
	}
	public void setIntenseFatigue(SymptomState intenseFatigue) {
		this.intenseFatigue = intenseFatigue;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getAnorexia() {
		return anorexia;
	}
	public void setAnorexia(SymptomState anorexia) {
		this.anorexia = anorexia;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}
	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getChestPain() {
		return chestPain;
	}
	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getMusclePain() {
		return musclePain;
	}
	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getJointPain() {
		return jointPain;
	}
	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getHeadache() {
		return headache;
	}
	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getCough() {
		return cough;
	}
	public void setCough(SymptomState cough) {
		this.cough = cough;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}
	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getDifficultySwallowing() {
		return difficultySwallowing;
	}
	public void setDifficultySwallowing(SymptomState difficultySwallowing) {
		this.difficultySwallowing = difficultySwallowing;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getSoreThroat() {
		return soreThroat;
	}
	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getJaundice() {
		return jaundice;
	}
	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}
	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getSkinRash() {
		return skinRash;
	}
	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getHiccups() {
		return hiccups;
	}
	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPainBehindEyes() {
		return painBehindEyes;
	}
	public void setPainBehindEyes(SymptomState painBehindEyes) {
		this.painBehindEyes = painBehindEyes;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getComa() {
		return coma;
	}
	public void setComa(SymptomState coma) {
		this.coma = coma;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getConfusedOrDisoriented() {
		return confusedOrDisoriented;
	}
	public void setConfusedOrDisoriented(SymptomState confusedOrDisoriented) {
		this.confusedOrDisoriented = confusedOrDisoriented;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}
	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getBleedingGums() {
		return bleedingGums;
	}
	public void setBleedingGums(SymptomState bleedingGums) {
		this.bleedingGums = bleedingGums;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getBleedingInjectionSite() {
		return bleedingInjectionSite;
	}
	public void setBleedingInjectionSite(SymptomState bleedingInjectionSite) {
		this.bleedingInjectionSite = bleedingInjectionSite;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getEpistaxis() {
		return epistaxis;
	}
	public void setEpistaxis(SymptomState epistaxis) {
		this.epistaxis = epistaxis;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getMelena() {
		return melena;
	}
	public void setMelena(SymptomState melena) {
		this.melena = melena;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getHematemesis() {
		return hematemesis;
	}
	public void setHematemesis(SymptomState hematemesis) {
		this.hematemesis = hematemesis;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getDigestedBloodInVomit() {
		return digestedBloodInVomit;
	}
	public void setDigestedBloodInVomit(SymptomState digestedBloodInVomit) {
		this.digestedBloodInVomit = digestedBloodInVomit;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getHemoptysis() {
		return hemoptysis;
	}
	public void setHemoptysis(SymptomState hemoptysis) {
		this.hemoptysis = hemoptysis;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getBleedingFromVagina() {
		return bleedingFromVagina;
	}
	public void setBleedingFromVagina(SymptomState bleedingFromVagina) {
		this.bleedingFromVagina = bleedingFromVagina;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getPetechiae() {
		return petechiae;
	}
	public void setPetechiae(SymptomState petechiae) {
		this.petechiae = petechiae;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getHematuria() {
		return hematuria;
	}
	public void setHematuria(SymptomState hematuria) {
		this.hematuria = hematuria;
	}
	
	@Enumerated(EnumType.STRING)
	public SymptomState getOther() {
		return other;
	}
	public void setOther(SymptomState other) {
		this.other = other;
	}
	
	@Column(length = 255)
	public String getOtherSymptoms() {
		return otherSymptoms;
	}
	public void setOtherSymptoms(String otherSymptoms) {
		this.otherSymptoms = otherSymptoms;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOtherNonHemorrhagic() {
		return otherNonHemorrhagic;
	}
	public void setOtherNonHemorrhagic(SymptomState otherNonHemorrhagic) {
		this.otherNonHemorrhagic = otherNonHemorrhagic;
	}
	
	@Column(length = 255)
	public String getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}
	public void setOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}
}
