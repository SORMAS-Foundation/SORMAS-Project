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

package de.symeda.sormas.backend.sormastosormas.entities;

import java.io.Serializable;

import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;

public class SyncDataDto implements Serializable {

	private static final long serialVersionUID = -739984061456636096L;

	private SormasToSormasDto shareData;

	private ShareTreeCriteria criteria;

	public SyncDataDto() {
	}

	public SyncDataDto(SormasToSormasDto shareData, ShareTreeCriteria criteria) {
		this.shareData = shareData;
		this.criteria = criteria;
	}

	public SormasToSormasDto getShareData() {
		return shareData;
	}

	public void setShareData(SormasToSormasDto shareData) {
		this.shareData = shareData;
	}

	public ShareTreeCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShareTreeCriteria criteria) {
		this.criteria = criteria;
	}
}
