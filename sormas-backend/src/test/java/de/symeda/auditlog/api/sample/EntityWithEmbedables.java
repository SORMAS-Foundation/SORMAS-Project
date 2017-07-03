package de.symeda.auditlog.api.sample;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.auditlog.api.HasUuid;

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
