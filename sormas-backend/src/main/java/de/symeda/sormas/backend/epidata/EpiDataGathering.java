package de.symeda.sormas.backend.epidata;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;

@Entity
public class EpiDataGathering extends AbstractDomainObject {

	private static final long serialVersionUID = 5491651166245301869L;
	
	public static final String DESCRIPTION = "description";
	public static final String GATHERING_DATE = "gatheringDate";
	public static final String GATHERING_ADDRESS = "gatheringAddress";
	public static final String EPI_DATA = "epiData";
	
	private EpiData epiData;
	private String description;
	private Date gatheringDate;
	private Location gatheringAddress;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public EpiData getEpiData() {
		return epiData;
	}
	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	@Column(length=512)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getGatheringDate() {
		return gatheringDate;
	}
	public void setGatheringDate(Date gatheringDate) {
		this.gatheringDate = gatheringDate;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Location getGatheringAddress() {
		if (gatheringAddress == null) {
			gatheringAddress = new Location();
		}
		return gatheringAddress;
	}
	public void setGatheringAddress(Location gatheringAddress) {
		this.gatheringAddress = gatheringAddress;
	}
	
}
