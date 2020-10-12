package de.symeda.sormas.api;

import org.apache.commons.text.StringEscapeUtils;

public class ResourceBundle {

	private java.util.ResourceBundle resourceBundle;

	public ResourceBundle(java.util.ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public String getString(String key, String defaultValue) {

		String value = defaultValue;
		try {
			Boolean hasKey = resourceBundle.containsKey(key);
			if (hasKey) {
				value = resourceBundle.getString(key);
			}
		} catch (Exception e) {

		}

		return value;
	}

	public String getString(String key) {
		return getString(key, null);
	}
}
