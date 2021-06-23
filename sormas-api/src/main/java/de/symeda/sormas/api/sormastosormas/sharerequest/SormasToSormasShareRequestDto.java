/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas.sharerequest;

import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.utils.DataHelper;

public class SormasToSormasShareRequestDto extends EntityDto {

	private static final long serialVersionUID = 2658688866070962522L;

	public static String I18N_PREFIX = "SormasToSormasShareRequest";

	public static String DATA_TYPE = "dataType";
	public static String STATUS = "status";
	public static String ORIGIN_INFO = "originInfo";
	public static String CASES = "cases";
	public static String CONTACTS = "contacts";
	public static String EVENTS = "events";

	private ShareRequestDataType dataType;

	private ShareRequestStatus status;

	private SormasToSormasOriginInfoDto originInfo;

	private List<SormasToSormasCasePreview> cases;
	private List<SormasToSormasContactPreview> contacts;
	private List<SormasToSormasEventPreview> events;

	public ShareRequestDataType getDataType() {
		return dataType;
	}

	public void setDataType(ShareRequestDataType dataType) {
		this.dataType = dataType;
	}

	public ShareRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ShareRequestStatus status) {
		this.status = status;
	}

	public SormasToSormasOriginInfoDto getOriginInfo() {
		return originInfo;
	}

	public void setOriginInfo(SormasToSormasOriginInfoDto originInfo) {
		this.originInfo = originInfo;
	}

	public List<SormasToSormasCasePreview> getCases() {
		return cases;
	}

	public void setCases(List<SormasToSormasCasePreview> cases) {
		this.cases = cases;
	}

	public List<SormasToSormasContactPreview> getContacts() {
		return contacts;
	}

	public void setContacts(List<SormasToSormasContactPreview> contacts) {
		this.contacts = contacts;
	}

	public List<SormasToSormasEventPreview> getEvents() {
		return events;
	}

	public void setEvents(List<SormasToSormasEventPreview> events) {
		this.events = events;
	}

	public static SormasToSormasShareRequestDto build() {
		SormasToSormasShareRequestDto dto = new SormasToSormasShareRequestDto();

		dto.setUuid(DataHelper.createUuid());

		return dto;
	}
}
