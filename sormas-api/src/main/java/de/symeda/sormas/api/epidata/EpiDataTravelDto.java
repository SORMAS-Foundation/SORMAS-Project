package de.symeda.sormas.api.epidata;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class EpiDataTravelDto extends DataTransferObject {

	private static final long serialVersionUID = 7369710205233407286L;

	public static final String I18N_PREFIX = "EpiDataTravel";
	
	public static final String TRAVEL_TYPE = "travelType";
	public static final String TRAVEL_DESTINATION = "travelDestination";
	public static final String TRAVEL_DATE_FROM = "travelDateFrom";
	public static final String TRAVEL_DATE_TO = "travelDateTo";
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private TravelType travelType;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private String travelDestination;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private Date travelDateFrom;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
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

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getTravelDateFrom() {
		return travelDateFrom;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setTravelDateFrom(Date travelDateFrom) {
		this.travelDateFrom = travelDateFrom;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getTravelDateTo() {
		return travelDateTo;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setTravelDateTo(Date travelDateTo) {
		this.travelDateTo = travelDateTo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiDataTravelDto other = (EpiDataTravelDto) obj;
		if (travelDateFrom == null) {
			if (other.travelDateFrom != null)
				return false;
		} else if (!travelDateFrom.equals(other.travelDateFrom))
			return false;
		if (travelDateTo == null) {
			if (other.travelDateTo != null)
				return false;
		} else if (!travelDateTo.equals(other.travelDateTo))
			return false;
		if (travelDestination == null) {
			if (other.travelDestination != null)
				return false;
		} else if (!travelDestination.equals(other.travelDestination))
			return false;
		if (travelType != other.travelType)
			return false;
		return true;
	}
	
}
