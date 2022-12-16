/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.utils.pseudonymization;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.MappingException;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class PseudonymizableDto extends EntityDto implements Pseudonymizable {

	private static final long serialVersionUID = 4181307802683421947L;

	public static final String PSEUDONYMIZED = "pseudonymized";
	public static final String IN_JURISDICTION = "inJurisdiction";

	/**
	 * Whether sensitive and/or personal data of this DTO is pseudonymized.
	 */
	@MappingException(reason = MappingException.COMPUTED_FOR_API)
	@Schema(description = "Whether sensitive and/or personal data of this DTO is pseudonymized.")
	private boolean pseudonymized;

	/**
	 * Whether the DTO is in the user's jurisdiction. Used to determine which user right needs to be considered
	 * to decide whether sensitive and/or personal data is supposed to be shown.
	 */
	@MappingException(reason = MappingException.COMPUTED_FOR_API)
	@Schema(description = "Whether the DTO is in the user's jurisdiction. Used to determine which user right needs to be considered "
		+ "to decide whether sensitive and/or personal data is supposed to be shown.")
	private boolean inJurisdiction;

	public boolean isPseudonymized() {
		return pseudonymized;
	}

	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
	}

	public boolean isInJurisdiction() {
		return inJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		this.inJurisdiction = inJurisdiction;
	}
}
