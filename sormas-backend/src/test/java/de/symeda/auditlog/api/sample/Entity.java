package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedEntity;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.auditlog.api.value.format.DefaultValueFormatter;

@Audited
public class Entity implements AuditedEntity {

	public static final String STRING = "string";
	public static final String INTEGER = "integer";
	public static final String FLAG = "flag";

	private final String uuid;

	private final int integer;
	private final Boolean flag;
	private final String string;

	public Entity(String uuid, Boolean flag, String string, int integer) {

		this.uuid = uuid;

		this.flag = flag;
		this.string = string;
		this.integer = integer;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;

	}

	public Boolean getFlag() {
		return flag;
	}

	public String getString() {
		return string;
	}

	public int getInteger() {
		return integer;
	}

	@Override
	public ValueContainer inspectAttributes() {

		DefaultValueContainer container = new DefaultValueContainer();

		container.put(FLAG, flag, new DefaultValueFormatter());
		container.put(STRING, string);
		container.put(INTEGER, integer);

		return container;
	}

}