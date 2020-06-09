/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.epidata;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;

@Entity
@Audited
public class EpiDataGathering extends AbstractDomainObject {

	private static final long serialVersionUID = 5491651166245301869L;

	public static final String TABLE_NAME = "epidatagathering";

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

	@Column(length = COLUMN_LENGTH_DEFAULT)
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
