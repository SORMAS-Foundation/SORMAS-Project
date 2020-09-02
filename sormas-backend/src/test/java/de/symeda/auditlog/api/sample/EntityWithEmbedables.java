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

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class EntityWithEmbedables implements HasUuid {

	private final String uuid;

	private final String someAttribute;
	private final FirstEmbeddable firstEmbeddable;
	private final NotAuditedEmbeddable notAuditedEmbeddable;

	public EntityWithEmbedables(String uuid, String someAttribute, FirstEmbeddable firstEmbeddable, NotAuditedEmbeddable notAuditedEmbeddable) {
		this.uuid = uuid;
		this.someAttribute = someAttribute;
		this.firstEmbeddable = firstEmbeddable;
		this.notAuditedEmbeddable = notAuditedEmbeddable;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	public String getSomeAttribute() {
		return someAttribute;
	}

	@Embedded
	public FirstEmbeddable getFirstEmbeddable() {
		return firstEmbeddable;
	}

	@Embedded
	public NotAuditedEmbeddable getNotAuditedEmbeddable() {
		return notAuditedEmbeddable;
	}

	@Audited
	@Embeddable
	public static class FirstEmbeddable {

		private final int integer;
		private final HasUuid entity;
		private final String notAuditied;

		public FirstEmbeddable(int integer, HasUuid entity, String notAudited) {
			this.integer = integer;
			this.entity = entity;
			notAuditied = notAudited;
		}

		public int getInteger() {
			return integer;
		}

		public HasUuid getEntity() {
			return entity;
		}

		@AuditedIgnore
		public String getNotAuditied() {
			return notAuditied;
		}
	}

	@Embeddable
	public static class NotAuditedEmbeddable {

		private final int integer;

		public NotAuditedEmbeddable(int integer) {
			this.integer = integer;
		}

		public int getInteger() {
			return integer;
		}
	}
}
