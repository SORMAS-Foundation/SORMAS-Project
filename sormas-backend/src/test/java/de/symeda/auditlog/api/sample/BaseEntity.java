package de.symeda.auditlog.api.sample;

import javax.persistence.Version;

import de.symeda.sormas.api.HasUuid;

public class BaseEntity implements HasUuid {

	public static final String UUID = "uuid";
	public static final String VERSION = "version";

	private final String uuid;

	private int version;

	public BaseEntity(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void increaseVersion() {
		this.version = version++;
	}
}
