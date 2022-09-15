/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.campaign;

import java.util.Date;

import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class CampaignIndexDto extends AbstractUuidDto {
	private static final long serialVersionUID = 2448753530580084851L;

	public static final String I18N_PREFIX = "Campaign";
	public static final String NAME = "name";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";

	private String name;
	private Date startDate;
	private Date endDate;

	public CampaignIndexDto(String uuid, String name, Date startDate, Date endDate) {
		super(uuid);
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
