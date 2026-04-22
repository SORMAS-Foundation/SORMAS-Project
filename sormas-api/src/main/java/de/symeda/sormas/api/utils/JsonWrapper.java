package de.symeda.sormas.api.utils;

import java.io.Serializable;

/**
 * Meant to wrap JSON serialized objects.
 */
public class JsonWrapper<T extends Class<?>> implements Serializable {

	private final String json;
	private final T classType;

	public JsonWrapper(String json, T classType) {
		this.json = json;
		this.classType = classType;
	}

	public String getJson() {
		return json;
	}

	public T getClassType() {
		return classType;
	}
}
