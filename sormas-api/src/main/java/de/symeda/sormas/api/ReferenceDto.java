package de.symeda.sormas.api;

import java.util.Date;

public abstract class ReferenceDto extends DataTransferObject {

	private static final long serialVersionUID = 4500877980734738141L;

	public static final String CAPTION = "caption";
	
	private String caption;
	
	public ReferenceDto() { }
	
	public ReferenceDto(Date creationDate, Date changeDate, String uuid) {
		super(creationDate, changeDate, uuid);
	}
	
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
