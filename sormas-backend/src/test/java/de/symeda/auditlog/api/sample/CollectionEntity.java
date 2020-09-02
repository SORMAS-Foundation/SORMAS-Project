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
package de.symeda.auditlog.api.sample;

import java.time.Month;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class CollectionEntity implements HasUuid {

	public static final String MONTH = "month";
	public static final String STRINGS = "strings";
	public static final String SIMPLEENTITIES = "simpleEntities";
	public static final String NULL_COLLECTION = "nullCollection";

	private final String uuid;
	private final List<String> strings = new ArrayList<>();
	private final Set<Month> month = EnumSet.noneOf(Month.class);
	private final List<SimpleBooleanFlagEntity> simpleEntities = new ArrayList<>();
	private final List<String> nullCollection = null;

	public CollectionEntity(String uuid) {
		this.uuid = uuid;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	public List<String> getStrings() {
		return strings;
	}

	public Set<Month> getMonth() {
		return month;
	}

	public List<SimpleBooleanFlagEntity> getSimpleEntities() {
		return simpleEntities;
	}

	public List<String> getNullCollection() {
		return nullCollection;
	}
}
