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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiDataGathering other = (EpiDataGathering) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (epiData == null) {
			if (other.epiData != null)
				return false;
		} else if (!epiData.equals(other.epiData))
			return false;
		if (gatheringAddress == null) {
			if (other.gatheringAddress != null)
				return false;
		} else if (!gatheringAddress.equals(other.gatheringAddress))
			return false;
		if (gatheringDate == null) {
			if (other.gatheringDate != null)
				return false;
		} else if (!gatheringDate.equals(other.gatheringDate))
			return false;
		return true;
	}

}
