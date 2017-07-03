package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.AuditedEntity;
import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Simple entity that is only used for tests.
 * 
 * @author Oliver Milke
 * @since 14.01.2016
 */
public final class SimpleEntity implements AuditedEntity {

	private final String uuid;
	private final ValueContainer attributes = new DefaultValueContainer();

	public SimpleEntity(String uuid) {

		this.uuid = uuid;
	}

	@Override
	public String getUuid() {

		return this.uuid;
	}

	@Override
	public ValueContainer inspectAttributes() {
		return this.attributes;
	}
}