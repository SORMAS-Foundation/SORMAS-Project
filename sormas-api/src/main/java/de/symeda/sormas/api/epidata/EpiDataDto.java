package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Disease;
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
	public static final String POULTRY_EAT = "poultryEat";
	public static final String POULTRY = "poultry";
	public static final String POULTRY_DETAILS = "poultryDetails";
	public static final String POULTRY_SICK = "poultrySick";
	public static final String POULTRY_SICK_DETAILS = "poultrySickDetails";
	public static final String POULTRY_DATE = "poultryDate";
	public static final String POULTRY_LOCATION = "poultryLocation";
	public static final String WILDBIRDS = "wildbirds";
	public static final String WILDBIRDS_DETAILS = "wildbirdsDetails";
	public static final String WILDBIRDS_DATE = "wildbirdsDate";
	public static final String WILDBIRDS_LOCATION = "wildbirdsLocation";
	public static final String CATTLE = "cattle";
	public static final String OTHER_ANIMALS = "otherAnimals";
	public static final String OTHER_ANIMALS_DETAILS = "otherAnimalsDetails";
	public static final String WATER_SOURCE = "waterSource";
	public static final String WATER_SOURCE_OTHER = "waterSourceOther";
	public static final String WATER_BODY = "waterBody";
	public static final String WATER_BODY_DETAILS = "waterBodyDetails";
	public static final String TICKBITE = "tickBite";
	public static final String FLEABITE = "fleaBite";
	public static final String DATE_OF_LAST_EXPOSURE = "dateOfLastExposure";
	public static final String PLACE_OF_LAST_EXPOSURE = "placeOfLastExposure";
	public static final String ANIMAL_CONDITION = "animalCondition";
	public static final String BURIALS = "burials";
	public static final String GATHERINGS = "gatherings";
	public static final String TRAVELS = "travels";
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown burialAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private YesNoUnknown gatheringAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.MONKEYPOX,Disease.PLAGUE,Disease.OTHER})
	private YesNoUnknown traveled;
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
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private YesNoUnknown poultryEat;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private YesNoUnknown poultry;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private String poultryDetails;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private YesNoUnknown poultrySick;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private String poultrySickDetails;
	@Diseases({Disease.NEW_INFLUENCA})
	private Date poultryDate;
	@Diseases({Disease.NEW_INFLUENCA})
	private String poultryLocation;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private YesNoUnknown wildbirds;
	@Diseases({Disease.NEW_INFLUENCA,Disease.OTHER})
	private String wildbirdsDetails;
	@Diseases({Disease.NEW_INFLUENCA})
	private Date wildbirdsDate;
	@Diseases({Disease.NEW_INFLUENCA})
	private String wildbirdsLocation;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.OTHER})
	private YesNoUnknown cattle;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.MONKEYPOX,Disease.OTHER})
	private YesNoUnknown otherAnimals;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.NEW_INFLUENCA,Disease.MONKEYPOX,Disease.OTHER})
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

	private List<EpiDataBurialDto> burials = new ArrayList<>();
	private List<EpiDataGatheringDto> gatherings = new ArrayList<>();
	private List<EpiDataTravelDto> travels = new ArrayList<>();
	
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
	
	public YesNoUnknown getPoultryEat() {
		return poultryEat;
	}
	public void setPoultryEat(YesNoUnknown poultryEat) {
		this.poultryEat = poultryEat;
	}
	
	public YesNoUnknown getPoultry() {
		return poultry;
	}
	public void setPoultry(YesNoUnknown poultry) {
		this.poultry = poultry;
	}
	
	public String getPoultryDetails() {
		return poultryDetails;
	}
	public void setPoultryDetails(String poultryDetails) {
		this.poultryDetails = poultryDetails;
	}
	
	public YesNoUnknown getPoultrySick() {
		return poultrySick;
	}
	public void setPoultrySick(YesNoUnknown poultrySick) {
		this.poultrySick = poultrySick;
	}
	
	public String getPoultrySickDetails() {
		return poultrySickDetails;
	}
	public void setPoultrySickDetails(String poultrySickDetails) {
		this.poultrySickDetails = poultrySickDetails;
	}

	public Date getPoultryDate() {
		return poultryDate;
	}
	public void setPoultryDate(Date poultryDate) {
		this.poultryDate = poultryDate;
	}
	
	public String getPoultryLocation() {
		return poultryLocation;
	}
	public void setPoultryLocation(String poultryLocation) {
		this.poultryLocation = poultryLocation;
	}
	
	public YesNoUnknown getWildbirds() {
		return wildbirds;
	}
	public void setWildbirds(YesNoUnknown wildbirds) {
		this.wildbirds = wildbirds;
	}
	
	public String getWildbirdsDetails() {
		return wildbirdsDetails;
	}
	public void setWildbirdsDetails(String wildbirdsDetails) {
		this.wildbirdsDetails = wildbirdsDetails;
	}

	public Date getWildbirdsDate() {
		return wildbirdsDate;
	}
	public void setWildbirdsDate(Date wildbirdsDate) {
		this.wildbirdsDate = wildbirdsDate;
	}
	
	public String getWildbirdsLocation() {
		return wildbirdsLocation;
	}
	public void setWildbirdsLocation(String wildbirdsLocation) {
		this.wildbirdsLocation = wildbirdsLocation;
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

}
