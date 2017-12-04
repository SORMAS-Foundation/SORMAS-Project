package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class Entity implements HasUuid {

	public static final String STRING = "string";
	public static final String INTEGER = "integer";
	public static final String FLAG = "flag";

	private final String uuid;

	private int integer;
	private Boolean flag;
	private String string;

	public Entity(String uuid, Boolean flag, String string, int integer) {

		this.uuid = uuid;

		this.setFlag(flag);
		this.setString(string);
		this.setInteger(integer);
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

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public void setString(String string) {
		this.string = string;
	}
}