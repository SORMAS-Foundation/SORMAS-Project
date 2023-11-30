/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.docgeneration;

import java.util.EnumMap;
import java.util.Map;

import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.uuid.HasUuid;

public class RootEntities {

	private final EnumMap<RootEntityType, Entity> entities;

	public RootEntities() {
		this.entities = new EnumMap<>(RootEntityType.class);
	}

	public RootEntities addReference(RootEntityType rootEntityType, HasUuid entity) {
		entities.put(rootEntityType, new Entity(entity, true));

		return this;
	}

	public RootEntities addEntity(RootEntityType rootEntityType, HasUuid entity) {
		entities.put(rootEntityType, new Entity(entity, false));

		return this;
	}

	public Map<RootEntityType, Entity> getEntities() {
		return entities;
	}

	public boolean hasReference(RootEntityType rootEntityType) {
		return entities.containsKey(rootEntityType);
	}

	public static final class Entity {

		private final HasUuid value;
		private final boolean isReference;

		private Entity(HasUuid entity, boolean isReference) {
			this.value = entity;
			this.isReference = isReference;
		}

		public HasUuid getValue() {
			return value;
		}

		public boolean isReference() {
			return isReference;
		}
	}
}
