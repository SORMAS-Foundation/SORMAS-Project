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

package de.symeda.sormas.api.sormastosormas;

import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

import javax.validation.Valid;

public abstract class SormasToSormasShareableDto extends PseudonymizableDto {

	public static final String SORMAS_TO_SORMAS_ORIGIN_INFO = "sormasToSormasOriginInfo";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";
	@Valid
	protected SormasToSormasOriginInfoDto sormasToSormasOriginInfo;
	protected boolean ownershipHandedOver;

	public abstract UserReferenceDto getReportingUser();

	public abstract void setReportingUser(UserReferenceDto reportingUser);

	@ImportIgnore
	public SormasToSormasOriginInfoDto getSormasToSormasOriginInfo() {
		return sormasToSormasOriginInfo;
	}

	public void setSormasToSormasOriginInfo(SormasToSormasOriginInfoDto sormasToSormasOriginInfo) {
		this.sormasToSormasOriginInfo = sormasToSormasOriginInfo;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}
}
