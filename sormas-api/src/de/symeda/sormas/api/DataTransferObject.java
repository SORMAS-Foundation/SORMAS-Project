package de.symeda.sormas.api;

import java.io.Serializable;

public abstract class DataTransferObject implements Serializable {

	public static final String UUID = "uuid";

	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
