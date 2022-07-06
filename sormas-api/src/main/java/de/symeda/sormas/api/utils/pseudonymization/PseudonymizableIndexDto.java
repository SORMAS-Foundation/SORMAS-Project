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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class PseudonymizableIndexDto implements Pseudonymizable, Serializable {

	private boolean pseudonymized;
	private String uuid;

	public boolean isPseudonymized() {
		return pseudonymized;
	}

	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
	}

	public PseudonymizableIndexDto() {
	}

	public PseudonymizableIndexDto(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCaption() {
		return toString();
	}

	public String i18nPrefix() {
		return null;
	}

	@Override
	public String toString() {
		return (i18nPrefix() != null ? i18nPrefix() : getClass().getSimpleName()) + StringUtils.SPACE + this.getUuid();
	}
}
