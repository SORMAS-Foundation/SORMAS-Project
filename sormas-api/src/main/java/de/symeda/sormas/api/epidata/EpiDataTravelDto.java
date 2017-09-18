package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;

public class EpiDataTravelDto extends DataTransferObject {

	private static final long serialVersionUID = 7369710205233407286L;

	public static final String I18N_PREFIX = "EpiDataTravel";
	
	public static final String TRAVEL_TYPE = "travelType";
	public static final String TRAVEL_DESTINATION = "travelDestination";
	public static final String TRAVEL_DATE_FROM = "travelDateFrom";
	public static final String TRAVEL_DATE_TO = "travelDateTo";
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private TravelType travelType;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private String travelDestination;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private Date travelDateFrom;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE,Disease.OTHER})
	private Date travelDateTo;

	
	public TravelType getTravelType() {
		return travelType;
	}
	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}
	
	public String getTravelDestination() {
		return travelDestination;
	}
	public void setTravelDestination(String travelDestination) {
		this.travelDestination = travelDestination;
	}

	public Date getTravelDateFrom() {
		return travelDateFrom;
	}
	public void setTravelDateFrom(Date travelDateFrom) {
		this.travelDateFrom = travelDateFrom;
	}

	public Date getTravelDateTo() {
		return travelDateTo;
	}
	public void setTravelDateTo(Date travelDateTo) {
		this.travelDateTo = travelDateTo;
	}
	
}
