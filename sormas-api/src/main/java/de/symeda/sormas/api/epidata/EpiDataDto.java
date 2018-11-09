package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpiDataDto extends EntityDto {

	private static final long serialVersionUID = 6292411396563549093L;
	
	public static final String I18N_PREFIX = "EpiData";
	
	public static final String BURIAL_ATTENDED = "burialAttended";
	public static final String GATHERING_ATTENDED = "gatheringAttended";
	public static final String TRAVELED = "traveled";
	public static final String RODENTS = "rodents";
	public static final String BATS = "bats";
	public static final String PRIMATES = "primates";
	public static final String SWINE = "swine";
	public static final String BIRDS = "birds";
	public static final String CATTLE = "cattle";
	public static final String OTHER_ANIMALS = "otherAnimals";
	public static final String OTHER_ANIMALS_DETAILS = "otherAnimalsDetails";
	public static final String WATER_SOURCE = "waterSource";
	public static final String WATER_SOURCE_OTHER = "waterSourceOther";
	public static final String WATER_BODY = "waterBody";
	public static final String WATER_BODY_DETAILS = "waterBodyDetails";
	public static final String TICK_BITE = "tickBite";
	public static final String FLEA_BITE = "fleaBite";
	public static final String DATE_OF_LAST_EXPOSURE = "dateOfLastExposure";
	public static final String PLACE_OF_LAST_EXPOSURE = "placeOfLastExposure";
	public static final String ANIMAL_CONDITION = "animalCondition";
	public static final String BURIALS = "burials";
	public static final String GATHERINGS = "gatherings";
	public static final String TRAVELS = "travels";
	public static final String DIRECT_CONTACT_CONFIRMED_CASE = "directContactConfirmedCase";
	public static final String DIRECT_CONTACT_PROBABLE_CASE = "directContactProbableCase";
	public static final String CLOSE_CONTACT_PROBABLE_CASE = "closeContactProbableCase";
	public static final String AREA_CONFIRMED_CASES = "areaConfirmedCases";
	public static final String PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE = "processingConfirmedCaseFluidUnsafe";
	public static final String PERCUTANEOUS_CASE_BLOOD = "percutaneousCaseBlood";
	public static final String DIRECT_CONTACT_DEAD_UNSAFE = "directContactDeadUnsafe";
	public static final String PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE = "processingSuspectedCaseSampleUnsafe";
	public static final String AREA_INFECTED_ANIMALS = "areaInfectedAnimals";
	public static final String SICK_DEAD_ANIMALS = "sickDeadAnimals";
	public static final String SICK_DEAD_ANIMALS_DETAILS = "sickDeadAnimalsDetails";
	public static final String SICK_DEAD_ANIMALS_DATE = "sickDeadAnimalsDate";
	public static final String SICK_DEAD_ANIMALS_LOCATION = "sickDeadAnimalsLocation";
	public static final String EATING_RAW_ANIMALS_IN_INFECTED_AREA = "eatingRawAnimalsInInfectedArea";
	public static final String EATING_RAW_ANIMALS = "eatingRawAnimals";
	public static final String EATING_RAW_ANIMALS_DETAILS = "eatingRawAnimalsDetails";

	// Fields are declared in the order they should appear in the import template
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown burialAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private YesNoUnknown gatheringAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private YesNoUnknown traveled;
	
	private List<EpiDataBurialDto> burials = new ArrayList<>();
	private List<EpiDataGatheringDto> gatherings = new ArrayList<>();
	private List<EpiDataTravelDto> travels = new ArrayList<>();

	@Diseases({Disease.EVD,Disease.NEW_INFLUENCA,Disease.CSM,Disease.MEASLES,Disease.OTHER})
	private YesNoUnknown directContactConfirmedCase;
	@Diseases({Disease.LASSA,Disease.OTHER})
	private YesNoUnknown directContactProbableCase;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown closeContactProbableCase;
	@Diseases({Disease.DENGUE, Disease.EVD, Disease.PLAGUE, Disease.YELLOW_FEVER, Disease.OTHER})
	private YesNoUnknown areaConfirmedCases;
	@Diseases({Disease.EVD, Disease.OTHER})
	private YesNoUnknown processingConfirmedCaseFluidUnsafe;
	@Diseases({Disease.EVD, Disease.OTHER})
	private YesNoUnknown percutaneousCaseBlood;
	@Diseases({Disease.EVD, Disease.OTHER})
	private YesNoUnknown directContactDeadUnsafe;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown processingSuspectedCaseSampleUnsafe;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown areaInfectedAnimals;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown sickDeadAnimals;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private String sickDeadAnimalsDetails;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private Date sickDeadAnimalsDate;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private String sickDeadAnimalsLocation;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown eatingRawAnimalsInInfectedArea;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private YesNoUnknown eatingRawAnimals;
	@Diseases({Disease.NEW_INFLUENCA, Disease.OTHER})
	private String eatingRawAnimalsDetails;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private YesNoUnknown rodents;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown bats;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.MONKEYPOX,Disease.OTHER})
	private YesNoUnknown primates;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown swine;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown birds;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown cattle;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.MONKEYPOX,Disease.OTHER})
	private YesNoUnknown otherAnimals;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.MONKEYPOX,Disease.OTHER})
	private String otherAnimalsDetails;
	@Diseases({Disease.CHOLERA,Disease.OTHER})
	private WaterSource waterSource;
	@Diseases({Disease.CHOLERA,Disease.OTHER})
	private String waterSourceOther;
	@Diseases({Disease.CHOLERA,Disease.OTHER})
	private YesNoUnknown waterBody;
	@Diseases({Disease.CHOLERA,Disease.OTHER})
	private String waterBodyDetails;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown tickBite;
	@Diseases({Disease.PLAGUE,Disease.OTHER})
	private YesNoUnknown fleaBite;
	@Diseases({Disease.MONKEYPOX,Disease.OTHER})
	private Date dateOfLastExposure;
	@Diseases({Disease.MONKEYPOX,Disease.OTHER})	
	private String placeOfLastExposure;
	@Diseases({Disease.MONKEYPOX,Disease.OTHER})
	private AnimalCondition animalCondition;
	
	public YesNoUnknown getBurialAttended() {
		return burialAttended;
	}
	public void setBurialAttended(YesNoUnknown burialAttended) {
		this.burialAttended = burialAttended;
	}
	
	public YesNoUnknown getGatheringAttended() {
		return gatheringAttended;
	}
	public void setGatheringAttended(YesNoUnknown gatheringAttended) {
		this.gatheringAttended = gatheringAttended;
	}
	
	public YesNoUnknown getTraveled() {
		return traveled;
	}
	public void setTraveled(YesNoUnknown traveled) {
		this.traveled = traveled;
	}
	
	public YesNoUnknown getRodents() {
		return rodents;
	}
	public void setRodents(YesNoUnknown rodents) {
		this.rodents = rodents;
	}
	
	public YesNoUnknown getBats() {
		return bats;
	}
	public void setBats(YesNoUnknown bats) {
		this.bats = bats;
	}
	
	public YesNoUnknown getPrimates() {
		return primates;
	}
	public void setPrimates(YesNoUnknown primates) {
		this.primates = primates;
	}
	
	public YesNoUnknown getSwine() {
		return swine;
	}
	public void setSwine(YesNoUnknown swine) {
		this.swine = swine;
	}
	
	public YesNoUnknown getBirds() {
		return birds;
	}
	public void setBirds(YesNoUnknown birds) {
		this.birds = birds;
	}
		
	public YesNoUnknown getCattle() {
		return cattle;
	}
	public void setCattle(YesNoUnknown cattle) {
		this.cattle = cattle;
	}
	
	public YesNoUnknown getOtherAnimals() {
		return otherAnimals;
	}
	public void setOtherAnimals(YesNoUnknown otherAnimals) {
		this.otherAnimals = otherAnimals;
	}
	
	public String getOtherAnimalsDetails() {
		return otherAnimalsDetails;
	}
	public void setOtherAnimalsDetails(String otherAnimalsDetails) {
		this.otherAnimalsDetails = otherAnimalsDetails;
	}
	
	public WaterSource getWaterSource() {
		return waterSource;
	}
	public void setWaterSource(WaterSource waterSource) {
		this.waterSource = waterSource;
	}
	
	public String getWaterSourceOther() {
		return waterSourceOther;
	}
	public void setWaterSourceOther(String waterSourceOther) {
		this.waterSourceOther = waterSourceOther;
	}
	
	public YesNoUnknown getWaterBody() {
		return waterBody;
	}
	public void setWaterBody(YesNoUnknown waterBody) {
		this.waterBody = waterBody;
	}
	
	public String getWaterBodyDetails() {
		return waterBodyDetails;
	}
	public void setWaterBodyDetails(String waterBodyDetails) {
		this.waterBodyDetails = waterBodyDetails;
	}
	
	public YesNoUnknown getTickBite() {
		return tickBite;
	}
	public void setTickBite(YesNoUnknown tickBite) {
		this.tickBite = tickBite;
	}
	
	public Date getDateOfLastExposure() {
		return dateOfLastExposure;
	}
	public void setDateOfLastExposure(Date dateOfLastExposure) {
		this.dateOfLastExposure = dateOfLastExposure;
	}
	
	public String getPlaceOfLastExposure() {
		return placeOfLastExposure;
	}
	public void setPlaceOfLastExposure(String placeOfLastExposure) {
		this.placeOfLastExposure = placeOfLastExposure;
	}
	
	public AnimalCondition getAnimalCondition() {
		return animalCondition;
	}
	public void setAnimalCondition(AnimalCondition animalCondition) {
		this.animalCondition = animalCondition;
	}
	
	public YesNoUnknown getFleaBite() {
		return fleaBite;
	}
	public void setFleaBite(YesNoUnknown fleaBite) {
		this.fleaBite = fleaBite;
	}
	
	public List<EpiDataBurialDto> getBurials() {
		return burials;
	}
	public void setBurials(List<EpiDataBurialDto> burials) {
		this.burials = burials;
	}
	
	public List<EpiDataGatheringDto> getGatherings() {
		return gatherings;
	}
	public void setGatherings(List<EpiDataGatheringDto> gatherings) {
		this.gatherings = gatherings;
	}
	
	public List<EpiDataTravelDto> getTravels() {
		return travels;
	}
	public void setTravels(List<EpiDataTravelDto> travels) {
		this.travels = travels;
	}
	public YesNoUnknown getDirectContactConfirmedCase() {
		return directContactConfirmedCase;
	}
	public void setDirectContactConfirmedCase(YesNoUnknown directContactConfirmedCase) {
		this.directContactConfirmedCase = directContactConfirmedCase;
	}
	public YesNoUnknown getProcessingConfirmedCaseFluidUnsafe() {
		return processingConfirmedCaseFluidUnsafe;
	}
	public void setProcessingConfirmedCaseFluidUnsafe(YesNoUnknown processingConfirmedCaseFluidUnsafe) {
		this.processingConfirmedCaseFluidUnsafe = processingConfirmedCaseFluidUnsafe;
	}
	public YesNoUnknown getPercutaneousCaseBlood() {
		return percutaneousCaseBlood;
	}
	public void setPercutaneousCaseBlood(YesNoUnknown percutaneousCaseBlood) {
		this.percutaneousCaseBlood = percutaneousCaseBlood;
	}
	public YesNoUnknown getDirectContactDeadUnsafe() {
		return directContactDeadUnsafe;
	}
	public void setDirectContactDeadUnsafe(YesNoUnknown directContactDeadUnsafe) {
		this.directContactDeadUnsafe = directContactDeadUnsafe;
	}
	public YesNoUnknown getProcessingSuspectedCaseSampleUnsafe() {
		return processingSuspectedCaseSampleUnsafe;
	}
	public void setProcessingSuspectedCaseSampleUnsafe(YesNoUnknown processingSuspectedCaseSampleUnsafe) {
		this.processingSuspectedCaseSampleUnsafe = processingSuspectedCaseSampleUnsafe;
	}
	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}
	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}
	public YesNoUnknown getSickDeadAnimals() {
		return sickDeadAnimals;
	}
	public void setSickDeadAnimals(YesNoUnknown sickDeadAnimals) {
		this.sickDeadAnimals = sickDeadAnimals;
	}
	public String getSickDeadAnimalsDetails() {
		return sickDeadAnimalsDetails;
	}
	public void setSickDeadAnimalsDetails(String sickDeadAnimalsDetails) {
		this.sickDeadAnimalsDetails = sickDeadAnimalsDetails;
	}
	public Date getSickDeadAnimalsDate() {
		return sickDeadAnimalsDate;
	}
	public void setSickDeadAnimalsDate(Date sickDeadAnimalsDate) {
		this.sickDeadAnimalsDate = sickDeadAnimalsDate;
	}
	public String getSickDeadAnimalsLocation() {
		return sickDeadAnimalsLocation;
	}
	public void setSickDeadAnimalsLocation(String sickDeadAnimalsLocation) {
		this.sickDeadAnimalsLocation = sickDeadAnimalsLocation;
	}
	public YesNoUnknown getEatingRawAnimalsInInfectedArea() {
		return eatingRawAnimalsInInfectedArea;
	}
	public void setEatingRawAnimalsInInfectedArea(YesNoUnknown eatingRawAnimalsInInfectedArea) {
		this.eatingRawAnimalsInInfectedArea = eatingRawAnimalsInInfectedArea;
	}
	public YesNoUnknown getEatingRawAnimals() {
		return eatingRawAnimals;
	}
	public void setEatingRawAnimals(YesNoUnknown eatingRawAnimals) {
		this.eatingRawAnimals = eatingRawAnimals;
	}
	public String getEatingRawAnimalsDetails() {
		return eatingRawAnimalsDetails;
	}
	public void setEatingRawAnimalsDetails(String eatingRawAnimalsDetails) {
		this.eatingRawAnimalsDetails = eatingRawAnimalsDetails;
	}
	public YesNoUnknown getDirectContactProbableCase() {
		return directContactProbableCase;
	}
	public void setDirectContactProbableCase(YesNoUnknown directContactProbableCase) {
		this.directContactProbableCase = directContactProbableCase;
	}
	public YesNoUnknown getCloseContactProbableCase() {
		return closeContactProbableCase;
	}
	public void setCloseContactProbableCase(YesNoUnknown closeContactProbableCase) {
		this.closeContactProbableCase = closeContactProbableCase;
	}
	public YesNoUnknown getAreaConfirmedCases() {
		return areaConfirmedCases;
	}
	public void setAreaConfirmedCases(YesNoUnknown areaConfirmedCases) {
		this.areaConfirmedCases = areaConfirmedCases;
	}

	public static EpiDataDto build() {
		EpiDataDto epiData = new EpiDataDto();
		epiData.setUuid(DataHelper.createUuid());
		return epiData;
	}

}
