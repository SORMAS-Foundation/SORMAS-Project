package de.symeda.sormas.api.campaign.data;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;

public class MapCampaignDataDto implements Serializable {

	private static final long serialVersionUID = -3021332968056368431L;

	public static final String I18N_PREFIX = "CampaignMapData";
	
	private String uuid;
	//private Date reportDate;
	//private CampaignDto campaign;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double reportLon;
	//TODO: Maybe add district, cluster, and region location
	
	public MapCampaignDataDto(
			String uuid,
			
			//CampaignDto campaign,
			Double reportLat,
			Double reportLon
			) {

			this.uuid = uuid;
			//this.reportDate = reportDate;
			this.reportLat = reportLat;
			this.reportLon = reportLon;
			//this.campaign = campaign;
		}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/*public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}*/

	/*public CampaignDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignDto campaign) {
		this.campaign = campaign;
	}*/

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}
	
	
}
