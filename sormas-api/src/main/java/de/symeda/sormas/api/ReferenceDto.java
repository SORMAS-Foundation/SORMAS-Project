package de.symeda.sormas.api;

import de.symeda.sormas.api.DataTransferObject;

public abstract class ReferenceDto extends DataTransferObject {

	private static final long serialVersionUID = 4500877980734738141L;

	public static final String CAPTION = "caption";
	
	private String caption;
	
	@Override
	public String toString() {
		return getCaption();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
