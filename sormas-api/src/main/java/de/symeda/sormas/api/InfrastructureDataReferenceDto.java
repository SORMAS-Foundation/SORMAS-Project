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

package de.symeda.sormas.api;

public abstract class InfrastructureDataReferenceDto extends ReferenceDto implements Cloneable {

	private static final long serialVersionUID = -3451269378082767059L;
	private Long externalId;

	protected InfrastructureDataReferenceDto() {
	}

	protected InfrastructureDataReferenceDto(String uuid) {
		super(uuid);
	}

	protected InfrastructureDataReferenceDto(String uuid, String caption, Long externalId) {
		super(uuid, caption);
		this.externalId = externalId;
	}

	public Long getExternalId() {
		return externalId;
	}

	@Override
	public InfrastructureDataReferenceDto clone() {
		try {
			return (InfrastructureDataReferenceDto) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
