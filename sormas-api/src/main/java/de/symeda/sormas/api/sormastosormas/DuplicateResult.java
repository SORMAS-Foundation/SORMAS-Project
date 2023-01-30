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

package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.sormastosormas.entities.DuplicateResultType;

@AuditedClass
public class DuplicateResult implements Serializable {

	private static final long serialVersionUID = 1647535939211815787L;

	private final DuplicateResultType type;

	private final Set<String> uuids;

	public DuplicateResult(DuplicateResultType type, Set<String> uuids) {
		this.type = type;
		this.uuids = uuids;
	}

	public static DuplicateResult none() {
		return new DuplicateResult(DuplicateResultType.NONE, Collections.emptySet());
	}

	public DuplicateResultType getType() {
		return type;
	}

	public Set<String> getUuids() {
		return uuids;
	}
}
