/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.infrastructure;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * An extension of the {@link AbstractDomainObject} that defines infrastructure data (e.g. regions, districts).
 * Infrastructure data should not be deleted from the system, but can be archived. Archived infrastructure data
 * still has to be transfered to the mobile application.
 */
@MappedSuperclass
public abstract class InfrastructureAdo extends AbstractDomainObject {

	private static final long serialVersionUID = 6512756286608581221L;

	// todo this should be included, however, we face problems as externalID used used sometimes in the code. See #6549.
	//public static final String EXTERNAL_ID = "externalId";
	public static final String ARCHIVED = "archived";

	private boolean centrallyManaged;
	private boolean archived;

	@Column(name = "centrally_managed")
	public boolean isCentrallyManaged() {
		return centrallyManaged;
	}

	public void setCentrallyManaged(boolean centrallyManaged) {
		this.centrallyManaged = centrallyManaged;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

}
