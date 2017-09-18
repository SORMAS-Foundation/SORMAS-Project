package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.Diseases;

public class EpiDataGatheringDto extends DataTransferObject {

	private static final long serialVersionUID = 4953376180428831063L;

	public static final String I18N_PREFIX = "EpiDataGathering";
	
	public static final String DESCRIPTION = "description";
	public static final String GATHERING_DATE = "gatheringDate";
	public static final String GATHERING_ADDRESS = "gatheringAddress";
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE})
	private String description;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE})
	private Date gatheringDate;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES,Disease.YELLOW_FEVER,Disease.DENGUE})
	private LocationDto gatheringAddress;
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGatheringDate() {
		return gatheringDate;
	}
	public void setGatheringDate(Date gatheringDate) {
		this.gatheringDate = gatheringDate;
	}
	
	public LocationDto getGatheringAddress() {
		return gatheringAddress;
	}
	public void setGatheringAddress(LocationDto gatheringAddress) {
		this.gatheringAddress = gatheringAddress;
	}
	
}
