package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpiDataDto extends DataTransferObject {

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
	public static final String BURIALS = "burials";
	public static final String GATHERINGS = "gatherings";
	public static final String TRAVELS = "travels";
	
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown burialAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private YesNoUnknown gatheringAttended;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private YesNoUnknown traveled;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown rodents;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown bats;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown primates;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown swine;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown birds;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private YesNoUnknown poultryEat;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private YesNoUnknown poultry;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private String poultryDetails;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private YesNoUnknown poultrySick;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private String poultrySickDetails;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private Date poultryDate;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private String poultryLocation;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private YesNoUnknown wildbirds;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private String wildbirdsDetails;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private Date wildbirdsDate;
	@Diseases({Disease.AVIAN_INFLUENCA})
	private String wildbirdsLocation;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown cattle;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA})
	private YesNoUnknown otherAnimals;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA})
	private String otherAnimalsDetails;
	@Diseases({Disease.CHOLERA})
	private WaterSource waterSource;
	@Diseases({Disease.CHOLERA})
	private String waterSourceOther;
	@Diseases({Disease.CHOLERA})
	private YesNoUnknown waterBody;
	@Diseases({Disease.CHOLERA})
	private String waterBodyDetails;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown tickBite;

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

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getPoultryDate() {
		return poultryDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
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

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getWildbirdsDate() {
		return wildbirdsDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiDataDto other = (EpiDataDto) obj;
		if (bats != other.bats)
			return false;
		if (birds != other.birds)
			return false;
		if (burialAttended != other.burialAttended)
			return false;
		if (burials == null) {
			if (other.burials != null)
				return false;
		} else if (!burials.equals(other.burials))
			return false;
		if (cattle != other.cattle)
			return false;
		if (gatheringAttended != other.gatheringAttended)
			return false;
		if (gatherings == null) {
			if (other.gatherings != null)
				return false;
		} else if (!gatherings.equals(other.gatherings))
			return false;
		if (otherAnimals != other.otherAnimals)
			return false;
		if (otherAnimalsDetails == null) {
			if (other.otherAnimalsDetails != null)
				return false;
		} else if (!otherAnimalsDetails.equals(other.otherAnimalsDetails))
			return false;
		if (poultry != other.poultry)
			return false;
		if (poultryDate == null) {
			if (other.poultryDate != null)
				return false;
		} else if (!poultryDate.equals(other.poultryDate))
			return false;
		if (poultryDetails == null) {
			if (other.poultryDetails != null)
				return false;
		} else if (!poultryDetails.equals(other.poultryDetails))
			return false;
		if (poultryEat != other.poultryEat)
			return false;
		if (poultryLocation == null) {
			if (other.poultryLocation != null)
				return false;
		} else if (!poultryLocation.equals(other.poultryLocation))
			return false;
		if (poultrySick != other.poultrySick)
			return false;
		if (poultrySickDetails == null) {
			if (other.poultrySickDetails != null)
				return false;
		} else if (!poultrySickDetails.equals(other.poultrySickDetails))
			return false;
		if (primates != other.primates)
			return false;
		if (rodents != other.rodents)
			return false;
		if (swine != other.swine)
			return false;
		if (tickBite != other.tickBite)
			return false;
		if (traveled != other.traveled)
			return false;
		if (travels == null) {
			if (other.travels != null)
				return false;
		} else if (!travels.equals(other.travels))
			return false;
		if (waterBody != other.waterBody)
			return false;
		if (waterBodyDetails == null) {
			if (other.waterBodyDetails != null)
				return false;
		} else if (!waterBodyDetails.equals(other.waterBodyDetails))
			return false;
		if (waterSource != other.waterSource)
			return false;
		if (waterSourceOther == null) {
			if (other.waterSourceOther != null)
				return false;
		} else if (!waterSourceOther.equals(other.waterSourceOther))
			return false;
		if (wildbirds != other.wildbirds)
			return false;
		if (wildbirdsDate == null) {
			if (other.wildbirdsDate != null)
				return false;
		} else if (!wildbirdsDate.equals(other.wildbirdsDate))
			return false;
		if (wildbirdsDetails == null) {
			if (other.wildbirdsDetails != null)
				return false;
		} else if (!wildbirdsDetails.equals(other.wildbirdsDetails))
			return false;
		if (wildbirdsLocation == null) {
			if (other.wildbirdsLocation != null)
				return false;
		} else if (!wildbirdsLocation.equals(other.wildbirdsLocation))
			return false;
		return true;
	}

}
