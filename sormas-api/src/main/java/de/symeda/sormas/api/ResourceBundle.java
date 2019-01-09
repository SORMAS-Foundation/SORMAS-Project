package de.symeda.sormas.api;

public class ResourceBundle {

	private java.util.ResourceBundle resourceBundle;
	
	public ResourceBundle (java.util.ResourceBundle _resourceBundle) {
		resourceBundle = _resourceBundle;
	}

	public String getString (String key, String _default) {
		String value = _default;
		
		try {
			Boolean hasKey = resourceBundle.containsKey(key);
			
			if (hasKey) {
				value = resourceBundle.getString(key);
			}
		}
		catch (Exception e) {
			
		}
		
		return value;
	}
	
	public String getString (String key) {
		return getString(key, null);
	}
}
